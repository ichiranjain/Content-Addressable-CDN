package topology;

import packetObjects.GenericPacketObj;


public class RoutingSwitch implements Runnable{

	//String packet;
	@SuppressWarnings("rawtypes")
	GenericPacketObj genericPacketObj;
	PIT pit;
	//Parse parse;
	NodeRepository nodeRepo;
	DirectlyConnectedNodes directlyConnectedNodes;
	PacketQueue2 packetQueue2;
	String recievedFromNode;


	@SuppressWarnings("rawtypes")
	public RoutingSwitch(GenericPacketObj genericPacketObj,
			PIT pit,
			DirectlyConnectedNodes directlyConnectedNodes,
			NodeRepository nodeRepo,
			PacketQueue2 packetQueue2){

		//this.packet = packet;
		this.genericPacketObj = genericPacketObj;
		this.pit = pit;
		this.directlyConnectedNodes = directlyConnectedNodes;
		this.nodeRepo = nodeRepo;
		this.packetQueue2 = packetQueue2;
		this.recievedFromNode = genericPacketObj.getRecievedFromNode();

		//parse = new Parse();
	}


	@Override
	public void run() {
		String action = genericPacketObj.getAction();


		switch(action){
		case "intrest" :

			break;

		case "data" :
			break;

		default : 
			System.out.println("Invalid route action");
			break;
		}
	}
}
