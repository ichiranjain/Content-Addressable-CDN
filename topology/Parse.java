package topology;
import java.lang.reflect.Type;
import java.util.ArrayList;

import packetObjects.AddNodeObj;
import packetObjects.DataObj;
import packetObjects.HelloObj;
import packetObjects.IntrestObj;
import packetObjects.LinkObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;
import packetObjects.RemoveNodeObj;
import packetObjects.TableObj;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


public class Parse {

	Gson gson = new Gson();

	public Parse(){

	}

	public String parseType(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("type");
		String typeInfo = jsonNameElement.getAsString();

		return typeInfo;
	}

	public String parseMsgID(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("msgID");
		String msgIDInfo = jsonNameElement.getAsString();

		return msgIDInfo;
	}

	public String parseAction(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("action");
		String actionInfo = jsonNameElement.getAsString();

		return actionInfo;
	}


	public AddNodeObj parseAddNodeJson(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNameElement.getAsString();


		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();


		JsonElement jsonNeighborsElement = jsonObject.get("neighbors");
		String neighborsString = jsonNeighborsElement.getAsString();
		Type neighborsType = new TypeToken<ArrayList<NeighborAndCostStrings>>(){}.getType();
		ArrayList<NeighborAndCostStrings> neighborsList = gson.fromJson(neighborsString, neighborsType);


		AddNodeObj addNodeInfo = new AddNodeObj(nodeName, neighborsList, msgID, jsonString);

		return addNodeInfo;
	}

	public ModifyNodeObj parseModifyNodeJson(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNameElement.getAsString();

		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();

		JsonElement jsonNeighborsElement = jsonObject.get("neighbors");
		String neighborsString = jsonNeighborsElement.getAsString();
		Type neighborsType = new TypeToken<ArrayList<NeighborAndCostStrings>>(){}.getType();
		ArrayList<NeighborAndCostStrings> neighborsList = gson.fromJson(neighborsString, neighborsType);


		ModifyNodeObj modifyNodeInfo = new ModifyNodeObj(nodeName, neighborsList, msgID, jsonString);
		return modifyNodeInfo;
	}

	public RemoveNodeObj parseRemoveNodeJson(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("removeNodeName");
		String removeNodeName = jsonNameElement.getAsString();

		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();

		RemoveNodeObj removeNodeInfo = new RemoveNodeObj(removeNodeName, msgID, jsonString);
		return removeNodeInfo;
	}

	public PrefixObj parsePrefixJson(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonContentNameElement = jsonObject.get("contentName");
		String contentName = jsonContentNameElement.getAsString();

		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();

		JsonElement jsonAddRemoveElement = jsonObject.get("flag");
		boolean addRemove = jsonAddRemoveElement.getAsBoolean();

		JsonElement jsonAdvertiserElement = jsonObject.get("advertiser");
		String advertiser = jsonAdvertiserElement.getAsString();

		PrefixObj prefixInfo = new PrefixObj(contentName, msgID, advertiser, addRemove, jsonString);
		return prefixInfo;
	}

	public boolean parsePrefixAddRemove(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonAddRemoveElement = jsonObject.get("flag");
		boolean addRemove = jsonAddRemoveElement.getAsBoolean();
		return addRemove;
	}

	public IntrestObj parseIntrestJson(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonContentNameElement = jsonObject.get("contentName");
		String contentName = jsonContentNameElement.getAsString();

		JsonElement jsonSenderNameElement = jsonObject.get("originRouter");
		String senderName = jsonSenderNameElement.getAsString();

		JsonElement jsonNonceElement = jsonObject.get("nonce");
		int nonce = jsonNonceElement.getAsInt();

		IntrestObj intrestInfo = new IntrestObj(contentName, senderName, nonce, jsonString);
		return intrestInfo;
	}

	public DataObj parseDataJson(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonContentNameElement = jsonObject.get("contentName");
		String contentName = jsonContentNameElement.getAsString();

		JsonElement jsonOriginRouterElement = jsonObject.get("originRouter");
		String originRouter = jsonOriginRouterElement.getAsString();

		JsonElement jsonFlagElement = jsonObject.get("flag");
		byte flag = jsonFlagElement.getAsByte();

		JsonElement jsonDataElement = jsonObject.get("data");
		String data = jsonDataElement.getAsString();

		DataObj dataInfo = new DataObj(contentName, originRouter, flag, data, jsonString);
		return dataInfo; 
	}

	public String parseContentName(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonContentNameElement = jsonObject.get("contentName");
		String contentName = jsonContentNameElement.getAsString();

		return contentName;
	}

	public byte parseDataFlag(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonFlagElement = jsonObject.get("flag");
		byte flag = jsonFlagElement.getAsByte();
		return flag;
	}

	public HelloObj parseHelloJson(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonFromNodeElement = jsonObject.get("fromNode");
		String fromNode = jsonFromNodeElement.getAsString();

		JsonElement jsonRequestTableElement = jsonObject.get("requestTable");
		boolean requestTable = jsonRequestTableElement.getAsBoolean();

		HelloObj helloObj = new HelloObj(fromNode, requestTable);
		return helloObj;
	}

	public boolean parseRequestTable(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonRequestTableElement = jsonObject.get("requestTable");
		boolean requestTable = jsonRequestTableElement.getAsBoolean();
		return requestTable;
	}

	public TableObj parseTableJson(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		//		JsonElement jsonFromNodeElement = jsonObject.get("fromNode");
		//		String fromNode = jsonFromNodeElement.getAsString();

		JsonElement jsonGraphElement = jsonObject.get("graph");
		String graphString = jsonGraphElement.getAsString();
		Type graphType = new TypeToken<ArrayList<Node>>(){}.getType();
		ArrayList<Node> graph = gson.fromJson(graphString, graphType);

		TableObj tableObj = new TableObj(graph);
		return tableObj;

	}

	public PrefixListObj parsePrefixListJson(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonIDElement = jsonObject.get("msgID");
		String msgID = jsonIDElement.getAsString();

		JsonElement jsonAddRemoveElement = jsonObject.get("flag");
		boolean addRemove = jsonAddRemoveElement.getAsBoolean();

		JsonElement jsonAdvertiserElement = jsonObject.get("advertiser");
		String advertiser = jsonAdvertiserElement.getAsString();

		JsonElement JE = jsonObject.get("prefixList");
		Type TYPE = new TypeToken<ArrayList<String>>(){}.getType();
		ArrayList<String> prefixList = new Gson().fromJson(JE.getAsString(), TYPE);

		PrefixListObj prefixListObj = new PrefixListObj(prefixList, advertiser, addRemove, msgID);
		return prefixListObj;
	}

	public LinkObj parseClientAddNodeJson(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		LinkObj linkObjInfo = new LinkObj(nodeName, cost);

		return linkObjInfo;

	}

	public LinkObj parseClientRemoveNodeJson(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
		JsonElement jsonNameElement = jsonObject.get("removeNodeName");
		String removeNodeName = jsonNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		LinkObj linkObjInfo = new LinkObj(removeNodeName, cost);

		return linkObjInfo;
	}

	public LinkObj parseAddLink(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonNodeNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNodeNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		return new LinkObj(nodeName, cost);
	}

	public LinkObj parseRemoveLink(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonNodeNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNodeNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		return new LinkObj(nodeName, cost);
	}

	public LinkObj parseModifyLink(String jsonString){
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonNodeNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNodeNameElement.getAsString();

		JsonElement jsonCostElement = jsonObject.get("cost");
		int cost = jsonCostElement.getAsInt();

		return new LinkObj(nodeName, cost);
	}

	public NeighborRequestObj parseRequestNeighbors(String jsonString){

		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		JsonElement jsonNodeNameElement = jsonObject.get("nodeName");
		String nodeName = jsonNodeNameElement.getAsString();

		return new NeighborRequestObj(nodeName);
	}

}
