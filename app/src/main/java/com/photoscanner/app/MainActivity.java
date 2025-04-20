package com.photoscanner.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ListenableFuture;
import android.media.ExifInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import android.content.ContentUris;
public class MainActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener {
    
    private static final String TAG = "MainActivity";
    
    // Resolution options
    public enum PhotoResolution {
        HIGH,    // Original camera resolution
        MEDIUM,  // 1280x960 (default)
        LOW      // 640x480
    }
    
    private static final String PREF_NAME = "PhotoScannerPrefs";
    private static final String PREF_RESOLUTION = "resolution";
    private static final String PREF_NAME_TEMPLATE = "name_template";
    private static final String PREF_NAME_COUNTER = "name_counter";
    private PhotoResolution currentResolution = PhotoResolution.MEDIUM; // Default
    private String nameTemplate = "Photo";  // Default
    private int nameCounter = 1;            // Default
    private boolean isCameraOn = false;     // Track camera state

    // Define required permissions
    private static final String[] REQUIRED_PERMISSIONS_MODERN = new String[] {
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final String[] REQUIRED_PERMISSIONS_LEGACY = new String[] {
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    
    /**
     * Set up batch mode controls, including switch functionality and button visibility
     */
    private void setupBatchModeControls() {
        Log.d(TAG, "Setting up batch mode controls");
        batchControlPanel = findViewById(R.id.batchControlPanel);
        batchModeSwitch = findViewById(R.id.batchModeSwitch);
        batchModeSwitch = findViewById(R.id.batchModeSwitch);
        batchCounterTextView = findViewById(R.id.batchCounterTextView);
        reviewBatchButton = findViewById(R.id.reviewBatchButton);
        
        // BatchScanManager references removed for v1.0 compatibility
        // if (batchScanManager == null) {
        //     batchScanManager = BatchScanManager.getInstance();
        // }
    }
    // UI components
    private PreviewView viewFinder;
    private MaterialButton captureButton;
    private MaterialButton viewPhotosButton;
    private View permissionContainer;
    private MaterialButton requestPermissionButton;
    private TextView permissionStatusText;
    private TextView versionTextView;
    private MaterialButton checkPermissionsButton;
    private MaterialButton forceStartButton;
    private MaterialButton bypassButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
    private ImageCapture imageCapture;
    
    // Batch mode UI components
    private View batchControlPanel;
    private SwitchCompat batchModeSwitch;
    private TextView batchCounterTextView;
    private MaterialButton reviewBatchButton;
    // BatchScanManager removed for v1.0 compatibility
    // private BatchScanManager batchScanManager;
    private boolean isBatchMode = false;
    // Store the most recently captured image URI
    private Uri lastCapturedImageUri = null;
    // Activity launcher for permissions
    private final ActivityResultLauncher<String[]> requestPermissionLauncher = 
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                Log.d(TAG, "Permission result received: " + permissions.toString());
                boolean allGranted = true;
                for (String permission : permissions.keySet()) {
                    Boolean isGranted = permissions.get(permission);
                    Log.d(TAG, "Permission: " + permission + " granted: " + isGranted);
                    allGranted = allGranted && (isGranted != null && isGranted);
                }
                
                // For Android 10+, we should also check if we have enough permissions
                // even if not all requested permissions are granted
                boolean enoughPermissions = false;
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    boolean cameraGranted = permissions.getOrDefault(Manifest.permission.CAMERA, false);
                    boolean readStorageGranted = permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);
                    
                    // For Android 10+, we just need camera and read permissions
                    enoughPermissions = cameraGranted && readStorageGranted;
                    Log.d(TAG, "For Android 10+, camera: " + cameraGranted + ", read: " + readStorageGranted);
                }
                
                Log.d(TAG, "All permissions granted: " + allGranted + ", enough permissions: " + enoughPermissions);
                if (allGranted || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && enoughPermissions)) {
                    // Sufficient permissions are granted, start camera
                    Log.d(TAG, "Showing camera UI and starting camera");
                    runOnUiThread(() -> {
                        try {
                            permissionContainer.setVisibility(View.GONE);
                            viewFinder.setVisibility(View.VISIBLE);
                            startCamera();
                        } catch (Exception e) {
                            Log.e(TAG, "Error updating UI after permissions granted", e);
                            Toast.makeText(MainActivity.this, "Error starting camera", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Permissions not granted, show permission UI
                    Log.d(TAG, "Showing permission UI");
                    runOnUiThread(() -> {
                        permissionContainer.setVisibility(View.VISIBLE);
                        viewFinder.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Camera and storage permissions are required", Toast.LENGTH_LONG).show();
                        // Update the permission status text on screen
                        updatePermissionStatusText();
                    });
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        Log.d(TAG, "onCreate: Initializing UI elements");
        // Initialize all UI elements
        viewFinder = findViewById(R.id.viewFinder);
        captureButton = findViewById(R.id.captureButton);
        // Set initial button text
        captureButton.setText(R.string.capture_button_start);
        viewPhotosButton = findViewById(R.id.viewPhotosButton);
        permissionContainer = findViewById(R.id.permissionContainer);
        requestPermissionButton = findViewById(R.id.requestPermissionButton);
        permissionStatusText = findViewById(R.id.permissionStatusText);
        checkPermissionsButton = findViewById(R.id.checkPermissionsButton);
        forceStartButton = findViewById(R.id.forceStartButton);
        bypassButton = findViewById(R.id.bypassButton);
        versionTextView = findViewById(R.id.versionTextView);
        
        // Set version text
        String versionText = "v" + BuildConfig.VERSION_NAME;
        versionTextView.setText(versionText);
        // Set up batch mode controls
        setupBatchModeControls();
        
        // Load saved resolution preference
        // Load saved resolution preference
        loadResolutionPreference();
        
        // Load saved naming preferences
        loadNamingPreferences();
        
        // Set up navigation drawer
        setupNavigationDrawer();
        // Set click listeners
        captureButton.setOnClickListener(v -> {
            if (!isCameraOn) {
                // First press - Start camera
                startCamera();
            } else {
                // Second press - Take photo
                takePhoto();
            }
        });
        viewPhotosButton.setOnClickListener(v -> openGallery());
        requestPermissionButton.setOnClickListener(v -> {
            Log.d(TAG, "Permission button clicked");
            requestPermissions();
        });
        
        // Add click listeners for debug buttons
        checkPermissionsButton.setOnClickListener(v -> {
            Log.d(TAG, "Check permissions button clicked");
            updatePermissionStatusText();
        });
        
        forceStartButton.setOnClickListener(v -> {
            Log.d(TAG, "Force start button clicked");
            forceStartCamera();
        });
        
        // Add a long press listener to force retry permission check
        requestPermissionButton.setOnLongClickListener(v -> {
            Log.d(TAG, "Permission button LONG pressed - forcing camera start attempt");
            Toast.makeText(this, "Forcing camera start attempt", Toast.LENGTH_SHORT).show();
            forceStartCamera();
            return true;
        });
        
        // Add click listener for bypass button
        bypassButton.setOnClickListener(v -> {
            Log.d(TAG, "BYPASS button clicked - completely bypassing permission system");
            Toast.makeText(this, "Bypassing permissions completely", Toast.LENGTH_SHORT).show();
            completelyBypassPermissions();
        });
        
        // Check permissions
        boolean hasPermissions = allPermissionsGranted();
        Log.d(TAG, "onCreate: Permissions check result: " + hasPermissions);
        
        if (hasPermissions) {
            Log.d(TAG, "onCreate: All permissions already granted, starting camera");
            permissionContainer.setVisibility(View.GONE);
            viewFinder.setVisibility(View.VISIBLE);
            // Camera will be started when user presses the capture button
        } else {
            Log.d(TAG, "onCreate: Permissions not granted, showing permission UI");
            requestPermissionButton.setText("Grant Permissions");
            permissionContainer.setVisibility(View.VISIBLE);
            viewFinder.setVisibility(View.GONE);
            updatePermissionStatusText();
            
            // Automatically request permissions when app starts
            requestPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity resumed");
        
        // Check permissions again in case they changed while app was paused
        boolean hasPermissions = allPermissionsGranted();
        Log.d(TAG, "onResume: Permissions check result: " + hasPermissions);
        
        // Update batch counter in case it changed
        try {
            updateBatchCounterUI();
        } catch (Exception e) {
            Log.e(TAG, "Error updating batch counter", e);
        }
        
        // Update UI based on current permission status
        if (hasPermissions) {
            Log.d(TAG, "onResume: Permissions granted, ensuring camera is running");
            permissionContainer.setVisibility(View.GONE);
            viewFinder.setVisibility(View.VISIBLE);
            
            // Only restart camera if it's not already set up
            // Camera will be started when user presses the capture button
            // if (imageCapture == null) {
            //     startCamera();
            // }
        } else {
            Log.d(TAG, "onResume: Permissions not granted, showing permission UI");
            permissionContainer.setVisibility(View.VISIBLE);
            viewFinder.setVisibility(View.GONE);
            updatePermissionStatusText();
        }
    }
    
    // Remove duplicate onResume method
    /* 
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: Activity resumed");
        // Check permissions again in case they changed while app was paused
        boolean hasPermissions = allPermissionsGranted();
        Log.d(TAG, "onResume: Permissions check result: " + hasPermissions);
        
        if (hasPermissions) {
            Log.d(TAG, "onResume: Permissions granted, ensuring camera is running");
            permissionContainer.setVisibility(View.GONE);
            viewFinder.setVisibility(View.VISIBLE);
            // Only restart camera if it's not already set up
            if (imageCapture == null) {
                startCamera();
            }
        } else {
            Log.d(TAG, "onResume: Permissions not granted");
            permissionContainer.setVisibility(View.VISIBLE);
            viewFinder.setVisibility(View.GONE);
            // Manually check individual permissions and log results
            logDetailedPermissionStatus();
            // Update the permission status text on screen
            updatePermissionStatusText();
        }
    }
    */
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Shutting down camera executor");
        cameraExecutor.shutdown();
    }
    
    private void requestPermissions() {
        // Log detailed permission status before requesting
        logDetailedPermissionStatus();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "Using modern permissions for Android 10+");
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS_MODERN);
        } else {
            Log.d(TAG, "Using legacy permissions for Android 9 and below");
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS_LEGACY);
        }
    }
    
    private void logDetailedPermissionStatus() {
        Log.d(TAG, "===== DETAILED PERMISSION STATUS =====");
        Log.d(TAG, "Android SDK Version: " + Build.VERSION.SDK_INT + 
               " (Android " + Build.VERSION.RELEASE + ")");
        
        // Check ALL possible permissions regardless of Android version
        String[] allPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        
        for (String permission : allPermissions) {
            int status = ContextCompat.checkSelfPermission(this, permission);
            boolean granted = status == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, "Permission: " + permission + 
                   " | Status code: " + status + 
                   " | Granted: " + granted);
        }
        Log.d(TAG, "======================================");
    }

    private void updateBatchCounterUI() {
        // Placeholder for missing method
        Log.d(TAG, "updateBatchCounterUI called");
        // No actual implementation since BatchScanManager is excluded for v1.0
    }
    
    private boolean allPermissionsGranted() {
        String[] permissionsToCheck;
        
        Log.d(TAG, "allPermissionsGranted: Android SDK Version: " + Build.VERSION.SDK_INT);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+ we only need CAMERA and READ_EXTERNAL_STORAGE
            permissionsToCheck = REQUIRED_PERMISSIONS_MODERN;
            Log.d(TAG, "Checking modern permissions for Android 10+");
        } else {
            // For older versions we need all three permissions
            permissionsToCheck = REQUIRED_PERMISSIONS_LEGACY;
            Log.d(TAG, "Checking legacy permissions for Android 9 and below");
        }
        
        boolean allGranted = true;
        StringBuilder statusReport = new StringBuilder("Permission status: ");
        
        for (String permission : permissionsToCheck) {
            boolean granted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, "Permission check: " + permission + " granted: " + granted);
            statusReport.append(permission).append("=").append(granted).append(", ");
            allGranted = allGranted && granted;
        }
        
        Log.d(TAG, statusReport.toString());
        Log.d(TAG, "allPermissionsGranted result: " + allGranted);
        return allGranted;
    }
    
    private void forceStartCamera() {
        // This method attempts to start the camera even if permissions check fails
        // It's a fallback for when permissions are actually granted but the app doesn't detect it
        Log.d(TAG, "forceStartCamera: Attempting to force camera start");
        logDetailedPermissionStatus();
        
        try {
            // Force show camera UI
            permissionContainer.setVisibility(View.GONE);
            viewFinder.setVisibility(View.VISIBLE);
            startCamera();
            Toast.makeText(this, "Forced camera start attempted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "forceStartCamera: Error forcing camera to start", e);
            Toast.makeText(this, "Error starting camera: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Don't revert to permission UI - keep trying with camera view visible
            Toast.makeText(this, "Keeping camera view visible despite error", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Updates the permission status text view with current permission status
     */
    private void updatePermissionStatusText() {
        StringBuilder status = new StringBuilder();
        
        status.append("Android: ").append(Build.VERSION.RELEASE)
              .append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n\n");
        
        // Check ALL possible permissions
        String[] allPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        
        for (String permission : allPermissions) {
            boolean granted = ContextCompat.checkSelfPermission(this, permission) 
                    == PackageManager.PERMISSION_GRANTED;
            status.append(permission.substring(permission.lastIndexOf(".") + 1))
                  .append(": ").append(granted ? "GRANTED ✓" : "DENIED ✗").append("\n");
        }
        
        // Add information about which permissions are actually required for this device
        status.append("\nRequired for this device (Android ").append(Build.VERSION.RELEASE).append("):\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            status.append("CAMERA, READ_EXTERNAL_STORAGE");
        } else {
            status.append("CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE");
        }
        
        runOnUiThread(() -> {
            permissionStatusText.setText(status.toString());
        });
    }
    
    private void completelyBypassPermissions() {
        Log.d(TAG, "completelyBypassPermissions: Forcing camera to start regardless of permission status");
        
        // Force the app to move past the permission screen
        runOnUiThread(() -> {
            try {
                // Hide permission container and show camera
                permissionContainer.setVisibility(View.GONE);
                viewFinder.setVisibility(View.VISIBLE);
                
                // Try to start the camera directly
                // Camera will be started when user presses the capture button
                // if (imageCapture == null) {
                //     startCamera();
                // }
                
                Toast.makeText(this, "Permissions bypassed, attempting to start camera", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error during permission bypass: " + e.getMessage(), e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void startCamera() {
        Log.d(TAG, "startCamera: Setting up camera");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
                ProcessCameraProvider.getInstance(this);
        
        cameraProviderFuture.addListener(() -> {
            try {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                Log.d(TAG, "startCamera: Getting camera provider");
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                
                // Preview use case
                Log.d(TAG, "startCamera: Creating preview use case");
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                // Image capture use case
                Log.d(TAG, "startCamera: Creating image capture use case");
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();
                // Select back camera as default
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                
                try {
                    // Unbind any bound use cases before rebinding
                    Log.d(TAG, "startCamera: Unbinding any previously bound use cases");
                    cameraProvider.unbindAll();
                    
                    // Bind use cases to camera
                    Log.d(TAG, "startCamera: Binding use cases to camera lifecycle");
                    cameraProvider.bindToLifecycle(
                            this, cameraSelector, preview, imageCapture);
                    
                    Log.d(TAG, "startCamera: Camera setup complete");
                    isCameraOn = true;
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Camera ready", Toast.LENGTH_SHORT).show();
                        captureButton.setText(R.string.capture_button_take);
                        captureButton.setEnabled(true);
                        viewPhotosButton.setEnabled(true);
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Use case binding failed", e);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Camera setup failed", Toast.LENGTH_SHORT).show());
                }
                
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Failed to get camera provider", e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Camera initialization failed", Toast.LENGTH_SHORT).show());
            }
        }, ContextCompat.getMainExecutor(this));
    }
    
    private void takePhoto() {
        if (imageCapture == null) {
            Log.e(TAG, "takePhoto: Camera not available");
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "takePhoto: Taking a picture");
        // Create time-stamped filename
        // Create filename using the template and counter
        // Create filename using the template and counter
        // Create filename using the template and counter
        String filename = generateFileName(nameTemplate, nameCounter);
        Log.d(TAG, "takePhoto: Using filename='" + filename + "' from template='" + nameTemplate + "', counter=" + nameCounter);
        Log.d(TAG, "DEBUG_NAMING: Creating photo with filename='" + filename + "' from template='" + nameTemplate + "', counter=" + nameCounter);
        
        // Increment counter for next use
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 (API 29) and above - Use MediaStore
            Log.d(TAG, "takePhoto: Using MediaStore for Android 10+");
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            
            // Apply resolution settings if not HIGH (original)
            if (currentResolution != PhotoResolution.HIGH) {
                int targetWidth, targetHeight;
                
                if (currentResolution == PhotoResolution.MEDIUM) {
                    targetWidth = 1280;
                    targetHeight = 960;
                } else { // LOW
                    targetWidth = 640;
                    targetHeight = 480;
                }
                
                Log.d(TAG, "takePhoto: Applying resolution setting: " + currentResolution +
                      " (" + targetWidth + "x" + targetHeight + ")");
            }
            
            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(
                    getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues)
                    .build();
            captureImage(outputOptions);
        } else {
            // Before Android 10 - Use File Storage
            Log.d(TAG, "takePhoto: Using direct file storage for Android 9 and below");
            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "PhotoScanner");
            
            if (!directory.exists() && !directory.mkdirs()) {
                Log.e(TAG, "Failed to create directory: " + directory);
                Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Apply resolution settings if not HIGH (original)
            if (currentResolution != PhotoResolution.HIGH) {
                int targetWidth, targetHeight;
                
                if (currentResolution == PhotoResolution.MEDIUM) {
                    targetWidth = 1280;
                    targetHeight = 960;
                } else { // LOW
                    targetWidth = 640;
                    targetHeight = 480;
                }
                
                Log.d(TAG, "takePhoto: Applying resolution setting: " + currentResolution +
                      " (" + targetWidth + "x" + targetHeight + ")");
            }
            
            File photoFile = new File(directory, filename + ".jpg");
            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
            captureImage(outputOptions);
        }
    }
    
    /**
     * Load the saved resolution preference
     */
    private void loadResolutionPreference() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String resolutionStr = prefs.getString(PREF_RESOLUTION, PhotoResolution.MEDIUM.name());
        try {
            currentResolution = PhotoResolution.valueOf(resolutionStr);
        } catch (IllegalArgumentException e) {
            // In case of invalid preference value, use default
            currentResolution = PhotoResolution.MEDIUM;
        }
        Log.d(TAG, "Loaded resolution preference: " + currentResolution);
    }

    /**
     * Save the selected resolution preference
     */
    private void saveResolutionPreference(PhotoResolution resolution) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_RESOLUTION, resolution.name());
        editor.apply();
        Log.d(TAG, "Saved resolution preference: " + resolution);
    }
    /**
     * Load the saved naming preferences
     */
    private void loadNamingPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        nameTemplate = prefs.getString(PREF_NAME_TEMPLATE, "Photo");
        nameCounter = prefs.getInt(PREF_NAME_COUNTER, 1);
        Log.d(TAG, "Loaded naming preferences: template=" + nameTemplate + ", counter=" + nameCounter);
    }

    /**
     * Save the naming preferences
     */
    private void saveNamingPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_NAME_TEMPLATE, nameTemplate);
        editor.putInt(PREF_NAME_COUNTER, nameCounter);
        editor.apply();
        Log.d(TAG, "Saved naming preferences: template=" + nameTemplate + ", counter=" + nameCounter);
    }

    /**
     * Generate a filename using the template and counter
     */
    private String generateFileName(String template, int counter) {
        // Format counter with leading zeros based on size
        String counterStr;
        if (counter < 10) {
            counterStr = String.format(Locale.US, "00%d", counter);
        } else if (counter < 100) {
            counterStr = String.format(Locale.US, "0%d", counter);
        } else {
            counterStr = String.format(Locale.US, "%d", counter);
        }
        
        return template + "_" + counterStr;
    }

    /**
     * Save the selected resolution preference
     */
    private void showResolutionDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_resolution, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        
        final AlertDialog dialog = builder.create();
        
        // Find UI elements
        RadioGroup radioGroup = dialogView.findViewById(R.id.resolution_radio_group);
        RadioButton radioHigh = dialogView.findViewById(R.id.radio_high);
        RadioButton radioMedium = dialogView.findViewById(R.id.radio_medium);
        RadioButton radioLow = dialogView.findViewById(R.id.radio_low);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        Button buttonOk = dialogView.findViewById(R.id.button_ok);
        
        // Set initial selected option based on current resolution
        switch (currentResolution) {
            case HIGH:
                radioHigh.setChecked(true);
                break;
            case MEDIUM:
                radioMedium.setChecked(true);
                break;
            case LOW:
                radioLow.setChecked(true);
                break;
        }
        
        // Set click listeners for buttons
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        
        buttonOk.setOnClickListener(v -> {
            // Get selected resolution
            PhotoResolution newResolution;
            int selectedId = radioGroup.getCheckedRadioButtonId();
            
            if (selectedId == R.id.radio_high) {
        // Set current values
                newResolution = PhotoResolution.HIGH;
            } else if (selectedId == R.id.radio_medium) {
                newResolution = PhotoResolution.MEDIUM;
            } else {
                newResolution = PhotoResolution.LOW;
            }
            
            // Update resolution if changed
            if (newResolution != currentResolution) {
                currentResolution = newResolution;
                saveResolutionPreference(currentResolution);
                
                // Show confirmation toast
                String resolutionName = "";
                switch (currentResolution) {
                    case HIGH:
                        resolutionName = getString(R.string.resolution_high);
                        break;
                    case MEDIUM:
                        resolutionName = getString(R.string.resolution_medium);
                        break;
                    case LOW:
                        resolutionName = getString(R.string.resolution_low);
                        break;
                }
                
                Toast.makeText(
                    MainActivity.this,
                    "Resolution set to " + resolutionName,
                    Toast.LENGTH_SHORT
                ).show();
            }
            
            dialog.dismiss();
        });
        
        dialog.show();
    }
    /**
     * Show the rename template dialog
     */
    private void showRenameTemplateDialog() {
        Log.d(TAG, "Showing rename template dialog");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rename_template, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        
        final AlertDialog dialog = builder.create();
        
        // Find views
        TextInputEditText templateInput = dialogView.findViewById(R.id.rename_template_input);
        TextInputEditText counterInput = dialogView.findViewById(R.id.rename_counter_input);
        TextView previewText = dialogView.findViewById(R.id.rename_preview_text);
        Button cancelButton = dialogView.findViewById(R.id.rename_cancel_button);
        Button saveButton = dialogView.findViewById(R.id.rename_save_button);
        
        // Set current values
        templateInput.setText(nameTemplate);
        counterInput.setText(String.valueOf(nameCounter));
        
        // Update preview when text changes
        TextWatcher previewUpdater = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String template = templateInput.getText().toString().trim();
                    int counter = Integer.parseInt(counterInput.getText().toString().trim());
                    String preview = generateFileName(template, counter);
                    previewText.setText(preview);
                } catch (NumberFormatException e) {
                    previewText.setText(getString(R.string.rename_invalid_counter));
                }
            }
        };
        
        templateInput.addTextChangedListener(previewUpdater);
        counterInput.addTextChangedListener(previewUpdater);
        
        // Initial preview
        String preview = generateFileName(nameTemplate, nameCounter);
        previewText.setText(preview);
        
        // Set button listeners
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        saveButton.setOnClickListener(v -> {
            try {
                String template = templateInput.getText().toString().trim();
                int counter = Integer.parseInt(counterInput.getText().toString().trim());
                
                if (template.isEmpty()) {
                    Toast.makeText(MainActivity.this, 
                        getString(R.string.rename_empty_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Save new values
                nameTemplate = template;
                nameCounter = counter;
                saveNamingPreferences();
                Toast.makeText(MainActivity.this, 
                    getString(R.string.rename_template_saved), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, 
                    getString(R.string.rename_invalid_counter), Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }

    /**
     * Process image capture and save the image
     * Process image capture and save the image
     */
    private void captureImage(ImageCapture.OutputFileOptions outputOptions) {
        Log.d(TAG, "captureImage: Processing image capture");
        imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = outputFileResults.getSavedUri();
                        String msg = "Photo saved successfully";
                        
                        // Apply resolution settings if not HIGH resolution
                        if (savedUri != null && currentResolution != PhotoResolution.HIGH) {
                            try {
                                // Get target dimensions based on selected resolution
                                int targetWidth, targetHeight;
                                if (currentResolution == PhotoResolution.MEDIUM) {
                                    targetWidth = 1280;
                                    targetHeight = 960;
                                } else { // LOW
                                    targetWidth = 640;
                                    targetHeight = 480;
                                }
                                
                                // Get original filename without extension
                                String originalFilename = getFileNameFromUri(savedUri);
                                if (originalFilename.toLowerCase().endsWith(".jpg")) {
                                    originalFilename = originalFilename.substring(0, originalFilename.length() - 4);
                                }
                                
                                // Resize the image while preserving the filename
                                savedUri = resizeImage(savedUri, targetWidth, targetHeight, originalFilename);
                                msg = "Photo saved and resized to " + targetWidth + "x" + targetHeight;
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to resize image: " + e.getMessage(), e);
                                msg = "Photo saved but resizing failed";
                            }
                        }
                        
                        if (savedUri != null) {
                            // Save the URI for later use
                            lastCapturedImageUri = savedUri;
                            
                            // Increment the name counter and save preferences
                            nameCounter++;
                            saveNamingPreferences();
                            Log.d(TAG, "Incremented name counter to: " + nameCounter);

                            // Stop camera after taking the photo
                            stopCamera();
                        }
                        final String finalMsg = msg;
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, finalMsg, Toast.LENGTH_SHORT).show());
                    }
                    
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to capture photo", Toast.LENGTH_SHORT).show());
                    }
                }
        );
    }
    
    /**
     * Stop and release the camera to save battery
     */
    private void stopCamera() {
        // Get camera provider future
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
                ProcessCameraProvider.getInstance(this);
        
        cameraProviderFuture.addListener(() -> {
            try {
                // Get camera provider
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                
                // Unbind all use cases
                cameraProvider.unbindAll();
                
                // Update state
                imageCapture = null;
                isCameraOn = false;
                
                // Update UI on main thread
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Camera turned off to save battery", Toast.LENGTH_SHORT).show();
                    captureButton.setText(R.string.capture_button_start);
                });
                
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error stopping camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }
    
    private void openGallery() {
        Log.d(TAG, "openGallery: Opening gallery to view photos");
        
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            
            if (lastCapturedImageUri != null) {
                // If we have a recently captured image, open it directly
                Log.d(TAG, "openGallery: Opening most recent image: " + lastCapturedImageUri);
                intent.setDataAndType(lastCapturedImageUri, "image/jpeg");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                // Otherwise, open the general gallery
                Log.d(TAG, "openGallery: No recent image, opening general gallery");
                Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                intent.setDataAndType(imagesUri, "image/*");
            }
            
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open gallery", e);
            Toast.makeText(this, "Cannot open gallery", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Set up the navigation drawer, including the drawer toggle and listener
     */
    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        
        // Set up the hamburger icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        // Set up navigation item click listener
        navigationView.setNavigationItemSelectedListener(this);
    }
    
    /**
     * Handle navigation drawer item clicks
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
        // Handle navigation view item clicks
        int id = item.getItemId();
        
        if (id == R.id.nav_metatags) {
            // Show metadata for the most recent image
            showMetadataDialog();
        } else if (id == R.id.nav_edit) {
            // Placeholder for future implementation
            Toast.makeText(this, "Edit feature coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_rename) {
            // Show rename template dialog
            showRenameTemplateDialog();
        } else if (id == R.id.nav_resolution) {
            showResolutionDialog();
        } else if (id == R.id.nav_statistics) {
            // Launch the Statistics activity
            Intent statisticsIntent = new Intent(this, StatisticsActivity.class);
            startActivity(statisticsIntent);
        } else if (id == R.id.nav_help) {
            showHelpDialog();
        } else if (id == R.id.nav_about) {
            showAboutDialog();
        } else if (id == R.id.nav_exit) {
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    /**
     * Show the About dialog
     */
    private void showAboutDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_about, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        
        final AlertDialog dialog = builder.create();
        
        // Set version info programmatically
        TextView versionText = dialogView.findViewById(R.id.about_version_text);
        if (versionText != null) {
            versionText.setText(getString(R.string.about_version));
        }
        
        // Set click listener for close button
        MaterialButton closeButton = dialogView.findViewById(R.id.about_close_button);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dialog.dismiss());
        }
        
        dialog.show();
    }
    
    /**
     * Show the Help dialog
     */
    private void showHelpDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_help, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        
        final AlertDialog dialog = builder.create();
        
        // Set click listener for close button
        MaterialButton closeButton = dialogView.findViewById(R.id.help_close_button);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dialog.dismiss());
        }
        
        dialog.show();
    }
    
    /**
     * Handle back button press - close drawer if open
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Show a dialog displaying metadata for the most recent image in the gallery
     */
    private void showMetadataDialog() {
        Log.d(TAG, "showMetadataDialog: Loading metadata for the most recent image");
        
        // Create dialog using the metadata layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_metadata, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        
        // Find views in the dialog
        ImageView thumbnailView = dialogView.findViewById(R.id.metadata_image_thumbnail);
        TextView imageNameText = dialogView.findViewById(R.id.metadata_image_name);
        TextView imageDimensionsText = dialogView.findViewById(R.id.metadata_image_dimensions);
        TextView exifContentText = dialogView.findViewById(R.id.metadata_exif_content);
        TextView iptcContentText = dialogView.findViewById(R.id.metadata_iptc_content);
        TextView otherContentText = dialogView.findViewById(R.id.metadata_other_content);
        ProgressBar loadingIndicator = dialogView.findViewById(R.id.metadata_loading_indicator);
        Button closeButton = dialogView.findViewById(R.id.metadata_close_button);
        
        // Set close button listener
        closeButton.setText(getString(R.string.metadata_close_button));
        closeButton.setOnClickListener(v -> dialog.dismiss());
        
        // Show loading state
        loadingIndicator.setVisibility(View.VISIBLE);
        thumbnailView.setVisibility(View.INVISIBLE);
        imageNameText.setText("");
        imageDimensionsText.setText("");
        exifContentText.setText(getString(R.string.metadata_loading));
        iptcContentText.setText(getString(R.string.metadata_loading));
        otherContentText.setText(getString(R.string.metadata_loading));
        
        // Show dialog while loading
        dialog.show();
        
        // Load data in background thread
        new Thread(() -> {
            try {
                // Try to use lastCapturedImageUri first if available
                Uri imageUri = null;
                if (lastCapturedImageUri != null) {
                    // Verify the URI is still valid
                    try {
                        getContentResolver().openInputStream(lastCapturedImageUri).close();
                        imageUri = lastCapturedImageUri;
                        Log.d(TAG, "Using last captured image: " + imageUri);
                    } catch (Exception e) {
                        Log.d(TAG, "Last captured URI invalid, finding most recent image instead");
                        imageUri = null;
                    }
                }
                
                // If no lastCapturedImageUri or it's invalid, find most recent image
                if (imageUri == null) {
                    imageUri = findMostRecentImageUri();
                }
                
                if (imageUri != null) {
                    // Load image metadata and thumbnail
                    String imagePath = getPathFromUri(imageUri);
                    String imageName = getFileNameFromUri(imageUri);
                    long fileSize = getFileSizeFromUri(imageUri);
                    int[] dimensions = getImageDimensions(imageUri);
                    
                    // Format size and dimensions text
                    String formattedSize = formatSize(fileSize);
                    String dimensionsText = dimensions[0] + " × " + dimensions[1] + " pixels, " + formattedSize;
                    
                    // Load thumbnail
                    final Bitmap thumbnail = getThumbnail(imageUri);
                    
                    // Extract metadata
                    String exifMetadata = "";
                    if (imagePath != null) {
                        exifMetadata = extractExifMetadata(imagePath);
                    }
                    
                    // IPTC metadata (placeholder - not fully implemented)
                    String iptcMetadata = "IPTC metadata extraction requires additional libraries.\n\nThis will be implemented in a future update.";
                    
                    // Other metadata (file info, etc.)
                    String otherMetadata = extractOtherMetadata(imageUri);
                    
                    // Final values for UI thread
                    final String finalImageName = imageName;
                    final String finalDimensionsText = dimensionsText;
                    final String finalExifMetadata = exifMetadata;
                    final String finalIptcMetadata = iptcMetadata;
                    final String finalOtherMetadata = otherMetadata;
                    
                    // Update UI on main thread
                    runOnUiThread(() -> {
                        // Hide loading, show thumbnail
                        loadingIndicator.setVisibility(View.GONE);
                        thumbnailView.setVisibility(View.VISIBLE);
                        
                        // Set thumbnail
                        if (thumbnail != null) {
                            thumbnailView.setImageBitmap(thumbnail);
                        } else {
                            // Show placeholder if thumbnail couldn't be loaded
                            thumbnailView.setImageResource(android.R.drawable.ic_menu_gallery);
                        }
                        
                        // Set text values
                        imageNameText.setText(finalImageName);
                        imageDimensionsText.setText(finalDimensionsText);
                        
                        // Show metadata with fallbacks for empty sections
                        if (finalExifMetadata.isEmpty()) {
                            exifContentText.setText(R.string.no_metadata_available);
                        } else {
                            exifContentText.setText(finalExifMetadata);
                        }
                        
                        if (finalIptcMetadata.isEmpty()) {
                            iptcContentText.setText(R.string.no_metadata_available);
                        } else {
                            iptcContentText.setText(finalIptcMetadata);
                        }
                        
                        if (finalOtherMetadata.isEmpty()) {
                            otherContentText.setText(R.string.no_metadata_available);
                        } else {
                            otherContentText.setText(finalOtherMetadata);
                        }
                    });
                } else {
                    // No images found
                    runOnUiThread(() -> {
                        loadingIndicator.setVisibility(View.GONE);
                        thumbnailView.setVisibility(View.VISIBLE);
                        thumbnailView.setImageResource(android.R.drawable.ic_menu_gallery);
                        imageNameText.setText(R.string.metadata_no_image);
                        imageDimensionsText.setText("");
                        exifContentText.setText(R.string.no_metadata_available);
                        iptcContentText.setText(R.string.no_metadata_available);
                        otherContentText.setText(R.string.no_metadata_available);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading metadata: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    loadingIndicator.setVisibility(View.GONE);
                    thumbnailView.setVisibility(View.VISIBLE);
                    thumbnailView.setImageResource(android.R.drawable.ic_menu_gallery);
                    imageNameText.setText("Error loading image");
                    imageDimensionsText.setText("");
                    exifContentText.setText("Error: " + e.getMessage());
                    iptcContentText.setText(R.string.no_metadata_available);
                    otherContentText.setText(R.string.no_metadata_available);
                });
            }
        }).start();
    }
    
    /**
     * Find the URI of the most recent image in the gallery
     */
    private Uri findMostRecentImageUri() {
        String[] projection = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN
        };
        
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";
        
        try (Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder)) {
            
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding most recent image: " + e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Get file path from URI
     */
    private String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file path: " + e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Get file name from URI
     */
    private String getFileNameFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DISPLAY_NAME };
        
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file name: " + e.getMessage(), e);
        }
        
        return "Unknown";
    }
    
    /**
     * Get file size from URI
     */
    private long getFileSizeFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.SIZE };
        
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                return cursor.getLong(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file size: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    /**
     * Get image dimensions from URI
     */
    private int[] getImageDimensions(Uri uri) {
        String[] projection = { 
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        };
        
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                
                // If dimensions not available, try decoding image bounds
                if (width <= 0 || height <= 0) {
                    try (InputStream input = getContentResolver().openInputStream(uri)) {
                        if (input != null) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeStream(input, null, options);
                            width = options.outWidth;
                            height = options.outHeight;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error decoding image bounds: " + e.getMessage(), e);
                    }
                }
                
                return new int[] { width, height };
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting image dimensions: " + e.getMessage(), e);
        }
        
        return new int[] { 0, 0 };
    }

    /**
     * Resize the image at the given URI to the target dimensions
     * @param imageUri Uri of the image to resize
     * @param targetWidth Target width in pixels
     * @param targetHeight Target height in pixels
     * @return Uri of the resized image
     * @throws IOException If an error occurs during resizing
     */
    private Uri resizeImage(Uri imageUri, int targetWidth, int targetHeight, String originalFilename) throws IOException {
        Log.d(TAG, "Resizing image to " + targetWidth + "x" + targetHeight);
        
        // Load the bitmap from the Uri
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        if (inputStream == null) {
            throw new IOException("Could not open input stream from Uri");
        }
        
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        
        // Calculate inSampleSize for memory-efficient loading
        int inSampleSize = 1;
        int halfWidth = options.outWidth / 2;
        int halfHeight = options.outHeight / 2;
        while ((halfWidth / inSampleSize) > targetWidth &&
               (halfHeight / inSampleSize) > targetHeight) {
            inSampleSize *= 2;
        }
        
        // Decode bitmap with calculated inSampleSize
        inputStream = getContentResolver().openInputStream(imageUri);
        if (inputStream == null) {
            throw new IOException("Could not open input stream from Uri");
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        
        if (originalBitmap == null) {
            throw new IOException("Failed to decode bitmap from Uri");
        }
        
        // Calculate scaling to maintain aspect ratio
        float originalWidth = originalBitmap.getWidth();
        float originalHeight = originalBitmap.getHeight();
        float scaleWidth = targetWidth / originalWidth;
        float scaleHeight = targetHeight / originalHeight;
        float scaleFactor = Math.min(scaleWidth, scaleHeight);
        
        // Create matrix for scaling
        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);
        
        // Create scaled bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(
                originalBitmap, 
                0, 0, 
                originalBitmap.getWidth(), 
                originalBitmap.getHeight(), 
                matrix, 
                true);
        
        // Release original bitmap to free memory
        if (originalBitmap != resizedBitmap) {
            originalBitmap.recycle();
        }
        
        // Save resized bitmap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+, use MediaStore
            // For Android 10+, use MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, originalFilename + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri resizedUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (resizedUri == null) {
                throw new IOException("Failed to create MediaStore entry for resized image");
            }
            
            OutputStream outputStream = getContentResolver().openOutputStream(resizedUri);
            if (outputStream == null) {
                throw new IOException("Failed to open output stream for resized image");
            }
            
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
            outputStream.close();
            
            // Delete the original image
            getContentResolver().delete(imageUri, null, null);
            
            return resizedUri;
        } else {
            // For older Android versions, use file storage
            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "PhotoScanner");
            
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Failed to create directory for resized image");
            }
            
            // Create a filename for the resized image
            // Use the original filename for the resized image
            String filename = originalFilename + ".jpg";
            File resizedFile = new File(directory, filename);
            // Save the bitmap to file
            FileOutputStream outputStream = new FileOutputStream(resizedFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
            outputStream.close();
            
            // Delete the original file
            // First get the file path from the Uri
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    File originalFile = new File(filePath);
                    if (originalFile.exists()) {
                        originalFile.delete();
                    }
                }
                cursor.close();
            }
            
            // Return Uri for the resized file
            return Uri.fromFile(resizedFile);
        }
    }

    /**
     * Extract EXIF metadata from an image file
     */
    private String extractExifMetadata(String imagePath) {
        StringBuilder exifInfo = new StringBuilder();
        
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            
            // Camera make and model
            String make = exif.getAttribute(ExifInterface.TAG_MAKE);
            String model = exif.getAttribute(ExifInterface.TAG_MODEL);
            if (make != null || model != null) {
                exifInfo.append(getString(R.string.metadata_label_camera)).append(": ")
                       .append(make != null ? make : "")
                       .append(model != null ? " " + model : "")
                       .append("\n\n");
            }
            
            // Date and time
            String datetime = exif.getAttribute(ExifInterface.TAG_DATETIME);
            if (datetime != null) {
                exifInfo.append(getString(R.string.metadata_label_date)).append(": ")
                       .append(datetime).append("\n\n");
            }
            
            // Exposure time
            String exposure = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            if (exposure != null) {
                float exposureValue = Float.parseFloat(exposure);
                exifInfo.append(getString(R.string.metadata_label_exposure)).append(": ");
                if (exposureValue < 1) {
                    exifInfo.append("1/").append(Math.round(1 / exposureValue));
                } else {
                    exifInfo.append(exposureValue);
                }
                exifInfo.append(" sec\n\n");
            }
            
            // Aperture
            String aperture = exif.getAttribute(ExifInterface.TAG_F_NUMBER);
            if (aperture == null) {
                aperture = exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE);
            }
            if (aperture != null) {
                exifInfo.append(getString(R.string.metadata_label_aperture)).append(": f/")
                       .append(aperture).append("\n\n");
            }
            
            // ISO speed
            String iso = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
            if (iso != null) {
                exifInfo.append(getString(R.string.metadata_label_iso)).append(": ")
                       .append(iso).append("\n\n");
            }
            
            // Focal length
            String focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            if (focalLength != null) {
                exifInfo.append(getString(R.string.metadata_label_focal_length)).append(": ")
                       .append(focalLength).append("mm\n\n");
            }
            
            // Flash
            String flash = exif.getAttribute(ExifInterface.TAG_FLASH);
            if (flash != null) {
                boolean flashFired = (Integer.parseInt(flash) & 0x1) != 0;
                exifInfo.append(getString(R.string.metadata_label_flash)).append(": ")
                       .append(flashFired ? "Used" : "Not used").append("\n\n");
            }
            
            // White balance
            String whiteBalance = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            if (whiteBalance != null) {
                int whiteBalanceValue = Integer.parseInt(whiteBalance);
                exifInfo.append(getString(R.string.metadata_label_white_balance)).append(": ")
                       .append(whiteBalanceValue == 0 ? "Auto" : "Manual").append("\n\n");
            }
            
            // GPS information
            String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

            if (latitude != null && longitude != null && latitudeRef != null && longitudeRef != null) {
                float lat = convertToDegree(latitude);
                float lng = convertToDegree(longitude);
                
                // If southern hemisphere or western hemisphere, negate the value
                if (latitudeRef.equals("S")) {
                    lat = -lat;
                }
                if (longitudeRef.equals("W")) {
                    lng = -lng;
                }
                
                exifInfo.append(getString(R.string.metadata_label_gps)).append(": ")
                       .append(String.format(Locale.US, "%.6f, %.6f", lat, lng))
                       .append("\n");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error extracting EXIF metadata: " + e.getMessage(), e);
            return "Error extracting EXIF metadata: " + e.getMessage();
        }
        
        
        return exifInfo.toString().trim();
    }
    
    /**
     * Convert GPS DMS format to decimal degrees
     */
    private float convertToDegree(String stringDMS) {
        try {
            String[] DMS = stringDMS.split(",", 3);
            String[] stringD = DMS[0].split("/", 2);
            float d0 = Float.parseFloat(stringD[0]);
            float d1 = Float.parseFloat(stringD[1]);
            float degrees = d0 / d1;

            String[] stringM = DMS[1].split("/", 2);
            float m0 = Float.parseFloat(stringM[0]);
            float m1 = Float.parseFloat(stringM[1]);
            float minutes = m0 / m1;

            String[] stringS = DMS[2].split("/", 2);
            float s0 = Float.parseFloat(stringS[0]);
            float s1 = Float.parseFloat(stringS[1]);
            float seconds = s0 / s1;

            return degrees + (minutes / 60) + (seconds / 3600);
        } catch (Exception e) {
            // In case of error, return 0
            return 0;
        }
    }
    /**
     * Extract additional metadata from an image
     */
    private String extractOtherMetadata(Uri imageUri) {
        StringBuilder info = new StringBuilder();
        
        try {
            // File type
            String mimeType = getContentResolver().getType(imageUri);
            if (mimeType != null) {
                info.append(getString(R.string.metadata_label_file_type)).append(": ")
                    .append(mimeType).append("\n\n");
            }
            
            // File size already displayed in dimensions text
            
            // Color space - extract from EXIF if available
            String path = getPathFromUri(imageUri);
            if (path != null) {
                try {
                    ExifInterface exif = new ExifInterface(path);
                    String colorSpace = exif.getAttribute(ExifInterface.TAG_COLOR_SPACE);
                    if (colorSpace != null) {
                        int colorSpaceValue = Integer.parseInt(colorSpace);
                        info.append(getString(R.string.metadata_label_color_space)).append(": ")
                            .append(colorSpaceValue == 1 ? "sRGB" : "Uncalibrated").append("\n\n");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error getting color space: " + e.getMessage());
                }
            }
            
            // Get creation date
            String[] projection = { MediaStore.Images.Media.DATE_ADDED };
            try (Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    long dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    String dateString = sdf.format(new Date(dateAdded * 1000)); // Convert from seconds to milliseconds
                    info.append("Date added: ").append(dateString);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting date added: " + e.getMessage());
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error extracting other metadata: " + e.getMessage(), e);
            return "Error extracting additional metadata: " + e.getMessage();
        }
        
        return info.toString().trim();
    }
    
    /**
     * Load a thumbnail of an image from a Uri
     */
    private Bitmap getThumbnail(Uri imageUri) {
        try {
            // First try to use the MediaStore thumbnail API
            Bitmap thumbnail = null;
            
            // Get the image ID
            String[] projection = { MediaStore.Images.Media._ID };
            try (Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                            getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                }
            }
            
            // If MediaStore thumbnail failed, create our own thumbnail
            if (thumbnail == null) {
                // Load a scaled-down version of the image
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8; // Load at 1/8 the original size
                
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    thumbnail = BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                }
            }
            
            return thumbnail;
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading thumbnail: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Format file size in bytes to KB, MB, etc.
     */
    private String formatSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            return (sizeInBytes / 1024) + " KB";
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            return String.format(Locale.US, "%.2f MB", sizeInBytes / (1024.0 * 1024.0));
        } else {
            return String.format(Locale.US, "%.2f GB", sizeInBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
