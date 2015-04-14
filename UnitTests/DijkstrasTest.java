package UnitTests;

import java.util.concurrent.ConcurrentHashMap;

import topology.Dijkstras;
import topology.Node;

public class DijkstrasTest {

	ConcurrentHashMap<String, Node> graph;

	public DijkstrasTest(){
		graph = new ConcurrentHashMap<String, Node>();
	}

	public void testDijkstras(){

		Dijkstras dijk = new Dijkstras();
		dijk.runDijkstras(graph, "A");

		System.out.println("A Best cost: " + graph.get("A").getBestCost() + " next hop: " + graph.get("A").getOriginNextHop());
		System.out.println("B Best cost: " + graph.get("B").getBestCost() + " next hop: " + graph.get("B").getOriginNextHop());
		System.out.println("C Best cost: " + graph.get("C").getBestCost() + " next hop: " + graph.get("C").getOriginNextHop());
		System.out.println("D Best cost: " + graph.get("D").getBestCost() + " next hop: " + graph.get("D").getOriginNextHop());
		System.out.println("E Best cost: " + graph.get("E").getBestCost() + " next hop: " + graph.get("E").getOriginNextHop());
		System.out.println("F Best cost: " + graph.get("F").getBestCost() + " next hop: " + graph.get("F").getOriginNextHop());
		System.out.println("G Best cost: " + graph.get("G").getBestCost() + " next hop: " + graph.get("G").getOriginNextHop());
		System.out.println("H Best cost: " + graph.get("H").getBestCost() + " next hop: " + graph.get("H").getOriginNextHop());

	}

	public void generateGraph(){
		graph.put("A", new Node("A"));
		graph.get("A").addNeighbor("B", 20);
		graph.get("A").addNeighbor("D", 80);
		graph.get("A").addNeighbor("G", 90);

		graph.put("B", new Node("B"));
		graph.get("B").addNeighbor("F", 10);

		graph.put("C", new Node("C"));
		graph.get("C").addNeighbor("F", 50);
		graph.get("C").addNeighbor("H", 20);
		graph.get("C").addNeighbor("D", 10);

		//		graph.put("C", new Node("C"));
		//		ArrayList<NeighborAndCostStrings> neighbors= new ArrayList<NeighborAndCostStrings>();
		//		neighbors.add(new NeighborAndCostStrings("F", 50));
		//		neighbors.add(new NeighborAndCostStrings("H", 20));
		//		neighbors.add(new NeighborAndCostStrings("D", 10));
		//		graph.get("C").setNeighborArray(neighbors);

		graph.put("D", new Node("D"));
		graph.get("D").addNeighbor("C", 10);
		graph.get("D").addNeighbor("G", 20);

		graph.put("E", new Node("E"));
		graph.get("E").addNeighbor("B", 50);
		graph.get("E").addNeighbor("G", 30);

		graph.put("F", new Node("F"));
		graph.get("F").addNeighbor("C", 10);
		graph.get("F").addNeighbor("D", 40);

		graph.put("G", new Node("G"));
		graph.get("G").addNeighbor("A", 20);

		graph.put("H", new Node("H"));

	}

}
