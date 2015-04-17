package overlayInterface;

import java.io.IOException;

import overlay.JoinPacket;
import overlay.Message;
import overlay.Peer;

public interface PeerInterface {

	void start() throws IOException;

	boolean join(String peer) throws IOException, ClassNotFoundException,
			InterruptedException;

	// void addPeer(JoinPacket packet) throws IOException;

	void updateNeighbors(JoinPacket packet) throws IOException;

	void listen();

	void remove(Peer p);

	boolean sendMessage(long neighbor, Message m);

}
