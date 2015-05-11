package packetObjects;

public class HelloObj {

	String fromName;
	boolean requestTable;
	String originalPacket;

	public HelloObj(String fromNode, boolean requestTable){
		this.fromName = fromNode;
		this.requestTable = requestTable;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public boolean getRequestTable() {
		return requestTable;
	}

	public void setRequestTable(boolean requestTable) {
		this.requestTable = requestTable;
	}

	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}

	public String getOriginalPacket(){
		return originalPacket;
	}
}
