import java.util.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class Main{
    public static String leftPad(String original, char pad, int minLength){
        StringBuilder sb = new StringBuilder(original);
        while(sb.length() < minLength) sb.insert(0, pad);
        return sb.toString();
    }
    public static void main(String[] args) throws Exception{
        BufferedImage original = ImageIO.read(new File("TestBitmaps/Lakitu.png"));
        final int width = original.getWidth();
        final int height = original.getHeight();
        ArrayList<ColorLayer> layers = new ArrayList<>();
        {
            HashSet<Integer> colors = new HashSet<>();
            for(int y=0; y<height; y++){
                for(int x=0; x<width; x++){
                    colors.add(original.getRGB(x, y));
                }
            }
            for(int c : colors){
                layers.add(new ColorLayer(c, original));
            }
        } // Getting the "colors" object out of scope
        System.gc();
        Collections.sort(layers);
        BitGrid stackedBits = new BitGrid(width, height);
        for(ColorLayer layer : layers){
            System.out.println(layer.debugInfo());
        }
        for(ColorLayer layer : layers.reversed()){
            layer.trace(stackedBits, original);
        }
    }
}