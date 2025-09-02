# CasaOS Android Client

An Android application that connects to CasaOS servers and displays the web interface in a WebView.

## Features

- **WebView Integration**: Displays the CasaOS web interface natively in the app
- **Secure Configuration**: Encrypted storage of server credentials using Android Security Crypto
- **Connection Testing**: Built-in connection testing to verify server accessibility
- **Authentication Support**: Handles CasaOS authentication with username/password
- **HTTPS Support**: Supports both HTTP and HTTPS connections (including self-signed certificates)
- **Material Design**: Modern Material 3 design with light/dark theme support
- **Offline Handling**: Graceful handling of connection errors with retry functionality

## Setup

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API level 24 (Android 7.0) or higher
- A running CasaOS instance

### Building the App

1. Clone or download this project
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Build and run the app on your device or emulator

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

### Key Components

- **MainActivity**: Main screen with WebView and connection management
- **SettingsActivity/SettingsFragment**: Configuration screen for server settings
- **NetworkManager**: Handles API communication and connection testing
- **SettingsManager**: Secure storage and retrieval of configuration data
- **CasaOSApiService**: Retrofit interface for CasaOS API endpoints

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

The app integrates with CasaOS through:

1. **Health Check**: Tests server availability via `/v1/sys/health` or home page
2. **Authentication**: Login via `/v1/auth/login` endpoint (if credentials provided)
3. **Web Interface**: Loads the full CasaOS web interface in WebView

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