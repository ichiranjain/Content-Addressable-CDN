package packetObjects;

import java.util.ArrayList;

/**
 * This class is used for sending a list of content to be added or removed</br>
 * on a cache server from clients, servers, and other cache servers. </br>
 * This object is used to construct a prefixList packet sent to cache servers.</br>
 * 
 * PrefixList: is a list of content names </br>
 * advertiser: is the ID of the node that can process requests for the content </br>
 * addRemoveFlag: is used to designate if the content names in the list should </br>
 * be added or removed</br>
 * msgID: is a unique message id required when updates are sent</br>
 * original packet: is the class represented as a json string packet
 * @author spufflez
 *
 */
public class PrefixListObj {

	ArrayList<String> prefixList;
	String advertiser;

	//true == add, false == remove
	boolean addRemoveFlag;
	String msgID;
	String originalPacket;

	/**
	 * Constructor
	 * @param prefixList
	 * @param advertiser
	 * @param addRemove
	 * @param msgID
	 */
	public PrefixListObj(ArrayList<String> prefixList, String advertiser, boolean addRemove, String msgID){
		this.prefixList = prefixList;
		this.advertiser = advertiser;
		this.addRemoveFlag = addRemove;
		this.msgID = msgID;
	}

	/**
	 * Get the list of content names
	 * @return content name list
	 */
	public ArrayList<String> getPrefixList() {
		return prefixList;
	}

	/**
	 * Set the list of Content names
	 * @param prefixList
	 */
	public void setPrefixList(ArrayList<String> prefixList) {
		this.prefixList = prefixList;
	}

	/**
	 * Get the length of the list of content names
	 * @return list size
	 */
	public int getPrefixListLength(){
		return prefixList.size();
	}

	/**
	 * Get a content name at the provided index
	 * @param index
	 * @return content name
	 */
	public String getPrefix(int index){
		return prefixList.get(index);
	}

	/**
	 * Get the advertiser ID
	 * @return advertiser ID
	 */
	public String getAdvertiser() {
		return advertiser;
	}

	/**
	 * Set the advertiser ID
	 * @param advertiser
	 */
	public void setAdvertiser(String advertiser) {
		this.advertiser = advertiser;
	}

	/**
	 * Get the addRemove flag
	 * @return add remove flag 
	 */
	public boolean isAddRemoveFlag() {
		return addRemoveFlag;
	}

	/**
	 * Set the add remove flag
	 * @param addRemoveFlag
	 */
	public void setAddRemoveFlag(boolean addRemoveFlag) {
		this.addRemoveFlag = addRemoveFlag;
	}

	/**
	 * Get the add remove flag
	 * @return
	 */
	public boolean getAddRemoveFlag(){
		return addRemoveFlag;
	}

	/**
	 * get the unique message ID
	 * @return
	 */
	public String getMsgID() {
		return msgID;
	}

	/**
	 * Set the unique message ID
	 * @param msgID
	 */
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	/**
	 * Get the original Packet
	 * @return
	 */
	public String getOriginalPacket() {
		return originalPacket;
	}

	/**
	 * Set the original Packet
	 * @param originalPacket
	 */
	public void setOriginalPacket(String originalPacket) {
		this.originalPacket = originalPacket;
	}


}
