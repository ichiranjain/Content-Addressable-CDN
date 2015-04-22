package topology;
import java.lang.reflect.Type;
import java.util.ArrayList;

import packetObjects.AddNodeObj;
import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.LinkObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;
import packetObjects.RemoveNodeObj;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


public class Parse2 {

	Gson gson = new Gson();

	public Parse2(){

	}

	//	public String parseType(String jsonString){
	//
	//		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
	//		JsonElement jsonNameElement = jsonObject.get("type");
	//		String typeInfo = jsonNameElement.getAsString();
	//
	//		return typeInfo;
	//	}
	//
	//	public String parseMsgID(String jsonString){
	//		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
	//		JsonElement jsonNameElement = jsonObject.get("msgID");
	//		String msgIDInfo = jsonNameElement.getAsString();
	//
	//		return msgIDInfo;
	//	}
	//
	//	public String parseAction(String jsonString){
	//		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
	//		JsonElement jsonNameElement = jsonObject.get("action");
	//		String actionInfo = jsonNameElement.getAsString();
	//
	//		return actionInfo;
	//	}


	public AddNodeObj parseAddNodeJson(JsonObject jsonObject, String originalPacket){

		JsonElement jsonNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNameElement.getAsString();


		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();


		JsonElement jsonNeighborsElement = jsonObject.get("neighbors");
		String neighborsString = jsonNeighborsElement.getAsString();
		Type neighborsType = new TypeToken<ArrayList<NeighborAndCostStrings>>(){}.getType();
		ArrayList<NeighborAndCostStrings> neighborsList = gson.fromJson(neighborsString, neighborsType);


		AddNodeObj addNodeInfo = new AddNodeObj(nodeName, neighborsList, msgID, originalPacket);

		return addNodeInfo;
	}

	public ModifyNodeObj parseModifyNodeJson(JsonObject jsonObject, String originalPacket){

		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNameElement.getAsString();

		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();

		JsonElement jsonNeighborsElement = jsonObject.get("neighbors");
		String neighborsString = jsonNeighborsElement.getAsString();
		Type neighborsType = new TypeToken<ArrayList<NeighborAndCostStrings>>(){}.getType();
		ArrayList<NeighborAndCostStrings> neighborsList = gson.fromJson(neighborsString, neighborsType);


		ModifyNodeObj modifyNodeInfo = new ModifyNodeObj(nodeName, neighborsList, msgID, originalPacket);
		return modifyNodeInfo;
	}

	public RemoveNodeObj parseRemoveNodeJson(JsonObject jsonObject, String originalPacket){

		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("removeNodeName");
		String removeNodeName = jsonNameElement.getAsString();

		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();

		RemoveNodeObj removeNodeInfo = new RemoveNodeObj(removeNodeName, msgID, originalPacket);
		return removeNodeInfo;
	}

	public PrefixObj parsePrefixJson(JsonObject jsonObject, String originalPacket){

		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonContentNameElement = jsonObject.get("prefix");
		String contentName = jsonContentNameElement.getAsString();

		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();

		JsonElement jsonAddRemoveElement = jsonObject.get("addRemove");
		boolean addRemove = jsonAddRemoveElement.getAsBoolean();

		JsonElement jsonAdvertiserElement = jsonObject.get("advertiser");
		String advertiser = jsonAdvertiserElement.getAsString();

		PrefixObj prefixInfo = new PrefixObj(contentName, msgID, advertiser, addRemove, originalPacket);
		return prefixInfo;
	}

	//	public boolean parsePrefixAddRemove(String jsonString){
	//		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
	//		JsonElement jsonAddRemoveElement = jsonObject.get("addRemove");
	//		boolean addRemove = jsonAddRemoveElement.getAsBoolean();
	//		return addRemove;
	//	}

	public IntrestObj parseIntrestJson(JsonObject jsonObject, String originalPacket){

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

	public DataObj parseDataJson(JsonObject jsonObject, String originalPacket){

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

	//	public String parseContentName(String jsonString){
	//		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
	//		JsonElement jsonContentNameElement = jsonObject.get("contentName");
	//		String contentName = jsonContentNameElement.getAsString();
	//
	//		return contentName;
	//	}
	//
	//	public byte parseDataFlag(String jsonString){
	//		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
	//		JsonElement jsonFlagElement = jsonObject.get("flag");
	//		byte flag = jsonFlagElement.getAsByte();
	//		return flag;
	//	}

	//	public HelloObj parseHelloJson(String jsonString){
	//
	//		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
	//		JsonElement jsonFromNodeElement = jsonObject.get("fromNode");
	//		String fromNode = jsonFromNodeElement.getAsString();
	//
	//		JsonElement jsonRequestTableElement = jsonObject.get("requestTable");
	//		boolean requestTable = jsonRequestTableElement.getAsBoolean();
	//
	//		HelloObj helloObj = new HelloObj(fromNode, requestTable);
	//		return helloObj;
	//	}
	//
	//	public boolean parseRequestTable(String jsonString){
	//
	//		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
	//		JsonElement jsonRequestTableElement = jsonObject.get("requestTable");
	//		boolean requestTable = jsonRequestTableElement.getAsBoolean();
	//		return requestTable;
	//	}
	//
	//	public TableObj parseTableJson(String jsonString){
	//
	//		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
	//		//		JsonElement jsonFromNodeElement = jsonObject.get("fromNode");
	//		//		String fromNode = jsonFromNodeElement.getAsString();
	//
	//		JsonElement jsonGraphElement = jsonObject.get("graph");
	//		String graphString = jsonGraphElement.getAsString();
	//		Type graphType = new TypeToken<ArrayList<Node>>(){}.getType();
	//		ArrayList<Node> graph = gson.fromJson(graphString, graphType);
	//
	//		TableObj tableObj = new TableObj(graph);
	//		return tableObj;
	//
	//	}

	public PrefixListObj parsePrefixListJson(JsonObject jsonObject){
		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();

		JsonElement jsonAddRemoveElement = jsonObject.get("addRemove");
		boolean addRemove = jsonAddRemoveElement.getAsBoolean();

		JsonElement jsonAdvertiserElement = jsonObject.get("advertiser");
		String advertiser = jsonAdvertiserElement.getAsString();

		JsonElement JE = jsonObject.get("prefixList");
		Type TYPE = new TypeToken<ArrayList<String>>(){}.getType();
		ArrayList<String> prefixList = new Gson().fromJson(JE.getAsString(), TYPE);

		PrefixListObj prefixListObj = new PrefixListObj(prefixList, advertiser, addRemove, msgID);
		return prefixListObj;
	}

	public LinkObj parseClientAddNodeJson(JsonObject jsonObject){

		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		LinkObj linkObjInfo = new LinkObj(nodeName, cost);

		return linkObjInfo;

	}

	public LinkObj parseClientRemoveNodeJson(JsonObject jsonObject){
		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("nodeName");
		String removeNodeName = jsonNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		LinkObj linkObjInfo = new LinkObj(removeNodeName, cost);

		return linkObjInfo;
	}

	public LinkObj parseAddLink(JsonObject jsonObject){
		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonNodeNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNodeNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		return new LinkObj(nodeName, cost);
	}

	public LinkObj parseRemoveLink(JsonObject jsonObject){
		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonNodeNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNodeNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		return new LinkObj(nodeName, cost);
	}

	public LinkObj parseModifyLink(JsonObject jsonObject, String originalPacket){
		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonNodeNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNodeNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		return new LinkObj(nodeName, cost);
	}

	public NeighborRequestObj parseRequestNeighbors(JsonObject jsonObject){

		//JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonNodeNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNodeNameElement.getAsString();

		return new NeighborRequestObj(nodeName);
	}

}
