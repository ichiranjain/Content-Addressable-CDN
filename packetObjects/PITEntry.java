package packetObjects;

import java.util.ArrayList;

public class PITEntry {

	long time;
	ArrayList<String> requesters;

	public PITEntry(long time, String requester) {
		this.time = time;
		requesters = new ArrayList<String>();
		requesters.add(requester);
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setRequesters(ArrayList<String> requesters) {
		this.requesters = requesters;
	}

	public ArrayList<String> getRequesters() {
		return requesters;
	}

	public void addRequester(String requester) {
		requesters.add(requester);
	}

	public void removeRequester(String requester) {
		int index = doesRequesterExist(requester);
		if (index != -1) {
			requesters.remove(index);
		}

	}

	public int doesRequesterExist(String requester) {
		for (int i = 0; i < requesters.size(); i++) {
			if (requesters.get(i).equals(requester)) {
				return i;
			}
		}
		return -1;
	}

	public int getSizeOfRequesters() {
		return requesters.size();
	}

	@Override
	public String toString() {
		String string = "";
		string = string + " time: " + time + " ";
		for (String requester : requesters) {
			string = string + requester + " ";
		}
		return string;

	}

}