package overlay;

import java.io.IOException;
import java.io.ObjectInputStream;

import packetObjects.PacketObj;

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
		connectedTo = Client.getIP(peerAddress);
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
				System.out.println(System.currentTimeMillis()
						+ "Message received from: " + connectedTo);
				System.out.println("Message type: " + m.type);
				System.out.println("Request no: " + m.requestNo);
				attempt = 0;
				// handle updates if not previously seen
				handleUpdate(m);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				running = false;
			} catch (IOException e) {
				e.printStackTrace();
                running = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
                running = false;
			}
		}
		System.out.println("Link to " + connectedTo + " dropped...");
	}

	public void handleUpdate(Message m) throws IOException,
			ClassNotFoundException, InterruptedException {
		if (m.type == 7) {
			Message<String> m2 = m;
			PacketObj pObj = new PacketObj(m2.packet, "", true);
			Client.pq2.addToGeneralQueue(pObj);
		}
	}
}