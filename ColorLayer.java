import java.io.IOException;
import java.util.*;

public class ColorLayer implements Comparable<ColorLayer>{
    private final int color;
    private final int x_min;
    private final int x_max;
    private final int y_min;
    private final int y_max;
    private final long bounding_area;
    private final long pixel_count;
    private final BitGrid mask;
    private Island[] children;

    private static final Island[] EMPTY_ISLANDS = new Island[0];

    public ColorLayer(int new_color, List<IntPoint> detections){
        color = new_color;
        int temp_x_min = Integer.MAX_VALUE;
        int temp_x_max = Integer.MIN_VALUE;
        int temp_y_min = Integer.MAX_VALUE;
        int temp_y_max = Integer.MIN_VALUE;
        for(IntPoint p : detections){
            temp_x_min = Math.min(temp_x_min, p.x);
            temp_x_max = Math.max(temp_x_max, p.x);
            temp_y_min = Math.min(temp_y_min, p.y);
            temp_y_max = Math.max(temp_y_max, p.y);
        }
        x_min = temp_x_min;
        x_max = temp_x_max;
        y_min = temp_y_min;
        y_max = temp_y_max;
        pixel_count = detections.size();
        long width = (x_max - x_min) + 1;
        long height = (y_max - y_min) + 1;
        bounding_area = width * height;
        mask = new BitGrid((int) width, (int) height);
        for(IntPoint p : detections){
            mask.setBit(p.x-x_min, p.y-y_min, true);
        }
        children = EMPTY_ISLANDS;
    }

    public String debugInfo(){
        StringBuilder sb = new StringBuilder("====ColorLayer ");
        sb.append(Main.leftPad(Integer.toHexString(color).toUpperCase(), '0', 8));
        sb.append("====");
        sb.append(System.lineSeparator());
        sb.append("X: ");
        sb.append(x_min);
        sb.append(" -> ");
        sb.append(x_max);
        sb.append(System.lineSeparator());
        sb.append("Y: ");
        sb.append(y_min);
        sb.append(" -> ");
        sb.append(y_max);
        sb.append(System.lineSeparator());
        sb.append("Area: ");
        sb.append(bounding_area);
        sb.append(" / ");
        sb.append("Count: ");
        sb.append(pixel_count);
        sb.append(System.lineSeparator());
        sb.append(children.length);
        if(children.length == 1){
            sb.append(" stored child.");
        } else {
            sb.append(" stored children.");
        }
        return sb.toString();
    }

    @Override
    public int compareTo(ColorLayer other) {
        int area_compare = Long.compare(other.bounding_area, bounding_area);
        if(area_compare == 0){
            int count_compare = Long.compare(other.pixel_count, pixel_count);
            if(count_compare == 0){
                return Integer.compareUnsigned(color, other.color);
            }
            return count_compare;
        }
        return area_compare;
    }

    private int[] getMatchedIslands(int[][] grid){
        BitSet matchedIslands = new BitSet();
        for(int y=0; y<mask.height; y++){
            for(int x=0; x<mask.width; x++){
                if(mask.getBit(x, y)){
                    matchedIslands.set(grid[y][x]);
                }
            }
        }
        return matchedIslands.stream().toArray();
    }

    public void generateChildren(BitGrid prevMask){
        for(int y=y_min; y<=y_max; y++){
            for(int x=x_min; x<=x_max; x++){
                if(mask.getBit(x-x_min, y-y_min)){
                    prevMask.setBit(x, y, true);
                }
            }
        }
        int[][] grid = new int[mask.height][mask.width];
        for(int y=y_min; y<=y_max; y++){
            for(int x=x_min; x<=x_max; x++){
                if(prevMask.getBit(x, y)){
                    grid[y-y_min][x-x_min] = -1;
                } else {
                    grid[y-y_min][x-x_min] = -2;
                }
            }
        }
        int islandCount = 0;
        for(int y=0; y<mask.height; y++){
            for(int x=0; x<mask.width; x++){
                if(grid[y][x] == -1){
                    FloodFills.fourDirectionFill(grid, new IntPoint(x, y), -1, islandCount);
                    islandCount++;
                }
            }
        }
        int[] validIslands = getMatchedIslands(grid);
        children = new Island[validIslands.length];
        for(int i=0; i<validIslands.length; i++){
            int index = validIslands[i];
            int local_x_min = mask.width;
            int local_x_max = -1;
            int local_y_min = mask.height;
            int local_y_max = -1;
            for(int y=0; y<mask.height; y++){
                for(int x=0; x<mask.width; x++){
                    if(grid[y][x] == index){
                        local_x_min = Math.min(local_x_min, x);
                        local_x_max = Math.max(local_x_max, x);
                        local_y_min = Math.min(local_y_min, y);
                        local_y_max = Math.max(local_y_max, y);
                    }
                }
            }
            int island_width = (local_x_max - local_x_min) + 1;
            int island_height = (local_y_max - local_y_min) + 1;
            BitGrid islandBits = new BitGrid(island_width, island_height);
            for(int y=local_y_min; y<=local_y_max; y++){
                for(int x=local_x_min; x<=local_x_max; x++){
                    if(grid[y][x] == index){
                        islandBits.setBit(x-local_x_min, y-local_y_min, true);
                    }
                }
            }
            children[i] = new Island(local_x_min + x_min, local_y_min + y_min, islandBits, true);
        }
    }

    public void printSVG(PrintSVG out) throws IOException{
        String colorStr = "#" + Main.leftPad(Integer.toHexString(color & 0xFFFFFF).toUpperCase(), '0', 6);
        out.print("<path fill=\"" + colorStr + "\" d=\"");
        children[0].pathTrace(out);
        for(int i=1; i<children.length; i++){
            out.print(" ");
            children[i].pathTrace(out);
        }
        out.println("\" />");
    }
}