public class IntPointQueueBounded extends IntPointQueue {
    private int x_min = Integer.MAX_VALUE;
    private int x_max = Integer.MIN_VALUE;
    private int y_min = Integer.MAX_VALUE;
    private int y_max = Integer.MIN_VALUE;

    public IntPointQueueBounded(int capacity){
        super(capacity);
    }

    public IntPointQueueBounded(){
        super();
    }

    public void add(final int x, final int y){
        if(x < x_min) x_min = x;
        if(x > x_max) x_max = x;
        if(y < y_min) y_min = y;
        if(y > y_max) y_max = y;
		super.add(x, y);
    }

    //The following four methods are only guaranteed to return
    //correct results BEFORE any polls are performed.

    public int x_min(){
        return x_min;
    }

    public int x_max(){
        return x_max;
    }

    public int y_min(){
        return y_min;
    }

    public int y_max(){
        return y_max;
    }
}
