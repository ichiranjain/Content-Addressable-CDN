package packetObjects;
import java.util.ArrayList;

import topology.NeighborAndCostStrings;


public class ModifyNodeObj {
	String name;
	ArrayList<NeighborAndCostStrings> neighbors;
	String msgID;
	String originalPacket;

	public ModifyNodeObj(String name, ArrayList<NeighborAndCostStrings> neighbors, String msgID){
		this.name = name;
		this.neighbors = neighbors;
		this.msgID = msgID;
	}

	public ModifyNodeObj(String name, ArrayList<NeighborAndCostStrings> neighbors, String msgID, String originalPacket){
		this.name = name;
		this.neighbors = neighbors;
		this.msgID = msgID;
		this.originalPacket = originalPacket;
	}

	public String getMsgID() {
		return msgID;
	}

	public String getName() {
		return name;
	}

	public NeighborAndCostStrings getNeighborAndCostString(int index){
		return neighbors.get(index);
	}

	public ArrayList<NeighborAndCostStrings> getNeighbors() {
		return neighbors;
	}

	public ArrayList<String> getNeighborsNames(){
		ArrayList<String> names = new ArrayList<String>();
		for(int i = 0; i < neighbors.size(); i++){
			names.add(neighbors.get(i).getNeighborName());
		}
		return names;	
	}

	public int getNeighborsListSize(){
		return neighbors.size();
	}

	public void removeNeighborAndCostString(int index){
		neighbors.remove(index);
	}

	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNeighbors(ArrayList<NeighborAndCostStrings> neighbors) {
		this.neighbors = neighbors;
	}

	public String getOriginalPacket(){
		return originalPacket;
	}
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}

}
