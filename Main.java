import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class Main{
    public static String leftPad(String original, char pad, int minLength){
        int paddingLength = minLength - original.length();
        if(paddingLength <= 0) return original;
        char[] padding = new char[paddingLength];
        Arrays.fill(padding, pad);
        return new String(padding) + original;
    }

    private static ColorLayer[] createLayers(BufferedImage bitmap){
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        HashMap<Integer, ArrayList<IntPoint>> detections = new HashMap<>();
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                int color = bitmap.getRGB(x, y);
                int alpha = color >>> 24;
                if(alpha == 0) continue; //We shall not create a ColorLayer for any color that is fully transparent.
                detections.computeIfAbsent(color, _ -> new ArrayList<>()).add(new IntPoint(x, y));
            }
        }
        ArrayList<ColorLayer> layers = new ArrayList<>();
        for(Entry<Integer, ArrayList<IntPoint>> item : detections.entrySet()){
            layers.add(new ColorLayer(item.getKey(), item.getValue()));
        }
        System.out.println(layers.size() + " ColorLayers created.");
        return layers.toArray(new ColorLayer[0]);
    }
    public static void main(String[] args) throws Exception{
        final long startTime = System.nanoTime();
        BufferedImage original = ImageIO.read(new File("TestBitmaps/Lakitu.png"));
        final int width = original.getWidth();
        final int height = original.getHeight();
        ColorLayer[] layers = createLayers(original);
        Arrays.sort(layers);
        BitGrid stackedBits = new BitGrid(width, height);
        BitGrid lastOpaqueBits = new BitGrid(stackedBits);
        for(int i=0; i<layers.length; i++){
            if(i % 100 == 0){
                System.out.print(i + " ColorLayers chunked.\r");
                System.out.flush();
            }
            int index = layers.length-1-i; //ColorLayers sorted back-to-front, but must be traced front-to-back.
            layers[index].generateChildren(stackedBits);
            int alpha = layers[index].color >>> 24;
            if(alpha == 0xFF){
                //Fully opaque layer
                lastOpaqueBits = new BitGrid(stackedBits);
            } else {
                //Translucent layer
                stackedBits = new BitGrid(lastOpaqueBits);
            }
        }
        System.out.println(layers.length + " ColorLayers chunked.");
        ObscurePrint fileOut = new ObscurePrint(new File("Testing.svg"), "    ");
        fileOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        fileOut.println("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + width + "\" height=\"" + height + "\" viewBox=\"0 0 " + width + " " + height + "\" shape-rendering=\"crispEdges\" fill-rule=\"evenodd\">");
        fileOut.moreIndent();
        for(int i=0; i<layers.length; i++){
            if(i % 1000 == 0){
                System.out.print(i + " ColorLayers written.\r");
                System.out.flush();
            }
            layers[i].printSVG(fileOut);
        }
        System.out.println(layers.length + " ColorLayers written.");
        fileOut.lessIndent();
        fileOut.print("</svg>");
        fileOut.close();
        final long endTime = System.nanoTime();
        long durationInNanos = endTime - startTime;
        double seconds = durationInNanos / 1_000_000_000.0;
        System.out.printf("Finished in %.9f seconds.%n", seconds);
    }
}