package overlay;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * This is a live link between two nodes. For multiple connections there would
 * be multiple Link threads running. The thread actively listens for incoming
 * objects from the other end of the link.
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
				// System.out.print(System.currentTimeMillis()
				// + "Message received from: " + connectedTo);
				// System.out.println("\tMessage type: " + m.type);
				attempt = 0;
				// handle updates if not previously seen
				if (!Peer.requests.contains(m.requestNo)) {
					while (Peer.requests.size() >= 100) {
						Peer.requests.removeFirst();
					}
					Peer.requests.add(m.requestNo);
					handleUpdate(m);
				} else {
					System.out.println("Packet discarded(repeated request no)");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				// running = false;
			} catch (IOException e) {
				attempt++;
				e.printStackTrace();
				if (attempt == 3) {
					try {
						ois.close();
						// broadcast force remove
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					running = false;
				}
			}
			catch (InterruptedException e) {
				// e.printStackTrace();
				if (type == 1 || type == 2)
					running = false;
			} finally {
				if (!running) {
					if (type == 1 || type == 2) {
						try {
							Peer.clientServers.get(Peer.idIPMap.get(ID)).socket
									.close();
						} catch (IOException e) {
							System.out.println("Error when closing "
									+ "client or server socket");
							e.printStackTrace();
						}
						Peer.clientServers.remove(ID);
						Peer.routing.removeClient(ID, -1);
					} else {
						try {
							Peer.neighbors.remove(connectedTo).socket.close();
						} catch (IOException e) {
							System.out.println("Error when closing "
									+ "cache server socket");
							e.printStackTrace();
						}
						Peer.neighbors.remove(connectedTo);
						// Peer.allNodes.remove(connectedTo);
						// broadcast to remove neighbor
						Message<String> forceRemove = new Message<String>(999,
								ID);
						// try {
						// // Peer.sendMessageToAllButX("", forceRemove);
						// } catch (IOException e) {
						// e.printStackTrace();
						// }
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
			Peer.allNodes.addAll(pollPakcet.allNodes);
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
					.writeObject(new Message<String>(403));
		}
		// new node added notification
		else if (m.type == 102) {
			Peer.allNodes.add(m.packet.toString());
		} else if (m.type == 7) {
			try {
				Message<String> m2 = m;
				Peer.routing.addPacket(m2.packet, ID, false);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Instance of message with type 7: "
						+ m.packet.getClass());
				System.out.println("inside message of type 7: "
						+ ((Message) m.packet).packet);
			}
		}
		// force remove dropped neighbor
		else if (m.type == 999) {
			Peer.allNodes.remove((String) m.packet);
			Peer.sendMessageX(connectedTo, m);
		}
	}
}