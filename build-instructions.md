# Photo Scanner Build Instructions

This document provides detailed instructions for building the Photo Scanner application for both development (debug) and production (release) purposes.

## Prerequisites

Before you can build the Photo Scanner app, you'll need the following:

- **Java Development Kit (JDK)** 11 or higher
- **Android SDK** with build tools version 30.0.3 or higher
- **Gradle** 7.0.2 or higher (the project includes a Gradle wrapper)
- **Android Studio** (recommended, but optional) version 2020.3.1 or higher
- **Git** for version control

### Environment Setup

Make sure your `JAVA_HOME` and `ANDROID_HOME` environment variables are properly set:

```bash
# For Linux/macOS
export JAVA_HOME=/path/to/your/jdk
export ANDROID_HOME=/path/to/your/android/sdk

# For Windows (set in Environment Variables dialog)
JAVA_HOME=C:\path\to\your\jdk
ANDROID_HOME=C:\path\to\your\android\sdk
```

## Debug Build Instructions

Debug builds are used during development and testing. They contain debugging information and are signed with a debug key.

### Using Command Line

1. Clone the repository:
   ```bash
   git clone https://github.com/username/photo-scanner.git
   cd photo-scanner
   ```

2. Build the debug version:
   ```bash
   ./gradlew clean assembleDebug
   ```

3. The debug APK will be generated at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Using Android Studio

1. Open Android Studio
2. Select "Open an Existing Project" and navigate to the Photo Scanner directory
3. Once the project is loaded, select `Build → Make Project` (or press Ctrl+F9 / Cmd+F9)
4. To build the debug APK, select `Build → Build Bundle(s) / APK(s) → Build APK(s)`
5. After the build completes, click on the "locate" link in the notification to find the APK

## Release Build Instructions

Release builds are optimized, obfuscated (if ProGuard is enabled), and signed with a production key. These builds are intended for distribution to users.

### Using Command Line

1. Ensure you have the keystore file at `keystore/release.keystore`
   (If you don't have this file, see the "Creating a New Keystore" section below)

2. Verify that the signing configuration in `app/build.gradle` matches your keystore:
   ```gradle
   signingConfigs {
       release {
           storeFile file("../keystore/release.keystore")
           storePassword "photoscanner123"
           keyAlias "photo_scanner"
           keyPassword "photoscanner123"
       }
   }
   ```

3. Build the release version:
   ```bash
   ./gradlew clean assembleRelease
   ```

4. The release APK will be generated at:
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

### Using Android Studio

1. Ensure the keystore configuration is set up as described above
2. Select `Build → Generate Signed Bundle / APK`
3. Choose "APK" and click "Next"
4. Fill in the keystore details or use an existing keystore
5. Select the "release" build variant and click "Finish"
6. The signed APK will be generated in the specified location

### Creating a New Keystore

If you need to create a new keystore (only if the existing one is not available):

```bash
keytool -genkey -v -keystore keystore/release.keystore -alias photo_scanner \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -storepass YOUR_STORE_PASSWORD -keypass YOUR_KEY_PASSWORD \
        -dname "CN=Photo Scanner Team, OU=Development, O=Photo Scanner, L=City, ST=State, C=US"
```

**IMPORTANT**: If you create a new keystore, you won't be able to provide updates to existing users without them uninstalling the app first. For this reason, keep your keystore file and passwords secure and backed up.

## Installing on a Device

### Using ADB (Android Debug Bridge)

1. Connect your device to your computer via USB
2. Enable USB debugging on your device (Settings → Developer options → USB debugging)
3. Install the APK using ADB:

   For debug builds:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

   For release builds:
   ```bash
   adb install -r app/build/outputs/apk/release/app-release.apk
   ```

### Using Android Studio

1. Connect your device to your computer via USB
2. Enable USB debugging on your device
3. Select "Run" (or press Shift+F10) and select your device from the list

### Manual Installation

1. Transfer the APK file to your Android device (via USB, email, cloud storage, etc.)
2. On your device, use a file browser to locate the APK
3. Tap on the APK file to start the installation process
4. You may need to allow installation from "Unknown sources" in your device settings

## Keystore Information

The Photo Scanner app uses a keystore located at `keystore/release.keystore` for signing release builds.

### Current Keystore Details

- **Location**: `keystore/release.keystore`
- **Alias**: `photo_scanner`
- **Validity**: 10,000 days from creation
- **Algorithm**: RSA 2048-bit

### Security Considerations

- **IMPORTANT**: The keystore file and its passwords should be kept secure and should NOT be committed to version control
- The `keystore` directory is included in `.gitignore` to prevent accidental commits
- For team development, share the keystore through secure channels
- Consider using a continuous integration (CI) service with encrypted environment variables for automated builds

### Backup Recommendations

- Keep a secure backup of the keystore file (`release.keystore`)
- Document the keystore passwords in a secure password manager
- Without this keystore, you won't be able to update the app on the Play Store under the same package name

## Troubleshooting

- **Build Failures**: Check the Gradle console output for specific error messages
- **Signing Issues**: Verify the keystore exists and the passwords are correct
- **Installation Failures**: If installing a release version over a debug version (or vice versa), you may need to uninstall the app first
- **Lint Errors**: The project is configured with `lintOptions { abortOnError false }` to prevent build failures due to lint errors in excluded files

For additional help, consult the project's issue tracker or contact the development team.

