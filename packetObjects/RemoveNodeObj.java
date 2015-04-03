package packetObjects;


public class RemoveNodeObj {
	String name;
	long msgID;
	String originalPacket;

	public RemoveNodeObj(String name, long msgID, String originalPacket){
		this.name = name;
		this.msgID = msgID;
		this.originalPacket = originalPacket;
	}
	public long getMsgID() {
		return msgID;
	}
	public String getName() {
		return name;
	}
	public void setMsgID(long msgID) {
		this.msgID = msgID;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOriginalPacket(){
		return originalPacket;
	}

}
