
/**
 * GridAction.java
 * This class creates <code>GridAction</code> objects to represent
 * the possible moves (actions) an agent can select in a Grid World game.
 *
 * Created: Fri May 31 07:28:56 2002
 *
 * @author Amy J. Kerr and Todd Neller
 * @version 1.1 */
public class GridAction implements Action{

    /**
     * An integer representation of a <code>GridAction</code>
     * instance; the <code>GridState</code> class provides constants
     * to intepret the value of dir.  */
    public int dir;


    /**
     * Creates a new <code>GridAction</code> instance to represent
     * a move in a Grid World game.
     *
     * @param direction, an <code>int</code> value representing the
     * direction of the move (action) */
    public GridAction (int direction){
	dir = direction;
    }
    
    public String toString() 
    {
	switch (dir) {
	case GridState.NORTH:
	    return "N";
	case GridState.SOUTH:
	    return "S";
	case GridState.EAST:
	    return "E";
	case GridState.WEST:
	    return "W";
	default:
	    return "?";
	}
    }
    
}// GridAction




