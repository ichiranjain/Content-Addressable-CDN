package topology;

import java.io.IOException;

import overlay.Message;
import overlay.Peer;
import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.LinkObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * This class creates packets</br>
 * It takes an object and converts it to its json packet form</br>
 * the json string is then saved to the object's original packet variable
 * @author spufflez
 *
 */
public class SendPacket {

	Gson gson = new Gson();

	/**
	 * Constructor
	 */
	public SendPacket(){

	}

	/**
	 * Creates an add link packet</br>
	 * This is called by the overlay when a cache server connects to this </br>
	 * cache server. The cache servers will become neighbors.
	 * @param linkObj
	 */
	public void createAddLinkPacket(LinkObj linkObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "addLink");
		packet.addProperty("nodeName", linkObj.getNeighboringNode());
		packet.addProperty("cost", linkObj.getCost());

		linkObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates an remove link packet</br>
	 * This is called by the overlay when a cache server is no longer a </br>
	 * neighbor to this cache server 
	 * @param linkObj
	 */
	public void createRemoveLinkPacket(LinkObj linkObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "removeLink");
		packet.addProperty("nodeName", linkObj.getNeighboringNode());
		packet.addProperty("cost", linkObj.getCost());

		linkObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates an modify link packet</br>
	 * This is called by the overlay when the cost to reach a neighboring </br>
	 * cache server changes 
	 * @param linkObj
	 */
	public void createModifyLinkPacket(LinkObj linkObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "modifyLink");
		packet.addProperty("nodeName", linkObj.getNeighboringNode());
		packet.addProperty("cost", linkObj.getCost());

		linkObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates an add client packet</br>
	 * This is called by the overlay when a client/server connects to this </br>
	 * cache server. 
	 * @param linkObj
	 */
	public void createAddClient(LinkObj linkObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "addClient");
		packet.addProperty("nodeName", linkObj.getNeighboringNode());
		packet.addProperty("cost", linkObj.getCost());

		linkObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates an remove client packet</br>
	 * This is called by the overlay when a client/server disconnects from</br>
	 * this cache server. 
	 * @param linkObj
	 */
	public void createRemoveClient(LinkObj linkObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "removeClient");
		packet.addProperty("nodeName", linkObj.getNeighboringNode());
		packet.addProperty("cost", linkObj.getCost());

		linkObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates an add client prefix packet</br>
	 * This is called by the client/server when a client/server wants to  </br>
	 * advertise a content name. (sent from server to cache server)
	 * @param prefixObj
	 */
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

	/**
	 * Creates an add client prefix packet</br>
	 * This is called by the client/server when a client/server wants to  </br>
	 * advertise a list of content names. (sent by server to cache server)
	 * @param prefixListObj
	 */
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

	/**
	 * Creates an modify node packet</br>
	 * This is called by the cache server when an update needs to be sent  </br>
	 * to the rest of the cache server. It contains the cache servers ID</br>
	 * and neighbors with costs.
	 * @param prefixObj
	 */
	public void createModifyNodePacket(ModifyNodeObj modifyNodeObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "modify");
		packet.addProperty("neighbors", gson.toJson(modifyNodeObj.getNeighbors()));
		packet.addProperty("nodeName", modifyNodeObj.getName());
		packet.addProperty("msgID", modifyNodeObj.getMsgID());

		modifyNodeObj.setOriginalPacket(packet.toString());

	}

	/**
	 * Creates an prefix packet</br>
	 * This is called by the cache server when it needs to send an update  </br>
	 * to the rest of the cache servers about new content names it has learned.</br>
	 * cache server to cache sever communication 
	 * @param prefixObj
	 */
	public void createPrefixPacket(PrefixObj prefixObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "prefix");
		packet.addProperty("addRemove", prefixObj.getAddRemoveFlag());
		packet.addProperty("prefix", prefixObj.getPrefixName());
		packet.addProperty("advertiser", prefixObj.getAdvertiser());
		packet.addProperty("msgID", prefixObj.getMsgID());

		prefixObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates an prefixList packet</br>
	 * This is called by the cache server when it needs to send an update  </br>
	 * to the rest of the cache servers about several new content names </br>
	 * it has learned. ( a list of content names) </br>
	 * cache server to cache sever communication
	 * @param prefixObj
	 */
	public void createPrefixListPacket(PrefixListObj prefixListObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "prefixList");
		packet.addProperty("addRemove", prefixListObj.getAddRemoveFlag());
		String prefixArray = gson.toJson(prefixListObj.getPrefixList());
		packet.addProperty("prefixList", prefixArray);
		packet.addProperty("advertiser", prefixListObj.getAdvertiser());
		packet.addProperty("msgID", prefixListObj.getMsgID());

		prefixListObj.setOriginalPacket(packet.toString());

	}

	/**
	 * Creates an Request Neighbors Interest packet packet</br>
	 * This is an interest packet addressed to a cache server, with the   </br>
	 * content name "cache server ID/np", when the cache server receives the</br>
	 * interest packet the /np will signal it to process it as a request </br>
	 * neighbors packet. </br>
	 * cache server to cache sever communication
	 * @param intrestObj
	 */
	public void createRequestNeighborsIntrestPacket(IntrestObj intrestObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "route");
		packet.addProperty("action", "intrest");
		packet.addProperty("nonce", intrestObj.getNonce());
		packet.addProperty("contentName", intrestObj.getContentName());
		packet.addProperty("originRouter", intrestObj.getOriginRouterName()); 

		intrestObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates an Neighbors Data packet</br>
	 * This is an data packet addressed to a cache server, with the   </br>
	 * content name "cache server ID/np", it will follow the pit entries back </br>
	 * to the cache server that made the request.</br>
	 * when the cache server receives the data packet the /np will signal</br>
	 * it to process it request neighbors response</br>
	 * The data field of the packet will contain an embedded modify node packet</br>
	 * this packet will then be processed as a modify node packet would be processed</br> 
	 * cache server to cache sever communication
	 * @param intrestObj
	 */
	public void createNeighborsDataPacket(DataObj dataObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "route");
		packet.addProperty("action", "data");
		packet.addProperty("flag", dataObj.getFlag());
		packet.addProperty("contentName", dataObj.getContentName());
		packet.addProperty("originRouter", dataObj.getOriginRouterName());
		packet.addProperty("data", dataObj.getData());

		dataObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates an PrefixList Data packet</br>
	 * This is an data packet addressed to a cache server, with the   </br>
	 * content name "cache server ID/np", it will follow the pit entries back </br>
	 * to the cache server that made the request.</br>
	 * when the cache server receives the data packet the /np will signal</br>
	 * it to process it request prefix response</br>
	 * The data field of the packet will contain an embedded prefixList packet</br>
	 * this packet will then be processed as a prefixList packet would be processed</br> 
	 * cache server to cache sever communication
	 * @param intrestObj
	 */
	public void createPrefixListDataPacket(DataObj dataObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "route");
		packet.addProperty("action", "data");
		packet.addProperty("flag", dataObj.getFlag());
		packet.addProperty("contentName", dataObj.getContentName());
		packet.addProperty("originRouter", dataObj.getOriginRouterName());
		packet.addProperty("data", dataObj.getData());

		dataObj.setOriginalPacket(packet.toString());
	}


	/**
	 * This creates an interest packet
	 * @param intrestObj
	 */
	public void createIntrestPacket(IntrestObj intrestObj){

		JsonObject packet = new JsonObject();

		packet.addProperty("type", "route");
		packet.addProperty("action", "intrest");
		packet.addProperty("contentName", intrestObj.getContentName());
		packet.addProperty("originRouter", intrestObj.getOriginRouterName());
		packet.addProperty("nonce", intrestObj.getNonce());

		intrestObj.setOriginalPacket(packet.toString());

	}

	/**
	 * This creates a data packet 
	 * @param dataObj
	 */
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

		dataObj.setOriginalPacket(packet.toString());

	}


	/**
	 * This creates a neighbors request packet</br>
	 * When a request for neighbors is received as an interest packet, it is</br>
	 * converted to an neighbor request packet and placed in the update queue</br>
	 * This is done because the class to process the updates has access to all the</br>
	 * neighbor lists and FIB.</br>
	 * This packet will be processed by the cache server and the response will be</br>
	 * sent as a data packet back to the cache server that made the request.
	 * @param neighborRequestObj
	 */
	public void createNeighborRequestPacket(NeighborRequestObj neighborRequestObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "neighborRequest");
		packet.addProperty("contentName", neighborRequestObj.getContentName());
		packet.addProperty("originRouter", neighborRequestObj.getOriginRouter());
		packet.addProperty("nextHop", neighborRequestObj.getNextHop());

		String pkt  = packet.toString();
		neighborRequestObj.setOriginalPacket(pkt);
	}

	/**
	 * This creates a prefix response packet</br>
	 * When a cache server receives a data packet containing a prefix response (content names)</br>
	 * to a prefix request it sent out. The embedded packet is converted into a</br>
	 * prefix response packet and placed in the update queue, to be processed</br>
	 * This is done because the only the update class is capable of applying updates.</br>
	 * The routing class can not apply updates to the graph</br>
	 * @param neighborRequestObj
	 */
	public void createPrefixResponsePacket(PrefixListObj prefixListObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "prefixResponse");
		packet.addProperty("addRemove", prefixListObj.getAddRemoveFlag());
		String prefixArray = gson.toJson(prefixListObj.getPrefixList());
		packet.addProperty("prefixList", prefixArray);
		packet.addProperty("advertiser", prefixListObj.getAdvertiser());
		packet.addProperty("msgID", prefixListObj.getMsgID());

		prefixListObj.setOriginalPacket(packet.toString());
	}

	/**
	 * This creates a neighbor response packet</br>
	 * When a cache server receives a data packet containing a neighbor response /br>
	 * to a neighbor request it sent out. The embedded packet is converted into a</br>
	 * neighbor response packet and placed in the update queue, to be processed</br>
	 * This is done because the only the update class is capable of applying updates.</br>
	 * The routing class can not apply updates to the graph</br>
	 * @param neighborRequestObj
	 */
	public void createNeighborResponsePacket(ModifyNodeObj modifyNodeObj){
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "neighborResponse");
		packet.addProperty("neighbors", gson.toJson(modifyNodeObj.getNeighbors()));
		packet.addProperty("nodeName", modifyNodeObj.getName());
		packet.addProperty("msgID", modifyNodeObj.getMsgID());

		modifyNodeObj.setOriginalPacket(packet.toString());
	}



	/**
	 * This is used to forward a packet to the next hop provided.
	 * @param packet
	 * @param nextHop
	 */
	public void forwardPacket(String packet, String nextHop){

		// this will forward a packet to only the router specified
		Message<String> packetMessage = new Message<String>(7, packet);
		System.out.println("nextHop: " + nextHop);
		Peer.sendMessage(nextHop, packetMessage);
		System.out.println("    -Forward packet next hop provided-");
		System.out.println("packet: " + packet);
		System.out.println("nextHop: " + nextHop);
		System.out.println("-------------------------------------------");
		System.out.println("");
	}

	/**
	 * This is used to broadcast the packet to all the cache servers </br>
	 * and clients connected to this cache server. 
	 * @param packet
	 * @throws IOException
	 */
	public void broadcast(String packet) throws IOException {

		// this will forward to everyone routers and clients

		Message<String> packetMessage = new Message<String>(7, packet);
		Peer.sendMessageToAllBut("", packetMessage);
		//		System.out.println("    -Broadcast-");
		//		System.out.println("packet: " + packet);
		//		System.out.println("-------------------------------------------");
		//		System.out.println("");
	}

	/**
	 * This is used to broadcast the packet to all the cache servers </br>
	 * connected to this cache server except the to the "do not send to node".</br>
	 * This is done so updates can be sent out to all the cache server </br>
	 * except the cache that the update was recieved from. 
	 * @param packet
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void forwardUpdate(String packet, String doNotSendToNode)
			throws IOException {
		// this will forward to all routers except the router name passed into
		// the function
		Message<String> packetMessage = new Message<String>(7, packet);
		Peer.sendMessageToAllBut(doNotSendToNode, packetMessage);
		//		System.out.println("    -ForwardUpdate do not send node provided-");
		//		System.out.println("packet: " + packet);
		//		System.out.println("doNotSendToNode: " + doNotSendToNode);
		//		System.out.println("-------------------------------------------");
		//		System.out.println("");
		//boolean true, send to routers 
	}

	/**
	 * This is used to broadcast the packet to all the cache servers </br>
	 * connected to this cache server. 
	 * @param packet
	 * @throws IOException
	 */
	public void forwardToAllRouters(String packet) throws IOException {
		// this forwards the packet to all routers only
		Message<String> packetMessage = new Message<String>(7, packet);
		Peer.sendMessageToAllBut("", packetMessage);
		//		System.out.println("    -Forward to all routers no hops provided-");
		//		System.out.println("packet: " + packet);
		//		System.out.println("-------------------------------------------");
		//		System.out.println("");
		//boolean
	}

}

