import java.util.Random;

/**
 * TileCodingMemory.java - general Tile Coding Memory for
 * continuous variable inputs.
 *
 * Created: Thu Nov  7 14:46:37 2002
 *
 * @author Todd Neller
 * @version 1.0 */

public class TileCodingMemory implements Featurizer {
    public int dims; // number of continuous variable inputs
    public double[] lowerBounds; // input lower bounds
    public double[] upperBounds; // input upper bounds
    public int[] partitions; // number of partitions for each input
    public double[] tileWidths; // resulting widths of tiles in each dimension
    public int numTiles; // total number of tiles (features) for each tiling
    public int numTilings; // number of tilings
    public double[][] tilingOffsets; // how much each dimension is randomly offset for each tiling
    public int numFeatures = 0; // total number of features
    public double[] features; // current present features ...
    public double[] featureWeights; // ... and associated weights
    
    public TileCodingMemory(double[] lowerBounds, double[] upperBounds,
			    int[] partitions, int numTilings) {
	// allocate and initialize TCM data structures
	dims = lowerBounds.length;
	this.lowerBounds = (double[]) lowerBounds.clone();
	this.upperBounds = (double[]) upperBounds.clone();
	this.partitions = (int[]) partitions.clone();
	tileWidths = new double[dims];
	for (int i = 0; i < dims; i++)
	    // Because of the random offset, the last non-offset
	    // partition is beyond the input space.
	    tileWidths[i] = (upperBounds[i] - lowerBounds[i])
		/ (partitions[i] - 1); 
	this.numTilings = numTilings;
	tilingOffsets = new double[numTilings][dims];
	Random rng = new Random(0); // for predictability
	// Possible improvement: make sure tilings are maximally "dispersed" from each other for greatest benefit
	for (int i = 0; i < numTilings; i++)
	    for (int j = 0; j < dims; j++)
		tilingOffsets[i][j] = rng.nextDouble();
	// compute the total number of features (tiles * tilings)
	numTiles = 1;
	for (int i = 0; i < partitions.length; i++)
	    numTiles *= partitions[i];
	numFeatures = numTiles * numTilings;

	// allocate features and weights
	features = new double[numFeatures];
	featureWeights = new double[numFeatures];
    }

    public double f(double[] input) 
    {	
	// normalize inputs to unit hypercube
	double[] scaledInput = new double[dims];
	for (int i = 0; i < dims; i++) {
	    double boundedInput
		= Math.min(upperBounds[i],Math.max(lowerBounds[i],input[i]));
	    scaledInput[i] = (boundedInput - lowerBounds[i]) / tileWidths[i];
	}
	
	// reset active features
	for (int i = 0; i < numFeatures; i++)
	    features[i] = 0.0;
	double output = 0.0;
	int baseFeature = 0;
	// For each tiling, ...
	for (int tiling = 0; tiling < numTilings; tiling++) {
	    int tile = 0;
	    int multiplier = 1;
	    // For each input, ...
	    for (int dim = 0; dim < dims; dim++) {
		// .. compute which tile is active.
		tile += multiplier
		    * (int)(scaledInput[dim] + tilingOffsets[tiling][dim]);
		multiplier *= partitions[dim];
	    }
	    features[baseFeature + tile] = 1.0; // mark tile as active
	    output += featureWeights[baseFeature + tile]; // incrementally compute output
	    baseFeature += numTiles;
	}
	return output;
    }

    public double f(State state)
    {
	return f(((DoubleArrayState) state).getDoubleArray());
    }
    
    public double[] getFeatures()
    {
	return (double[]) features.clone();
    }

    public double[] getFeatures(double[] input)
    {
	f(input);
	return (double[]) features.clone();
    }

    public double[] getFeatures(State state) 
    {
	return getFeatures(((DoubleArrayState) state).getDoubleArray());
    }
    
    
    public double[] getFeatureWeights()
    {
	return (double[]) featureWeights.clone();
    }

    public void setFeatureWeights(double[] newFeatureWeights)
    {
	for (int i = 0; i < numFeatures; i++)
	    featureWeights[i] = newFeatureWeights[i];
    }

    /**
     * <code>main</code> - test code
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args) 
    {
	Random rng = new Random();
	double[] lowerBounds = {-10.0, -100.0, -1000.0};
	double[] upperBounds = {10.0, 100.0, 1000.0};
	int[] partitions = {2, 3, 4};
	int numTilings = 3;
	TileCodingMemory tcm = 	new TileCodingMemory(lowerBounds, upperBounds, partitions, numTilings);
	double[] x = {0.0, 0.0, 0.0};
	for (int trial = 0; trial < 1000; trial++) {
	    tcm.f(x);
	    double[] features = tcm.getFeatures();
	    for (int i = 0; i < features.length; i++)
		System.out.print((features[i] == 0.0) ? "0" : "1");
	    System.out.println();
	    for (int i = 0; i < x.length; i++) {
		x[i] += rng.nextGaussian();
		x[i] = Math.max(lowerBounds[i], Math.min(upperBounds[i], x[i]));
	    }
	}
    }
}// TileCodingMemory
