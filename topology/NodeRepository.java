package topology;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class is the graph of all the nodes in the network</br>
 * 
 * this machines name is the ID of this machine
 * @author spufflez
 *
 */
public class NodeRepository {

	ConcurrentHashMap<String, Node> graph;
	String thisMachinesName;

	int nodeCounter;
	ConcurrentHashMap<Integer, Long> convergenceHM;

	/**
	 * Constructor
	 * @param thisMachinesName
	 */
	public NodeRepository(String thisMachinesName){
		this.thisMachinesName = thisMachinesName;
		graph = new ConcurrentHashMap<String, Node>();
		convergenceHM = new ConcurrentHashMap<Integer, Long>();
		nodeCounter = 0;
	}

	/**
	 * Check if a node exists in the graph
	 * @param nodeName
	 * @return true if it exists and false if it dne
	 */
	public boolean HMdoesNodeExist(String nodeName){
		if (graph.containsKey(nodeName) == true) {
			return true;
		}else{
			return false;
		}
	}


	/**
	 * add the node if it does not exist
	 * @param nodeName
	 */
	public void HMaddNode(String nodeName){
		graph.putIfAbsent(nodeName, new Node(nodeName));
		nodeCounter++;
		convergenceHM.putIfAbsent(nodeCounter, System.currentTimeMillis());
	}


	/**
	 * remove the node if it exists, else this method does nothing
	 * @param nodeName
	 */
	public void HMremoveNode(String nodeName){
		graph.remove(nodeName);
	}


	/**
	 * return the specified node
	 * @param nodeName
	 * @return node
	 */
	public Node HMgetNode(String nodeName){
		return graph.get(nodeName);
	}


	/**
	 * get the size of the Hash map storing the nodes
	 * @return size of the graph
	 */
	public int HMsizeOfGraph(){
		return graph.size();
	}


	/**
	 * add a neighbor to a node if the neighbor does not exists
	 * @param nodeName
	 * @param neighborName
	 * @param cost
	 */
	public void HMaddNeighbor(String nodeName, String neighborName, int cost){
		if(graph.get(nodeName).doesNeighborExist(neighborName) == false){			
			graph.get(nodeName).addNeighbor(neighborName, cost);
		}
	}


	/**
	 * remove a neighbor from a node only if the neighbor is present
	 * @param nodeName
	 * @param neighborName
	 */
	public void HMremoveNeighbor(String nodeName, String neighborName){
		if(graph.get(nodeName).doesNeighborExist(neighborName) == true){			
			graph.get(nodeName).removeNeighbor(neighborName);
		}
	}


	/**
	 * set the neighbors array for a node to a new array of neighbors
	 * @param nodeName
	 * @param neighbors
	 */
	public void HMsetNeighborList(String nodeName, ArrayList<NeighborAndCostStrings> neighbors){
		graph.get(nodeName).setNeighborArray(neighbors);
	}


	/**
	 * get the hash map of neighbors
	 * @return graph 
	 */
	public ConcurrentHashMap<String, Node> getGraph(){
		return graph;
	}


	/**
	 * return this machines ID
	 * @return this machines ID
	 */
	public String getThisMachinesName(){
		return thisMachinesName;
	}

	/**
	 * Set this machines ID
	 * @param thisMachinesName
	 */
	public void setThisMachinesName(String thisMachinesName){
		this.thisMachinesName = thisMachinesName;
	}

	/**
	 * Get the graph as an array list of nodes
	 * @return array list of nodes in the graph
	 */
	public ArrayList<Node> getGraphList(){

		ArrayList<Node> graphList = new ArrayList<Node>();
		Set<String> keys = graph.keySet();
		for(String key : keys){
			graphList.add(graph.get(key));
		}

		return graphList;
	}

}
