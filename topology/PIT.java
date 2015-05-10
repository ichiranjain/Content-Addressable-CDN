package topology;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import packetObjects.PITEntry;

public class PIT {

	//needs a PIT object
	ConcurrentHashMap<String, PITEntry> pit;


	public PIT(){
		pit = new ConcurrentHashMap<String, PITEntry>();
	}

	public void addEntry(String content, String requester ){
		long time = System.nanoTime();
		pit.put(content, new PITEntry(time));
		pit.get(content).addRequester(requester);
	}
	public void addClientEntry(String content, String clientRequester ){
		long time = System.nanoTime();
		pit.put(content, new PITEntry(time));
		pit.get(content).addClientRequester(clientRequester);
	}

	public PITEntry addEntryIfItDoesntExist(String content){
		long time = System.nanoTime();
		PITEntry exists = pit.putIfAbsent(content,  new PITEntry(time));
		//pit.get(content).addRequester(requester);
		return exists;
	}

	public PITEntry addClientEntryIfItDoesntExist(String content){
		long time = System.nanoTime();
		PITEntry exists = pit.putIfAbsent(content,  new PITEntry(time));
		//pit.get(content).addClientRequester(requester);
		return exists;
	}

	public void removeEntry(String content){
		if(doesEntryExist(content) == true){
			pit.remove(content);			
		}
	}

	public boolean doesEntryExist(String content){
		if(pit.containsKey(content) == true){
			return true;
		}else{
			return false;
		}
	}

	public PITEntry getRequesters(String content){			
		return pit.get(content);
	}
	public PITEntry getClientRequesters(String content){			
		return pit.get(content);
	}

	public void addRequester(String content, String requester){
		pit.get(content).addRequester(requester);
	}
	public void addCLientRequester(String content, String requester){
		pit.get(content).addClientRequester(requester);
	}

	public void removeRequester(String content, String requester){
		if(pit.get(content).doesRequesterExist(requester) > -1){
			pit.get(content).removeRequester(requester);
		}
	}
	public void removeCLientRequester(String content, String requester){
		if(pit.get(content).doesClientRequesterExist(requester) > -1){
			pit.get(content).removeClientRequester(requester);
		}
	}

	public boolean doesRequesterExist(String content, String requester){
		if(pit.get(content).doesRequesterExist(requester) > -1){
			return true;
		}else{
			return false;
		}
	}
	public boolean doesClientRequesterExist(String content, String requester){
		if(pit.get(content).doesClientRequesterExist(requester) > -1){
			return true;
		}else{
			return false;
		}
	}

	public int sizeOfRequestersList(String content){
		return pit.get(content).getSizeOfRequesters();
	}
	public int sizeOfClientRequestersList(String content){
		return pit.get(content).getSizeOfClientRequesters();
	}

	public long getTime(String content){
		return pit.get(content).getTime();
	}

	public void setTime(String content){
		pit.get(content).setTime(System.nanoTime());
	}

	public ArrayList<String> getPitEntries(){

		Set<String> keys = pit.keySet();
		ArrayList<String> entries = new ArrayList<String>();
		for(String key : keys){
			entries.add(key);
		}
		return entries;

	}

	public ArrayList<String> getPitNamesAndEntries(){
		ArrayList<String> entries = new ArrayList<String>();
		Set<String> keys = pit.keySet();
		for(String key : keys){
			entries.add(key + ": " + pit.get(key).toString());
		}
		return entries;
	}
}
