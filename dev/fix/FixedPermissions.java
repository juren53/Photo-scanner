// This is the correct definition for the permissions arrays:

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
