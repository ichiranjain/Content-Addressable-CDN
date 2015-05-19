package topology;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class stores all the update message ID's that have been processed</br>
 * 
 * msgHM: is a hash map that stores the ID with a value as the time stamp
 * @author spufflez
 *
 */
public class UpdateMsgsSeen {

	ConcurrentHashMap<String, Long> msgIDHM;

	/**
	 * Constructor
	 */
	public UpdateMsgsSeen(){
		msgIDHM = new ConcurrentHashMap<String, Long>();
	}

	/**
	 * Add a message ID to the hash map
	 * @param msgID
	 * @param time
	 */
	public void addMsgID(String msgID, Long time){
		msgIDHM.put(msgID, time);
	}

	/**
	 * Remove a message ID from the hash map
	 * @param msgID
	 */
	public void removeMsgID(String msgID){
		msgIDHM.remove(msgID);
	}

	/**
	 * Check if the hash map contains a message ID
	 * @param msgID
	 * @return true if the ID exists and false if it dne
	 */
	public boolean doesMsgIDExist(String msgID){
		// if(msgIDHM.contains(msgID) == true){
		if (msgIDHM.containsKey(msgID) == true) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Gets a list of all message IDs in the Hash map
	 * @return array list of message IDs
	 */
	public ArrayList<String> getListOfMsgIDs(){
		Set<String> keys = msgIDHM.keySet();
		ArrayList<String> msgIDs = new ArrayList<String>();
		for(String key : keys){
			msgIDs.add(key);
		}
		return msgIDs;
	}

	/**
	 * Gets the time stamp for a specific message ID
	 * @param msgID
	 * @return time stamp for the given ID
	 */
	public long getMsgIDTime(String msgID){
		return msgIDHM.get(msgID);
	}

	/**
	 * Sets the time stamp for the given message ID
	 * @param msgID
	 * @param time
	 */
	public void setMsgIDTime(String msgID, long time){
		msgIDHM.put(msgID, time);
	}

	/**
	 * Gets the message ID's and there time stamps, this is used for printing</br>
	 * the contents of the hash map
	 * @return
	 */
	public ArrayList<String> getMsgIDsAndTimes(){
		Set<String> keys = msgIDHM.keySet();
		ArrayList<String> entries = new ArrayList<String>();
		for(String key : keys){
			entries.add("MsgID: " + key + " " + msgIDHM.get(key));
		}
		return entries;
	}

}


