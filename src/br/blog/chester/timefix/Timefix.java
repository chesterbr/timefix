package br.blog.chester.timefix;

/*
 * Copyright © 2006 Carlos Duarte do Nascimento (Chester)
 * cd@pobox.com
 * 
 * Este programa é um software livre; você pode redistribui-lo e/ou 
 * modifica-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 2 da 
 * Licença, ou (na sua opnião) qualquer versão.
 *
 * Este programa é distribuido na esperança que possa ser util, 
 * mas SEM NENHUMA GARANTIA; sem uma garantia implicita de ADEQUAÇÂO
 * a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença
 * Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU
 * junto com este programa, se não, escreva para a Fundação do Software
 * Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TimeZone;

import bmsi.util.ZoneInfo;

/**
 * Allows Java environments to adjust their default TimeZone settings to
 * accomodate changes such as Daylight Savivings Time updates.
 * <p>
 * This ajust can be made on the aplication itself (by calling the ??? method),
 * or by inserting the class on the application startup script (it will adjust
 * the timezone and call the "real" startup class).
 * <p>
 * The latter method is very useful for deployers that don't have access to the
 * application source code.
 * <p>
 * The hard work of reading the timezone info from the default UNIX
 * location/format is done by Stuart D. Gathman's Java implementation of the
 * "tz" package. It is distributed along with this code for convenience only,
 * but it's official home is at http://www.bmsi.com/java/#TZ .
 * 
 * @author chester
 * @see bmsi.util.ZoneInfo
 */

public class Timefix {

	/**
	 * Corrected timezone (obtained from /etc/localtime by ZoneInfo)
	 */
	private static TimeZone timeZone = null;

	/**
	 * Retrieves unique instance of the corrected timezone
	 * 
	 * @return new (or pre-existing) instance
	 * @throws IOException
	 *             if ZoneInfo fails to retrieve timezone info
	 */
	private static synchronized TimeZone getTimeZone() throws IOException {
		if (timeZone == null) {
			timeZone = new ZoneInfo();
		}
		return timeZone;
	}

	/**
	 * Adjusts the JVM's default timezone to our custom timezone.
	 * 
	 * @throws IOException
	 */
	public static void fixTimeZone() throws IOException {
		TimeZone.setDefault(getTimeZone());
	}

	/**
	 * Adjusts the timezone and calls the <code>main</code> method from a
	 * class.
	 * <p>
	 * The first received parameter is the name of the class. In practice, this
	 * method (combined with the timefix class on the default package) allows an
	 * administrator to replace:
	 * <p>
	 * <code>java MyMainClass param1 param2 param3 </code>
	 * <p>
	 * with
	 * <p>
	 * <code>java timefix MyMainClass param1 param2 param3</code>
	 * <p>
	 * to solve timezone problems (such as DST changes) without access to the
	 * source code
	 * 
	 * @param args
	 *            name of the original main class, followed by its arguments
	 */
	public static void main(String args[]) throws Throwable {

		// Adjust the timezone
		fixTimeZone();

		// Split the name of the original main class (first element of args[])
		// and its arguments (remainder of args[])
		String mainClassName = args[0];
		Object[] mainClassArgs = { removeElemento(args, 0) };

		// Call the main() method on the original main class, using the original
		// arguments - it will never know what happened :-)
		Class[] argTypes = { String[].class };
		Method metodoMain = Class.forName(mainClassName).getDeclaredMethod(
				"main", argTypes);
		try {
			metodoMain.invoke(null, mainClassArgs);
		} catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}

	}

	/**
	 * Removes an element from an string array.
	 * 
	 * @param a
	 *            Array from which we want to remove an element
	 * @param pos
	 *            Position of the removed element
	 * @return new array (without the referenced element).
	 */
	private static String[] removeElemento(String[] a, int pos) {
		if (pos < 0 || pos >= a.length)
			return a;
		String[] aa = new String[a.length - 1];
		if (pos > 0)
			System.arraycopy(a, 0, aa, 0, pos);
		if (pos < a.length - 1)
			System.arraycopy(a, pos + 1, aa, pos, aa.length - pos);
		return aa;
	}

}
