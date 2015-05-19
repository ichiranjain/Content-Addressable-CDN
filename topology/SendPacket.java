package topology;

import overlay.Client;
import overlay.Message;
import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;

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
	public SendPacket(){

	}

	/**
	 * Creates a client prefix packet in json, this is used to send </br>
	 * one content name to the cache server
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
	 * Creates a client prefix list packet in json, this is used to send </br>
	 * a list of content names to the cache server
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
	 * Creates an interest packet 
	 * @param intrestObj
	 */
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

	/**
	 * Creates a Data packet
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
	 * Send a packet to the cache server
	 * @param packet
	 */
	public void forwardPacket(String packet) {

		// this will forward a packet to only the router specified
		Message<String> packetMessage = new Message<String>(7, packet);
		Client.sendMessage(packetMessage);
		System.out.println("    -Forward packet next hop provided-");
		System.out.println("packet: " + packet);
		System.out.println("-------------------------------------------");
		System.out.println("");
	}
}