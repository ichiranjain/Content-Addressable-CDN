package packetObjects;
import java.util.ArrayList;

import topology.NeighborAndCostStrings;


/**
 * Add node class is used when adding a node to the graph. </br>
 * This class holds the necessary information for adding a node to the graph.
 * 
 * name: is the ID of the cache server being added to the graph</br>
 * neighbors: is a list of neighbors for the cache server being added </br>
 * and there costs</br>
 * msgID: is a unique message ID needed for every update 
 * @author spufflez
 *
 */
public class AddNodeObj {

	String name;
	ArrayList<NeighborAndCostStrings> neighbors;
	String msgID;
	String originalPacket;

	/**
	 * Add node constructor, that accepts a list of neighbors.
	 * 
	 * @param name
	 * @param neighbors
	 * @param msgID
	 */
	public AddNodeObj(String name, ArrayList<NeighborAndCostStrings> neighbors, String msgID){
		this.name = name;
		this.neighbors = neighbors;
		this.msgID = msgID;
	}

	/**
	 * Add node constructor to be used when a node has no neighbors. 
	 * 
	 * @param name
	 * @param msgID
	 */
	public AddNodeObj(String name, String msgID){
		this.name = name;
		this.msgID = msgID;
		neighbors = new ArrayList<NeighborAndCostStrings>();
	}

	/**
	 * Add node constructor when adding a node with only a name.
	 * @param name
	 */
	public AddNodeObj(String name){
		this.name = name;
		neighbors = new ArrayList<NeighborAndCostStrings>();
	}

	/**
	 * @param name
	 * @param neighbors
	 * @param msgID
	 * @param originalPacket
	 */
	public AddNodeObj(String name, ArrayList<NeighborAndCostStrings> neighbors, String msgID, String originalPacket){
		this.name = name;
		this.neighbors = neighbors;
		this.msgID = msgID;
		this.originalPacket = originalPacket;
	}

	/**
	 * get add node message ID
	 * @return
	 */
	public String getMsgID() {
		return msgID;
	}

	/**
	 * get add node name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * get the specified neighbor and its cost 
	 * @param index
	 * @return
	 */
	public NeighborAndCostStrings getNeighborAndCostString(int index){
		return neighbors.get(index);
	}

	/**
	 * get the array list of neighbors and there costs
	 * @return
	 */
	public ArrayList<NeighborAndCostStrings> getNeighbors() {
		return neighbors;
	}

	/**
	 * remove a specific neighbor at the provided index from the array list of neighbors
	 * @param index
	 */
	public void removeNeighborAndCostString(int index){
		neighbors.remove(index);
	}

	/**
	 * get an array list of all the neighbor names
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
	 * get the size of the neighbors list
	 * @return
	 */
	public int getNeighborsListSize(){
		return neighbors.size();
	}

	/**
	 * set the add node message ID
	 * @param msgID
	 */
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	/**
	 * set the add node name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * set the add node neighbors list
	 * @param neighbors
	 */
	public void setNeighbors(ArrayList<NeighborAndCostStrings> neighbors) {
		this.neighbors = neighbors;
	}

	/**
	 * get the original json string of the packet
	 * @return
	 */
	public String getOriginalPacket(){
		return originalPacket;
	}

	/**
	 * set the original packet to the provided json string, </br>
	 * this is used to store the json representation of the object.
	 * @param originalPacket
	 */
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}


}
