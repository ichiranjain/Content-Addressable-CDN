package UnitTests;

import java.util.ArrayList;

import packetObjects.LinkObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;
import topology.DirectlyConnectedNodes;
import topology.FIB;
import topology.NeighborAndCostStrings;
import topology.NodeRepository;
import topology.PIT;
import topology.ProcessUpdates;
import topology.UpdateMsgsSeen;

public class UpdateProcessTest {

	NodeRepository nodeRepo;
	FIB fib;
	ProcessUpdates process;
	DirectlyConnectedNodes directlyConnectedNodes;
	UpdateMsgsSeen upDatesSeen;

	public UpdateProcessTest(){
		directlyConnectedNodes = new DirectlyConnectedNodes();
		upDatesSeen = new UpdateMsgsSeen();

		nodeRepo = new NodeRepository("A");
		fib = new FIB(nodeRepo, new PIT());

		nodeRepo.HMaddNode("A");
		nodeRepo.HMaddNeighbor("A", "B", 20);
		nodeRepo.HMaddNeighbor("A", "D", 80);
		nodeRepo.HMaddNeighbor("A", "G", 90);
		nodeRepo.HMgetNode("A").setBestCost(0);
		nodeRepo.HMgetNode("A").setOriginNextHop("A");

		nodeRepo.HMaddNode("B");
		nodeRepo.HMaddNeighbor("B", "F", 10);
		nodeRepo.HMgetNode("B").setBestCost(20);
		nodeRepo.HMgetNode("B").setOriginNextHop("B");

		nodeRepo.HMaddNode("C");
		nodeRepo.HMaddNeighbor("C", "F", 50);
		nodeRepo.HMaddNeighbor("C", "H", 20);
		nodeRepo.HMaddNeighbor("C", "D", 10);
		nodeRepo.HMgetNode("C").setBestCost(40);
		nodeRepo.HMgetNode("C").setOriginNextHop("B");

		nodeRepo.HMaddNode("D");
		nodeRepo.HMaddNeighbor("D", "C", 10);
		nodeRepo.HMaddNeighbor("D", "G", 20);
		nodeRepo.HMgetNode("D").setBestCost(50);
		nodeRepo.HMgetNode("D").setOriginNextHop("B");

		nodeRepo.HMaddNode("E");
		nodeRepo.HMaddNeighbor("E", "B", 50);
		nodeRepo.HMaddNeighbor("E", "G", 30);
		nodeRepo.HMgetNode("E").setBestCost(1000);
		nodeRepo.HMgetNode("E").setOriginNextHop("B");

		nodeRepo.HMaddNode("F");
		nodeRepo.HMaddNeighbor("F", "C", 10);
		nodeRepo.HMaddNeighbor("F", "D", 40);
		nodeRepo.HMgetNode("F").setBestCost(30);
		nodeRepo.HMgetNode("F").setOriginNextHop("B");

		nodeRepo.HMaddNode("G");
		nodeRepo.HMaddNeighbor("G", "A", 20);
		nodeRepo.HMgetNode("G").setBestCost(70);
		nodeRepo.HMgetNode("G").setOriginNextHop("B");

		nodeRepo.HMaddNode("H");
		nodeRepo.HMgetNode("H").setBestCost(60);
		nodeRepo.HMgetNode("H").setOriginNextHop("B");

		process = new ProcessUpdates(nodeRepo, upDatesSeen, fib, directlyConnectedNodes);
	}

	public void testUpdateProcess(){

		String doNotSendToNode = "ZZ";

		LinkObj linkObj = new LinkObj("Z", 12);
		PrefixObj prefixObj = new PrefixObj("prefix1", "MSGID", "Z", true);

		ArrayList<String> prefixList = new ArrayList<String>();
		prefixList.add("prefix1");
		prefixList.add("prefix2");
		prefixList.add("prefix3");
		PrefixListObj prefixListObj = new PrefixListObj(prefixList, "Z", true, "D3408583940");

		ArrayList<NeighborAndCostStrings> neighbors = new ArrayList<NeighborAndCostStrings>();
		neighbors.add(new NeighborAndCostStrings("B", 11));
		neighbors.add(new NeighborAndCostStrings("D", 22));
		neighbors.add(new NeighborAndCostStrings("G", 33));
		ModifyNodeObj modifyNodeObj = new ModifyNodeObj("A", neighbors, "MSGID3" );

		NeighborRequestObj neighborRequestObj = new NeighborRequestObj("F");

		//works
		//		process.addLink(linkObj);
		//		ArrayList<NeighborAndCostStrings> neighbors1 = nodeRepo.HMgetNode("A").getNeighbors();
		//		for(int i = 0; i < neighbors1.size(); i++){
		//			System.out.println("A's neighbor: " + neighbors1.get(i).getNeighborName());
		//		}
		//		String[] routers = directlyConnectedNodes.getDirectlyConnectedRoutersList();
		//		for(String router : routers){
		//			System.out.println("directly connected router: " + router);
		//		}
		//		System.out.println("is 'Z' in the graph: " + nodeRepo.HMdoesNodeExist("Z"));
		//		System.out.println("is 'Z' in the fib: " + fib.doesHashMapContainPrefix(1, "Z"));
		//
		//needs dijkstras modified
		//		process.removeLink(linkObj);
		//		System.out.println("");
		//		ArrayList<NeighborAndCostStrings> neighbors2 = nodeRepo.HMgetNode("A").getNeighbors();
		//		for(int i = 0; i < neighbors2.size(); i++){
		//			System.out.println("A's neighbor: " + neighbors2.get(i).getNeighborName());
		//		}
		//		System.out.println("node z best cost: " + nodeRepo.HMgetNode("Z").getBestCost());
		//		String[] removedRouters = directlyConnectedNodes.getDirectlyConnectedRoutersList();
		//		for(String router : removedRouters){
		//			System.out.println("directly connected router: " + router);
		//		}
		//		System.out.println("is 'Z' in the graph: " + nodeRepo.HMdoesNodeExist("Z"));
		//		System.out.println("is 'Z' in the fib: " + fib.doesHashMapContainPrefix(1, "Z"));

		process.addClientLink(linkObj);
		System.out.println("");
		ArrayList<NeighborAndCostStrings> neighbors2 = nodeRepo.HMgetNode("A").getNeighbors();
		for(int i = 0; i < neighbors2.size(); i++){
			System.out.println("A's neighbor: " + neighbors2.get(i).getNeighborName());
		}
		System.out.println("does node 'z' exist: " + nodeRepo.HMdoesNodeExist("Z"));
		String[] clients = directlyConnectedNodes.getDirectlyConnectedClientsList();
		for(String client : clients){
			System.out.println("directly connected client: " + client);
		}
		System.out.println("is 'Z' in the graph: " + nodeRepo.HMdoesNodeExist("Z"));
		System.out.println("is 'Z' in the fib: " + fib.doesHashMapContainPrefix(1, "Z"));
		process.removeClientLink(linkObj);
		process.modifyLink(linkObj);

		process.addClientLink(linkObj);

		process.addCLientPrefix(prefixObj, doNotSendToNode);
		prefixObj = new PrefixObj("prefix1", "MSGID", "Z", true);
		process.removeClientPrefix(prefixObj, doNotSendToNode);


		process.addClientPrefixList(prefixListObj, doNotSendToNode);
		prefixListObj = new PrefixListObj(prefixList, "Z", true, "D3408583940");
		process.removeClientPrefixList(prefixListObj, doNotSendToNode);

		process.requestNeighbors(doNotSendToNode);
		process.processNeighborsResponse(modifyNodeObj);
		process.processPrefixListResponse(prefixListObj);
		process.processIntrestRequestForNeighbors(neighborRequestObj);

		process.addPrefix(prefixObj, doNotSendToNode);
		process.removePrefix(prefixObj, doNotSendToNode);

		process.addPrefixList(prefixListObj, doNotSendToNode);
		process.removePrefixList(prefixListObj, doNotSendToNode);

		process.getNeighbors();
		process.getMyNeighbors();
		process.getMyDirectlyConnectedPrefixes();
	}

}
