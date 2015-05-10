package topology;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Dijkstras {

	public Dijkstras(){

	}

	//might want to take a NodeRepository vs a graph
	public void runDijkstras(ConcurrentHashMap<String,Node> graph, String origin){	

		//used to get the least cost node while running Dijkstra's algorithm
		PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>();

		//
		HashMap<String, Boolean> visitedHM = new HashMap<String, Boolean>();

		//process origin node first 
		Node currentNode = graph.get(origin);

		//graph.get(origin).

		//the origin sets its cost to zero
		currentNode.setBestCost(0);

		//the origin sets it self as the next hop 
		currentNode.setOriginNextHop(currentNode.getName());

		visitedHM.put(currentNode.getName(), true);

		//the origin places its neighbors in the priority queue
		for(int i = 0; i < currentNode.sizeOfNeighborList(); i++){

			String neighborName = currentNode.getNeighbor(i).getNeighborName();

			//check if the neighbor exists

			//get the origins nodes neighbor at index "i"
			//check if it exists, get the name
			//set the neighbors origin next hop to itself
			graph.get(neighborName).setOriginNextHop(neighborName);

			//get the origins nodes neighbor at index "i"
			//set the neighbors best cost to the best cost for the origin to reach the neighbor
			graph.get(neighborName).setBestCost(currentNode.getNeighbor(i).getCost());

			//add the nodes name to the visited hash map and mark as visited
			visitedHM.put(neighborName, true);

			//add the neighbor to the queue 
			priorityQueue.add(graph.get(neighborName));

		}

		//loop while the priority queue is not empty
		while(priorityQueue.isEmpty() == false){

			//get the node with the lowest cost from the priority queue
			currentNode = priorityQueue.poll();

			//place the current nodes neighbors in the priority queue
			for(int i = 0; i < currentNode.sizeOfNeighborList(); i++){

				//check the hash map to see if the neighbor was visited 
				if(visitedHM.containsKey(currentNode.getNeighbor(i).getNeighborName()) == true){
					//if the neighbor has been visited 

					//check if the neighbor is in the priority queue
					if(priorityQueue.contains(graph.get(currentNode.getNeighbor(i).getNeighborName())) == true){
						//if the neighbor is still in the priority queue
						//check if its best cost is greater then your best cost plus the cost of the link
						//if the calculated best cost is better then the neighbors current best cost, replace it 
						if( graph.get(currentNode.getNeighbor(i).getNeighborName()).getBestCost() > (currentNode.getNeighbor(i).getCost() + currentNode.getBestCost()) ){

							//remove neighbor from the priority queue
							priorityQueue.remove(graph.get(currentNode.getNeighbor(i).getNeighborName()));

							//update origin next hop
							//get the current nodes neighbor at index "i"
							//get the nodeWrapper for the neighbor
							//set the neighbors origin next hop to the current nodes next hop
							graph.get(currentNode.getNeighbor(i).getNeighborName()).setOriginNextHop(currentNode.getOriginNextHop());

							//update best cost
							//get the current nodes neighbor at index "i"
							//get the nodeWrapper for the neighbor
							//set the neighbors best cost to the best cost of the current node plus the cost to reach the neighbor
							graph.get(currentNode.getNeighbor(i).getNeighborName()).setBestCost(currentNode.getNeighbor(i).getCost() + currentNode.getBestCost());

							//add to the priority queue
							priorityQueue.add(graph.get(currentNode.getNeighbor(i).getNeighborName()));
						}
					}

				}else{
					//the neighbor has never been visited 

					//add the neighbor's name to the visited hash map and mark as visited
					visitedHM.put(currentNode.getNeighbor(i).getNeighborName(), true);

					//get the current nodes neighbor at index "i"
					//get the nodeWrapper for the neighbor
					//set the neighbors origin next hop to the current nodes next hop
					graph.get(currentNode.getNeighbor(i).getNeighborName()).setOriginNextHop(currentNode.getOriginNextHop());

					//get the current nodes neighbor at index "i"
					//get the nodeWrapper for the neighbor
					//set the neighbors best cost to the best cost of the current node plus the cost to reach the neighbor
					graph.get(currentNode.getNeighbor(i).getNeighborName()).setBestCost(currentNode.getNeighbor(i).getCost() + currentNode.getBestCost());

					//add the neighbor to the queue 
					priorityQueue.add(graph.get(currentNode.getNeighbor(i).getNeighborName()));
				}
			}
		}//end while

		//check if all nodes were visited 
		Set<String> keys = graph.keySet();
		for(String key : keys){
			if(visitedHM.containsKey(key) == false){
				//set the best cost to the node to infinity, because no links 
				//go into the node ... there for it is unreachable
				graph.get(key).setBestCost(Integer.MAX_VALUE);
				graph.get(key).setOriginNextHop("");
				graph.remove(key);

			}
		}
	}
}
