package packetObjects;

import java.util.ArrayList;


public class PITEntry {

	long time;
	ArrayList<String> requesters;
	ArrayList<String> clientRequesters;

	public PITEntry(long time){
		this.time = time;
		requesters = new ArrayList<String>();
		clientRequesters = new ArrayList<String>();
	}

	public void setTime(long time){
		this.time = time;
	}
	public long getTime(){
		return time;
	}
	public void setRequesters(ArrayList<String> requesters){
		this.requesters = requesters;
	}
	public void setClientRequesters(ArrayList<String> clientRequesters){
		this.clientRequesters = clientRequesters;
	}
	public ArrayList<String> getRequesters(){
		return requesters;
	}
	public ArrayList<String> getClientRequesters(){
		return clientRequesters;
	}
	public void addRequester(String requester){
		requesters.add(requester);
	}
	public void addClientRequester(String clientRequester){
		clientRequesters.add(clientRequester);
	}
	public void removeRequester(String requester){
		int index = doesRequesterExist(requester);
		if(index != -1){
			requesters.remove(index);
		}

	}
	public void removeClientRequester(String clientRequester){
		int index = doesClientRequesterExist(clientRequester);
		if(index != -1){
			clientRequesters.remove(index);
		}

	}
	public int doesRequesterExist(String requester){
		for(int i = 0; i < requesters.size(); i++){
			if(requesters.get(i).equals(requester)){
				return i;
			}
		}
		return -1;
	}
	public int doesClientRequesterExist(String clientRequester){
		for(int i = 0; i < clientRequesters.size(); i++){
			if(clientRequesters.get(i).equals(clientRequester)){
				return i;
			}
		}
		return -1;
	}
	public int getSizeOfRequesters(){
		return requesters.size();
	}
	public int getSizeOfClientRequesters(){
		return clientRequesters.size();
	}

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
