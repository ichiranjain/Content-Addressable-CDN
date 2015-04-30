package topology;

import packetObjects.LinkObj;
import packetObjects.PacketObj;

public class PassToRoutingLayer {

	PacketQueue2 packetQueue2;
	SendPacket sendPacket;

	public PassToRoutingLayer(PacketQueue2 packetQueue2){
		this.packetQueue2 = packetQueue2;
		this.sendPacket = new SendPacket();
	}

	public void addLink(String nodeName, int nodeCost){
		System.out.println("New link to: " + nodeName);
		//System.out.println("creating add link obj");
		//make the obj
		LinkObj addlinkObj = new LinkObj(nodeName, nodeCost);
		//create json
		sendPacket.createAddLinkPacket(addlinkObj);

		PacketObj packetObj = new PacketObj(addlinkObj.getOriginalPacket(), nodeName, true);
		//add to the queue
		packetQueue2.addToGeneralQueue(packetObj);
	}

	public void removeLink(String nodeName, int nodeCost){
		LinkObj removelinkObj = new LinkObj(nodeName, nodeCost);
		//create json
		sendPacket.createRemoveLinkPacket(removelinkObj);

		PacketObj packetObj = new PacketObj(removelinkObj.getOriginalPacket(), nodeName, true);
		//add to the queue
		packetQueue2.addToGeneralQueue(packetObj);
	}

	public void modifyLink(String nodeName, int nodeCost){
		LinkObj modifylinkObj = new LinkObj(nodeName, nodeCost);
		//create json
		sendPacket.createModifyLinkPacket(modifylinkObj);

		PacketObj packetObj = new PacketObj(modifylinkObj.getOriginalPacket(), nodeName, true);
		//add to the queue
		packetQueue2.addToGeneralQueue(packetObj);
	}

	//the cost is not used so it can be any value
	public void addClient(String nodeName, int nodeCost){
		LinkObj addClientLinkObj = new LinkObj(nodeName, nodeCost);
		//create json
		sendPacket.createAddClient(addClientLinkObj);

		PacketObj packetObj = new PacketObj(addClientLinkObj.getOriginalPacket(), nodeName, true);
		//add to the queue
		packetQueue2.addToGeneralQueue(packetObj);
	}

	public void removeClient(String nodeName, int nodeCost){
		LinkObj removeClientLinkObj = new LinkObj(nodeName, nodeCost);
		//create json
		sendPacket.createRemoveClient(removeClientLinkObj);

		PacketObj packetObj = new PacketObj(removeClientLinkObj.getOriginalPacket(), nodeName, true);
		//add to the queue
		packetQueue2.addToGeneralQueue(packetObj);
	}

	public void addPacket(String routingPacket, String fromNode, boolean directlyConnectedUpdate){

		PacketObj packetObj = new PacketObj(routingPacket, fromNode, directlyConnectedUpdate);
		packetQueue2.addToGeneralQueue(packetObj);
		//System.out.println("added to general q");
	}

}
