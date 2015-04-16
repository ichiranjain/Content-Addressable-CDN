package overlay;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listen for connection requests from new peers on Server Socket <br/>
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

	// Socket peerSocket;

	/**
	 * Constructor for initializing peer.
	 * 
	 * @param p
	 * @param serverSocket
	 */
	public Listen(Peer p) {
		this.p = p;
		// this.serverSocket = serverSocket;
		//
		// p.peerSocket = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (true) {
			try {
				p.peerSocket = Peer.serverSocket.accept();
				System.out.println("Connection request received from "
						+ p.peerSocket.getRemoteSocketAddress() + " ...");
				setUpObjectStreams();
				if (nodeJoinPermitted(p.peerSocket)) {
					// read initial message from neighbor
					System.out
							.println("waiting for message from peer that requested connection..");
					Message<JoinPacket> m = (Message<JoinPacket>) ois
							.readObject();
					System.out.println("message received.. type: " + m.type);
					p.addPeer(m.packet, p.peerSocket, oos, ois);
					// p.addPeer(null, p.peerSocket, oos, ois);
					// start listening
					new Link(p.peerSocket.getRemoteSocketAddress() + "", ois)
							.start();
					System.out.println("Client peer now connected... IP: "
							+ p.peerSocket.getRemoteSocketAddress());
					System.out.println("New map: " + Peer.neighbors);
				} else {
					System.out.println("Connection from "
							+ p.peerSocket.getRemoteSocketAddress()
							+ " was dropped bacause all neighbors "
							+ "positions are busy.");
					System.out
							.println("Potential peer has been informed about "
									+ "other nodes in the network.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				p.peerSocket = null;
				oos = null;
				ois = null;
			}
		}
	}

	/**
	 * Method that initializes the input and output object streams. <br/>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void setUpObjectStreams() throws IOException, InterruptedException {
		System.out
				.println("** setting up object streams for listening - start**");
		// oos = new ObjectOutputStream(new BufferedOutputStream(
		// peerSocket.getOutputStream()));
		oos = new ObjectOutputStream(p.peerSocket.getOutputStream());
		this.sleep(1000);
		ois = new ObjectInputStream(p.peerSocket.getInputStream());

		System.out
				.println("object input and output stream set up successfully..	");
		// oos.flush();
		// ois = new ObjectInputStream(new BufferedInputStream(
		// peerSocket.getInputStream()));

		System.out.println("received object input stream..");
		System.out
				.println("** setting up object streams for listening - finish");
	}

	/**
	 * Checks if new join request can be processed by current node.<br/>
	 * Does this by checking if number of links after the node joins in are<br/>
	 * within limits of log<i>n</i>.
	 * 
	 * @param peerSocket
	 * @return
	 */
	public boolean nodeJoinPermitted(Socket peerSocket) {
		return true;
		// System.out.println("** node join permissing check - start **");
		// int existingNetworkSize = Peer.allNodes.size();
		// int newNetworkSize = Peer.allNodes.size() + 1;
		// if (Math.ceil(Math.log(existingNetworkSize)) == Math.ceil(Math
		// .log(newNetworkSize))) {
		// System.out.println("permission denied..");
		// System.out.println("** node join permission check - finish");
		// return false;
		// } else {
		// if (Peer.vacancies.size() == 0) {
		// //
		// }
		// System.out.println("permission granted..");
		// System.out.println("** node join permission check - finish");
		// return true;
		// }
	}
}