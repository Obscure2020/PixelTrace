public class ConnectedComponents {
    public static int fourNeighborEnumerate(int[][] grid, int target){
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
        int maxFinalId = -1;
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                final int old = grid[y][x];
                if(old >= 0){
                    final int replace = groups[old];
                    grid[y][x] = replace;
                    if(replace > maxFinalId) maxFinalId = replace;
                }
            }
        }
        return maxFinalId + 1;
    }

    public static int eightNeighborEnumerate(int[][] grid, int target){
        EqualityTracker tracker = new EqualityTracker();
        int islandCount = 0;
        final int height = grid.length;
        final int width = grid[0].length;
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                if(grid[y][x] != target) continue;
                final boolean up_checkable = y > 0;
                final boolean left_checkable = x > 0;
                final boolean right_checkable = x < width - 1;
                final int up_left = (up_checkable && left_checkable) ? grid[y-1][x-1] : -1;
                final int up = up_checkable ? grid[y-1][x] : -1;
                final int up_right = (up_checkable && right_checkable) ? grid[y-1][x+1] : -1;
                final int left = left_checkable ? grid[y][x-1] : -1;
                final int match = Math.max(Math.max(up_left, up), Math.max(up_right, left));
                if(match >= 0){
                    grid[y][x] = match;
                    final boolean consider_up_left = up_left >= 0;
                    final boolean consider_up = up >= 0;
                    final boolean consider_up_right = up_right >= 0;
                    final boolean consider_left = left >= 0;
                    if(consider_up_left && consider_up && (up_left != up)) tracker.markEquivalent(up_left, up);
                    if(consider_up_left && consider_up_right && (up_left != up_right)) tracker.markEquivalent(up_left, up_right);
                    if(consider_up_left && consider_left && (up_left != left)) tracker.markEquivalent(up_left, left);
                    if(consider_up && consider_up_right && (up != up_right)) tracker.markEquivalent(up, up_right);
                    if(consider_up && consider_left && (up != left)) tracker.markEquivalent(up, left);
                    if(consider_up_right && consider_left && (up_right != left)) tracker.markEquivalent(up_right, left);
                } else {
                    tracker.include(islandCount);
                    grid[y][x] = islandCount;
                    islandCount++;
                }
            }
        }
        int[] groups = tracker.groupReport();
        int maxFinalId = -1;
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                final int old = grid[y][x];
                if(old >= 0){
                    final int replace = groups[old];
                    grid[y][x] = replace;
                    if(replace > maxFinalId) maxFinalId = replace;
                }
            }
        }
        return maxFinalId + 1;
    }
}