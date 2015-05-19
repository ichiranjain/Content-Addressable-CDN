package packetObjects;


/**
 * This class is used for sending a content to be added or removed</br>
 * on a cache server from clients, servers, and other cache servers. </br>
 * This object is used to construct a prefix packet sent to cache servers.</br>
 * 
 * PrefixName: is the content name </br>
 * advertiser: is the ID of the node that can process requests for the content </br>
 * addRemoveFlag: is used to designate if the content names in the list should </br>
 * be added or removed</br>
 * msgID: is a unique message id required when updates are sent</br>
 * original packet: is the class represented as a json string packet
 * @author spufflez
 *
 */
public class PrefixObj {

	String prefixName;
	String advertiser;
	//true == add
	//false == remove
	boolean addRemoveFlag;
	String originalPacket;
	String msgID;

	/**
	 * Constructor
	 * @param prefixName
	 * @param msgID
	 * @param advertiser
	 * @param addRemove
	 * @param originalPacket
	 */
	public PrefixObj(String prefixName, String msgID, String advertiser, boolean addRemove, String originalPacket){
		this.prefixName = prefixName;
		this.advertiser = advertiser;
		this.addRemoveFlag = addRemove;
		this.originalPacket = originalPacket;
		this.msgID = msgID;
	}


	/**
	 * Constructor without the original packet
	 * @param prefixName
	 * @param msgID
	 * @param advertiser
	 * @param addRemove
	 */
	public PrefixObj(String prefixName, String msgID, String advertiser, boolean addRemove){
		this.prefixName = prefixName;
		this.advertiser = advertiser;
		this.addRemoveFlag = addRemove;
		this.msgID = msgID;
	}

	/**
	 * Get the content name
	 * @return content name
	 */
	public String getPrefixName() {
		return prefixName;
	}

	/**
	 * Set the content name
	 * @param prefixName
	 */
	public void setPrefixName(String prefixName) {
		this.prefixName = prefixName;
	}

	/**
	 * Get the advertisers ID
	 * @return advertiser
	 */
	public String getAdvertiser() {
		return advertiser;
	}

	/**
	 * Set the advertisers ID
	 * @param advertiser
	 */
	public void setAdvertiser(String advertiser) {
		this.advertiser = advertiser;
	}


	/**
	 * Set the add remove flag
	 * @param addRemove
	 */
	public void setAddRemoveFlag(boolean addRemove){
		this.addRemoveFlag = addRemove;
	}

	/**
	 * Get the add remove flag 
	 * @return add remove flag
	 */
	public boolean getAddRemoveFlag(){
		return addRemoveFlag;
	}

	/**
	 * Set the original packet
	 * @param originalPacket
	 */
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}

	/**
	 * Get the original packet
	 * @return original packet 
	 */
	public String getOriginalPacket(){
		return originalPacket;
	}

	/**
	 * Set the unique message ID
	 * @param msgID
	 */
	public void setMsgID(String msgID){
		this.msgID = msgID;
	}

	/**
	 * Get the unique message ID
	 * @return message ID
	 */
	public String getMsgID(){
		return msgID;
	}


}
