# Block Blast Game

Game Tetris-style được viết bằng Java và JavaFX.

## Yêu cầu:
- **Java JDK 8** (khuyến nghị - có JavaFX đi kèm sẵn)
- Hoặc **Java 11+** với JavaFX SDK

## Cách chạy:

### Windows:
Chạy file `run-game.bat` - script sẽ tự động tìm Java và JavaFX

### macOS/Linux:
```bash
# Cấp quyền thực thi
chmod +x run-game.sh

# Chạy game
./run-game.sh
```

Script sẽ tự động tìm Java và JavaFX trên hệ thống.

### Hoặc chạy thủ công:

#### Với Java 8:

**Windows:**
```bash
javac -d . -encoding UTF-8 -cp "%JAVA_HOME%\jre\lib\ext\jfxrt.jar" src/main/java/game/*.java src/main/java/ui/*.java src/main/java/utils/*.java
java -cp .;"%JAVA_HOME%\jre\lib\ext\jfxrt.jar" game.BlockBlastGame
```

**macOS/Linux:**
```bash
javac -d . -encoding UTF-8 -cp "$JAVA_HOME/jre/lib/ext/jfxrt.jar" src/main/java/game/*.java src/main/java/ui/*.java src/main/java/utils/*.java
java -cp .:"$JAVA_HOME/jre/lib/ext/jfxrt.jar" game.BlockBlastGame
```

#### Với Java 11+ (cần JavaFX SDK):

**Windows:**
```bash
javac --module-path "C:\javafx-sdk\lib" --add-modules javafx.controls -d . -encoding UTF-8 src/main/java/game/*.java src/main/java/ui/*.java src/main/java/utils/*.java
java --module-path "C:\javafx-sdk\lib" --add-modules javafx.controls game.BlockBlastGame
```

**macOS/Linux:**
```bash
javac --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls -d . -encoding UTF-8 src/main/java/game/*.java src/main/java/ui/*.java src/main/java/utils/*.java
java --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls game.BlockBlastGame
```

## Điều khiển:
- **Mũi tên trái/phải**: Di chuyển piece
- **Mũi tên xuống**: Rơi nhanh
- **Space**: Xoay piece
- **P hoặc ESC**: Pause game

## Cài đặt Java trên macOS/Linux:

### macOS:
```bash
# Sử dụng Homebrew
brew install openjdk@8

# Hoặc tải từ Oracle/AdoptOpenJDK
```

### Linux (Ubuntu/Debian):
```bash
sudo apt-get update
sudo apt-get install openjdk-8-jdk
```

### Linux (Fedora/RHEL):
```bash
sudo dnf install java-1.8.0-openjdk-devel
```

## Lưu ý:
- File leaderboard: `src/resources/leaderboard.txt`
- Nếu gặp lỗi "javac not found", cần cài JDK (không phải JRE)
- **iOS**: JavaFX không chạy trực tiếp trên iOS. Xem `HUONG-DAN-IOS.md` để biết cách port sang iOS

