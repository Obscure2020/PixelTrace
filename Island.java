import java.io.IOException;
import java.util.*;

public class Island {
    static private final int RIGHT = 0;
    static private final int DOWN = 1;
    static private final int LEFT = 2;
    static private final int UP = 3;

    //For an explanation of the values in this table, see "Archive/Original Corner Table.png"
    static private final List<Map<Integer, Integer>> cornerTable = List.of(
        //Index 0 = Right
        Map.of(2,DOWN,8,UP,13,DOWN,9,UP,6,DOWN,7,UP),
        //Index 1 = Down
        Map.of(8,LEFT,4,RIGHT,9,LEFT,6,RIGHT,7,LEFT,11,RIGHT),
        //Index 2 = Left
        Map.of(1,DOWN,4,UP,14,DOWN,9,DOWN,6,UP,11,UP),
        //Index 3 = Up
        Map.of(1,RIGHT,2,LEFT,14,RIGHT,13,LEFT,9,RIGHT,6,LEFT)
    );

    private final int global_x_min;
    private final int global_y_min;
    private final BitGrid pixels;
    private Island[] children = new Island[0];

    public Island(int x_min, int y_min, BitGrid pixels_input, boolean canHaveChildren){
        global_x_min = x_min;
        global_y_min = y_min;
        pixels = pixels_input;

        if(!canHaveChildren) return;

        final int width = pixels.width;
        final int height = pixels.height;
        //Cache these values to enjoy a slight performance uplift and less verbose syntax.

        if(width<=2 || height<=2) return;

        int[][] grid = new int[height][width];
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                grid[y][x] = pixels.getBit(x, y) ? -2 : -1;
            }
        }
        final boolean remainingWork = edgeExclusionPass(grid);
        if(!remainingWork) return;

        final int childCount = ConnectedComponents.fourNeighborEnumerate(grid, -1);
        children = new Island[childCount];
        int[] child_x_min = new int[childCount];
        Arrays.fill(child_x_min, width);
        int[] child_x_max = new int[childCount];
        Arrays.fill(child_x_max, -1);
        int[] child_y_min = new int[childCount];
        Arrays.fill(child_y_min, height);
        int[] child_y_max = new int[childCount];
        Arrays.fill(child_y_max, -1);
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                int id = grid[y][x];
                if(id >= 0){
                    child_x_min[id] = Math.min(child_x_min[id], x);
                    child_x_max[id] = Math.max(child_x_max[id], x);
                    child_y_min[id] = Math.min(child_y_min[id], y);
                    child_y_max[id] = Math.max(child_y_max[id], y);
                }
            }
        }
        for(int i=0; i<childCount; i++){
            int child_width = (child_x_max[i] - child_x_min[i]) + 1;
            int child_height = (child_y_max[i] - child_y_min[i]) + 1;
            BitGrid childBits = new BitGrid(child_width, child_height);
            for(int y=child_y_min[i]; y<=child_y_max[i]; y++){
                for(int x=child_x_min[i]; x<=child_x_max[i]; x++){
                    if(grid[y][x] == i){
                        childBits.setBit(x-child_x_min[i], y-child_y_min[i], true);
                    }
                }
            }
            children[i] = new Island(child_x_min[i] + global_x_min, child_y_min[i] + global_y_min, childBits, false);
        }
    }

    private static boolean edgeExclusionPass(int[][] grid){
        final int numGroups = ConnectedComponents.eightNeighborEnumerate(grid, -1);
        if(numGroups < 1) return false;
        BitSet validSet = new BitSet(numGroups);
        validSet.set(0, numGroups);
        final int height = grid.length;
        final int width = grid[0].length;
        for(int x=0; x<width; x++){
            final int top = grid[0][x];
            final int bottom = grid[height-1][x];
            if(top >= 0) validSet.clear(top);
            if(bottom >= 0) validSet.clear(bottom);
        }
        if(validSet.isEmpty()) return false;
        for(int y=1; y<height-1; y++){
            final int left = grid[y][0];
            final int right = grid[y][width-1];
            if(left >= 0) validSet.clear(left);
            if(right >= 0) validSet.clear(right);
        }
        if(validSet.isEmpty()) return false;
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                final int old = grid[y][x];
                if(old >= 0){
                    grid[y][x] = validSet.get(old) ? -1 : -2;
                }
            }
        }
        return true;
    }

    private boolean safeLookup(int x, int y){
        return (x>=0) && (x<pixels.width) && (y>=0) && (y<pixels.height) && pixels.getBit(x, y);
    }

    private int fourSquareVal(int x, int y){
        int topLeft = safeLookup(x-1, y-1) ? 8 : 0;
        int top = safeLookup(x, y-1) ? 4 : 0;
        int left = safeLookup(x-1, y) ? 2 : 0;
        int center = safeLookup(x, y) ? 1 : 0;
        return topLeft | top | left | center;
    }

    private long findUpperLeftCorner(){
        for(int y=0; y<pixels.height; y++){
            for(int x=0; x<pixels.width; x++){
                if(fourSquareVal(x, y) == 1){
                    return (((long) x) << 32) | Integer.toUnsignedLong(y);
                }
            }
        }
        throw new AssertionError("Every island has at least one upper-left corner. The only way for this exception to trip is some sort of memory corruption or other catastrophic error has occurred.");
    }

    public void traceSVG(ObscurePrint out) throws IOException{
        long start = findUpperLeftCorner();
        int start_x = (int) (start >>> 32);
        int start_y = (int) start;
        int prev_x = start_x;
        int prev_y = start_y;
        int cur_x = start_x+1;
        int cur_y = start_y;
        int direction = RIGHT;
        out.print("M " + (global_x_min + start_x) + " " + (global_y_min + start_y));
        while((cur_x != start_x) || (cur_y != start_y)){
            int turn = cornerTable.get(direction).getOrDefault(fourSquareVal(cur_x, cur_y), -1);
            if(turn >= 0){
                if((direction == RIGHT) || (direction == LEFT)){ //Horizontal Line
                    out.print(" h ");
                    out.print(cur_x - prev_x);
                    prev_x = cur_x;
                } else { //Vertical Line
                    out.print(" v ");
                    out.print(cur_y - prev_y);
                    prev_y = cur_y;
                }
                direction = turn;
            }
            switch(direction) {
                case RIGHT -> cur_x++;
                case DOWN -> cur_y++;
                case LEFT -> cur_x--;
                case UP -> cur_y--;
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            }
        }
        out.print(" z");
        for(Island child : children){
            out.print(" ");
            child.traceSVG(out);
        }
    }

    public void traceTikZ(ObscurePrint out, final int globalHeight) throws IOException {
        long start = findUpperLeftCorner();
        int start_x = (int) (start >>> 32);
        int start_y = (int) start;
        int cur_x = start_x+1;
        int cur_y = start_y;
        int direction = RIGHT;
        out.print(" (");
        out.print(global_x_min + start_x);
        out.print(",");
        out.print(globalHeight - (global_y_min + start_y));
        out.print(")");
        while((cur_x != start_x) || (cur_y != start_y)){
            int turn = cornerTable.get(direction).getOrDefault(fourSquareVal(cur_x, cur_y), -1);
            if(turn >= 0){
                if((direction == DOWN) || (direction == UP)){
                    out.print(" -| (");
                    out.print(global_x_min + cur_x);
                    out.print(",");
                    out.print(globalHeight - (global_y_min + cur_y));
                    out.print(")");
                }
                direction = turn;
            }
            switch(direction) {
                case RIGHT -> cur_x++;
                case DOWN -> cur_y++;
                case LEFT -> cur_x--;
                case UP -> cur_y--;
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            }
        }
        out.print(" -| cycle");
        for(Island child : children) child.traceTikZ(out, globalHeight);
    }
}