import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class SkipList<K extends Comparable<K>, E> {

    private Random rnd = new Random();
    
    private int level;
    private int size;
    
    private SkipNode head;
    
    public SkipList() {
        level = 0;
        head = new SkipNode(null, level);
        size = 0;
    }
    
    private void adjustHead(int newLevel) {
        SkipNode newHead = new SkipNode(null, newLevel);
        for (int x = 0; x <= level; x++) {
            newHead.forward[x] = head.forward[x];
        }
        head = newHead;
        level = newLevel;
    }

    private int randomLevel() {
        int lev = 0;
        while (rnd.nextInt(2) == 0) {
            lev++;
        }
        return lev;
    }
    
    public void dump() {
        System.out.println("Node has depth " + (level + 1) + ", Value (null)");
        SkipNode tempNode = head;
        for (int i = 0; i < size; i++) {
            tempNode = tempNode.forward[0];
            System.out.println("Node has depth " +
                    tempNode.forward.length + ", Value "
                    + ((KVPair) tempNode.element()).value().toString());
        }
        //skip list size
        System.out.println("SkipList size is: " + size);
    }
    
    public int getSize() {
        return size;
    }

    /** Insert a KVPair into the skiplist */
    public boolean insert(KVPair<K, E> it) {
        int newLevel = randomLevel();
        Comparable<K> k = it.key();
        if (level < newLevel)
            adjustHead(newLevel);
        @SuppressWarnings("unchecked") // Generic array allocation
        SkipNode[] update = new SkipNode[level+  1];
        SkipNode x = head;        // Start at header node
        for (int i = level; i >= 0; i--) { // Find insert position
            while ((x.forward[i] != null) && (k.compareTo(
                    ((KVPair<K, E>) (x.forward[i]).element()).key()) > 0))
                x = x.forward[i];
            update[i] = x;               // Track end at level i
        } 
        x = new SkipNode(it, newLevel);
        for (int i = 0; i <= newLevel; i++) {      // Splice into list
            x.forward[i] = update[i].forward[i]; // Who x points to
            update[i].forward[i] = x;            // Who y points to
        }
        size++;                       // Increment dictionary size
        return true;
    }
    
    public ArrayList<E[]> intersections() {
        SkipNode<E> outerIterator = head.forward[0];
        SkipNode<E> innerIterator = head.forward[0];
        ArrayList<E[]> intersectionList = new ArrayList();
        
        for (int i = 0; i < size; i++) {
            Rectangle outerRect = (Rectangle) 
                    ((KVPair) outerIterator.element()).value();
            for (int j = i; j < size; j++) {
                Rectangle innerRect = (Rectangle) 
                        ((KVPair) innerIterator.element()).value();
                
                if (i != j && outerRect.intersects(innerRect)) {
                    Rectangle[] temp = new Rectangle[2];
                    temp[0] = outerRect;
                    temp[1] = innerRect;
                    intersectionList.add((E[]) temp);
                }
                
                innerIterator = innerIterator.forward[0];
            }
            outerIterator = outerIterator.forward[0];
            innerIterator = outerIterator;
        }
        
        return intersectionList;
    }
    
    public ArrayList<E> regionSearch(int x, int y, int w, int h) {
        ArrayList<E> foundList = new ArrayList();
        
        if (x < 0 || y < 0 || w < 0 || h < 0) {
            return foundList;
        }
        
        //search for the given region
        SkipNode tempNode = head;
        for (int i = 0; i < size; i++) {
            //upate the node
            tempNode = tempNode.forward[0];
            
            //grab the current rectangle
            Rectangle rect = (Rectangle) ((KVPair) tempNode.element()).value();
            
            //check to see if no intersection
            if ( (rect.x + rect.width) < x || (x + w) < rect.x || (rect.y + rect.height) < y || (y + h) < rect.y ) {
                //do nothing
            } else {
                //there was an intersection, so add to array
                foundList.add((E)((KVPair<K,E>) tempNode.element()).value());
            }
        }
        
        return foundList;
    }
    
    public E remove(Comparable<K> key) {
        System.out.println("REMOVING " + key + "==========================");
        E removed = null;
        SkipNode x = head;
        SkipNode remove = null;
        for (int i = level; i >= 0; i--) {
            while ((x.forward[i] != null) && 
                    key.compareTo(((KVPair<K, E>) 
                            x.forward[i].element()).key()) != 0) {
                // Search until we find the first instance of a SkipNode with key
                System.out.print(".");
                x = x.forward[i];
            }
            if (x.forward[i] != null) {
                // We've found the SkipNode we want to remove. Break out of the search.
                System.out.println("Found");
                remove = x.forward[i];
                break;
            }
        }
        
        if (remove == null) {
            System.out.println("Not found");
            // The loop has run all the way through and we've found nothing.
            return null;
        }
        
        x = head;
        for (int i = remove.forward.length - 1; i >= 0; i--) {
            System.out.println("Removing");
            // Move forward in the current depth until we've found the SkipNode
            while (x.forward[i] != remove) {
                x = x.forward[i];
            }
            // Decouple the SkipNode at this level
            x.forward[i] = remove.forward[i];
        }
        
        return (E) ((KVPair<K, E>) remove.element()).value();
    }
    
    public E remove(E value) {
        
        return null;
    }
    
    public ArrayList<E> search(Comparable<K> key) {
        boolean found = false;
        SkipNode x = head;                     // Dummy header node
        for (int i = level; i >= 0; i--) {        // For each level...
            while ((x.forward[i] != null) && (key.compareTo(
                  ((KVPair<K,E>) x.forward[i].element()).key()) > 0)) { // go forward
                x = x.forward[i];              // Go one last step
            }
        }
        x = x.forward[0];  // Move to actual record, if it exists
        if ((x != null) && (key.compareTo(
                ((KVPair<K, E>) x.element()).key()) == 0)) {
            ArrayList<E> foundList = new ArrayList();            
            while ((x != null) && 
                    key.compareTo(((KVPair<K,E>) x.element()).key()) == 0) {
                foundList.add((E)((KVPair<K, E>) x.element()).value());
                x = x.forward[0];
            }
            return foundList;
        }
        else
            return null;
    }

}
