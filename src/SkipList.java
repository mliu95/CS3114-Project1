import java.lang.reflect.Array;
import java.util.Random;

public class SkipList<K extends Comparable<K>,E> {

    private Random rnd = new Random();
    
    private int level;
    private int size;
    
    private SkipNode head;
    
    public SkipList() {
        head = new SkipNode(null, level);
        level = -1;
        size = 0;
    }
    
    private void adjustHead(int newLevel) {
        SkipNode newHead = new SkipNode(null, newLevel);
        for(int x = 0; x < level; x++) {
            newHead.forward[x] = head.forward[x];
        }
        head = newHead;
        level = newLevel;
    }

    private int randomLevel() {
        int lev;
        for (lev = 0; rnd.nextInt(2) == 0; lev++);
        return lev;
    }
    
    public void dump() {
        System.out.println("Node has depth " + level + ", Value (null)");
        SkipNode tempNode = head;
        for (int i = 0; i < size; i++) {
            tempNode = tempNode.forward[0];
            System.out.println("Node has depth " + tempNode.forward.length + 
                    ", Value " + ((KVPair) tempNode.element()).value().toString());
        }
        //skip list size
        System.out.println("SkipList size is: " + size);
    }

    /** Insert a KVPair into the skiplist */
    public boolean insert(KVPair<K,E> it) {
        int newLevel = randomLevel();
        Comparable<K> k = it.key();
        if (level < newLevel)
            adjustHead(newLevel);
        @SuppressWarnings("unchecked") // Generic array allocation
        SkipNode[] update = new SkipNode[level+1];
        SkipNode x = head;        // Start at header node
        for (int i=level; i>=0; i--) { // Find insert position
            while((x.forward[i] != null) && (k.compareTo(((KVPair<K, E>) (x.forward[i]).element()).key()) > 0))
                x = x.forward[i];
            update[i] = x;               // Track end at level i
        } 
        x = new SkipNode(it, newLevel);
        for (int i=0; i<=newLevel; i++) {      // Splice into list
            x.forward[i] = update[i].forward[i]; // Who x points to
            update[i].forward[i] = x;            // Who y points to
        }
        size++;                       // Increment dictionary size
        return true;
    }
    
    public void search(K key) {
        
    }
}
