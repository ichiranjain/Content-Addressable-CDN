package topology;

/**
 * This class keeps neighbors and there costs as a single entity 
 * @author spufflez
 *
 */
public class NeighborAndCostStrings {

	String neighborName;
	int cost;


	/**
	 * Constructor
	 * @param neighborName
	 * @param cost
	 */
	public NeighborAndCostStrings(String neighborName, int cost){
		this.neighborName = neighborName;
		this.cost = cost;
	}

	/**
	 * Get the neighbor ID
	 * @return neighbor ID
	 */
	public String getNeighborName() {
		return neighborName;
	}

	/**
	 * Set the neighbor ID
	 * @param neighborName
	 */
	public void setNeighborName(String neighborName) {
		this.neighborName = neighborName;
	}

	/**
	 * Get the neighbor cost
	 * @return neighbor cost
	 */
	public int getCost() {
		return cost;
	}


	/**
	 * Set the neighbor Cost
	 * @param cost
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * used for printing the neighbor and cost
	 */
	@Override
	public String toString(){
		String string = "Neighbor Name: " + neighborName + " Cost: " + cost;
		return string;
	}

}
