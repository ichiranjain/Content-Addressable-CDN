package packetObjects;

/**
 * This class is used to when an interest packet is received by a cache server requesting </br>
 * its neighbors and content. This class is used to place that as an object in the </br>
 * update queue. 
 * 
 * Content name: is used, so the data packet can be routed back to the requester </br>
 * origin Router: is the ID of the first cache server the interest packet passed through </br>
 * original packet: is this class represented as a json string</br>
 * nextHop: is the ID of the node to send the packet to
 * @author spufflez
 *
 */
public class NeighborRequestObj {

	String contentName;
	String originRouter;
	String originalPacket;
	String nextHop;

	/**
	 * Constructor
	 * @param contentName
	 * @param originRouter
	 * @param originalPacket
	 * @param nextHop
	 */
	public NeighborRequestObj(String contentName, String originRouter, String originalPacket, String nextHop){
		this.contentName = contentName;
		this.originRouter = originRouter;
		this.originalPacket = originalPacket;
		this.nextHop = nextHop;
	}

	/**
	 * Constructor that does not require an original packet
	 * @param contentName
	 * @param originRouter
	 * @param nextHop
	 */
	public NeighborRequestObj(String contentName, String originRouter, String nextHop){
		this.contentName = contentName;
		this.originRouter = originRouter;
		this.nextHop = nextHop;
	}

	/**
	 * Get the content name
	 * @return
	 */
	public String getContentName() {
		return contentName;
	}

	/**
	 * Set the content name
	 * @param contentName
	 */
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	/**
	 * get the original json representation of the packet 
	 * @return
	 */
	public String getOriginalPacket() {
		return originalPacket;
	}

	/**
	 * Set the original json representation of the packet 
	 * @param originalPacket
	 */
	public void setOriginalPacket(String originalPacket) {
		this.originalPacket = originalPacket;
	}

	/**
	 * Get the ID of the origin cache server that set the request
	 * @return
	 */
	public String getOriginRouter(){
		return originRouter;
	}

	/**
	 * Set the ID of the origin cache server that set the request
	 * @param originRouter
	 */
	public void setOriginRouter(String originRouter){
		this.originRouter = originRouter;
	}

	/**
	 * Get the ID of the Node to send the data packet to 
	 * @return
	 */
	public String getNextHop(){
		return nextHop;
	}

	/**
	 *  Set the ID of the Node to send the data packet to 
	 * @param nextHop
	 */
	public void setNextHop(String nextHop){
		this.nextHop = nextHop;
	}

}
