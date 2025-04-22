# Troubleshooting

*Last update: April 21, 2025*

This guide helps you solve common issues you might encounter while using Photo Scanner.

## Introduction

Even the best-designed apps occasionally face issues due to device differences, operating system variations, or unexpected scenarios. This troubleshooting guide addresses common problems that Photo Scanner users might experience and provides step-by-step solutions to resolve them.

If you encounter a problem while using Photo Scanner, this section will help you:

- Identify the specific issue you're experiencing
- Apply tested solutions to resolve the problem
- Understand how to prevent similar issues in the future
- Determine when to seek additional support

![Troubleshooting Overview](../images/troubleshooting-overview.png)

## Camera Problems

### Camera Won't Initialize

**Symptoms**: Blank camera preview, "Error starting camera" message, or camera preview doesn't appear
***Solutions**:

1. **Restart Camera**:
   - Tap the "Start Camera" button again
   - If denied, grant the permission and restart the app

2. **Force Camera Restart**:
   - Close the app completely (swipe it away from recent apps)
   - Restart your device
   - Relaunch Photo Scanner
   - Press the "Start Camera" button

3. **Check for Camera Conflicts**:
   - Close any other apps that might be using the camera
   - Try switching to a different camera app briefly, then back to Photo Scanner

4. **Clear App Cache**:
   - Go to Settings > Apps > Photo Scanner > Storage
   - Tap "Clear Cache" (not "Clear Data" which would reset all settings)
   - Restart the app

### Camera Shows Black Screen

**Symptoms**: Camera initializes but shows only a black screen instead of a preview

**Solutions**:

1. **Restart Camera**:
   - Toggle the batch mode switch off and on
   - Tap the "Start Camera" button again

2. **Check Physical Camera**:
   - Make sure the camera lens isn't covered or obstructed
   - Clean the lens if it appears dirty

3. **Device Reboot**:
   - Restart your device completely
   - Try the app again after reboot

### Camera is Slow or Laggy

**Symptoms**: Camera preview is jerky, unresponsive, or has significant delay

**Solutions**:

1. **Free Up Resources**:
   - Close other apps running in the background
   - Restart your device to clear memory

2. **Lower Resolution**:
   - Open the navigation drawer (☰)
   - Tap "Resolution" and select a lower setting
   - Try the camera again

3. **Battery Saving Mode**:
   - Check if your device is in battery saving mode, which might limit performance
   - Temporarily disable battery saving for testing

### Photo Capture Fails

**Symptoms**: App crashes or shows an error when trying to take a photo

**Solutions**:

1. **Check Storage Space**:
   - Verify you have sufficient storage space on your device
   - Delete unnecessary files or apps if storage is low

2. **Permissions Verification**:
   - Ensure both Camera and Storage permissions are granted
   - For Android 10+, verify that the app has proper storage access

3. **Stable Position**:
   - Hold the device more steadily when capturing
   - Avoid moving during the capture process

## Storage and Saving Issues

### Cannot Save Photos

**Symptoms**: "Failed to save image" error or photos don't appear in gallery after capture

**Solutions**:

1. **Storage Permission**:
   - Verify storage permission is granted:
     - Settings > Apps > Photo Scanner > Permissions > Storage
     - Set to "Allow" if not already enabled

2. **Storage Space Check**:
   - Ensure your device has sufficient free space:
     - Go to Settings > Storage to check available space
     - Free up space by deleting unnecessary files if needed

3. **Storage Location Access**:
   - For Android 10+ users:
     - Go to Settings > Privacy > Permission Manager > Files and Media
     - Find Photo Scanner and allow access to media
   - For older Android versions:
     - Ensure external storage is mounted properly

4. **Alternative Save Method**:
   - Try changing the resolution setting to see if it affects saving capability
   - Restart the app and attempt saving again

### Photos Missing from Gallery

**Symptoms**: Successfully captured photos don't appear in device gallery

**Solutions**:

1. **Media Scan Trigger**:
   - Connect your device to a computer
   - Disconnect after a few seconds to trigger a media scan

2. **Gallery App Refresh**:
   - Force close your gallery app
   - Reopen it to scan for new media

3. **File Location Check**:
   - Navigate to the correct storage location:
     - For Android 10+: Pictures directory in your device's storage
     - For older versions: Pictures/PhotoScanner directory
   - Check if files exist using a file manager app

4. **Restart Device**:
   - Reboot your device to refresh media database
   - Check gallery again after restart

### Duplicate Files Created

**Symptoms**: Multiple copies of the same photo appear after scanning

**Solutions**:

1. **Check Batch Mode**:
   - Ensure batch mode isn't toggled on unintentionally
   - Toggle it off if not needed

2. **Review Capture Process**:
   - Avoid double-tapping the capture button
   - Wait for confirmation that a photo was saved before taking another

3. **Counter Reset**:
   - Check if your file naming counter needs to be reset
   - Update the counter in File Naming Template settings

## Permission Problems

### Permission Denied Despite Granting

**Symptoms**: App still reports permission issues even after granting permissions

**Solutions**:

1. **Force Permission Update**:
   - Go to device Settings > Apps > Photo Scanner
   - Tap "Permissions"
   - Toggle each permission off and then on again
   - Restart the app completely

2. **App Reset**:
   - Uninstall and reinstall the app (note: this will reset all settings)
   - Grant permissions when prompted during first launch

3. **System Update Check**:
   - Ensure your Android OS is up to date
   - Some permission issues are resolved by system updates

### Cannot Access Permission Settings

**Symptoms**: Unable to find or access permission settings for the app

**Solutions**:

1. **Alternative Access Path**:
   - Go to Settings > Privacy > Permission Manager
   - Select Camera or Storage
   - Find Photo Scanner in the list of apps

2. **System Settings Search**:
   - In your device Settings, use the search function
   - Search for "permissions" or "Photo Scanner"

3. **App Info Access**:
   - Long-press the Photo Scanner app icon
   - Select "App info" or equivalent option
   - Tap "Permissions"

## Image Quality Issues

### Blurry Photos

**Symptoms**: Captured photos appear out of focus or blurry

**Solutions**:

1. **Steady Positioning**:
   - Hold your device more steadily when capturing
   - Consider using a surface or tripod for support

2. **Focus Check**:
   - Ensure the camera has focused before capturing
   - Tap the screen to focus if your device supports it
   - Make sure there's adequate lighting

3. **Resolution Increase**:
   - Open the navigation drawer (☰)
   - Tap "Resolution" and select a higher setting

4. **Clean Lens**:
   - Check for smudges or dirt on the camera lens
   - Clean with a soft, lint-free cloth

### Poor Color or Lighting

**Symptoms**: Photos appear too dark, too bright, or with unnatural colors

**Solutions**:

1. **Lighting Improvement**:
   - Ensure the subject is evenly lit
   - Avoid direct sunlight or harsh shadows
   - Try using diffused light sources

2. **White Balance**:
   - Take the photo in natural daylight if possible
   - Avoid mixed lighting conditions (e.g., artificial and natural together)

3. **Device Camera Settings**:
   - Some devices allow adjusting camera settings through the system
   - Check if HDR or other enhancements can be enabled

### Distorted Images

**Symptoms**: Photos appear stretched, cropped incorrectly, or with perspective issues

**Solutions**:

1. **Parallel Alignment**:
   - Hold your device parallel to the document or photo
   - Avoid taking photos at an angle

2. **Distance Check**:
   - Maintain appropriate distance from the subject
   - Too close or too far can cause distortion

3. **Processing Adjustment**:
   - After capture, use the app's processing features to correct perspective
   - Try cropping and straightening the image

## File Naming Issues

### Sequential Numbering Problems

**Symptoms**: File numbering skips, restarts unexpectedly, or doesn't increment

**Solutions**:

1. **Reset Counter**:
   - Open the navigation drawer (☰)
   - Tap "File Naming Template"
   - Set your desired starting number explicitly
   - Save changes

2. **Template Check**:
   - Verify your template doesn't include characters that may be interpreted as formatting

3. **Preferences Reset**:
   - If counter behavior is seriously incorrect:
     - Go to device Settings > Apps > Photo Scanner > Storage
     - Tap "Clear Data" (warning: this resets all settings)
     - Reconfigure your preferences

### Invalid Filename Characters

**Symptoms**: Error messages about invalid filenames or files that don't save properly

**Solutions**:

1. **Remove Special Characters**:
   - Open the navigation drawer (☰)
   - Tap "File Naming Template"
   - Use only letters, numbers, and underscores
   - Avoid symbols like `/ \ : * ? " < > |`

2. **Simplify Template**:
   - Use shorter, simpler templates
   - Test with a basic template like "Scan" to isolate the issue

## App Performance Issues

### App Crashes or Freezes

**Symptoms**: App suddenly closes, stops responding, or shows "App has stopped" message

**Solutions**:

1. **Force Stop and Restart**:
   - Go to device Settings > Apps > Photo Scanner
   - Tap "Force Stop"
   - Relaunch the app

2. **Clear Cache**:
   - Go to device Settings > Apps > Photo Scanner > Storage
   - Tap "Clear Cache"
   - Restart the app

3. **Free Up Resources**:
   - Close other apps running in the background
   - Restart your device to clear memory

4. **App Update Check**:
   - Ensure you're using the latest version of Photo Scanner
   - Check for updates in the Play Store

### Slow Operation

**Symptoms**: App is sluggish, features take a long time to respond

**Solutions**:

1. **Lower Resolution Setting**:
   - Open the navigation drawer (☰)
   - Tap "Resolution" and select a lower setting

2. **Reduce Background Apps**:
   - Close other apps running in the background
   - Check for resource-intensive apps

3. **Device Restart**:
   - Restart your device to clear memory and background processes

4. **Storage Space Check**:
   - Ensure your device has adequate free storage space
   - Performance can degrade when storage is nearly full

## When to Contact Support

If you've tried the relevant troubleshooting steps and still experience issues, you may need additional support. Consider contacting support when:

- You encounter a problem not covered in this guide
- Suggested solutions don't resolve your issue
- You experience repeated crashes or data loss
- You find what appears to be a bug in the software

### How to Report Issues Effectively

When reporting an issue, please include:

1. Your device model and Android version
2. The version of Photo Scanner you're using
3. Detailed steps to reproduce the problem
4. Any error messages displayed
5. Screenshots if applicable
6. What troubleshooting steps you've already tried

## Prevention Tips

### Avoiding Common Problems

Prevent issues before they occur with these best practices:

1. **Regular Maintenance**:
   - Keep your app updated to the latest version
   - Periodically clear the app cache 
   - Maintain sufficient free storage space

2. **Optimal Usage Habits**:
   - Close the app properly when finished
   - Grant all necessary permissions when prompted
   - Follow the recommended workflow for capturing and processing

3. **Device Care**:
   - Keep your device's OS updated
   - Restart your device periodically
   - Keep your camera lens clean

4. **Data Management**:
   - Transfer important photos to a computer or cloud storage
   - Don't rely solely on your device for photo storage
   - Organize photos into appropriate folders

## Next Steps

If you're still experiencing issues after consulting this guide, you might want to:

- Check the [basic features](basic-features.md) guide to ensure you're using the app as intended
- Review [settings and preferences](settings.md) to optimize your configuration
- Explore the [advanced features](advanced-features.md) section for more detailed information

For specific feature troubleshooting, refer to the dedicated guides for [resolution settings](resolution.md) and [file naming](file-naming.md).


[← Back to Table of Contents](../SUMMARY.md)
