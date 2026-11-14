@echo off
setlocal EnableExtensions EnableDelayedExpansion
set GRADLE_VERSION=8.11.1
if not defined GRADLE_USER_HOME set GRADLE_USER_HOME=%USERPROFILE%\.gradle
set INSTALL_DIR=!GRADLE_USER_HOME!\ntplaytree-gradle\gradle-!GRADLE_VERSION!
if not exist "!INSTALL_DIR!\bin\gradle.bat" (
  if not exist "!GRADLE_USER_HOME!\ntplaytree-gradle" mkdir "!GRADLE_USER_HOME!\ntplaytree-gradle"
  set ZIP=!GRADLE_USER_HOME!\ntplaytree-gradle\gradle-!GRADLE_VERSION!-bin.zip
  if not exist "!ZIP!" powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-!GRADLE_VERSION!-bin.zip' -OutFile '!ZIP!'"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '!ZIP!' '!GRADLE_USER_HOME!\ntplaytree-gradle'"
)
call "!INSTALL_DIR!\bin\gradle.bat" %*
endlocal
