import java.text.DecimalFormat;
import java.util.Iterator;

/**
 * This class implements the agent's methods for learning using a
 * temporal-difference control alogorithm and look-up tables as
 * memory.  The agent seeks to learn the optimal policy by
 * approximating the optimal action-value function, Q*.
 *
 * Created: Fri May 31 07:03:02 2002
 *
 * @author Amy J. Kerr
 * @version 1.0 */
public abstract class TDAgentTab extends TDAgent{

    /**
     * A two-dimensional string array for holding the current STATE
     * value estimates for each state.  The first-dimension and
     * second-dimension indices correspond to the x and y-coordinates,
     * respectively, of the Grid World game board. The values are
     * stored as strings because they are formatted numbers */
    private String[][] stateValues
	= new String[GridState.NUM_COLUMNS][GridState.NUM_ROWS];
    
    /**
     * A <code>DecimalFormat</code> instance for formatting the state
     * value estimates.  */
    private DecimalFormat df = new DecimalFormat("+00.0000;-00.0000");
    
    /**
     * Creates a new <code>TDAgentTab</code> instance.
     *
     * @param epsilon a <code>double</code> value, the probability
     * that an agent explores.  
     * @param alpha a <code>double</code> value, the learning rate of
     * the agent.
     * @param gamma a <code>double</code> value, the discount rate of
     * learning.  
     * @param lambda a <code>double</code> value, the trace-decay 
     * parameter.  */
    public TDAgentTab(double epsilon, double alpha, double gamma, 
		      double lambda){
	super(epsilon, alpha, gamma, lambda);
    }

    
    /**
     * Updates the eligibility trace values, calls TDAgent's step()
     * method, and returns the next action in response to the current
     * state and the previous reward.  This method is called once
     * on each step of the simulation. */
    public Action step(State nextState, double reward) 
    {
	// increment the eligibility trace of the (previous) state-action
	// pair (i.e. "accumulating" eligibilities);
	if(lambda != 0)
	    setElig(prevState, prevAction, 
		    //		    getElig(prevState, prevAction) + 1);
	// To get "replacing" eligibility traces, comment the previous
	// line and uncomment the next:
		    1);
	
	return super.step(nextState, reward);
    }


    /**
     * Updates the state-action value estimates and the eligibility
     * trace values for all state-action pairs by calling the getQEst()
     * and updateElig() methods.
     *
     * @param error, the TD-error in the previous state-action pair */
    public void updateEst(double error){	
	Iterator stateIt = getStateIterator();
	Iterator actIt;
	State state;
	Action act;

	while(stateIt.hasNext()){
	    state  = (State)(stateIt.next());
	    actIt = getActIterator(state);
	    
	    while(actIt.hasNext()){
		act = (Action)(actIt.next());
		
		setQEst(state, act, getQEst(state, act) + 
			alpha * error * getElig(state, act));

		if(lambda != 0)
		    decayElig(state, act);
	    }
	}
    }
    

    /**
     * Resets the Q-estimates to 0.
     */
    public void resetEst(){
	Iterator stateIt = getStateIterator();
	Iterator actIt;
	State state;
	Action act;

	while(stateIt.hasNext()){
	    state  = (State)(stateIt.next());
	    actIt = getActIterator(state);
	    
	    while(actIt.hasNext()){
		act = (Action)(actIt.next());
		
		setQEst(state, act, 0);
	    }
	}
    }

    /**
     * Resets the eligibility trace values to 0.
     */
    public void resetElig(){
	Iterator stateIt = getStateIterator();
	Iterator actIt;
	State state;
	Action act;

	while(stateIt.hasNext()){
	    state  = (State)(stateIt.next());
	    actIt = getActIterator(state);
	    
	    while(actIt.hasNext()){
		act = (Action)(actIt.next());
		
		if(lambda != 0)
		    setElig(state, act, 0);
	    }
	}
    }


    /**
     * Returns an iterator of all possible states for a given
     * reinforcement learning problem.
     *
     * @return an <code>Iterator</code> value */
    public abstract Iterator getStateIterator();
    
    /**
     * Returns an iterator of the possible actions to be taken in
     * response to the given state.
     *
     * @param state a <code>State</code> value
     * @return an <code>Iterator</code> value */
    public abstract Iterator getActIterator(State state);
    
    /**
     * Decays the specified eligibility trace value in accordance with
     * the temporal-difference learning algorithm employed by the
     * agent.  This method should call setElig() with the appropriate
     * new value.  This method should be called by updateEst() ONLY IF the
     * learning algorithm uses eligibility traces.
     *
     * @param state a <code>State</code> value
     * @param act an <code>Action</code> value */
    public abstract void decayElig(State state, Action act);

    /**
     * Computes the current value estimate for each state and stores
     * these estimates in the 2-dimensional stateValues array.  The
     * method assumes that the agent employs an E-greedy policy; thus,
     * V(s) = (1-epsilon) Q(s,a*) + (epsilon) [sum over all actions
     * (P(s,a) Q(s,a) )] where s is the given state, a* is the optimal
     * action (with respect to current state-value estimates), epsilon
     * is the probability of exploration, and P(s,a) is the
     * probability of randomly choosing to take action a in state s.  */
    public void computeStateValues(){
	double probAction = 1.0 / GridState.NUM_ACTIONS;
	double exploitValue; // the maximal state-action estimate for the state
	double exploreValue; // the weighted sum of all state-action
	                     // estimates for the state
	double q;
	
	for(int i = 0; i < GridState.NUM_COLUMNS; i++){
	    for(int j = 0; j < GridState.NUM_ROWS; j++){

		exploitValue = getQEst(new GridState(i,j), 
				       new GridAction(0)); // k = 0
		exploreValue = probAction * exploitValue; 
		
		for(int k = 1; k < GridState.NUM_ACTIONS; k++){ // index of k begins at 1

		    q = getQEst(new GridState(i,j), new GridAction(k));

		    exploreValue += probAction * q;

		    if (q > exploitValue)
			exploitValue = q;

		    if (k == GridState.NUM_ACTIONS - 1) // calculate stateValue[i][j][k]
			stateValues[i][j] =
			    df.format((1-epsilon) * exploitValue + 
				      epsilon * exploreValue);
		}
	    }
	}
    }

    /**
     * Returns a text representation of the Grid World grid with the
     * current state-value estimates for each state.
     */
    public String drawGrid(){
	StringBuffer border, data, sides, grid = new StringBuffer();
	String sqBorder = "-----------";
	String spaces = "           ";

	// the expected number of characters in one row of the graphic
	int length = (sqBorder.length() + GridState.NUM_COLUMNS + 1) 
	             * GridState.NUM_COLUMNS;
	

	for(int j = 0; j < GridState.NUM_ROWS; j++){ // for each row

	    // initalize border, data, and sides for this row.
	    border = new StringBuffer(length).append("+").append(sqBorder).append("+");
	    data   = new StringBuffer(length).append("| ").append(stateValues[0][j]).append("  |");

	    if (j == GridState.START_ROW && 0 == GridState.START_COL)
		sides = new StringBuffer(length).append("|").append("     S     ").append("|");
	    else
		sides  = new StringBuffer(length).append("|").append(spaces).append("|");


	    for(int i = 1; i < GridState.NUM_COLUMNS; i++){ // for each column in the row

		// append to border, data & sides to form a grid square 
		// for each column in this row
		border.append(sqBorder).append("+");
		data.append(" ").append(stateValues[i][j]).append("  |");
		
		if(i == GridState.GOAL_COL && j == GridState.GOAL_ROW){
		    sides.append("     G     ").append("|");
		}
		else if (i == GridState.START_COL && j == GridState.START_ROW)
		    sides.append("     S     ").append("|");
		else 
		    sides.append(spaces).append("|");
		
		


		if(i == GridState.NUM_COLUMNS - 1){ // if done row, print it out
		    grid.append(border.toString());
		    grid.append('\n');
		    grid.append(data.toString());
		    grid.append('\n');
		    grid.append(sides.toString());
		    grid.append('\n');
		    		    
		    if(j == GridState.NUM_ROWS - 1) // if at bottom row, print out bottom border
			grid.append(border.toString() + "\n");
		}
	    }
	}
	return grid.toString();
    }
	    
    public String toString() 
    {
	computeStateValues();
	return drawGrid();
    }
}



