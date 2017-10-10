
/**
 * MtnCarState.java
 *
 * Created: Sat Mar 29 10:45:57 2003
 *
 * @author Todd W. Neller
 * @version
 */

public class MtnCarState implements DoubleArrayState, Cloneable {
    static final double MAX_POS = .6;
    static final double MIN_POS = -1.2;
    static final double MAX_VEL = .07;
    static final double MIN_VEL = -MAX_VEL;
    static final double GOAL_POS = .5;    

    public double pos = -.5;
    public double vel = 0;
    
    public MtnCarState() {}

    public MtnCarState(double pos, double vel) {
	this.pos = pos;
	this.vel = vel;
    }    

    /**
     * <code>step</code> - change accelerations and take a
     * bounded step according to the simplified physics of the
     * example.
     *
     * @param action an <code>Action</code> value */
    public void step(Action action) 
    {
	int actNum = ((MtnCarAction) action).actNum;
	vel += (actNum-1)*0.001 + Math.cos(3*pos)*(-0.0025);
	vel = Math.min(MAX_VEL, Math.max(MIN_VEL, vel));
	pos += vel;
	pos = Math.min(MAX_POS, Math.max(MIN_POS, pos));
    }
    
    /**
     * <code>isTerminal</code> - return whether or not the car has
     * reached its goal position
     *
     * @return a <code>boolean</code> value */
    public boolean isTerminal() 
    {
	return pos > GOAL_POS;
    }

    /**
     * <code>getDoubleArray</code> - return a {pos,vel} state array
     *
     * @return a <code>double[]</code> value
     */
    public double[] getDoubleArray() 
    {
	double[] stateArray = {pos, vel};
	return stateArray;
    }
    
    public Object clone() 
    {
	try {
	    return super.clone();
	}
	catch (CloneNotSupportedException e) {
	    e.printStackTrace();
	    return null;
	}
    }
    
    public String toString() 
    {
	return pos + "\t" + vel;
    }

}// MtnCarState
