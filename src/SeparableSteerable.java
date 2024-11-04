public class SeparableSteerable {
    public static void main(String[] args) {

        // sample command input
        // % java SeparableSteerable circle4.pgm 8 + various display and feature mode settings
        String filename = args[0];
        int sigma = Integer.parseInt(args[1]);
        int displayMode = Integer.parseInt(args[2]);
//        int featureMode = Integer.parseInt(args[3]);

        Image input = new Image();
        input.ReadPGM(filename);

        // padding 3 sigma
        Image padded = Relaxation.applyPadding(input, sigma);
        padded.WritePGM("padded.pgm");

        Image cropped = Relaxation.cropImage(padded, input.width, input.height, sigma);

        Image gaussian = applyGaussianBlur(cropped, sigma);
        gaussian.WritePGM("G.pgm");

        Image Gx = applyDerivativeX(cropped, sigma);
        scaleImage(Gx, displayMode);
        Gx.WritePGM("Gx_2.pgm");

        Image Gy = applyDerivativeY(cropped, sigma);
        scaleImage(Gy, displayMode);
        Gy.WritePGM("Gy_2.pgm");

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

    // scale image (display mode)
    public static void scaleImage(Image img, int displayMode) {
        int width = img.width;
        int height = img.height;

        // Find the min and max values
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        switch (displayMode) {
            case 1:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        double value = img.pixels[x][y];
                        if (value < min) min = value;
                        if (value > max) max = value;
                    }
                }

                // Scale values: map min to 0, max to 255, and zero to 128
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int scaledValue = (int) ((img.pixels[x][y] - min) / (max - min) * 255);
                        img.pixels[x][y] = scaledValue;
                    }
                }
                break;
            case 2:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        double value = img.pixels[x][y];
                        if (value > 0 && value > max) max = value;
                    }
                }

                // Scale positive values to [0, 255], set negative values to 0
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        if (img.pixels[x][y] > 0) {
                            img.pixels[x][y] = (int) (img.pixels[x][y] / max * 255);
                        } else {
                            img.pixels[x][y] = 0;
                        }
                    }
                }
                break;
            case 3:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        double value = img.pixels[x][y];
                        if (value < 0 && value < min) min = value;
                    }
                }

                // Scale negative values to [0, 255], set positive values to 0
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        if (img.pixels[x][y] < 0) {
                            img.pixels[x][y] = (int) (img.pixels[x][y] / min * -255); // Convert to positive range
                        } else {
                            img.pixels[x][y] = 0;
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid display mode");
        }
    }


}