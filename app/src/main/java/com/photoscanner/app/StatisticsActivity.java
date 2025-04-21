package com.photoscanner.app;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";
    private static final int REQUEST_READ_STORAGE = 1001;

    private RecyclerView recyclerView;
    private TextView noImagesText;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private List<ImageStats> imageStatsList;
    private ImageStatsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Initialize views
        recyclerView = findViewById(R.id.recycler_statistics);
        noImagesText = findViewById(R.id.text_no_images);
        toolbar = findViewById(R.id.toolbar_statistics);
        progressBar = findViewById(R.id.progress_bar_loading);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Image Statistics");

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageStatsList = new ArrayList<>();
        adapter = new ImageStatsAdapter(imageStatsList);
        recyclerView.setAdapter(adapter);

        // Check for permissions and load images
        if (hasReadStoragePermission()) {
            loadImagesFromGallery();
        } else {
            requestReadStoragePermission();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasReadStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_STORAGE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImagesFromGallery();
            } else {
                Toast.makeText(this, "Storage permission is required to view image statistics", Toast.LENGTH_LONG).show();
                noImagesText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    private void loadImagesFromGallery() {
        new LoadImagesTask().execute();
    }

    /**
     * AsyncTask to load images from the gallery in the background
     */
    private class LoadImagesTask extends AsyncTask<Void, Void, List<ImageStats>> {
        @Override
        protected void onPreExecute() {
            noImagesText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<ImageStats> doInBackground(Void... voids) {
            List<ImageStats> result = new ArrayList<>();

            // Query for images using MediaStore
            String[] projection = {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.DATA
            };

            String selection = MediaStore.Images.Media.MIME_TYPE + "=? OR " +
                    MediaStore.Images.Media.MIME_TYPE + "=?";
            String[] selectionArgs = {"image/jpeg", "image/png"};
            String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

            try (Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder)) {

                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                    int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                    int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        String name = cursor.getString(nameColumn);
                        long size = cursor.getLong(sizeColumn);
                        long dateTaken = cursor.getLong(dateColumn);
                        String data = cursor.getString(dataColumn);

                        Uri contentUri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                        // Create ImageStats object
                        ImageStats stats = new ImageStats();
                        stats.id = id;
                        stats.name = name;
                        stats.uri = contentUri;
                        stats.size = formatSize(size);
                        stats.date = formatDate(dateTaken);

                        // Extract resolution and metadata
                        try {
                            extractImageDetails(stats, data);
                        } catch (IOException e) {
                            Log.e(TAG, "Error extracting image details: " + e.getMessage());
                        }

                        result.add(stats);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading images: " + e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<ImageStats> result) {
            // Hide progress bar
            progressBar.setVisibility(View.GONE);
            
            imageStatsList.clear();
            imageStatsList.addAll(result);
            adapter.notifyDataSetChanged();

            if (imageStatsList.isEmpty()) {
                noImagesText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Extract image details such as resolution and metadata
     */
    private void extractImageDetails(ImageStats stats, String imagePath) throws IOException {
        // Get resolution using BitmapFactory options
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // Only decode bounds, not the entire image
        BitmapFactory.decodeFile(imagePath, options);
        stats.resolution = options.outWidth + " x " + options.outHeight;

        // Extract EXIF metadata
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            StringBuilder exifInfo = new StringBuilder();

            // Add camera model
            String make = exif.getAttribute(ExifInterface.TAG_MAKE);
            String model = exif.getAttribute(ExifInterface.TAG_MODEL);
            if (make != null || model != null) {
                exifInfo.append("Camera: ").append(make != null ? make : "")
                        .append(model != null ? " " + model : "").append("\n");
            }

            // Add camera settings
            String aperture = exif.getAttribute(ExifInterface.TAG_APERTURE_VALUE);
            if (aperture != null) {
                exifInfo.append("Aperture: f/").append(aperture).append("\n");
            }

            String exposure = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            if (exposure != null) {
                float exposureValue = Float.parseFloat(exposure);
                if (exposureValue < 1) {
                    exifInfo.append("Exposure: 1/").append(Math.round(1 / exposureValue)).append("\n");
                } else {
                    exifInfo.append("Exposure: ").append(exposureValue).append("\n");
                }
            }

            String iso = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
            if (iso != null) {
                exifInfo.append("ISO: ").append(iso).append("\n");
            }

            // Flash information
            String flash = exif.getAttribute(ExifInterface.TAG_FLASH);
            if (flash != null) {
                boolean flashFired = (Integer.parseInt(flash) & 0x1) != 0;
                exifInfo.append("Flash: ").append(flashFired ? "Used" : "Not used").append("\n");
            }

            // Focal length
            String focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            if (focalLength != null) {
                exifInfo.append("Focal Length: ").append(focalLength).append("mm\n");
            }

            stats.exifMetadata = exifInfo.toString();

            // For IPTC metadata, we would need a specialized library
            // This is a simplified implementation
            stats.iptcMetadata = "No IPTC metadata available\n\nImplement a specialized IPTC metadata reader for full support";

        } catch (IOException e) {
            stats.exifMetadata = "Error reading EXIF metadata";
            stats.iptcMetadata = "Error reading IPTC metadata";
            throw e;
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
        } else {
            return String.format(Locale.getDefault(), "%.2f MB", sizeInBytes / (1024.0 * 1024.0));
        }
    }

    /**
     * Format date from timestamp
     */
    private String formatDate(long timestamp) {
        if (timestamp == 0) {
            return "Unknown date";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Model class to hold image statistics
     */
    private static class ImageStats {
        long id;
        String name;
        Uri uri;
        String size;
        String resolution;
        String date;
        String exifMetadata;
        String iptcMetadata;
    }

    /**
     * Adapter for displaying image statistics in the RecyclerView
     */
    private class ImageStatsAdapter extends RecyclerView.Adapter<ImageStatsAdapter.ViewHolder> {

        private final List<ImageStats> items;

        public ImageStatsAdapter(List<ImageStats> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_statistics, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ImageStats stats = items.get(position);
            holder.bind(stats);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /**
         * ViewHolder for image statistics items
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView thumbnailView;
            final TextView nameText;
            final TextView sizeText;
            final TextView resolutionText;
            final TextView dateText;
            final TextView exifMetadataText;
            final TextView iptcMetadataText;
            final ImageButton expandCollapseButton;
            final LinearLayout metadataLayout;
            boolean expanded = false;

            ViewHolder(View view) {
                super(view);
                thumbnailView = view.findViewById(R.id.image_thumbnail);
                nameText = view.findViewById(R.id.text_image_name);
                sizeText = view.findViewById(R.id.text_image_size);
                resolutionText = view.findViewById(R.id.text_image_resolution);
                dateText = view.findViewById(R.id.text_image_date);
                exifMetadataText = view.findViewById(R.id.text_exif_metadata);
                iptcMetadataText = view.findViewById(R.id.text_iptc_metadata);
                expandCollapseButton = view.findViewById(R.id.button_expand_collapse);
                metadataLayout = view.findViewById(R.id.layout_metadata_details);

                // Set up expand/collapse click listener
                expandCollapseButton.setOnClickListener(v -> {
                    expanded = !expanded;
                    metadataLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
                    expandCollapseButton.setImageResource(
                            expanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);
                });
            }

            void bind(ImageStats stats) {
                nameText.setText(stats.name);
                sizeText.setText("Size: " + stats.size);
                resolutionText.setText("Resolution: " + stats.resolution);
                resolutionText.setText("Resolution: " + stats.resolution);
                dateText.setText("Date: " + stats.date);
                exifMetadataText.setText(stats.exifMetadata);
                iptcMetadataText.setText(stats.iptcMetadata);
                
                // Load thumbnail image
                loadThumbnail(stats.uri, thumbnailView);
            }
        }
    }
    
    /**
     * Load a thumbnail image from a Uri into an ImageView
     */
    private void loadThumbnail(Uri uri, ImageView imageView) {
        try {
            // Use AsyncTask to load the image in the background
            new AsyncTask<Uri, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Uri... uris) {
                    try {
                        // Load a thumbnail of the image
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4; // Scale down to 1/4 size
                        return MediaStore.Images.Media.getBitmap(getContentResolver(), uris[0]);
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading thumbnail: " + e.getMessage());
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        // Set a placeholder image if loading fails
                        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                }
            }.execute(uri);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up thumbnail loading: " + e.getMessage());
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}
