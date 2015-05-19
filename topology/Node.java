package topology;
import java.util.ArrayList;


/**
 * This class is a node to be used in the graph. Cache servers are represented</br>
 * as a node in a graph.</br>
 * 
 * name: is the ID of the cache server</br>
 * neighbors: are the neighbors the cache server has</br>
 * bestCost: is the best Cost to reach the node</br>
 * originNextHop: this is the next hop this cache server should use to reach a </br>
 * the specified node
 * @author spufflez
 *
 */
public class Node implements Comparable<Node>{

	String name;
	ArrayList<NeighborAndCostStrings> neighbors;
	int bestCost;
	String originNextHop;

	/**
	 * Constructor
	 * @param name
	 */
	public Node(String name){
		this.name = name;
		neighbors = new ArrayList<NeighborAndCostStrings>();
		bestCost = Integer.MAX_VALUE;
		originNextHop = "";

	}

	/**
	 * Set the ID of the node
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 * Get the ID of the cache server this node represents node 
	 * @return ID of the cache server
	 */
	public String getName(){
		return name;
	}

	/**
	 * Set the best cost to this node 
	 * @param bestCost
	 */
	public void setBestCost(int bestCost){
		this.bestCost = bestCost;
	}
	/**
	 * Get the best cost value to reach this node
	 * @return best cost value
	 */
	public int getBestCost(){
		return bestCost;
	}

	/**
	 * Set the origin Next Hop
	 * @param originNextHop
	 */
	public void setOriginNextHop(String originNextHop){
		this.originNextHop = originNextHop;
	}

	/**
	 * Get the origin next hop, this is the next hop that should be used to reach this node
	 * @return origin next hop 
	 */
	public String getOriginNextHop(){
		return originNextHop;
	}

	/**
	 * Get the neighbor at the given index
	 * @param index
	 * @return neighbor and its cost
	 */
	public NeighborAndCostStrings getNeighbor(int index){
		return neighbors.get(index);
	}

	/**
	 * Add a neighbor to the list of neighbors the node has
	 * @param neighborName
	 * @param cost
	 */
	public void addNeighbor(String neighborName, int cost){
		neighbors.add(new NeighborAndCostStrings(neighborName, cost));
	}

	/**
	 *Remove a neighbor from the list of neighbors the node has
	 * @param neighborName
	 */
	public void removeNeighbor(String neighborName){
		int neighborIndex = getNeighborIndex(neighborName);
		neighbors.remove(neighborIndex);
	}

	/**
	 * Check if the given neighbors is in the neighbors list
	 * @param name
	 * @return true if the neighbor exists and false if dne
	 */
	public boolean doesNeighborExist(String name){
		for(int i = 0; i < neighbors.size(); i++){
			if(neighbors.get(i).getNeighborName().equals(name)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the index of a neighbor in the list
	 * @param name
	 * @return index of the neighbor and -1 if dne
	 */
	public int getNeighborIndex(String name){
		for(int i = 0; i < neighbors.size(); i++){
			if(neighbors.get(i).getNeighborName().equals(name)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Set the cost for a given neighbor
	 * @param index
	 * @param cost
	 */
	public void setNeighborCost(int index, int cost){
		neighbors.get(index).setCost(cost);
	}

	/**
	 * Set the neighbors array for a given node
	 * @param neighbors
	 */
	public void setNeighborArray(ArrayList<NeighborAndCostStrings> neighbors){
		this.neighbors = neighbors;
	}

	/**
	 * Get the size of the neighbors list
	 * @return size of the neighbors lsit
	 */
	public int sizeOfNeighborList(){
		if(neighbors.size() == 0){
			return 0;			
		}else{
			return neighbors.size();
		}
	}

	/**
	 * Get the array list of neighbors
	 * @return array list of neighbors
	 */
	public ArrayList<NeighborAndCostStrings> getNeighbors(){
		return neighbors;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * compare 2 nodes 
	 */
	@Override
	public int compareTo(Node o) {
		if(this.bestCost == o.bestCost){
			return 0;
		}else if(this.bestCost > o.bestCost){
			return 1;
		}else{
			return -1;
		}
	}

	/**
	 * check if 2 nodes are equal 
	 * @param o
	 * @return
	 */
	public boolean equals(Node o){
		if(this.name.equals(o.getName()) == true){
			return true;
		}else{
			return false;
		}
	}
}
