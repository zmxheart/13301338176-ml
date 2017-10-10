
/**
 * DoubleArrayState.java - An interface for implementing the input
 * that an agent receives from the environment.  These states can
 * return an array of doubles to represent the state.
 *
 *
 * Created: Fri May 31 07:54:39 2002
 *
 * @author Todd Neller
 * @version 1.0 */
public interface DoubleArrayState extends State{
    double[] getDoubleArray();
}

















