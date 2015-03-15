package overlay;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;

public class Peer {
	static ServerSocket serverSocket;
	static Socket peerSocket;
	static HashMap<String, SocketContainer> peerSockets;
	static HashMap<String, Integer> vacancies;
	static HashSet<String> allNodes;
	static PrintStream os;
	static Scanner s;
	static int neighbors;
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
		peerSocket = null;
		peerSockets = new HashMap<String, SocketContainer>();
		allNodes = new HashSet<String>();
		os = null;
		s = null;
		neighbors = 0;
		logN = 0;
	}

	public Peer() {

	}

	public static void updateLogN() {
		logN = (int) Math.ceil(Math.log10(allNodes.size()) / Math.log10(2));
	}

	public static void main(String[] args) throws IOException {
		System.out.println("1 - Start Node");
		System.out.println("2 - Join Node");

		s = new Scanner(System.in);
		int x = Integer.parseInt(s.nextLine());
		if (x == 1) {
			System.out.println("Starting node...");
			System.out.println("IP: "
					+ InetAddress.getLocalHost().getHostAddress());
			// creating peer
			System.out.println("Waiting for client peer...");
			peerSocket = serverSocket.accept();
			addPeer(peerSocket);

			System.out.println("Client peer now connected... IP: "
					+ peerSocket.getRemoteSocketAddress());
		} else if (x == 2) {
			System.out.print("Please enter server address: ");
			String server = s.nextLine();
			peerSocket = new Socket(server, 43125);
			addPeer(peerSocket);
		}
		new Receive(peerSocket.getRemoteSocketAddress() + "").start();

		Listen listen = new Listen(serverSocket);
		listen.start();

		// send msgs to neighbors
		while (true) {
			try {
				String line = s.nextLine();
				String[] parts = line.split("=");
				if (peerSockets.containsKey(parts[0])) {
					System.out.println("Sending msg to " + parts[0] + "...");
					os = peerSockets.get(parts[0]).os;
					os.println(parts[1]);
				} else {

				}
			} catch (Exception e) {
			}
		}
	}

	public static void addPeer(Socket peerSocket) throws IOException {
		DataInputStream is = new DataInputStream(peerSocket.getInputStream());
		PrintStream os = new PrintStream(peerSocket.getOutputStream());
		peerSockets.put(peerSocket.getRemoteSocketAddress() + "",
				new SocketContainer(peerSocket, is, os));
		allNodes.add(peerSocket.getRemoteSocketAddress().toString());
		neighbors = peerSockets.size();
		updateNeighbors(peerSocket.getRemoteSocketAddress().toString());
		updateLogN();
	}

	public static void updateNeighbors(String newPeer) {
		// send neighbors with new peer info
		StringBuilder sb = new StringBuilder();
		sb.append("vacancies");
		for (Entry<String, Integer> entry : vacancies.entrySet()) {
			sb.append("#");
			sb.append(entry.getKey());
			sb.append("#");
			sb.append(entry.getValue());
		}
		for (Entry<String, SocketContainer> entry : peerSockets.entrySet()) {
			String key = entry.getKey();
			SocketContainer sc = entry.getValue();
			sc.os.println(sc.socket.getRemoteSocketAddress() + "="
					+ sb.toString());
		}

		// send >new peer info about existing neighbors
		sb = new StringBuilder();
		sb.append("neighbors");
		for (Entry<String, SocketContainer> entry : peerSockets.entrySet()) {
			sb.append("#");
			sb.append(entry.getKey());
		}
	}
}

/**
 * Container object for socket, input stream and output stream
 * 
 * @author Gaurav
 *
 */
class SocketContainer {
	Socket socket;
	DataInputStream is;
	PrintStream os;

	public SocketContainer(Socket socket, DataInputStream is, PrintStream os) {
		this.socket = socket;
		this.is = is;
		this.os = os;
	}
}

/**
 * Read from command line and send to the connected peer using socket.
 * 
 * @author Gaurav
 *
 */
class Send extends Thread {

	PrintStream os;
	String peerAddress;

	public Send(String peerAddress) throws IOException {
		this.peerAddress = peerAddress;
		os = new PrintStream(Peer.peerSockets.get(peerAddress).os);
	}

	@Override
	public void run() {
		while (true) {
			try {
				String line = Peer.s.nextLine();
				String[] parts = line.split("=");
				if (Peer.peerSockets.containsKey(parts[0])) {
					System.out.println("Sending msg to " + parts[0] + "...");
					os = Peer.peerSockets.get(parts[0]).os;
					os.println(parts[1]);
				}
			} catch (Exception e) {

			}
		}
	}
}

/**
 * Receive from neighbor are print it out
 * 
 * @author Gaurav
 *
 */
class Receive extends Thread {
	DataInputStream is = null;

	public Receive(String peerAddress) throws IOException {
		is = new DataInputStream(Peer.peerSockets.get(peerAddress).is);
	}

	@Override
	public void run() {
		int attempt = 0;
		System.out.println("Input Stream started...");
		while (true) {
			try {
				String receivedContent = is.readLine();
				String[] parts = receivedContent.split("#");
				if (parts[0].equals("vacancies")) {

				} else if (parts[0].equals("allNodes")) {
					for (int i = 1; i < parts.length; i++) {
						Peer.allNodes.add(parts[i]);
					}
				}
				System.out.println(is.readLine());
				attempt = 0;
				// handle updates
			} catch (IOException e) {
				attempt++;
				e.printStackTrace();
				if (attempt == 3) {
					try {
						is.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}

class Listen extends Thread {
	ServerSocket serverSocket;

	Socket peerSocket;

	public Listen(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		peerSocket = null;
	}

	@Override
	public void run() {
		while (true) {
			try {
				peerSocket = serverSocket.accept();
				Peer.addPeer(peerSocket);
				new Receive(peerSocket.getRemoteSocketAddress() + "").start();
				System.out.println("Client peer now connected... IP: "
						+ peerSocket.getRemoteSocketAddress());
				System.out.println("New map: " + Peer.peerSockets);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
