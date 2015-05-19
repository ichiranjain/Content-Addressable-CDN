package topology;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import packetObjects.PITEntry;

/**
 * This class is the PIT table for the system </br>
 * It contains a hash map: key == content name</br>
 * value == pit entry
 * @author spufflez
 *
 */
public class PIT {

	//needs a PIT object
	ConcurrentHashMap<String, PITEntry> pit;


	/**
	 * Constructor
	 */
	public PIT(){
		pit = new ConcurrentHashMap<String, PITEntry>();
	}

	/**
	 * Add a pit entry from a cache server to the hash map
	 * @param content
	 * @param requester
	 */
	public void addEntry(String content, String requester ){
		long time = System.nanoTime();
		pit.put(content, new PITEntry(time));
		pit.get(content).addRequester(requester);
	}
	/**
	 * Add a pit entry from a client/server to the hash map
	 * @param content
	 * @param clientRequester
	 */
	public void addClientEntry(String content, String clientRequester ){
		long time = System.nanoTime();
		pit.put(content, new PITEntry(time));
		pit.get(content).addClientRequester(clientRequester);
	}

	/**
	 * Add a pit entry from a cache server to the hash map it an entry does </br>
	 * not already exists and insert an entry if an entry doesn't exist. 
	 * @param content
	 * @return null if an entry does not exist, and the value of the entry if an </br>
	 * does exist
	 */
	public PITEntry addEntryIfItDoesntExist(String content){
		long time = System.nanoTime();
		PITEntry exists = pit.putIfAbsent(content,  new PITEntry(time));
		//pit.get(content).addRequester(requester);
		return exists;
	}

	/**
	 * Add a pit entry from a client/server to the hash map it an entry does </br>
	 * not already exists and insert an entry if an entry doesn't exist. 
	 * @param content
	 * @return null if an entry does not exist, and the value of the entry if an </br>
	 * does exist
	 */
	public PITEntry addClientEntryIfItDoesntExist(String content){
		long time = System.nanoTime();
		PITEntry exists = pit.putIfAbsent(content,  new PITEntry(time));
		//pit.get(content).addClientRequester(requester);
		return exists;
	}

	/**
	 * Removes a PIT entry form the hash map
	 * @param content
	 */
	public void removeEntry(String content){
		if(doesEntryExist(content) == true){
			pit.remove(content);			
		}
	}

	/**
	 * Checks if a PIT entry exists
	 * @param content
	 * @return true if the entry exists and false if dne
	 */
	public boolean doesEntryExist(String content){
		if(pit.containsKey(content) == true){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Gets the PIT entry
	 * @param content
	 * @return PIT entry
	 */
	public PITEntry getRequesters(String content){			
		return pit.get(content);
	}
	/**
	 * Gets the pit entry
	 * @param content
	 * @return PIT entry
	 */
	public PITEntry getClientRequesters(String content){			
		return pit.get(content);
	}

	/**
	 * Adds a cache server to the PIT entry
	 * @param content
	 * @param requester
	 */
	public void addRequester(String content, String requester){
		pit.get(content).addRequester(requester);
	}
	/**
	 * Adds a client/server to the pit entry
	 * @param content
	 * @param requester
	 */
	public void addCLientRequester(String content, String requester){
		pit.get(content).addClientRequester(requester);
	}

	/**
	 * Removes a cache server ffrom the PIT entry
	 * @param content
	 * @param requester
	 */
	public void removeRequester(String content, String requester){
		if(pit.get(content).doesRequesterExist(requester) > -1){
			pit.get(content).removeRequester(requester);
		}
	}
	/**
	 * Removes a client/server from the PIT entry
	 * @param content
	 * @param requester
	 */
	public void removeCLientRequester(String content, String requester){
		if(pit.get(content).doesClientRequesterExist(requester) > -1){
			pit.get(content).removeClientRequester(requester);
		}
	}

	/**
	 * Checks if a cache server exists in the PIT entry
	 * @param content
	 * @param requester
	 * @return true if it exists and false if dne
	 */
	public boolean doesRequesterExist(String content, String requester){
		if(pit.get(content).doesRequesterExist(requester) > -1){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Checks if a client/server exists in the PIT entry
	 * @param content
	 * @param requester
	 * @return true if it exists and false if dne
	 */
	public boolean doesClientRequesterExist(String content, String requester){
		if(pit.get(content).doesClientRequesterExist(requester) > -1){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Gets the size of the list for the cache servers that requested the content
	 * @param content
	 * @return size of the cache server list
	 */
	public int sizeOfRequestersList(String content){
		return pit.get(content).getSizeOfRequesters();
	}
	/**
	 * Gets the size of the client/server list of requestors
	 * @param content
	 * @return size of the client/server list of requestors 
	 */
	public int sizeOfClientRequestersList(String content){
		return pit.get(content).getSizeOfClientRequesters();
	}

	/**
	 * Gets the time stamp for the PIT entry
	 * @param content
	 * @return PIT entry time stamp
	 */
	public long getTime(String content){
		return pit.get(content).getTime();
	}

	/**
	 * Sets the time stamp for the PIT entry
	 * @param content
	 */
	public void setTime(String content){
		pit.get(content).setTime(System.nanoTime());
	}

	/**
	 * Gets the PIT entries in an array for printing 
	 * @return array of cache server entries
	 */
	public ArrayList<String> getPitEntries(){

		Set<String> keys = pit.keySet();
		ArrayList<String> entries = new ArrayList<String>();
		for(String key : keys){
			entries.add(key);
		}
		return entries;

	}

	/**
	 * Gets all the PIT entries for printing and the name of the content requested
	 * @return array of content requested and the requestor
	 */
	public ArrayList<String> getPitNamesAndEntries(){
		ArrayList<String> entries = new ArrayList<String>();
		Set<String> keys = pit.keySet();
		for(String key : keys){
			entries.add(key + ": " + pit.get(key).toString());
		}
		return entries;
	}
}
