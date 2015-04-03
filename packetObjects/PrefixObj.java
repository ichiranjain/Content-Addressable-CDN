package packetObjects;
import java.util.ArrayList;


public class PrefixObj {

	String contentName;
	ArrayList<String> advertisers;
	//true == add
	//false == remove
	boolean addRemove;
	String originalPacket;
	long msgID;


	public PrefixObj(String contentName, long msgID, ArrayList<String> advertisers, boolean addRemove, String originalPacket){
		this.contentName = contentName;
		this.advertisers = advertisers;
		this.addRemove = addRemove;
		this.originalPacket = originalPacket;
		this.msgID = msgID;
	}


	public String getContentName() {
		return contentName;
	}


	public void setContentName(String contentName) {
		this.contentName = contentName;
	}


	public ArrayList<String> getAdvertisers() {
		return advertisers;
	}


	public void setAdvertisers(ArrayList<String> advertisers) {
		this.advertisers = advertisers;
	}

	public String getAdvertiser(int index){
		return advertisers.get(index);
	}

	public int indexOFAdvertiser(String name ){
		if(advertisers.contains(name) == true){
			return advertisers.indexOf(name);
		}else{
			return -1;
		}
	}

	public boolean doesAdvertiserExist(String name){
		if(advertisers.contains(name) == true){
			return true;
		}else{
			return false;
		}
	}

	public int sizeOfAdvertisersList(){
		return advertisers.size();
	}
	public void setAddRemove(boolean addRemove){
		this.addRemove = addRemove;
	}
	public boolean getAddRemove(){
		return addRemove;
	}

	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}
	public String getOriginalPacket(){
		return originalPacket;
	}
	public void setMsgID(long msgID){
		this.msgID = msgID;
	}
	public long getMsgID(){
		return msgID;
	}


}
