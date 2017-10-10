
/**
 * MtnCarSimulation.java - Sutton and Barto Example 8.2
 *
 * Created: Sun Mar 30 13:45:53 2003
 *
 * @author Todd W. Neller
 * @version 1.0
 */

public class MtnCarSimulation extends Simulation {
    static boolean verbose = false;
    static final int MAX_STEPS = 10000;

    public MtnCarSimulation(Agent agt, Environment env) {
	super(agt, env);
    }

    public void collectData(State state, Action act, State nextState, double reward) {
	if (verbose) 
	    System.out.println(state + "\t" 
			       + (((MtnCarAction) act).actNum - 1));
    }
    
    public static void main(String[] args){
	final double ALPHA = .05;
	final double EPSILON = .01; // try 0, .01, .1, .2
	final double GAMMA = 1.0;
	final double LAMBDA = .9;
	final int TRIALS = 1000;
	final int REPORTS = 100;
	MtnCarEnvironment env = new MtnCarEnvironment();
	MtnCarAgentLGDSarsaTCM agt = new MtnCarAgentLGDSarsaTCM(ALPHA, EPSILON, GAMMA, LAMBDA);
	agt.rng.setSeed(0);
	MtnCarSimulation sim = new MtnCarSimulation(agt, env);
	sim.init(null);
	System.out.println(agt);
	for (int i=0; i<REPORTS; i++) {
	    sim.trials(TRIALS/REPORTS, MAX_STEPS, 1);
	    System.out.println();
	    System.out.println(agt);
	}
	verbose = true;
	agt.epsilon = 0;
	sim.trials(1, MAX_STEPS, 1);
    }
} // MtnCarSimulation
