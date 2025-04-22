
*Last update: April 21, 2025*


This guide explains the various settings and preferences available in Photo Scanner and how to configure them for optimal use.

## Introduction to Settings

Photo Scanner offers several customizable settings that allow you to tailor the app's behavior to your specific needs. These settings control aspects such as:

- Image quality and resolution
- File naming conventions
- Permission management
- Camera behavior
- Interface preferences

Configuring these settings appropriately can enhance your scanning experience by optimizing for quality, storage efficiency, battery life, or workflow preferences.

![Settings Overview](../images/settings-overview.png)

## Accessing Settings

Photo Scanner's settings are accessible through multiple paths, depending on the setting type:

### Via Navigation Drawer

Most settings are accessed through the navigation drawer:

1. Launch Photo Scanner
2. Tap the menu icon (☰) in the top-left corner to open the navigation drawer
3. Look for specific settings options:
   - "Resolution" for image quality settings
   - "File Naming Template" for filename configuration
   - "Help" for documentation and assistance

### Settings Available in Navigation Drawer

The navigation drawer provides access to the following settings and features:

- **Meta-tags**: View and manage photo metadata
- **Edit**: Access photo editing features
- **File Naming Template**: Configure automatic file naming
- **Statistics**: Review captured images and statistics
- **Resolution**: Adjust photo capture quality
- **Help**: Access user documentation
- **About**: View app information and version
- **Exit**: Close the application

### During First Launch

Some settings, particularly permissions, are configured during the app's first launch:

1. When you first launch Photo Scanner, you'll be prompted to grant necessary permissions
2. The app will guide you through the permission setup process
3. You can also access permissions later through your device's system settings

## Detailed Settings Explanation

### Permission Settings

Photo Scanner requires the following permissions to function properly:

#### Camera Permission

- **Purpose**: Allows the app to access your device's camera for capturing photos
- **Required**: Yes (essential for core functionality)
- **When Requested**: At first launch or when using camera features
- **How to Grant**:
  1. Tap "Allow" when prompted, or
  2. Go to your device's Settings > Apps > Photo Scanner > Permissions > Camera

#### Storage Permission

- **Purpose**: Allows the app to save photos to your device
- **Required**: Yes (essential for core functionality)
- **When Requested**: At first launch or when saving photos
- **How to Grant**:
  1. Tap "Allow" when prompted, or
  2. Go to your device's Settings > Apps > Photo Scanner > Permissions > Storage
- **Note**: On Android 10 (API 29) and above, the app uses more limited storage access

### Resolution Settings

Control the quality and file size of your scanned images:

- **High**: Original camera resolution (maximum quality, largest file size)
- **Medium**: 1280×960 pixels (balanced quality and size, default setting)
- **Low**: 640×480 pixels (lowest quality, smallest file size)

**How to Access**:
1. Open the navigation drawer by tapping the menu icon (☰)
2. Tap "Resolution"
3. Select your preferred option
4. Tap "OK" to confirm

**Persistence**: Your selected resolution setting is remembered between app sessions.

### File Naming Settings

Configure how your scanned photos are named:

- **Name Template**: Base text for your filenames (e.g., "FamilyPhotos")
- **Starting Number**: The number to start sequential numbering from (typically 1)

**How to Access**:
1. Open the navigation drawer by tapping the menu icon (☰)
2. Tap "File Naming Template"
3. Enter your desired template and starting number
4. View the preview of how names will appear
5. Tap "Save" to confirm

**Persistence**: Your file naming template and current counter are saved between app sessions.

### Camera Settings

Configure camera behavior:

#### Battery-Saving Mode

- **Purpose**: Automatically turns off the camera after capturing a photo to save battery
- **Behavior**: Camera is initialized again when you press the "Start Camera" button
- **Default**: Enabled
- **Note**: This feature is particularly useful during long scanning sessions

**How to Toggle**:
- The battery-saving mode is built into the app's workflow. The camera automatically powers down after capturing an image.

### Interface Preferences

Photo Scanner's interface can be customized in the following ways:

#### Theme Settings

Photo Scanner respects your device's system theme settings:

- **Light Theme**: Used when your device is set to light mode
- **Dark Theme**: Used when your device is set to dark mode
- **How to Change**: Change your device's system theme in the system settings

## How Settings Are Saved

Photo Scanner uses Android's SharedPreferences system to store your settings:

- **Automatic Saving**: Settings are saved automatically when you change them
- **Persistence Between Sessions**: Settings remain in effect when you close and reopen the app
- **Device Specific**: Settings are stored on the current device and are not synchronized across devices
- **Retained During Updates**: Settings are preserved when you update the app

## Recommended Settings for Different Scenarios

### For Family Photo Digitization

- **Resolution**: High (to preserve maximum detail)
- **File Naming**: Use a descriptive template like "FamilyAlbum" or "Vacation1995"
- **Battery-Saving Mode**: Keep enabled for long scanning sessions

### For Document Scanning

- **Resolution**: Medium (balances readability with file size)
- **File Naming**: Use categories like "Receipts" or "Documents"

### For Quick Reference Scans

- **Resolution**: Low (for faster processing and smaller file sizes)
- **File Naming**: Simple template like "Scan" or "Quick"

### For Limited Storage Devices

- **Resolution**: Medium or Low (to conserve space)
- **Cleanup**: Regularly transfer photos to other storage

### For Maximum Quality

- **Resolution**: High
- **Lighting**: Ensure good lighting conditions
- **Stability**: Hold the device steady or use a tripod

## Managing App Permissions

### Checking Current Permission Status

To verify what permissions are currently granted to Photo Scanner:

1. Go to your device's Settings
2. Tap "Apps" or "Applications"
3. Find and tap "Photo Scanner"
4. Tap "Permissions"
5. Review the list of permissions and their status

### Updating Permissions

If you accidentally denied a required permission or need to change permissions:

1. Go to your device's Settings > Apps > Photo Scanner > Permissions
2. Tap on the permission you want to modify
3. Select "Allow" or the appropriate option
4. Return to Photo Scanner and retry the feature that requires the permission

### Permission Troubleshooting

If you experience permission-related issues:

- **Camera Not Working**: Verify camera permission is granted
- **Cannot Save Photos**: Check storage permission
- **Permission Denied Messages**: Follow the prompts to open settings and grant the required permission

## Additional Configuration Options

### Storage Location

Photos are saved to your device's standard photo location:

- On Android 10 (API 29) and above: The standard Pictures directory
- On older Android versions: Pictures/PhotoScanner directory

This location cannot be changed within the app but follows Android system conventions.

### Language Settings

Photo Scanner uses your device's system language settings:

- The app will display text in your device's selected language if supported
- If your language is not supported, the app will use English by default
- To change language, change your device's system language settings

## Resetting App Settings

If you need to reset all settings to their default values:

1. Go to your device's Settings > Apps > Photo Scanner
2. Tap "Storage"
3. Tap "Clear Data" (note: this will reset all settings and may delete local app data)
4. Restart Photo Scanner
5. Set up your preferences again

## Next Steps

After configuring your settings, you might want to explore:

- [Basic Features](basic-features.md) to learn the fundamentals of Photo Scanner
- [Advanced Features](advanced-features.md) to make the most of the app's capabilities
- [Troubleshooting](troubleshooting.md) for solutions to common issues

For specific settings, you can learn more in the dedicated guides for [Resolution Settings](resolution.md) and [File Naming Templates](file-naming.md).

