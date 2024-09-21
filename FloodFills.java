public class FloodFills {
    public static void fourDirectionFill(int[][] grid, IntPoint start, int target, int newVal){
        IntQueue points = new IntQueue();
        points.add(start.x, start.y);
        int width = grid[0].length - 1;
        int height = grid.length - 1;

        while(!points.isEmpty()){
            int x = points.poll();
            int y = points.poll();
            if(grid[y][x] != target) continue;
            grid[y][x] = newVal;

            if(x > 0) points.add(x-1, y);
            if(x < width) points.add(x+1, y);
            if(y > 0) points.add(x, y-1);
            if(y < height) points.add(x, y+1);
        }
    }

    public static void eightDirectionFill(int[][] grid, IntPoint start, int target, int newVal){
        IntQueue points = new IntQueue();
        points.add(start.x, start.y);
        int width = grid[0].length - 1;
        int height = grid.length - 1;

        while(!points.isEmpty()){
            int x = points.poll();
            int y = points.poll();
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