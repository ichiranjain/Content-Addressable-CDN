package topology;

import packetObjects.PacketObj;

public class PassToRoutingLayer {

	PacketQueue2 packetQueue2;
	SendPacket sendPacket;

	public PassToRoutingLayer(PacketQueue2 packetQueue2){
		this.packetQueue2 = packetQueue2;
		this.sendPacket = new SendPacket();
	}

	public void addPacket(String routingPacket, String fromNode, boolean directlyConnectedUpdate){

		PacketObj packetObj = new PacketObj(routingPacket, fromNode, directlyConnectedUpdate);
		packetQueue2.addToGeneralQueue(packetObj);
		//System.out.println("added to general q");
	}

}
