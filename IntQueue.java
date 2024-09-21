public class IntQueue {
    private int[] arr;
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    public IntQueue(int capacity){
        arr = new int[Math.max(capacity, 4)]; //Four seems a sane minimum capacity to me.
    }

    public IntQueue(){
        this(64);
    }

    private int wrapIncrement(int i){
        i++;
        //Note for future maintainers:
        //Thou shalt NOT replace the following with a modulo.
        //This is MEASURABLY faster.
        //Sincerely, Obscure (Joey)
        return (i >= arr.length) ? 0 : i;
    }

    public void add(int i){
        if(wrapIncrement(tail) == head){
            int[] newArr = new int[arr.length + Math.max(arr.length>>1, 4)];
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
        arr[tail] = i;
        tail = wrapIncrement(tail);
        size++;
    }

    public void add(int a, int b){
        add(a);
        add(b);
    }

    public int poll(){
        if(isEmpty()){
            throw new IllegalStateException("IntQueue is empty.");
        }
        int result = arr[head];
        head = wrapIncrement(head);
        size--;
        return result;
    }

    public final boolean isEmpty(){
        return head == tail;
    }

    public int size(){
        return size;
    }
}
