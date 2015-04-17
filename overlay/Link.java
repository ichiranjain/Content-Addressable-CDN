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

	public Link(String peerAddress, ObjectInputStream ois) throws IOException {
		System.out.println("Initializing link to " + peerAddress + " - start");
		connectedTo = Peer.getIP(peerAddress);
		this.ois = ois;
		System.out.println("Initializing link to " + peerAddress + " - finish");
	}

	@Override
	public void run() {
		Message m = null;
		int attempt = 0;
		System.out.println("Input Stream started...");
		while (true) {
			try {
				System.out.print(".");
				this.sleep(1000);
				m = (Message) ois.readObject();
				System.out.println("Message received from: " + connectedTo);
				System.out.println("type: " + m.type);
				System.out.println("request no: " + m.requestNo);
				 attempt = 0;
				// handle updates
				handleUpdate(m);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				attempt++;
				e.printStackTrace();
				if (attempt == 3) {
					try {
						ois.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void handleUpdate(Message m) {
		if (m.type == 50) {
			System.out.println("Handling update of type 50 - start");
			JoinPacket jp = (JoinPacket) m.packet;
			Peer.vacancies.putAll(jp.vacancies);
			Peer.allNodes.addAll(jp.allNodes);
			System.out.println("Handled update from newly joined node...");
			System.out.println("Neighbors: " + Peer.neighbors);
			System.out.println("allNodes: " + Peer.allNodes);
			System.out.println("Handling update of type 50 - end");
		}
	}
}