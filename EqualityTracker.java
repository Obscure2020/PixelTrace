import java.util.ArrayList;

public class EqualityTracker {
    private ArrayList<Integer> jumps;
    private ArrayList<Integer> checked;

    public EqualityTracker(){
        jumps = new ArrayList<>();
        checked = new ArrayList<>();
    }

    public void include(final int id){
        while(jumps.size() <= id){
            jumps.add(-1);
        }
    }

    private int findRoot(final int id){
        final int first_parent = jumps.get(id);
        if(first_parent < 0) return id;
        final int grand_parent = jumps.get(first_parent);
        if(grand_parent < 0) return first_parent;
        int prev_jump = grand_parent;
        int root = Integer.MIN_VALUE;
        checked.clear();
        checked.add(id);
        checked.add(first_parent);
        while(true){
            final int jump = jumps.get(prev_jump);
            if(jump >= 0){
                checked.add(prev_jump);
                prev_jump = jump;
            } else {
                root = prev_jump;
                break;
            }
        }
        for(int i : checked) jumps.set(i, root);
        return root;
    }

    public void markEquivalent(final int first_id, final int second_id){
        if(first_id >= jumps.size()){
            throw new IllegalArgumentException("first_id \"" + first_id + "\" not already included.");
        }
        if(second_id >= jumps.size()){
            throw new IllegalArgumentException("second_id \"" + second_id + "\" not already included.");
        }
        if(first_id == second_id) return;
        final int first_root = findRoot(first_id);
        final int second_root = findRoot(second_id);
        if(first_root == second_root) return;
        final int left_root = Math.min(first_root, second_root);
        final int right_root = Math.max(first_root, second_root);
        jumps.set(right_root, left_root);
    }

    public int[] groupReport(){
        final int len = jumps.size();
        int[] result = new int[len];
        int nextFamily = 0;
        for(int i=0; i<len; i++){
            final int jump = jumps.get(i);
            if(jump < 0){
                result[i] = nextFamily;
                nextFamily++;
            } else {
                result[i] = result[jump];
            }
        }
        return result;
    }
}
