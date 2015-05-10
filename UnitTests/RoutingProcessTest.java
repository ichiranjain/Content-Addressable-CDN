package UnitTests;

import topology.DirectlyConnectedNodes;
import topology.FIB;
import topology.NodeRepository;
import topology.PIT;
import topology.ProcessRoutingPackets;
import topology.SendPacket;
import topology.UpdateMsgsSeen;

public class RoutingProcessTest {

	NodeRepository nodeRepo;
	FIB fib;
	PIT pit;
	ProcessRoutingPackets process;
	DirectlyConnectedNodes directlyConnectedNodes;
	UpdateMsgsSeen upDatesSeen;
	SendPacket sendPacket;

	public RoutingProcessTest(){

		directlyConnectedNodes = new DirectlyConnectedNodes();
		upDatesSeen = new UpdateMsgsSeen();

		nodeRepo = new NodeRepository("A");

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

		pit = new PIT();
		fib = new FIB(nodeRepo, pit, directlyConnectedNodes);
		fib.addPrefixToFIB("prefix1", "A");
		fib.addPrefixToFIB("prefix2", "B");

		this.sendPacket= new SendPacket();

	}

	public void testProcessRoutingPacets(){

		//		IntrestObj intrestObj = new IntrestObj("prefix2", "D", 1234);
		//		sendPacket.createIntrestPacket(intrestObj);
		//		PacketObj packet = new PacketObj(intrestObj.getOriginalPacket(), "G", false);
		//		process = new ProcessRoutingPackets(packet.getPacket(), nodeRepo, fib, pit, directlyConnectedNodes);
		//		process.processIntrest(intrestObj);
		//
		//		byte b = 0;
		//		DataObj dataObj0 = new DataObj("prefix2", "D", b, "data here", "", b);
		//		sendPacket.createDataPacket(dataObj0);
		//		packet = new PacketObj(dataObj0.getOriginalPacket(), "G", false);
		//		process = new ProcessRoutingPackets(packet.getPacket(), nodeRepo, fib, pit, directlyConnectedNodes);
		//		process.processData0(dataObj0);
		//
		//		b = 1;
		//		DataObj dataObj1 = new DataObj("prefix2", "D", b, "data here", "", b);
		//		sendPacket.createDataPacket(dataObj0);
		//		packet = new PacketObj(dataObj1.getOriginalPacket(), "G", false);
		//		process = new ProcessRoutingPackets(packet.getPacket(), nodeRepo, fib, pit, directlyConnectedNodes);
		//		process.processData1(dataObj1);
		//
		//		b = 2;
		//		DataObj dataObj2 = new DataObj("prefix2", "D", b, "data here", "", b);
		//		sendPacket.createDataPacket(dataObj0);
		//		packet = new PacketObj(dataObj2.getOriginalPacket(), "G", false);
		//		process = new ProcessRoutingPackets(packet.getPacket(), nodeRepo, fib, pit, directlyConnectedNodes);
		//		process.processData2(dataObj2);
	}

}
