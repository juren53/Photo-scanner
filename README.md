# Photo Scanner App

A simple Android application for scanning and digitizing physical photographs using your smartphone camera.

## Features

- **Photo Capture**: Take high-quality photos of physical photographs
- **Local Storage**: Save captured images to your device's gallery
- **Gallery View**: View all captured images directly from the app
- **Permission Handling**: Proper management of camera and storage permissions

## Planned Features

- Image optimization (auto-cropping, perspective correction)
- Edge detection
- Batch scanning capability
- Organization/categorization system
- IPTC metadata tagging
- Connection to local computer for transfer
- Enhanced image processing using OpenCV

## Requirements

- Android device running Android 5.0 (API level 21) or higher
- Camera permission
- Storage permission
- Java Development Kit (JDK) 11 or higher (for development)
- Android SDK (for development)

## Building the App

### Prerequisites

- JDK 11 or higher
- Android SDK with build tools version 30.0.3 or higher
- Gradle 7.0.2 or higher

### Build Instructions

1. Clone the repository:
   ```
   git clone https://github.com/username/photo-scanner.git
   cd photo-scanner
   ```

2. Build the app using Gradle:
   ```
   ./gradlew build
   ```

3. Install the app on a connected device or emulator:
   ```
   ./gradlew installDebug
   ```

## Usage

1. Launch the Photo Scanner app on your Android device
2. Grant camera and storage permissions when prompted
3. Use the camera preview to frame the photograph you want to scan
4. Press the "Capture" button to take a picture
5. The image will be saved to your device's gallery
6. Press the "View Photos" button to see all captured images

## Development Phases

This app is being developed in phases:

1. **Core Development** (Current Phase)
   - Basic camera implementation
   - Image capture functionality
   - Storage management

2. **Advanced Features** (Upcoming)
   - Edge detection
   - Auto-cropping
   - Image enhancement

3. **Refinement**
   - UI/UX improvements
   - Performance optimization
   - Cross-device testing

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements

- Built with CameraX, a Jetpack library
- Uses Android's MediaStore for modern storage access

