package packetObjects;

import java.util.ArrayList;

import topology.Node;

public class TableObj {

	//HashMap<String, Node> graph;
	String fromNode;
	ArrayList<Node> graph;

	public TableObj(String fromNode, ArrayList<Node> graph){
		this.graph = graph;
	}

	public String getFromNode(){
		return fromNode;
	}

	public void setFromNode(String fromNode){
		this.fromNode = fromNode;
	}

	public ArrayList<Node> getGraph() {
		return graph;
	}

	public void setGraph(ArrayList<Node> graph) {
		this.graph = graph;
	}

	public int sizeOfGraph(){
		return graph.size();
	}



}
