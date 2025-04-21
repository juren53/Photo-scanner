# Building Photo Scanner

*Last update: April 21, 2025*

This guide provides comprehensive instructions for setting up your development environment, building the Photo Scanner application from source, and testing your build.
## Introduction to the Build Process

Photo Scanner uses Gradle as its build system, which handles dependency management, build configuration, and application packaging. The build process includes:

1. Setting up the development environment
2. Obtaining the source code
3. Configuring project dependencies
4. Compiling the application
5. Running tests
6. Generating debug or release builds

The application is written in Java and targets the Android platform using the Android SDK.

## Prerequisites

### System Requirements

- **Operating System**: Windows, macOS, or Linux
- **RAM**: Minimum 8GB (16GB or more recommended)
- **Disk Space**: At least 10GB of free space for Android Studio, SDKs, and project files
- **Java**: JDK 11 or higher

### Development Environment

#### Required Software

1. **Android Studio** (2020.3.1 or higher)
2. **Android SDK** with:
   - Build Tools (version 30.0.3 or higher)
   - Platform SDK for Android API level 21+
   - Android SDK Command-line Tools
   - Android SDK Platform-Tools
3. **Git** for version control

#### Installing Android Studio

1. Download Android Studio from [developer.android.com](https://developer.android.com/studio)
2. Run the installer and follow the on-screen instructions
3. During setup, ensure you select:
   - Android SDK
   - Android SDK Platform
   - Android Virtual Device (AVD)
   - Performance (Intel HAXM or equivalent)

#### Installing Required SDK Components

After installing Android Studio:

1. Launch Android Studio
2. Go to Tools > SDK Manager
3. In the SDK Platforms tab, select:
   - Android 11.0 (API level 30)
   - Android 5.0 (API level 21)
4. In the SDK Tools tab, select:
   - Android SDK Build-Tools 30.0.3
   - Android SDK Command-line Tools
   - Android SDK Platform-Tools
   - Android Emulator
   - Intel x86 Emulator Accelerator (if using Intel processor)
5. Click Apply to download and install the selected components

## Obtaining the Source Code

### Cloning the Repository

```bash
# Clone the repository
git clone https://github.com/juren53/Photo-scanner.git

# Navigate to the project directory
cd Photo-scanner
```

### Repository Structure

The repository is organized as follows:

```
Photo-scanner/
├── app/                  # Main application module
├── gradle/               # Gradle wrapper files
├── docs/                 # Documentation
├── keystore/             # Release signing keystore
├── build.gradle          # Project-level build script
├── settings.gradle       # Project settings
├── gradle.properties     # Gradle configuration properties
└── README.md             # Project overview
```

## Building the Application

### First-Time Setup

Before building for the first time, you need to configure your local environment:

1. Create a `local.properties` file in the project root directory if it doesn't exist
2. Add the path to your Android SDK:

```properties
sdk.dir=/path/to/your/android/sdk
```

On Windows, use escaped backslashes or forward slashes:
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
# OR
sdk.dir=C:/Users/YourUsername/AppData/Local/Android/Sdk
```

### Using Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to and select the `Photo-scanner` directory
4. Wait for the project to sync and index
5. Click the "Build" menu and select "Build Project"

### Using Command Line

Gradle commands can be executed using the included Gradle wrapper:

```bash
# On Linux/macOS
./gradlew build

# On Windows
gradlew.bat build
```

### Basic Gradle Commands

- `./gradlew assembleDebug` - Builds a debug APK
- `./gradlew assembleRelease` - Builds a release APK (requires signing configuration)
- `./gradlew installDebug` - Builds and installs the debug APK on a connected device
- `./gradlew clean` - Clears build files
- `./gradlew tasks` - Lists all available tasks

## Build Variants

Photo Scanner supports different build variants to facilitate development and testing.

### Build Types

- **Debug**: Default build type for development
  - Debugging enabled
  - Developer features enabled
  - Debug signing key

- **Release**: Production-ready build
  - Optimized and minified
  - Debugging disabled
  - Requires a proper signing key

### Product Flavors

The app currently defines the following product flavors:

- **development**: For internal development and testing with extra logging
- **production**: For public release with optimized settings

### Configuring Build Variants

1. In Android Studio, open the "Build Variants" panel (usually on the left side)
2. Select the desired build variant from the dropdown list
3. Android Studio will reconfigure the project for the selected variant

### Customizing Variants

You can customize build variants in the app's `build.gradle` file:

```groovy
android {
    // ... other config

    buildTypes {
        debug {
            minifyEnabled false
            applicationIdSuffix ".debug"
            versionNameSuffix "-debug"
        }
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    productFlavors {
        development {
            dimension "environment"
            applicationIdSuffix ".dev"
            versionNameSuffix "-dev"
        }
        production {
            dimension "environment"
        }
    }
}
```

## Running Tests

Photo Scanner includes both unit tests and instrumented (UI) tests to ensure code quality.

### Running Unit Tests

Unit tests can be run using Android Studio or Gradle:

#### Using Android Studio

1. Right-click on the test directory or specific test class
2. Select "Run Tests"

#### Using Gradle

```bash
# Run all unit tests
./gradlew test

# Run tests for a specific build variant
./gradlew testDebugUnitTest
```

### Running Instrumented Tests

Instrumented tests require a connected device or emulator:

#### Using Android Studio

1. Connect a device or start an emulator
2. Right-click on the androidTest directory or specific test class
3. Select "Run Tests"

#### Using Gradle

```bash
# Run all instrumented tests on connected device
./gradlew connectedAndroidTest
```

### Viewing Test Results

Test results are available in HTML format at:

- Unit tests: `app/build/reports/tests/testDebugUnitTest/index.html`
- Instrumented tests: `app/build/reports/androidTests/connected/index.html`

## Common Build Issues

### Gradle Sync Failed

**Symptoms**: Project fails to sync with Gradle, showing "Gradle sync failed" error.

**Solutions**:
1. Check your internet connection (for dependency downloads)
2. Verify `local.properties` has the correct SDK path
3. Try "File > Invalidate Caches / Restart" in Android Studio
4. Update Gradle and Gradle Plugin versions if necessary

### Dependency Resolution Problems

**Symptoms**: Build fails with "Could not resolve" or "Could not find" errors.

**Solutions**:
1. Check your internet connection
2. Update the dependency to an available version
3. Add a specific Maven repository if required
4. Verify the dependency exists and is spelled correctly

### Minimum SDK Compatibility Issues

**Symptoms**: "Call requires API level XX" or similar errors.

**Solutions**:
1. Use SDK version check before calling APIs:
```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.XX) {
    // Call newer API
} else {
    // Fallback for older versions
}
```
2. Use Android Jetpack's compatibility libraries

### Java Version Conflicts

**Symptoms**: "Java language level" errors or "Unsupported class file major version".

**Solutions**:
1. Check your JDK version matches project configuration
2. Configure Java compatibility in `build.gradle`:
```groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
}
```

## Creating Release Builds

Release builds should be properly signed and optimized for distribution.

### Signing Configuration

1. Create a keystore for signing (if you don't have one):
```bash
keytool -genkey -v -keystore photo_scanner.keystore -alias photo_scanner -keyalg RSA -keysize 2048 -validity 10000
```

2. Configure your signing information in one of two ways:

#### Option A: In `gradle.properties` (Not for version control)
```properties
RELEASE_STORE_FILE=path/to/photo_scanner.keystore
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=photo_scanner
RELEASE_KEY_PASSWORD=your_key_password
```

#### Option B: In your local environment variables

```
export PHOTO_SCANNER_STORE_FILE=path/to/photo_scanner.keystore
export PHOTO_SCANNER_STORE_PASSWORD=your_keystore_password
export PHOTO_SCANNER_KEY_ALIAS=photo_scanner
export PHOTO_SCANNER_KEY_PASSWORD=your_key_password
```

3. Reference these properties in `app/build.gradle`:

```groovy
android {
    // ... other config

    signingConfigs {
        release {
            storeFile file(RELEASE_STORE_FILE)
            storePassword RELEASE_STORE_PASSWORD
            keyAlias RELEASE_KEY_ALIAS
            keyPassword RELEASE_KEY_PASSWORD
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            // ... other config
        }
    }
}
```

### Building a Release APK

Once signing is configured, build a release APK:

```bash
./gradlew assembleRelease
```

The release APK will be located at `app/build/outputs/apk/release/app-release.apk`

### Building an App Bundle (AAB)

For Google Play distribution, create an Android App Bundle:

```bash
./gradlew bundleRelease
```

The AAB will be located at `app/build/outputs/bundle/release/app-release.aab`

### Verifying Release Builds

1. Install the release APK on a test device:
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

2. Test all critical functionality
3. Verify crash reporting and analytics are configured correctly
4. Check resource efficiency (battery, memory, storage)

## Continuous Integration

Photo Scanner can be integrated with CI systems like GitHub Actions for automated building and testing.

### Sample GitHub Actions Workflow

Create a file `.github/workflows/android.yml`:

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 11
      uses: actions/setup-java@v1
      with:
        java-version: 11
    - name: Build with Gradle
      run: ./gradlew build
    - name: Run Tests
      run: ./gradlew test
```

## Next Steps

After successfully building the application, you may want to:

- [Understand the architecture](architecture.md)
- [Learn how to contribute](contributing.md)
- [Review the project overview](overview.md)

For any build issues not covered here, consult the [Android developer documentation](https://developer.android.com/studio/build) or open an issue in the project repository.

