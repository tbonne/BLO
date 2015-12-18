package dynamic_NN;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Parameters {
	
	//List of parameters to set for each run
	
	//system
	public final static int numThreads = 1;
	
	//landscape
	public final static int landscapeSize = 100;
	
	//nodes
	public final static int numberOfNodes = 800;
	public final static int numberOfInputNodes = 2;
	public final static NormalDistribution noise = new NormalDistribution(0,0.001);
	public final static int activityLagSize = 5;
	public final static double alpha = 0.9; //learning rate
	
	//public final static double activeThreshold = 0.8;
	//public final static double randomActiveP = 0.05; 
	// active threshold is plus a random number (0,1)
	
	//edges
	public final static int numberOfEdges = 850;
	public final static int numberOfInputEdgesPerInputNode = 10;
	public final static double g = 2.5;
	public final static double pc = 0.1;
	public final static NormalDistribution edgeWeightD = new NormalDistribution(0,g*(Math.pow(pc*numberOfNodes,0.5)));
	
	//public final static double pulseSpeed = 0.1;
	//public final static double pulseStrength = 0.2;
	//public final static double edgeWeightUp = 0.1;
	//public final static double edgeWeightDown = 0.01;
	

}
