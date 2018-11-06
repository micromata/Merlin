@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  merlin-desktop startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and MERLIN_DESKTOP_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\merlin-desktop-0.9-SNAPSHOT.jar;%APP_HOME%\lib\merlin-server-0.9-SNAPSHOT.jar;%APP_HOME%\lib\merlin-core-0.9-SNAPSHOT.jar;%APP_HOME%\lib\commons-lang3-3.8.1.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\slf4j-log4j12-1.7.25.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\javafx-fxml-11.0.1-ea+1-mac.jar;%APP_HOME%\lib\javafx-controls-11.0.1-ea+1-mac.jar;%APP_HOME%\lib\javafx-controls-11.0.1-ea+1.jar;%APP_HOME%\lib\javafx-graphics-11.0.1-ea+1-mac.jar;%APP_HOME%\lib\javafx-graphics-11.0.1-ea+1.jar;%APP_HOME%\lib\javafx-base-11.0.1-ea+1-mac.jar;%APP_HOME%\lib\javafx-base-11.0.1-ea+1.jar;%APP_HOME%\lib\jetty-servlet-9.4.12.v20180830.jar;%APP_HOME%\lib\jetty-security-9.4.12.v20180830.jar;%APP_HOME%\lib\jetty-server-9.4.12.v20180830.jar;%APP_HOME%\lib\jetty-servlets-9.4.12.v20180830.jar;%APP_HOME%\lib\jaxb-core-2.3.0.1.jar;%APP_HOME%\lib\jaxb-runtime-2.3.1.jar;%APP_HOME%\lib\jersey-container-servlet-2.27.jar;%APP_HOME%\lib\jersey-media-multipart-2.27.jar;%APP_HOME%\lib\jersey-media-json-jackson-2.27.jar;%APP_HOME%\lib\jersey-hk2-2.27.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.8.10.jar;%APP_HOME%\lib\jackson-databind-2.9.6.jar;%APP_HOME%\lib\jackson-annotations-2.9.6.jar;%APP_HOME%\lib\jaxws-api-2.3.1.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\poi-ooxml-4.0.0.jar;%APP_HOME%\lib\poi-4.0.0.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\jetty-http-9.4.12.v20180830.jar;%APP_HOME%\lib\jetty-io-9.4.12.v20180830.jar;%APP_HOME%\lib\jetty-continuation-9.4.12.v20180830.jar;%APP_HOME%\lib\jetty-util-9.4.12.v20180830.jar;%APP_HOME%\lib\txw2-2.3.1.jar;%APP_HOME%\lib\istack-commons-runtime-3.0.7.jar;%APP_HOME%\lib\stax-ex-1.8.jar;%APP_HOME%\lib\FastInfoset-1.2.15.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\jersey-container-servlet-core-2.27.jar;%APP_HOME%\lib\jersey-server-2.27.jar;%APP_HOME%\lib\jersey-client-2.27.jar;%APP_HOME%\lib\jersey-media-jaxb-2.27.jar;%APP_HOME%\lib\jersey-common-2.27.jar;%APP_HOME%\lib\jersey-entity-filtering-2.27.jar;%APP_HOME%\lib\javax.ws.rs-api-2.1.jar;%APP_HOME%\lib\mimepull-1.9.6.jar;%APP_HOME%\lib\hk2-locator-2.5.0-b42.jar;%APP_HOME%\lib\jackson-core-2.9.6.jar;%APP_HOME%\lib\javax.xml.soap-api-1.4.0.jar;%APP_HOME%\lib\hk2-api-2.5.0-b42.jar;%APP_HOME%\lib\hk2-utils-2.5.0-b42.jar;%APP_HOME%\lib\javax.annotation-api-1.3.2.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\commons-codec-1.10.jar;%APP_HOME%\lib\commons-collections4-4.2.jar;%APP_HOME%\lib\poi-ooxml-schemas-4.0.0.jar;%APP_HOME%\lib\commons-compress-1.18.jar;%APP_HOME%\lib\curvesapi-1.04.jar;%APP_HOME%\lib\javax.inject-2.5.0-b42.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.1.jar;%APP_HOME%\lib\validation-api-1.1.0.Final.jar;%APP_HOME%\lib\aopalliance-repackaged-2.5.0-b42.jar;%APP_HOME%\lib\javassist-3.22.0-CR2.jar;%APP_HOME%\lib\xmlbeans-3.0.1.jar;%APP_HOME%\lib\javax.inject-1.jar

@rem Execute merlin-desktop
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MERLIN_DESKTOP_OPTS%  -classpath "%CLASSPATH%" de.micromata.merlin.app.javafx.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable MERLIN_DESKTOP_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%MERLIN_DESKTOP_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
