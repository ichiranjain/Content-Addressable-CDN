package overlay;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import topology.MainEntryPoint;
import topology.PassToRoutingLayer;

/**
 * Peer class to represent a node in the overlay network. Implements the
 * PeerInterface interface.
 * 
 * @author Gaurav Komera
 *
 */
public class Peer { // implements PeerInterface
	// socket to communicate with neighboring nodes
	static Socket peerSocket;

	// ID of this node
	static String ID;
	// serverSocket to listen on
	static ServerSocket serverSocket;
	// map of neighboring cacheServers
	static HashMap<String, SocketContainer> neighbors;
	// map of neighboring clients and servers
	static HashMap<String, SocketContainer> clientServers;
	// map of vacancies with current and neighboring nodes
	static HashMap<String, Integer> vacancies;
	// set of all nodes in the connected network
	static HashSet<String> allNodes;
	static Scanner scanner;
	static int logN;

	static HashMap<String, String> idIPMap;

	static LinkedList<Long> requests;

	static PassToRoutingLayer routing;

	// static block for initializing static content
	// like serverSocket used for listening
	{
		while (true) {
			try {
				serverSocket = new ServerSocket(43125);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Server socket started at port 43125...");

		neighbors = new HashMap<String, SocketContainer>();
		vacancies = new HashMap<String, Integer>();
		allNodes = new HashSet<String>();
		scanner = null;
		logN = 0;
		requests = new LinkedList<Long>();
		idIPMap = new HashMap<String, String>();
		clientServers = new HashMap<String, SocketContainer>();
	}

	/**
	 * Constructor generates ID and initializes peerSocket to null
	 * 
	 * peerSocket - used to accept connections from neighbors
	 */
	public Peer() {
		try {
			ID = generateID("") + ""; // unique ID based on IP address
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		peerSocket = null;
	}

	// Main thread
	public static void main(String[] args) throws IOException,
	ClassNotFoundException, InterruptedException {
		MainEntryPoint mep = null;
		Peer p = new Peer();
		if (args.length == 1) {
			if (args[0].equals("man")) {
				System.out.println("To start a new network");
				System.out.println("\n\tjava Peer start new\n");
				System.out.println("To join an existing network");
				System.out.println("\n\tjava Peer join <node address>");
				System.out.println("\t <node address> is the IP address of a "
						+ "node that is already part of the existing "
						+ "network.\n");
				return;
			} else if (args[0].toLowerCase().equals("start")) {
				p.start();

				// Start listening on server socket for new connections
				p.listen();
				mep = startRouting();
				Thread mepThread = new Thread(mep);
				mepThread.start();

			} else {
				System.out.println("Please pass command line arguments "
						+ "suggesting the mode in which the node is to "
						+ "be started.");
				System.out.println("Suggestion:\n\n\t type 'java Peer man' on "
						+ "the command line");
				return;
			}
		} else if (args.length == 2) {
			if (args[0].toLowerCase().equals("join")) {
				String server = args[1].toLowerCase();

				// Start listening on server socket
				p.listen();
				mep = startRouting();
				Thread mepThread = new Thread(mep);
				mepThread.start();

				// join node
				Message<JoinPacket> m = Peer.join(server, true);

				List<String> neighborsOfPeer = new ArrayList<String>(
						m.packet.neighbors);
				System.out.println("neighborsOfPeer: " + neighborsOfPeer);

				// connect to node that was dropped by peer
				if (!linksSatisfied() && m.type == -2) {
					Peer.join(m.packet.dropped, false);
				}
				// connecting to more neighbors to satisfy log n condition for
				// this node
				int i = 0;
				while (!linksSatisfied() && i < neighborsOfPeer.size()) {
					// do not send request to already connected neighbor
					while (Peer.neighbors.containsKey(neighborsOfPeer.get(i))) {
						i++;
					}
					System.out
					.println("Joining peer " + neighborsOfPeer.get(0));
					m = Peer.join(neighborsOfPeer.get(i), false);
					neighborsOfPeer.clear();
					neighborsOfPeer.addAll(m.packet.neighbors);
					i = 0;
				}
			}
		}

		// start polling neighbors
		Polling poll = new Polling();
		poll.start();

		/*************************************
		 * Start ROUTING
		 *************************************/

		routing = new PassToRoutingLayer(mep.packetQueue2);
		scanner = new Scanner(System.in);

		boolean alive = true;
		String action = "";
		while (alive) {

			// add functions here to get node repo
			// Fib data
			// pit data
			// directly connected clients data
			// directly connected routers data
			// update msg data
			action = scanner.next();

			switch (action) {
			case "node":
				System.out.println("-printing nodes-");
				mep.printNodeRepo();
				break;

			case "nd":
				// scanner
				action = scanner.next();
				mep.printNodeDetails(action);
				break;

			case "fib":
				mep.printFIB();
				break;

			case "pit":
				mep.printPIT();
				break;

			case "drr":
				mep.printDirectlyConnectedRouters();
				break;

			case "drc":
				mep.printDirectlyConnectedClietns();
				break;

			case "msgIDs":
				mep.printMsgIDsSeen();
				break;

			case "si":
				System.out.println("Enter content name:");
				action = scanner.next();
				mep.intrestPacket(action);
				break;

			case "sd":
				System.out.println("Enter content name:");
				String contentName = scanner.next();
				System.out.println("Enter origin Router name:");
				String originRouter = scanner.next();
				System.out.println("Enter from node:");
				String fromNode = scanner.next();
				mep.dataPacket(contentName, originRouter, fromNode);
				break;

			case "sp":
				System.out.println("enter a prefix");
				action = scanner.next();
				System.out.println("add or remove boolean");
				boolean addRemovePrefix = scanner.hasNextBoolean();
				mep.prefix(action, addRemovePrefix);
				break;

			case "spl":
				System.out.println("add or remove boolean");
				boolean addRemovePrefixList = scanner.hasNextBoolean();
				mep.prefixList(addRemovePrefixList);
				break;

			case "kill":
				mep.killThreads();
				alive = false;
				System.out.println("killing program");
				break;
			case "overlay":
				System.out.println("neighbors: "+neighbors);
				System.out.println("clients+servers: " + clientServers);
				break;
			default:
				System.out.println("default hit");
				break;
			}

		}
		System.out.println("program terminating");

		// share general messages with neighbors
		// while (true) {
		// try {
		//
		// Thread.sleep(1500);
		// System.out.println("Neighbors: " + neighbors);
		// System.out.println("allNodes: " + allNodes);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// System.out.println("");
		// }
		// }
	}

	public static MainEntryPoint startRouting() {
		MainEntryPoint mep = new MainEntryPoint(ID + "", 300000, 86400000,
				86400000, 86400000, 86400000);
		routing = new PassToRoutingLayer(mep.packetQueue2);

		return mep;

	}

	/**
	 * This method updates meta data after adding new peer connection.
	 */
	public static void addPeer(JoinPacket packet, Socket peerSocket,
			ObjectOutputStream oos, ObjectInputStream ois, Link link)
					throws IOException {
		String peer = getIP(peerSocket.getRemoteSocketAddress().toString());
		neighbors.put(peer,
				new SocketContainer(peerSocket, ois, oos, link));
		allNodes.add(peer);
		allNodes.addAll(packet.allNodes);
		// notify neighbors about new node
	}

	public static String getIP(String port) {
		int i = 0;
		int slash = 0;
		int end = port.length();
		for (; i < port.length(); i++) {
			if (port.charAt(i) == '/') {
				slash = i + 1;
			} else if (port.charAt(i) == ':') {
				end = i;
				break;
			}
		}
		return port.substring(slash, end);
	}

	// send remaining neighbors information about new peer
	public static void updateNeighbors(List<String> except, JoinPacket packet,
			int type)
					throws IOException {
		// send neighbors with new peer info
		int i = 0;
		System.out.println("Total neighbors: " + neighbors.size());
		packet.doNotConnect = except;
		Message<JoinPacket> m = new Message<JoinPacket>(type, packet);
		for (Entry<String, SocketContainer> e : neighbors.entrySet()) {
			if (!except.contains(e.getKey())) {
				++i;
				System.out
				.println("Sending new neighbor update: " + e.getKey());
				e.getValue().oos.writeObject(m);
			}
		}
		System.out.println("Total neighbors notified: " + i);
	}

	/**
	 * Method that starts a new thread to begin listening for new nodes that
	 * wish to join in.
	 */
	public void listen() {
		Listen listen = new Listen(this);
		listen.start();
	}

	public void start() throws IOException {
		System.out.println("Starting node...");
		System.out
		.println("IP: " + InetAddress.getLocalHost().getHostAddress());
		// creating peer
		System.out.println("Waiting for client peer...");
		allNodes.add(getIP(InetAddress.getLocalHost().getHostAddress()));
		// peerSocket = serverSocket.accept();
		// this.addPeer(null);
		//
		// System.out.println("Client peer now connected... IP: "
		// + peerSocket.getRemoteSocketAddress());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Message<JoinPacket> join(String peer, boolean recurse)
			throws IOException,
			ClassNotFoundException, InterruptedException {
		// ArrayList<String> connectedTo = new ArrayList<String>();
		long joinStartTime = System.currentTimeMillis();
		peerSocket = new Socket(peer, 43125);

		Message<String> newNodeMsg = new Message<String>(102, peer);
		sendMessageToAllButX("", newNodeMsg);

		// connectedTo.add(getIP(peer));
		//
		JoinPacket packet = new JoinPacket();
		Message<JoinPacket> joinMessage = new Message<JoinPacket>(1, packet);

		ObjectOutputStream oos = new ObjectOutputStream(
				peerSocket.getOutputStream());
		// Thread.sleep(100);
		ObjectInputStream ois = new ObjectInputStream(
				peerSocket.getInputStream());

		oos.writeObject(joinMessage);
		oos.flush();
		System.out.println("Join message sent");
		System.out.println("Waiting for acknowledgement");
		Message<JoinPacket> m = (Message) ois.readObject();
		long joinStartFinish = System.currentTimeMillis();

		System.out.println("Acknowledgement type: " + m.type);

		// start listening to connected peer for any future communication
		Link link = new Link(peerSocket.getRemoteSocketAddress() + "", ois, 3);
		link.start();
		//
		addPeer(m.packet, peerSocket, oos, ois, link);
		// System.out.println("all links up.. now contacting neighbors");
		// updateNeighbors(connectedTo, m.packet, 50);

		// INFORM ROUTING LAYER ABOUT NEW NEIGHBOR
		System.out.println(System.currentTimeMillis() + " sent to routing:: "
				+ generateID(peerSocket.getRemoteSocketAddress().toString())
				+ " cost::" + (joinStartFinish - joinStartTime));
		routing.addLink(generateID(peerSocket.getRemoteSocketAddress()
				.toString()) + "",(int) (joinStartFinish - joinStartTime));

		return m;
	}

	/**
	 * Update meta-data of node using received join packet.
	 * 
	 * @param m
	 */
	public static void updateMetaData(Message<JoinPacket> m) {
		System.out.println("** Updating meta data - start**");
		JoinPacket packet = m.packet;
		System.out.println("Packet allNodes: " + packet.allNodes);
		System.out.println("Packet neighbors: " + packet.neighbors);
		// allNodes.addAll(packet.allNodes);
		// vacancies.putAll(packet.vacancies);
		System.out.println("After update");
		System.out.println("allNodes: " + allNodes);
		System.out.println("neighbors: " + neighbors);
		System.out.println("** Updating meta data - finish**");
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
	@SuppressWarnings("rawtypes")
	public static boolean sendMessage(String ID, Message m) {
		System.out.println("(((enter sendMessage)))");
		System.out.println("neighbors: " + neighbors);
		System.out.println("clientServer: " + clientServers);
		System.out.println("idIPMap: " + idIPMap);
		try {
			System.out.println(":::ID::: " + ID);
			SocketContainer sc = neighbors.get(idIPMap.get(ID));
			if (sc == null) {
				System.out.println("inside if");
				sc = clientServers.get(idIPMap.get(ID));
			}
			System.out.println("sc: " + sc);
			sc.oos.writeObject(m);
		} catch (IOException e) {
			System.out.println("inside catch");
			System.out.println("(((exit sendMessage)))");
			return false;
		}
		System.out.println("(((enter sendMessage)))");
		return true;
	}

	@SuppressWarnings("rawtypes")
	public static boolean sendMessageX(String IP, Message m) {
		try {
			System.out.println("SendMessageX::" + IP + " looking in "
					+ neighbors.keySet());
			SocketContainer sc = neighbors.get(IP);
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
	@SuppressWarnings("rawtypes")
	public static boolean sendMessage(List<String> IDs, Message m)
			throws IOException {
		for (String id : IDs) {
			if (!sendMessage(id, m)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Method to be called by upper layers to send a message to all<br/>
	 * neighbors except ID.<br/>
	 * 
	 * Message type should be set to 7.
	 * 
	 * @param ID
	 * @param m
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static boolean sendMessageToAllBut(String ID, Message m)
			throws IOException {
		List<String> IPs = new ArrayList<String>(neighbors.keySet());
		for (String ip : IPs) {
			if (!("" + idIPMap.get(ID)).equals(ip)) {
				if (!sendMessage(generateID(ip) + "", m)) {
					return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public static boolean sendMessageToAllButX(String IP, Message m)
			throws IOException {
		List<String> IPs = new ArrayList<String>(neighbors.keySet());
		for (String ip : IPs) {
			if (!("" + IP).equals(ip)) {
				if (!sendMessageX(ip, m)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Update the number of required neighbors
	 */
	public static void updateLogN() {
		logN = (int) Math.ceil(Math.log10(allNodes.size()) / Math.log10(2));
	}


	/**
	 * Node ID generator
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public static long generateID(String IP) throws UnknownHostException {
		String hostAddress = InetAddress.getLocalHost().getHostAddress();
		if (!IP.equals("")) {
			hostAddress = IP;
		}
		hostAddress = getIP(hostAddress);
		System.out.println("Generating ID... (" + hostAddress + ")");
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
		idIPMap.put(ID + "", hostAddress);

		return ID;
	}

	/**
	 * Checks if new join request can be processed by current node.<br/>
	 * Does this by checking if number of links after the node joins in are<br/>
	 * within limits of log<i>n</i>.
	 * 
	 * @param peerSocket
	 * @return
	 */
	public static boolean nodeDropRequired(Socket peerSocket) {
		int newNetworkSize = Peer.allNodes.size();
		int presentNeighbors = Peer.neighbors.size();
		int requiredNeighbors = (int) Math.ceil(Math.log(newNetworkSize)
				/ Math.log(2));
		System.out.println("presentNeighbors: " + presentNeighbors
				+ " requiredNeighbors: " + requiredNeighbors);
		if (presentNeighbors > requiredNeighbors) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method checks if the number of links on the node are correct
	 * according to the total number of nodes in the network
	 */
	public static boolean linksSatisfied() {
		int linksPresent = neighbors.size();
		int linksRequired = (int) Math.ceil(Math.log(allNodes.size())
				/ Math.log(2));
		System.out.println("linksPresent: " + linksPresent);
		System.out.println("linksRequired: " + linksRequired);
		System.out
		.println("linksSatisfied: " + (linksPresent == linksRequired));
		return linksPresent == linksRequired;
	}
}