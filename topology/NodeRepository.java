package topology;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class NodeRepository {

	ConcurrentHashMap<String, Node> graph;
	String thisMachinesName;

	int nodeCounter;
	ConcurrentHashMap<Integer, Long> convergenceHM;

	public NodeRepository(String thisMachinesName){
		this.thisMachinesName = thisMachinesName;
		graph = new ConcurrentHashMap<String, Node>();
		convergenceHM = new ConcurrentHashMap<Integer, Long>();
		nodeCounter = 0;
	}

	public boolean HMdoesNodeExist(String nodeName){
		if (graph.containsKey(nodeName) == true) {
			return true;
		}else{
			return false;
		}
	}

	//add the node if it does not exist
	public void HMaddNode(String nodeName){
		graph.putIfAbsent(nodeName, new Node(nodeName));
		nodeCounter++;
		convergenceHM.putIfAbsent(nodeCounter, System.currentTimeMillis());
	}

	//remove the node if it exists, else this method does nothing
	public void HMremoveNode(String nodeName){
		graph.remove(nodeName);
	}

	//return the specified node
	public Node HMgetNode(String nodeName){
		return graph.get(nodeName);
	}

	//get the size of the Hash map storing the nodes
	public int HMsizeOfGraph(){
		return graph.size();
	}

	//add a neighbor to a node if the neighbor does not exists
	public void HMaddNeighbor(String nodeName, String neighborName, int cost){
		if(graph.get(nodeName).doesNeighborExist(neighborName) == false){			
			graph.get(nodeName).addNeighbor(neighborName, cost);
		}
	}

	//remove a neighbor from a node only if the neighbor is present
	public void HMremoveNeighbor(String nodeName, String neighborName){
		if(graph.get(nodeName).doesNeighborExist(neighborName) == true){			
			graph.get(nodeName).removeNeighbor(neighborName);
		}
	}

	//set the neighbors array for a node to a new array of neighbors
	public void HMsetNeighborList(String nodeName, ArrayList<NeighborAndCostStrings> neighbors){
		graph.get(nodeName).setNeighborArray(neighbors);
	}

	//get the hash map of neighbors
	public ConcurrentHashMap<String, Node> getGraph(){
		return graph;
	}

	//return this machines name
	public String getThisMachinesName(){
		return thisMachinesName;
	}

	public void setThisMachinesName(String thisMachinesName){
		this.thisMachinesName = thisMachinesName;
	}

	public ArrayList<Node> getGraphList(){

		ArrayList<Node> graphList = new ArrayList<Node>();
		Set<String> keys = graph.keySet();
		for(String key : keys){
			graphList.add(graph.get(key));
		}

		return graphList;
	}

}
