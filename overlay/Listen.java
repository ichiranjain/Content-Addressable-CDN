package overlay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listen for new connection requests from new peers on Server Socket <br/>
 * On receipt of new connection request check if peer can be added and <br/>
 * act accordingly.
 * 
 * @author Gaurav Komera
 *
 */
public class Listen extends Thread {
	Peer p;
	ServerSocket serverSocket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Socket peerSocket;

	public Listen(Peer p, ServerSocket serverSocket) {
		this.p = p;
		this.serverSocket = serverSocket;

		peerSocket = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (true) {
			try {
				peerSocket = serverSocket.accept();
				System.out.println("Connection request received from "
						+ peerSocket.getRemoteSocketAddress() + " ...");
				if (nodeJoinPermitted(peerSocket)) {

					Message<JoinPacket> m = (Message<JoinPacket>) ois
							.readObject();

					p.addPeer(m.packet);
					new Receive(peerSocket.getRemoteSocketAddress() + "")
							.start();
					System.out.println("Client peer now connected... IP: "
							+ peerSocket.getRemoteSocketAddress());
					System.out.println("New map: " + Peer.neighbors);
				} else {
					System.out
							.println("Connection from "
									+ peerSocket.getRemoteSocketAddress()
							+ " was dropped bacause all neighbors "
							+ "positions are busy.");
					System.out
							.println("Potential peer has been informed about "
									+ "other nodes in the network.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void setUpObjectStreams() throws IOException {
		ois = new ObjectInputStream(new BufferedInputStream(
				peerSocket.getInputStream()));
		oos = new ObjectOutputStream(new BufferedOutputStream(
				peerSocket.getOutputStream()));
	}

	public boolean nodeJoinPermitted(Socket peerSocket) {
		int existingNetworkSize = Peer.allNodes.size();
		int newNetworkSize = Peer.allNodes.size() + 1;
		if (Math.ceil(Math.log(existingNetworkSize)) == Math.ceil(Math
				.log(newNetworkSize))) {
			return false;
		} else {
			if (Peer.vacancies.size() == 0) {
				//
			}
			return true;
		}
	}
}