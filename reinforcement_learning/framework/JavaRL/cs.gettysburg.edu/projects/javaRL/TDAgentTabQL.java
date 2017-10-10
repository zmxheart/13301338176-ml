/**
 * This abstract class implements the agent's methods for learning the
 * optimal policy for a reinforcement learning problem using Watkin's
 * Q(lambda)-learning on-policy control algorithm with eligibility
 * traces and tabular memory.
 *
 * Created: Fri May 31 07:03:02 2002
 *
 * @author Amy J. Kerr
 * @version 1.0 */
public abstract class TDAgentTabQL extends TDAgentTab{
    
    /**
     * A class boolean variable; set to true if the agent's next
     * selected action is (one of) the optimal action(s) for the
     * current state.  The boolean is set in getTDError() and should
     * not be mutated until the corresponding call to updateElig() is
     * made for the particular iteration of learning.  */
    boolean optActSelected = false;

    /**
     * Creates a new <code>TDAgentTabQL</code> instance, a TDAgent
     * that learns using Watkin's Q(lambda)-learning control algorithm.
     *
     * @param epsilon a <code>double</code> value, the probability
     * that the agent explores.  
     * @param alpha a <code>double</code> value, the learning rate of
     * the agent.
     * @param gamma a <code>double</code> value, the discount rate of
     * learning.  
     * @param lambda a <code>double</code> value, the trace-decay
     * parameter */
    public TDAgentTabQL(double epsilon, double alpha, double gamma, 
			   double lambda){
	super(epsilon, alpha, gamma, lambda);
    }
    
    /**
     * Computes the error in an estimated state-action value for
     * Watkin's Q(lambda)-learning control algorithm; sets the boolean
     * optActSelected for use in the updateElig() method.
     *
     * @param prevActResult an <code>ActionResult</code> describing
     * the consequent reward and state given to the agent for its
     * previous action.
     * @param nextAct a <code>Action</code> value, the action
     * taken in response to <code>State</code> nextState
     * @return an <code>double</code> value, the TD-error */
    public double getTDError(State nextState, double reward,
			     Action nextAct){

	State currState = nextState;
	Action optAct = getOptimalAction(currState);
	
	return reward + gamma * getQEst(currState, optAct) 
	       - getQEst(prevState, prevAction);
    }


    /**
     * Decays the eligibility trace value, based on Watkin's Q(lambda)-learning
     * control algorithm, for a given state-action pair, and resets
     * the optActSelected boolean if necessary.  This method should be
     * called by the TDAgent.java method updateEst().
     * 
     * @param state a <code>State</code> value
     * @param act an <code>Aciton</code> value */
    public void decayElig(State state, Action act){
	if(optimalAction)
	    setElig(state, act, gamma * lambda * getElig(state, act));
	else
	    setElig(state, act, 0); //looks ahead only 1 step
    }
}




























