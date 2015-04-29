package caching;

/**
 * Created by rushabhmehta91 on 4/13/15.
 */
public class ContentPacket {

    // incomingPacketType: 0=request,1=reply,2=incomingContent
    private int incomingPacketType;
    private Object data;

    public ContentPacket(int incomingPacketType, Object data) {
        this.incomingPacketType = incomingPacketType;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getIncomingPacketType() {
        return incomingPacketType;
    }

    public void setIncomingPacketType(int incomingPacketType) {
        this.incomingPacketType = incomingPacketType;
    }

    public String toString(ContentPacket cp) {
        if (cp.data instanceof Content) {
            Content c = (Content) cp.getData();
            return "Incoming Packet type: " + cp.getIncomingPacketType() + " " + " Content Name: " + c.getContentName();
        } else if (cp.data instanceof String) {
            String requestedContentName = (String) cp.data;
            return "Incoming Packet type: " + cp.getIncomingPacketType() + " " + " Content Name: " + requestedContentName;
        } else {
            return " Type of Object unknown";
        }
    }
}
