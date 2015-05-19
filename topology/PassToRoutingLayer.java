package topology;

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
	 * Places a packet in the general queue
	 * @param routingPacket
	 * @param fromNode
	 * @param directlyConnectedUpdate
	 */
	public void addPacket(String routingPacket, String fromNode, boolean directlyConnectedUpdate){

		PacketObj packetObj = new PacketObj(routingPacket, fromNode, directlyConnectedUpdate);
		packetQueue2.addToGeneralQueue(packetObj);
		//System.out.println("added to general q");
	}

}
