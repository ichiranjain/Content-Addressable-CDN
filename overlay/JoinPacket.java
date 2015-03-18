package overlay;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class JoinPacket implements Serializable {
	// map of neighboring sockets
	HashSet<String> neighbors;

	// map of vacancies with current and neighboring nodes
	HashMap<String, Integer> vacancies;

	// set of all nodes in the connected network
	HashSet<String> allNodes;

	public JoinPacket(Peer p) {
		neighbors = new HashSet<String>();
		for (Entry<String, SocketContainer> entry : p.neighbors.entrySet()) {
			neighbors.add(entry.getKey());
		}
		vacancies = p.vacancies;
		allNodes = p.allNodes;
	}
}
