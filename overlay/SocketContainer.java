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

	public SocketContainer(Socket socket, ObjectInputStream ois,
			ObjectOutputStream oos) {
		this.socket = socket;
		this.ois = ois;
		this.oos = oos;
	}
}