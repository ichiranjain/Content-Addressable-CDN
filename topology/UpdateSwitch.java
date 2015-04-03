package topology;

import packetObjects.AddNodeObj;
import packetObjects.HelloObj;
import packetObjects.ModifyNodeObj;
import packetObjects.PrefixObj;
import packetObjects.RemoveNodeObj;
import packetObjects.TableObj;

public class UpdateSwitch implements Runnable{

	String packet;
	NodeRepository nodeRepo;
	ProcessUpdates process;
	UpdateMsgsSeen msgsSeen;
	Parse parse;

	public UpdateSwitch(String packet, 
			NodeRepository nodeRepo,
			ProcessUpdates process,
			UpdateMsgsSeen msgsSeen,
			Parse parse){

		this.packet = packet;
		this.nodeRepo = nodeRepo;
		this.process = process;
		this.msgsSeen = msgsSeen;
		this.parse = parse;

	}

	@Override
	public void run() {

		long msgID = parse.parseMsgID(packet);

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

		String action = parse.parseAction(packet);

		switch(action){
		case "addNode" :
			//add the Node
			AddNodeObj addNodeObj = parse.parseAddNodeJson(packet);
			process.addNode(addNodeObj);
			break;

		case "removeNode" :
			//remove a node
			RemoveNodeObj removeNodeObj = parse.parseRemoveNodeJson(packet);
			process.removeNode(removeNodeObj);
			break;

		case "modifyNode" :
			//modify a node
			ModifyNodeObj modifyNodeObj = parse.parseModifyNodeJson(packet);
			process.modifyNode(modifyNodeObj);
			break;

		case "prefix" :
			//do something
			boolean addRemove = parse.parsePrefixAddRemove(packet);
			if(addRemove == true){
				//add the packet
				PrefixObj prefixObj = parse.parsePrefixJson(packet);
				process.addPrefix(prefixObj);
			}else{
				//remove the packet
				PrefixObj prefixObj = parse.parsePrefixJson(packet);
				process.removePrefix(prefixObj);
			}
			break;

		case "hello" :
			boolean requestTable = parse.parseRequestTable(packet);
			if(requestTable == true){
				HelloObj helloObj = parse.parseHelloJson(packet);
				process.processHelloTableRequest(helloObj);
			}else{
				HelloObj helloObj = parse.parseHelloJson(packet);
				process.processHelloHeartBeat(helloObj);
			}
			break;

		case "table" :
			TableObj tableObj = parse.parseTableJson(packet);
			process.processTable(tableObj);
			break;

		default :
			System.out.println("Error in UpdateSwitch - unrecognized packet: dropping packet");
			break;
		}

	}



}
