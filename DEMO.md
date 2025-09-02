# CasaOS Android Client Demo

This document demonstrates the key features and functionality of the CasaOS Android Client.

## App Overview

The CasaOS Android Client is a native Android application that provides a seamless way to access your CasaOS server through a WebView interface. It combines the full functionality of the CasaOS web interface with native Android features like secure credential storage and connection management.

## Key Features Demonstrated

### 1. Initial Setup Screen
When you first open the app, you'll see a connection screen with:
- **Status Message**: "Not Connected"
- **Instructions**: "Configure your CasaOS server connection in settings to get started"
- **Settings Button**: Opens the configuration screen
- **Retry Button**: Attempts to reconnect after configuration

### 2. Settings Configuration
The settings screen provides comprehensive server configuration:

#### Server Settings
- **Server URL**: Enter your CasaOS server IP or hostname
  - Example: `192.168.1.100` or `casaos.local`
- **Port**: Configure the port (default: 80)
- **Use HTTPS**: Toggle for secure connections
- **Username**: Optional CasaOS username
- **Password**: Optional CasaOS password (securely encrypted)

#### Connection Testing
- **Test Connection Button**: Validates server accessibility
- **Real-time Feedback**: Shows connection success or detailed error messages

### 3. WebView Integration
Once connected, the app displays the full CasaOS interface:
- **Native Navigation**: Seamless browsing within CasaOS
- **JavaScript Support**: Full functionality of web interface
- **Responsive Design**: Adapts to different screen sizes
- **Pull-to-Refresh**: Swipe down to reload the page

### 4. Security Features
- **Encrypted Storage**: Credentials stored using Android Security Crypto
- **HTTPS Support**: Handles self-signed certificates for local servers
- **Domain Restriction**: Navigation limited to configured CasaOS server

### 5. Error Handling
- **Connection Errors**: Clear error messages with retry options
- **Network Issues**: Graceful handling of connectivity problems
- **Authentication Failures**: Helpful feedback for login issues

## Usage Scenarios

### Scenario 1: Local Network Access
1. Connect to your home WiFi network
2. Configure server URL (e.g., `192.168.1.100`)
3. Set port to 80 (or your custom port)
4. Leave HTTPS disabled for local HTTP connections
5. Test connection and connect

### Scenario 2: Secure HTTPS Connection
1. Enable "Use HTTPS" in settings
2. Configure your server URL
3. Set port to 443 (or your custom HTTPS port)
4. The app automatically handles self-signed certificates
5. Test and connect securely

### Scenario 3: Authenticated Access
1. Configure server URL and port
2. Enter your CasaOS username and password
3. The app will authenticate and store the session token
4. Enjoy authenticated access to your CasaOS instance

## Technical Implementation

### Architecture Highlights
- **MVVM Pattern**: Clean separation of concerns
- **Retrofit + OkHttp**: Robust networking with SSL support
- **Material 3 Design**: Modern Android UI guidelines
- **Encrypted SharedPreferences**: Secure credential storage
- **WebView Optimization**: JavaScript enabled with security considerations

### Network Configuration
- **Connection Testing**: Multi-endpoint health checking
- **Authentication Flow**: JWT token management
- **SSL Handling**: Self-signed certificate support
- **Error Recovery**: Automatic retry mechanisms

### User Experience
- **Intuitive Setup**: Step-by-step configuration process
- **Visual Feedback**: Loading states and progress indicators
- **Responsive Design**: Works on phones and tablets
- **Offline Handling**: Clear messaging when disconnected

## Building and Installation

### Development Setup
1. Open project in Android Studio
2. Sync Gradle dependencies
3. Build and run on device/emulator

### Release Build
1. Configure signing certificate
2. Build release APK: `./gradlew assembleRelease`
3. Install on target devices

## Testing Checklist

- [ ] App launches successfully
- [ ] Settings screen opens and saves configuration
- [ ] Connection testing works with valid/invalid servers
- [ ] WebView loads CasaOS interface correctly
- [ ] Authentication flow works (if enabled)
- [ ] Pull-to-refresh functionality works
- [ ] Back navigation works within WebView
- [ ] Error handling displays appropriate messages
- [ ] App handles network connectivity changes
- [ ] Settings are persisted between app launches

## Future Enhancements

Potential improvements for future versions:
- **Push Notifications**: Server status alerts
- **Offline Mode**: Cached content for basic functionality
- **Multiple Servers**: Support for multiple CasaOS instances
- **Biometric Authentication**: Fingerprint/face unlock
- **Dark Theme**: Enhanced dark mode support
- **Tablet Optimization**: Better tablet layout support

## Conclusion

The CasaOS Android Client successfully bridges the gap between the powerful CasaOS web interface and native Android functionality. It provides a secure, user-friendly way to access your personal cloud from anywhere, with the convenience of a native mobile app experience.