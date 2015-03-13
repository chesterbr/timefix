## Introduction ##

This library allows a developer or a sysadmin to force Java applications (including those hosted on Java servers such as Tomcat) to use the host operating system's timezone info (stored at `/etc/localtime`, using the [tz database format](http://www.twinsun.com/tz/tz-link.htm)).

It was created because Sun's JVM has its own timezone info (which gets quickly outdated in countries where Daylight Saving Time changes every year, such as Brazil). Sysadmins (and some Linux distros) are used to update this information on the underlying OS, but doing the same on the JVM can be complicated and/or unfeasible.

## Usage ##

To use the library, application developers can just call the Timefix.fizTimeZone() method on the application startup (but it makes your app OS-dependable). A better solution for OS-neutrality (and for deployers/sysadmins that have no access to the application source code) is to force the timezone change by 'injecting' the class on the application's startup script.

For example, if your startup script contains:

> `java com.xyz.MyAppStartupClass param1 param2 param3`

you can just add the lib to the classpath (e.g.: `export CLASSPATH=$CLASSPATH:timefix-1.0.jar`) and insert 'timefix' before the main class name:

> `java `**`timefix`**` com.xyz.MyAppStartupClass param1 param2 param3`

and the app will start with the correct timezone info. Whenever you change `/etc/localtime`, just restart the applicaiton and it will use the new info. It also works with application servers (such as Tomcat, in which you change `catalina.sh`).

## Included Library ##

The 'magic' of reading and interpreting timezone information from `/etc/localtime` is cast by Stuart D. Gathman's excellent Java implementation of the tz routines, which you can download at http://www.bmsi.com/java/#TZ. It is included in this project in compliance with LGPL, and a copy of his `ZoneInfo.java` is included (unchanged) on the repository just as a convenience for builders - you should check his page for other uses (and to see lots of interesting code too).