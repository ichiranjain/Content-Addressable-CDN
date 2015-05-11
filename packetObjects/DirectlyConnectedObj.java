package packetObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DirectlyConnectedObj {

	//list of prefixes being advertised
	HashMap<String, Boolean> prefixList;

	//list of prefixes could also be an array list 

	//name of the node 	
	String clientName;

	public DirectlyConnectedObj(String clientName){
		this.clientName = clientName;

		//the clients name is also a prefix
		this.prefixList = new HashMap<String, Boolean>();
		this.prefixList.put(clientName, true);
	}

	public DirectlyConnectedObj(String clientName, HashMap<String, Boolean> prefixList){
		this.clientName = clientName;
		this.prefixList = prefixList;	
	}


	public HashMap<String, Boolean> getPrefixList() {
		return prefixList;
	}

	public ArrayList<String> getPrefixArrayList(){
		ArrayList<String> prefixArrayList = new ArrayList<String>();
		Set<String> keys = prefixList.keySet();
		for( String key : keys){
			prefixArrayList.add(key);
		}

		return prefixArrayList;
	}

	public void setPrefixList(HashMap<String, Boolean> prefixList) {
		this.prefixList = prefixList;
	}

	public void addPrefix(String prefix){
		if(prefixList.containsKey(prefix) == false){
			prefixList.put(prefix, true);
		}
	}

	public void removePrefix(String prefix){
		if(prefixList.containsKey(prefix) == true){
			prefixList.remove(prefix);
		}
	}

	public boolean doesPrefixExist(String prefix){
		return prefixList.containsKey(prefix);
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public String toString(){
		Set<String> keys = prefixList.keySet();
		String entry = "";
		for(String key : keys){
			entry = entry + " " +  key;
		}
		return entry;
	}


}
