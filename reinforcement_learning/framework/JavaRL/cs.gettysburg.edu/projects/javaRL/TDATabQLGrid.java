import java.util.Iterator;

// This class is **IDENTICAL** TO TDATabSARSAGrid.java except for the
// class it extends!

/**
 * TDATabQLGrid.java 
 * This class implements the agent's methods for learning in the game
 * Grid World using the Q-Learning temporal-difference control alogorithm;
 * this class is IDENTICAL TO TDATabSARSAGrid.java except for the class
 * it extends.  The agent seeks to learn the optimal policy by
 * approximating the optimal state-action function, Q*(state,action),
 * using look-up tables as memory and an E-greedy policy.
 *
 * Created: Fri May 31 07:03:02 2002
 *
 * @author Amy J. Kerr
 * @version 1.0 */
public class TDATabQLGrid extends TDAgentTabQL{

    /**
     * The GridEnvir instance associated with the simulation.  This
     * variable is initialized in the init() method and should not be
     * altered outside of that method. */
    private GridEnvir gridEnv;

    /**
     * The number of possible actions from a state. This value is
     * obtained from the GridEnvir instance, gridEnv.  It should not
     * be altered outside of the init() method. */
    protected int numActions;

    /**
     * The number of rows in the Grid World environment; this value is
     * obtained from the GridEnvir instance, gridEnv. It should not be
     * altered outside of the init() method. */
    protected int numRows;

    /**
     * The number of columns in the Grid World environment; this value
     * is obtained from the GridEnvir instance, gridEnv.  It should
     * not be altered outside of the init() method */
    protected int numColumns;

    /**
     * A 3-dimensional array to hold the current state-action value
     * estimates. The indices of the first and second dimensions
     * correspond to the x and y-coordinates (respectively) of a
     * GridState. The third-dimension indices correspond to the
     * numerical constants that represent the possible actions from
     * that state, as defined in the GridEnvir class. */
    public double[][][] qEst;
   
    /**
     * A 3-dimensional array to hold the eligibility trace values. The
     * indices of the first and second dimensions correspond to the x
     * and y-coordinates (respectively) of a GridState. The
     * third-dimension indices correspond to the numerical constants
     * that represent the possible actions from that state, as defined
     * in the GridEnvir class. */
    protected double[][][] eligTrace;


    /**
     * Creates a new <code>TDATabQLGrid</code> instance.
     *
     * @param epsilon a <code>double</code> value, the probability
     * that an agent explores; assume agent employs an E-greedy policy.  
     * @param alpha a <code>double</code> value, the learning rate of
     * the agent.
     * @param gamma a <code>double</code> value, the discount rate of
     * learning.  
     * @param lambda a <code>double</code> value, the trace-decay 
     * parameter.  
     */
    public TDATabQLGrid(double epsilon, double alpha, double gamma, 
			  double lambda){
	super(epsilon, alpha, gamma, lambda);
    }


    /**
     * Initializes an instance of the agent by constructing the
     * necessary data structures for learning (including the data
     * structures for the Q-estimates and eligibility trace values),
     * calls super.init() to reset the values in these data
     * structures, and initializes the agent to its naive condition in
     * any other ways.  This method is called once when the simulation
     * is first assembled and initialized.  The inherited
     * <code>Simulation</code> instance, sim, and its corresponding
     * environment are both guaranteed to be "inited" by the time the
     * agent's init() method is called.
     * 
     * @param args a <code>Object[]</code> value */
    public void init( Object[] args ){

	// obtain information about the environment
	// and store locally in class variables
	gridEnv    = (GridEnvir)sim.env;
	numColumns = GridState.NUM_COLUMNS;
	numRows    = GridState.NUM_ROWS;
	numActions = GridState.NUM_ACTIONS;

	eligTrace  = new double[numColumns][numRows][numActions];
	qEst       = new double[numColumns][numRows][numActions];
	super.init(args); // resets Q-estimates & eligib. trace values
    }

    // super class' implementation of startTrial() is sufficient
    // super class' implementation of step() is sufficient
  
    /**
     * Chooses a random GridAction from the current state.
     *
     * @param currState a <code>State</code> value, the current
     * state.  Must be a GridState instance.
     * @return the <code>Action</code> to be taken in response to the
     * current state; a <code>GridAction</code> instance.  */
    public Action getRandomAction(State currState){
	return new GridAction(rng.nextInt(numActions));
    }
    

    /**
     * Chooses the optimal GridAction (with respect to the current
     * state-action value estimates) for the current state.
     *
     * @param currState a <code>State</code> value, the current
     * state. Must be a <code>GridState</code> instance.
     * @return the <code>Action</code> to be taken in response to the
     * current state; a <code>GridAction</code> instance. */
    public Action getOptimalAction(State currState){
	return new GridAction (indexOfMax3D(qEst, 
					    ((GridState)currState).x, 
					    ((GridState)currState).y, 
					    numActions));
    }


    /**
     * Returns the current value estimate for the given state-action
     * pair.
     *
     * @param state a <code>State</code> value; must be a
     * <code>GridState</code> instance
     * @param act an <code>Action</code> value. Must be a
     * <code>GridAction</code> instance.
     * @return a <code>double</code> value, the current estimate of
     * the value of the state-action pair */
    public double getQEst( State state, Action act ){
	return qEst[((GridState)state).x]
	           [((GridState)state).y]
	           [((GridAction)act).dir];
    }

    /** 
     * Sets the state-action (Q) value for the given state-action
     * pair.
     *
     * @param state a <code>State</code> value; must be a
     * <code>GridState</code> instance
     * @param act an <code>Action</code> value; must be a
     * <code>GridAction</code> instance.
     * @param value a <code>double</code> value, the new value for the
     * state-action pair */
    public void setQEst(State state, Action act, double value ){
	qEst[((GridState)state).x]
            [((GridState)state).y]
            [((GridAction)act).dir] = value;
    }


    /**
     * Returns the current eligibility trace value for the given
     * state-action pair; if lambda == 0, returns 0.  
     *
     * @param state a <code>State</code> value; must be a
     * <code>GridState</code> instance
     * @param act an <code>Action</code> value; must be a
     * <code>GridAction</code> instance.
     * @return a <code>double</code> value, the current value estimate
     * for the state-action pair (state, act) */
    public double getElig( State state, Action act ){
	if(lambda == 0)
	    return 0;

	return  eligTrace[((GridState)state).x]
	                 [((GridState)state).y]
	                 [((GridAction)act).dir];
    }
    
    

    
    /** 
     * Sets the eligibility trace value for the given state-action
     * pair; if lambda == 0, does nothing.
     *
     * @param state a <code>State</code> value; must be a 
     *  <code>GridState</code> instance
     * @param act an <code>Action</code> value; must be a 
     * <code>GridAction</code> instance
     * @param value a <code>double</code> value, the new value for the 
     * eligibility trace of the given state-action pair */
    public void setElig(State state, Action act, double value ){
	if(lambda != 0)
	    eligTrace[((GridState)state).x][((GridState)state).y]
		     [((GridAction)act).dir] = value;
    }
    

    /**
     * Returns the third-dimension index of the maximum double value
     * in a three-dimensional array for the given first and second
     * dimension indices.  If 2 elements are equal, the smaller index
     * is returned.
     * 
     * @param arr a <code>double[][][]</code> value
     * @param stateX an <code>int</code> value, the first-dimension
     * index of the array.
     * @param stateY an <code>int</code> value, the second-dimension
     * index of the array.
     * @param n an <code>int</code> value, the length of the
     * third-dimension of the array.
     * @return an <code>int</code> value, the third-dimension index of
     * the maximal value in the array for the given first and second
     * indices */
    public int indexOfMax3D(double[][][] arr, int stateX, 
			    int stateY, int n){
	
	int maxIndex =0;
	double max = arr[stateX][stateY][0];

	for(int j = 1; j < n; j++){
	    if (max < arr[stateX][stateY][j]){
		max      = arr[stateX][stateY][j];
		maxIndex = j;
	    } 
	}
	return maxIndex;
    }

    
    /**
     * Returns a <code>GridStateIterator</code> instance containing all
     * possible states an agent may receive as output from a the
     * environment in the Grid World reinforcement learning task.
     *
     * @return an <code>Iterator</code> value */
    public Iterator getStateIterator(){
	return new GridStateIterator(numColumns, numRows);
    }
    
    
    /**
     * Returns a <code>GridActIterator</code> instance containing all
     * of the possible actions an agent can take in response to the
     * given state.
     *
     * @param state a <code>State</code> value
     * @return an <code>Iterator</code> value */
    public Iterator getActIterator(State state){
	return new GridActIterator(numActions);	
    }
}// TDATabQLGrid



