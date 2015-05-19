package packetObjects;


/**This class is used when a node needs to be removed from the graph</br>
 * name: is the name of the node to be removed</br>
 * msgID: is the unique message ID required for all updates</br>
 * original packet: is class represented as a json string
 * @author spufflez
 *
 */
public class RemoveNodeObj {
	String name;
	String msgID;
	String originalPacket;

	/**
	 * Constructor
	 * @param name
	 * @param msgID
	 * @param originalPacket
	 */
	public RemoveNodeObj(String name, String msgID, String originalPacket){
		this.name = name;
		this.msgID = msgID;
		this.originalPacket = originalPacket;
	}

	/**
	 * Constructor without the original packet set
	 * @param name
	 * @param msgID
	 */
	public RemoveNodeObj(String name, String msgID){
		this.name = name;
		this.msgID = msgID;
	}
	/**
	 * Get the unique message ID 
	 * @return message ID
	 */
	public String getMsgID() {
		return msgID;
	}
	/**
	 * Get the ID of the node to be removed
	 * @return ID of the node to be removed
	 */
	public String getName() {
		return name;
	}
	/**
	 * Set the unique message ID 
	 * @param msgID
	 */
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}
	/**
	 * Set the ID of the node to be removed 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Get the original apcket 
	 * @return
	 */
	public String getOriginalPacket(){
		return originalPacket;
	}
	/**
	 * Set the original Packet 
	 * @param originalPacket
	 */
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}


}
