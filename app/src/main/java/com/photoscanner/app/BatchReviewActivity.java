package com.photoscanner.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for reviewing and managing batch-scanned images.
 */
public class BatchReviewActivity extends AppCompatActivity {
    private static final String TAG = "BatchReviewActivity";
    private static final int REQUEST_ADD_MORE_PHOTOS = 101;
    
    private TextView titleTextView;
    private TextView batchInfoTextView;
    private RecyclerView imagesRecyclerView;
    private MaterialButton addMoreButton;
    private MaterialButton saveAllButton;
    private ProgressBar progressBar;
    
    private BatchImageAdapter imageAdapter;
    private BatchScanManager batchManager;
    
    // Request codes for result handling
    public static final int RESULT_BATCH_SAVED = 1001;
    public static final int RESULT_ADD_MORE = 1002;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_review);
        
        // Initialize the BatchScanManager
        batchManager = BatchScanManager.getInstance();
        
        // Initialize UI components
        initializeUI();
        
        // Set up the RecyclerView
        setupRecyclerView();
        
        // Set up button click listeners
        setupClickListeners();
        
        // Update batch info text
        updateBatchInfo();
    }
    
    private void initializeUI() {
        titleTextView = findViewById(R.id.titleTextView);
        batchInfoTextView = findViewById(R.id.batchInfoTextView);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        addMoreButton = findViewById(R.id.addMoreButton);
        saveAllButton = findViewById(R.id.saveAllButton);
        progressBar = findViewById(R.id.progressBar);
    }
    
    private void setupRecyclerView() {
        // Create the adapter with the current batch of images
        imageAdapter = new BatchImageAdapter(batchManager.getScannedImages());
        
        // Set up the RecyclerView with a grid layout
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        imagesRecyclerView.setAdapter(imageAdapter);
    }
    
    private void setupClickListeners() {
        // Add more button: return to camera screen
        addMoreButton.setOnClickListener(v -> {
            setResult(RESULT_ADD_MORE);
            finish();
        });
        
        // Save all button: save all images in the batch
        saveAllButton.setOnClickListener(v -> {
            if (batchManager.isBatchEmpty()) {
                Toast.makeText(this, "No images to save", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Save Images")
                    .setMessage("Save all " + batchManager.getBatchSize() + " images to gallery?")
                    .setPositiveButton("Save", (dialog, which) -> saveAllImages())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
    
    private void saveAllImages() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(batchManager.getBatchSize());
        progressBar.setProgress(0);
        
        // Disable buttons during saving
        addMoreButton.setEnabled(false);
        saveAllButton.setEnabled(false);
        
        // Save all images
        batchManager.saveAllImages(this, new BatchScanManager.BatchOperationListener() {
            @Override
            public void onOperationComplete(boolean success, List<Uri> savedUris) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);
                
                // Re-enable buttons
                addMoreButton.setEnabled(true);
                saveAllButton.setEnabled(true);
                
                if (success) {
                    // Show success message
                    String message = "Successfully saved " + savedUris.size() + " images";
                    Toast.makeText(BatchReviewActivity.this, message, Toast.LENGTH_SHORT).show();
                    
                    // Clear the batch
                    batchManager.clearBatch();
                    
                    // Return to main activity
                    setResult(RESULT_BATCH_SAVED);
                    finish();
                } else {
                    // Show error message
                    Snackbar.make(imagesRecyclerView, 
                            "Some images could not be saved", Snackbar.LENGTH_LONG).show();
                    
                    // Update the adapter with remaining images
                    imageAdapter.updateImages(batchManager.getScannedImages());
                    updateBatchInfo();
                }
            }
            
            @Override
            public void onOperationProgress(int current, int total) {
                // Update progress bar
                progressBar.setProgress(current);
            }
            
            @Override
            public void onOperationError(String errorMessage) {
                // Show error message
                Log.e(TAG, "Error saving images: " + errorMessage);
                Snackbar.make(imagesRecyclerView, errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }
    
    private void updateBatchInfo() {
        int batchSize = batchManager.getBatchSize();
        batchInfoTextView.setText("Photos: " + batchSize);
        
        // Enable/disable save button based on batch size
        saveAllButton.setEnabled(batchSize > 0);
    }
    
    /**
     * Adapter for displaying batch images in a RecyclerView
     */
    private class BatchImageAdapter extends RecyclerView.Adapter<BatchImageAdapter.ImageViewHolder> {
        private List<Uri> images;
        
        public BatchImageAdapter(List<Uri> images) {
            this.images = new ArrayList<>(images);
        }
        
        public void updateImages(List<Uri> newImages) {
            this.images = new ArrayList<>(newImages);
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_batch_image, parent, false);
            return new ImageViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            Uri imageUri = images.get(position);
            
            // Set image number
            holder.imageNumberTextView.setText("#" + (position + 1));
            
            // Load image into ImageView
            holder.imageView.setImageURI(imageUri);
            
            // Set delete button click listener
            holder.deleteButton.setOnClickListener(v -> {
                // Confirm deletion
                new AlertDialog.Builder(BatchReviewActivity.this)
                        .setTitle("Remove Image")
                        .setMessage("Remove this image from the batch?")
                        .setPositiveButton("Remove", (dialog, which) -> {
                            // Remove image from batch
                            if (batchManager.removeScannedImage(position)) {
                                // Update adapter
                                images.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, images.size() - position);
                                
                                // Update batch info
                                updateBatchInfo();
                                
                                // Show message
                                Toast.makeText(BatchReviewActivity.this, 
                                        "Image removed from batch", Toast.LENGTH_SHORT).show();
                                
                                // If all images are removed, return to main activity
                                if (batchManager.isBatchEmpty()) {
                                    setResult(RESULT_CANCELED);
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }
        
        @Override
        public int getItemCount() {
            return images.size();
        }
        
        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageButton deleteButton;
            TextView imageNumberTextView;
            
            ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.batchImageView);
                deleteButton = itemView.findViewById(R.id.deleteButton);
                imageNumberTextView = itemView.findViewById(R.id.imageNumberTextView);
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        // If batch is not empty, confirm exit
        if (!batchManager.isBatchEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard Batch")
                    .setMessage("Discard all " + batchManager.getBatchSize() + " images?")
                    .setPositiveButton("Discard", (dialog, which) -> {
                        batchManager.clearBatch();
                        setResult(RESULT_CANCELED);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}

