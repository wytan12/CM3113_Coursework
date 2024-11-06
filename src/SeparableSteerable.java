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

        // padding 3 sigma
        Image padded = Relaxation.applyPadding(input, sigma);
        padded.WritePGM("padded.pgm");

        Image gaussian = applyGaussianBlur(padded, sigma);
        Image gaussianCropped = Relaxation.cropImage(gaussian, input.width, input.height, sigma);
        gaussianCropped.WritePGM("G.pgm");

        Image Gx = applyDerivativeX(padded, sigma);
        Image GxCropped = Relaxation.cropImage(Gx, input.width, input.height, sigma);
        scaleImage(GxCropped, displayMode);
        GxCropped.WritePGM("Gx.pgm");

        Image Gy = applyDerivativeY(padded, sigma);
        Image GyCropped = Relaxation.cropImage(Gy, input.width, input.height, sigma);
        scaleImage(GyCropped, displayMode);
        GyCropped.WritePGM("Gy.pgm");

        Image Gxx = applySecondDerivativeX(padded, sigma);
        Image GxxCropped = Relaxation.cropImage(Gxx, input.width, input.height, sigma);
        scaleImage(GxxCropped, displayMode);
        GxxCropped.WritePGM("Gxx.pgm");

        Image Gyy = applySecondDerivativeY(padded, sigma);
        Image GyyCropped = Relaxation.cropImage(Gyy, input.width, input.height, sigma);
        scaleImage(GyyCropped, displayMode);
        GyyCropped.WritePGM("Gyy.pgm");

        Image Gxy = applyMixedDerivativeXY(padded, sigma);
        Image GxyCropped = Relaxation.cropImage(Gxy, input.width, input.height, sigma);
        scaleImage(GxyCropped, displayMode);
        GxyCropped.WritePGM("Gxy.pgm");

        switch (featureMode) {
            case 1:
                Image edges = computeEdges(Gx, Gy);
                Image edgesCropped = Relaxation.cropImage(edges, input.width, input.height, sigma);
                edgesCropped.WritePGM("edges.pgm");
                break;
            case 2: //TODO
                
                break;
            case 3: //TODO
                
                break;
            case 4: //TODO
                
                break;
            case 5: //TODO
                
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

    public static Image computeEdges(Image Gx, Image Gy) {
        Image edges = new Image(Gx.depth, Gx.width, Gx.height);
        for (int y = 0; y < Gx.height; y++) {
            for (int x = 0; x < Gx.width; x++) {
                int magnitude = (int) Math.sqrt(Gx.pixels[x][y] * Gx.pixels[x][y] + Gy.pixels[x][y] * Gy.pixels[x][y]);
                edges.pixels[x][y] = Math.min(255, magnitude);
            }
        }
        return edges;
    }

    // scale image (display mode)
    public static void scaleImage(Image img, int displayMode) {
        int width = img.width;
        int height = img.height;
        double min, max;

        min = max = 0;

        switch (displayMode) {
            case 1:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        min = Math.min(min, img.pixels[x][y]);
                        max = Math.max(max, img.pixels[x][y]);
//                        double value = img.pixels[x][y];
//                        if (value < min) min = value;
//                        if (value > max) max = value;
                    }
                }

                double midGray = 128.0;
                double range = Math.max(max, -min); // Use the larger of max or -min to handle positive/negative scaling


                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int scaledValue = (int) ((img.pixels[x][y] / range) * 127 + midGray);
                        img.pixels[x][y] = Math.max(0, Math.min(255, scaledValue)); // Clamp to 0–255 range
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

                // Step 3: Rescale intensities so that max value maps to 255
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
                            min = Math.min(min, img.pixels[x][y]); // Find the maximum negative intensity (closest to zero)
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