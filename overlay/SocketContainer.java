package overlay;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Container object for socket, input stream and output stream
 * 
 * @author Gaurav
 *
 */
class SocketContainer {
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Link link;

	public SocketContainer(Socket socket, ObjectInputStream ois,
			ObjectOutputStream oos, Link link) {
		this.socket = socket;
		this.ois = ois;
		this.oos = oos;
		this.link = link;
	}
}