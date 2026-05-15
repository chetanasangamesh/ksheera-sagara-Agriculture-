#!/bin/bash
set -e

ANDROID_SDK_ROOT="$HOME/android-sdk"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
CMDLINE_ZIP="$HOME/cmdline-tools.zip"

echo "================================================"
echo "  KSHEERA SAGARA — Android APK Builder"
echo "================================================"
echo ""

# Step 1: Download Android Command Line Tools if needed
if [ ! -d "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin" ]; then
    echo "[1/5] Downloading Android SDK Command Line Tools..."
    mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"
    wget -q --show-progress -O "$CMDLINE_ZIP" "$CMDLINE_TOOLS_URL"
    echo "[1/5] Extracting..."
    unzip -q "$CMDLINE_ZIP" -d "$ANDROID_SDK_ROOT/cmdline-tools"
    mv "$ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    rm -f "$CMDLINE_ZIP"
    echo "[1/5] Done."
else
    echo "[1/5] Android Command Line Tools already installed. Skipping."
fi

export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/34.0.0:$PATH"

# Step 2: Accept licenses
echo "[2/5] Accepting Android SDK licenses..."
yes | sdkmanager --licenses > /dev/null 2>&1 || true

# Step 3: Install required SDK packages
if [ ! -d "$ANDROID_SDK_ROOT/platforms/android-36" ]; then
    echo "[3/5] Installing Android SDK packages (this may take a few minutes)..."
    sdkmanager "platform-tools" "build-tools;34.0.0" "platforms;android-36"
    echo "[3/5] Done."
else
    echo "[3/5] Android SDK packages already installed. Skipping."
fi

# Step 4: Make gradlew executable
echo "[4/5] Preparing Gradle build..."
chmod +x ./gradlew

# Step 5: Build the APK
echo "[5/5] Building APK (this takes 2-5 minutes on first run)..."
export JAVA_HOME="${JAVA_HOME:-$(dirname $(dirname $(readlink -f $(which java))))}"
./gradlew assembleDebug --no-daemon --stacktrace 2>&1

# Check if APK was built
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    SIZE=$(du -sh "$APK_PATH" | cut -f1)
    echo ""
    echo "================================================"
    echo "  ✅ APK BUILT SUCCESSFULLY!"
    echo "  Size: $SIZE"
    echo "  Location: $APK_PATH"
    echo ""
    echo "  HOW TO INSTALL:"
    echo "  1. Open the Files panel in Replit (left sidebar)"
    echo "  2. Navigate to app/build/outputs/apk/debug/"
    echo "  3. Right-click app-debug.apk → Download"
    echo "  4. Transfer to your Android phone"
    echo "  5. Enable 'Install from unknown sources' in phone settings"
    echo "  6. Open the APK file to install"
    echo "================================================"
else
    echo ""
    echo "❌ Build failed. Check the error messages above."
fi
