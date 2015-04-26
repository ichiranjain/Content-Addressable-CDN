package overlay;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

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

	public JoinPacket() {
		neighbors = new HashSet<String>();
		for (Entry<String, SocketContainer> entry : Peer.neighbors.entrySet()) {
			neighbors.add(entry.getKey());
		}
		vacancies = Peer.vacancies;
		allNodes = Peer.allNodes;
	}

	public JoinPacket(List<String> doNotConnect) {
		this();
		this.doNotConnect = doNotConnect;
	}
}
