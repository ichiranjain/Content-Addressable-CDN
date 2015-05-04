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
	int previousCost;
	boolean running;
	int type; // 1 - client, 2 - server, 3 - cache server
	String ID;

	public Link(String peerAddress, ObjectInputStream ois, int type)
			throws IOException {
		previousCost = -1;
		connectedTo = Peer.getIP(peerAddress);
		this.ois = ois;
		running = true;
		this.type = type;
		ID = Peer.generateID(Peer.getIP(connectedTo)) + "";
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
				System.out.println(System.currentTimeMillis()
						+ "Message received from: " + connectedTo);
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
				} else {
					System.out.println("KYABAATKYABAATKYABAAT!");
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
			} finally {
				if (!running) {
					if (type == 1 || type == 2) {
						Peer.clientServers.remove(ID);
						Peer.routing.removeClient(ID, -1);
					} else {
						Peer.neighbors.remove(connectedTo);
						Peer.allNodes.remove(connectedTo);
						// inform neighbors about dropped node
						Peer.routing.removeLink(ID, -1);
					}
				}
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
			Peer.routing.removeLink(Peer.generateID(connectedTo) + "", 0);
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
			Peer.sendMessageX(connectedTo, pollReply);
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
			
			if(previousCost < 0) {
				Peer.routing.modifyLink(Peer.generateID(connectedTo) + "",
						(int) (endTime - startTime));				
				previousCost = (int) (endTime - startTime);				
			}
			double change =(double) previousCost/(endTime - startTime);
			change *= 100;
			if (Math.abs(change - 100) > 30) {
				Peer.routing.modifyLink(Peer.generateID(connectedTo) + "",
						(int) (endTime - startTime));				
				previousCost = (int) (endTime - startTime);
			}
		}
		// force remove node because it dropped
		else if (m.type == 200) {

		}
		// routing and other packets
		else if (m.type == 0 /* or anything else */) {

		} // routing and other packets
		else if (m.type == 402 /* or anything else */) {
			Peer.clientServers.get(connectedTo).oos
					.writeObject(new Message(403));
		}
		// new node added notification
		else if (m.type == 102) {
			Peer.allNodes.add(m.packet.toString());
		} else if (m.type == 7) {
			Message<String> m2 = m;
			Peer.routing.addPacket(m2.packet, ID, false);
		}
	}
}