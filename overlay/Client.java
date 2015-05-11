package overlay;

import packetObjects.IntrestObj;
import topology.GeneralQueueHandler;
import topology.PacketQueue2;
import topology.SendPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
	static ObjectInputStream ois;
	static ObjectOutputStream oos;
	static Link link;
	static SendPacket sendPacketObj;
	static String ID;
	static HashMap<String, String> idIPMap;
	static GeneralQueueHandler gqh;
	static PacketQueue2 pq2;
	static ProcessData pd;
	//used to get rtt, can be removed
	static ConcurrentHashMap<String, Long> rtt;

    public static void main(String[] args) throws
            IOException {
        Scanner s = new Scanner(System.in);
		sendPacketObj = new SendPacket();
		boolean clientStarted = true;
		boolean connected = false;

		//used for rtt, can be removed
		rtt = new ConcurrentHashMap<String, Long>();

		pq2 = new PacketQueue2();
		gqh = new GeneralQueueHandler(pq2, true);
		Thread gqhThread = new Thread(gqh);
		gqhThread.start();
		pd = new ProcessData(rtt);
		pd.start();

		while (clientStarted) {
			while (!connected) {
				try {
					System.out.print("Enter cache server to connect to: ");
					String cacheServerAddress = s.nextLine();
					Socket cacheServer = new Socket(cacheServerAddress, 43125);
					ois = new ObjectInputStream(cacheServer.getInputStream());
					oos = new ObjectOutputStream(cacheServer.getOutputStream());
					Message<JoinPacket> joinMessage = new Message<JoinPacket>(11); // handle
					// in
					// Peer
					oos.writeObject(joinMessage);
					link = new Link(cacheServerAddress, ois);
					link.start();
					ID = generateID(getIP(cacheServerAddress)) + "";
					connected = true;
					// oos.writeObject("joining client");
				} catch (UnknownHostException e) {
					System.out.println("Connection error.. Please try again..");
				}
			}
			System.out.println("Enter content to be fetched(EXIT to exit): ");
			String msg = s.nextLine();
			IntrestObj intrst = new IntrestObj(msg, "", 1);
			sendPacketObj.createIntrestPacket(intrst);
			sendPacketObj.forwardPacket(intrst.getOriginalPacket());
			rtt.put(msg, System.currentTimeMillis());
		}
	}

    public static long generateID(String IP) throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        if (!IP.equals("")) {
            hostAddress = IP;
        }
        hostAddress = getIP(hostAddress);
        // System.out.println("Generating ID... (" + hostAddress + ")");
        long prime1 = 105137;
        long prime2 = 179422891;
        long ID = 0;
        for (int i = 0; i < hostAddress.length(); i++) {
            char c = hostAddress.charAt(i);
            if (c != '.') {
                ID += (prime1 * (hostAddress.charAt(i) * i)) % prime2;
            }
        }
        // System.out.println("ID: " + ID);
        //idIPMap.put(ID + "", hostAddress);

        return ID;
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

	@SuppressWarnings("rawtypes")
	public static boolean sendMessage(Message m) {
		try {
			oos.writeObject(m);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
