package packetObjects;

public class IntrestObj {

	String contentName;
	String originRouter;
	int nonce;
	String originalPacket;

	public IntrestObj(String contentName, String originRouter, int nonce, String originalPacket){
		this.contentName = contentName;
		this.originRouter = originRouter;
		this.nonce = nonce;
		this.originalPacket = originalPacket;
	}

	public IntrestObj(String contentName, String originRouter, int nonce){
		this.contentName = contentName;
		this.originRouter = originRouter;
		this.nonce = nonce;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getOriginRouterName() {
		return originRouter;
	}

	public void setOriginRouterName(String senderName) {
		this.originRouter = senderName;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}

	public String getOriginalPacket(){
		return originalPacket;
	}

}
