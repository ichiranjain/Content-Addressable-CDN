package overlay;

import java.io.ObjectInputStream;

/**
 * Created by Chiran on 4/26/15.
 */
public class ServerLinks extends Thread {

    String nodeConnected;
    ObjectInputStream ois = null;
    boolean running;


    public ServerLinks(String cacheNodeIp, ObjectInputStream ois) {
        nodeConnected = cacheNodeIp;
        this.ois = ois;
        running = true;

    }

    public void run() {

        Message m = null;
        while (running) {
            try {
                m = (Message) ois.readObject();
                System.out.println("Message received from: " + nodeConnected);
                System.out.println("Message type: " + m.type);
                System.out.println("Request no: " + m.requestNo);
                handleUpdate(m);

            } catch (Exception e) {
                e.printStackTrace();
                running = false;
            }
            if (!running) {

                // inform neighbors about dropped node
            }

        }


    }

    private void handleUpdate(Message m) {

        if (m.type == 7) {
            if (m.packet instanceof String) {
                //Server.serveRequest();
            }
        }

    }


}
