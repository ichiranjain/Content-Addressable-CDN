package packetObjects;


public class PrefixObj {

	String prefixName;
	String advertiser;
	//true == add
	//false == remove
	boolean addRemoveFlag;
	String originalPacket;
	String msgID;


	public PrefixObj(String prefixName, String msgID, String advertiser, boolean addRemove, String originalPacket){
		this.prefixName = prefixName;
		this.advertiser = advertiser;
		this.addRemoveFlag = addRemove;
		this.originalPacket = originalPacket;
		this.msgID = msgID;
	}

	public PrefixObj(String prefixName, String msgID, String advertiser, boolean addRemove){
		this.prefixName = prefixName;
		this.advertiser = advertiser;
		this.addRemoveFlag = addRemove;
		this.msgID = msgID;
	}


	public String getPrefixName() {
		return prefixName;
	}


	public void setPrefixName(String prefixName) {
		this.prefixName = prefixName;
	}


	public String getAdvertiser() {
		return advertiser;
	}


	public void setAdvertiser(String advertiser) {
		this.advertiser = advertiser;
	}

	//
	//	public int indexOFAdvertiser(String name ){
	//		if(advertisers.contains(name) == true){
	//			return advertisers.indexOf(name);
	//		}else{
	//			return -1;
	//		}
	//	}
	//
	//	public boolean doesAdvertiserExist(String name){
	//		if(advertisers.contains(name) == true){
	//			return true;
	//		}else{
	//			return false;
	//		}
	//	}
	//
	//	public int sizeOfAdvertisersList(){
	//		return advertisers.size();
	//	}
	public void setAddRemoveFlag(boolean addRemove){
		this.addRemoveFlag = addRemove;
	}
	public boolean getAddRemoveFlag(){
		return addRemoveFlag;
	}

	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}
	public String getOriginalPacket(){
		return originalPacket;
	}
	public void setMsgID(String msgID){
		this.msgID = msgID;
	}
	public String getMsgID(){
		return msgID;
	}


}
