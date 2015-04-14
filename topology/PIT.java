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
		pit.put(content, new PITEntry(time, requester));
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

	public void addRequester(String content, String requester){
		pit.get(content).addRequester(requester);
	}

	public void removeRequester(String content, String requester){
		if(pit.get(content).doesRequesterExist(requester) > -1){
			pit.get(content).removeRequester(requester);
		}
	}

	public boolean doesRequesterExist(String content, String requester){
		if(pit.get(content).doesRequesterExist(requester) > -1){
			return true;
		}else{
			return false;
		}
	}

	public int sizeOfRequestersList(String content){
		return pit.get(content).getSizeOfRequesters();
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
}
