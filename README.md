# Photo Scanner App

A simple Android application for scanning and digitizing physical photographs using your smartphone camera.

## Features

- **Photo Capture**: Take high-quality photos of physical photographs
- **Local Storage**: Save captured images to your device's gallery
- **Gallery View**: View all captured images directly from the app
- **Permission Handling**: Proper management of camera and storage permissions
- **Navigation Drawer**: Easy access to app features through a hamburger menu
- **Help & About Screens**: Easy access to app information and usage instructions
- **Material Design UI**: Consistent styling with proper alignment and spacing
- **Dark Mode Support**: Proper theme support for both light and dark modes

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
   - Metadata tagging

3. **Refinement**
   - UI/UX improvements
   - Performance optimization
   - Cross-device testing

## License

This project is licensed under the MIT License - see the LICENSE file for details.
## Versions

### v1.0 (Initial Release)
- Basic camera functionality
- Photo capture and storage
- Gallery access
- Basic UI layout
- Permission management

### v1.1
- New app icon
- Navigation drawer with hamburger menu
- Help and About screens
- Resolution selection (high, medium, low)
- Image statistics activity with EXIF data display
- Photo metadata viewing capability
- Improved UI with Material Design components
- Dark mode support
- Consistent styling and better layout
- See [v1.1 Release Notes](v1.1-release-notes.md) for more details

### v1.2
- Custom file naming system with templates
- Sequential numbering for consecutive scans
- Persistent naming preferences between sessions
- Real-time filename preview
- Fixed GPS coordinates extraction
- Improved error handling and stability
- See [v1.2 Release Notes](v1.2-release-notes.md) for more details

### v1.3 (Current)
- Battery-saving camera mode
- Improved metadata handling
- Enhanced UI responsiveness
- Bug fixes and performance improvements

## Acknowledgements

- Built with CameraX, a Jetpack library
- Uses Android's MediaStore for modern storage access
- Implements Material Design components for UI

