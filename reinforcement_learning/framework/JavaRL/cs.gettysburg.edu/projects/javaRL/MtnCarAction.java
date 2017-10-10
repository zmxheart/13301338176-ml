
/**
 * MtnCarAction.java - one of three Mountain-Car actions: BACK,
 * IDLE, or FWD
 *
 * Created: Sat Mar 29 10:44:05 2003
 *
 * @author Todd W. Neller
 * @version 1.0 */

public class MtnCarAction implements Action {
    public static final int BACK = 0;
    public static final int IDLE = 1;
    public static final int FWD = 2;
    public int actNum;
    
    public MtnCarAction(int actNum){
	this.actNum = actNum;
    }
}// MtnCarAction
