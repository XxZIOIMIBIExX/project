#!/bin/bash

echo "🧹 Performing clean build of CasaOS Android Client..."

# Remove all build directories and cache
echo "🗑️  Removing build directories..."
rm -rf app/build/
rm -rf build/
rm -rf .gradle/

# Remove any intermediate files
echo "🗑️  Removing intermediate files..."
find . -name "*.tmp" -delete
find . -name "*.cache" -delete

# Clean Gradle cache
echo "🧹 Cleaning Gradle cache..."
./gradlew clean

# Verify XML files are correct
echo "🔍 Verifying XML namespace corrections..."
if grep -r "android-auto" app/src/main/res/ 2>/dev/null; then
    echo "❌ Found remaining android-auto references!"
    grep -rn "android-auto" app/src/main/res/
    exit 1
else
    echo "✅ No android-auto references found"
fi

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Clean build successful!"
    echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"
    
    # Show APK info
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        APK_SIZE=$(du -h app/build/outputs/apk/debug/app-debug.apk | cut -f1)
        echo "📊 APK size: $APK_SIZE"
    fi
else
    echo "❌ Clean build failed!"
    echo "💡 Try opening the project in Android Studio and syncing Gradle files"
    exit 1
fi

echo "🎉 Clean build process completed!"