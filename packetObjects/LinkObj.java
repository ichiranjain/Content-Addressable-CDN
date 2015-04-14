package packetObjects;

public class LinkObj {

	String neighboringNode;
	int cost;
	String originalPacket;

	public LinkObj(String neighboringNode, int cost){
		this.neighboringNode = neighboringNode;
		this.cost = cost;
	}

	public LinkObj(String neighboringNode){
		this.neighboringNode = neighboringNode;
	}

	public String getNeighboringNode() {
		return neighboringNode;
	}

	public void setNeighboringNode(String neighboringNode) {
		this.neighboringNode = neighboringNode;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getOriginalPacket(){
		return originalPacket;
	}

	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}


}
