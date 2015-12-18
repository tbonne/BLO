package dynamic_NN;

import repast.simphony.random.RandomHelper;

public class Edge {
	double weight;
	Node start, end;
	int ID,pulse;
	double length=0;
	
	
	public Edge(int id, Node s, Node e){
		ID = id;
		start = s;
		end = e;
		//weight = RandomHelper.nextDouble()*Parameters.scalingWeights;
		weight = Parameters.edgeWeightD.sample();
		length=s.getCoord().distance(e.getCoord());
		pulse=0;
	}
	
	public void setWeight(double d){
		weight=d;
	}
	public Node getStartNode(){
		return start;
	}
	public double getWeight(){
		return weight;
	}
	public Node getEndNode(){
		return end;
	}
	public double getLength(){
		return length;
	}
	public int getPulse(){
		return pulse;
	}
	public void setPulse(int i ){
		pulse = i;
	}
}
