@echo off
setlocal enabledelayedexpansion

echo ========================================
echo  Block Blast Game
echo ========================================
echo.

REM -------- 1. Find javac --------
set "JAVAC_FOUND=0"
where javac >nul 2>&1
if %ERRORLEVEL% EQU 0 set "JAVAC_FOUND=1"

if !JAVAC_FOUND! EQU 0 (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javac.exe" (
            set "PATH=%JAVA_HOME%\bin;%PATH%"
            set "JAVAC_FOUND=1"
        )
    )
)

if !JAVAC_FOUND! EQU 0 (
    for /d %%I in ("C:\Program Files\Eclipse Adoptium\*") do (
        if exist "%%I\bin\javac.exe" (
            set "PATH=%%I\bin;%PATH%"
            set "JAVAC_FOUND=1"
            goto :javac_found
        )
    )
    for /d %%I in ("C:\Program Files\Java\*") do (
        if exist "%%I\bin\javac.exe" (
            set "PATH=%%I\bin;%PATH%"
            set "JAVAC_FOUND=1"
            goto :javac_found
        )
    )
)

:javac_found
if !JAVAC_FOUND! EQU 0 (
    echo [ERROR] javac not found. Install JDK 11+.
    pause
    exit /b 1
)

REM -------- 2. Find JavaFX SDK --------
set "JAVAFX_LIB="

if defined JAVAFX_HOME (
    if exist "%JAVAFX_HOME%\lib\javafx.controls.jar" set "JAVAFX_LIB=%JAVAFX_HOME%\lib"
)

if "!JAVAFX_LIB!"=="" (
    for /d %%I in ("C:\javafx-sdk*") do (
        if exist "%%I\lib\javafx.controls.jar" (
            set "JAVAFX_LIB=%%I\lib"
            goto :found_javafx
        )
    )
)
if "!JAVAFX_LIB!"=="" (
    for /d %%I in ("C:\Program Files\javafx-sdk*") do (
        if exist "%%I\lib\javafx.controls.jar" (
            set "JAVAFX_LIB=%%I\lib"
            goto :found_javafx
        )
    )
)
if "!JAVAFX_LIB!"=="" (
    for /d %%I in ("%USERPROFILE%\Downloads\javafx-sdk*") do (
        if exist "%%I\lib\javafx.controls.jar" (
            set "JAVAFX_LIB=%%I\lib"
            goto :found_javafx
        )
    )
)

:found_javafx
if "!JAVAFX_LIB!"=="" (
    echo [ERROR] JavaFX SDK not found.
    echo Download from https://gluonhq.com/products/javafx/
    echo and extract to C:\javafx-sdk-21 or set JAVAFX_HOME.
    pause
    exit /b 1
)

echo [OK] javac :
where javac
echo [OK] JavaFX: !JAVAFX_LIB!
echo.

REM -------- 3. Compile --------
echo ========================================
echo  Compiling...
echo ========================================

set "SOURCES=src\main\java\game\*.java src\main\java\game\rules\*.java src\main\java\ui\*.java src\main\java\utils\*.java"

javac --module-path "!JAVAFX_LIB!" --add-modules javafx.controls,javafx.fxml -d . -encoding UTF-8 %SOURCES%
if !ERRORLEVEL! NEQ 0 (
    echo.
    echo [ERROR] Compilation failed.
    pause
    exit /b 1
)
echo [OK] Compiled successfully.
echo.

REM -------- 4. Run --------
echo ========================================
echo  Running game...
echo ========================================

java --module-path "!JAVAFX_LIB!" --add-modules javafx.controls,javafx.fxml -cp . game.BlockBlastGame
if !ERRORLEVEL! NEQ 0 (
    echo.
    echo [ERROR] Game failed to run.
    pause
    exit /b 1
)

pause
endlocal
