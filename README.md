CM3113: Computer Vision
# **Separable and Steerable Filters for Image Processing**

A Java implementation of separable and steerable Gaussian filters for detecting image features like edges, ridges, and corners. This project demonstrates efficient filtering, feature extraction, and visualization of results.

## **Features**
- **Edge Detection**:
  - Standard edges using the gradient magnitude ( \sqrt{G_x^2 + G_y^2}).
  - Radial edges aligned from the image center using directional derivatives.
  - Non-Maximum Suppression (NMS) for edge refinement.
- **Ridge Detection**:
  - Standard ridges based on the eigenvalues of the Hessian matrix.
  - Radial ridges aligned with radial directions from the image center.
- **Corner Detection**:
  - Keypoints detected using the determinant of the Hessian matrix (Gxx Gyy - Gxy^2).
  - Enhanced with Harris Corner Detection.
  - Thresholding using standard deviation (\(0 + 3s\)) with overlay visualization.

## **Getting Started**

1. Compile the Java files:
```bash
javac Image.java ImagePPM.java Helper.java Feature.java SeparableSteerable.java
```

2. Run the program:
```bash
java SeparableSteerable <input_image.pgm> <sigma> <display_mode> <feature_mode>
```

### **Input**
- `<input_image.pgm>`: The input image file in PGM format.
- `<sigma>`: The standard deviation for the Gaussian filter.
- `<display_mode>`: Visualization mode:
  - `1`: Scaled signed values with zero response at mid-gray (128).
  - `2`: Scaled negative responses.
  - `3`: Scaled positive responses.
- `<feature_mode>`: Feature computation:
  - `1`: Standard edges.
  - `2`: Radial edges.
  - `3`: Standard ridges.
  - `4`: Radial ridges.
  - `5`: Corners.

### **Output**
The program generates the following output files in `.pgm` or `.ppm` format:
- **Edges**: `edges.pgm`, `edgesRadial.pgm`
- **Ridges**: `ridgesEigen.pgm`, `ridgesRadial.pgm`
- **Corners**: `corners.pgm`, `cornersOverlay.ppm`

## **How It Works**
- **Separable Filters**: Efficiently compute Gaussian smoothing and derivatives using 1D convolutions.
- **Steerable Filters**: Compute directional responses by combining derivatives of Gaussian kernels for arbitrary angles.
- **Feature Extraction**:
  - **Edges**: Computed using the first derivatives (Gx, Gy).
  - **Ridges**: Computed using eigenvalues of the Hessian matrix (Gxx, Gyy, Gxy).
  - **Corners**: Computed using the determinant of the Hessian matrix, refined with Harris Corner Detection.


### **Example Usage**
For an input image `circle.pgm` with (sigma = 2), display mode `1`, and feature mode `5` (corners):
```bash
java SeparableSteerable circle.pgm 2 1 5
```

