package topology;

import java.util.concurrent.ConcurrentHashMap;

public class UpdateMsgsSeen {

	ConcurrentHashMap<Long, Long> msgIDs;

	public UpdateMsgsSeen(){
		msgIDs = new ConcurrentHashMap<Long, Long>();
	}

	public void addMsgID(Long msgID, Long time){
		msgIDs.put(msgID, time);
	}

	public void removeMsgID(Long msgID){
		msgIDs.remove(msgID);
	}

	public boolean doesMsgIDExist(long msgID){
		if(msgIDs.contains(msgID) == true){
			return true;
		}else{
			return false;
		}
	}

	//	public static void main(String[] args) {
	//		System.out.println("it works");
	//	}
}


