package topology;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import packetObjects.DirectlyConnectedObj;

/**
 * This class keeps track of all the directly connected devices to the cache server</br>
 * 
 * directlyConnectedRouters: is a hash map of the ID's of the directly </br>
 * connected cache servers, the value for this hash map means nothing </br>
 * directlyConnectedClients: is a list of the directly connected clients/servers</br>
 * the value is an entry containing the all the content names the server or client is advertising
 * @author spufflez
 *
 */
public class DirectlyConnectedNodes {

	ConcurrentHashMap<String, Boolean> directlyConnectedRouters; 
	ConcurrentHashMap<String, DirectlyConnectedObj> directlyConnectedClients;

	/**
	 * Constructor
	 */
	public DirectlyConnectedNodes(){
		directlyConnectedRouters = new ConcurrentHashMap<String, Boolean>();
		directlyConnectedClients = new ConcurrentHashMap<String, DirectlyConnectedObj>();
	}

	/**
	 * Gets a hash map of the directly connected cache servers
	 * @return hash map of the directly connected cache server
	 */
	public ConcurrentHashMap<String, Boolean> getDirectlyConnectedRouters() {
		return directlyConnectedRouters;
	}

	/**
	 * Sets the hash map of directly conneced cache servers
	 * @param directlyConnectedRouters
	 */
	public void setDirectlyConnectedRouters(ConcurrentHashMap<String, Boolean> directlyConnectedRouters) {
		this.directlyConnectedRouters = directlyConnectedRouters;
	}

	/**
	 * Adds a directly connected cache server to the hash map
	 * @param routerName
	 */
	public void addDirectlyConnectedRouter(String routerName){
		if(directlyConnectedRouters.containsKey(routerName) == false){
			directlyConnectedRouters.put(routerName, true);
		}
	}

	/**
	 * Removes a directly connected cache server from the hash map 
	 * @param routerName
	 */
	public void removeDirectlyConnectedRouter(String routerName){
		if(directlyConnectedRouters.containsKey(routerName) == true){
			directlyConnectedRouters.remove(routerName);
		}
	}

	/**
	 * Checks if the directly connected cache server is in the list
	 * @param routerName
	 * @return true if it exists and false if it dne
	 */
	public boolean doesDirectlyConnectedRouterExist(String routerName){
		return directlyConnectedRouters.containsKey(routerName);
	}

	/**
	 * Gets a list of the directly connected cache server
	 * @return array list of the directly connected cache servers
	 */
	public String[] getDirectlyConnectedRoutersList(){
		Set<String> keys = directlyConnectedRouters.keySet();
		return keys.toArray(new String[keys.size()]);
	}


	/*
	 * client functions
	 */

	/**
	 * Gets a hash map of the directly connected clients/servers
	 * @return hash map of directly connected clients/servers
	 */
	public ConcurrentHashMap<String, DirectlyConnectedObj> getDirectlyConnectedClients() {
		return directlyConnectedClients;
	}

	/**
	 * Sets the hash map of directly connected clients/servers
	 * @param directlyConnectedClients
	 */
	public void setDirectlyConnectedClients(ConcurrentHashMap<String, DirectlyConnectedObj> directlyConnectedClients) {
		this.directlyConnectedClients = directlyConnectedClients;
	}

	/**
	 * Adds a directly connected client/server to the list 
	 * @param clientName
	 */
	public void addDirectlyConnectedClient(String clientName){
		if(directlyConnectedClients.containsKey(clientName) == false){
			directlyConnectedClients.put(clientName, new DirectlyConnectedObj(clientName));
		}
	}

	/**
	 * Removes a directly connected client/server from the list 
	 * @param clientName
	 */
	public void removeDirectlyConnectedClient(String clientName){
		if(directlyConnectedClients.containsKey(clientName) == true){
			directlyConnectedClients.remove(clientName);
		}
	}

	/**
	 * Checks if the client/server exists in the list 
	 * @param clientName
	 * @return true if it exists and false if dne
	 */
	public boolean doesDirectlyConnectedClientExist(String clientName){
		return directlyConnectedClients.containsKey(clientName);
	}

	/**
	 * Gets a directly connects client/server 
	 * @param clientName
	 * @return gets the directly connected client/server entry
	 */
	public DirectlyConnectedObj getDirectlyConnectedClient(String clientName){
		return directlyConnectedClients.get(clientName);
	}

	/**
	 * Gets a list of the directly clients/servers
	 * @return array of the ID's of the directly connected clients/servers
	 */
	public String[] getDirectlyConnectedClientsList(){
		Set<String> keys = directlyConnectedClients.keySet();
		return keys.toArray(new String[keys.size()]);
	}

	/**
	 * Gets all the directly connected client/server entries</br>
	 * Use this for printing the contents of the list
	 * @return Array List of the all the directly connected client/server entries
	 */
	public ArrayList<String> getClientEntries(){
		Set<String> clients = directlyConnectedClients.keySet();
		ArrayList<String> entries = new ArrayList<String>();
		for(String client : clients){
			entries.add("Client: " + client + " " + directlyConnectedClients.get(client).toString());
		}
		return entries;
	}


}
