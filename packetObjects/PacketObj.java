package packetObjects;

/**
 * This class is used by the overlay to pass a packet to the routing layer. 
 * The packet is passed as a string along with the ID of the node it was received from
 * and if the packet is an update packet for a directly connected node.
 * 
 * packet: is the packet as a json string</br>
 * Received from node: is the ID of the node that sent the packet</br>
 * directlyConnectedUpdate: is a boolean stating if the packet is a update </br>
 * for a directly connected node</br>
 * 
 * @author spufflez
 *
 */
public class PacketObj {

	String packet; 
	String recievedFromNode;
	boolean directlyConnectedUpdate;

	/**
	 * Constructor
	 * @param packet
	 * @param recievedFromNode
	 * @param directlyConnectedUpdate
	 */
	public PacketObj(String packet, String recievedFromNode, boolean directlyConnectedUpdate){
		this.packet = packet;
		this.recievedFromNode = recievedFromNode;
		this.directlyConnectedUpdate = directlyConnectedUpdate;
	}

	/**
	 * Get the packet
	 * @return
	 */
	public String getPacket() {
		return packet;
	}

	/**
	 * Set the packet
	 * @param packet
	 */
	public void setPacket(String packet) {
		this.packet = packet;
	}

	/**
	 * Get the ID of the node the packet was sent from
	 * @return
	 */
	public String getRecievedFromNode() {
		return recievedFromNode;
	}

	/**
	 * Set the ID of the node the packet was recieved from 
	 * @param recievedFromNode
	 */
	public void setRecievedFromNode(String recievedFromNode) {
		this.recievedFromNode = recievedFromNode;
	}


	/**
	 * Was this a directly connected up date
	 * @return
	 */
	public boolean getDirectlyConnectedUpdate(){
		return directlyConnectedUpdate;
	}


	/**
	 * Set if the packet is a directly connect update.
	 * @param directlyConnectedUpdate
	 */
	public void setDirectlyConnectedUpdate(boolean directlyConnectedUpdate){
		this.directlyConnectedUpdate = directlyConnectedUpdate;
	}



}
