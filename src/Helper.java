public class Helper {

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

    // convolution separable
    public static Image convolution(Image img, double[]kernelX, double[]kernelY){
        int width = img.width;
        int height = img.height;
        Image result = new Image(img.depth, width, height);

        double[][] temp = new double[width][height];

        // horizontal
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                double sum = 0;
                for (int i = -kernelX.length / 2; i <= kernelX.length / 2; i++) {
                    // access the neighboring pixels
                    int c = x + i;
                    // prevent out of bound error
                    if (c >=0 && c < width) {
                        sum += img.pixels[c][y] * kernelX[i + kernelX.length / 2];
                    }
                }
                temp[x][y] = sum;
            }
        }

        // vertical
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sum = 0;
                for (int i = -kernelY.length / 2; i <= kernelY.length / 2; i++) {
                    int r = y + i;
                    if (r >= 0 && r < height) {
                        sum += temp[x][r] * kernelY[i + kernelY.length / 2];
                    }
                }
                result.pixels[x][y] = (int) sum;
            }
        }
        return result;
    }



}
