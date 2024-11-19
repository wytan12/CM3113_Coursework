public class Feature {
    public static Image computeEdges(Image Gx, Image Gy) {
        Image edges = new Image(Gx.depth, Gx.width, Gx.height);

        for (int y = 0; y < Gx.height; y++) {
            for (int x = 0; x < Gx.width; x++) {
                int gx = Gx.pixels[x][y];
                int gy = Gy.pixels[x][y];
                //int magnitude = (int) Math.sqrt(Gx.pixels[x][y] * Gx.pixels[x][y] + Gy.pixels[x][y] * Gy.pixels[x][y]);
                //edges.pixels[x][y] = Math.min(255, magnitude);
                 // Calculate gradient direction in radians
                edges.pixels[x][y] = (int) Math.sqrt(gx * gx + gy * gy);
            }
        }

        return edges;
    }
//    public static Image computeEdges(Image Gx, Image Gy) {
//        Image edges = new Image(Gx.depth, Gx.width, Gx.height);
//        double maxMagnitude = 0;
//
//        // First pass - find maximum magnitude
//        for (int y = 0; y < Gx.height; y++) {
//            for (int x = 0; x < Gx.width; x++) {
//                double gx = Gx.pixels[x][y];
//                double gy = Gy.pixels[x][y];
//                double magnitude = Math.sqrt(gx * gx + gy * gy);
//                maxMagnitude = Math.max(maxMagnitude, magnitude);
//            }
//        }
//
//        // Second pass - scale and set pixels
//        for (int y = 0; y < Gx.height; y++) {
//            for (int x = 0; x < Gx.width; x++) {
//                double gx = Gx.pixels[x][y];
//                double gy = Gy.pixels[x][y];
//                double magnitude = Math.sqrt(gx * gx + gy * gy);
//                edges.pixels[x][y] = (int)(255 * magnitude / maxMagnitude);
//            }
//        }
//        return edges;
//    }

    // extra feature
    public static Image nonMaxSuppression(Image Gx, Image Gy, Image edges) {
        double[][] angles = new double[Gx.width][Gx.height];
        // Non-maximum suppression
        // go through all the points on the gradient intensity matrix
        // find the pixel with max value in the edge direction
        Image suppressedEdges = new Image(Gx.depth, Gx.width, Gx.height);
        for (int y = 1; y < Gx.height - 1; y++) {
            for (int x = 1; x < Gx.width - 1; x++) {
                double gx = Gx.pixels[x][y];
                double gy = Gy.pixels[x][y];
                angles[x][y] = Math.atan2(gy, gx);

                double angle = angles[x][y] * 180 / Math.PI; // Convert to degrees
                angle = (angle < 0) ? angle + 180 : angle; // Ensure angle is positive

                int magnitude = edges.pixels[x][y];
                int q = 255, r = 255;

                // Determine which two neighbors to compare based on the gradient direction
                if ((angle >= 0 && angle < 22.5) || (angle >= 157.5 && angle <= 180)) {
                    // 0 degrees (horizontal)
                    q = edges.pixels[x + 1][y];
                    r = edges.pixels[x - 1][y];
                } else if (angle >= 22.5 && angle < 67.5) {
                    // 45 degrees (diagonal)
                    q = edges.pixels[x + 1][y + 1];
                    r = edges.pixels[x - 1][y - 1];
                } else if (angle >= 67.5 && angle < 112.5) {
                    // 90 degrees (vertical)
                    q = edges.pixels[x][y + 1];
                    r = edges.pixels[x][y - 1];
                } else if (angle >= 112.5 && angle < 157.5) {
                    // 135 degrees (diagonal)
                    q = edges.pixels[x - 1][y + 1];
                    r = edges.pixels[x + 1][y - 1];
                }

                // Suppress non-maximum
                if (magnitude >= q && magnitude >= r) {
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
        // calculate image center
        double centerX = (Gx.width - 1) / 2.0;
        double centerY = (Gx.height - 1)/ 2.0;

        for (int y = 0; y < Gx.height; y++) {
            for (int x = 0; x < Gx.width; x++) {
                // calculate direction vector from center to pixel
                double dx = x - centerX;
                double dy = y - centerY;

                // normalize the direction vector
                // length is positive
                double length = Math.sqrt(dx * dx + dy * dy);

                if (length > 0) {
                    // Normalize direction vector
                    dx /= length;
                    dy /= length;

                    // Compute gradient magnitude in tangential direction
                    double response = Gx.pixels[x][y] * dx + Gy.pixels[x][y] * dy;
                    radialEdges.pixels[x][y] = (int) response;

                } else {
                    radialEdges.pixels[x][y] = 0;
                }

            }
        }
        return radialEdges;
    }

    public static Image computeRidgesEigen(Image Gxx, Image Gxy, Image Gyy) {
        Image ridges = new Image(Gxx.depth, Gxx.width, Gxx.height);

        for (int y = 0; y < Gxx.height; y++) {
            for (int x = 0; x < Gxx.width; x++) {
                double a = Gxx.pixels[x][y];
                double b = Gyy.pixels[x][y];
                double c = Gxy.pixels[x][y];

                double temp = Math.sqrt((a - b) * (a - b) + 4 * c * c);
                double k1 = 0.5 * (a + b + temp);
                double k2 = 0.5 * (a + b - temp);

                double response;

                // use the eigenvalue with larger absolute value
                if (Math.abs(k1) > Math.abs(k2)) {
                    response = k1;
                }
                else{
                    response = k2;
                }

                ridges.pixels[x][y] = (int)response;
            }
        }
        return ridges;
    }

    public static Image computeRadialRidges(Image Gxx, Image Gxy, Image Gyy, Image inputImage) {
        Image radialRidges = new Image(Gxx.depth, Gxx.width, Gxx.height);
        double centerX = Gxx.width / 2.0;
        double centerY = Gxx.height/ 2.0;

        for (int y = 0; y < Gxx.height; y++) {
            for (int x = 0; x < Gxx.width; x++) {
                // calculate direction vector from center
                double dx = x - centerX;
                double dy = y - centerY;
                // normalize
                double length = Math.sqrt(dx * dx + dy * dy);
                if (length > 0) {
                    dx /= length;
                    dy /= length;

                    // calculate second derivative in radial direction
                    double response = dx * dx * Gxx.pixels[x][y] + 2 * dx * dy * Gxy.pixels[x][y] + dy * dy * Gyy.pixels[x][y];

                    radialRidges.pixels[x][y] = (int)response;
                }
                else{
                    radialRidges.pixels[x][y] = 0;
                }

            }
        }
        return radialRidges;
    }

    // use Harris corner detector
    public static Image computeCorners(Image Gxx, Image Gxy, Image Gyy) {
        Image corners = new Image(Gxx.depth, Gxx.width, Gxx.height);

        for (int y = 0; y < Gxx.height; y++) {
            for (int x = 0; x < Gxx.width; x++) {
                double determinant = (Gxx.pixels[x][y] * Gyy.pixels[x][y]) - (Gxy.pixels[x][y] * Gxy.pixels[x][y]);
                double trace = Gxx.pixels[x][y] + Gyy.pixels[x][y];
                double H = determinant - 0.2 * trace * trace;
                //corners.pixels[x][y] = (int) determinant;
                corners.pixels[x][y] = (int) H;
            }
        }

        return corners;
    }

    public static ImagePPM overlayCorners(Image input, Image corners, double thresholdFactor) {

        // calculate mean and standard deviation of the corner response
        int width = corners.width;
        int height = corners.height;
        double sum = 0.0, sumSq = 0.0;
        int count = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double response = corners.pixels[x][y];
                sum += response;
                sumSq += response * response;
            }
        }

        double mean = sum / count;
        double variance = (sumSq / count) - (mean * mean);
        double stdDev = Math.sqrt(variance);
        double threshold = thresholdFactor * stdDev;

        ImagePPM overlay = new ImagePPM(255, input.width, input.height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // copy grayscale pixel intensity to RGB
                int intensity = input.pixels[x][y];
                overlay.pixels[0][x][y] = intensity;
                overlay.pixels[1][x][y] = intensity;
                overlay.pixels[2][x][y] = intensity;

                // overlay red for strong corner responses
                if (corners.pixels[x][y] > threshold) {
                    overlay.pixels[0][x][y] = 255;
                    overlay.pixels[1][x][y] = 0;
                    overlay.pixels[2][x][y] = 0;
                }
            }
        }

        return overlay;
    }


    public static Image nonMaxSuppression(Image corners) {
        int width = corners.width;
        int height = corners.height;

        Image suppressed = new Image(corners.depth, width, height);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int response = corners.pixels[x][y];

                // Check if the current pixel is the local maximum
                boolean isMax = true;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dx != 0 || dy != 0) { // Exclude the center pixel
                            if (corners.pixels[x + dx][y + dy] > response) {
                                isMax = false;
                                break;
                            }
                        }
                    }
                    if (!isMax) break;
                }

                suppressed.pixels[x][y] = isMax ? response : 0;
            }
        }

        return suppressed;
    }


}
