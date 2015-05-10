package overlay;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Read from command line and send to the connected peer using socket.
 * 
 * @author Gaurav
 *
 */
public class Send extends Thread {

	ObjectOutputStream oos;
	String peerAddress;

	public Send(String peerAddress) throws IOException {
		this.peerAddress = peerAddress;
		oos = Peer.neighbors.get(peerAddress).oos;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String line = Peer.scanner.nextLine();
				String[] parts = line.split("=");
				if (Peer.neighbors.containsKey(parts[0])) {
					System.out.println("Sending msg to " + parts[0] + "...");
					oos = Peer.neighbors.get(parts[0]).oos;
				}
			} catch (Exception e) {

			}
		}
	}
}