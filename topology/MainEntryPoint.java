package topology;

import java.util.ArrayList;
import java.util.Scanner;

import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.PacketObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;


/**
 * This class is the main thread to start initialize all the routing tables and</br>
 * start all the routing threads.</br>
 * It also contains all of the routing functions that are called from the </br>
 * terminal while the cache server is running.
 * 
 * This machines name: is the ID of this machine</br>
 * @author spufflez
 *
 */
public class MainEntryPoint implements Runnable{

	String thisMachinesName;
	boolean running;
	int pitSleepTime;
	long pitKeepMsgTime;
	int msgIDSleepTime;
	long msgIDKeepMsgTime;
	int fibSleepTime;

	public PacketQueue2 packetQueue2;
	NodeRepository nodeRepo;
	PIT pit;
	DirectlyConnectedNodes directlyConnectedNodes;
	UpdateMsgsSeen updateMsgsSeen;
	FIB fib;
	Scanner scanner; 

	/**
	 * COnstructor
	 * @param thisMachinesName
	 * @param pitSleepTime
	 * @param pitKeepMsgTime
	 * @param msgIDSleepTime
	 * @param msgIDKeepMsgTime
	 * @param fibSleepTime
	 */
	public MainEntryPoint(String thisMachinesName, int pitSleepTime, long pitKeepMsgTime, int msgIDSleepTime, long msgIDKeepMsgTime, int fibSleepTime) {
		//System.out.println("MainEntryPoint constructor called...");
		this.thisMachinesName = thisMachinesName;
		this.pitSleepTime = pitSleepTime;
		this.pitKeepMsgTime = pitKeepMsgTime;
		this.msgIDSleepTime = msgIDSleepTime;
		this.msgIDKeepMsgTime = msgIDKeepMsgTime;
		this.fibSleepTime = fibSleepTime;
		this.running = true;

		packetQueue2 = new PacketQueue2();
		nodeRepo = new NodeRepository(thisMachinesName);
		pit = new PIT();
		directlyConnectedNodes = new DirectlyConnectedNodes();
		updateMsgsSeen = new UpdateMsgsSeen();
		fib = new FIB(nodeRepo, pit, directlyConnectedNodes);
		// scanner = new Scanner(System.in);
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


		//keepMsgTime is a long for the amount nano Time the entry should be kept before it is removed
		//sleep time is an int for the amount mili seconds the thread should sleep

		//MsgIds entries 
		//suggested run every 1 minute with a entry keep time of 2 to 3 minutes 
		Thread removeMsgIDs = new Thread(new MsgIDEntryDiscard(updateMsgsSeen, msgIDSleepTime, msgIDKeepMsgTime, running));
		removeMsgIDs.start();

		//PIT entries
		//suggested to have run every 1 minutes with an entry keep time of 30 seconds
		Thread removePitEntries = new Thread(new PITEntryDiscard(pit, pitSleepTime, pitKeepMsgTime, running));
		removePitEntries.start();

		//FIB
		//suggest to have run every 10 to 15 seconds /// 20,000 milliseconds  == 20 seconds
		Thread removeFibEntries = new Thread(new FIBEntryDiscard(fib, nodeRepo, fibSleepTime, running));
		removeFibEntries.start();




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

	public void intrestPacket(String contentName){
		IntrestObj intrestObj1 = new IntrestObj(contentName, "", 12345);
		SendPacket sendPacket = new SendPacket();
		sendPacket.createIntrestPacket(intrestObj1);
		PacketObj packetObj1 = new PacketObj(intrestObj1.getOriginalPacket(), "53830144", false);
		packetQueue2.addToGeneralQueue(packetObj1);
		//System.out.println("added to general q");
	}

	public void dataPacket(String contentName, String originRouter, String fromNode){
		SendPacket sendPacket = new SendPacket();
		byte b = 0;
		DataObj dataObj = new DataObj(contentName, originRouter, b, "data data", b, true);
		sendPacket.createDataPacket(dataObj);
		PacketObj packetObj1 = new PacketObj(dataObj.getOriginalPacket(), fromNode, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void prefix(String prefix, boolean addRemove){
		String msgID = nodeRepo.thisMachinesName + System.nanoTime();
		PrefixObj prefixObj4 = new PrefixObj(prefix, msgID, nodeRepo.thisMachinesName, addRemove);
		SendPacket sendPacket = new SendPacket();
		sendPacket.createPrefixPacket(prefixObj4);
		PacketObj packetObj1 = new PacketObj(prefixObj4.getOriginalPacket(), nodeRepo.thisMachinesName, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void prefixList(boolean addRemove){
		String msgID = nodeRepo.thisMachinesName + System.nanoTime();
		ArrayList<String> prefixList = new ArrayList<String>();
		prefixList.add("prefix1");
		prefixList.add("prefix2/video");
		prefixList.add("prefix3/video/news");
		PrefixListObj prefixListObj3 = new PrefixListObj(prefixList, nodeRepo.thisMachinesName, addRemove, msgID);
		SendPacket sendPacket = new SendPacket();
		sendPacket.createPrefixListPacket(prefixListObj3);
		PacketObj packetObj1 = new PacketObj(prefixListObj3.getOriginalPacket(), nodeRepo.thisMachinesName, false);
		packetQueue2.addToGeneralQueue(packetObj1);
	}

	public void ping(String nodeID){
		IntrestObj intrestObj1 = new IntrestObj(nodeID+"/ping", nodeRepo.getThisMachinesName(), 12345);
		SendPacket sendPacket = new SendPacket();
		sendPacket.createIntrestPacket(intrestObj1);
		PacketObj packetObj1 = new PacketObj(intrestObj1.getOriginalPacket(), nodeRepo.getThisMachinesName(), false);
		packetQueue2.addToGeneralQueue(packetObj1);
		//System.out.println("added to general q");
	}

	public void autoPing(String nodeID, int pingCount){
		for(int i = 0; i < pingCount; i++){			
			ping(nodeID);
		}
	}

	public void timedPing(String nodeID, int pingCount){
		for(int i = 0; i < pingCount; i++){			
			ping(nodeID);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void convergenceTime(){
		int nodeCounter = nodeRepo.nodeCounter;
		for(int i = 1; i <= nodeCounter; i++){
			System.out.println("Node: " + i + " time:"+ (nodeRepo.convergenceHM.get(i) - nodeRepo.convergenceHM.get(1)) );
		}
	}

}
