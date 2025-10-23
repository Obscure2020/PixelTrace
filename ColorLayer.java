import java.io.IOException;
import java.util.*;

public class ColorLayer implements Comparable<ColorLayer>{
    public final int color;
    private final int x_min;
    private final int x_max;
    private final int y_min;
    private final int y_max;
    private final long bounding_area;
    private final int pixel_count;
    private final BitGrid mask;
    private Island[] children;

    private static final Island[] EMPTY_ISLANDS = new Island[0];

    public ColorLayer(int new_color, IntPointQueueBounded detections){
        color = new_color;
        x_min = detections.x_min();
        x_max = detections.x_max();
        y_min = detections.y_min();
        y_max = detections.y_max();
        pixel_count = detections.size();
        long width = (x_max - x_min) + 1;
        long height = (y_max - y_min) + 1;
        bounding_area = width * height;
        mask = new BitGrid((int) width, (int) height);
        while(!detections.isEmpty()){
            long packed_point = detections.poll();
            int x = (int) (packed_point >>> 32);
            int y = (int) packed_point;
            mask.setBit(x-x_min, y-y_min, true);
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
        int alpha_compare = Integer.compare(color >>> 24, other.color >>> 24); //Lower alpha is further towards the back
        if(alpha_compare == 0){
            int area_compare = Long.compare(other.bounding_area, bounding_area); //Greater area is further towards the back
            if(area_compare == 0){
                int count_compare = Integer.compare(other.pixel_count, pixel_count); //Greater pixel count is further towards the back
                if(count_compare == 0){
                    return Integer.compareUnsigned(color & 0xFFFFFF, other.color & 0xFFFFFF); //Darker color is further towards the back
                }
                return count_compare;
            }
            return area_compare;
        }
        return alpha_compare;
    }

    public void generateChildren(BitGrid prevMask){
        int[][] grid = new int[mask.height][mask.width];
        for(int global_y=y_min; global_y<=y_max; global_y++){
            final int local_y = global_y - y_min;
            for(int global_x=x_min; global_x<=x_max; global_x++){
                final int local_x = global_x - x_min;
                boolean marked = false;
                if(mask.getBit(local_x, local_y)){
                    marked = true;
                    prevMask.setBit(global_x, global_y, true);
                } else if(prevMask.getBit(global_x, global_y)){
                    marked = true;
                }
                grid[local_y][local_x] = marked ? -1 : -2;
            }
        }
        final int childCountUnchecked = ConnectedComponents.enumerate(grid, -1);
        int[] childIndices = new int[childCountUnchecked];
        Arrays.fill(childIndices, -1);
        int childCount = 0;
        for(int y=0; y<mask.height; y++){
            for(int x=0; x<mask.width; x++){
                if(mask.getBit(x, y)){
                    final int oldId = grid[y][x];
                    if(childIndices[oldId] < 0){
                        childIndices[oldId] = childCount;
                        childCount++;
                        if(childCount == childCountUnchecked) break;
                    }
                }
            }
            if(childCount == childCountUnchecked) break;
        }
        children = new Island[childCount];
        int[] child_x_min = new int[childCount];
        Arrays.fill(child_x_min, mask.width);
        int[] child_x_max = new int[childCount];
        Arrays.fill(child_x_max, -1);
        int[] child_y_min = new int[childCount];
        Arrays.fill(child_y_min, mask.height);
        int[] child_y_max = new int[childCount];
        Arrays.fill(child_y_max, -1);
        for(int y=0; y<mask.height; y++){
            for(int x=0; x<mask.width; x++){
                final int id = grid[y][x];
                if(id >= 0){
                    final int index = childIndices[id];
                    if(index >= 0){
                        child_x_min[index] = Math.min(child_x_min[index], x);
                        child_x_max[index] = Math.max(child_x_max[index], x);
                        child_y_min[index] = Math.min(child_y_min[index], y);
                        child_y_max[index] = Math.max(child_y_max[index], y);
                    }
                }
            }
        }
        for(int i=0; i<childCountUnchecked; i++){
            final int index = childIndices[i];
            if(index < 0) continue;
            int child_width = (child_x_max[index] - child_x_min[index]) + 1;
            int child_height = (child_y_max[index] - child_y_min[index]) + 1;
            BitGrid childBits = new BitGrid(child_width, child_height);
            for(int y=child_y_min[index]; y<=child_y_max[index]; y++){
                for(int x=child_x_min[index]; x<=child_x_max[index]; x++){
                    if(grid[y][x] == i){
                        childBits.setBit(x-child_x_min[index], y-child_y_min[index], true);
                    }
                }
            }
            children[index] = new Island(child_x_min[index] + x_min, child_y_min[index] + y_min, childBits, true);
        }
    }

    public void printSVG(ObscurePrint out) throws IOException {
        String colorSpec = "fill=\"#" + Main.leftPad(Integer.toHexString(color & 0xFFFFFF).toUpperCase(), '0', 6) + "\"";
        int alpha = color >>> 24;
        if(alpha != 0xFF){
            float opacity = ((float) alpha) / 255.0f;
            colorSpec += " fill-opacity=\"" + opacity + "\"";
        }
        out.print("<path " + colorSpec + " d=\"");
        children[0].traceSVG(out);
        for(int i=1; i<children.length; i++){
            out.print(" ");
            children[i].traceSVG(out);
        }
        out.println("\" />");
    }

    public void printTikZ(ObscurePrint out, final int globalHeight) throws IOException {
        final String colorName = "pt-" + Main.leftPad(Integer.toHexString(color & 0xFFFFFF).toUpperCase(), '0', 6);
        out.print("\\definecolor{");
        out.print(colorName);
        out.print("}{RGB}{");
        out.print((color >>> 16) & 0xFF);
        out.print(", ");
        out.print((color >>> 8) & 0xFF);
        out.print(", ");
        out.print(color & 0xFF);
        out.println("}");
        out.print("\\fill[");
        out.print(colorName);
        final int alpha = color >>> 24;
        if(alpha != 0xFF){
            float opacity = ((float) alpha) / 255.0f;
            out.print(",opacity=" + opacity);
        }
        out.print("]");
        children[0].traceTikZ(out, globalHeight);
        for(int i=1; i<children.length; i++){
            out.print(" ");
            children[i].traceTikZ(out, globalHeight);
        }
        out.println(";");
    }
}