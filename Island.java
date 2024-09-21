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

        int[][] grid = new int[pixels.height][pixels.width];
        for(int y=0; y<pixels.height; y++){
            for(int x=0; x<pixels.width; x++){
                if(pixels.getBit(x, y)){
                    grid[y][x] = -2;
                } else {
                    grid[y][x] = -1;
                }
            }
        }
        for(int x=0; x<pixels.width; x++){
            if(grid[0][x] == -1) FloodFills.eightDirectionFill(grid, new IntPoint(x, 0), -1, -2);
            if(grid[pixels.height - 1][x] == -1) FloodFills.eightDirectionFill(grid, new IntPoint(x, pixels.height - 1), -1, -2);
        }
        for(int y=1; y<pixels.height; y++){
            if(grid[y][0] == -1) FloodFills.eightDirectionFill(grid, new IntPoint(0, y), -1, -2);
            if(grid[y][pixels.width-1] == -1) FloodFills.eightDirectionFill(grid, new IntPoint(pixels.width-1, y), -1, -2);
        }
        int childCount = 0;
        for(int y=0; y<pixels.height; y++){
            for(int x=0; x<pixels.width; x++){
                if(grid[y][x] == -1){
                    FloodFills.fourDirectionFill(grid, new IntPoint(x, y), -1, childCount);
                    childCount++;
                }
            }
        }
        children = new Island[childCount];
        for(int i=0; i<childCount; i++){
            int local_x_min = pixels.width;
            int local_x_max = -1;
            int local_y_min = pixels.height;
            int local_y_max = -1;
            for(int y=0; y<pixels.height; y++){
                for(int x=0; x<pixels.width; x++){
                    if(grid[y][x] == i){
                        local_x_min = Math.min(local_x_min, x);
                        local_x_max = Math.max(local_x_max, x);
                        local_y_min = Math.min(local_y_min, y);
                        local_y_max = Math.max(local_y_max, y);
                    }
                }
            }
            int child_width = (local_x_max - local_x_min) + 1;
            int child_height = (local_y_max - local_y_min) + 1;
            BitGrid childBits = new BitGrid(child_width, child_height);
            for(int y=local_y_min; y<=local_y_max; y++){
                for(int x=local_x_min; x<=local_x_max; x++){
                    if(grid[y][x] == i){
                        childBits.setBit(x-local_x_min, y-local_y_min, true);
                    }
                }
            }
            children[i] = new Island(local_x_min + global_x_min, local_y_min + global_y_min, childBits, false);
        }
    }

    private boolean safeLookup(int x, int y){
        return x >= 0 && x < pixels.width
                && y >= 0 && y < pixels.height
                && pixels.getBit(x, y);
    }

    private int fourSquareVal(int x, int y){
        int topLeft = safeLookup(x-1, y-1) ? 8 : 0;
        int top = safeLookup(x, y-1) ? 4 : 0;
        int left = safeLookup(x-1, y) ? 2 : 0;
        int center = safeLookup(x, y) ? 1 : 0;
        return topLeft | top | left | center;
    }

    private IntPoint findUpperLeftCorner(){
        for(int y=0; y<pixels.height; y++){
            for(int x=0; x<pixels.width; x++){
                if(fourSquareVal(x, y) == 1){
                    return new IntPoint(x, y);
                }
            }
        }
        return new IntPoint(0, 0); //Just in case...
    }

    public void pathTrace(PrintSVG out) throws IOException{
        IntPoint start = findUpperLeftCorner();
        int prev_x = start.x;
        int prev_y = start.y;
        int cur_x = start.x+1;
        int cur_y = start.y;
        int direction = RIGHT;
        out.print("M " + (global_x_min + start.x) + " " + (global_y_min + start.y));
        while((cur_x != start.x) || (cur_y != start.y)){
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
            child.pathTrace(out);
        }
    }
}