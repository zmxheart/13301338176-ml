import java.util.Vector;
import java.util.Iterator;

/**
 * GridStateIterator.java
 * A class for creating and using an iterator that returns all
 * possible GridStates an agent may receive for the Grid World
 * reinforcement learning problem.  The default implementations of the
 * methods assume that GridStates are represented as ordered (x,y)
 * pairs, and that all nonnegative x and y-coordinates less than or
 * equal to GridEnvir.NUM_COLUMNS and GridEnvir.NUM_ROWS are possible.
 * 
 * Created: Thu Jun 13 12:09:27 2002
 *
 * @author Amy J. Kerr
 * @version 1.0 */
public class GridStateIterator implements Iterator{
     
     /**
     * A vector containing all possible GridStates the agent may
     * receive as input from the environment.  */
    Vector states = new Vector();
    
    /**
     * The index of the element in the vector that should be returned
     * on the next call to next().  */
    int index = 0;
    

    /**
     * Creates a new <code>GridStateIterator</code> instance.
     *
     * @param numColumns an <code>int</code> value, the number of
     * columns in the Grid World grid
     * @param numRows an <code>int</code> value, the number of rows in
     * the Grid World grid. */
    public GridStateIterator (int numColumns, int numRows){
	for(int x = 0; x < numColumns; x++){
	    for (int y = 0; y < numRows; y++){
		states.add(new GridState(x, y));
	    }
	}
    }
    

    /**
     * Returns true if the iterator has more elements.
     *
     * @return a <code>boolean</code> value */
    public boolean hasNext(){
	if(states.size() == 0 || index == states.size())
	    return false;
	else 
	    return true;
    }


    /**
     * Returns the next element in the iterator.  Cannot be called if
     * hasNext() == false;
     *
     * @return an <code>Object</code> value, a <code>GridState</code>
     * instance.  */
    public Object next(){
	index++;
	return states.elementAt(index - 1);
	
    }

    /**
     * Removes from the underlying collection the last element
     * returned by the iterator.  This method can be called only once
     * per call to next(); it must be called immediately AFTER the
     * call to next(). */
    public void remove(){
	if(states.size() > 0){
	    index--;
	    states.remove(index);
	}
    }
}// GridStateIterator
