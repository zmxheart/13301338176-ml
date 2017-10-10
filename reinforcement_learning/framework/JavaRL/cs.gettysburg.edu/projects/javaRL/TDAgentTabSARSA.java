/**
 * This abstract class implements the agent's methods for learning the
 * optimal policy for a reinforcement learning problem using the
 * SARSA(lambda) on-policy control algorithm with accumulating
 * eligibility traces and tabular memory.
 *
 * Created: Fri May 31 07:03:02 2002
 *
 * @author Amy J. Kerr
 * @version 1.0 */
public abstract class TDAgentTabSARSA extends TDAgentTab{
    
    /**
     * Creates a new <code>TDAgentTabSARSA</code> instance, a
     * TDAgent that learns using the SARSA(lambda) control algorithm.
     *
     * @param epsilon a <code>double</code> value, the probability
     * that the agent explores.  
     * @param alpha a <code>double</code> value, the learning rate of
     * the agent.
     * @param gamma a <code>double</code> value, the discount rate of
     * learning.  
     * @param lambda a <code>double</code> value, the trace-decay
     * parameter
     */
    public TDAgentTabSARSA(double epsilon, double alpha, double gamma, 
			   double lambda){
	super(epsilon, alpha, gamma, lambda);
    }
    
    /**
     * Computes the error in a state-action value estimate for
     * the SARSA(lambda)-control algorithm. 
     *
     * @param prevActResult an <code>ActionResult</code> describing
     * the consequent reward and state given to the agent for its
     * previous action.
     * @param nextAct a <code>Action</code> value, the action
     * taken in response to <code>State</code> nextState
     * @return an <code>double</code> value, the TD-error 
     */
    public double getTDError(State nextState, double reward,
			     Action nextAct){
	return reward + gamma * 
	    getQEst(nextState, nextAct) - 
	    getQEst(prevState, prevAction);
    }


    /**
     * Decays the eligibility trace value, based on the SARSA control
     * algorithm, for a given state-action pair.  This method should
     * be called by the TDAgent.java method updateEst(). 
     * 
     * @param state a <code>State</code> value
     * @param act an <code>Aciton</code> value */
    public void decayElig(State state, Action act){
	setElig(state, act, gamma * lambda * getElig(state, act));
    }
}

























