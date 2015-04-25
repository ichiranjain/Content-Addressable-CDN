package overlay;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Receive message objects from neighbors and process them.
 * 
 * @author Gaurav Komera
 *
 */
public class Link extends Thread {
	ObjectInputStream ois = null;
	String connectedTo;
	boolean running;

	public Link(String peerAddress, ObjectInputStream ois) throws IOException {
		connectedTo = Peer.getIP(peerAddress);
		this.ois = ois;
		running = true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void run() {
		Message m = null;
		int attempt = 0;
		System.out.println("Started listening on link to " + connectedTo);
		while (running) {
			try {
				m = (Message) ois.readObject();
				System.out.println("Message received from: " + connectedTo);
				System.out.println("Message type: " + m.type);
				System.out.println("Request no: " + m.requestNo);
				 attempt = 0;
				// handle updates if not previously seen
				if (!Peer.requests.contains(m.requestNo)) {
					while (Peer.requests.size() >= 100) {
						Peer.requests.removeFirst();
					}
					Peer.requests.add(m.requestNo);
					handleUpdate(m);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				running = false;
			} catch (IOException e) {
				attempt++;
				e.printStackTrace();
				if (attempt == 3) {
					try {
						ois.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					running = false;
				}
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				running = false;
			}
			if (!running) {
				Peer.neighbors.remove(connectedTo);
				Peer.allNodes.remove(connectedTo);
				// inform neighbors about dropped node
			}
		}
		System.out.println("Link to " + connectedTo + " dropped...");
	}

	public void handleUpdate(Message m) throws IOException,
			ClassNotFoundException, InterruptedException {
		if (m.type == 50) {
			JoinPacket jp = (JoinPacket) m.packet;
			Peer.allNodes.addAll(jp.allNodes);
			// connect to neighbors in jp except for doNotConnect
			// if (!Peer.linksSatisfied()) {
			// for (String neighbor : jp.neighbors) {
			// if (!neighbor.equals(jp.doNotConnect)) {
			// Peer.join(neighbor, false);
			// break;
			// }
			// }
			// }
		} else if (m.type == 3) {
			running = false;
			Peer.neighbors.remove(connectedTo);
			System.out.println("Removed " + connectedTo + " as neighbor");
		}
		// poll packet
		else if (m.type == 100) {
			// process neighbors and vacancies
			JoinPacket pollPakcet = (JoinPacket) m.packet;
			Peer.allNodes.addAll(pollPakcet.neighbors);
			// if busy don't do anything

			// else send reply with neighbors
			JoinPacket pollReplyPakcet = new JoinPacket();
			Message<JoinPacket> pollReply = new Message<JoinPacket>(101,
					pollReplyPakcet);
			Peer.sendMessage(connectedTo, pollReply);
		}
		// poll reply
		else if (m.type == 101) {
			long startTime = Polling.pollLatency.get(connectedTo);
			long endTime = System.currentTimeMillis();
			Polling.pollLatency.remove(connectedTo);
			// LET ROUTING KNOW ABOUT SCORE

			// gather vacancies and send reply
			// process neighbors and vacancies
			JoinPacket pollReplyPacket = (JoinPacket) m.packet;
			Peer.allNodes.addAll(pollReplyPacket.neighbors);
		}
		// force remove node because it dropped
		else if (m.type == 200) {
			// forward same packet to neighbors
		}
		// routing and other packets
		else if (m.type == 0 /* or anything else */) {

		}
		// new node added notification
		else if (m.type == 102) {
			Peer.allNodes.add(m.packet.toString());
		}
	}
}