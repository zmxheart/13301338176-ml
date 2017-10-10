/**
 * GridSim.java 
 * This class runs simulations of Grid World using SARSA tabular and
 * neural network TD-control algorithms
 *
 * @author Amy J. Kerr and Todd Neller
 * @version 1.0 
 */
public class GridSim extends Simulation {

    /**
     * Creates a new <code>GridSim</code> instance, which manages
     * the interaction between a Grid World-specific agent & a
     * <code>GridEnvir</code> instance.
     *
     * @param agt an <code>Agent</code> subclass that is tailored to
     * the Grid World task.
     * @param env an <code>Environment</code> value; must be a
     * <code>GridEnvir</code> instance */
    public GridSim( Agent agt, Environment env ){
	super(agt, env);
    }
    

    /**
     * Prints a graphical representation of the board with the current
     * STATE (not state-action) value estimates; the method is called
     * regularly by the step() method, but the data is only printed at
     * the end of a trial.
     *
     * @param state a <code>State</code> value. Should not be
     * mutated within this method.
     * @param act an <code>Action</code> value. Should not be
     * mutated within this method.
     * @param nextState a <code>State</code> value. Should not be
     * mutated within this method.
     * @param reward a <code>double</code> value. Should not be
     * mutated within this method.  */
    public void collectData( State state, Action act, State nextState, 
			     double reward ){
	System.out.println(state + " " + act + " " + reward + " " + nextState);
	if(nextState.isTerminal()){ // end of trial

	    System.out.println("\nFound goal in " 
			       + ((GridEnvir) env).numStepsInTrial
			       + " steps.");	    
	    System.out.println(agt);
	}
    }


    public static void main(String[] args){
	double gamma = 1.0;
	double epsilon = .01;
	double alpha = .01;
	double lambda = .9; 
	
	int numTrials = 10000;
	int maxStepsPerTrial = 100;
	
	// the trials method will call collectData() only when the
	// trial number is divisible by printDivisor
	int printDivisor = 1000;

	//Agent agt = new TDATabSARSAGrid(epsilon, alpha, gamma, lambda);
	//Agent agt = new TDATabQLGrid(epsilon, alpha, gamma, lambda);
	//Agent agt = new SimpleGridAgentSARSA(epsilon, alpha, gamma, lambda);
	Agent agt = new SimpleGridAgentQL(epsilon, alpha, gamma, lambda);
	    
	Environment env = new GridEnvir();
	
	GridSim sim = new GridSim(agt, env);
  
	System.out.println("Number of trials per simulation: " + numTrials);
	System.out.println("Parameters: epsilon = " + epsilon + " alpha = " +
			   alpha + " gamma = " + gamma + " lambda =" + lambda);
	sim.init(null);
	sim.trials(numTrials, maxStepsPerTrial, printDivisor);
    }
}












