package UnitTests;

import topology.Node;

public class NodeTest {

	public NodeTest(){

	}

	public void testNode(){
		Node node0 = new Node("node0");
		node0.addNeighbor("neighbor1", 1);
		node0.setBestCost(10);
		node0.setOriginNextHop("Node100");


		Node node1 = new Node("node1");
		node1.addNeighbor("neighbor1", 1);
		node1.setBestCost(20);

		Node node2 = new Node("node0");
		node2.addNeighbor("neighbor2", 2);
		node2.setBestCost(10);

		System.out.println("node0 size of neighbors list: " + node0.sizeOfNeighborList());
		System.out.println("node0 does neighbor neighbor1 exist: " + node0.doesNeighborExist("neighbor1"));

		System.out.println("");
		System.out.println("expect -1 becuase node0 has a lower best cost ");
		System.out.println("compare node0 to node1: " + node0.compareTo(node1));
		System.out.println("is node0 equal to node1: " + node0.equals(node1));

		System.out.println("");
		System.out.println("expect 0 becuase the best costs are the same");
		System.out.println("compare node0 to node2: " + node0.compareTo(node2));
		System.out.println("is node0 equal to node2: " + node0.equals(node2));
	}

}
