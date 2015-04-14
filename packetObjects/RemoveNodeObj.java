package packetObjects;


public class RemoveNodeObj {
	String name;
	String msgID;
	String originalPacket;

	public RemoveNodeObj(String name, String msgID, String originalPacket){
		this.name = name;
		this.msgID = msgID;
		this.originalPacket = originalPacket;
	}

	public RemoveNodeObj(String name, String msgID){
		this.name = name;
		this.msgID = msgID;
	}
	public String getMsgID() {
		return msgID;
	}
	public String getName() {
		return name;
	}
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOriginalPacket(){
		return originalPacket;
	}
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}


}
