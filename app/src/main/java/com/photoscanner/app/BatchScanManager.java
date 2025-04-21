package com.photoscanner.app;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages batch scanning of multiple photos in a single session.
 * 
 * This class provides functionality to:
 * 1. Store multiple scanned images in memory
 * 2. Add new images to the current batch
 * 3. Remove images from the batch
 * 4. Save all images at once
 * 5. Provide access to the images for review
 */
public class BatchScanManager {
    private static final String TAG = "BatchScanManager";
    
    // Singleton instance
    private static BatchScanManager instance;
    
    // List to store scanned image URIs
    private List<Uri> scannedImages;
    
    // Executor for background operations
    private final ExecutorService executor;
    
    // Listener for batch operations
    public interface BatchOperationListener {
        void onOperationComplete(boolean success, List<Uri> savedUris);
        void onOperationProgress(int current, int total);
        void onOperationError(String errorMessage);
    }
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private BatchScanManager() {
        scannedImages = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * Get the singleton instance of the BatchScanManager
     */
    public static synchronized BatchScanManager getInstance() {
        if (instance == null) {
            instance = new BatchScanManager();
        }
        return instance;
    }
    
    /**
     * Add a new scanned image to the batch
     * 
     * @param imageUri URI of the scanned image
     * @return Position of the newly added image in the batch
     */
    public int addScannedImage(Uri imageUri) {
        if (imageUri != null) {
            scannedImages.add(imageUri);
            Log.d(TAG, "Added image to batch: " + imageUri);
            return scannedImages.size() - 1;
        }
        return -1;
    }
    
    /**
     * Remove a scanned image from the batch
     * 
     * @param position Position of the image to remove
     * @return true if removal was successful, false otherwise
     */
    public boolean removeScannedImage(int position) {
        if (position >= 0 && position < scannedImages.size()) {
            Uri removed = scannedImages.remove(position);
            Log.d(TAG, "Removed image from batch: " + removed);
            return true;
        }
        return false;
    }
    
    /**
     * Get all scanned images in the batch
     * 
     * @return List of image URIs
     */
    public List<Uri> getScannedImages() {
        return new ArrayList<>(scannedImages);
    }
    
    /**
     * Get the number of images in the batch
     * 
     * @return Current batch size
     */
    public int getBatchSize() {
        return scannedImages.size();
    }
    
    /**
     * Clear all images from the batch
     */
    public void clearBatch() {
        scannedImages.clear();
        Log.d(TAG, "Batch cleared");
    }
    
    /**
     * Check if the batch is empty
     * 
     * @return true if the batch is empty, false otherwise
     */
    public boolean isBatchEmpty() {
        return scannedImages.isEmpty();
    }
    
    /**
     * Save all images in the batch to the device gallery
     * 
     * @param context Application context
     * @param listener Listener to be notified of operation progress and completion
     */
    public void saveAllImages(Context context, BatchOperationListener listener) {
        if (scannedImages.isEmpty()) {
            if (listener != null) {
                listener.onOperationError("No images to save");
            }
            return;
        }
        
        final int totalImages = scannedImages.size();
        final List<Uri> savedUris = new ArrayList<>();
        
        executor.execute(() -> {
            boolean allSaved = true;
            
            for (int i = 0; i < totalImages; i++) {
                try {
                    Uri sourceUri = scannedImages.get(i);
                    Uri savedUri = saveImageToGallery(context, sourceUri);
                    
                    if (savedUri != null) {
                        savedUris.add(savedUri);
                    } else {
                        allSaved = false;
                    }
                    
                    // Report progress
                    if (listener != null) {
                        final int currentProgress = i + 1;
                        context.getMainExecutor().execute(() -> 
                            listener.onOperationProgress(currentProgress, totalImages));
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error saving image " + (i + 1), e);
                    allSaved = false;
                    
                    if (listener != null) {
                        final String errorMsg = "Error saving image " + (i + 1) + ": " + e.getMessage();
                        context.getMainExecutor().execute(() -> listener.onOperationError(errorMsg));
                    }
                }
            }
            
            // Report completion
            if (listener != null) {
                final boolean success = allSaved;
                context.getMainExecutor().execute(() -> listener.onOperationComplete(success, savedUris));
            }
        });
    }
    
    /**
     * Save a single image to the gallery
     * 
     * @param context Application context
     * @param sourceUri Source image URI
     * @return URI of the saved image, or null if saving failed
     */
    private Uri saveImageToGallery(Context context, Uri sourceUri) {
        try {
            // Create a unique filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String filename = "PhotoScanner_" + timestamp;
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ (API 29+): Use MediaStore
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                
                Uri imageUri = context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                
                if (imageUri != null) {
                    // Copy the content from source URI to destination URI
                    try (OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri)) {
                        // Convert the source URI to a bitmap and compress to the output stream
                        Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(
                                context.getContentResolver(), sourceUri);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        return imageUri;
                    }
                }
            } else {
                // Pre-Android 10: Use file system
                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File photoScannerDir = new File(picturesDir, "PhotoScanner");
                
                if (!photoScannerDir.exists() && !photoScannerDir.mkdirs()) {
                    Log.e(TAG, "Failed to create directory: " + photoScannerDir);
                    return null;
                }
                
                File outputFile = new File(photoScannerDir, filename + ".jpg");
                
                // Copy the content from source URI to destination file
                try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                    // Convert the source URI to a bitmap and compress to the output stream
                    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(
                            context.getContentResolver(), sourceUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    
                    // Add the file to the media store so it appears in the gallery
                    return Uri.fromFile(outputFile);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving image to gallery", e);
        }
        
        return null;
    }
    
    /**
     * Get a specific image from the batch
     * 
     * @param position Position of the image in the batch
     * @return URI of the image, or null if position is invalid
     */
    public Uri getImageAt(int position) {
        if (position >= 0 && position < scannedImages.size()) {
            return scannedImages.get(position);
        }
        return null;
    }
    
    /**
     * Replace an image in the batch at a specific position
     * 
     * @param position Position to replace
     * @param newImageUri New image URI
     * @return true if replacement was successful, false otherwise
     */
    public boolean replaceImage(int position, Uri newImageUri) {
        if (position >= 0 && position < scannedImages.size() && newImageUri != null) {
            scannedImages.set(position, newImageUri);
            Log.d(TAG, "Replaced image at position " + position);
            return true;
        }
        return false;
    }
    
    /**
     * Clean up resources when no longer needed
     */
    public void shutdown() {
        executor.shutdown();
    }
}

