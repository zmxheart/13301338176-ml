import java.util.Random;

/**
 * MtnCarAgentLGDSarsaTCM.java - Mountain-Car Agent with Linear
 * Gradient-Descent Sarsa with Tile Coding Memory
 *
 * Created: Sat Mar 29 11:09:14 2003
 *
 * @author Todd W. Neller
 * @version 1.0 */

public class MtnCarAgentLGDSarsaTCM extends Agent {
    static final int ACTIONS = 3; // number of agent actions
    // default learning parameters
    public double alpha = .05, epsilon = 0, gamma = 1, lambda = .9;
    // tile coding memory (TCM) for each action 
    public TileCodingMemory[] actionTCM 
	= new TileCodingMemory[ACTIONS];
    public double numFeatures; // number of features for each TCM
    public double[][] elig; // eligibility for each action-feature pair
    public double[][] theta = new double[ACTIONS][]; // TCM weights for each action
    public int oldActNum; // previous action number
    public double oldQ; // Q(s,a) for previous (s,a) pair
    public double[] oldFeatures; // feature vector for previous s
    public int steps; // current time step (total time steps so far)
    Random rng = new Random(); // random number generator

    public MtnCarAgentLGDSarsaTCM() {}
    
    public MtnCarAgentLGDSarsaTCM(double alpha, double epsilon, 
				  double gamma, double lambda) {
	this.alpha = alpha;
	this.epsilon = epsilon;
	this.gamma = gamma;
	this.lambda = lambda;
    }

    /**
     * <code>init</code> - initialize TCMs and reset eligibilities
     *
     * @param a an <code>Object[]</code> value
     */
    public void init( Object[] a ) {
	double[] lowerBounds 
	    = {MtnCarState.MIN_POS, MtnCarState.MIN_VEL};
	double[] upperBounds
	    = {MtnCarState.MAX_POS, MtnCarState.MAX_VEL};
	int[] partitions = {10,10};
	int numTilings = 9;
	for (int i = 0; i < ACTIONS; i++) {
	    actionTCM[i] 
		= new TileCodingMemory(lowerBounds, upperBounds, 
				       partitions, numTilings);
	    theta[i] = actionTCM[i].featureWeights;
	}
	numFeatures = actionTCM[0].numFeatures;
	elig = new double[ACTIONS][actionTCM[0].numFeatures];
    }

    /**
     * <code>getGreedyAction</code> - return the greedy action,
     * breaking ties by preferring backward movement
     *
     * @param state a <code>MtnCarState</code> value
     * @return a <code>MtnCarAction</code> value */
    private MtnCarAction getGreedyAction(MtnCarState state) 
    {
	int bestAct = 0;
	double bestQ = actionTCM[bestAct].f(state);
	for (int act=1; act<ACTIONS; act++) {
	    double q = actionTCM[act].f(state);
	    if (q > bestQ) {
		bestQ = q;
		bestAct = act;
	    }
	}
	return new MtnCarAction(bestAct);
    }

    /**
     * <code>getAction</code> - return the e-greedy action
     *
     * @param state a <code>MtnCarState</code> value
     * @return a <code>MtnCarAction</code> value
     */
    private MtnCarAction getAction(MtnCarState state) 
    {
	if (rng.nextDouble() < epsilon) // e-greedy
	    return new MtnCarAction(rng.nextInt(3));
	else
	    return getGreedyAction(state);
    }


    /**
     * <code>startTrial</code> - reset step count and
     * eligibilities, select and record a first action
     *
     * @param state a <code>State</code> value
     * @return an <code>Action</code> value */
    public Action startTrial( State state ) 
    {
	steps = 0;
	elig = new double[ACTIONS][actionTCM[0].numFeatures];
	MtnCarAction action = getAction((MtnCarState) state);
	oldActNum = action.actNum;
	oldQ = actionTCM[oldActNum].f(state);
	oldFeatures = actionTCM[oldActNum].getFeatures(state);
	for (int i=0; i<numFeatures; i++)
	    elig[oldActNum][i] += oldFeatures[i]; // accumulating traces
	return action;
    }
    
    /**
     * <code>step</code> - learn from previous steps according to
     * linear gradient-descent SARSA, and return the next
     * e-greedy action.
     *
     * @param nextState a <code>State</code> value
     * @param reward a <code>double</code> value
     * @return an <code>Action</code> value */
    public Action step(State nextState, double reward) 
    {
	steps++;

	double delta = reward - oldQ;

	// handle terminal case
	if (nextState.isTerminal()) {
	    for (int a = 0; a < ACTIONS; a++)
		for (int f = 0; f < numFeatures; f++) {    
		    theta[a][f] += alpha * delta * elig[a][f];
		}
	    
	    System.out.println("Goal in " + steps + " steps");
	    return null;
	}
	
	// select the next action and finish computing delta
	MtnCarAction newAction = getAction((MtnCarState) nextState);
	int newActNum = newAction.actNum;
	double newQ = actionTCM[newActNum].f(nextState);
	double[] newFeatures = actionTCM[newActNum].getFeatures(nextState);
	delta += gamma * newQ;	

	// update linear weights and decay eligibilities
	for (int a = 0; a < ACTIONS; a++)
	    for (int f = 0; f < numFeatures; f++) {
		theta[a][f] += alpha * delta * elig[a][f];
		elig[a][f] *= gamma * lambda;
	    }
	oldActNum = newActNum;
	oldQ = actionTCM[newActNum].f(nextState);
	oldFeatures = actionTCM[newActNum].getFeatures(nextState);

	// replacing eligibility traces
	for (int i=0; i<numFeatures; i++)
	    if (oldFeatures[i] != 0)
		elig[oldActNum][i] = 1;

	return newAction;
    }

    /**
     * <code>toString</code> - display a sampling of the current
     * policy
     *
     * @return a <code>String</code> value */
    public String toString() 
    {
	final int SIZE = 10;
	final double D_POS = (MtnCarState.MAX_POS - MtnCarState.MIN_POS) / (SIZE - 1);
	final double D_VEL = (MtnCarState.MAX_VEL - MtnCarState.MIN_VEL) / (SIZE - 1);
	
	StringBuffer sb = new StringBuffer();
	sb.append("Action sampling (position->down, velocity->across)\n");
	sb.append("-1 back, 0 idle, 1 forward\n");
	double pos = MtnCarState.MIN_POS;
	for (int i=0; i<SIZE; i++) {
	    double vel = MtnCarState.MIN_VEL;
	    for (int j=0; j<SIZE; j++) {
		int accel = getGreedyAction(new MtnCarState(pos, vel)).actNum - 1;
		if (accel >= 0) sb.append(' ');
		sb.append(accel);
		sb.append(' ');
		vel += D_VEL;
	    }
	    sb.append('\n');
	    pos += D_POS;
	}
	return sb.toString();
    }
    

}// MtnCarAgentLGDSarsaTCM
