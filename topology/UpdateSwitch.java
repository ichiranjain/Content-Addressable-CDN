package topology;

import java.io.IOException;

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
		//System.out.println("update switch action recieved: " + action);
		//		LinkObj linkObj;
		//		boolean addRemove;
		//		PrefixListObj prefixListObj;
		//		PrefixObj prefixObj;

		//System.out.println("inside update switch::action::" + action);

		switch(action){

		case "addLink" :
			LinkObj addLinkObj = (LinkObj) genericPacketObj.getObj();
			try {
				process.addLink(addLinkObj);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case "removeLink" :
			LinkObj removeLinkObj = (LinkObj) genericPacketObj.getObj();
			try {
				process.removeLink(removeLinkObj);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case "modifyLink" :
			LinkObj modifyLinkObj = (LinkObj) genericPacketObj.getObj();
			try {
				process.modifyLink(modifyLinkObj);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case "modify" : 
			//System.out.println("--MODIFY MSG RECIEVED IN UPDATE SWITCH--");
			ModifyNodeObj modifyNodeObj = (ModifyNodeObj) genericPacketObj.getObj();
			if(doesMsgIDExist(modifyNodeObj.getMsgID()) == false){				
				try {
					process.modifyNode(modifyNodeObj,
							genericPacketObj.getRecievedFromNode());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				//System.out.println("--MODIFY NODE THE NODE DOES NOT EXISTS YET--");
			}
			break;

		case "prefix" :
			//do something
			PrefixObj prefixObj = (PrefixObj) genericPacketObj.getObj();
			if(doesMsgIDExist(prefixObj.getMsgID()) == false){				
				boolean addRemovePrefix = prefixObj.getAddRemoveFlag();
				try {
					if (addRemovePrefix == true) {
						// add the packet
						process.addPrefix(prefixObj,
								genericPacketObj.getRecievedFromNode());
					} else {
						// remove the packet
						process.removePrefix(prefixObj,
								genericPacketObj.getRecievedFromNode());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;

		case "prefixList" : 
			PrefixListObj prefixListObj = (PrefixListObj) genericPacketObj.getObj();
			if(doesMsgIDExist(prefixListObj.getMsgID()) == false){				
				boolean addRemovePrefixList = prefixListObj.getAddRemoveFlag();
				try {
					if (addRemovePrefixList == true) {
						// add the packet
						process.addPrefixList(prefixListObj,
								genericPacketObj.getRecievedFromNode());
					} else {
						// remove the packet
						process.removePrefixList(prefixListObj,
								genericPacketObj.getRecievedFromNode());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;

		case "addClient" : 
			LinkObj addClienLlinkObj = (LinkObj) genericPacketObj.getObj();
			try {
				process.addClientLink(addClienLlinkObj);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case "removeClient" : 
			LinkObj removeClientLinkObj = (LinkObj) genericPacketObj.getObj();
			try {
				process.removeClientLink(removeClientLinkObj);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case "clientPrefix" : 
			PrefixObj clientPrefixObj = (PrefixObj) genericPacketObj.getObj();
			boolean addRemoveClientPrefix = clientPrefixObj.getAddRemoveFlag();
			try {
				if (addRemoveClientPrefix == true) {
					// add the packet
					process.addCLientPrefix(clientPrefixObj,
							genericPacketObj.getRecievedFromNode());
				} else {
					// remove the packet
					process.removeClientPrefix(clientPrefixObj,
							genericPacketObj.getRecievedFromNode());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "clientPrefixList" : 
			PrefixListObj clientPrefixListObj = (PrefixListObj) genericPacketObj.getObj();
			boolean addRemoveClientPrefixList = clientPrefixListObj.getAddRemoveFlag();
			try {
				if (addRemoveClientPrefixList == true) {
					// add the packet
					process.addClientPrefixList(clientPrefixListObj,
							genericPacketObj.getRecievedFromNode());
				} else {
					// remove the packet
					process.removeClientPrefixList(clientPrefixListObj,
							genericPacketObj.getRecievedFromNode());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "neighborRequest" :
			NeighborRequestObj neighborRequestObj = (NeighborRequestObj) genericPacketObj.getObj();
			process.processIntrestRequestForNeighbors(neighborRequestObj);
			//process.requestNeighbors(neighborRequestObj.getFromName());
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
			//msgsSeen.addMsgID(msgID, System.nanoTime());
			return false;
		}
	}



}
