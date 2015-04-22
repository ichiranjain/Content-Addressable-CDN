package topology;

import packetObjects.GenericPacketObj;
import packetObjects.LinkObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;

public class UpdateSwitch implements Runnable{

	//PacketObj packetObj;
	NodeRepository nodeRepo;
	ProcessUpdates process;
	UpdateMsgsSeen msgsSeen;
	Parse2 parse2;
	@SuppressWarnings("rawtypes")
	GenericPacketObj genericPacketObj;

	@SuppressWarnings("rawtypes")
	public UpdateSwitch(GenericPacketObj genericPacketObj, 
			NodeRepository nodeRepo,
			FIB fib,
			DirectlyConnectedNodes directlyConnectedNodes,
			UpdateMsgsSeen updateMsgsSeen){

		this.genericPacketObj = genericPacketObj;
		this.nodeRepo = nodeRepo;
		this.msgsSeen = updateMsgsSeen;
		this.process = new ProcessUpdates(nodeRepo, updateMsgsSeen, fib, directlyConnectedNodes);
		this.parse2 = new Parse2();

	}

	@Override
	public void run() {

		//String msgID = parse.parseMsgID(packetObj.getPacket());

		//		//has the update been seen before
		//		if(msgsSeen.doesMsgIDExist(msgID) == true){
		//			//if so do not process the update, drop the packet
		//			//this update has looped around
		//			return;
		//		}else{
		//			//this is a new msg ... so add it to the msgs seen 
		//			msgsSeen.addMsgID(msgID, System.nanoTime());
		//		}


		//String type = parse.parseType(packet);

		//String action = parse.parseAction(packetObj.getPacket());
		String action = genericPacketObj.getAction();
		//		LinkObj linkObj;
		//		boolean addRemove;
		//		PrefixListObj prefixListObj;
		//		PrefixObj prefixObj;

		switch(action){

		case "addLink" :
			LinkObj addLinkObj = (LinkObj) genericPacketObj.getObj();
			process.addLink(addLinkObj);
			break;

		case "removeLink" :
			LinkObj removeLinkObj = (LinkObj) genericPacketObj.getObj();
			process.removeLink(removeLinkObj);
			break;

		case "modifyLink" :
			LinkObj modifyLinkObj = (LinkObj) genericPacketObj.getObj();
			process.modifyLink(modifyLinkObj);
			break;

		case "modify" : 
			ModifyNodeObj modifyNodeObj = (ModifyNodeObj) genericPacketObj.getObj();
			if(doesMsgIDExist(modifyNodeObj.getMsgID()) == false){				
				process.modifyNode(modifyNodeObj, genericPacketObj.getRecievedFromNode());
			}
			break;

		case "prefix" :
			//do something
			PrefixObj prefixObj = (PrefixObj) genericPacketObj.getObj();
			if(doesMsgIDExist(prefixObj.getMsgID()) == false){				
				boolean addRemovePrefix = prefixObj.getAddRemoveFlag();
				if(addRemovePrefix == true){
					//add the packet
					process.addPrefix(prefixObj, genericPacketObj.getRecievedFromNode());
				}else{
					//remove the packet
					process.removePrefix(prefixObj, genericPacketObj.getRecievedFromNode());
				}
			}
			break;

		case "prefixList" : 
			PrefixListObj prefixListObj = (PrefixListObj) genericPacketObj.getObj();
			if(doesMsgIDExist(prefixListObj.getMsgID()) == false){				
				boolean addRemovePrefixList = prefixListObj.getAddRemoveFlag();
				if(addRemovePrefixList == true){
					//add the packet
					process.addPrefixList(prefixListObj, genericPacketObj.getRecievedFromNode());
				}else{
					//remove the packet
					process.removePrefixList(prefixListObj, genericPacketObj.getRecievedFromNode());
				}
			}
			break;

		case "addClient" : 
			LinkObj addClienLlinkObj = (LinkObj) genericPacketObj.getObj();
			process.addClientLink(addClienLlinkObj);
			break;

		case "removeClient" : 
			LinkObj removeClientLinkObj = (LinkObj) genericPacketObj.getObj();
			process.removeClientLink(removeClientLinkObj);
			break;

		case "clientPrefix" : 
			PrefixObj clientPrefixObj = (PrefixObj) genericPacketObj.getObj();
			boolean addRemoveClientPrefix = clientPrefixObj.getAddRemoveFlag();
			if(addRemoveClientPrefix == true){
				//add the packet
				process.addCLientPrefix(clientPrefixObj, genericPacketObj.getRecievedFromNode());
			}else{
				//remove the packet
				process.removeClientPrefix(clientPrefixObj, genericPacketObj.getRecievedFromNode());
			}
			break;

		case "clientPrefixList" : 
			PrefixListObj clientPrefixListObj = (PrefixListObj) genericPacketObj.getObj();
			boolean addRemoveClientPrefixList = clientPrefixListObj.getAddRemoveFlag();
			if(addRemoveClientPrefixList == true){
				//add the packet
				process.addClientPrefixList(clientPrefixListObj, genericPacketObj.getRecievedFromNode());
			}else{
				//remove the packet
				process.removeClientPrefixList(clientPrefixListObj, genericPacketObj.getRecievedFromNode());
			}
			break;

		case "neighborRequest" :
			NeighborRequestObj neighborRequestObj = (NeighborRequestObj) genericPacketObj.getObj();
			process.requestNeighbors(neighborRequestObj.getFromName());
			break;

		case "prefixResponse" :
			PrefixListObj prefixListObjResponse = (PrefixListObj) genericPacketObj.getObj();
			//if(doesMsgIDExist(prefixListObjResponse.getMsgID()) == false){				
			process.processPrefixListResponse(prefixListObjResponse);
			//}
			break;

		case "neighborResponse" :

			ModifyNodeObj modifyNodeObjResponse = (ModifyNodeObj) genericPacketObj.getObj();
			//if(doesMsgIDExist(modifyNodeObjResponse.getMsgID()) == false){
			process.processNeighborsResponse(modifyNodeObjResponse);
			//}
			break;

		default :
			System.out.println("Error in UpdateSwitch - unrecognized packet: dropping packet");
			break;
		}

	}

	public boolean doesMsgIDExist(String msgID){
		//has the update been seen before
		if(msgsSeen.doesMsgIDExist(msgID) == true){
			//if so do not process the update, drop the packet
			//this update has looped around
			return true;
		}else{
			//this is a new msg ... so add it to the msgs seen 
			msgsSeen.addMsgID(msgID, System.nanoTime());
			return false;
		}
	}



}
