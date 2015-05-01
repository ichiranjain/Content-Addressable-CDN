package topology;

import java.util.ArrayList;
import java.util.Scanner;

import packetObjects.IntrestObj;
import packetObjects.PacketObj;


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

	Scanner scanner; 

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
		//add my self as a directly connected client
		directlyConnectedNodes.addDirectlyConnectedClient(thisMachinesName);
		//add my name as a prefix 
		directlyConnectedNodes.getDirectlyConnectedClient(thisMachinesName).addPrefix(thisMachinesName);

		//start the handlers

		//general
		Thread generalQueueHandler = new Thread(new 
				GeneralQueueHandler(packetQueue2, running));

		//routing
		Thread routingQueueHandler = new Thread ( new 
				RoutingQueueHandler(packetQueue2, nodeRepo, 
						pit, directlyConnectedNodes, running));

		generalQueueHandler.start();
		routingQueueHandler.start();

		//start the removal threads
		//update msagId's seen
		//sleep time id for Thread.sleep
		//keepMsgTime is in nano Time to remove old entries
		Thread removeMsgIDs = new Thread(new MsgIDEntryDiscard(updateMsgsSeen, msgIDSleepTime, msgIDKeepMsgTime, running));
		// removeMsgIDs.start();
		//PIT entries
		Thread removePitEntries = new Thread(new PITEntryDiscard(pit, pitSleepTime, pitKeepMsgTime, running));
		// removePitEntries.start();


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
		IntrestObj intrestObj1 = new IntrestObj(contentName, nodeRepo.thisMachinesName, 12345);
		SendPacket sendPacket = new SendPacket();
		sendPacket.createIntrestPacket(intrestObj1);
		PacketObj packetObj1 = new PacketObj(intrestObj1.getOriginalPacket(), "fakeClient", false);
		packetQueue2.addToGeneralQueue(packetObj1);
		//System.out.println("added to general q");
	}


}
