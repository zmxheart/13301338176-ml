import java.text.DecimalFormat;
import java.util.Random;

public class SimpleGridAgentQL extends Agent {

    /**
     * Learning parameters
     */
    public double epsilon, alpha, gamma, lambda;

    /**
     * Problem parameters
     */
    private int rows, cols, actions;

    private GridState prevState;
    private GridAction prevAction;
    
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
    protected double[][][] elig;


    /**
     * <code>rng</code> - a random number generator.
     */
    private Random rng = new Random();

    /**
     * A <code>DecimalFormat</code> instance for formatting the state
     * value estimates.  */
    private DecimalFormat df = new DecimalFormat("+00.0000;-00.0000");    

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
    public SimpleGridAgentQL(double epsilon, double alpha, double gamma, 
				double lambda){
	this.epsilon = epsilon;
	this.alpha = alpha;
	this.gamma = gamma;
	this.lambda = lambda;
	rows = GridState.NUM_ROWS;
	cols = GridState.NUM_COLUMNS;
	actions = GridState.NUM_ACTIONS;
    }


    /**
     * Initializes an instance of the agent by constructing the
     * necessary data structures for learning (including the data
     * structures for the Q-estimates and eligibility trace values).
     * 
     * @param a a <code>Object[]</code> value */
    public void init( Object[] a){
	elig = new double[rows][cols][actions];
	qEst = new double[rows][cols][actions];
    }

    public Action startTrial(State state) 
    {
	// reset eligibilities
	elig = new double[rows][cols][actions];
	prevState = (GridState) state;
	prevAction = getAction((GridState) state);
	return prevAction;
    }

    public Action step(State nextState, double reward) 
    {
	GridState state = (GridState) nextState;
	GridAction action = getAction(state);
	GridAction greedyAction = getGreedyAction(state);
	boolean greedyPicked = false;
	if (qEst[state.y][state.x][action.dir]
	    == qEst[state.y][state.x][greedyAction.dir]) {
	    greedyAction = action;
	    greedyPicked = true;
	}
	double error = reward - qEst[prevState.y][prevState.x][prevAction.dir];
	if (!state.isTerminal())
	    error += gamma * qEst[state.y][state.x][greedyAction.dir];
	elig[prevState.y][prevState.x][prevAction.dir]++;
	// or, for replacing traces:
	// elig[prevState.x][prevState.y][prevAction.dir] = 1;
	for (int row = 0; row < rows; row++)
	    for (int col = 0; col < cols; col++)
		for (int act = 0; act < actions; act++) {
		    qEst[row][col][act] += alpha * error * elig[row][col][act];
		    if (greedyPicked)
			elig[row][col][act] *= gamma * lambda;
		    else
			elig[row][col][act] = 0;
		}
	prevState = state;
	prevAction = action;
	return action;
    }
    
  
    /**
     * Return the epsilon-greedy action according to current Q-estimates.
     *
     * @param state a <code>GridState</code> value
     * @return a <code>GridAction</code> value
     */
    public GridAction getAction(GridState state) 
    {
	if (rng.nextDouble() < epsilon)
	    // random exploratory action
	    return new GridAction(rng.nextInt(actions));
	else
	    // current estimated optimal action
	    return getGreedyAction(state);
    }


    /**
     * Return the greedy action according to current Q-estimates.
     *
     * @param state a <code>GridState</code> value
     * @return a <code>GridAction</code> value
     */
    public GridAction getGreedyAction(GridState state) 
    {
	int bestAct = 0;
	double bestQ = qEst[state.y][state.x][bestAct];
	for (int act = 1; act < actions; act++)
	    if (qEst[state.y][state.x][act] > bestQ) {
		bestQ = qEst[state.y][state.x][act];
		bestAct = act;
	    }
	return new GridAction(bestAct);
    }


    public String toString() 
    {
	StringBuffer sb = new StringBuffer();
	for (int row = 0; row < rows; row++) {
	    for (int col = 0; col < cols; col++) {
		int bestAct = 0;
		double bestQ = qEst[row][col][bestAct];
		double avgQ = bestQ;
		for (int act = 1; act < actions; act++) {
		    if (qEst[row][col][act] > bestQ)
			bestQ = qEst[row][col][act];
		    avgQ += qEst[row][col][act];
		}
		avgQ /= actions;
		double vEst = epsilon * avgQ + (1 - epsilon) * bestQ;
		sb.append(df.format(vEst));
		sb.append(' ');
	    }
	    sb.append('\n');
	}
	return sb.toString();
    }

}



