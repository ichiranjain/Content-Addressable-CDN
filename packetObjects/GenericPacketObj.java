package packetObjects;

public class GenericPacketObj<K> {

	String action;
	String recievedFromNode;
	K obj;


	public GenericPacketObj(String action, String recievedFromNode, K obj){
		this.action = action;
		this.recievedFromNode = recievedFromNode;
		this.obj = obj;
	}

	public String getAction() {
		//GenericPacketObj<LinkObj> o = new GenericPacketObj<LinkObj>("action", "recFrom", new LinkObj("as"));
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getRecievedFromNode() {
		return recievedFromNode;
	}

	public void setRecievedFromNode(String recievedFromNode) {
		this.recievedFromNode = recievedFromNode;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(K obj) {
		this.obj = obj;
	}
}
