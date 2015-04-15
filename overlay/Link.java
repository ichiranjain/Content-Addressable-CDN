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

	public Link(String peerAddress) throws IOException {
		System.out.println("Initializing link to " + peerAddress + " - start");
		connectedTo = peerAddress;
		ois = Peer.neighbors.get(peerAddress).ois;
		System.out.println("Initializing link to " + peerAddress + " - finish");
	}

	@Override
	public void run() {
		Message m = null;
		int attempt = 0;
		System.out.println("Input Stream started...");
		while (true) {
			try {
				m = (Message) ois.readObject();
				System.out.println("Message received from: " + connectedTo);
				System.out.println("type: " + m.type);
				System.out.println("request no: " + m.requestNo);
				attempt = 0;
				// handle updates
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
		}
	}
}