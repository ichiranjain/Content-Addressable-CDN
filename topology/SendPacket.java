package topology;

import overlay.Message;
import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.LinkObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;
import caching.ServerLFS;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * This class is used to convert an object to json and add the </br>
 * headers. The packet is saved as a json string and saved to </br>
 * original packet in the object. 
 * @author spufflez
 *
 */
public class SendPacket {

	Gson gson = new Gson();

	/**
	 * Constructor
	 */
	public SendPacket() {

	}

	/**
	 * Creates an add client packet</br>
	 * called by the overlay, to tell the cache server a client is connecting
	 * @param linkObj
	 */
	public void createAddClient(LinkObj linkObj) {
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "addClient");
		packet.addProperty("nodeName", linkObj.getNeighboringNode());
		packet.addProperty("cost", linkObj.getCost());

		linkObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates an remove client packet</br>
	 * called by the overlay, to tell the cache server a client has died
	 * @param linkObj
	 */
	public void createRemoveClient(LinkObj linkObj) {
		JsonObject packet = new JsonObject();

		packet.addProperty("type", "update");
		packet.addProperty("action", "removeClient");
		packet.addProperty("nodeName", linkObj.getNeighboringNode());
		packet.addProperty("cost", linkObj.getCost());

		linkObj.setOriginalPacket(packet.toString());
	}

	/**
	 * Creates a client prefix packet in json, this is used to send </br>
	 * one content name to the cache server
	 * @param prefixObj
	 */
	public void createClientPrefix(PrefixObj prefixObj) {
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
	 * Creates a client prefix list packet in json, this is used to send </br>
	 * a list of content names to the cache server
	 * @param prefixListObj
	 */
	public void createClientPrefixList(PrefixListObj prefixListObj) {
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
	 * Creates a prefix packet to be sent between cache servers</br>
	 * This packet will contain content names each server knows about
	 * @param prefixObj
	 */
	public void createPrefixPacket(PrefixObj prefixObj) {
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
	 * Creates a prefixList packet to be sent between cache servers</br>
	 * This packet will contain a list content names each server knows about
	 * @param prefixObj
	 */
	public void createPrefixListPacket(PrefixListObj prefixListObj) {
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
	 * Creates an interest packet 
	 * @param intrestObj
	 */
	public void createIntrestPacket(IntrestObj intrestObj) {

		JsonObject packet = new JsonObject();

		packet.addProperty("type", "route");
		packet.addProperty("action", "intrest");
		packet.addProperty("contentName", intrestObj.getContentName());
		packet.addProperty("originRouter", intrestObj.getOriginRouterName());
		packet.addProperty("nonce", intrestObj.getNonce());

		//String pkt  = packet.toString();
		intrestObj.setOriginalPacket(packet.toString());

	}

	/**
	 * Creates a Data packet
	 * @param dataObj
	 */
	public void createDataPacket(DataObj dataObj) {
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



	/**
	 * Send a packet to the cache server
	 * @param packet
	 */
	public void forwardPacket(String packet, String nextHop) {

		// this will forward a packet to only the router specified
		Message<String> packetMessage = new Message<String>(7, packet);
		ServerLFS.sendMessage(nextHop, packetMessage);
		System.out.println("-------------------------------------------");
		System.out.println("    -Forward packet next hop provided-");
		//System.out.println("packet: " + packet);
		//System.out.println("nextHop: " + nextHop);
		System.out.println("-------------------------------------------");
		System.out.println("");
	}

}

