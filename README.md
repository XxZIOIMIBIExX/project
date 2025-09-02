# CasaOS Android Client

A native Android application for managing CasaOS servers with Material Design 3 UI.

## Features

### ✅ Completed
- **Native UI Architecture**: Complete transformation from WebView to native Android UI
- **Material Design 3**: Modern, consistent UI following Google's design guidelines
- **Bottom Navigation**: 4-tab structure (Status, Apps, Files, Settings)
- **Login System**: Native login screen with JWT authentication
- **Status Monitoring**: CPU, RAM, and disk usage monitoring with progress bars
- **App Management**: List, start, stop, restart CasaOS applications
- **API Integration**: Comprehensive CasaOS API support (v1/sys, v2/app_management, v3/file)
- **Secure Storage**: Encrypted storage for authentication tokens and settings

### 🔄 In Progress
- **File Manager**: Complete file browser with grid/list view toggle
- **Settings Panel**: Server configuration and app preferences
- **WebSocket Terminal**: SSH terminal access for apps and system

## Setup

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API level 24 (Android 7.0) or higher
- Java Development Kit (JDK 8 or later)
- A running CasaOS instance

### Building the App

1. Clone or download this project
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Build and run the app on your device or emulator

**Note**: If you encounter build errors related to XML namespaces, see the [TROUBLESHOOTING.md](TROUBLESHOOTING.md) file for solutions.

### Configuration

1. Open the app and tap "Open Settings"
2. Configure your CasaOS server settings:
   - **Server URL**: Your CasaOS server IP or hostname (e.g., `192.168.1.100`)
   - **Port**: CasaOS server port (default: 80)
   - **Use HTTPS**: Enable for secure connections
   - **Username**: Your CasaOS username (optional)
   - **Password**: Your CasaOS password (optional)
3. Tap "Test Connection" to verify the settings
4. Return to the main screen to connect

## Architecture

### 📱 App Structure

```
LoginActivity (Launcher)
├── Server connection setup
├── JWT authentication
└── → MainActivity (on successful login)

MainActivity (Bottom Navigation)
├── StatusFragment - System monitoring
├── AppsFragment - Application management  
├── FilesFragment - File browser (placeholder)
└── SettingsFragment - Configuration (placeholder)
```

### Key Components

- **LoginActivity**: Native login screen with server configuration
- **MainActivity**: Main app with bottom navigation and fragment management
- **StatusFragment**: Real-time system monitoring with progress bars
- **AppsFragment**: CasaOS application management with start/stop controls
- **FilesFragment**: File browser (placeholder implementation)
- **SettingsFragment**: App configuration (placeholder implementation)
- **NetworkManager**: Comprehensive CasaOS API communication
- **SettingsManager**: Secure storage and retrieval of configuration data
- **CasaOSApiService**: Retrofit interface for all CasaOS API endpoints

### Security Features

- **Encrypted Storage**: User credentials are stored using Android's EncryptedSharedPreferences
- **Certificate Validation**: Supports self-signed certificates for development environments
- **Authentication Token Management**: Handles JWT tokens for authenticated sessions

### Network Configuration

The app supports various CasaOS deployment scenarios:

- **Local Network**: Direct connection to CasaOS on local network
- **HTTPS with Self-Signed Certificates**: For secure local connections
- **Authentication**: Optional username/password authentication
- **Port Configuration**: Flexible port configuration for custom setups

## CasaOS API Integration

The app integrates with CasaOS APIs:
- **Authentication**: `/v1/auth/login` - JWT token-based authentication
- **System Info**: `/v1/sys/hardware` - CPU, RAM, disk monitoring
- **App Management**: `/v2/app_management/*` - List, control applications
- **File Operations**: `/v3/file/*` - File browser and management
- **Terminal Access**: `/v1/sys/wsssh` - WebSocket SSH terminal

### 🔧 Technical Stack

- **Language**: Kotlin
- **UI Framework**: Material Design 3
- **Networking**: Retrofit 2 + OkHttp
- **JSON Parsing**: Gson with lenient configuration
- **Security**: Android Security Crypto for token storage
- **Architecture**: MVVM with Fragment-based navigation

### 📦 Build Information

- **Target SDK**: Android 34 (API Level 34)
- **Min SDK**: Android 24 (API Level 24)
- **Build Tools**: Gradle 8.2, Java 17
- **APK Size**: ~7.2MB (debug build)

## Troubleshooting

### Connection Issues

1. **Server Unreachable**: 
   - Verify the server URL and port
   - Check network connectivity
   - Ensure CasaOS is running

2. **Authentication Failed**:
   - Verify username and password
   - Check if authentication is required

3. **HTTPS Issues**:
   - For self-signed certificates, the app accepts them automatically
   - Ensure the correct protocol (HTTP/HTTPS) is selected

### WebView Issues

1. **Page Not Loading**:
   - Check JavaScript is enabled (enabled by default)
   - Verify the server is accessible
   - Try refreshing the page

2. **Navigation Issues**:
   - The app restricts navigation to the configured CasaOS domain
   - Use the back button to navigate within the WebView

## Development

### Project Structure

```
app/src/main/java/com/casaos/client/
├── data/           # Data models and classes
├── network/        # Network services and API interfaces
├── ui/             # UI components and fragments
├── utils/          # Utility classes
├── MainActivity.kt # Main activity
└── SettingsActivity.kt # Settings activity
```

### Key Dependencies

- **Retrofit**: HTTP client for API communication
- **OkHttp**: HTTP client with SSL support
- **Material Components**: UI components
- **Security Crypto**: Encrypted storage
- **Preference**: Settings UI

### Building for Release

1. Update version in `app/build.gradle`
2. Configure signing in Android Studio
3. Build release APK: `./gradlew assembleRelease`

## License

This project is open source. Please check the CasaOS project for their licensing terms.

## Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## Disclaimer

This is an unofficial client for CasaOS. It is not affiliated with or endorsed by the CasaOS team.