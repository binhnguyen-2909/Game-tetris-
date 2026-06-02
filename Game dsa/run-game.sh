#!/bin/bash
# Script chạy Block Blast Game trên macOS/Linux với Java 8 và JavaFX

echo "========================================"
echo " Block Blast Game"
echo "========================================"
echo ""

# Tìm javac
JAVAC_FOUND=0
if command -v javac &> /dev/null; then
    JAVAC_FOUND=1
    echo "[OK] Tìm thấy javac trong PATH"
fi

# Nếu không tìm thấy trong PATH, tìm trong JAVA_HOME
if [ $JAVAC_FOUND -eq 0 ]; then
    if [ -n "$JAVA_HOME" ]; then
        if [ -f "$JAVA_HOME/bin/javac" ]; then
            export PATH="$JAVA_HOME/bin:$PATH"
            JAVAC_FOUND=1
            echo "[OK] Tìm thấy javac trong JAVA_HOME: $JAVA_HOME"
        fi
    fi
fi

# Tìm trong các thư mục thông thường trên macOS
if [ $JAVAC_FOUND -eq 0 ]; then
    # macOS thường cài Java ở /Library/Java/JavaVirtualMachines
    for java_dir in /Library/Java/JavaVirtualMachines/*/Contents/Home; do
        if [ -f "$java_dir/bin/javac" ]; then
            export PATH="$java_dir/bin:$PATH"
            export JAVA_HOME="$java_dir"
            JAVAC_FOUND=1
            echo "[OK] Tìm thấy javac tại: $java_dir"
            break
        fi
    done
fi

# Linux thường cài ở /usr/lib/jvm
if [ $JAVAC_FOUND -eq 0 ]; then
    for java_dir in /usr/lib/jvm/*; do
        if [ -f "$java_dir/bin/javac" ]; then
            export PATH="$java_dir/bin:$PATH"
            export JAVA_HOME="$java_dir"
            JAVAC_FOUND=1
            echo "[OK] Tìm thấy javac tại: $java_dir"
            break
        fi
    done
fi

if [ $JAVAC_FOUND -eq 0 ]; then
    echo "[ERROR] Không tìm thấy javac!"
    echo "Vui lòng cài đặt JDK 8 trước."
    echo ""
    echo "Trên macOS:"
    echo "  brew install openjdk@8"
    echo ""
    echo "Trên Linux (Ubuntu/Debian):"
    echo "  sudo apt-get install openjdk-8-jdk"
    echo ""
    exit 1
fi

# Tìm JavaFX (jfxrt.jar) - Java 8 có JavaFX đi kèm
JAVAFX_PATH=""

# Tìm từ JAVA_HOME
if [ -n "$JAVA_HOME" ]; then
    if [ -f "$JAVA_HOME/jre/lib/ext/jfxrt.jar" ]; then
        JAVAFX_PATH="$JAVA_HOME/jre/lib/ext/jfxrt.jar"
    elif [ -f "$JAVA_HOME/lib/jfxrt.jar" ]; then
        JAVAFX_PATH="$JAVA_HOME/lib/jfxrt.jar"
    fi
fi

# Tìm trong các thư mục Java trên macOS
if [ -z "$JAVAFX_PATH" ]; then
    for java_dir in /Library/Java/JavaVirtualMachines/*/Contents/Home; do
        if [ -f "$java_dir/jre/lib/ext/jfxrt.jar" ]; then
            JAVAFX_PATH="$java_dir/jre/lib/ext/jfxrt.jar"
            break
        elif [ -f "$java_dir/lib/jfxrt.jar" ]; then
            JAVAFX_PATH="$java_dir/lib/jfxrt.jar"
            break
        fi
    done
fi

# Linux
if [ -z "$JAVAFX_PATH" ]; then
    for java_dir in /usr/lib/jvm/*; do
        if [ -f "$java_dir/jre/lib/ext/jfxrt.jar" ]; then
            JAVAFX_PATH="$java_dir/jre/lib/ext/jfxrt.jar"
            break
        elif [ -f "$java_dir/lib/jfxrt.jar" ]; then
            JAVAFX_PATH="$java_dir/lib/jfxrt.jar"
            break
        fi
    done
fi

if [ -z "$JAVAFX_PATH" ]; then
    echo "[WARNING] Không tìm thấy jfxrt.jar"
    echo "Đang thử biên dịch không cần JavaFX..."
    JAVAFX_CLASSPATH=""
else
    echo "[OK] Tìm thấy JavaFX tại: $JAVAFX_PATH"
fi

echo ""
echo "========================================"
echo " Đang biên dịch game..."
echo "========================================"

if [ -z "$JAVAFX_PATH" ]; then
    javac -d . -encoding UTF-8 src/main/java/game/*.java src/main/java/ui/*.java src/main/java/utils/*.java
else
    javac -d . -encoding UTF-8 -cp "$JAVAFX_PATH" src/main/java/game/*.java src/main/java/ui/*.java src/main/java/utils/*.java
fi

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Biên dịch thất bại!"
    echo ""
    echo "Nếu gặp lỗi \"package javafx does not exist\", bạn có thể:"
    echo "1. Tải JavaFX SDK: https://openjfx.io/"
    echo "2. Hoặc sử dụng JDK 8 có JavaFX đi kèm"
    echo ""
    exit 1
fi

echo "[OK] Biên dịch thành công!"
echo ""
echo "========================================"
echo " Đang chạy game..."
echo "========================================"

if [ -z "$JAVAFX_PATH" ]; then
    java game.BlockBlastGame
else
    java -cp .:"$JAVAFX_PATH" game.BlockBlastGame
fi

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Chạy game thất bại!"
    exit 1
fi

