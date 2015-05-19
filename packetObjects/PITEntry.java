package packetObjects;

import java.util.ArrayList;


/**
 * This class is a PIT entry and stores the following information</br>
 * This class has 2 lists, 1 for cache server requests and 1 for client requests</br>
 * This class also stores the Time Stamp for the PIT entry.</br>
 * There are 2 lists because if a client/server that requested the data dies</br>
 * the packet is dropped, but if a packet was requested by another cache server </br>
 * an attempt is made to route the packet back to the cache server </br>
 * 
 * time: is the time stamp for the PIT entry</br>
 * requesters: is a list of all the cache servers that requested the packet</br>
 * cleintRequesters: is a list of all the client/servers that requested the packet</br>
 * @author spufflez
 *
 */
public class PITEntry {

	long time;
	ArrayList<String> requesters;
	ArrayList<String> clientRequesters;

	/**
	 * Constructor
	 * @param time
	 */
	public PITEntry(long time){
		this.time = time;
		requesters = new ArrayList<String>();
		clientRequesters = new ArrayList<String>();
	}

	/**
	 * Set the PIT entry time stamp
	 * @param time
	 */
	public void setTime(long time){
		this.time = time;
	}
	/**
	 * Get the PIT entry time stamp
	 * @return time stamp
	 */
	public long getTime(){
		return time;
	}
	/**
	 * Set the cache servers that requested the content
	 * @param requesters
	 */
	public void setRequesters(ArrayList<String> requesters){
		this.requesters = requesters;
	}
	/**
	 * Set the client/servers that requested the content
	 * @param clientRequesters
	 */
	public void setClientRequesters(ArrayList<String> clientRequesters){
		this.clientRequesters = clientRequesters;
	}
	/**
	 * Get an array list of the cache servers that requested the content
	 * @return
	 */
	public ArrayList<String> getRequesters(){
		return requesters;
	}
	/**
	 * Get a list of the clients/servers that requested the content 
	 * @return
	 */
	public ArrayList<String> getClientRequesters(){
		return clientRequesters;
	}
	/**
	 * Add a cache server to the requestors list
	 * @param requester
	 */
	public void addRequester(String requester){
		requesters.add(requester);
	}
	/**
	 * Add a client/server to the client requestors list 
	 * @param clientRequester
	 */
	public void addClientRequester(String clientRequester){
		clientRequesters.add(clientRequester);
	}
	/**
	 * Remove a cache server fromt he requestors list 
	 * @param requester
	 */
	public void removeRequester(String requester){
		int index = doesRequesterExist(requester);
		if(index != -1){
			requesters.remove(index);
		}

	}
	/**
	 * Remove a client/server from the client requestors list 
	 * @param clientRequester
	 */
	public void removeClientRequester(String clientRequester){
		int index = doesClientRequesterExist(clientRequester);
		if(index != -1){
			clientRequesters.remove(index);
		}

	}
	/**
	 * Check if a cache server exists in the requesters list 
	 * @param requester
	 * @return index if it exists, -1 if dne
	 */
	public int doesRequesterExist(String requester){
		for(int i = 0; i < requesters.size(); i++){
			if(requesters.get(i).equals(requester)){
				return i;
			}
		}
		return -1;
	}
	/**
	 * check if a client/server exists in the client requesters list 
	 * @param clientRequester
	 * @return index if it exists, -1 if dne
	 */
	public int doesClientRequesterExist(String clientRequester){
		for(int i = 0; i < clientRequesters.size(); i++){
			if(clientRequesters.get(i).equals(clientRequester)){
				return i;
			}
		}
		return -1;
	}
	/**
	 * Get the size of the cache server requesters list 
	 * @return
	 */
	public int getSizeOfRequesters(){
		return requesters.size();
	}
	/**
	 * Get the size of the cleint/server requesters list 
	 * @return
	 */
	public int getSizeOfClientRequesters(){
		return clientRequesters.size();
	}

	/**
	 * Get the cache server and client/server requesters as a string for printing
	 */
	@Override
	public String toString(){
		String string = "";
		string  = string + " time: " + time + " ";
		for(String requester : requesters){
			string = string + requester + ": ";
		}
		for(String clientRequester : clientRequesters){
			string = string + clientRequester + "; ";
		}
		return string;

	}

}
