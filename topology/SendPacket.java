package topology;

import overlay.Client;
import overlay.Message;
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