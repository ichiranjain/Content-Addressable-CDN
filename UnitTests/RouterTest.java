package UnitTests;

import java.util.ArrayList;
import java.util.Scanner;

import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.LinkObj;
import packetObjects.ModifyNodeObj;
import packetObjects.PacketObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;
import topology.DirectlyConnectedNodes;
import topology.FIB;
import topology.FIBEntryDiscard;
import topology.GeneralQueueHandler;
import topology.MsgIDEntryDiscard;
import topology.NeighborAndCostStrings;
import topology.Node;
import topology.NodeRepository;
import topology.PIT;
import topology.PITEntryDiscard;
import topology.PacketQueue2;
import topology.RoutingQueueHandler;
import topology.SendPacket;
import topology.UpdateMsgsSeen;
import topology.UpdateQueueHandler;


public class RouterTest implements Runnable{

	String thisMachinesName;
	boolean running;
	int sleepTime;
	long keepMsgTime;

	PacketQueue2 packetQueue2;
	NodeRepository nodeRepo;
	PIT pit;
	DirectlyConnectedNodes directlyConnectedNodes;
	UpdateMsgsSeen updateMsgsSeen;
	FIB fib;
	Scanner scanner; 
	SendPacket sendPacket = new SendPacket();

	public RouterTest(String thisMachinesName, int sleepTime, long keepMsgTime) {
		this.thisMachinesName = thisMachinesName;
		this.sleepTime = sleepTime;
		this.keepMsgTime = keepMsgTime;
		this.running = true;

		packetQueue2 = new PacketQueue2();
		nodeRepo = new NodeRepository(thisMachinesName);
		pit = new PIT();
		directlyConnectedNodes = new DirectlyConnectedNodes();
		updateMsgsSeen = new UpdateMsgsSeen();
		fib = new FIB(nodeRepo, pit, directlyConnectedNodes);
		scanner = new Scanner(System.in);
	}

	@Override
	public void run() {



		//add myself to the graph
		nodeRepo.HMaddNode(thisMachinesName);
		//set my best cost
		nodeRepo.HMgetNode(thisMachinesName).setBestCost(0);
		//set my next hop to my self
		nodeRepo.HMgetNode(thisMachinesName).setOriginNextHop(thisMachinesName);
		//add my name to the FIB table 
		fib.addPrefixToFIB(thisMachinesName, thisMachinesName);
		//add my self as a directly connected client
		directlyConnectedNodes.addDirectlyConnectedClient(thisMachinesName);
		//add my name as a prefix 
		directlyConnectedNodes.getDirectlyConnectedClient(thisMachinesName).addPrefix(thisMachinesName);

		//start the handlers

		//general
		Thread generalQueueHandler = new Thread(new 
				GeneralQueueHandler(packetQueue2, running));
		//update
		Thread updateQueueHandler = new Thread( new 
				UpdateQueueHandler(packetQueue2, nodeRepo, fib, 
						directlyConnectedNodes, updateMsgsSeen, running));
		//routing
		Thread routingQueueHandler = new Thread ( new 
				RoutingQueueHandler(packetQueue2, nodeRepo, fib, 
						pit, directlyConnectedNodes, running));

		generalQueueHandler.start();
		updateQueueHandler.start();
		routingQueueHandler.start();

		//start the removal threads
		//update msagId's seen
		//sleep time id for Thread.sleep
		//keepMsgTime is in nano Time to remove old entries
		Thread removeMsgIDs = new Thread(new MsgIDEntryDiscard(updateMsgsSeen, sleepTime, keepMsgTime, running));
		removeMsgIDs.start();
		//PIT entries
		Thread removePitEntries = new Thread(new PITEntryDiscard(pit, sleepTime, keepMsgTime, running));
		removePitEntries.start();
		//FIB
		Thread removeFibEntries = new Thread(new FIBEntryDiscard(fib, nodeRepo, sleepTime, running));
		removeFibEntries.start();


		/*
		 * add code for the test 
		 * 
		 * act like the over lay is sending the router a msg
		 */

		addLink("B", 60000);
		//addLink("D", 7);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		//receive request for neighbors
		//		requestNeighbors("A", "B", 1234, "B");
		//
		//		try {
		//			Thread.sleep(1000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		ArrayList<NeighborAndCostStrings> neighbors = new ArrayList<NeighborAndCostStrings>();
		neighbors.add(new NeighborAndCostStrings("A", 233));

		modifyNode("B", neighbors, "B");
		modifyNode("B", neighbors, "B");
		//receive neighbor response
		//		try {
		//			Thread.sleep(1000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//
		//		byte flag = 0;
		//		ArrayList<NeighborAndCostStrings> neighbors = new ArrayList<NeighborAndCostStrings>();
		//		neighbors.add(new NeighborAndCostStrings("A", 5));
		//		neighbors.add(new NeighborAndCostStrings("C", 4));
		//
		//		neighborsResponse("B", neighbors, "B", "A", flag, flag);
		//		//receive prefix response
		//
		//
		//		try {
		//			Thread.sleep(1000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//
		//		ArrayList<String> prefixList = new ArrayList<String>();
		//		prefixList.add("B");
		//		prefixList.add("Bprefix1");
		//
		//		prefixResponse(prefixList, "B", true, "B", "A", flag, flag);
		//
		//		//add client
		//		addClient("client1A", 0, "client1A");
		//		//add client
		//		addClient("client2A", 0, "client2A");
		//		//add client prefix
		//		addClient("client3A", 0, "client3A");
		//
		//		try {
		//			Thread.sleep(1000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		//add client prefix List update
		//		clientPrefix("client1Aprefix1", "client1A", true, "client1A");
		//
		//		prefixList = new ArrayList<String>();
		//		prefixList.add("client2Aprefix1");
		//		prefixList.add("client2Aprefix2");
		//		prefixList.add("client2Aprefix3");
		//		prefixList.add("client2Aprefix4");
		//		clientPrefixList(prefixList, "client2A", true, "client2A");
		//
		//		//add prefix update 
		//		prefix("Bprefix1", "B", true, "B");
		//		//add prefixList update 
		//
		//		prefixList = new ArrayList<String>();
		//		prefixList.add("Bprefix2");
		//		prefixList.add("Bprefix3");
		//		prefixList.add("Bprefix4");
		//		prefixList(prefixList, "B", true, "B");
		//
		//		//remove client prefix
		//		clientPrefix("client2Aprefix1", "client2A", false, "client2A");
		//		//remove client prefixList
		//		prefixList = new ArrayList<String>();
		//		prefixList.add("client2Aprefix2");
		//		prefixList.add("client2Aprefix3");
		//		clientPrefixList(prefixList, "client2A", false, "client2A");
		//
		//		//remove prefix
		//		prefix("Bprefix1", "B", false, "B");
		//		//remove preifxList
		//		prefixList = new ArrayList<String>();
		//		prefixList.add("Bprefix2");
		//		prefixList.add("Bprefix3");
		//		prefixList(prefixList, "B", false, "B");
		//
		//		//modify node packet
		//		neighbors = new ArrayList<NeighborAndCostStrings>();
		//		neighbors.add(new NeighborAndCostStrings("A", 7));
		//		neighbors.add(new NeighborAndCostStrings("C", 6));
		//		modifyNode("D", neighbors, "D");
		//
		//		//modify link
		//		modifyLink("B", 3);
		//		//remove link
		//		removeLink("D", 7);
		//
		//
		//		try {
		//			Thread.sleep(2000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		//Interest packet
		//normal route
		//intrestPacket("Bprefix4", "", 1234, "client3A");

		//		try {
		//			Thread.sleep(1000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		// needs synchronization added intrestPacket("Bprefix4", "A", 1234, "client4A");

		//route DNE
		//intrestPacket("D", "A", 2345, "A");

		//route with a large prefix that matches a smaller one
		// worked intrestPacket("B/video", "A", 3456, "client3A");

		//no pit entry
		//intrestPacket(contentName, originRouter, nonce, fromNode);

		//Duplicate request so pit entry get another requester
		//intrestPacket("Bprefix4", "client1A", 5678, "A");


		//data packet 
		//normal route
		//dataPacket("Bprefix4", "A", flag, "some data", flag, true, "B");
		//		//data packet set flag to 1
		//flag = 1;
		//byte cacheFlag = 0;
		//dataPacket("Bprefix4", "A", flag, "some data2", cacheFlag, true, "B");

		//data packet matching this router 
		//dataPacket(contentName, originRouter, flag, data, cacheFlag, lastChunk, fromNode);

		//data packet with flag set 2 no pit entry
		//flag = 2;
		//dataPacket("Bprefix4", "A", flag, "this data", cacheFlag, true, "client3");


		/*
		 * 
		 * 
		 * 
		 */

		boolean alive = true;
		String action = "";
		while(alive == true){


			action = scanner.next();

			switch(action){
			case "node" :
				System.out.println("-printing nodes-");
				printNodeRepo();
				break;

			case "nd" :
				//scanner
				action = scanner.next();
				printNodeDetails(action);
				break;

			case "fib" :
				printFIB();
				break;

			case "pit" :
				printPIT();
				break;

			case "drr" :
				printDirectlyConnectedRouters();
				break;

			case "drc" :
				printDirectlyConnectedClietns();
				break;

			case "msg" :
				printMsgIDsSeen();
				break;

			case "kill" :
				killThreads();
				alive = false;
				System.out.println("killing program");
				break;

			default :
				System.out.println("default hit");
				break;
			}

		}
		System.out.println("program terminating");
		System.exit(0);

	}

	public void killThreads(){
		running = false;
	}
	public void printNodeRepo(){
		ArrayList<Node> graph = nodeRepo.getGraphList();
		for(Node node : graph){
			System.out.println("Node: " + node.getName());
		}
	}
	public void printNodeDetails(String nodeName){
		Node node = nodeRepo.HMgetNode(nodeName);
		System.out.println("Node Name: " + node.getName());
		System.out.println("Node Best Cost: " + node.getBestCost());
		System.out.println("Node Origin Next Hop: " + node.getOriginNextHop());
		ArrayList<NeighborAndCostStrings> neighbors = node.getNeighbors();
		for(NeighborAndCostStrings neighbor : neighbors){
			System.out.println("Neighbor: " + neighbor);
		}
	}
	public void printFIB(){
		ArrayList<String> entries = fib.getFIBEntries();
		for(String entry : entries){
			System.out.println("FIB entry: " + entry);
		}
	}
	public void printPIT(){
		ArrayList<String> entries = pit.getPitNamesAndEntries();
		for(String entry : entries){
			System.out.println("PIT entry: " + entry);
		}
	}
	public void printDirectlyConnectedRouters(){
		String[] routers = directlyConnectedNodes.getDirectlyConnectedRoutersList();
		for(String router : routers){
			System.out.println("Directly Connected Router: " + router);
		}
	}
	public void printDirectlyConnectedClietns(){
		ArrayList<String> entries = directlyConnectedNodes.getClientEntries();
		for(String entry : entries){
			System.out.println("Directly connected client: " + entry);
		}
	}
	public void printMsgIDsSeen(){
		ArrayList<String> entries = updateMsgsSeen.getMsgIDsAndTimes();
		for(String entry : entries){
			System.out.println("MsgID: " + entry);
		}
	}


	/*
	 * 
	 * Packet Functions
	 */
	public void addLink(String nodeName, int nodeCost){
		//System.out.println("creating add link obj");
		//make the obj
		LinkObj addlinkObj = new LinkObj(nodeName, nodeCost);
		//create json
		sendPacket.createAddLinkPacket(addlinkObj);

		PacketObj packetObj1 = new PacketObj(addlinkObj.getOriginalPacket(), nodeName, true);
		//add to the queue
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void removeLink(String nodeName, int nodeCost){
		LinkObj removelinkObj = new LinkObj(nodeName, nodeCost);
		//create json
		sendPacket.createRemoveLinkPacket(removelinkObj);

		PacketObj packetObj1 = new PacketObj(removelinkObj.getOriginalPacket(), nodeName, true);
		//add to the queue
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void modifyLink(String nodeName, int nodeCost){
		LinkObj modifylinkObj = new LinkObj(nodeName, nodeCost);
		//create json
		sendPacket.createModifyLinkPacket(modifylinkObj);

		PacketObj packetObj1 = new PacketObj(modifylinkObj.getOriginalPacket(), nodeName, true);
		//add to the queue
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void modifyNode(String nodeName, ArrayList<NeighborAndCostStrings> neighbors, String fromNode){
		String msgID = nodeName + System.nanoTime();
		ModifyNodeObj modifyNodeObj = new ModifyNodeObj(nodeName, neighbors, msgID );
		sendPacket.createModifyNodePacket(modifyNodeObj);
		PacketObj packetObj1 = new PacketObj(modifyNodeObj.getOriginalPacket(), fromNode, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void prefix(String prefix, String advertiser, boolean addRemove, String fromNode){
		String msgID = advertiser + System.nanoTime();
		PrefixObj prefixObj4 = new PrefixObj(prefix, msgID, advertiser, addRemove);
		sendPacket.createPrefixPacket(prefixObj4);
		PacketObj packetObj1 = new PacketObj(prefixObj4.getOriginalPacket(), fromNode, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void prefixList(ArrayList<String> prefixList, String advertiser, boolean addRemove, String fromNode){
		String msgID = advertiser + System.nanoTime();
		PrefixListObj prefixListObj3 = new PrefixListObj(prefixList, advertiser, addRemove, msgID);
		sendPacket.createPrefixListPacket(prefixListObj3);
		PacketObj packetObj1 = new PacketObj(prefixListObj3.getOriginalPacket(), fromNode, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void addClient(String clientName, int cost, String fromNode){
		LinkObj linkObj7 = new LinkObj(clientName, cost);
		sendPacket.createAddClient(linkObj7);
		PacketObj packetObj1 = new PacketObj(linkObj7.getOriginalPacket(), fromNode, true);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void removeClient(String clientName, int cost, String fromNode){
		LinkObj linkObj7 = new LinkObj(clientName, cost);
		sendPacket.createRemoveClient(linkObj7);
		PacketObj packetObj1 = new PacketObj(linkObj7.getOriginalPacket(), fromNode, true);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void clientPrefix(String prefix, String advertiser, boolean addRemove, String fromNode){
		String msgID = advertiser + System.nanoTime();
		PrefixObj prefixObj3 = new PrefixObj(prefix, msgID, advertiser, addRemove);
		sendPacket.createClientPrefix(prefixObj3);
		PacketObj packetObj1 = new PacketObj(prefixObj3.getOriginalPacket(), fromNode, true);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void clientPrefixList(ArrayList<String> prefixList, String advertiser, boolean addRemove, String fromNode){
		String msgID = advertiser + System.nanoTime();
		PrefixListObj prefixObj3 = new PrefixListObj(prefixList, advertiser, addRemove, msgID);
		sendPacket.createClientPrefixList(prefixObj3);
		PacketObj packetObj1 = new PacketObj(prefixObj3.getOriginalPacket(), fromNode, true);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void intrestPacket(String contentName, String originRouter, int nonce, String fromNode){
		IntrestObj intrestObj1 = new IntrestObj(contentName, originRouter, nonce);
		sendPacket.createIntrestPacket(intrestObj1);
		PacketObj packetObj1 = new PacketObj(intrestObj1.getOriginalPacket(), fromNode, false);
		packetQueue2.addToGeneralQueue(packetObj1);
		//System.out.println("added to general q");
	}
	public void dataPacket(String contentName, String originRouter, byte flag, String data, byte cacheFlag, boolean lastChunk, String fromNode){
		DataObj dataObj = new DataObj(contentName, originRouter, flag, data, cacheFlag, lastChunk);
		sendPacket.createDataPacket(dataObj);
		PacketObj packetObj1 = new PacketObj(dataObj.getOriginalPacket(), fromNode, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void requestNeighbors(String contentName, String originRouter, int nonce, String fromNode){
		IntrestObj intrestObj1 = new IntrestObj(contentName, originRouter, nonce);
		sendPacket.createIntrestPacket(intrestObj1);
		PacketObj packetObj1 = new PacketObj(intrestObj1.getOriginalPacket(), fromNode, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void neighborsResponse(String advertiser, ArrayList<NeighborAndCostStrings> neighbors, String fromNode, String originRouter, byte flag, byte cacheFlag){

		String msgID = advertiser + System.nanoTime();
		ModifyNodeObj modifyNodeObj = new ModifyNodeObj(advertiser, neighbors, msgID );
		//sendPacket.createModifyNodePacket(modifyNodeObj);
		sendPacket.createNeighborResponsePacket(modifyNodeObj);

		DataObj dataObj = new DataObj(originRouter, originRouter, flag, modifyNodeObj.getOriginalPacket(), cacheFlag, true);
		sendPacket.createDataPacket(dataObj);
		PacketObj packetObj1 = new PacketObj(dataObj.getOriginalPacket(), fromNode, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void prefixResponse(ArrayList<String> prefixList, String advertiser, boolean addRemove, String fromNode, String originRouter, byte flag, byte cacheFlag){

		String msgID = advertiser + System.nanoTime();
		PrefixListObj prefixListObj3 = new PrefixListObj(prefixList, advertiser, addRemove, msgID);
		//sendPacket.createPrefixListPacket(prefixListObj3);
		sendPacket.createPrefixResponsePacket(prefixListObj3);

		DataObj dataObj = new DataObj(originRouter, originRouter, flag, prefixListObj3.getOriginalPacket(), cacheFlag, true);
		sendPacket.createDataPacket(dataObj);
		PacketObj packetObj1 = new PacketObj(dataObj.getOriginalPacket(), fromNode, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}
}

