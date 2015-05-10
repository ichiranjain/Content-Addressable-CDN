package overlay;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Packet class for join and join acknowledgement messages
 * 
 * @author Gaurav
 *
 */
@SuppressWarnings("serial")
public class JoinPacket implements Serializable {
	// map of neighboring sockets
	HashSet<String> neighbors;

	// map of vacancies with current and neighboring nodes
	HashMap<String, Integer> vacancies;

	// set of all nodes in the connected network
	HashSet<String> allNodes;

	// dropped node
	String dropped;

	// Peer that was recently connected and should not be sent a request again
	List<String> doNotConnect;

	/**
	 * Initialize the instance variables with the current view of the Peer
	 * contents
	 */
	public JoinPacket() {
		neighbors = new HashSet<String>(Peer.neighbors.keySet());
		allNodes = new HashSet<String>(Peer.allNodes);
	}

	/**
	 * Special constructor to set values for doNotConnect list
	 * 
	 * @param doNotConnect
	 */
	public JoinPacket(List<String> doNotConnect) {
		this();
		this.doNotConnect = doNotConnect;
	}
}
