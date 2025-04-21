package com.photoscanner.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EdgeDetectionActivity extends AppCompatActivity {
    private static final String TAG = "EdgeDetectionActivity";
    private static final String EXTRA_IMAGE_URI = "image_uri";
    
    private ImageView previewImageView;
    private EdgeDetectionView edgeDetectionView;
    private TextView statusTextView;
    private MaterialButton retryButton;
    private MaterialButton cropButton;
    private TextView versionTextView;
    
    private Uri imageUri;
    private Bitmap originalBitmap;
    private Point[] detectedCorners;
    
    private ExecutorService executorService;
    private Handler mainHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edge_detection);
        
        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV initialization failed");
            Toast.makeText(this, "OpenCV initialization failed", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // Initialize UI components
        previewImageView = findViewById(R.id.previewImageView);
        edgeDetectionView = findViewById(R.id.edgeDetectionView);
        statusTextView = findViewById(R.id.statusTextView);
        retryButton = findViewById(R.id.retryButton);
        cropButton = findViewById(R.id.cropButton);
        versionTextView = findViewById(R.id.versionTextView);
        
        // Set version text
        String versionText = "v" + BuildConfig.VERSION_NAME;
        versionTextView.setText(versionText);
        
        // Initialize executor service for background tasks
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Get image URI from intent
        imageUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
        if (imageUri == null) {
            Log.e(TAG, "No image URI provided");
            Toast.makeText(this, "No image provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Load the image
        loadImage();
        
        // Set up button listeners
        retryButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        
        cropButton.setOnClickListener(v -> cropImage());
    }
    
    private void loadImage() {
        showStatus("Loading image...");
        
        executorService.execute(() -> {
            try {
                // Load the bitmap from URI
                originalBitmap = getBitmapFromUri(imageUri);
                
                if (originalBitmap == null) {
                    mainHandler.post(() -> {
                        Toast.makeText(EdgeDetectionActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }
                
                // Display the bitmap
                mainHandler.post(() -> {
                    previewImageView.setImageBitmap(originalBitmap);
                    showStatus("Detecting edges...");
                });
                
                // Detect edges
                detectedCorners = EdgeDetectionUtils.detectDocumentEdges(originalBitmap);
                
                mainHandler.post(() -> {
                    if (detectedCorners != null) {
                        edgeDetectionView.setCorners(detectedCorners);
                        hideStatus();
                        Toast.makeText(EdgeDetectionActivity.this, "Adjust corners if needed", Toast.LENGTH_SHORT).show();
                    } else {
                        // No edges detected
                        hideStatus();
                        Toast.makeText(EdgeDetectionActivity.this, "No edges detected. Please try again or adjust manually.", Toast.LENGTH_LONG).show();
                        
                        // Create default corners at the image edges
                        int width = originalBitmap.getWidth();
                        int height = originalBitmap.getHeight();
                        Point[] defaultCorners = new Point[4];
                        defaultCorners[0] = new Point(0, 0); // Top-left
                        defaultCorners[1] = new Point(width, 0); // Top-right
                        defaultCorners[2] = new Point(width, height); // Bottom-right
                        defaultCorners[3] = new Point(0, height); // Bottom-left
                        edgeDetectionView.setCorners(defaultCorners);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
                mainHandler.post(() -> {
                    Toast.makeText(EdgeDetectionActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    hideStatus();
                });
            }
        });
    }
    
    private void cropImage() {
        if (!edgeDetectionView.areEdgesDetected()) {
            Toast.makeText(this, "No edges detected to crop", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showStatus("Processing image...");
        
        executorService.execute(() -> {
            try {
                // Get the adjusted corners from the view
                Point[] corners = edgeDetectionView.getCorners();
                
                // Apply perspective transform
                Bitmap croppedBitmap = EdgeDetectionUtils.perspectiveTransform(originalBitmap, corners);
                
                // Save the cropped image to a temporary file
                Uri croppedUri = saveBitmapToTemp(croppedBitmap);
                
                mainHandler.post(() -> {
                    hideStatus();
                    if (croppedUri != null) {
                        // Return the cropped image URI to MainActivity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("cropped_image_uri", croppedUri);
                        setResult(RESULT_OK, resultIntent);
                        
                        Toast.makeText(this, "Image cropped successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        setResult(RESULT_CANCELED);
                        Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error cropping image", e);
                mainHandler.post(() -> {
                    hideStatus();
                    Toast.makeText(EdgeDetectionActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        InputStream input = getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(input);
    }
    
    private Uri saveBitmapToTemp(Bitmap bitmap) {
        try {
            File outputDir = getCacheDir();
            File outputFile = File.createTempFile("cropped_", ".jpg", outputDir);
            
            FileOutputStream out = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            
            return Uri.fromFile(outputFile);
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap", e);
            return null;
        }
    }
    
    private void showStatus(String message) {
        statusTextView.setText(message);
        statusTextView.setVisibility(View.VISIBLE);
    }
    
    private void hideStatus() {
        statusTextView.setVisibility(View.GONE);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
    
    public static Intent createIntent(android.content.Context context, Uri imageUri) {
        Intent intent = new Intent(context, EdgeDetectionActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, imageUri);
        return intent;
    }
}

