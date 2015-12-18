package dynamic_NN;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixFormat;
import org.apache.commons.math3.linear.RealVector;

import repast.simphony.random.RandomHelper;

public class Learning {
	
	public static double[] netActivity;
	
	public Learning(){
		netActivity = new double[Parameters.activityLagSize];
		for(double d: netActivity){
			d=RandomHelper.nextDouble();
		}
	}
	
	public void learn(){
		//get all weights into a vector
		for(Node n : ModelSetup.allNodes){
			//n.updateWeights();
			n.updateX();
		}
	}
}
