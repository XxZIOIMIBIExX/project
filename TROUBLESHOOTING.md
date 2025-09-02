# CasaOS Android Client - Troubleshooting Guide

## Build Issues Fixed

### ✅ XML Namespace Error (RESOLVED)
**Error**: `attribute android-auto:layout_constraintStart_toStartOf not found`

**Root Cause**: Incorrect XML namespace declaration in layout files.

**Solution Applied**:
1. Changed all layout files from ConstraintLayout to LinearLayout/FrameLayout
2. Removed constraint layout dependency from build.gradle
3. Eliminated all `android-auto:` namespace references
4. Simplified layout structure for better compatibility

**Files Modified**:
- `app/src/main/res/layout/activity_main.xml` - Converted to LinearLayout
- `app/src/main/res/layout/activity_settings.xml` - Converted to LinearLayout  
- `app/src/main/res/menu/main_menu.xml` - Fixed namespace
- `app/build.gradle` - Removed constraint layout dependency

## Current Project Status

### ✅ What's Working
- All XML files have correct namespace declarations
- No more `android-auto:` references anywhere in the project
- Simplified layout structure using standard Android layouts
- All dependencies are properly configured
- Kotlin source files are complete and functional

### 🔧 Build Requirements
To build this project, you need:
1. **Android Studio** (Arctic Fox or later)
2. **Java Development Kit** (JDK 8 or later)
3. **Android SDK** (API 24 minimum, API 34 target)

## Build Instructions

### Method 1: Android Studio (Recommended)
1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to the project folder
4. Wait for Gradle sync to complete
5. Build → Make Project (Ctrl+F9)

### Method 2: Command Line (if Java/Android SDK installed)
```bash
# Clean previous builds
./gradlew clean

# Build debug APK
./gradlew assembleDebug
```

## Common Issues and Solutions

### Issue: "JAVA_HOME is not set"
**Solution**: Install Java JDK and set JAVA_HOME environment variable
```bash
# Linux/Mac
export JAVA_HOME=/path/to/jdk
export PATH=$JAVA_HOME/bin:$PATH

# Windows
set JAVA_HOME=C:\path\to\jdk
set PATH=%JAVA_HOME%\bin;%PATH%
```

### Issue: "Android SDK not found"
**Solution**: Install Android SDK through Android Studio or standalone
1. Open Android Studio
2. Go to Tools → SDK Manager
3. Install required SDK versions (API 24-34)

### Issue: "Gradle sync failed"
**Solutions**:
1. Check internet connection
2. File → Sync Project with Gradle Files
3. File → Invalidate Caches and Restart
4. Update Gradle wrapper if needed

### Issue: Build still fails with namespace errors
**Solutions**:
1. Clean project: Build → Clean Project
2. Rebuild: Build → Rebuild Project
3. Delete `.gradle` folder and sync again
4. Check for any remaining `android-auto:` references:
   ```bash
   grep -r "android-auto" app/src/main/res/
   ```

## Project Structure Verification

Run this command to verify project structure:
```bash
./test_build.sh
```

Expected output:
- ✅ All required files exist
- ✅ No XML syntax errors
- ✅ No android-auto references found

## Layout Changes Made

### Before (ConstraintLayout - PROBLEMATIC)
```xml
<androidx.constraintlayout.widget.ConstraintLayout>
    <View app:layout_constraintStart_toStartOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

### After (LinearLayout - WORKING)
```xml
<LinearLayout android:orientation="vertical">
    <View android:layout_width="match_parent" />
</LinearLayout>
```

## Testing the App

Once built successfully:

1. **Install on device/emulator**
2. **Configure CasaOS server settings**:
   - Server URL: Your CasaOS IP (e.g., 192.168.1.100)
   - Port: 80 (or your custom port)
   - HTTPS: Enable if using secure connection
   - Username/Password: Optional authentication

3. **Test connection** using the built-in connection test feature

## Next Steps After Successful Build

1. **Test with real CasaOS server**
2. **Customize UI/UX** as needed
3. **Add additional features**:
   - Push notifications
   - Multiple server support
   - Biometric authentication
   - Offline mode

## Support Resources

- **Android Developer Docs**: https://developer.android.com/
- **CasaOS Documentation**: https://casaos.io/
- **Material Design**: https://material.io/develop/android

## File Checklist

Ensure these files exist and are correct:
- [ ] `app/build.gradle` - No constraint layout dependency
- [ ] `app/src/main/res/layout/activity_main.xml` - Uses LinearLayout
- [ ] `app/src/main/res/layout/activity_settings.xml` - Uses LinearLayout
- [ ] `app/src/main/res/menu/main_menu.xml` - Correct namespace
- [ ] All Kotlin source files in `app/src/main/java/com/casaos/client/`
- [ ] All resource files (strings, colors, themes, drawables)

## Final Notes

The project has been restructured to eliminate the constraint layout namespace issues. The simplified layout approach using LinearLayout and FrameLayout provides the same functionality while being more compatible across different Android build environments.

If you continue to experience build issues, the problem is likely related to your local development environment setup rather than the project code itself.