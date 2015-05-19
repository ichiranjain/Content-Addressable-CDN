package topology;

import packetObjects.LinkObj;
import packetObjects.PacketObj;

/**
 * This class is used by the overlay, it contains functions that the overlay </br>
 * calls to construct and pass packets to the routing layer.
 * 
 * @author spufflez
 *
 */
public class PassToRoutingLayer {

	PacketQueue2 packetQueue2;
	SendPacket sendPacket;

	/**
	 * Constructor
	 * @param packetQueue2
	 */
	public PassToRoutingLayer(PacketQueue2 packetQueue2){
		this.packetQueue2 = packetQueue2;
		this.sendPacket = new SendPacket();
	}



	/**
	 * Creates a add client  packet and places it in the general queue</br>
	 * the cost is not used so it can be any value
	 * @param nodeName
	 * @param nodeCost
	 */	public void addClient(String nodeName, int nodeCost) {
		 LinkObj addClientLinkObj = new LinkObj(nodeName, nodeCost);
		 //create json
		 sendPacket.createAddClient(addClientLinkObj);

		 PacketObj packetObj = new PacketObj(addClientLinkObj.getOriginalPacket(), nodeName, true);
		 //add to the queue
		 packetQueue2.addToGeneralQueue(packetObj);
	 }


	 /**
	  * Creates a remove client packet and places it in the general queue
	  * @param nodeName
	  * @param nodeCost
	  */
	 public void removeClient(String nodeName, int nodeCost) {
		 LinkObj removeClientLinkObj = new LinkObj(nodeName, nodeCost);
		 //create json
		 sendPacket.createRemoveClient(removeClientLinkObj);

		 PacketObj packetObj = new PacketObj(removeClientLinkObj.getOriginalPacket(), nodeName, true);
		 //add to the queue
		 packetQueue2.addToGeneralQueue(packetObj);
	 }


	 /**
	  * Places a packet in the general queue
	  * @param routingPacket
	  * @param fromNode
	  * @param directlyConnectedUpdate
	  */
	 public void addPacket(String routingPacket, String fromNode, boolean directlyConnectedUpdate){

		 PacketObj packetObj = new PacketObj(routingPacket, fromNode, directlyConnectedUpdate);
		 packetQueue2.addToGeneralQueue(packetObj);
	 }

}
