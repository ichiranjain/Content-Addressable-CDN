package topology;

import overlay.Message;
import overlay.Peer;
import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class SendPacket {

	Gson gson = new Gson();

	public SendPacket(){

	}

	//	public void createAddClient(LinkObj linkObj){
	//		JsonObject packet = new JsonObject();
	//
	//		packet.addProperty("type", "update");
	//		packet.addProperty("action", "addClient");
	//		packet.addProperty("nodeName", linkObj.getNeighboringNode());
	//		packet.addProperty("cost", linkObj.getCost());
	//
	//		linkObj.setOriginalPacket(packet.toString());
	//	}
	//	public void createRemoveClient(LinkObj linkObj){
	//		JsonObject packet = new JsonObject();
	//
	//		packet.addProperty("type", "update");
	//		packet.addProperty("action", "removeClient");
	//		packet.addProperty("nodeName", linkObj.getNeighboringNode());
	//		packet.addProperty("cost", linkObj.getCost());
	//
	//		linkObj.setOriginalPacket(packet.toString());
	//	}
	public void createClientPrefix(PrefixObj prefixObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "clientPrefix");
		packet.addProperty("prefix", prefixObj.getPrefixName());
		packet.addProperty("addRemove", prefixObj.getAddRemoveFlag());
		packet.addProperty("advertiser", prefixObj.getAdvertiser());
		packet.addProperty("msgID", prefixObj.getMsgID());

		prefixObj.setOriginalPacket(packet.toString());
	}
	public void createClientPrefixList(PrefixListObj prefixListObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "clientPrefixList");
		packet.addProperty("addRemove", prefixListObj.getAddRemoveFlag());
		String prefixArray = gson.toJson(prefixListObj.getPrefixList());
		packet.addProperty("prefixList", prefixArray);
		packet.addProperty("advertiser", prefixListObj.getAdvertiser());
		packet.addProperty("msgID", prefixListObj.getMsgID());

		prefixListObj.setOriginalPacket(packet.toString());
	}

	public void createIntrestPacket(IntrestObj intrestObj){

		JsonObject packet = new JsonObject();

		packet.addProperty("type", "route");
		packet.addProperty("action", "intrest");
		packet.addProperty("contentName", intrestObj.getContentName());
		packet.addProperty("originRouter", intrestObj.getOriginRouterName());
		packet.addProperty("nonce", intrestObj.getNonce());

		//String pkt  = packet.toString();
		intrestObj.setOriginalPacket(packet.toString());

	}
	public void createDataPacket(DataObj dataObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "route");
		packet.addProperty("action", "data");
		packet.addProperty("flag", dataObj.getFlag());
		packet.addProperty("contentName", dataObj.getContentName());
		packet.addProperty("originRouter", dataObj.getOriginRouterName());
		packet.addProperty("data", dataObj.getData());
		packet.addProperty("cacheFlag", dataObj.getCacheFlag());
		packet.addProperty("lastChunk", dataObj.getLastChunk());

		//String pkt  = packet.toString();
		dataObj.setOriginalPacket(packet.toString());

	}

	/*
	 * Depending on what I need to pass to gurav's function 
	 * if it can be made generic, then use on generic 
	 * forward function instead of separate forward functions
	 */
	public void forwardPacket(String packet, String nextHop){

		// this will forward a packet to only the router specified
		Message<String> packetMessage = new Message<String>(7, packet);
		Peer.sendMessage(nextHop, packetMessage);
		System.out.println("    -Forward packet next hop provided-");
		System.out.println("packet: " + packet);
		System.out.println("nextHop: " + nextHop);
		System.out.println("-------------------------------------------");
		System.out.println("");
	}

	//	public void broadcast(String packet) throws IOException {
	//
	//		// this will forward to everyone routers and clients
	//
	//		Message<String> packetMessage = new Message<String>(7, packet);
	//		Peer.sendMessageToAllBut("", packetMessage);
	//		System.out.println("    -Broadcast-");
	//		System.out.println("packet: " + packet);
	//		System.out.println("-------------------------------------------");
	//		System.out.println("");
	//	}
	//
	//	public void forwardUpdate(String packet, String doNotSendToNode)
	//			throws IOException {
	//		// this will forward to all routers except the router name passed into
	//		// the function
	//		Message<String> packetMessage = new Message<String>(7, packet);
	//		Peer.sendMessageToAllBut(doNotSendToNode, packetMessage);
	//		System.out.println("    -ForwardUpdate do not send node provided-");
	//		System.out.println("packet: " + packet);
	//		System.out.println("doNotSendToNode: " + doNotSendToNode);
	//		System.out.println("-------------------------------------------");
	//		System.out.println("");
	//		//boolean true, send to routers 
	//	}
	//
	//	public void forwardToAllRouters(String packet) throws IOException {
	//		// this forwards the packet to all routers only
	//		Message<String> packetMessage = new Message<String>(7, packet);
	//		Peer.sendMessageToAllBut("", packetMessage);
	//		System.out.println("    -Forward to all routers no hops provided-");
	//		System.out.println("packet: " + packet);
	//		System.out.println("-------------------------------------------");
	//		System.out.println("");
	//		//boolean
	//	}

}

//	public void sendTablePacket(TableObj tableObj){
//		ArrayList<JsonObject> nodeArray = new ArrayList<JsonObject>();
//		JsonObject node;
//
//
//		for(int i = 0; i < tableObj.getGraph().size(); i++){
//
//			node = new JsonObject();
//			node.addProperty("neighbors", gson.toJson(tableObj.getGraph().get(i).getNeighbors()));
//			node.addProperty("name", tableObj.getGraph().get(i).getName());
//			nodeArray.add(node);
//
//		}
//
//		JsonObject packet = new JsonObject();
//		packet.addProperty("type", "update");
//		packet.addProperty("action", "table");
//		packet.addProperty("graph", gson.toJson(nodeArray));
//
//
//		String pkt  = packet.toString();
//		tableObj.setOriginalPacket(pkt);
//	}
