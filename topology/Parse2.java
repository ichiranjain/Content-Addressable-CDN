package topology;

import packetObjects.DataObj;
import packetObjects.IntrestObj;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This class parses raw packets into there corresponding object
 * @author spufflez
 *
 */
public class Parse2 {

	Gson gson = new Gson();


	/**
	 * Constructor
	 */
	public Parse2(){

	}

	/**
	 * Parses an interest packet 
	 * @param jsonObject
	 * @param originalPacket
	 * @return interest object 
	 * @throws Exception
	 */
	public IntrestObj parseIntrestJson(JsonObject jsonObject, String originalPacket) throws Exception {

		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonContentNameElement = jsonObject.get("contentName");
		String contentName = jsonContentNameElement.getAsString();

		JsonElement jsonSenderNameElement = jsonObject.get("originRouter");
		String senderName = jsonSenderNameElement.getAsString();

		JsonElement jsonNonceElement = jsonObject.get("nonce");
		int nonce = jsonNonceElement.getAsInt();

		IntrestObj intrestInfo = new IntrestObj(contentName, senderName, nonce, originalPacket);
		return intrestInfo;
	}

	/**
	 * Parses a data packet 
	 * @param jsonObject
	 * @param originalPacket
	 * @return data object 
	 * @throws Exception
	 */
	public DataObj parseDataJson(JsonObject jsonObject, String originalPacket) throws Exception {

		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonContentNameElement = jsonObject.get("contentName");
		String contentName = jsonContentNameElement.getAsString();

		JsonElement jsonOriginRouterElement = jsonObject.get("originRouter");
		String originRouter = jsonOriginRouterElement.getAsString();

		JsonElement jsonFlagElement = jsonObject.get("flag");
		byte flag = jsonFlagElement.getAsByte();

		JsonElement jsonDataElement = jsonObject.get("data");
		String data = jsonDataElement.getAsString();

		JsonElement jsonCacheFlagElement = jsonObject.get("cacheFlag");
		byte cacheFlag = jsonCacheFlagElement.getAsByte();

		JsonElement jsonLastChunkElement = jsonObject.get("lastChunk");
		boolean lastChunk = jsonLastChunkElement.getAsBoolean();

		DataObj dataInfo = new DataObj(contentName, originRouter, flag, data, originalPacket, cacheFlag, lastChunk);
		return dataInfo; 
	}

}
