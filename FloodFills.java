public class FloodFills {
    public static void fourDirectionFill(int[][] grid, int start_x, int start_y, int target, int newVal){
        IntPointQueue points = new IntPointQueue();
        points.add(start_x, start_y);
        int width = grid[0].length - 1;
        int height = grid.length - 1;

        while(!points.isEmpty()){
            long packed_point = points.poll();
            int x = (int) (packed_point >>> 32);
            int y = (int) packed_point;
            if(grid[y][x] != target) continue;
            grid[y][x] = newVal;

            if(x > 0) points.add(x-1, y);
            if(x < width) points.add(x+1, y);
            if(y > 0) points.add(x, y-1);
            if(y < height) points.add(x, y+1);
        }
    }

    public static void eightDirectionFill(int[][] grid, int start_x, int start_y, int target, int newVal){
        IntPointQueue points = new IntPointQueue();
        points.add(start_x, start_y);
        int width = grid[0].length - 1;
        int height = grid.length - 1;

        while(!points.isEmpty()){
            long packed_point = points.poll();
            int x = (int) (packed_point >>> 32);
            int y = (int) packed_point;
            if(grid[y][x] != target) continue;
            grid[y][x] = newVal;

            if(x > 0){
                points.add(x-1, y);
                if(y > 0) points.add(x-1, y-1);
                if(y < height - 1) points.add(x-1, y+1);
            }
            if(x < width){
                points.add(x+1, y);
                if(y > 0) points.add(x+1, y-1);
                if(y < height - 1) points.add(x+1, y+1);
            }
            if(y > 0) points.add(x, y-1);
            if(y < height - 1) points.add(x, y+1);
        }
    }
}