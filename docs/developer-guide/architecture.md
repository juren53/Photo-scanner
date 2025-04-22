# Photo Scanner Architecture

*Last update: April 21, 2025*

This document provides an in-depth look at the architecture of the Photo Scanner application, describing its components, their interactions, and the design patterns employed.
## Architectural Overview

Photo Scanner follows a modular architecture that separates different concerns into distinct components, making the application more maintainable, testable, and extensible.

### High-Level Architecture

![Architecture Diagram](../images/architecture-detailed.png)

At the highest level, Photo Scanner is structured into five distinct layers:

1. **Presentation Layer**: UI components including activities, fragments, and adapters
2. **Business Logic Layer**: Core application logic that processes user inputs and manages workflows
3. **Camera Interaction Layer**: Components handling camera initialization, preview, and capture
4. **Data Management Layer**: Storage, file naming, and metadata handling
5. **Utility Layer**: Helper classes and cross-cutting concerns

## Component Details

### Presentation Layer

The presentation layer is responsible for displaying the user interface and handling user interactions. It follows an activity-based approach with supporting dialogs.

#### Key Components:

- **MainActivity**: The primary entry point for the application, responsible for:
  - Initializing the camera interface
  - Managing the navigation drawer
  - Handling permissions
  - Coordinating between different features
  
- **StatisticsActivity**: Displays collected photos and their metadata
  
- **DialogComponents**: Various dialog classes for:
  - Resolution settings
  - File naming templates
  - Metadata display
  - Help and about information

#### UI Pattern:

Photo Scanner uses a simplified MVC (Model-View-Controller) pattern where:
- Activities and Fragments act as both View and Controller
- Data classes serve as the Model
- Business logic is handled within activities or dedicated manager classes

### Business Logic Layer

This layer contains the core application logic that processes user inputs and manages application workflows.

#### Key Components:
- **CameraManager**: Initializes and controls the camera functionality
  
- **FileNamingManager**: Handles file naming templates and sequential numbering
  
- **SettingsManager**: Manages application settings and preferences

### Camera Interaction Layer

This layer handles all camera-related functionality, abstracting the complexities of the Android camera API.

#### Key Components:

- **CameraProvider**: Wraps Android's `ProcessCameraProvider` to simplify camera operations
  
- **ImageCapture**: Manages photo capture functionality
  
- **Preview**: Handles the camera preview display

The camera layer uses the CameraX Jetpack library, which provides a consistent API across different Android devices and versions.

### Data Management Layer

The data management layer handles all aspects of data storage, retrieval, and manipulation.

#### Key Components:

- **StorageManager**: Handles file saving and retrieval
  
- **MetadataExtractor**: Extracts and processes EXIF and other metadata
  
- **PreferencesManager**: Manages user preferences and settings persistence

### Utility Layer

The utility layer provides helper functions and handles cross-cutting concerns.

#### Key Components:

- **PermissionHelper**: Simplifies permission management
  
- **LogUtil**: Unified logging mechanism
  
- **ImageProcessingUtils**: Utilities for image processing and manipulation

## Design Patterns

Photo Scanner employs several design patterns to improve its architecture:

### Singleton Pattern

Used for managers and utility classes that need to maintain a single instance:

```java
public class SettingsManager {
    private static SettingsManager instance;
    
    private SettingsManager() {
        // Private constructor
    }
    
    public static synchronized SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }
    
    // Methods...
}
```

### Builder Pattern

Employed for complex object construction, such as camera configurations:

```java
ImageCapture imageCapture = new ImageCapture.Builder()
    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
    .build();
```

### Observer Pattern

Used for event notification, particularly with the CameraX API:

```java
cameraProviderFuture.addListener(() -> {
    try {
        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
        // Setup camera...
    } catch (Exception e) {
        // Handle exception
    }
}, ContextCompat.getMainExecutor(this));
```

### Factory Method Pattern

Used for creating different types of objects based on runtime conditions:

```java
public class DialogFactory {
    public static Dialog createDialog(Context context, DialogType type) {
        switch (type) {
            case RESOLUTION:
                return new ResolutionDialog(context);
            case METADATA:
                return new MetadataDialog(context);
            // Other cases...
            default:
                return null;
        }
    }
}
```

## Code Organization

The Photo Scanner codebase is organized into packages that reflect its architectural layers and functional areas.

### Package Structure

```
com.photoscanner.app/
├── activities/           # Main application activities
│   ├── MainActivity.java
│   └── StatisticsActivity.java
├── camera/               # Camera functionality
│   ├── CameraManager.java
│   └── CaptureManager.java
├── ui/                   # UI components
│   ├── adapters/         # RecyclerView adapters
│   ├── dialogs/          # Dialog classes
│   └── views/            # Custom views
├── data/                 # Data management
│   ├── metadata/         # Metadata extraction and handling
│   ├── preferences/      # User preferences
│   └── storage/          # File storage management
├── util/                 # Utility classes
│   ├── ImageUtils.java
│   ├── LogUtils.java
│   └── PermissionUtils.java
└── model/                # Data models
    ├── Photo.java
    ├── Resolution.java
    └── MetadataEntry.java
```

## Key Classes and Responsibilities

### MainActivity

The central controller of the application with responsibilities including:

- Camera initialization and management
- Permission handling
- Navigation drawer setup and management
- Workflow coordination between features
- UI state management
- Event handling for user interactions

```java
public class MainActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener {
    // Camera components
    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    
    // Settings
    private PhotoResolution currentResolution;
    private String nameTemplate;
    private int nameCounter;
    
    // Methods for camera management, photo capturing, etc.
}
```

### CameraManager

Encapsulates camera initialization, configuration, and operation:

```java
public class CameraManager {
    private final ProcessCameraProvider cameraProvider;
    private final ImageCapture.Builder imageCaptureBuilder;
    
    // Methods for camera lifecycle management
    public void startCamera() { /* ... */ }
    public void stopCamera() { /* ... */ }
    
    // Methods for image capture
    public void captureImage(OutputFileOptions options, ImageCapture.OnImageSavedCallback callback) { /* ... */ }
}
```

### StorageManager

Handles file storage operations:

```java
public class StorageManager {
    // Methods for file operations
    public Uri saveImage(Bitmap image, String filename) { /* ... */ }
    public boolean deleteImage(Uri imageUri) { /* ... */ }
    public Uri findMostRecentImage() { /* ... */ }
}
```

### MetadataExtractor

Extracts and processes image metadata:

```java
public class MetadataExtractor {
    // Methods for metadata extraction
    public String extractExifMetadata(String imagePath) { /* ... */ }
    public String extractIptcMetadata(String imagePath) { /* ... */ }
    public float convertToDegree(String stringDMS) { /* ... */ }
}
```

### PreferencesManager

Manages user preferences:

```java
public class PreferencesManager {
    private final SharedPreferences preferences;
    
    // Methods for preference management
    public void saveResolutionPreference(PhotoResolution resolution) { /* ... */ }
    public PhotoResolution loadResolutionPreference() { /* ... */ }
    public void saveNamingPreferences(String template, int counter) { /* ... */ }
}
```

## Control Flow

The control flow of Photo Scanner follows these general steps:

1. App starts with `MainActivity`
2. Permission check is performed
3. If permissions are granted, camera UI is displayed
4. User interacts with the camera interface
5. When the capture button is pressed:
   - The image is captured
   - Processing is applied based on settings
   - The file is named and saved
6. The user can then view the image or continue capturing

## Concurrency Model

Photo Scanner uses a combination of UI thread and background thread processing:

- UI interactions are handled on the main thread
- Camera operations use `cameraExecutor` for background processing
- File IO operations are performed on background threads
- Image processing is done on dedicated threads to avoid blocking the UI

## Architectural Considerations

### Performance Optimization

- **Battery Optimization**: Camera is shut down when not in use
- **Memory Management**: Images are processed at the selected resolution
- **Responsive UI**: Heavy operations are offloaded to background threads

### Error Handling

- Comprehensive exception handling for camera operations
- Fallback mechanisms for permission issues
- User feedback for operation failures

### Extensibility

The modular architecture allows for easy addition of new features:

- New processing algorithms can be added to `ImageProcessingUtils`
- Additional metadata extractors can extend `MetadataExtractor`
- New UI features can be added without changing core camera functionality

## Future Architectural Improvements

Planned improvements to the architecture include:

1. **Migration to MVVM**: Separating UI logic from business logic more cleanly
2. **Dependency Injection**: Implementing a DI framework for better testability
3. **Repository Pattern**: Adding a data repository layer for more robust data handling
4. **Work Manager Integration**: Using WorkManager for background tasks
5. **Kotlin Coroutines**: Replacing Java executors with coroutines for background processing

## Related Documentation

- [Project Overview](overview.md): High-level overview of the project
- [Build Instructions](build.md): How to build the application
- [Contributing](contributing.md): Guidelines for contributing to the project

