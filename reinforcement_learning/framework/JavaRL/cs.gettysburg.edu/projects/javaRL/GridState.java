
/**
 * GridState.java 
 * This class constructs a representation of the state input that
 * an agent receives from a <code>GridEnvir</code> environment in Grid
 * World.
 *
 *
 * Created: Fri May 31 07:54:39 2002
 *
 * @author Amy J. Kerr and Todd Neller
 * @version 1.1 */
public class GridState implements State, Cloneable {
    
    /**
     * The integer representation of a northward action.  */
    public static final int NORTH = 0;

    /**
     * The integer representation of an eastward action.  */
    public static final int EAST = 1;

    /**
     * The integer representation of a southward action.  */
    public static final int SOUTH = 2;

    /**
     * The integer representation of a westward action.  */
    public static final int WEST = 3;

    /**
     * The number of possible actions from any state.  */
    public static final int NUM_ACTIONS = 4;    

    /**
     * A constant representing the number of rows in a particular
     * Grid World game board */
    public static final int NUM_ROWS = 5;

    /**
     * A constant representing the number of columns in a particular
     * Grid World game board */
    public static final int NUM_COLUMNS = 3;

    /**
     * A constant representing the default start row number. */
    public static final int START_ROW = 0;

    /**
     * A constant representing the default start column number. */
    public static final int START_COL = 0;

    /**
     * A constant representing the goal row number. */
    public static final int GOAL_ROW = 1;

    /**
     * A constant representing the goal column number. */
    public static final int GOAL_COL = 2;

    
    /**
     * The x-coordinate of a state (position) on the Grid World grid.
     * A <code>GridState</code> instance is completely defined by
     * an ordered pair of x and y-coordinates.  */
    public int x;

    /**
     * The y-coordinate of a state (position) on the Grid World grid.
     * A <code>GridState</code> instance is completely defined by
     * an ordered pair of x and y-coordinates.  */
    public int y;
    

    /**
     * Creates a new <code>GridState</code> instance with the default
     * start row and column. */
    public GridState() {
	x = START_COL;
	y = START_ROW;
    }

    /**
     * Creates a new <code>GridState</code> instance, which is
     * represented as a two-dimensional point.
     *
     * @param xPos an <code>int</code> value, representing the
     * x-coordinate of the current state
     * @param yPos an <code>int</code> value, representing the 
     * y-coordinate of the current state
     */
    public GridState (int xPos, int yPos){ // in (x,y) order
	x = xPos;
	y = yPos;
    }

    /**
     * Returns whether or not this is the goal position (the terminal
     * state).
     *
     * @return a <code>boolean</code> value
     */
    public boolean isTerminal() 
    {
	return x == GOAL_COL && y == GOAL_ROW;
    }
    
    /**
     * Checks whether two <code>GridState</code> instances are
     * identical; returns true if they are equal.
     *
     * @param state a <code>GridState</code> value
     * @return a <code>boolean</code> value */
    public boolean equals(GridState state){
	return (this.x == state.x && this.y == state.y);
    }    

    public void move(GridAction act) 
    {
	final int[] DX = {0, 1, 0, -1};
	final int[] DY = {-1, 0, 1, 0};

	x = (x + DX[act.dir] + NUM_COLUMNS) % NUM_COLUMNS;
	y = (y + DY[act.dir] + NUM_ROWS) % NUM_ROWS;
    }
    
    
    public Object clone() 
    {
	return new GridState(x, y);
    }
    

    public String toString() 
    {
	return "(" + x + "," + y + ")";
    }
}// GridState

