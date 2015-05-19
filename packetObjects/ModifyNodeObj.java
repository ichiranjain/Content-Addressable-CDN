package packetObjects;
import java.util.ArrayList;

import topology.NeighborAndCostStrings;


/**
 * This class is used for sending updates to cache servers. </br>
 * name: is the name of the cache server that is issuing the update </br>
 * 
 * name: is the ID of the node this update should be applied to </br>
 * neighbors: is a list of all the neighbors "above named node" has </br>
 * msgID: is a unique ID used needed for all updates</br>
 * original Packet: is this class represented as a json string
 * @author spufflez
 *
 */
public class ModifyNodeObj {
	String name;
	ArrayList<NeighborAndCostStrings> neighbors;
	String msgID;
	String originalPacket;

	/**
	 * Constructor that does not require an original packet
	 * @param name
	 * @param neighbors
	 * @param msgID
	 */
	public ModifyNodeObj(String name, ArrayList<NeighborAndCostStrings> neighbors, String msgID){
		this.name = name;
		this.neighbors = neighbors;
		this.msgID = msgID;
	}

	/**
	 * Constructor 
	 * @param name
	 * @param neighbors
	 * @param msgID
	 * @param originalPacket
	 */
	public ModifyNodeObj(String name, ArrayList<NeighborAndCostStrings> neighbors, String msgID, String originalPacket){
		this.name = name;
		this.neighbors = neighbors;
		this.msgID = msgID;
		this.originalPacket = originalPacket;
	}

	/**
	 * Get the unique message ID 
	 * @return
	 */
	public String getMsgID() {
		return msgID;
	}

	/**
	 * Get the ID of the cache server that this update pertains to 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get a neighbor and its cost at the provided index
	 * @param index
	 * @return
	 */
	public NeighborAndCostStrings getNeighborAndCostString(int index){
		return neighbors.get(index);
	}

	/**
	 * Get the array of neighbors and there costs
	 * @return
	 */
	public ArrayList<NeighborAndCostStrings> getNeighbors() {
		return neighbors;
	}

	/**
	 * Get an array of the only the neighbors names
	 * @return
	 */
	public ArrayList<String> getNeighborsNames(){
		ArrayList<String> names = new ArrayList<String>();
		for(int i = 0; i < neighbors.size(); i++){
			names.add(neighbors.get(i).getNeighborName());
		}
		return names;	
	}

	/**
	 * Get the size of the neighbors list
	 * @return
	 */
	public int getNeighborsListSize(){
		return neighbors.size();
	}

	/**
	 * Remove a neighbor fromt he list at the provided index
	 * @param index
	 */
	public void removeNeighborAndCostString(int index){
		neighbors.remove(index);
	}

	/**
	 * Set the unique message ID
	 * @param msgID
	 */
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	/**
	 * Set the ID of the cache server that this update pertains to 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the list of neighbors 
	 * @param neighbors
	 */
	public void setNeighbors(ArrayList<NeighborAndCostStrings> neighbors) {
		this.neighbors = neighbors;
	}

	/**
	 * Get the json representation of the object 
	 * @return
	 */
	public String getOriginalPacket(){
		return originalPacket;
	}

	/**
	 * Set the json representation of the object 
	 * @param originalPacket
	 */
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}

}
