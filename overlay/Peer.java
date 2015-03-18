package overlay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import overlayInterface.PeerInterface;

/**
 * Peer class to represent a node in the overlay network. Implements the
 * PeerInterface interface.
 * 
 * @author Gaurav Komera
 *
 */
public class Peer implements PeerInterface {
	Socket peerSocket;

	// ID of this node
	long ID;
	// serverSocket to listen on
	static ServerSocket serverSocket;
	// socket to communicate with neighboring nodes
	// map of neighboring sockets
	static HashMap<String, SocketContainer> neighbors;
	// map of vacancies with current and neighboring nodes
	static HashMap<String, Integer> vacancies;
	// set of all nodes in the connected network
	static HashSet<String> allNodes;
	static Scanner s;
	static int logN;

	// static block for initializing static content
	{
		while (true) {
			try {
				serverSocket = new ServerSocket(43125);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			ID = generateID(); // unique ID based on IP address
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		neighbors = new HashMap<String, SocketContainer>();
		vacancies = new HashMap<String, Integer>();
		allNodes = new HashSet<String>();
		s = null;
		logN = 0;
	}

	// Default constructor
	public Peer() {
		peerSocket = null;
	}

	// Main thread
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		if (args.length < 2) {
			if (args.length == 1 && args[0].equals("man")) {
				System.out.println("To start a new network");
				System.out.println("\n\tjava Peer start new\n");
				System.out.println("To join an existing network");
				System.out.println("\n\tjava Peer join <node address>");
				System.out.println("\t <node address> is the IP address of a "
						+ "node that is already part of the existing "
						+ "network.\n");
			} else {
				System.out.println("Please pass command line arguments "
						+ "suggesting the mode in which the node is to "
						+ "be started.");
				System.out.println("Suggestion:\n\n\t type 'java Peer man' on "
						+ "the command line");
			}
			return;
		}
		Peer p = new Peer();

		// static scanner object for the class
		s = new Scanner(System.in);
		if (args[0].toLowerCase().equals("start")) {
			p.start();

			// Start listening on server socket for new connections
			p.listen();

		} else if (args[0].toLowerCase().equals("join")) {
			String server = args[1].toLowerCase();

			p.join(server);

			// Start listening on server socket
			p.listen();
			
		}

		// share general messages with neighbors
		while (true) {
			try {
				// take input from command line
				String line = s.nextLine();
				Message<String> m = new Message<String>(99, line);

				for (Entry<String, SocketContainer> entry : neighbors
						.entrySet()) {
					if (!entry.getKey().equals(
							p.peerSocket.getRemoteSocketAddress())) {
						System.out.print("Sending new neighbor info to: ");
						System.out.println(entry.getValue().socket
								.getRemoteSocketAddress());
						entry.getValue().oos.writeObject(m);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("");
			}
		}
		// p.peerSocket = null;
	}

	/**
	 * This method updates meta data after adding new peer connection.
	 */
	@Override
	public void addPeer(JoinPacket packet) throws IOException {

		InputStream is = new BufferedInputStream(peerSocket.getInputStream());
		ObjectInputStream ois = new ObjectInputStream(is);
		OutputStream os = new BufferedOutputStream(peerSocket.getOutputStream());
		ObjectOutputStream oos = new ObjectOutputStream(os);

		// adding peer to
		neighbors.put(peerSocket.getRemoteSocketAddress() + "",
				new SocketContainer(peerSocket, ois, oos));
		// adding neighbor to set of all nodes in the network
		allNodes.add(peerSocket.getRemoteSocketAddress().toString());
		// update remaining neighbors with information about new neighbor
		updateNeighbors(peerSocket, packet);
		// update expected number of connections
		updateLogN();
	}

	// send remaining neighbors information about new peer
	@Override
	public void updateNeighbors(Socket newPeer, JoinPacket packet) {
		// **packet is null when the node starts**
		// send neighbors with new peer info
		

		// send > new peer info about existing neighbors

	}

	@Override
	public void listen() {
		Listen listen = new Listen(this, serverSocket);
		listen.start();
	}

	@Override
	public void remove(Peer p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() throws IOException {
		System.out.println("Starting node...");
		System.out
				.println("IP: " + InetAddress.getLocalHost().getHostAddress());
		// creating peer
		System.out.println("Waiting for client peer...");
		// peerSocket = serverSocket.accept();
		// this.addPeer(null);
		//
		// System.out.println("Client peer now connected... IP: "
		// + peerSocket.getRemoteSocketAddress());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean join(String peer) throws IOException, ClassNotFoundException {
		peerSocket = new Socket(peer, 43125);
		Message<JoinPacket> joinMessage = new Message<JoinPacket>(1);
		
		InputStream is = new BufferedInputStream(peerSocket.getInputStream());
		ObjectInputStream ois = new ObjectInputStream(is);
		OutputStream os = new BufferedOutputStream(peerSocket.getOutputStream());
		ObjectOutputStream oos = new ObjectOutputStream(os);
		
		oos.writeObject(joinMessage);

		Message<JoinPacket> m = (Message) ois.readObject();

		// update node's metadata using information from new node
		updateMetaData(m);

		// RECONNECT IF CONNECTION FAILS
		// while (m.type == -1) {
		// HashMap<String, Integer> vacancies = (HashMap<String, Integer>)
		// m.packet.vacancies;
		// if (vacancies.size() == 0) {
		//
		// } else {
		//
		// }
		// }
		// check if connection was accepted

		this.addPeer(m.packet);
		// start listening to connected peer for messages
		new Link(peerSocket.getRemoteSocketAddress() + "").start();

		this.updateNeighbors(peerSocket, m.packet);

		return true;
	}

	/**
	 * Update meta-data of node using received join packet.
	 * 
	 * @param m
	 */
	public void updateMetaData(Message<JoinPacket> m) {
		JoinPacket packet = (JoinPacket) m.packet;
		Peer.allNodes.addAll(packet.allNodes);
		Peer.vacancies.putAll(packet.vacancies);
	}
	
	/**
	 * Method to be called by upper layers to send a message to a particular<br/>
	 * neighbor.<br/>
	 * 
	 * Message type should be set to 7.
	 * 
	 * @param ID
	 * @param m
	 * @return
	 */
	public boolean sendMessage(long ID, Message m) {
		try {
			SocketContainer sc = neighbors.get(ID);
			sc.oos.writeObject(m);			
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Method to be called by upper layers to send a message to a list of<br/>
	 * neighbors.<br/>
	 * 
	 * Message type should be set to 7.
	 * 
	 * @param IDs
	 * @param m
	 * @return
	 * @throws IOException
	 */
	public boolean sendMessage(List<Long> IDs, Message m) throws IOException {
		for (Long id : IDs) {
			if (!sendMessage(id, m)) {
				return false;
			}
		}
		return true;
	}

	// update the number of required neighbors
	public void updateLogN() {
		logN = (int) Math.ceil(Math.log10(allNodes.size()) / Math.log10(2));
	}

	/**
	 * Node ID generator
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public static long generateID() throws UnknownHostException {
		String hostAddress = InetAddress.getLocalHost().getHostAddress();
		System.out.println("Generating ID for node... (" + hostAddress + ")");
		long prime1 = 105137;
		long prime2 = 179422891;
		long ID = 0;
		for (int i = 0; i < hostAddress.length(); i++) {
			char c = hostAddress.charAt(i);
			if (c != '.') {
				ID += (prime1 * hostAddress.charAt(i)) % prime2;
			}
		}
		System.out.println("ID: " + ID);
		return ID;
	}
}