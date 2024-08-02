import java.awt.image.BufferedImage;

public class ColorLayer {
    private final int color;
    private final int x_min;
    private final int x_max;
    private final int y_min;
    private final int y_max;
    private final int bounding_area;
    private final int pixel_count;

    public ColorLayer(int new_color, BufferedImage bitmap){
        color = new_color;
        int temp_x_min = Integer.MAX_VALUE;
        int temp_x_max = Integer.MIN_VALUE;
        int temp_y_min = Integer.MAX_VALUE;
        int temp_y_max = Integer.MIN_VALUE;
        int temp_count = 0;
        for(int y=0; y<bitmap.getHeight(); y++){
            for(int x=0; x<bitmap.getWidth(); x++){
                if(bitmap.getRGB(x, y) == color){
                    temp_x_min = Math.min(temp_x_min, x);
                    temp_x_max = Math.max(temp_x_max, x);
                    temp_y_min = Math.min(temp_y_min, y);
                    temp_y_max = Math.max(temp_y_max, y);
                    temp_count++;
                }
            }
        }
        x_min = temp_x_min;
        x_max = temp_x_max;
        y_min = temp_y_min;
        y_max = temp_y_max;
        pixel_count = temp_count;
        int width = (x_max - x_min) + 1;
        int height = (y_max - y_min) + 1;
        bounding_area = width * height;
    }
}