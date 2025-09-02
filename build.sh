#!/bin/bash

# CasaOS Android Client Build Script

echo "🏗️  Building CasaOS Android Client..."

# Check if Android SDK is available
if ! command -v adb &> /dev/null; then
    echo "❌ Android SDK not found. Please install Android Studio and SDK."
    exit 1
fi

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Debug build successful!"
    echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Debug build failed!"
    exit 1
fi

# Optional: Install on connected device
read -p "📲 Install on connected device? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "📱 Installing on device..."
    adb install app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo "✅ Installation successful!"
        echo "🚀 You can now launch the CasaOS Client app on your device."
    else
        echo "❌ Installation failed. Make sure USB debugging is enabled."
    fi
fi

echo "🎉 Build process completed!"