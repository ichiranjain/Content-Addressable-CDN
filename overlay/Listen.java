package overlay;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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
	boolean running;

	/**
	 * Constructor for initializing peer.
	 * 
	 * @param p
	 * @param serverSocket
	 */
	public Listen(Peer p) {
		this.p = p;
		running = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (running) {
			try {
				// wait for new peer
				Peer.peerSocket = Peer.serverSocket.accept();
				System.out.println("Connection request from "
						+ Peer.peerSocket.getRemoteSocketAddress());
				setUpObjectStreams();
				System.out.println("Waiting for join packet");
				Message<JoinPacket> m = (Message<JoinPacket>) ois.readObject();
				JoinPacket replyPacket = new JoinPacket();
				System.out.println("Join packet type " + m.type);
				System.out.println("Sending acknowledgement");
				Message<JoinPacket> mReply = new Message<JoinPacket>(2,
						replyPacket);

				// code 11 for client join
				// code 400 for server join
				if (m.type == 11 || m.type == 400) {
					// start listening on new link with new joinee
					Link link = new Link(
							Peer.peerSocket.getRemoteSocketAddress() + "", ois,
							m.type == 11 ? 1 : 2);
					link.start();
					Peer.clientServers.put(
							Peer.getIP(Peer.peerSocket.getRemoteSocketAddress()
									.toString()) + "", new SocketContainer(
									Peer.peerSocket, ois, oos, link));
					Peer.routing
							.addClient(
									Peer.generateID(Peer.getIP(Peer.peerSocket
											.getRemoteSocketAddress()
											.toString()))
											+ "", 0);
				}
				// else cache server join
				else {
					// start listening on new link with new joinee
					Link link = new Link(
							Peer.peerSocket.getRemoteSocketAddress() + "", ois,
							3);
					link.start();

					Peer.addPeer(m.packet, Peer.peerSocket, oos, ois, link);

					if (Peer.nodeDropRequired()) {
						mReply.type = -2;
						System.out.println("Dropping neighbor...");
						String dropped = dropNeighbor(Peer
								.getIP(Peer.peerSocket.getRemoteSocketAddress()
.toString()),
								m.packet.neighbors);
						mReply.packet.dropped = dropped;
						oos.writeObject(mReply);
						oos.flush();
						System.out.println("Node dropped" + dropped);
					} else {
						// read initial message from neighbor
						System.out.println("No drops needed");
						oos.writeObject(mReply);
						oos.flush();
					}
					// adding new peer and updating meta-data
					System.out.println("Peer now coonnected");
					System.out.println("New neighbors " + Peer.neighbors);
					System.out.println("New allNodes " + Peer.allNodes);
					System.out.println("ID sent to routing:: "
							+ Peer.generateID(Peer.peerSocket
									.getRemoteSocketAddress().toString())
							+ " cost::" + 60000);
					Peer.routing.addLink(
							Peer.generateID(Peer.peerSocket
									.getRemoteSocketAddress().toString()) + "",
							60000);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				Peer.peerSocket = null;
				oos = null;
				ois = null;
			}
		}
	}

	public String dropNeighbor(String except, HashSet<String> exceptNeighbors)
			throws IOException {
		// randomly select neighbor to be dropped
		Random r = new Random();
		int d = r.nextInt(Peer.neighbors.size());
		String dropped = null;
		List<String> neighbors = new ArrayList<String>(Peer.neighbors.keySet());
		// Node to be dropped should not be the newly connected node or any of
		// its new neighbors
		while (neighbors.get(d).equals(except)
				|| exceptNeighbors.contains(neighbors.get(d))) {
			d = r.nextInt(Peer.neighbors.size());
		}
		dropped = neighbors.get(d);

		// notify neighbor of link drop using message of type 3
		SocketContainer neighbor = Peer.neighbors.get(dropped);
		JoinPacket dp = new JoinPacket();
		dp.dropped = dropped;
		Message<JoinPacket> m = new Message<JoinPacket>(3, dp);
		neighbor.oos.writeObject(m);
		neighbor.oos.flush();

		// Removing neighbor to be dropped from records
		neighbor.link.running = false;
		Peer.neighbors.remove(dropped);
		// Peer.allNodes.remove(dropped);

		// INFORM ROUTING ABOUT LINK DROP!
		Peer.routing.removeLink(Peer.generateID(dropped) + "", 0);

		return dropped;
	}

	/**
	 * Method that initializes the input and output object streams. <br/>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void setUpObjectStreams() throws IOException, InterruptedException {
		oos = new ObjectOutputStream(Peer.peerSocket.getOutputStream());
		// Thread.sleep(100);
		ois = new ObjectInputStream(Peer.peerSocket.getInputStream());
		System.out.println("OOS and OIS set up");
	}

}