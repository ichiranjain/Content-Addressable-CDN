package topology;

import packetObjects.LinkObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PacketObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;

public class UpdateSwitch implements Runnable{

	PacketObj packetObj;
	NodeRepository nodeRepo;
	ProcessUpdates process;
	UpdateMsgsSeen msgsSeen;
	Parse parse;

	public UpdateSwitch(PacketObj packetObj, 
			NodeRepository nodeRepo,
			FIB fib,
			DirectlyConnectedNodes directlyConnectedNodes,
			UpdateMsgsSeen updateMsgsSeen){

		this.packetObj = packetObj;
		this.nodeRepo = nodeRepo;
		this.msgsSeen = updateMsgsSeen;
		this.process = new ProcessUpdates(nodeRepo, updateMsgsSeen, fib, directlyConnectedNodes);
		this.parse = new Parse();

	}

	@Override
	public void run() {

		String msgID = parse.parseMsgID(packetObj.getPacket());

		//has the update been seen before
		if(msgsSeen.doesMsgIDExist(msgID) == true){
			//if so do not process the update, drop the packet
			//this update has looped around
			return;
		}else{
			//this is a new msg ... so add it to the msgs seen 
			msgsSeen.addMsgID(msgID, System.nanoTime());
		}


		//String type = parse.parseType(packet);

		String action = parse.parseAction(packetObj.getPacket());
		LinkObj linkObj;
		boolean addRemove;
		PrefixListObj prefixListObj;
		PrefixObj prefixObj;

		switch(action){

		case "addLink" :
			//add the Node
			//AddNodeObj addNodeObj = parse.parseAddNodeJson(packet);
			linkObj = parse.parseAddLink(packetObj.getPacket());
			process.addLink(linkObj);
			break;

		case "removeLink" :
			linkObj = parse.parseRemoveLink(packetObj.getPacket());
			process.removeLink(linkObj);
			break;

		case "modifyLink" :
			linkObj = parse.parseModifyLink(packetObj.getPacket());
			process.modifyLink(linkObj);
			break;

		case "modify" : 
			ModifyNodeObj modifyNodeObj = parse.parseModifyNodeJson(packetObj.getPacket());
			process.modifyNode(modifyNodeObj, packetObj.getRecievedFromNode());
			break;

		case "prefix" :
			//do something
			addRemove = parse.parsePrefixAddRemove(packetObj.getPacket());
			if(addRemove == true){
				//add the packet
				prefixObj = parse.parsePrefixJson(packetObj.getPacket());
				process.addPrefix(prefixObj, packetObj.getRecievedFromNode());
			}else{
				//remove the packet
				prefixObj = parse.parsePrefixJson(packetObj.getPacket());
				process.removePrefix(prefixObj, packetObj.getRecievedFromNode());
			}
			break;

		case "prefixList" : 
			addRemove = parse.parsePrefixAddRemove(packetObj.getPacket());
			if(addRemove == true){
				//add the packet
				prefixListObj = parse.parsePrefixListJson(packetObj.getPacket());
				process.addPrefixList(prefixListObj, packetObj.getRecievedFromNode());
			}else{
				//remove the packet
				prefixListObj = parse.parsePrefixListJson(packetObj.getPacket());
				process.removePrefixList(prefixListObj, packetObj.getRecievedFromNode());
			}
			break;

		case "addClient" : 
			linkObj = parse.parseClientAddNodeJson(packetObj.getPacket());
			process.addClientLink(linkObj);
			break;

		case "removeClient" : 
			linkObj = parse.parseClientRemoveNodeJson(packetObj.getPacket());
			process.removeClientLink(linkObj);
			break;

		case "clientPrefix" : 
			addRemove = parse.parsePrefixAddRemove(packetObj.getPacket());
			if(addRemove == true){
				//add the packet
				prefixObj = parse.parsePrefixJson(packetObj.getPacket());
				process.addCLientPrefix(prefixObj, packetObj.getRecievedFromNode());
			}else{
				//remove the packet
				prefixObj = parse.parsePrefixJson(packetObj.getPacket());
				process.removeClientPrefix(prefixObj, packetObj.getRecievedFromNode());
			}
			break;

		case "clientPrefixList" : 
			addRemove = parse.parsePrefixAddRemove(packetObj.getPacket());
			if(addRemove == true){
				//add the packet
				prefixListObj = parse.parsePrefixListJson(packetObj.getPacket());
				process.addClientPrefixList(prefixListObj, packetObj.getRecievedFromNode());
			}else{
				//remove the packet
				prefixListObj = parse.parsePrefixListJson(packetObj.getPacket());
				process.removeClientPrefixList(prefixListObj, packetObj.getRecievedFromNode());
			}
			break;

		case "neighborRequest" :
			NeighborRequestObj neighborRequestObj = parse.parseRequestNeighbors(packetObj.getPacket());
			process.requestNeighbors(neighborRequestObj.getFromName());
			break;

		case "prefixResponse" :
			prefixListObj = parse.parsePrefixListJson(packetObj.getPacket());
			process.processPrefixListResponse(prefixListObj);
			break;

		case "neighborResponse" :
			modifyNodeObj = parse.parseModifyNodeJson(packetObj.getPacket());
			process.processNeighborsResponse(modifyNodeObj);
			break;

			//		case "hello" :
			//			boolean requestTable = parse.parseRequestTable(packet);
			//			if(requestTable == true){
			//				HelloObj helloObj = parse.parseHelloJson(packet);
			//				process.processHelloTableRequest(helloObj);
			//			}else{
			//				HelloObj helloObj = parse.parseHelloJson(packet);
			//				//process.processHelloHeartBeat(helloObj);
			//			}
			//			break;
			//
			//		case "table" :
			//			TableObj tableObj = parse.parseTableJson(packet);
			//			process.processTable(tableObj);
			//			break;

		default :
			System.out.println("Error in UpdateSwitch - unrecognized packet: dropping packet");
			break;
		}

	}



}
