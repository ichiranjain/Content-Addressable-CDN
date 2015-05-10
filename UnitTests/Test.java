package UnitTests;

public class Test {

	public static void main(String[] args) {

		//test dijkstra's class 
		//		DijkstrasTest dijkTest = new DijkstrasTest();
		//		dijkTest.generateGraph();
		//		dijkTest.testDijkstras();

		//test Node class
		//		NodeTest nodeTest = new NodeTest();
		//		nodeTest.testNode();

		//test FIB
		//		FIBTest fibTest = new FIBTest();
		//		fibTest.testFIB();

		//test PIT
		//		PITTest pitTest = new PITTest();
		//		pitTest.testPIT();

		//test SendPacket
		//		SendPacketTest sendPacketTest = new SendPacketTest();
		//		sendPacketTest.testSendPacket();

		//test ProcessUpdates class
		//		UpdateProcessTest updateProcessTest = new UpdateProcessTest();
		//		updateProcessTest.testUpdateProcess();

		//test Preocess Routing Packets
		//		RoutingProcessTest routingProcessTest = new RoutingProcessTest();
		//		routingProcessTest.testProcessRoutingPacets();

		//update simulation 
		//		UpdateSimulation updateSim = new UpdateSimulation();
		//		updateSim.startSimulation();
		//		updateSim.addLinks();
		//		updateSim.requestForNeighbors();
		//		updateSim.neighborsAndPrefixResponse();
		//		updateSim.modifyNode();
		//		updateSim.modifyLink();
		//		updateSim.removeLink();
		//		updateSim.addClient();
		//		updateSim.addPrefixesToClient();
		//		updateSim.removeClient();
		//		updateSim.prefixUpdate();
		//
		//		updateSim.intrestPacket();
		//		updateSim.intrestPacketNoPrefix();
		//		updateSim.intrestPacketPITentry();
		//		updateSim.intrestLongPrefix();
		//		updateSim.dataPacket();
		//		//updateSim.dataPacketNoNextHop();
		//		updateSim.dataPacketPassTowardServer();
		//		updateSim.dataPacketForwardToServer();
		//		updateSim.dataPacketNoPITEntry();
		Thread thread = new Thread(new RouterTest("A", 10000, 40000L));
		thread.start();


	}

}
