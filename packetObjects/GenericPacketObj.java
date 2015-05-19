package packetObjects;

/**
 * After a packet is parsed into an object, it is wrapped in this class</br>
 * so it can be stored in a queue.
 * 
 * action: is the action the packet is used for, this will </br>
 * identify the type of object stored in this class.</br>
 * Received: from Node is the ID of the node that sent the packet</br>
 * obj: is the object being stored 
 * @author spufflez
 *
 * @param <K>
 */
public class GenericPacketObj<K> {

	String action;
	String recievedFromNode;
	K obj;

	/**
	 * Constructor for a generic packet object
	 * @param action
	 * @param recievedFromNode
	 * @param obj
	 */
	public GenericPacketObj(String action, String recievedFromNode, K obj){
		this.action = action;
		this.recievedFromNode = recievedFromNode;
		this.obj = obj;
	}


	/**
	 * get the action of the obj.</br>
	 * Each obj action is used to identify the type of object being stored </br>
	 * 
	 * @return action
	 */
	public String getAction() {
		//GenericPacketObj<LinkObj> o = new GenericPacketObj<LinkObj>("action", "recFrom", new LinkObj("as"));
		return action;
	}

	/**
	 * The action is a string used in a switch to identify how to process the contained obj
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * get the ID of the node that sent the packet to this cache server
	 * @return
	 */
	public String getRecievedFromNode() {
		return recievedFromNode;
	}

	/**
	 * set the received from node string to the ID of the node that sent the packet</br>
	 * to this cache server
	 * @param recievedFromNode
	 */
	public void setRecievedFromNode(String recievedFromNode) {
		this.recievedFromNode = recievedFromNode;
	}

	/**
	 * gets the obj being stored
	 * @return
	 */
	public Object getObj() {
		return obj;
	}

	/**
	 * sets the object to be stored.
	 * @param obj
	 */
	public void setObj(K obj) {
		this.obj = obj;
	}
}
