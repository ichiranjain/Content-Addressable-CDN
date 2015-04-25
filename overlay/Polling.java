package overlay;

import java.util.HashMap;

public class Polling extends Thread {

	static HashMap<String, Long> pollLatency;
	static boolean polling;

	{
		polling = true;
		pollLatency = new HashMap<String, Long>();
	}

	public Polling() {

	}

	@Override
	public void run() {
		while (polling) {
			try {
				Thread.sleep(10000);
				if (Peer.neighbors.size() > 0) {
					// poll neighbors by sending own neighbors
					JoinPacket jp = new JoinPacket();
					Message<JoinPacket> m = new Message<JoinPacket>(100, jp);
					for (String neighbor : Peer.neighbors.keySet()) {
						pollLatency.put(Peer.getIP(neighbor),
								-1 * System.currentTimeMillis());
						Peer.sendMessage(neighbor, m);
					}					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
