package dynamic_NN;

public class Runnable_Node implements Runnable {
	
Node node;
	
	Runnable_Node(Node n){
		node = n;
	}
	
	@Override
	public void run(){
		Throwable thrown = null;
	    try {
		node.step();
	    } catch (Throwable e) {
	        thrown = e;
	        System.out.println("Problem lies in node step code" + thrown);
	    } finally {
	    	return;
	    }
	}

}
