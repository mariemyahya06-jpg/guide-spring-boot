@REM ----------------------------------------------------------------------------
@REM Maven Wrapper (only-script) pour Windows
@REM Telecharge Maven automatiquement puis lance la commande demandee.
@REM Usage : .\mvnw.cmd spring-boot:run
@REM ----------------------------------------------------------------------------
@echo off
setlocal enabledelayedexpansion

set "WRAPPER_PROPERTIES=%~dp0.mvn\wrapper\maven-wrapper.properties"
if not exist "%WRAPPER_PROPERTIES%" (
  echo [ERREUR] Fichier introuvable : %WRAPPER_PROPERTIES%
  exit /b 1
)

set "DISTRIBUTION_URL="
for /F "usebackq eol=# tokens=1,* delims==" %%A in ("%WRAPPER_PROPERTIES%") do (
  if "%%A"=="distributionUrl" set "DISTRIBUTION_URL=%%B"
)

if "%DISTRIBUTION_URL%"=="" (
  echo [ERREUR] distributionUrl manquant dans maven-wrapper.properties
  exit /b 1
)

@REM Nom du fichier zip et du dossier Maven
for %%F in ("%DISTRIBUTION_URL%") do set "DISTRIBUTION_ZIP=%%~nxF"
set "MAVEN_BASENAME=%DISTRIBUTION_ZIP:-bin.zip=%"

set "WRAPPER_DIR=%USERPROFILE%\.m2\wrapper\dists\%MAVEN_BASENAME%"
set "MVN_CMD=%WRAPPER_DIR%\%MAVEN_BASENAME%\bin\mvn.cmd"

if not exist "%MVN_CMD%" (
  echo Telechargement de Maven : %DISTRIBUTION_URL%
  if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -UseBasicParsing -Uri '%DISTRIBUTION_URL%' -OutFile '%WRAPPER_DIR%\maven.zip'"
  if errorlevel 1 (
    echo [ERREUR] Echec du telechargement de Maven.
    exit /b 1
  )
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "Expand-Archive -Path '%WRAPPER_DIR%\maven.zip' -DestinationPath '%WRAPPER_DIR%' -Force"
  if errorlevel 1 (
    echo [ERREUR] Echec de l'extraction de Maven.
    exit /b 1
  )
)

call "%MVN_CMD%" %*
