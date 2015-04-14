package packetObjects;

import java.util.ArrayList;

public class PrefixListObj {

	ArrayList<String> prefixList;
	String advertiser;

	//true == add, false == remove
	boolean addRemoveFlag;
	String msgID;
	String originalPacket;

	public PrefixListObj(ArrayList<String> prefixList, String advertiser, boolean addRemove, String msgID){
		this.prefixList = prefixList;
		this.advertiser = advertiser;
		this.addRemoveFlag = addRemove;
		this.msgID = msgID;
	}

	public ArrayList<String> getPrefixList() {
		return prefixList;
	}

	public void setPrefixList(ArrayList<String> prefixList) {
		this.prefixList = prefixList;
	}

	public int getPrefixListLength(){
		return prefixList.size();
	}

	public String getPrefix(int index){
		return prefixList.get(index);
	}

	public String getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(String advertiser) {
		this.advertiser = advertiser;
	}

	public boolean isAddRemoveFlag() {
		return addRemoveFlag;
	}

	public void setAddRemoveFlag(boolean addRemoveFlag) {
		this.addRemoveFlag = addRemoveFlag;
	}

	public boolean getAddRemoveFlag(){
		return addRemoveFlag;
	}

	public String getMsgID() {
		return msgID;
	}

	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	public String getOriginalPacket() {
		return originalPacket;
	}

	public void setOriginalPacket(String originalPacket) {
		this.originalPacket = originalPacket;
	}


}
