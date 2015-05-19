package packetObjects;

/**
 * This class is used to represent an interest packet 
 * content Name is the 
 * 
 * content name: is the name of the content being requested</br>
 * originRouter: is the ID of the first cache server the interest packet passed through</br>
 * nonce: the nonce is not used but was added to the class in case it is needed in the future</br>
 * original packet: is the class represented as a json string
 * @author spufflez
 *
 */
public class IntrestObj {

	String contentName;
	String originRouter;
	int nonce;
	String originalPacket;

	/**
	 * Constructor 
	 * @param contentName
	 * @param originRouter
	 * @param nonce
	 * @param originalPacket
	 */
	public IntrestObj(String contentName, String originRouter, int nonce, String originalPacket){
		this.contentName = contentName;
		this.originRouter = originRouter;
		this.nonce = nonce;
		this.originalPacket = originalPacket;
	}


	/**
	 * Constructor that does not require an original packet
	 * @param contentName
	 * @param originRouter
	 * @param nonce
	 */
	public IntrestObj(String contentName, String originRouter, int nonce){
		this.contentName = contentName;
		this.originRouter = originRouter;
		this.nonce = nonce;
	}

	/**
	 * get the content name
	 * @return content name 
	 */
	public String getContentName() {
		return contentName;
	}

	/**
	 * set the content name
	 * @param contentName
	 */
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}


	/**
	 * get the ID of the first cache server the packet has passed through
	 * @return content name
	 */
	public String getOriginRouterName() {
		return originRouter;
	}


	/**
	 * set to the ID of the first cache server the packet hass passed through
	 * @param senderName
	 */
	public void setOriginRouterName(String senderName) {
		this.originRouter = senderName;
	}

	/**
	 * The nonce is currently not used in the system but was added to the interest</br>
	 * packet in case it would be needed in the future. Get the nonce
	 * 
	 * @return nonce
	 */
	public int getNonce() {
		return nonce;
	}

	/**
	 * The nonce is currently not used in the system but was added to the interest</br>
	 * packet in case it would be needed in the future. Set the nonce
	 * 
	 * @param nonce
	 */
	public void setNonce(int nonce) {
		this.nonce = nonce;
	}


	/**
	 * Set to the json string representation of the object,</br>
	 * Used to store the json packet
	 * @param originalPacket
	 */
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}


	/**
	 * Get the original packet as a json string
	 * @return original json packet
	 */
	public String getOriginalPacket(){
		return originalPacket;
	}

}
