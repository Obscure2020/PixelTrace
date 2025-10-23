public class ConnectedComponents {
    public static void enumerate(int[][] grid, int target){
        EqualityTracker tracker = new EqualityTracker();
        int islandCount = 0;
        final int height = grid.length;
        final int width = grid[0].length;
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                if(grid[y][x] != target) continue;
                final int up = (y > 0) ? grid[y - 1][x] : -1;
                final int left = (x > 0) ? grid[y][x - 1] : -1;
                final int match = Math.max(up, left);
                if(match >= 0){
                    grid[y][x] = match;
                    if((up >= 0) && (left >= 0) && (up != left)){
                        tracker.markEquivalent(up, left);
                    }
                } else {
                    tracker.include(islandCount);
                    grid[y][x] = islandCount;
                    islandCount++;
                }
            }
        }
        int[] groups = tracker.groupReport();
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                final int old = grid[y][x];
                if(old >= 0) grid[y][x] = groups[old];
            }
        }
    }
}