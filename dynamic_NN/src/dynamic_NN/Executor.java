package dynamic_NN;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/*
 * 
 * This class controls the multithreading processes, breaking the model up into 3 seperate multithreaded sequences:
 * 			1) Nodes
 * 			2) Edges
 * 			3) Pulses
 */


public class Executor {
	
	private static final int pThreads = Parameters.numThreads;
	private static ExecutorService executor;

	public Executor(){
		executor = Executors.newFixedThreadPool(pThreads);
	}

	//synchronous scheduling

	public static void processNodes(){

		
		Collection<Callable<Void>> tasks_inputs = new ArrayList<Callable<Void>>();
		for (Node n:ModelSetup.allNodes){
			Runnable worker = new Runnable_Node(n);
			tasks_inputs.add(Executors.callable(worker,(Void)null));
		}

		try {
			for (Future<?> f : executor.invokeAll(tasks_inputs)) { //invokeAll() blocks until ALL tasks submitted to executor complete
				f.get(); 
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	
	public static void processLearning(){

		for(Learning l :ModelSetup.allLearning){
			l.learn();
		}
		
		/*
		Collection<Callable<Void>> tasks_inputs = new ArrayList<Callable<Void>>();
		for (Node n:ModelSetup.allNodes){
			Runnable worker = new Runnable_Node(n);
			tasks_inputs.add(Executors.callable(worker,(Void)null));
		}

		try {
			for (Future<?> f : executor.invokeAll(tasks_inputs)) { //invokeAll() blocks until ALL tasks submitted to executor complete
				f.get(); 
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}catch (NullPointerException e){
			e.printStackTrace();
		}*/
	}

	/*public static void processPulses(){

			//inputs prior to action
			Collection<Callable<Void>> tasks_inputs = new ArrayList<Callable<Void>>();
			for (Pulse p:ModelSetup.allPulses){
				if(p!=null){
				Runnable worker = new Runnable_Pulse(p);
				tasks_inputs.add(Executors.callable(worker,(Void)null));
				}
			}

			try {
				for (Future<?> f : executor.invokeAll(tasks_inputs)) { //invokeAll() blocks until ALL tasks submitted to executor complete
					f.get(); 
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}catch (NullPointerException e){
				e.printStackTrace();
			}
	}
	
	public static void processEdges(){

			//inputs prior to action
			Collection<Callable<Void>> tasks_inputs = new ArrayList<Callable<Void>>();
			for (Edge e:ModelSetup.allEdges){
				Runnable worker = new Runnable_Edge(e);
				tasks_inputs.add(Executors.callable(worker,(Void)null));
			}

			try {
				for (Future<?> f : executor.invokeAll(tasks_inputs)) { //invokeAll() blocks until ALL tasks submitted to executor complete
					f.get(); 
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}catch (NullPointerException e){
				e.printStackTrace();
		}
	}
	
	public static void processClean(){
		//for (Pulse p : ModelSetup.removePulses){
		//	ModelSetup.allPulses.remove(p);
		//}
		ModelSetup.allPulses.removeAll(ModelSetup.removePulses); //something is up with the number of pulses > than edges...edges hummm
		ModelSetup.allPulses.removeAll(Collections.singleton(null));
		ModelSetup.removePulses.clear();
	}*/
	
	public void shutdown(){
		executor.shutdown();
	}
}
