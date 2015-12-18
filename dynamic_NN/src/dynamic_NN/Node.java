package dynamic_NN;

import java.util.ArrayList;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.random.RandomHelper;

import com.sun.media.sound.ModelDestination;
import com.vividsolutions.jts.geom.Coordinate;

public class Node { //error in the math somewhere: nodes have high activation -1 +1 and have high error but no difference in error after corrections (the weights are too high to be changed in time?)... 

	ArrayList<Edge> inEdges;
	ArrayList<Edge> outEdges;
	int id;
	//double strength;
	Coordinate coord;
	int activePotential=0;
	double xt0,xt1,fun;
	public  RealMatrix Pt;
	public  double[] previousActivity;
	public double previousSumAct;


	public Node(int i, Coordinate coordinate) {
		inEdges = new ArrayList<Edge>();
		outEdges = new ArrayList<Edge>();
		//active = 0;
		id = i;
		xt0=RandomHelper.nextDouble()-0.5;
		//strength = RandomHelper.nextDouble();
		coord = coordinate;
		previousSumAct=(RandomHelper.nextDouble()-0.5)*2;
		//activeThreshold = Parameters.activeThreshold+RandomHelper.nextDouble();
	}

	public void nodeInitialize(){

		//set learning matrix
		if(inEdges.size()>0){
			Pt = MatrixUtils.createRealIdentityMatrix(inEdges.size());
			Pt=Pt.scalarMultiply(1/Parameters.alpha); 

			//set previous activity
			previousActivity = new double[Parameters.activityLagSize];
			for(double d: previousActivity){
				d=RandomHelper.nextDouble();
			}
		}
	}

	public void step(){

		//adjust to noise
		//this.reduceNoise();     //each node simply resist change, and updates to maintain similar state to previous time (markovian)
		this.updateWeights();    //force matching to a particular function

		//new activity after adjusting			
		double Wx = 0;
		for(Edge e:inEdges){
			Wx = Wx + e.getWeight()*(e.start.getX());
		}
		xt1 =  Math.tanh(xt0 + Wx + Parameters.noise.sample());//

		//visualize node activity
		if(Math.round(xt1)>0)activePotential=1;
		if(Math.round(xt1)<0)activePotential=-1;
		if(Math.round(xt1)==0)activePotential=0;
	}



	/*****************************************methods**************************************************/


	public void reduceNoise(){
		if(inEdges.size()>0){

			if(this.getID()>100){

				//calculate error
				double e = calculateDiff();

				//create weight and active vector
				RealVector Wt = new ArrayRealVector(inEdges.size());
				RealVector rt = new ArrayRealVector(inEdges.size());
				int count = 0;
				for(Edge edge: inEdges){
					Wt.setEntry(count, edge.getWeight());
					rt.setEntry(count, Math.tanh(edge.getStartNode().getX()));
					count++;
				}

				//calculate new weights
				//RealVector Wt1 = Wt.subtract( ((RealVector) (Pt.multiply((RealMatrix) rt))).mapMultiply(e)  );
				//RealVector Wt1 = Wt.subtract((Pt.operate(rt)).mapMultiply(e));
				RealVector Wt1 = (Wt.add((Pt.operate(rt)).mapMultiply(-1*e)));

				//update weights
				int count2=0;
				for(Edge edge : inEdges){
					edge.setWeight(Wt1.getEntry(count2));
					count2++;
				}

				double et = calculateDiff();

				if(et-e>=0.0){
					System.out.println("error = "+ et +" , error diff = "+(et-e) + ", act = "+this.getX());	
				}

				//System.out.println("error = "+ et +" , error diff = "+(et-e) + ", act = "+this.getX());

				//calculate new learning
				RealMatrix Pt1 = Pt.subtract(     (Pt.operate(rt).outerProduct(rt).multiply(Pt)).scalarMultiply(1/( 1 + Pt.transpose().operate(rt).dotProduct(rt)))        );
				Pt=Pt1;

			} else {

			}
		}
	}

	public void updateWeights(){

		if(inEdges.size()>0){

			//calculate error
			double e = calculateError();

			//create weight and active vector
			RealVector Wt = new ArrayRealVector(inEdges.size());
			RealVector rt = new ArrayRealVector(inEdges.size());
			int count = 0;
			for(Edge edge: inEdges){
				Wt.setEntry(count, edge.getWeight());
				rt.setEntry(count, Math.tanh(edge.getStartNode().getX()));
				count++;
			}

			//calculate new weights
			//RealVector Wt1 = Wt.subtract( ((RealVector) (Pt.multiply((RealMatrix) rt))).mapMultiply(e)  );
			//RealVector Wt1 = Wt.subtract((Pt.operate(rt)).mapMultiply(e));
			RealVector Wt1 = (Wt.add((Pt.operate(rt)).mapMultiply(-1*e)));

			//update weights
			int count2=0;
			for(Edge edge : inEdges){ 
				edge.setWeight(Wt1.getEntry(count2));
				count2++;
			}

			double et = calculateError();

			if(et-e==0.0){
				//System.out.println("error = "+ et +" , error diff = "+(et-e) + ", act = "+this.getX());	
			}

			//System.out.println("error = "+ et +" , error diff = "+(et-e) + ", act = "+this.getX());

			//calculate new learning
			RealMatrix Pt1 = Pt.subtract(     (Pt.operate(rt).outerProduct(rt).multiply(Pt)).scalarMultiply(1/( 1 + Pt.transpose().operate(rt).dotProduct(rt)))        );
			Pt=Pt1;
		}
	}

	private double calculateError(){
		double sd = 0;
		double sumAct = 0;
		for(Edge edge:inEdges){
			sumAct=sumAct+edge.getWeight()*edge.getStartNode().getX(); 
		}
		sumAct = Math.tanh(sumAct);

		/*
		//update window
		previousActivity = addPos(previousActivity, 0, sumAct);

		//variation

		double mean = 0;
		for(double d: previousActivity){
			mean = mean + d;
		}

		for(double d: previousActivity){
			sd = sd + Math.pow(d-mean,2);
		}*/

		double time = RunEnvironment.getInstance().getCurrentSchedule().getTickCount()/100;
		double function = Math.sin(time*Math.PI);
		fun = function;

		if(this.id==1){
			//System.out.println("fun = " +function  + "  nn = "+sumAct);
		}

		sd = sumAct-function;

		//System.out.println("sd = "+sd);
		//sd=0;

		return sd;

	}

	private double calculateDiff(){
		double sd = 0;
		double sumAct = 0;
		for(Edge edge:inEdges){
			sumAct=sumAct+edge.getWeight()*edge.getStartNode().getX(); 
		}
		sumAct = Math.tanh(sumAct);



		sd = sumAct-previousSumAct;

		previousSumAct = sumAct;

		//System.out.println("sd = "+sd);
		//sd=0;

		return sd;

	}

	private static double[] addPos(double[] a, int pos, double num) {
		double[] result = new double[a.length];
		for(int i = 0; i < pos; i++)
			result[i] = a[i];
		result[pos] = num;
		for(int i = pos + 1; i < a.length; i++)
			result[i] = a[i - 1];
		return result;
	}




	/*****************************************get/set methods**************************************************/

	public void updateX(){
		xt0=xt1;
	}
	public int getID(){
		return id;
	}
	public ArrayList<Edge> getBackEdges(){
		return inEdges;
	}

	public Coordinate getCoord(){
		return coord;
	}
	public ArrayList<Edge> getOutEdges(){
		return outEdges;
	}
	public ArrayList<Edge> getInEdges(){
		return inEdges;
	}
	public double getX(){
		return xt0;
	}
	public double getFun(){
		return fun/10;
	}
	public int getActivePotential(){
		return activePotential;
	}

}
