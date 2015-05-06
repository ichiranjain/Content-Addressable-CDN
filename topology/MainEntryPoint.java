package topology;

import java.util.ArrayList;
import java.util.Scanner;


public class MainEntryPoint implements Runnable{

	String thisMachinesName;
	boolean running;
	int pitSleepTime;
	long pitKeepMsgTime;
	int msgIDSleepTime;
	long msgIDKeepMsgTime;
	int fibSleepTime;

	public PacketQueue2 packetQueue2;
	//NodeRepository nodeRepo;
	//PIT pit;
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
		//nodeRepo = new NodeRepository(thisMachinesName);
		//pit = new PIT();
		directlyConnectedNodes = new DirectlyConnectedNodes();
		updateMsgsSeen = new UpdateMsgsSeen();
		//fib = new FIB(nodeRepo, pit, directlyConnectedNodes);
		// scanner = new Scanner(System.in);
	}

	@Override
	public void run() {



		//		//add myself to the graph
		//		nodeRepo.HMaddNode(thisMachinesName);
		//		//set my best cost
		//		nodeRepo.HMgetNode(thisMachinesName).setBestCost(0);
		//		//set my next hop to my self
		//		nodeRepo.HMgetNode(thisMachinesName).setOriginNextHop(thisMachinesName);
		//		//add my name to the FIB table 
		//		//fib.addPrefixToFIB(thisMachinesName, thisMachinesName);
		//		//add my self as a directly connected client
		//		directlyConnectedNodes.addDirectlyConnectedClient(thisMachinesName);
		//		//add my name as a prefix 
		//		directlyConnectedNodes.getDirectlyConnectedClient(thisMachinesName).addPrefix(thisMachinesName);

		//start the handlers

		//general
		Thread generalQueueHandler = new Thread(new 
				GeneralQueueHandler(packetQueue2, running));


		generalQueueHandler.start();


		//start the removal threads
		//update msagId's seen
		//sleep time id for Thread.sleep
		//keepMsgTime is in nano Time to remove old entries
		Thread removeMsgIDs = new Thread(new MsgIDEntryDiscard(updateMsgsSeen, msgIDSleepTime, msgIDKeepMsgTime, running));
		// removeMsgIDs.start();
		//PIT entries
		//Thread removePitEntries = new Thread(new PITEntryDiscard(pit, pitSleepTime, pitKeepMsgTime, running));
		// removePitEntries.start();




	}

	public void killThreads(){
		running = false;
	}


	//	public void printPIT(){
	//		ArrayList<String> entries = pit.getPitNamesAndEntries();
	//		for(String entry : entries){
	//			System.out.println("PIT entry: " + entry);
	//		}
	//	}
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



	//	public void prefix(String prefix, boolean addRemove){
	//		String msgID = nodeRepo.thisMachinesName + System.nanoTime();
	//		PrefixObj prefixObj4 = new PrefixObj(prefix, msgID, nodeRepo.thisMachinesName, addRemove);
	//		SendPacket sendPacket = new SendPacket();
	//		sendPacket.createPrefixPacket(prefixObj4);
	//		PacketObj packetObj1 = new PacketObj(prefixObj4.getOriginalPacket(), nodeRepo.thisMachinesName, false);
	//		packetQueue2.addToGeneralQueue(packetObj1);
	//	}
	//
	//	public void prefixList(boolean addRemove){
	//		String msgID = nodeRepo.thisMachinesName + System.nanoTime();
	//		ArrayList<String> prefixList = new ArrayList<String>();
	//		prefixList.add("prefix1");
	//		prefixList.add("prefix2/video");
	//		prefixList.add("prefix3/video/news");
	//		PrefixListObj prefixListObj3 = new PrefixListObj(prefixList, nodeRepo.thisMachinesName, addRemove, msgID);
	//		SendPacket sendPacket = new SendPacket();
	//		sendPacket.createPrefixListPacket(prefixListObj3);
	//		PacketObj packetObj1 = new PacketObj(prefixListObj3.getOriginalPacket(), nodeRepo.thisMachinesName, false);
	//		packetQueue2.addToGeneralQueue(packetObj1);
	//	}

}
