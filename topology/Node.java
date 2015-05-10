package topology;
import java.util.ArrayList;


public class Node implements Comparable<Node>{

	String name;
	ArrayList<NeighborAndCostStrings> neighbors;
	int bestCost;
	String originNextHop;

	public Node(String name){
		this.name = name;
		neighbors = new ArrayList<NeighborAndCostStrings>();
		bestCost = Integer.MAX_VALUE;
		originNextHop = "";

	}

	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}

	public void setBestCost(int bestCost){
		this.bestCost = bestCost;
	}
	public int getBestCost(){
		return bestCost;
	}

	public void setOriginNextHop(String originNextHop){
		this.originNextHop = originNextHop;
	}

	public String getOriginNextHop(){
		return originNextHop;
	}

	public NeighborAndCostStrings getNeighbor(int index){
		return neighbors.get(index);
	}

	public void addNeighbor(String neighborName, int cost){
		neighbors.add(new NeighborAndCostStrings(neighborName, cost));
	}

	public void removeNeighbor(String neighborName){
		int neighborIndex = getNeighborIndex(neighborName);
		neighbors.remove(neighborIndex);
	}

	//is the neighbor in the neighbors list
	public boolean doesNeighborExist(String name){
		for(int i = 0; i < neighbors.size(); i++){
			if(neighbors.get(i).getNeighborName().equals(name)){
				return true;
			}
		}
		return false;
	}

	public int getNeighborIndex(String name){
		for(int i = 0; i < neighbors.size(); i++){
			if(neighbors.get(i).getNeighborName().equals(name)){
				return i;
			}
		}
		return -1;
	}

	public void setNeighborCost(int index, int cost){
		neighbors.get(index).setCost(cost);
	}

	public void setNeighborArray(ArrayList<NeighborAndCostStrings> neighbors){
		this.neighbors = neighbors;
	}

	public int sizeOfNeighborList(){
		if(neighbors.size() == 0){
			return 0;			
		}else{
			return neighbors.size();
		}
	}

	public ArrayList<NeighborAndCostStrings> getNeighbors(){
		return neighbors;
	}

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

	public boolean equals(Node o){
		if(this.name.equals(o.getName()) == true){
			return true;
		}else{
			return false;
		}
	}
}
