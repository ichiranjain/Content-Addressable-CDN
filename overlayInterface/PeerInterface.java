package overlayInterface;

import java.io.IOException;
import java.net.Socket;

import overlay.JoinPacket;
import overlay.Peer;

public interface PeerInterface {

	void start() throws IOException;

	boolean join(String peer) throws IOException, ClassNotFoundException;

	void addPeer(JoinPacket packet) throws IOException;

	void updateNeighbors(Socket newPeer, JoinPacket packet);

	void listen();

	void remove(Peer p);
}
