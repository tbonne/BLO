package dynamic_NN;

import java.util.ArrayList;
import java.util.Collections;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;

public class ModelSetup implements ContextBuilder<Object>{
	
	private static Context mainContext;
	private static Geography geog;
	public static ArrayList<Node> allNodes;
	public static ArrayList<Node> allInputNodes;
	public static ArrayList<Edge> allEdges;
	//public static ArrayList<Pulse> allPulses;
	public static ArrayList<Learning> allLearning;
	
	private static final int nodeSize = Parameters.numberOfNodes;
	private static final int inputNodeSize = Parameters.numberOfInputNodes;
	private static final int edgeSize = Parameters.numberOfEdges;
	private static final int landSize = Parameters.landscapeSize;

	public Context<Object> build(Context<Object> context){
		
		System.out.println("Running BLO object");

		/********************************
		 * 								*
		 * initialize model parameters	*
		 * 								*
		 *******************************/

		mainContext = context; //static link to context
		allNodes = new ArrayList<Node>();	
		allInputNodes = new ArrayList<Node>();	
		allEdges = new ArrayList<Edge>();	
		//allPulses = new ArrayList<Pulse>();	
		allLearning = new ArrayList<Learning>();	
		
		System.out.println("Building geog");

		//Create Geometry factory; used to create gis shapes (points=primates; polygons=resources)
		GeometryFactory fac = new GeometryFactory();

		GeographyParameters<Object> params= new GeographyParameters<Object>();
		GeographyFactory factory = GeographyFactoryFinder.createGeographyFactory(null);
		geog = factory.createGeography("geog", context, params);
		
		/************************************
		 * 							        *
		 * Adding Nodes to the landscape	*
		 * 							        *
		 * *********************************/
		
		System.out.println("adding nodes");
		
		for (int j = 0; j < nodeSize; j++){
			Coordinate coord = new Coordinate(RandomHelper.nextDoubleFromTo((0),(landSize)), RandomHelper.nextDoubleFromTo(0,(landSize)));
			Node node = new Node(j,coord);
			allNodes.add(node);
			context.add(node);
			Point geom = fac.createPoint(coord);
			geog.move(node,geom);
		}
		
		for (int j = 0; j < inputNodeSize; j++){
			Coordinate coord = new Coordinate(RandomHelper.nextDoubleFromTo((0),(landSize)), 0);
			Node node = new Node(j,coord);
			allInputNodes.add(node);
			context.add(node);
			Point geom = fac.createPoint(coord);
			geog.move(node,geom);
		}

		/************************************
		 * 							        *
		 * Adding edges to the landscape	*
		 * 							        *
		 * *********************************/
		
		ArrayList<Node> connectedNodes = new ArrayList<Node>();
		
		//select first node ranomly
		Collections.shuffle(allNodes);
		connectedNodes.add(allNodes.get(0));
		
		
		for (int j = 0; j < edgeSize; j++){
			
			//select random connected node
			Collections.shuffle(connectedNodes);
			Node startN = connectedNodes.get(0);
			
			//find different node
			Node endN = startN;
			
			if(connectedNodes.size()<allNodes.size()){
				//if there are still unconnected nodes
				
				while(connectedNodes.contains(endN)==true){
				Collections.shuffle(allNodes);
				endN = allNodes.get(0);
				}
					
			} else {
				//if all nodes are connected
				while(endN!=startN){
					Collections.shuffle(allNodes);
					endN = allNodes.get(0);
					}
			}
			
			//add edge
			Edge edge = new Edge(j,startN,endN);
			startN.getOutEdges().add(edge);
			endN.getInEdges().add(edge);
			allEdges.add(edge);
			context.add(edge);
			Coordinate[] coords = {edge.getStartNode().getCoord(),edge.getEndNode().getCoord()};
			LineString line = fac.createLineString(coords);
			geog.move(edge,line);
			
			//add new node to connected nodes
			if(connectedNodes.contains(endN)==false)connectedNodes.add(endN);
			
		}
		
		//initalize nodes 
		for(Node n : allNodes){
			n.nodeInitialize();
		}
		
		/************************************
		 * 							        *
		 * Adding learning component    	*
		 * 							        *
		 * *********************************/
		
		Learning l = new Learning();
		allLearning.add(l);
		
		/************************************
		 * 							        *
		 * Scheduler to synchronize runs	*
		 * 							        *
		 * *********************************/
		
		//executor takes care of the processing of the schedule
		Executor executor = new Executor();
		createSchedule(executor);
		
		return context;
		
	}
	
	
	private void createSchedule(Executor executor){

		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();

		ScheduleParameters agentStepParams_Nodes = ScheduleParameters.createRepeating(1, 1, 6); //start, interval, priority (high number = higher priority)
		schedule.schedule(agentStepParams_Nodes,executor,"processNodes");
		
		ScheduleParameters agentStepParams_Learn = ScheduleParameters.createRepeating(1, 1, 5); //start, interval, priority (high number = higher priority)
		schedule.schedule(agentStepParams_Learn,executor,"processLearning");

		//ScheduleParameters agentStepParams_Pulse = ScheduleParameters.createRepeating(1, 1, 4); //start, interval, priority (high number = higher priority)
		//schedule.schedule(agentStepParams_Pulse,executor,"processPulses");

		//ScheduleParameters agentStepParams_Clean = ScheduleParameters.createRepeating(1, 1, 3); //start, interval, priority (high number = higher priority)
		//schedule.schedule(agentStepParams_Clean,executor,"processClean");

		//ScheduleParameters agentStepParams = ScheduleParameters.createRepeating(1, 1, 2);
		//schedule.schedule(agentStepParams,executor,"envUpdate");
	}
	

}
