@echo off
chcp 65001 >nul
REM Script chạy Block Blast Game với Java 8 và JavaFX

echo ========================================
echo  Block Blast Game
echo ========================================
echo.

REM Tìm javac
SET JAVAC_FOUND=0
where javac >nul 2>&1
IF %ERRORLEVEL% EQU 0 SET JAVAC_FOUND=1

REM Nếu không tìm thấy trong PATH, tìm trong các thư mục thông thường
IF %JAVAC_FOUND% EQU 0 (
    IF DEFINED JAVA_HOME (
        IF EXIST "%JAVA_HOME%\bin\javac.exe" (
            SET "PATH=%JAVA_HOME%\bin;%PATH%"
            SET JAVAC_FOUND=1
        )
    )
)

IF %JAVAC_FOUND% EQU 0 (
    FOR /D %%I IN ("C:\Program Files\Java\*") DO (
        IF EXIST "%%I\bin\javac.exe" (
            SET "PATH=%%I\bin;%PATH%"
            SET JAVAC_FOUND=1
            GOTO :javac_found
        )
    )
    FOR /D %%I IN ("C:\Program Files (x86)\Java\*") DO (
        IF EXIST "%%I\bin\javac.exe" (
            SET "PATH=%%I\bin;%PATH%"
            SET JAVAC_FOUND=1
            GOTO :javac_found
        )
    )
)

:javac_found
IF %JAVAC_FOUND% EQU 0 (
    echo [ERROR] Khong tim thay javac!
    echo Vui long cai dat JDK 8 truoc.
    pause
    exit /b 1
)

REM Tìm JavaFX (jfxrt.jar) - Java 8 có JavaFX đi kèm
SET JAVAFX_PATH=

REM Tìm từ JAVA_HOME
IF DEFINED JAVA_HOME (
    IF EXIST "%JAVA_HOME%\jre\lib\ext\jfxrt.jar" (
        SET "JAVAFX_PATH=%JAVA_HOME%\jre\lib\ext\jfxrt.jar"
        GOTO :found_javafx
    )
    IF EXIST "%JAVA_HOME%\lib\jfxrt.jar" (
        SET "JAVAFX_PATH=%JAVA_HOME%\lib\jfxrt.jar"
        GOTO :found_javafx
    )
)

REM Tìm trong Program Files\Java
FOR /D %%I IN ("C:\Program Files\Java\*") DO (
    IF EXIST "%%I\jre\lib\ext\jfxrt.jar" (
        SET "JAVAFX_PATH=%%I\jre\lib\ext\jfxrt.jar"
        GOTO :found_javafx
    )
    IF EXIST "%%I\lib\ext\jfxrt.jar" (
        SET "JAVAFX_PATH=%%I\lib\ext\jfxrt.jar"
        GOTO :found_javafx
    )
    IF EXIST "%%I\lib\jfxrt.jar" (
        SET "JAVAFX_PATH=%%I\lib\jfxrt.jar"
        GOTO :found_javafx
    )
)

REM Tìm từ đường dẫn javac
FOR /F "tokens=*" %%I IN ('where javac') DO (
    SET "JAVAC_PATH=%%I"
    FOR %%J IN ("%%~dI%%~pI..") DO (
        IF EXIST "%%J\jre\lib\ext\jfxrt.jar" (
            SET "JAVAFX_PATH=%%J\jre\lib\ext\jfxrt.jar"
            GOTO :found_javafx
        )
        IF EXIST "%%J\lib\jfxrt.jar" (
            SET "JAVAFX_PATH=%%J\lib\jfxrt.jar"
            GOTO :found_javafx
        )
    )
)

:found_javafx
IF "%JAVAFX_PATH%"=="" (
    echo [WARNING] Khong tim thay jfxrt.jar
    echo Dang thu bien dich khong can JavaFX...
    SET "JAVAFX_CLASSPATH="
) ELSE (
    echo [OK] Tim thay JavaFX tai: %JAVAFX_PATH%
)

echo.
echo ========================================
echo  Dang bien dich game...
echo ========================================

IF "%JAVAFX_PATH%"=="" (
    javac -d . -encoding UTF-8 src/main/java/game/*.java src/main/java/game/rules/*.java src/main/java/ui/*.java src/main/java/utils/*.java
) ELSE (
    javac -d . -encoding UTF-8 -cp "%JAVAFX_PATH%" src/main/java/game/*.java src/main/java/game/rules/*.java src/main/java/ui/*.java src/main/java/utils/*.java
)

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Bien dich that bai!
    echo.
    echo Neu gap loi "package javafx does not exist", ban can:
    echo 1. Tai JavaFX SDK: https://openjfx.io/
    echo 2. Hoac su dung JDK 8 co JavaFX di kem
    pause
    exit /b 1
)

echo [OK] Bien dich thanh cong!
echo.
echo ========================================
echo  Dang chay game...
echo ========================================

IF "%JAVAFX_PATH%"=="" (
    java game.BlockBlastGame
) ELSE (
    java -cp .;"%JAVAFX_PATH%" game.BlockBlastGame
)

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Chay game that bai!
    pause
    exit /b 1
)

pause

