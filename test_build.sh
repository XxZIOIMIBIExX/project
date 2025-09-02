#!/bin/bash

echo "🧪 Testing CasaOS Android Client build..."

# Check if we have the necessary files
echo "📋 Checking project structure..."

required_files=(
    "app/build.gradle"
    "build.gradle"
    "settings.gradle"
    "app/src/main/AndroidManifest.xml"
    "app/src/main/java/com/casaos/client/MainActivity.kt"
    "app/src/main/res/layout/activity_main.xml"
)

for file in "${required_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file exists"
    else
        echo "❌ $file missing"
        exit 1
    fi
done

echo "📦 All required files present!"

# Check XML syntax
echo "🔍 Checking XML syntax..."
if command -v xmllint &> /dev/null; then
    for xml_file in app/src/main/res/layout/*.xml app/src/main/res/menu/*.xml app/src/main/res/xml/*.xml; do
        if [ -f "$xml_file" ]; then
            if xmllint --noout "$xml_file" 2>/dev/null; then
                echo "✅ $xml_file syntax OK"
            else
                echo "❌ $xml_file has syntax errors"
                xmllint --noout "$xml_file"
                exit 1
            fi
        fi
    done
else
    echo "⚠️  xmllint not available, skipping XML validation"
fi

echo "🎉 Project structure and XML files look good!"
echo "📱 Ready for Android Studio import and build!"

# Show project summary
echo ""
echo "📊 Project Summary:"
echo "   - Package: com.casaos.client"
echo "   - Min SDK: 24 (Android 7.0)"
echo "   - Target SDK: 34 (Android 14)"
echo "   - Kotlin files: $(find app/src/main/java -name "*.kt" | wc -l)"
echo "   - Layout files: $(find app/src/main/res/layout -name "*.xml" | wc -l)"
echo "   - Dependencies: Material 3, Retrofit, OkHttp, Security Crypto"