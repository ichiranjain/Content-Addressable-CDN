package topology;

public class NeighborAndCostStrings {

	String neighborName;
	int cost;
	
	public NeighborAndCostStrings(String neighborName, int cost){
		this.neighborName = neighborName;
		this.cost = cost;
	}
	
	public String getNeighborName() {
		return neighborName;
	}

	public void setNeighborName(String neighborName) {
		this.neighborName = neighborName;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String toString(){
		String string = "Neighbor Name: " + neighborName + " Cost: " + cost;
		return string;
	}

}
