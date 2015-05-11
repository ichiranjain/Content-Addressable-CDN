package packetObjects;

public class PacketObj {

	String packet; 
	String recievedFromNode;
	boolean directlyConnectedUpdate;

	public PacketObj(String packet, String recievedFromNode, boolean directlyConnectedUpdate){
		this.packet = packet;
		this.recievedFromNode = recievedFromNode;
		this.directlyConnectedUpdate = directlyConnectedUpdate;
	}

	public String getPacket() {
		return packet;
	}

	public void setPacket(String packet) {
		this.packet = packet;
	}

	public String getRecievedFromNode() {
		return recievedFromNode;
	}

	public void setRecievedFromNode(String recievedFromNode) {
		this.recievedFromNode = recievedFromNode;
	}

	public boolean getDirectlyConnectedUpdate(){
		return directlyConnectedUpdate;
	}

	public void setDirectlyConnectedUpdate(boolean directlyConnectedUpdate){
		this.directlyConnectedUpdate = directlyConnectedUpdate;
	}



}
