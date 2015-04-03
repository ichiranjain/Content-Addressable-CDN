package packetObjects;

public class DataObj {

	String contentName;
	String senderName;
	byte flag;
	String data;
	String originalPacket;

	public DataObj(String contentName, String senderName, byte flag, String data, String originalPacket){
		this.contentName = contentName;
		this.senderName = senderName;
		if(flag > 2){
			flag = 2;
		}
		if(flag < 0){
			flag = 0;
		}
		this.flag = flag;
		this.data = data;
		this.originalPacket = originalPacket;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		if(flag > 2){
			flag = 2;
		}
		if(flag < 0){
			flag = 0;
		}
		this.flag = flag;
	}

	public void setData(String data){
		this.data = data;
	}

	public String getData(){
		return data;
	}

	public String getOriginalPacket(){
		return originalPacket;
	}

	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}


}
