/**
 * GridEnvir.java
 * This class implements the methods for the dynamics of a Grid World
 * environment for a rectangular grid that is actually wrapped around
 * to form a continuous torus.  It is assumed that from each location
 * on the grid, there are always four potential moves; these moves
 * correspond to the four cardinal directions.
 *
 * Created: Fri May 31 07:57:05 2002
 *
 * @author Amy J. Kerr and Todd Neller
 * @version 1.1 */
public class GridEnvir extends Environment {

    /**
     * A constant representing the reward received for each transition
     * up until and including the transition into the goal state.  */    
    public static final double REWARD = -1;
    
    /**
     * The GridState representing the current position of the agent on
     * the game board. */
    private GridState currState;


    /** 
     * The number of steps taken in the current simulation trial.
     * This integer is reset in the startTrial() method and
     * incremented in the step() method. */
    public int numStepsInTrial = 0;    
    

     /**
     * This method initializes a <code>GridEnvir</code> instance; it
     * actually does nothing.  The method makes any needed
     * data-structures and resets the environment to its original,
     * naive condition.  Normally, this method is called once when the
     * simulation is first assembled and initialized.  The agent
     * corresponding to this class' <code>Simulation</code> instance,
     * sim, is not guaranteed to be initialized when this method is
     * called.
     * 
     * @param a a <code>Object[]</code> value */
    public void init(Object[] a){
    }    

        
    /**
     * Performs any needed initialization of the environment to
     * prepare it for the beginning of a new trial; returns the first
     * <code>State</code> of the new trial.
     *
     * @return a <code>State</code> instance, the first 
     * <code>GridState</code> of the new trial.  
     */
    public State startTrial(){
	currState = new GridState();
	numStepsInTrial = 0;
	return currState;
    }
    
    /**
     * Causes the environment to undergo a transition from its current
     * state to a next state by taking the <code>Action</code> act.
     * The method returns an <code>ActionResult</code> instance
     * describing the numerical payoff and the next
     * <code>GridState</code> for taking the given action in the
     * current state.  The method is called once by the
     * <code>Simulation</code> instance on each step of the
     * simulation.
     * 
     * @param act the <code>Action</code> taken in the current state;
     * must be a <code>GridAction</code> instance.
     * @return an <code>ActionResult</code> instance describing the
     * resulting <code>GridState</code> and reward for taking the given
     * action. */
    public ActionResult step( Action act ){
	// Take Action act and store the resulting GridState
	// as the (new) current states 
	currState = (GridState) currState.clone();
	currState.move((GridAction) act);
	numStepsInTrial++;

	return new ActionResult(currState, REWARD);
    }

}// GridEnvir













