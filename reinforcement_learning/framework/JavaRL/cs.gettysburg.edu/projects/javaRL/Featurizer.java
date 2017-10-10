
/**
 * Featurizer.java
 *
 *
 * Created: Sat Jan 18 14:46:34 2003
 *
 * @author Todd Neller
 * @version 1.0
 */

public interface Featurizer {
    public double[] getFeatures(State state);
    public void setFeatureWeights(double[] newFeatureWeights);
    public double[] getFeatureWeights();
}// Featurizer
