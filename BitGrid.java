import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class BitGrid {
    public final int width;
    public final int height;
    private final long[] binData;

    public BitGrid(int new_width, int new_height){
        if(new_width < 1){
            throw new IllegalArgumentException("Width must be at least 1. \"" + new_width + "\" was specified.");
        }
        width = new_width;
        if(new_height < 1){
            throw new IllegalArgumentException("Height must be at least 1. \"" + new_height + "\" was specified.");
        }
        height = new_height;
        long pixels = ((long) width) * ((long) height);
        long cells = (pixels + 63) / 64;
        if(cells > Integer.MAX_VALUE){
            throw new IllegalArgumentException("Dimensions of " + width + "x" + height + " would require the storage of " + cells + " longs, which is beyond indexing maximum of " + Integer.MAX_VALUE + ".");
        }
        binData = new long[(int)cells];
    }

    private IndexAndMask indexBit(int x, int y){
        if(x < 0){
            throw new IllegalArgumentException("X must be at least 0. \"" + x + "\" was specified.");
        }
        if(y < 0){
            throw new IllegalArgumentException("Y must be at least 0. \"" + y + "\" was specified.");
        }
        final int xMax = width - 1;
        final int yMax = height - 1;
        if(x > xMax){
            throw new IllegalArgumentException("X must be at most " + xMax + ". \"" + x + "\" was specified.");
        }
        if(y > yMax){
            throw new IllegalArgumentException("Y must be at most " + yMax + ". \"" + y + "\" was specified.");
        }
        long pixel = ((long) y) * width + x;
        int index = (int)(pixel >> 6);
        long mask = Long.MIN_VALUE >>> (pixel & 63);
        return new IndexAndMask(index, mask);
    }

    public void setBit(int x, int y, boolean value){
        IndexAndMask spot = indexBit(x, y);
        binData[spot.index] = (binData[spot.index] & ~spot.mask) | (value ? spot.mask : 0);
    }

    public void toggleBit(int x, int y){
        IndexAndMask spot = indexBit(x, y);
        binData[spot.index] = binData[spot.index] ^ spot.mask;
    }

    public boolean getBit(int x, int y){
        IndexAndMask spot = indexBit(x, y);
        return (binData[spot.index] & spot.mask) != 0;
    }

    public void debugFile(File location) throws IOException{
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                canvas.setRGB(x, y, getBit(x, y) ? -1 : 0);
            }
        }
        ImageIO.write(canvas, "PNG", new FileOutputStream(location, false));
    }
}