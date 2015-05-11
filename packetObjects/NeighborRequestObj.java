package packetObjects;

public class NeighborRequestObj {

	String contentName;
	String originRouter;
	String originalPacket;

	public NeighborRequestObj(String fromName, String originRouter, String originalPacket){
		this.contentName = fromName;
		this.originRouter = originRouter;
		this.originalPacket = originalPacket;
	}

	public NeighborRequestObj(String fromName, String originRouter){
		this.contentName = fromName;
		this.originRouter = originRouter;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String fromName) {
		this.contentName = fromName;
	}

	public String getOriginalPacket() {
		return originalPacket;
	}

	public void setOriginalPacket(String originalPacket) {
		this.originalPacket = originalPacket;
	}

	public String getOriginRouter(){
		return originRouter;
	}

	public void setOriginRouter(String originRouter){
		this.originRouter = originRouter;
	}

}
