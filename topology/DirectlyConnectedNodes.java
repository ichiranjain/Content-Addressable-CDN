package topology;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import packetObjects.DirectlyConnectedObj;

public class DirectlyConnectedNodes {

	ConcurrentHashMap<String, Boolean> directlyConnectedRouters; 
	ConcurrentHashMap<String, DirectlyConnectedObj> directlyConnectedClients;

	public DirectlyConnectedNodes(){
		directlyConnectedRouters = new ConcurrentHashMap<String, Boolean>();
		directlyConnectedClients = new ConcurrentHashMap<String, DirectlyConnectedObj>();
	}

	public ConcurrentHashMap<String, Boolean> getDirectlyConnectedRouters() {
		return directlyConnectedRouters;
	}

	public void setDirectlyConnectedRouters(ConcurrentHashMap<String, Boolean> directlyConnectedRouters) {
		this.directlyConnectedRouters = directlyConnectedRouters;
	}

	public void addDirectlyConnectedRouter(String routerName){
		if(directlyConnectedRouters.containsKey(routerName) == false){
			directlyConnectedRouters.put(routerName, true);
		}
	}

	public void removeDirectlyConnectedRouter(String routerName){
		if(directlyConnectedRouters.containsKey(routerName) == true){
			directlyConnectedRouters.remove(routerName);
		}
	}

	public boolean doesDirectlyConnectedRouterExist(String routerName){
		return directlyConnectedRouters.containsKey(routerName);
	}

	public String[] getDirectlyConnectedRoutersList(){
		Set<String> keys = directlyConnectedRouters.keySet();
		return keys.toArray(new String[keys.size()]);
	}


	/*
	 * client functions
	 */

	public ConcurrentHashMap<String, DirectlyConnectedObj> getDirectlyConnectedClients() {
		return directlyConnectedClients;
	}

	public void setDirectlyConnectedClients(ConcurrentHashMap<String, DirectlyConnectedObj> directlyConnectedClients) {
		this.directlyConnectedClients = directlyConnectedClients;
	}

	public void addDirectlyConnectedClient(String clientName){
		if(directlyConnectedClients.containsKey(clientName) == false){
			directlyConnectedClients.put(clientName, new DirectlyConnectedObj(clientName));
		}
	}

	public void removeDirectlyConnectedClient(String clientName){
		if(directlyConnectedClients.containsKey(clientName) == true){
			directlyConnectedClients.remove(clientName);
		}
	}

	public boolean doesDirectlyConnectedClientExist(String clientName){
		return directlyConnectedClients.containsKey(clientName);
	}

	public DirectlyConnectedObj getDirectlyConnectedClient(String clientName){
		return directlyConnectedClients.get(clientName);
	}

	public String[] getDirectlyConnectedClientsList(){
		Set<String> keys = directlyConnectedClients.keySet();
		return keys.toArray(new String[keys.size()]);
	}

	public ArrayList<String> getClientEntries(){
		Set<String> clients = directlyConnectedClients.keySet();
		ArrayList<String> entries = new ArrayList<String>();
		for(String client : clients){
			entries.add("Client: " + client + " " + directlyConnectedClients.get(client).toString());
		}
		return entries;
	}


}
