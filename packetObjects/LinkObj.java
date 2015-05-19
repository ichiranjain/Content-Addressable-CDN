package packetObjects;

/**
 * This class is used to represent a link connected to the cache server</br>
 * The overlay primarily creates these objects and sends them to the routing layer.
 * 
 * neighboringNode: is the ID of the node creating the link with this cache server</br>
 * cost: is the cost to get to the neighboring node</br>
 * original packet: is this class represented as a json string
 * @author spufflez
 *
 */
public class LinkObj {

	String neighboringNode;
	int cost;
	String originalPacket;

	/**
	 * Constructor
	 * @param neighboringNode
	 * @param cost
	 */
	public LinkObj(String neighboringNode, int cost){
		this.neighboringNode = neighboringNode;
		this.cost = cost;
	}

	/**
	 * Constructor that does not require a cost
	 * @param neighboringNode
	 */
	public LinkObj(String neighboringNode){
		this.neighboringNode = neighboringNode;
	}

	/**
	 * Get the ID of the node that forms the link with this cache server </br>
	 * This cache servers neighbor
	 * @return neighbor ID
	 */
	public String getNeighboringNode() {
		return neighboringNode;
	}

	/**
	 * Set the ID of the node that forms the link with this cache server </br>
	 * This cache servers neighbor
	 * @param neighboringNode
	 */
	public void setNeighboringNode(String neighboringNode) {
		this.neighboringNode = neighboringNode;
	}

	/**
	 * Get the cost to the neighbor 
	 * @return
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * Set the cost to the neighbor
	 * @param cost
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/**
	 * get the original json representation of this class as a packet 
	 * @return
	 */
	public String getOriginalPacket(){
		return originalPacket;
	}

	/**
	 * set the original json representation of this class as a packet 
	 * @param originalPacket
	 */
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}


}
