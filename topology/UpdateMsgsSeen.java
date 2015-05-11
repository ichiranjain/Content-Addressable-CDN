package topology;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateMsgsSeen {

	ConcurrentHashMap<String, Long> msgIDHM;

	public UpdateMsgsSeen(){
		msgIDHM = new ConcurrentHashMap<String, Long>();
	}

	public void addMsgID(String msgID, Long time){
		msgIDHM.put(msgID, time);
	}

	public void removeMsgID(String msgID){
		msgIDHM.remove(msgID);
	}

	public boolean doesMsgIDExist(String msgID){
		// if(msgIDHM.contains(msgID) == true){
		if (msgIDHM.containsKey(msgID) == true) {
			return true;
		}else{
			return false;
		}
	}

	public ArrayList<String> getListOfMsgIDs(){
		Set<String> keys = msgIDHM.keySet();
		ArrayList<String> msgIDs = new ArrayList<String>();
		for(String key : keys){
			msgIDs.add(key);
		}
		return msgIDs;
	}

	public long getMsgIDTime(String msgID){
		return msgIDHM.get(msgID);
	}

	public void setMsgIDTime(String msgID, long time){
		msgIDHM.put(msgID, time);
	}

	public ArrayList<String> getMsgIDsAndTimes(){
		Set<String> keys = msgIDHM.keySet();
		ArrayList<String> entries = new ArrayList<String>();
		for(String key : keys){
			entries.add("MsgID: " + key + " " + msgIDHM.get(key));
		}
		return entries;
	}



	//	public static void main(String[] args) {
	//		System.out.println("it works");
	//	}
}


