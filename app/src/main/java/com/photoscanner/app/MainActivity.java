package com.photoscanner.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PhotoScannerApp";
    
    // Permission request constants for Android 9 and lower
    private static final String[] REQUIRED_PERMISSIONS_LEGACY = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    
    
    // Permission request constants for Android 10+
    private static final String[] REQUIRED_PERMISSIONS_MODERN = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    
    // UI components
    private PreviewView viewFinder;
    private MaterialButton captureButton;
    private MaterialButton viewPhotosButton;
    private View permissionContainer;
    private MaterialButton requestPermissionButton;
    private TextView permissionStatusText;
    private MaterialButton checkPermissionsButton;
    private MaterialButton forceStartButton;
    private MaterialButton bypassButton;
    private ImageCapture imageCapture;
    private final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
    
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
        Log.d(TAG, "onCreate: Starting Photo Scanner app");
        setContentView(R.layout.activity_main);
        
        try {
            // Initialize UI components
            Log.d(TAG, "onCreate: Initializing UI components");
            viewFinder = findViewById(R.id.viewFinder);
            captureButton = findViewById(R.id.captureButton);
            viewPhotosButton = findViewById(R.id.viewPhotosButton);
            permissionContainer = findViewById(R.id.permissionContainer);
            requestPermissionButton = findViewById(R.id.requestPermissionButton);
            permissionStatusText = findViewById(R.id.permissionStatusText);
            checkPermissionsButton = findViewById(R.id.checkPermissionsButton);
            forceStartButton = findViewById(R.id.forceStartButton);
            bypassButton = findViewById(R.id.bypassButton);
            
            // Force set initial visibility
            viewFinder.setVisibility(View.VISIBLE);
            permissionContainer.setVisibility(View.GONE);
            // Set click listeners
            Log.d(TAG, "onCreate: Setting up click listeners");
            captureButton.setOnClickListener(v -> takePhoto());
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
            Log.d(TAG, "onCreate: Checking permissions");
            boolean hasPermissions = allPermissionsGranted();
            Log.d(TAG, "onCreate: Permissions check result: " + hasPermissions);
            
            if (hasPermissions) {
                Log.d(TAG, "onCreate: All permissions already granted, starting camera");
                permissionContainer.setVisibility(View.GONE);
                viewFinder.setVisibility(View.VISIBLE);
                startCamera();
            } else {
                Log.d(TAG, "onCreate: Permissions not granted, showing permission UI");
                // Update the request button text to make it clearer
                requestPermissionButton.setText("Grant Permissions");
                permissionContainer.setVisibility(View.VISIBLE);
                viewFinder.setVisibility(View.GONE);
                // Update permission status text
                updatePermissionStatusText();
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error initializing app", e);
            Toast.makeText(this, "Error initializing app", Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
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
                if (imageCapture == null) {
                    startCamera();
                }
                
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
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
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
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Camera ready", Toast.LENGTH_SHORT).show();
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
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String filename = "PhotoScanner_" + timestamp;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 (API 29) and above - Use MediaStore
            Log.d(TAG, "takePhoto: Using MediaStore for Android 10+");
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            
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
            
            File photoFile = new File(directory, filename + ".jpg");
            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
            
            captureImage(outputOptions);
        }
    }
    
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
                        if (savedUri != null) {
                            msg = "Photo saved: " + savedUri.toString();
                            // Save the URI for later use
                            lastCapturedImageUri = savedUri;
                        }
                        Log.d(TAG, msg);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Photo saved successfully", Toast.LENGTH_SHORT).show());
                    }
                    
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to capture photo", Toast.LENGTH_SHORT).show());
                    }
                }
        );
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
}
