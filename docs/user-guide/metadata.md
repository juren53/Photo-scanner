
*Last update: April 21, 2025*


This guide explains how to use Photo Scanner's metadata features to view, understand, and manage the technical information associated with your scanned photos.

## Introduction to Metadata

Metadata is information about your photos beyond the image itself—essentially "data about data." It can include details about when and how a photo was taken, what device was used, where it was captured, and much more.

For scanned photos, metadata serves several important purposes:

- **Helps with Organization**: Sort and find photos based on capture date or other attributes
- **Provides Technical Context**: Understand the conditions under which the photo was taken
- **Preserves Historical Information**: Maintains details about the original photographer, date, or location
- **Simplifies Searching**: Enables finding photos based on specific criteria
- **Assists with Authenticity**: Verifies when and how a photo was created

Photo Scanner automatically extracts and displays metadata from your images, giving you access to this valuable information without additional tools or software.

![Metadata Overview](../images/metadata-overview.png)

## Types of Metadata in Photo Scanner

Photo Scanner can extract and display three categories of metadata:

### EXIF Metadata

EXIF (Exchangeable Image File Format) data contains technical information recorded by the camera or scanning device:

- **Camera Information**: Make, model, and software
- **Capture Settings**: Exposure time, aperture, ISO, focal length
- **Date and Time**: When the photo was taken
- **Location**: GPS coordinates (if available)
- **Technical Details**: Resolution, color space, compression

### IPTC Metadata

IPTC (International Press Telecommunications Council) metadata includes descriptive information that can be added to images:

- **Content Description**: Title, caption, description
- **Creator Information**: Photographer name, contact info
- **Copyright**: Rights information and usage terms
- **Categories and Keywords**: Subject matter classification
- **Location Details**: City, state, country names (not GPS coordinates)

*Note: Currently, Photo Scanner focuses on displaying existing IPTC metadata. Full editing capabilities are planned for future versions.*

### Additional Metadata

This category includes file system information and other details:

- **File Information**: Name, type, size, dimensions
- **System Data**: Creation date, modification date
- **App-Specific Data**: Information added by Photo Scanner
- **Color Profile**: Color space information
- **Device-Specific Details**: Additional technical information

## Accessing Metadata

Photo Scanner provides two ways to access metadata information:

### Method 1: From the Navigation Drawer

1. Launch Photo Scanner
2. Open the navigation drawer by tapping the menu icon (☰) in the top-left corner
3. Tap "Meta-tags"
4. If you've taken photos with the app, the metadata dialog will show information for your most recently captured photo
5. If no photos have been taken yet, you'll be prompted to capture an image first

### Method 2: From the Statistics Activity

1. Open the navigation drawer by tapping the menu icon (☰)
2. Tap "Review Images" (listed as "Statistics" in the menu)
3. Select any photo from the gallery view
4. Tap the "Info" button in the toolbar
5. The metadata dialog will appear showing information for the selected photo

## Understanding the Metadata Dialog

The metadata dialog in Photo Scanner presents information in a user-friendly format:

### Dialog Layout

The dialog includes:

1. **Thumbnail**: Preview of the photo being examined
2. **Filename**: Name of the image file
3. **Dimensions and Size**: Image resolution and file size
4. **Tab Navigation**: Switch between EXIF, IPTC, and Additional metadata
5. **Close Button**: Dismiss the dialog when finished

### EXIF Tab Content

The EXIF tab typically displays:

```
Camera: Canon EOS R6
Date: 2025:04:20 15:32:47
Exposure: 1/250 sec
Aperture: f/4.0
ISO: 400
Focal Length: 50mm
Flash: Not used
White Balance: Auto
Location: 40.712776, -74.005974
```

### IPTC Tab Content

The IPTC tab might display (if available):

```
Title: Family Gathering 2025
Creator: Jane Smith
Copyright: © Jane Smith Photography
Keywords: family, reunion, celebration
```

### Additional Tab Content

The Additional tab typically shows:

```
File Type: image/jpeg
Date added: 2025-04-21 10:15:32
Color Space: sRGB
```

## Interpreting Metadata Information

Understanding what the metadata values mean can help you better organize and work with your photos:

### Camera Information

- **Make/Model**: Identifies which device took the photo
- **Software**: What software processed the image originally

### Exposure Information

- **Exposure Time**: How long the shutter was open (e.g., "1/250" means 1/250th of a second)
- **Aperture**: How wide the lens opening was (smaller f-numbers mean wider apertures)
- **ISO**: Light sensitivity (higher numbers mean more sensitivity but potentially more noise)
- **Focal Length**: The lens focal length used (affects perspective and field of view)

### GPS Coordinates

- **Format**: Displayed as decimal degrees (latitude, longitude)
- **Interpretation**: Positive latitude numbers are north of the equator, negative are south. Positive longitude numbers are east of the prime meridian, negative are west.
- **Example**: "40.712776, -74.005974" represents New York City

### Dates and Times

- **Capture Time**: When the original photo was taken
- **Format**: Typically shown as YYYY:MM:DD HH:MM:SS

## How Photo Scanner Handles Metadata

Photo Scanner manages metadata in specific ways during different phases of operation:

### During Capture

- When taking photos with Photo Scanner, the app records:
  - Device information (your smartphone details)
  - Current date and time
  - Resolution settings
  - File naming information
  - App version

### During Processing

- The app preserves existing metadata when processing images
- Some processing actions may add metadata (like editing timestamps)

### Viewing External Photos

- When viewing photos not taken with Photo Scanner:
  - The app extracts available metadata from the file
  - Some fields may be empty if the original photo lacks that information
  - The app displays "No metadata available" for empty sections

## Future Metadata Features

Photo Scanner has plans to expand metadata capabilities in upcoming versions:

- **IPTC Editing**: Add or edit descriptive information like titles and keywords
- **Custom Fields**: Create your own metadata fields for specialized cataloging
- **Batch Metadata**: Apply the same metadata to multiple photos at once
- **Metadata Templates**: Save sets of metadata for quick application
- **Export Options**: Include or exclude metadata when sharing photos

## Tips for Using Metadata Effectively

### Organization Strategies

- **Date-Based Organization**: Use capture dates to sort photos chronologically
- **Location Grouping**: Group photos by GPS coordinates for travel collections
- **Device Tracking**: Identify which device was used for different photos
- **Technical Comparison**: Compare settings between similar photos to identify optimal conditions

### Practical Applications

- **Family History**: Record dates and locations for genealogical research
- **Documentation**: Verify when documents were scanned for record-keeping
- **Photo Projects**: Group photos by technical characteristics for consistent editing
- **Learning Photography**: Study the settings used for successful photos

### Best Practices

- **Regular Reviews**: Periodically check metadata to ensure important information is preserved
- **Backup Metadata**: When exporting or backing up photos, ensure metadata is included
- **Consistent Workflow**: Develop a routine for checking and using metadata
- **Privacy Awareness**: Remember that metadata may contain location information you might not want to share

## Troubleshooting Metadata Issues

| Problem | Solution |
|---------|----------|
| No metadata appears | The original photo may not contain metadata; try scanning a different photo |
| GPS coordinates missing | The original camera may not have recorded location data, or location services were disabled |
| Incorrect date/time | The camera's clock may have been set incorrectly when the original was taken |
| Some fields show as "Unknown" | Not all devices record all types of metadata; some fields may be unavailable |
| IPTC data not appearing | Many consumer cameras don't record IPTC metadata; this is more common in professional settings |

## Next Steps

After exploring metadata management, you might want to learn about:

- [File Naming Templates](file-naming.md) for organizing your collection
- [Viewing and Managing Photos](viewing-managing.md) to better utilize your metadata

For general app settings, check out the [Settings and Preferences](settings.md) guide.


[← Back to Table of Contents](../SUMMARY.md)
