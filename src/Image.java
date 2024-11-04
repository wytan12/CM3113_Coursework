import java.io.*;
import java.util.*;

public class Image
{
    public int [][] pixels;
    public int depth,width,height;

    public Image()
    {
        pixels = new int[1500][1500];
        depth = width = height = 0;
    }

    public Image(int inDepth, int inWidth, int inHeight)
    {
        pixels = new int[inWidth][inHeight];
        width = inWidth;
        height = inHeight;
        depth = inDepth;
    }

    public void ReadPGM(String fileName)
    {
        String line;
        StringTokenizer st;

        try {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(fileName))));

            DataInputStream in2 =
                    new DataInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(fileName)));

            // read PGM image header
            line = in.readLine();
            in2.skip((line+"\n").getBytes().length);
            if (!Objects.equals(line,"P5")) {
                System.out.println("ERROR: image is not correct PGM format");
                System.exit(0);
            }

            // skip comments
            do {
                line = in.readLine();
                in2.skip((line+"\n").getBytes().length);
            } while (line.charAt(0) == '#');

            // the current line has dimensions
            st = new StringTokenizer(line);
            width = Integer.parseInt(st.nextToken());
            height = Integer.parseInt(st.nextToken());

            // overwrite pixels array to the correct dimensions
            pixels = new int[width][height];

            // next line has pixel depth
            line = in.readLine();
            in2.skip((line+"\n").getBytes().length);
            st = new StringTokenizer(line);
            depth = Integer.parseInt(st.nextToken());
            if (depth != 255) {
                System.out.println("Error: depth = "+depth+" (instead of 255)");
                System.exit(0);
            }

            // read pixels now
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    pixels[x][y] = in2.readUnsignedByte();

            in.close();
            in2.close();
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: image in "+fileName+" too big");
        } catch(FileNotFoundException e) {
            System.out.println("Error: file "+fileName+" not found");
        } catch(IOException e) {
            System.out.println("Error: end of stream encountered when reading "+fileName);
        }
    }

    public void WritePGM(String fileName)
    {
        String line;
        StringTokenizer st;
        int i;

        try {
            DataOutputStream out =
                    new DataOutputStream(
                            new BufferedOutputStream(
                                    new FileOutputStream(fileName)));

            out.writeBytes("P5\n");
            out.writeBytes("#created by Paul Rosin\n");
            out.writeBytes(width+" "+height+"\n255\n");

            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    out.writeByte((byte)pixels[x][y]);

            out.close();
        } catch(IOException e) {
            System.out.println("ERROR: cannot write output file");
        }
    }
}
