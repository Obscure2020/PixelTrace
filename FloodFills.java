public class FloodFills {
    public static void fourDirectionFill(int[][] grid, final int start_x, final int start_y, final int target, final int newVal){
        if(grid[start_y][start_x] != target) return;
        grid[start_y][start_x] = newVal;

        final int width = grid[0].length - 1;
        final int height = grid.length - 1;

        IntPointQueue points = new IntPointQueue();
        if((start_x > 0) && (grid[start_y][start_x-1] == target)){
            grid[start_y][start_x-1] = newVal;
            points.add(start_x-1, start_y);
        }
        if((start_x < width) && (grid[start_y][start_x+1] == target)){
            grid[start_y][start_x+1] = newVal;
            points.add(start_x+1, start_y);
        }
        if((start_y > 0) && (grid[start_y-1][start_x] == target)){
            grid[start_y-1][start_x] = newVal;
            points.add(start_x, start_y-1);
        }
        if((start_y < height) && (grid[start_y+1][start_x] == target)){
            grid[start_y+1][start_x] = newVal;
            points.add(start_x, start_y+1);
        }

        while(!points.isEmpty()){
            final long packed_point = points.poll();
            final int x = (int) (packed_point >>> 32);
            final int y = (int) packed_point;
            if((x > 0) && (grid[y][x-1] == target)){
                grid[y][x-1] = newVal;
                points.add(x-1, y);
            }
            if((x < width) && (grid[y][x+1] == target)){
                grid[y][x+1] = newVal;
                points.add(x+1, y);
            }
            if((y > 0) && (grid[y-1][x] == target)){
                grid[y-1][x] = newVal;
                points.add(x, y-1);
            }
            if((y < height) && (grid[y+1][x] == target)){
                grid[y+1][x] = newVal;
                points.add(x, y+1);
            }
        }
    }

    public static void eightDirectionFill(int[][] grid, final int start_x, final int start_y, final int target, final int newVal){
        if(grid[start_y][start_x] != target) return;
        grid[start_y][start_x] = newVal;

        final int width = grid[0].length - 1;
        final int height = grid.length - 1;

        boolean y_up_check = start_y > 0;
        boolean y_down_check = start_y < height;

        IntPointQueue points = new IntPointQueue();
        if((start_x > 0) && (grid[start_y][start_x-1] == target)){
            grid[start_y][start_x-1] = newVal;
            points.add(start_x-1, start_y);
            if(y_up_check && (grid[start_y-1][start_x-1] == target)){
                grid[start_y-1][start_x-1] = newVal;
                points.add(start_x-1, start_y-1);
            }
            if(y_down_check && (grid[start_y+1][start_x-1] == target)){
                grid[start_y+1][start_x-1] = newVal;
                points.add(start_x-1, start_y+1);
            }
        }
        if((start_x < width) && (grid[start_y][start_x+1] == target)){
            grid[start_y][start_x+1] = newVal;
            points.add(start_x+1, start_y);
            if(y_up_check && (grid[start_y-1][start_x+1] == target)){
                grid[start_y-1][start_x+1] = newVal;
                points.add(start_x+1, start_y-1);
            }
            if(y_down_check && (grid[start_y+1][start_x+1] == target)){
                grid[start_y+1][start_x+1] = newVal;
                points.add(start_x+1, start_y+1);
            }
        }
        if(y_up_check && (grid[start_y-1][start_x] == target)){
            grid[start_y-1][start_x] = newVal;
            points.add(start_x, start_y-1);
        }
        if(y_down_check && (grid[start_y+1][start_x] == target)){
            grid[start_y+1][start_x] = newVal;
            points.add(start_x, start_y+1);
        }

        while(!points.isEmpty()){
            final long packed_point = points.poll();
            final int x = (int) (packed_point >>> 32);
            final int y = (int) packed_point;
            y_up_check = y > 0;
            y_down_check = y < height;
            if((x > 0) && (grid[y][x-1] == target)){
                grid[y][x-1] = newVal;
                points.add(x-1, y);
                if(y_up_check && (grid[y-1][x-1] == target)){
                    grid[y-1][x-1] = newVal;
                    points.add(x-1, y-1);
                }
                if(y_down_check && (grid[y+1][x-1] == target)){
                    grid[y+1][x-1] = newVal;
                    points.add(x-1, y+1);
                }
            }
            if((x < width) && (grid[y][x+1] == target)){
                grid[y][x+1] = newVal;
                points.add(x+1, y);
                if(y_up_check && (grid[y-1][x+1] == target)){
                    grid[y-1][x+1] = newVal;
                    points.add(x+1, y-1);
                }
                if(y_down_check && (grid[y+1][x+1] == target)){
                    grid[y+1][x+1] = newVal;
                    points.add(x+1, y+1);
                }
            }
            if(y_up_check && (grid[y-1][x] == target)){
                grid[y-1][x] = newVal;
                points.add(x, y-1);
            }
            if(y_down_check && (grid[y+1][x] == target)){
                grid[y+1][x] = newVal;
                points.add(x, y+1);
            }
        }
    }
}