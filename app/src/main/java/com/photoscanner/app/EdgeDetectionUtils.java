package com.photoscanner.app;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for image processing, edge detection, and perspective transformation.
 * 
 * This class provides methods to:
 * 1. Detect document edges in photographs using multiple techniques
 * 2. Apply perspective transformation to correct document skew
 * 3. Handle various lighting conditions with automatic adjustment
 * 4. Process images with enhanced algorithms for better results
 */
public class EdgeDetectionUtils {
    private static final String TAG = "EdgeDetectionUtils";
    
    // Detection algorithm types
    public enum DetectionMethod {
        CONTOUR_BASED,    // Uses contour detection (primary method)
        HOUGH_LINES,      // Uses Hough line detection (fallback method)
        COMBINED          // Uses multiple methods and selects best result
    }
    
    // Document edge detection parameters
    private static final double MIN_DOCUMENT_AREA_RATIO = 0.15; // Minimum document size relative to image
    private static final double MAX_DOCUMENT_AREA_RATIO = 0.95; // Maximum document size relative to image
    private static final double APPROX_POLY_DP_FACTOR = 0.02;   // Approximation factor for polygonal curves

    /**
     * Detects the edges of a document in an image.
     * 
     * This method provides a simplified interface to document edge detection with default parameters.
     * It uses the COMBINED detection method which tries multiple approaches and selects the best result.
     * 
     * @param sourceBitmap The input image
     * @return An array of 4 points representing the corners of the detected document,
     *         or null if no document was detected
     */
    public static Point[] detectDocumentEdges(Bitmap sourceBitmap) {
        return detectDocumentEdges(sourceBitmap, DetectionMethod.COMBINED);
    }
    
    /**
     * Detects the edges of a document in an image using a specified detection method.
     * 
     * @param sourceBitmap The input image
     * @param method The detection method to use (CONTOUR_BASED, HOUGH_LINES, or COMBINED)
     * @return An array of 4 points representing the corners of the detected document,
     *         or null if no document was detected
     */
    public static Point[] detectDocumentEdges(Bitmap sourceBitmap, DetectionMethod method) {
        try {
            Log.d(TAG, "Detecting document edges using method: " + method);
            
            // Convert bitmap to OpenCV Mat
            Mat sourceMat = new Mat();
            Utils.bitmapToMat(sourceBitmap, sourceMat);
            
            // Get image dimensions
            int imageWidth = sourceMat.cols();
            int imageHeight = sourceMat.rows();
            double imageArea = imageWidth * imageHeight;
            Log.d(TAG, "Image dimensions: " + imageWidth + "x" + imageHeight);
            
            // Apply preprocessing to enhance edges
            Mat processedMat = preprocessImage(sourceMat);
            
            Point[] documentCorners = null;
            
            // Use specified detection method
            switch (method) {
                case CONTOUR_BASED:
                    documentCorners = detectDocumentByContours(processedMat, imageArea);
                    break;
                case HOUGH_LINES:
                    documentCorners = detectDocumentByHoughLines(processedMat);
                    break;
                case COMBINED:
                    // Try contour detection first
                    documentCorners = detectDocumentByContours(processedMat, imageArea);
                    
                    // If contour detection fails, try Hough lines
                    if (documentCorners == null) {
                        Log.d(TAG, "Contour detection failed, trying Hough lines");
                        documentCorners = detectDocumentByHoughLines(processedMat);
                    }
                    
                    // If both fail, try with different preprocessing
                    if (documentCorners == null) {
                        Log.d(TAG, "Both methods failed, trying with enhanced preprocessing");
                        Mat enhancedMat = enhanceImageForDifficultLighting(sourceMat);
                        documentCorners = detectDocumentByContours(enhancedMat, imageArea);
                        enhancedMat.release();
                    }
                    break;
            }
            
            // If we found document corners, order them properly
            if (documentCorners != null) {
                documentCorners = orderPoints(documentCorners);
                Log.d(TAG, "Document corners detected");
            } else {
                Log.d(TAG, "No document detected");
            }
            
            // Clean up
            sourceMat.release();
            processedMat.release();
            
            return documentCorners;
        } catch (Exception e) {
            Log.e(TAG, "Error detecting document edges: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Preprocesses an image to enhance edge detection.
     * 
     * This method applies several preprocessing techniques to make document edges more detectable:
     * 1. Converts to grayscale
     * 2. Applies gaussian blur to reduce noise
     * 3. Performs adaptive histogram equalization to improve contrast
     * 4. Uses bilateral filtering to preserve edges while reducing noise
     * 
     * @param sourceMat The source image in OpenCV Mat format
     * @return A preprocessed image ready for edge detection
     */
    private static Mat preprocessImage(Mat sourceMat) {
        // Create output matrix
        Mat processedMat = new Mat();
        
        // Convert to grayscale if not already
        if (sourceMat.channels() > 1) {
            Imgproc.cvtColor(sourceMat, processedMat, Imgproc.COLOR_BGR2GRAY);
        } else {
            sourceMat.copyTo(processedMat);
        }
        
        // Apply Gaussian blur to reduce noise (5x5 kernel)
        Imgproc.GaussianBlur(processedMat, processedMat, new Size(5, 5), 0);
        
        // Apply CLAHE (Contrast Limited Adaptive Histogram Equalization) to improve contrast
        Mat equalized = new Mat();
        Imgproc.createCLAHE(2.0, new Size(8, 8)).apply(processedMat, equalized);
        processedMat.release();
        
        // Apply bilateral filtering to reduce noise while preserving edges
        Mat filtered = new Mat();
        Imgproc.bilateralFilter(equalized, filtered, 9, 75, 75);
        equalized.release();
        
        return filtered;
    }
    
    /**
     * Enhances an image specifically for difficult lighting conditions.
     * 
     * This method is designed to improve document detection in:
     * - Low light conditions
     * - High contrast scenes
     * - Uneven lighting
     * 
     * @param sourceMat The source image in OpenCV Mat format
     * @return An enhanced image optimized for difficult lighting
     */
    private static Mat enhanceImageForDifficultLighting(Mat sourceMat) {
        // Create output matrix
        Mat enhancedMat = new Mat();
        
        // Convert to grayscale if not already
        if (sourceMat.channels() > 1) {
            Imgproc.cvtColor(sourceMat, enhancedMat, Imgproc.COLOR_BGR2GRAY);
        } else {
            sourceMat.copyTo(enhancedMat);
        }
        
        // Apply strong histogram equalization
        Imgproc.equalizeHist(enhancedMat, enhancedMat);
        
        // Apply morphological operations to enhance structure
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(enhancedMat, enhancedMat, Imgproc.MORPH_OPEN, kernel);
        kernel.release();
        
        // Apply adaptive thresholding
        Mat thresholded = new Mat();
        Imgproc.adaptiveThreshold(enhancedMat, thresholded, 255, 
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        enhancedMat.release();
        
        return thresholded;
    }
    
    /**
     * Detects document edges using contour-based approach.
     * 
     * This method:
     * 1. Applies edge detection to find edges in the image
     * 2. Finds contours in the edge-detected image
     * 3. Identifies quadrilateral contours that could be document boundaries
     * 4. Filters contours based on area and shape to find the most likely document
     * 
     * @param processedMat Pre-processed image in grayscale
     * @param imageArea Total area of the original image for relative size comparison
     * @return An array of 4 points representing the corners of the detected document,
     *         or null if no suitable document contour was found
     */
    private static Point[] detectDocumentByContours(Mat processedMat, double imageArea) {
        try {
            // Use Canny edge detector to find edges
            Mat edges = new Mat();
            Imgproc.Canny(processedMat, edges, 75, 200);
            
            // Find contours in the edge-detected image
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            
            // Log number of contours found
            Log.d(TAG, "Found " + contours.size() + " contours");
            
            // Sort contours by area (descending)
            contours.sort((c1, c2) -> {
                double area1 = Imgproc.contourArea(c1);
                double area2 = Imgproc.contourArea(c2);
                return Double.compare(area2, area1);
            });
            
            // Find the largest contour that could be a document (rectangle-like)
            for (MatOfPoint contour : contours) {
                double contourArea = Imgproc.contourArea(contour);
                double contourAreaRatio = contourArea / imageArea;
                
                // Skip if contour is too small or too large relative to the image
                if (contourAreaRatio < MIN_DOCUMENT_AREA_RATIO || contourAreaRatio > MAX_DOCUMENT_AREA_RATIO) {
                    contour.release();
                    continue;
                }
                
                // Convert to MatOfPoint2f for approxPolyDP
                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
                double perimeter = Imgproc.arcLength(contour2f, true);
                
                // Approximate the contour to a polygon
                MatOfPoint2f approx = new MatOfPoint2f();
                Imgproc.approxPolyDP(contour2f, approx, APPROX_POLY_DP_FACTOR * perimeter, true);
                
                // If the polygon has 4 points, it could be our document
                if (approx.total() == 4) {
                    org.opencv.core.Point[] points = approx.toArray();
                    
                    // Convert OpenCV points to Android points
                    Point[] documentCorners = new Point[4];
                    for (int i = 0; i < 4; i++) {
                        documentCorners[i] = new Point((int) points[i].x, (int) points[i].y);
                    }
                    
                    // Clean up
                    contour.release();
                    contour2f.release();
                    approx.release();
                    edges.release();
                    hierarchy.release();
                    
                    Log.d(TAG, "Document contour found with area ratio: " + contourAreaRatio);
                    return documentCorners;
                }
                
                contour.release();
                contour2f.release();
                approx.release();
            }
            
            // Clean up
            edges.release();
            hierarchy.release();
            
            Log.d(TAG, "No suitable document contour found");
            return null;
            
        } catch (Exception e) {
            Log.e(TAG, "Error in contour detection: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Detects document edges using Hough line transform.
     * 
     * This method is used as a fallback when contour detection fails. It:
     * 1. Detects lines in the image using Hough transform
     * 2. Identifies dominant horizontal and vertical lines
     * 3. Finds intersections of these lines to determine potential document corners
     * 4. Forms a quadrilateral from the most suitable intersection points
     * 
     * @param processedMat Pre-processed image in grayscale
     * @return An array of 4 points representing the corners of the detected document,
     *         or null if lines couldn't be reliably detected
     */
    private static Point[] detectDocumentByHoughLines(Mat processedMat) {
        try {
            // Apply Canny edge detection with more aggressive parameters
            Mat edges = new Mat();
            Imgproc.Canny(processedMat, edges, 50, 150);
            
            // Use probabilistic Hough Line Transform to detect lines
            Mat lines = new Mat();
            Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 80, 50, 10);
            
            // If no lines were detected, return null
            if (lines.rows() < 4) {
                Log.d(TAG, "Not enough lines detected for Hough method: " + lines.rows());
                edges.release();
                lines.release();
                return null;
            }
            
            // Separate horizontal and vertical lines
            List<org.opencv.core.Point[]> horizontalLines = new ArrayList<>();
            List<org.opencv.core.Point[]> verticalLines = new ArrayList<>();
            
            for (int i = 0; i < lines.rows(); i++) {
                double[] line = lines.get(i, 0);
                
                // Calculate line angle
                double dx = line[2] - line[0];
                double dy = line[3] - line[1];
                double angle = Math.abs(Math.toDegrees(Math.atan2(dy, dx)));
                
                // Classify as horizontal or vertical (with tolerance)
                if ((angle < 30 || angle > 150)) {
                    // Horizontal line
                    horizontalLines.add(new org.opencv.core.Point[] {
                        new org.opencv.core.Point(line[0], line[1]),
                        new org.opencv.core.Point(line[2], line[3])
                    });
                } else if ((angle > 60 && angle < 120)) {
                    // Vertical line
                    verticalLines.add(new org.opencv.core.Point[] {
                        new org.opencv.core.Point(line[0], line[1]),
                        new org.opencv.core.Point(line[2], line[3])
                    });
                }
            }
            
            // We need at least 2 horizontal and 2 vertical lines
            if (horizontalLines.size() < 2 || verticalLines.size() < 2) {
                Log.d(TAG, "Not enough horizontal/vertical lines: H=" + 
                        horizontalLines.size() + ", V=" + verticalLines.size());
                edges.release();
                lines.release();
                return null;
            }
            
            // Find top, bottom, left, and right boundary lines
            org.opencv.core.Point[] topLine = findExtremeHorizontalLine(horizontalLines, true, processedMat.rows());
            org.opencv.core.Point[] bottomLine = findExtremeHorizontalLine(horizontalLines, false, processedMat.rows());
            org.opencv.core.Point[] leftLine = findExtremeVerticalLine(verticalLines, true, processedMat.cols());
            org.opencv.core.Point[] rightLine = findExtremeVerticalLine(verticalLines, false, processedMat.cols());
            
            // If any boundary line is missing, we can't form a complete quadrilateral
            if (topLine == null || bottomLine == null || leftLine == null || rightLine == null) {
                Log.d(TAG, "Could not identify all boundary lines");
                edges.release();
                lines.release();
                return null;
            }
            
            // Find intersections to form corners
            org.opencv.core.Point topLeft = findIntersection(topLine, leftLine);
            org.opencv.core.Point topRight = findIntersection(topLine, rightLine);
            org.opencv.core.Point bottomRight = findIntersection(bottomLine, rightLine);
            org.opencv.core.Point bottomLeft = findIntersection(bottomLine, leftLine);
            
            // Convert to Android points
            Point[] documentCorners = new Point[4];
            documentCorners[0] = new Point((int) topLeft.x, (int) topLeft.y);
            documentCorners[1] = new Point((int) topRight.x, (int) topRight.y);
            documentCorners[2] = new Point((int) bottomRight.x, (int) bottomRight.y);
            documentCorners[3] = new Point((int) bottomLeft.x, (int) bottomLeft.y);
            
            // Clean up
            edges.release();
            lines.release();
            
            Log.d(TAG, "Document corners detected using Hough lines");
            return documentCorners;
            
        } catch (Exception e) {
            Log.e(TAG, "Error in Hough line detection: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Finds the extreme horizontal line (top-most or bottom-most) from a list of lines.
     * 
     * @param horizontalLines List of horizontal lines
     * @param findTop If true, finds the top-most line; otherwise finds the bottom-most line
     * @param imageHeight Height of the image for boundary checks
     * @return The extreme horizontal line as an array of two Points
     */
    private static org.opencv.core.Point[] findExtremeHorizontalLine(
            List<org.opencv.core.Point[]> horizontalLines, boolean findTop, int imageHeight) {
        
        if (horizontalLines.isEmpty()) return null;
        
        org.opencv.core.Point[] extremeLine = horizontalLines.get(0);
        double extremeValue = (extremeLine[0].y + extremeLine[1].y) / 2.0;
        
        for (org.opencv.core.Point[] line : horizontalLines) {
            double yAvg = (line[0].y + line[1].y) / 2.0;
            
            if ((findTop && yAvg < extremeValue) || (!findTop && yAvg > extremeValue)) {
                extremeValue = yAvg;
                extremeLine = line;
            }
        }
        
        // Check if line is at the image boundary (margin of 10 pixels)
        double yAvg = (extremeLine[0].y + extremeLine[1].y) / 2.0;
        if ((findTop && yAvg < 10) || (!findTop && yAvg > imageHeight - 10)) {
            // Line is at the image boundary, likely not a document edge
            return null;
        }
        
        return extremeLine;
    }
    
    /**
     * Finds the extreme vertical line (left-most or right-most) from a list of lines.
     * 
     * @param verticalLines List of vertical lines
     * @param findLeft If true, finds the left-most line; otherwise finds the right-most line
     * @param imageWidth Width of the image for boundary checks
     * @return The extreme vertical line as an array of two Points
     */
    private static org.opencv.core.Point[] findExtremeVerticalLine(
            List<org.opencv.core.Point[]> verticalLines, boolean findLeft, int imageWidth) {
        
        if (verticalLines.isEmpty()) return null;
        
        org.opencv.core.Point[] extremeLine = verticalLines.get(0);
        double extremeValue = (extremeLine[0].x + extremeLine[1].x) / 2.0;
        
        for (org.opencv.core.Point[] line : verticalLines) {
            double xAvg = (line[0].x + line[1].x) / 2.0;
            
            if ((findLeft && xAvg < extremeValue) || (!findLeft && xAvg > extremeValue)) {
                extremeValue = xAvg;
                extremeLine = line;
            }
        }
        
        // Check if line is at the image boundary (margin of 10 pixels)
        double xAvg = (extremeLine[0].x + extremeLine[1].x) / 2.0;
        if ((findLeft && xAvg < 10) || (!findLeft && xAvg > imageWidth - 10)) {
            // Line is at the image boundary, likely not a document edge
            return null;
        }
        
        return extremeLine;
    }
    
    /**
     * Finds the intersection point of two lines.
     * 
     * @param line1 First line as an array of two Points
     * @param line2 Second line as an array of two Points
     * @return The intersection point
     */
    private static org.opencv.core.Point findIntersection(
            org.opencv.core.Point[] line1, org.opencv.core.Point[] line2) {
        
        // Line 1 represented as a1x + b1y = c1
        double a1 = line1[1].y - line1[0].y;
        double b1 = line1[0].x - line1[1].x;
        double c1 = a1 * line1[0].x + b1 * line1[0].y;
        
        // Line 2 represented as a2x + b2y = c2
        double a2 = line2[1].y - line2[0].y;
        double b2 = line2[0].x - line2[1].x;
        double c2 = a2 * line2[0].x + b2 * line2[0].y;
        
        // Determinant
        double determinant = a1 * b2 - a2 * b1;
        
        // Lines are parallel or coincident
        if (Math.abs(determinant) < 0.001) {
            // Return midpoint of first line as a fallback
            return new org.opencv.core.Point(
                    (line1[0].x + line1[1].x) / 2.0,
                    (line1[0].y + line1[1].y) / 2.0
            );
        }
        
        // Calculate intersection point
        double x = (b2 * c1 - b1 * c2) / determinant;
        double y = (a1 * c2 - a2 * c1) / determinant;
        
        return new org.opencv.core.Point(x, y);
    }
    
    /**
     * Apply perspective transform to crop and correct the document
     * 
     * @param sourceBitmap The original image
     * @param corners The four corners of the document
     * @return A new bitmap with the document cropped and perspective-corrected
     */
    public static Bitmap perspectiveTransform(Bitmap sourceBitmap, Point[] corners) {
        try {
            if (corners == null || corners.length != 4) {
                Log.e(TAG, "Invalid corners for perspective transform");
                return sourceBitmap;
            }
            
            // Convert bitmap to OpenCV Mat
            Mat sourceMat = new Mat();
            Utils.bitmapToMat(sourceBitmap, sourceMat);
            
            // Convert Android points to OpenCV points
            org.opencv.core.Point[] sourcePoints = new org.opencv.core.Point[4];
            for (int i = 0; i < 4; i++) {
                sourcePoints[i] = new org.opencv.core.Point(corners[i].x, corners[i].y);
            }
            
            // Calculate the width and height of the destination image
            double widthA = Math.sqrt(Math.pow(corners[2].x - corners[3].x, 2) + 
                               Math.pow(corners[2].y - corners[3].y, 2));
            double widthB = Math.sqrt(Math.pow(corners[1].x - corners[0].x, 2) + 
                               Math.pow(corners[1].y - corners[0].y, 2));
            int maxWidth = (int) Math.max(widthA, widthB);
            
            double heightA = Math.sqrt(Math.pow(corners[1].x - corners[2].x, 2) + 
                                Math.pow(corners[1].y - corners[2].y, 2));
            double heightB = Math.sqrt(Math.pow(corners[0].x - corners[3].x, 2) + 
                                Math.pow(corners[0].y - corners[3].y, 2));
            int maxHeight = (int) Math.max(heightA, heightB);
            
            // Create destination points
            org.opencv.core.Point[] destPoints = new org.opencv.core.Point[4];
            destPoints[0] = new org.opencv.core.Point(0, 0);
            destPoints[1] = new org.opencv.core.Point(maxWidth - 1, 0);
            destPoints[2] = new org.opencv.core.Point(maxWidth - 1, maxHeight - 1);
            destPoints[3] = new org.opencv.core.Point(0, maxHeight - 1);
            
            // Get the perspective transform
            MatOfPoint2f src = new MatOfPoint2f(sourcePoints);
            MatOfPoint2f dst = new MatOfPoint2f(destPoints);
            Mat transformMatrix = Imgproc.getPerspectiveTransform(src, dst);
            
            // Warp the image
            Mat warpedMat = new Mat(maxHeight, maxWidth, CvType.CV_8UC3);
            Imgproc.warpPerspective(sourceMat, warpedMat, transformMatrix, new Size(maxWidth, maxHeight));
            
            // Convert back to bitmap
            Bitmap resultBitmap = Bitmap.createBitmap(warpedMat.cols(), warpedMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(warpedMat, resultBitmap);
            
            // Clean up
            sourceMat.release();
            src.release();
            dst.release();
            transformMatrix.release();
            warpedMat.release();
            
            return resultBitmap;
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying perspective transform: " + e.getMessage(), e);
            return sourceBitmap;
        }
    }
    
    /**
     * Orders points in the sequence: top-left, top-right, bottom-right, bottom-left
     */
    private static Point[] orderPoints(Point[] points) {
        if (points == null || points.length != 4) return points;
        
        Point[] orderedPoints = new Point[4];
        
        // Calculate sum and difference of coordinates
        double[] sums = new double[4];
        double[] diffs = new double[4];
        
        for (int i = 0; i < 4; i++) {
            sums[i] = points[i].x + points[i].y;
            diffs[i] = points[i].x - points[i].y;
        }
        
        // Top-left point will have the smallest sum
        int minSumIdx = 0;
        for (int i = 1; i < 4; i++) {
            if (sums[i] < sums[minSumIdx]) minSumIdx = i;
        }
        orderedPoints[0] = points[minSumIdx];
        
        // Bottom-right point will have the largest sum
        int maxSumIdx = 0;
        for (int i = 1; i < 4; i++) {
            if (sums[i] > sums[maxSumIdx]) maxSumIdx = i;
        }
        orderedPoints[2] = points[maxSumIdx];
        
        // Top-right point will have the largest difference
        int maxDiffIdx = 0;
        for (int i = 1; i < 4; i++) {
            if (diffs[i] > diffs[maxDiffIdx]) maxDiffIdx = i;
        }
        orderedPoints[1] = points[maxDiffIdx];
        
        // Bottom-left point will have the smallest difference
        int minDiffIdx = 0;
        for (int i = 1; i < 4; i++) {
            if (diffs[i] < diffs[minDiffIdx]) minDiffIdx = i;
        }
        orderedPoints[3] = points[minDiffIdx];
        
        return orderedPoints;
    }
}

