public class SeparableSteerable {
    public static void main(String[] args) {

        // sample command input
        // % java SeparableSteerable circle4.pgm 8 + various display and feature mode settings
        String filename = args[0];
        int sigma = Integer.parseInt(args[1]);
        int displayMode = Integer.parseInt(args[2]);
        int featureMode = Integer.parseInt(args[3]);

        Image input = new Image();
        input.ReadPGM(filename);

        int width = input.width;
        int height = input.height;

        // padding 3 sigma
        Image padded = Relaxation.applyPadding(input, sigma);
        padded.WritePGM("padded.pgm");

        Image gaussian = applyGaussianBlur(padded, sigma);
        Image gaussianCropped = Relaxation.cropImage(gaussian, width, height, sigma);
        gaussianCropped.WritePGM("G.pgm");

        Image Gx = applyDerivativeX(padded, sigma);
        Image GxCropped = Relaxation.cropImage(Gx, width, height, sigma);
        scaleImage(GxCropped, displayMode);
        GxCropped.WritePGM("Gx.pgm");

        Image Gy = applyDerivativeY(padded, sigma);
        Image GyCropped = Relaxation.cropImage(Gy, width, height, sigma);
        scaleImage(GyCropped, displayMode);
        GyCropped.WritePGM("Gy.pgm");

        Image Gxx = applySecondDerivativeX(padded, sigma);
        Image GxxCropped = Relaxation.cropImage(Gxx, width, height, sigma);
        scaleImage(GxxCropped, displayMode);
        GxxCropped.WritePGM("Gxx.pgm");

        Image Gyy = applySecondDerivativeY(padded, sigma);
        Image GyyCropped = Relaxation.cropImage(Gyy, width, height, sigma);
        scaleImage(GyyCropped, displayMode);
        GyyCropped.WritePGM("Gyy.pgm");

        Image Gxy = applyMixedDerivativeXY(padded, sigma);
        Image GxyCropped = Relaxation.cropImage(Gxy, width, height, sigma);
        scaleImage(GxyCropped, displayMode);
        GxyCropped.WritePGM("Gxy.pgm");

        // generate output by selecting feature mode
        switch (featureMode) {
            case 1:
                Image edges = Feature.computeEdges(Gx, Gy);
                Image edgesCropped = Relaxation.cropImage(edges, width, height, sigma);
                scaleImage(edgesCropped, 2);
                edgesCropped.WritePGM("output/sailboat/edges.pgm");

                Image edgesNMS = Feature.nonMaxSuppression(Gx, Gy, edges);
                Image edgesNMSCropped = Relaxation.cropImage(edgesNMS, width, height, sigma);
                scaleImage(edgesNMSCropped, 2);
                edgesNMSCropped.WritePGM("output/sailboat/edgesNMS.pgm");
                break;
            case 2:
                Image radialEdges = Feature.computeRadialEdges(Gx, Gy, padded);
                Image radialEdgesCropped = Relaxation.cropImage(radialEdges, width, height, sigma);
                scaleImage(radialEdgesCropped, displayMode);
                radialEdgesCropped.WritePGM("edgesRadial.pgm");
                break;
            case 3:
                Image ridges = Feature.computeRidgesEigen(Gxx, Gxy, Gyy);
                scaleImage(ridges, displayMode);
                Image ridgesCropped = Relaxation.cropImage(ridges, width, height, sigma);
                ridgesCropped.WritePGM("ridgesEigen.pgm");
                break;
            case 4:
                Image radialRidges = Feature.computeRadialRidges(Gxx, Gxy, Gyy, padded);
                scaleImage(radialRidges, displayMode);
                Image radialRidgesCropped = Relaxation.cropImage(radialRidges, width, height, sigma);
                radialRidgesCropped.WritePGM("ridgesRadial.pgm");
                break;
            case 5:
                Image corners = Feature.computeCorners(Gxx, Gxy, Gyy);
                Image cornersCropped = Relaxation.cropImage(corners, width, height, sigma);
                scaleImage(cornersCropped, displayMode);
                cornersCropped.WritePGM("corners.pgm");

                // create corner overlay image
                Image cornerOverlay = Relaxation.cropImage(corners, width, height, sigma);
                scaleImage(cornerOverlay, 3);
                double thresholdFactor = 3.0; // Threshold factor (3 * stdDev)
                ImagePPM overlayImage = Feature.overlayCorners(input, cornerOverlay, thresholdFactor);
                overlayImage.WritePPM("cornersOverlay.ppm");

                break;
            default:
                throw new IllegalArgumentException("Invalid feature mode.");
        }


    }

    //gaussian filter
    public static Image applyGaussianBlur(Image img, int sigma) {
        double[] kernel = Relaxation.gaussianKernel(sigma);
        return Relaxation.convolution(img, kernel, kernel);
    }

    public static Image applyDerivativeX(Image img, int sigma) {
        double[] gaussianKernel = Relaxation.gaussianKernel(sigma);
        double[] derivativeKernelX = Relaxation.firstDerivativeKernel(sigma);
        return Relaxation.convolution(img, derivativeKernelX, gaussianKernel);
    }

    public static Image applyDerivativeY(Image img, int sigma) {
        double[] gaussianKernel = Relaxation.gaussianKernel(sigma);
        double[] derivativeKernelY = Relaxation.firstDerivativeKernel(sigma);
        return Relaxation.convolution(img, gaussianKernel, derivativeKernelY);
    }

    public static Image applySecondDerivativeX(Image img, int sigma) {
        double[] gaussianKernel = Relaxation.gaussianKernel(sigma);
        double[] secondDerivativeKernel = Relaxation.secondDerivativeKernel(sigma); 
        return Relaxation.convolution(img, secondDerivativeKernel, gaussianKernel);
    }

    public static Image applySecondDerivativeY(Image img, int sigma) {
        double[] gaussianKernel = Relaxation.gaussianKernel(sigma);
        double[] secondDerivativeKernel = Relaxation.secondDerivativeKernel(sigma);
        return Relaxation.convolution(img, gaussianKernel, secondDerivativeKernel);
    }

    public static Image applyMixedDerivativeXY(Image img, int sigma) {
        double[] derivativeKernel = Relaxation.firstDerivativeKernel(sigma);
        return Relaxation.convolution(img, derivativeKernel, derivativeKernel); 
    }

    // scale image (display mode)
    public static void scaleImage(Image img, int displayMode) {
        int width = img.width;
        int height = img.height;
        double min, max;

        min = max = 0;

        switch (displayMode) {
            case 1:
                // find the min and max
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        min = Math.min(min, img.pixels[x][y]);
                        max = Math.max(max, img.pixels[x][y]);
                    }
                }

                double midGray = 128.0;
                double scale;

                // either the maximum positive intensity is 255 or the minimum negative intensity is 0
                if (Math.abs(max) > Math.abs(min)) {
                    scale = 128.0 / max;
                } else {
                    scale = 128.0 / Math.abs(min);
                }

                // scale to fit [0,255]
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        // zero response will set to 128
                        int scaledValue = (int)(img.pixels[x][y] * scale + midGray);
                        img.pixels[x][y] = Math.max(0, Math.min(255, scaledValue));
                    }
                }
                break;
            case 2:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        if (img.pixels[x][y] < 0) {
                            img.pixels[x][y] = 0;
                        }
                    }
                }

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        max = Math.max(max, img.pixels[x][y]);
                    }
                }

                // rescale intensities so that max value maps to 255
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int scaledValue = (int) ((img.pixels[x][y] / max) * 255);
                        img.pixels[x][y] = scaledValue;
                    }
                }
                break;
            case 3:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        if (img.pixels[x][y] > 0) {
                            img.pixels[x][y] = 0; // Set positive values to zero
                        } else {
                            // Find the maximum negative intensity (closest to zero)
                            min = Math.min(min, img.pixels[x][y]);
                        }
                    }
                }

                // Scale negative values to [0, 255], set positive values to 0
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int scaledValue = (int) ((img.pixels[x][y] / min) * 255);
                        img.pixels[x][y] = scaledValue; // Clamp to 0–255 range
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid display mode");
        }
    }

}