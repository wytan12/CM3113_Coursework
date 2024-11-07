public class Feature {
    public static Image computeEdges(Image Gx, Image Gy) {
        Image edges = new Image(Gx.depth, Gx.width, Gx.height);
        double[][] angles = new double[Gx.width][Gx.height];

        for (int y = 0; y < Gx.height; y++) {
            for (int x = 0; x < Gx.width; x++) {
                int gx = Gx.pixels[x][y];
                int gy = Gy.pixels[x][y];
                int magnitude = (int) Math.sqrt(Gx.pixels[x][y] * Gx.pixels[x][y] + Gy.pixels[x][y] * Gy.pixels[x][y]);
                edges.pixels[x][y] = Math.min(255, magnitude);
                angles[x][y] = Math.atan2(gy, gx); // Calculate gradient direction in radians
            }
        }

        // Non-maximum suppression
        Image suppressedEdges = new Image(Gx.depth, Gx.width, Gx.height);
        for (int y = 1; y < Gx.height - 1; y++) {
            for (int x = 1; x < Gx.width - 1; x++) {
                double angle = angles[x][y] * 180 / Math.PI; // Convert to degrees
                angle = (angle < 0) ? angle + 180 : angle; // Ensure angle is positive

                int magnitude = edges.pixels[x][y];
                int compare1 = 0, compare2 = 0;

                // Determine which two neighbors to compare based on the gradient direction
                if ((angle >= 0 && angle < 22.5) || (angle >= 157.5 && angle <= 180)) {
                    // 0 degrees (horizontal)
                    compare1 = edges.pixels[x + 1][y];
                    compare2 = edges.pixels[x - 1][y];
                } else if (angle >= 22.5 && angle < 67.5) {
                    // 45 degrees (diagonal)
                    compare1 = edges.pixels[x + 1][y + 1];
                    compare2 = edges.pixels[x - 1][y - 1];
                } else if (angle >= 67.5 && angle < 112.5) {
                    // 90 degrees (vertical)
                    compare1 = edges.pixels[x][y + 1];
                    compare2 = edges.pixels[x][y - 1];
                } else if (angle >= 112.5 && angle < 157.5) {
                    // 135 degrees (diagonal)
                    compare1 = edges.pixels[x - 1][y + 1];
                    compare2 = edges.pixels[x + 1][y - 1];
                }

                // Suppress non-maximum
                if (magnitude >= compare1 && magnitude >= compare2) {
                    suppressedEdges.pixels[x][y] = magnitude;
                } else {
                    suppressedEdges.pixels[x][y] = 0;
                }
            }
        }

        return suppressedEdges;
    }

    public static Image computeRadialEdges(Image Gx, Image Gy, Image input) {
        Image radialEdges = new Image(Gx.depth, Gx.width, Gx.height);
        // Calculate image center
        double centerX = (input.width - 1) / 2.0;
        double centerY = (input.height - 1)/ 2.0;

        for (int y = 0; y < Gx.height; y++) {
            for (int x = 0; x < Gx.width; x++) {
                // Calculate direction vector from center to pixel
                double dx = x - centerX;
                double dy = y - centerY;
                // Normalize the direction vector
                double length = Math.sqrt(dx * dx + dy * dy);
                if (length > 0) {
                    dx /= length;
                    dy /= length;
                    // Project gradient onto radial direction
                    double response = Gx.pixels[x][y] * dx + Gy.pixels[x][y] * dy;
                    // radialEdges.pixels[x][y] = (int) Math.max(-255, Math.min(255, response));
                    radialEdges.pixels[x][y] = (int) response;
                } else {
                    radialEdges.pixels[x][y] = 0; // Set a default value when length is zero
                }
            }
        }
        return radialEdges;
    }

    public static Image computeRidgesEigen(Image Gxx, Image Gxy, Image Gyy) {
        Image ridges = new Image(Gxx.depth, Gxx.width, Gxx.height);

        for (int y = 0; y < Gxx.height; y++) {
            for (int x = 0; x < Gxx.width; x++) {
                // Calculate eigenvalues using the formula from your assignment
                double a = Gxx.pixels[x][y];
                double b = Gxy.pixels[x][y];
                double c = Gyy.pixels[x][y];

                // Calculate the larger eigenvalue
                double temp = Math.sqrt((a - c) * (a - c) + 4 * b * b);
                double lambda1 = 0.5 * (a + c + temp);
                double lambda2 = 0.5 * (a + c - temp);

                // Use the eigenvalue with larger absolute value
                double response = Math.abs(lambda1) > Math.abs(lambda2) ? lambda1 : lambda2;
                ridges.pixels[x][y] = (int)response;
            }
        }
        return ridges;
    }

    public static Image computeRadialRidges(Image Gxx, Image Gxy, Image Gyy, Image inputImage) {
        Image radialRidges = new Image(Gxx.depth, Gxx.width, Gxx.height);
        double centerX = inputImage.width / 2.0;
        double centerY = inputImage.height / 2.0;

        for (int y = 0; y < Gxx.height; y++) {
            for (int x = 0; x < Gxx.width; x++) {
                // Calculate direction vector from center
                double dx = x - centerX;
                double dy = y - centerY;
                // Normalize
                double length = Math.sqrt(dx * dx + dy * dy);
                if (length > 0) {
                    dx /= length;
                    dy /= length;

                    // Calculate second derivative in radial direction
                    double response = dx * dx * Gxx.pixels[x][y] +
                            2 * dx * dy * Gxy.pixels[x][y] +
                            dy * dy * Gyy.pixels[x][y];

                    radialRidges.pixels[x][y] = (int)response;
                }
            }
        }
        return radialRidges;
    }

    public static Image computeCorners(Image Gxx, Image Gxy, Image Gyy) {
        Image corners = new Image(Gxx.depth, Gxx.width, Gxx.height);
        for (int y = 0; y < Gxx.height; y++) {
            for (int x = 0; x < Gxx.width; x++) {
                int determinant = Gxx.pixels[x][y] * Gyy.pixels[x][y] - Gxy.pixels[x][y] * Gxy.pixels[x][y];
                corners.pixels[x][y] = determinant;
            }
        }
        return corners;
    }

}
