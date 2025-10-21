public class IntPointQueueBounded {
    private long[] arr;
    private int head = 0;
    private int tail = 0;
    private int x_min = Integer.MAX_VALUE;
    private int x_max = Integer.MIN_VALUE;
    private int y_min = Integer.MAX_VALUE;
    private int y_max = Integer.MIN_VALUE;

    public IntPointQueueBounded(int capacity){
        arr = new long[Math.max(capacity, 4)]; //Four seems a sane minimum capacity to me.
    }

    public IntPointQueueBounded(){
        this(32);
    }

    private int wrapIncrement(int i){
        i++;
        //Note for future maintainers:
        //Thou shalt NOT replace the following with a modulo.
        //This is MEASURABLY faster.
        //Sincerely, Obscure (Joey)
        return (i >= arr.length) ? 0 : i;
    }

    public void add(final int x, final int y){
        if(x < x_min) x_min = x;
        if(x > x_max) x_max = x;
        if(y < y_min) y_min = y;
        if(y > y_max) y_max = y;
        final long cell = (((long) x) << 32) | Integer.toUnsignedLong(y);
        if(wrapIncrement(tail) == head){
            long[] newArr = new long[arr.length + Math.max(arr.length>>1, 4)];
            if(head < tail){
                //Queue contents are not wrapped around the edge.
                int len = tail - head;
                System.arraycopy(arr, head, newArr, 0, len);
                tail = len;
            } else {
                //Queue contents are wrapped around the edge, and will now be un-wrapped.
                int headLen = arr.length - head;
                System.arraycopy(arr, head, newArr, 0, headLen);
                System.arraycopy(arr, 0, newArr, headLen, tail);
                tail = tail + headLen;
            }
            head = 0;
            arr = newArr;
        }
        arr[tail] = cell;
        tail = wrapIncrement(tail);
    }

    public long poll(){
        if(isEmpty()){
            throw new IllegalStateException("IntPointQueueBounded is empty.");
        }
        long result = arr[head];
        head = wrapIncrement(head);
        return result;
    }

    public boolean isEmpty(){
        return head == tail;
    }

    public int size(){
        int diff = tail - head;
        return (diff < 0) ? diff + arr.length : diff;
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
