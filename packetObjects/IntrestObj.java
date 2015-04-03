package packetObjects;

public class IntrestObj {

	String contentName;
	String senderName;
	int nonce;
	String originalPacket;

	public IntrestObj(String contentName, String senderName, int nonce, String originalPacket){
		this.contentName = contentName;
		this.senderName = senderName;
		this.nonce = nonce;
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

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}


}
