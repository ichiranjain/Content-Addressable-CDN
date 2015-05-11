package overlay;

import java.io.Serializable;

/**
 * This class represents the message that will be directly passed on to the
 * neighbor.
 *
 * @author Gaurav Komera
 *
 * @param <K>
 *            Make sure K implements the Serializable marker interface
 */
@SuppressWarnings("serial")
public class Message<K> implements Serializable {
    //
    // :::: Message types ::::
    // 1 - add new neighbor
    // -1 - add new neighbor forcefully
    // 2 - add neighbor acknowledgement positive
    // -2 - add neighbor acknowledgement negative(node links full contact
    // new peer)
    // 3 - remove peer
    // 4 - remove peer acknowledgement
    // 100 - poll
    // 101 - poll acknowledgement
    // 7 - send packet
    // 8 - packet acknowledgement
    // 50 - sharing new neighbor info
    // 99 - random message
    // 100 poll
    // 101 poll reply
    // 102 notification about new neighbor to be added, no reply needed
    int type;
    long requestNo;
    K packet;

    // initialize type and request number
    public Message(int type) {
        this.type = type;
        requestNo = requestNumberGenerator();
    }

    // initializing type and request number
    // and also the Serializable packet that is sent by the forwarding layer
    public Message(int type, K packet) {
        this(type);
        this.packet = packet;
    }

    // constructor with custom request number
    public Message(int type, int requestNumber) {
        this.type = type;
        requestNo = requestNumberGenerator();
    }

    private long requestNumberGenerator() {
        return System.nanoTime();
    }
}