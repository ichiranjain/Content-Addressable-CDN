package packetObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * This class is used for directly connected clients/servers </br>
 * it stores the client/servers name and the content it is advertising</br>
 * clients and servers are refereed to as clients, since they connect to a chance server.
 * 
 * prefixList: is a hash map of all the content names a client/server is advertising,</br>
 * the value of the hash map is not used</br>
 * clientName: is the ID of the client/server directly connected to the cache server
 * 
 * @author spufflez
 *
 */
public class DirectlyConnectedObj {

	//list of prefixes being advertised
	HashMap<String, Boolean> prefixList;

	//list of prefixes could also be an array list 

	//name of the node 	
	String clientName;

	/**
	 * constructor for a directly connected node with no content being advertised
	 * @param clientName
	 */
	public DirectlyConnectedObj(String clientName){
		this.clientName = clientName;

		//the clients name is also a prefix
		this.prefixList = new HashMap<String, Boolean>();
		this.prefixList.put(clientName, true);
	}

	/**
	 * constructor for a directly connected node, advertising content
	 * @param clientName
	 * @param prefixList
	 */
	public DirectlyConnectedObj(String clientName, HashMap<String, Boolean> prefixList){
		this.clientName = clientName;
		this.prefixList = prefixList;	
	}


	/**
	 * get a hash map of the content a directly connected client/server is advertising
	 * @return hash map of content names
	 */
	public HashMap<String, Boolean> getPrefixList() {
		return prefixList;
	}

	/**
	 * get the content a client/server is advertising as an array list
	 * @return array list of content names
	 */
	public ArrayList<String> getPrefixArrayList(){
		ArrayList<String> prefixArrayList = new ArrayList<String>();
		Set<String> keys = prefixList.keySet();
		for( String key : keys){
			prefixArrayList.add(key);
		}

		return prefixArrayList;
	}

	/**
	 * set the hash map of content a client/server is advertising
	 * @param prefixList
	 */
	public void setPrefixList(HashMap<String, Boolean> prefixList) {
		this.prefixList = prefixList;
	}

	/**
	 * add a content that a client/server is advertising
	 * @param prefix
	 */
	public void addPrefix(String prefix){
		if(prefixList.containsKey(prefix) == false){
			prefixList.put(prefix, true);
		}
	}

	/**
	 * remove a content that a client/server is advertising
	 * @param prefix
	 */
	public void removePrefix(String prefix){
		if(prefixList.containsKey(prefix) == true){
			prefixList.remove(prefix);
		}
	}


	/**
	 * checks of a client/server has the provided content in there list of content
	 * they are advertising
	 * @param prefix
	 * @return true is contained in list false if not in list
	 */
	public boolean doesPrefixExist(String prefix){
		return prefixList.containsKey(prefix);
	}

	/**
	 * get the client/servers ID 
	 * @return client ID
	 */
	public String getClientName() {
		return clientName;
	}


	/**
	 * set the client/servers ID
	 * @param clientName
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	/**
	 * convert the client/servers content list to a string for printing
	 * @see java.lang.Object#toString()
	 */
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
