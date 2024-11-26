public class Gaussian {
    public static double[] gaussianKernel(int sigma){

        int k = 3 * sigma;
        int size = 2 * k + 1;
        double[] kernel = new double[size];
        double sum = 0;

        for (int i = -k; i <= k; i++) {
            kernel[i + k] = Math.exp(-0.5 * (i * i) / (sigma * sigma));
            sum += kernel[i + k];
        }

        // normalize kernel
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }

    public static double[] firstDerivativeKernel(int sigma){
        int radius = 3 * sigma;
        int size = 2 * radius + 1;
        double[] kernel = new double[size];
        double sum = 0;

        for (int i = -radius; i <= radius; i++) {
            kernel[i + radius] = (-i / (double) (sigma * sigma)) * Math.exp(-0.5 * (i * i) / (sigma * sigma));
            sum += Math.abs(kernel[i + radius]);
        }

        return kernel;
    }

    public static double[] secondDerivativeKernel(int sigma) {
        int radius = 3 * sigma;
        int size = 2 * radius + 1;
        double[] kernel = new double[size];
        double sum = 0;
        for (int i = -radius; i <= radius; i++) {
            kernel[i + radius] = (i * i - sigma * sigma) * Math.exp(-0.5 * (i * i) / (sigma * sigma));
            sum += Math.abs(kernel[i + radius]);
        }

        return kernel;
    }
}
