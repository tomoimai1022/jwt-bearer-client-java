@echo off
set DIR=%~dp0
set GRADLE_VERSION=8.7
set WRAPPER_JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
set WRAPPER_PROPERTIES=%DIR%gradle\wrapper\gradle-wrapper.properties

if not exist "%WRAPPER_JAR%" (
  echo Downloading Gradle Wrapper JAR...
  mkdir "%DIR%gradle\wrapper" 2>nul
  powershell -Command "Invoke-WebRequest -OutFile '%WRAPPER_JAR%' 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip'"
  REM Extraction logic omitted for brevity. Please run 'gradle wrapper' after installing Gradle for full support.
)

java -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
