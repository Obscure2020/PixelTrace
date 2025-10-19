public class IntPointQueue {
    private IntQueue back;
    private int x_min = Integer.MAX_VALUE;
    private int x_max = Integer.MIN_VALUE;
    private int y_min = Integer.MAX_VALUE;
    private int y_max = Integer.MIN_VALUE;

    public IntPointQueue(int capacity){
        back = new IntQueue(Math.max(capacity, 4) << 1);
    }

    public IntPointQueue(){
        this(32);
    }

    public void add(IntPoint pt){
        int x = pt.x;
        int y = pt.y;
        if(x < x_min) x_min = x;
        if(x > x_max) x_max = x;
        if(y < y_min) y_min = y;
        if(y > y_max) y_max = y;
        back.add(x, y);
    }

    public void add(int x, int y){
        if(x < x_min) x_min = x;
        if(x > x_max) x_max = x;
        if(y < y_min) y_min = y;
        if(y > y_max) y_max = y;
        back.add(x, y);
    }

    public IntPoint poll(){
        int back_size = back.size();
        if(back_size == 0){
            throw new IllegalStateException("IntPointQueue is empty.");
        } else if((back_size & 1) == 1){
            throw new IllegalStateException("Internal IntQueue has an odd number of elements??");
        }
        return new IntPoint(back.poll(), back.poll());
    }

    public long pollPacked(){
        int back_size = back.size();
        if(back_size == 0){
            throw new IllegalStateException("IntPointQueue is empty.");
        } else if((back_size & 1) == 1){
            throw new IllegalStateException("Internal IntQueue has an odd number of elements??");
        }
        long x_part = ((long) back.poll()) << 32;
        long y_part = Integer.toUnsignedLong(back.poll());
        return x_part | y_part;
    }

    public boolean isEmpty(){
        return back.isEmpty();
    }

    public int size(){
        return back.size() >> 1;
    }

    //Only guaranteed to be correct BEFORE any polls are performed.
    public int x_min(){
        return x_min;
    }

    //Only guaranteed to be correct BEFORE any polls are performed.
    public int x_max(){
        return x_max;
    }

    //Only guaranteed to be correct BEFORE any polls are performed.
    public int y_min(){
        return y_min;
    }

    //Only guaranteed to be correct BEFORE any polls are performed.
    public int y_max(){
        return y_max;
    }
}
