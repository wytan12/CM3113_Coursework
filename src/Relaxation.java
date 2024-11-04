public class Relaxation {

    public static Image applyPadding(Image img, int sigma){

        int padding = 3 * sigma;
        int width = img.width;
        int height = img.height;
        int paddedWidth = width + 2 * padding;
        int paddedHeight = height + 2 * padding;

        // image[width][height]
        Image padded = new Image(img.depth, paddedWidth, paddedHeight);

        // center
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                padded.pixels[x + padding][y+padding] = img.pixels[x][y];
            }
        }

//        // test for bug
//        for(int x = 0; x < width; x++){
//            for(int y = 0; y < height; y++){
//                padded.pixels[x][y] = img.pixels[x][y];
//            }
//        }

        // reflect top and bottom border
        for(int x = 0; x < width; x++){
            for(int p = 0; p < padding; p++){
                // top
                padded.pixels[x + padding][p] = img.pixels[x][padding - p -1];
                // bottom
                padded.pixels[x + padding][p + height + padding] = img.pixels[x][height - p - 1];
            }
        }

        // reflect left and right
        for(int y = 0; y < height; y++){
            for(int p = 0; p < padding; p++){
                // left
                padded.pixels[p][y + padding] = img.pixels[padding - p - 1][y];
                // right
                padded.pixels[p + width + padding][y + padding] = img.pixels[width - p - 1][y];
            }
        }

        // reflect corner
        for (int m = 0; m < padding; m++) {
            for (int n = 0; n < padding; n++) {
                padded.pixels[m][n] = img.pixels[padding - m - 1 ][padding - n - 1];
                padded.pixels[paddedWidth - padding + m][n] = img.pixels[width - m - 1 ][padding - n - 1];
                padded.pixels[m][paddedHeight - padding + n] = img.pixels[padding - m - 1 ][height - n - 1];
                padded.pixels[paddedWidth - padding + m][paddedHeight - padding + n] = img.pixels[width - m - 1 ][height - n - 1];
            }
        }

        return padded;
    }

    // crop image to original size
    public static Image cropImage (Image img, int originWidth, int originHeight, int sigma){
        int padding = 3 * sigma;
        Image cropped = new Image(img.depth, originWidth, originHeight);

        for (int x = 0; x < originWidth ; x++) {
            for (int y = 0; y < originHeight; y++) {
                cropped.pixels[x][y] = img.pixels[x+padding][y+padding];
            }
        }

        return cropped;
    }

    public static double[] gaussianKernel(int sigma){

        int radius = 3 * sigma;
        int size = 2 * radius + 1;
        double[] kernel = new double[size];
        double sum = 0;

        for (int i = -radius; i <= radius; i++) {
            kernel[i + radius] = Math.exp(-0.5 * (i * i) / (sigma * sigma));
            sum += kernel[i + radius];
        }

        // normalize kernel
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }

    public static double[] firstDerivativeKernel(int sigma){
        int radius = (int) Math.ceil(3 * sigma);
        int size = 2 * radius + 1;
        double[] kernel = new double[size];
        double sum = 0;

        for (int i = -radius; i <= radius; i++) {
            kernel[i + radius] = -i * Math.exp(-0.5 * (i * i) / (sigma * sigma));
            sum += Math.abs(kernel[i + radius]);
        }

        // Normalize kernel
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }
        return kernel;
    }

    // convolution separable
    public static Image convolution(Image img, double[]kernelX, double[]kernelY){
        int width = img.width;
        int height = img.height;
        Image result = new Image(img.depth, width, height);

        int [][]temp = new int[width][height];

        // horizontal
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                double sum = 0;
                for (int i = -kernelX.length / 2; i <= kernelX.length / 2; i++) {
                    int xi = Math.min(Math.max(x + i, 0), width - 1); // Clamping at borders
                    sum += img.pixels[xi][y] * kernelX[i + kernelX.length / 2];
                }
                temp[x][y] = (int) sum;
            }
        }

        // vertical
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sum = 0;
                for (int i = -kernelY.length / 2; i <= kernelY.length / 2; i++) {
                    int yi = Math.min(Math.max(y + i, 0), height - 1); // Clamping at borders
                    sum += temp[x][yi] * kernelY[i + kernelY.length / 2];
                }
                result.pixels[x][y] = (int) Math.max(0, Math.min(255, sum)); // Clamping to 0-255 range
            }
        }


        return result;
    }



}
