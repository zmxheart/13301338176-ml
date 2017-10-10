
/**
 * MtnCarEnvironment.java - Mountain-Car environment; most environment-related code is embedded in the MtnCarState.
 *
 * Created: Sat Mar 29 11:00:47 2003
 *
 * @author Todd W. Neller
 * @version 1.0 */

public class MtnCarEnvironment extends Environment {
    public MtnCarState state;

    public void init( Object[] a ) {}

    public State startTrial() 
    {
	state = new MtnCarState();
	return state;
    }
    
    public ActionResult step( Action act ) 
    {
	state = (MtnCarState) state.clone();
	state.step(act);
	// reward is always -1
	return new ActionResult(state, -1);
    }

}// MtnCarEnvironment
