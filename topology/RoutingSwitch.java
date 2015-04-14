package topology;

import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PacketObj;
import packetObjects.PrefixListObj;


public class RoutingSwitch implements Runnable{

	String packet;
	FIB fib;
	ProcessRoutingPackets process;
	PIT pit;
	Parse parse;
	NodeRepository nodeRepo;
	DirectlyConnectedNodes directlyConnectedNodes;
	PacketQueue packetQueue;


	public RoutingSwitch(String packet,
			FIB fib,
			PIT pit,
			DirectlyConnectedNodes directlyConnectedNodes,
			NodeRepository nodeRepo,
			PacketQueue packetQueue){

		this.packet = packet;
		this.fib = fib;
		this.pit = pit;
		this.directlyConnectedNodes = directlyConnectedNodes;
		this.nodeRepo = nodeRepo;
		this.packetQueue = packetQueue;

		parse = new Parse();
		process = new ProcessRoutingPackets(packet, nodeRepo, fib, pit, directlyConnectedNodes);
	}


	@Override
	public void run() {
		String action = parse.parseAction(packet);
		String contentName = parse.parseContentName(packet);

		if(contentName.equals(nodeRepo.getThisMachinesName()) == true){
			SendPacket sendPacket = new SendPacket();
			PacketObj packetObj;

			switch(action){
			case "intrest" : 
				//parse the interest packet to get the origin router name
				IntrestObj intrestObj = parse.parseIntrestJson(packet);

				//parse the neighbor request 
				NeighborRequestObj neighborRequestObj = new NeighborRequestObj(intrestObj.getContentName());

				//create the packet
				sendPacket.createNeighborRequestPacket(neighborRequestObj);

				//add to the update queue
				packetObj = new PacketObj(neighborRequestObj.getOriginalPacket(), nodeRepo.getThisMachinesName(), false);
				packetQueue.addToUpdateQueue(packetObj);
				break;

			case "data" : 
				//parse the data packet to get the data 
				DataObj dataObj = parse.parseDataJson(packet);

				action = parse.parseAction(dataObj.getData());
				if(action.equals("prefix")){
					//call prefix function
					PrefixListObj prefixListObj = parse.parsePrefixListJson(dataObj.getData());

					//create the update packet
					sendPacket.createPrefixResponsePacket(prefixListObj);

					//add to update queue
					packetObj = new PacketObj(prefixListObj.getOriginalPacket(), nodeRepo.getThisMachinesName(), false);
					packetQueue.addToUpdateQueue(packetObj);
				}else{
					//call neighbors function
					ModifyNodeObj modifyNodeObj = parse.parseModifyNodeJson(dataObj.getData());

					//create the update packet
					sendPacket.createNeighborResponsePacket(modifyNodeObj);

					//add to update queue
					packetObj = new PacketObj(modifyNodeObj.getOriginalPacket(), nodeRepo.getThisMachinesName(), false);
					packetQueue.addToUpdateQueue(packetObj);
				}
				break;

			default : 
				System.out.println("Error in RouteSwitch - unrecognized packet: dropping packet");
				break;
			}

		}else{

			//the request is not for the router 

			switch(action){
			case "intrest" :

				IntrestObj intrestObj = parse.parseIntrestJson(packet);
				process.processIntrest(intrestObj);
				break;

			case "data" :

				DataObj dataObj = parse.parseDataJson(packet);

				switch(dataObj.getFlag()){
				case 0 :
					process.processData0(dataObj);
					break;

				case 1 :
					process.processData1(dataObj);
					break;

				case 2 :
					process.processData2(dataObj);
					break;
				default : 
					System.out.println("data flag set to an incorrect value");
					break;
				}

				break;

			default :
				System.out.println("Error in RouteSwitch - unrecognized packet: dropping packet");
				break;

			}
		}

	}

}
