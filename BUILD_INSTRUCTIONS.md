# CasaOS Android Client - Build Instructions

## Fixed Issues

The original build errors have been resolved:

### ✅ XML Namespace Issues Fixed
- Changed `xmlns:app="http://schemas.android.com/apk/res/android-auto"` to `xmlns:app="http://schemas.android.com/apk/res-auto"`
- Fixed in all layout files (`activity_main.xml`, `activity_settings.xml`) and menu files

### ✅ Deprecated Method Warnings Addressed
- Added proper deprecation annotations for `onBackPressed()` method
- Added suppression annotations where appropriate

## Build Requirements

### Prerequisites
- **Android Studio**: Arctic Fox (2020.3.1) or later
- **Java**: JDK 8 or later
- **Android SDK**: API level 24 (Android 7.0) minimum
- **Gradle**: 8.4 (included via wrapper)

### Dependencies
All dependencies are automatically downloaded via Gradle:
- AndroidX libraries (Core, AppCompat, Material 3)
- Networking (Retrofit, OkHttp, Gson)
- Security (EncryptedSharedPreferences)
- UI Components (ConstraintLayout, SwipeRefreshLayout, Preferences)

## Build Steps

### Option 1: Android Studio (Recommended)
1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to the project folder and select it
4. Wait for Gradle sync to complete
5. Click "Build" → "Make Project" or press Ctrl+F9
6. Run on device/emulator with "Run" → "Run 'app'" or press Shift+F10

### Option 2: Command Line
```bash
# Navigate to project directory
cd /path/to/project

# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

### Option 3: Using Build Script
```bash
# Use the provided build script
./build.sh
```

## Project Structure

```
project/
├── app/
│   ├── build.gradle                 # App-level build configuration
│   ├── proguard-rules.pro          # ProGuard rules for release builds
│   └── src/main/
│       ├── AndroidManifest.xml     # App manifest
│       ├── java/com/casaos/client/
│       │   ├── MainActivity.kt     # Main activity with WebView
│       │   ├── SettingsActivity.kt # Settings screen
│       │   ├── data/
│       │   │   └── CasaOSConfig.kt # Data models
│       │   ├── network/
│       │   │   ├── CasaOSApiService.kt # API interface
│       │   │   └── NetworkManager.kt   # Network handling
│       │   ├── ui/
│       │   │   └── SettingsFragment.kt # Settings UI
│       │   └── utils/
│       │       └── SettingsManager.kt  # Settings storage
│       └── res/
│           ├── drawable/            # Vector icons
│           ├── layout/              # UI layouts
│           ├── menu/                # Menu definitions
│           ├── values/              # Strings, colors, themes
│           └── xml/                 # Preferences, backup rules
├── build.gradle                     # Project-level build configuration
├── settings.gradle                  # Gradle settings
├── gradle.properties              # Gradle properties
├── gradlew                         # Gradle wrapper (Unix)
├── gradlew.bat                     # Gradle wrapper (Windows)
└── gradle/wrapper/                 # Gradle wrapper files
```

## Configuration

### First Run Setup
1. Install and launch the app
2. Tap "Open Settings" on the connection screen
3. Configure your CasaOS server:
   - **Server URL**: Your CasaOS server IP (e.g., `192.168.1.100`)
   - **Port**: Server port (default: 80)
   - **Use HTTPS**: Enable for secure connections
   - **Username/Password**: Optional authentication credentials
4. Tap "Test Connection" to verify settings
5. Return to main screen to connect

### Server Requirements
- CasaOS server running and accessible on your network
- Port 80 (HTTP) or 443 (HTTPS) open and accessible
- Optional: User account for authentication

## Troubleshooting

### Build Issues
- **Gradle sync failed**: Check internet connection and try "File" → "Sync Project with Gradle Files"
- **SDK not found**: Install Android SDK through Android Studio SDK Manager
- **Build tools missing**: Update build tools in SDK Manager

### Runtime Issues
- **Connection failed**: Verify server URL, port, and network connectivity
- **WebView not loading**: Check JavaScript is enabled (enabled by default)
- **Authentication errors**: Verify username/password if using authentication

### Common Solutions
1. **Clean and rebuild**: "Build" → "Clean Project" then "Build" → "Rebuild Project"
2. **Invalidate caches**: "File" → "Invalidate Caches and Restart"
3. **Update dependencies**: Check for newer versions in build.gradle files
4. **Check logs**: Use Android Studio's Logcat for runtime debugging

## Testing

### Manual Testing Checklist
- [ ] App launches without crashes
- [ ] Settings screen opens and saves configuration
- [ ] Connection testing works with valid/invalid servers
- [ ] WebView loads CasaOS interface correctly
- [ ] Authentication flow works (if enabled)
- [ ] Pull-to-refresh functionality works
- [ ] Back navigation works within WebView
- [ ] Error handling displays appropriate messages
- [ ] Settings persist between app launches

### Test Script
Run the included test script to verify project structure:
```bash
./test_build.sh
```

## Release Build

### Signing Configuration
For release builds, configure signing in `app/build.gradle`:

```gradle
android {
    signingConfigs {
        release {
            storeFile file('path/to/keystore.jks')
            storePassword 'store_password'
            keyAlias 'key_alias'
            keyPassword 'key_password'
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            // ... other release configuration
        }
    }
}
```

### Build Release APK
```bash
./gradlew assembleRelease
```

The signed APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`

## Support

For issues with:
- **Android development**: Check Android Developer documentation
- **CasaOS server**: Refer to CasaOS documentation
- **This app**: Check the README.md and DEMO.md files

## Next Steps

After successful build:
1. Test the app with your CasaOS server
2. Customize the UI/UX as needed
3. Add additional features (push notifications, multiple servers, etc.)
4. Publish to Google Play Store (requires developer account and additional setup)