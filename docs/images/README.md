
*Last update: April 21, 2025*


This directory contains all images used in the Photo Scanner documentation. 
## Image Naming Convention

- Example: `feature-name-description.png`
- Example: `metadata-extraction-flow.png`
- Screenshot format: `screen-name-action.png` (e.g., `settings-resolution-selection.png`)
2. **Size**: Keep image file sizes under 500KB when possible
3. **Dimensions**: 
   - Screenshots should be no wider than 1280px
   - Diagrams should be sized appropriately for clarity
4. **Naming Convention**: Use lowercase with hyphens and descriptive names
   - Example: `main-screen-dark-mode.png`
   - Example: `batch-scanning-flow-diagram.png`
5. **Organization**: 
   - Place all images in this directory
   - No subdirectories unless the image collection becomes very large

## Placeholder Images

The documentation currently references images that may not exist yet. These images need to be created and added to this directory:

- `main-screen.png` - Screenshot of the main application screen
- `camera-interface.png` - Labeled screenshot of the camera interface
- `batch-mode.png` - Screenshot showing batch mode in action
- `processing-options.png` - Screenshot of image processing options
- `file-naming-dialog.png` - File naming configuration dialog
- `resolution-settings.png` - Resolution selection dialog
- `metadata-dialog.png` - Metadata viewing interface

For consistency in documentation screenshots:

1. Use a clean installation of the app
2. Use a recent Android device or emulator with minimal customization
3. Clear any personal data from view
4. Ensure the UI is in English
5. Capture in normal mode (not dark mode) unless demonstrating dark mode specifically
6. Use the device's screenshot functionality or Android Studio's screenshot tool

## Editing Images

When editing screenshots for documentation:

1. Add callouts or numbered annotations as needed to highlight UI elements
2. Use a consistent style for annotations (red circles, numbered callouts, etc.)
3. Crop to focus on relevant UI elements, removing unnecessary parts
4. Ensure any text in the image is readable
5. Consider adding a thin border around screenshots for better visibility in the documentation

## Using Images in Documentation

When referencing images in Markdown files, use relative paths:

```markdown
![Alt Text](../images/image-name.png)
```

Add appropriate alt text that describes the image for accessibility.

## Image Updates

When updating the app UI, remember to update the corresponding documentation images to keep them in sync with the current version of the application.

