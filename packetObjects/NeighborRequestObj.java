package packetObjects;

public class NeighborRequestObj {

	String fromName;
	String originalPacket;

	public NeighborRequestObj(String fromName, String originalPacket){
		this.fromName = fromName;
		this.originalPacket = originalPacket;
	}

	public NeighborRequestObj(String fromName){
		this.fromName = fromName;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getOriginalPacket() {
		return originalPacket;
	}

	public void setOriginalPacket(String originalPacket) {
		this.originalPacket = originalPacket;
	}

}
