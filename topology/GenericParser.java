package topology;

import packetObjects.DataObj;
import packetObjects.GenericPacketObj;
import packetObjects.IntrestObj;
import packetObjects.LinkObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PacketObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GenericParser {

	Gson gson = new Gson();
	Parse2 parse = new Parse2();
	PacketQueue2 packetQueue2;

	public GenericParser(PacketQueue2 packetQueue2) {
		this.packetQueue2 = packetQueue2;
		// TODO Auto-generated constructor stub
		//Implement runnable ... because this will be run in a thread 
		// in the general queue thread pool 
	}

	public void parsePacket(PacketObj packetObj){

		JsonObject jsonObject = gson.fromJson(packetObj.getPacket(), JsonObject.class);
		JsonElement jsonTypeElement = jsonObject.get("type");
		String type = jsonTypeElement.getAsString();

		//System.out.println("Inside parsePacket::type::" + type);

		switch (type){

		case "update" :
			//System.out.println("parsing update packet");
			parseUpdatePacket(jsonObject, packetObj);
			break;
		case "route" :
			parseRoutePacket(jsonObject, packetObj);
			break;

		default :
			System.out.println("Invalid packet type");
			break;

		}

	}

	public void parseUpdatePacket(JsonObject jsonObject, PacketObj packetObj){

		JsonElement jsonActionElement = jsonObject.get("action");
		String action = jsonActionElement.getAsString();
		System.out.println("Update action: " + action);
		LinkObj linkObj;
		PrefixListObj prefixListObj;
		PrefixObj prefixObj;
		NeighborRequestObj neighborRequestObj;
		//GenericPacketObj genericPacketObj;
		ModifyNodeObj modifyNodeObj;

		System.out.println("Parsing update packet now::action::" + action);

		switch(action){

		case "addLink" :

			//System.out.println("parsing addlink");
			//parse the packet into a addLinkObj
			linkObj = parse.parseAddLink(jsonObject);
			//create the genericPacketObj
			GenericPacketObj<LinkObj> gpoAddLink = new GenericPacketObj<LinkObj>(action, packetObj.getRecievedFromNode(), linkObj);
			//add it to the Update Queue

			packetQueue2.addToUpdateQueue(gpoAddLink);
			//System.out.println("update added to update queue");

			break;

		case "removeLink" :
			linkObj = parse.parseRemoveLink(jsonObject);
			GenericPacketObj<LinkObj> gpoRemoveLink = new GenericPacketObj<LinkObj>(action, packetObj.getRecievedFromNode(), linkObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoRemoveLink);
			break;

		case "modifyLink" :
			linkObj = parse.parseModifyLink(jsonObject, packetObj.getPacket());
			GenericPacketObj<LinkObj> gpoModifyLink = new GenericPacketObj<LinkObj>(action, packetObj.getRecievedFromNode(), linkObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoModifyLink);
			break;

		case "modify" : 
			modifyNodeObj = parse.parseModifyNodeJson(jsonObject, packetObj.getPacket());
			GenericPacketObj<ModifyNodeObj> gpoModifyNodeObj = new GenericPacketObj<ModifyNodeObj>(action, packetObj.getRecievedFromNode(), modifyNodeObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoModifyNodeObj);
			break;

		case "prefix" :

			prefixObj = parse.parsePrefixJson(jsonObject, packetObj.getPacket());
			GenericPacketObj<PrefixObj> gpoPrefixObj = new GenericPacketObj<PrefixObj>(action, packetObj.getRecievedFromNode(), prefixObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoPrefixObj);
			break;

		case "prefixList" : 

			prefixListObj = parse.parsePrefixListJson(jsonObject);
			GenericPacketObj<PrefixListObj> gpoPrefixListObj = new GenericPacketObj<PrefixListObj>(action, packetObj.getRecievedFromNode(), prefixListObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoPrefixListObj);
			break;

		case "addClient" : 
			linkObj = parse.parseClientAddNodeJson(jsonObject);
			GenericPacketObj<LinkObj> gpoAddClient = new GenericPacketObj<LinkObj>(action, packetObj.getRecievedFromNode(), linkObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoAddClient);
			break;

		case "removeClient" : 
			linkObj = parse.parseClientRemoveNodeJson(jsonObject);
			GenericPacketObj<LinkObj> gpoRemoveClient = new GenericPacketObj<LinkObj>(action, packetObj.getRecievedFromNode(), linkObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoRemoveClient);
			break;

		case "clientPrefix" : 
			prefixObj = parse.parsePrefixJson(jsonObject, packetObj.getPacket());
			GenericPacketObj<PrefixObj> gpoClientPrefix = new GenericPacketObj<PrefixObj>(action, packetObj.getRecievedFromNode(), prefixObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoClientPrefix);
			break;

		case "clientPrefixList" : 
			prefixListObj = parse.parsePrefixListJson(jsonObject);
			GenericPacketObj<PrefixListObj> gpoClientPrefixList= new GenericPacketObj<PrefixListObj>(action, packetObj.getRecievedFromNode(), prefixListObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoClientPrefixList);
			break;

		case "neighborRequest" :
			neighborRequestObj = parse.parseRequestNeighbors(jsonObject);
			GenericPacketObj<NeighborRequestObj> gpoNeighborRequestObj = new GenericPacketObj<NeighborRequestObj>(action, packetObj.getRecievedFromNode(), neighborRequestObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoNeighborRequestObj);
			break;

		case "prefixResponse" :
			prefixListObj = parse.parsePrefixListJson(jsonObject);
			GenericPacketObj<PrefixListObj> gpoPrefixResponse = new GenericPacketObj<PrefixListObj>(action, packetObj.getRecievedFromNode(), prefixListObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoPrefixResponse);
			break;

		case "neighborResponse" :
			modifyNodeObj = parse.parseModifyNodeJson(jsonObject, packetObj.getPacket());
			GenericPacketObj<ModifyNodeObj> gpoNeighborResponse = new GenericPacketObj<ModifyNodeObj>(action, packetObj.getRecievedFromNode(), modifyNodeObj);
			//add it to the Update Queue
			packetQueue2.addToUpdateQueue(gpoNeighborResponse);
			break;

		default :
			System.out.println("Invalid update packet action");
			break;

		}
	}

	public void parseRoutePacket(JsonObject jsonObject, PacketObj packetObj){
		JsonElement jsonTypeElement = jsonObject.get("action");
		String action = jsonTypeElement.getAsString();
		System.out.println("Routing action::" + action);
		//GenericPacketObj genericPacketObj;
		switch(action){

		case "intrest" :
			IntrestObj intrestObj = parse.parseIntrestJson(jsonObject, packetObj.getPacket());
			GenericPacketObj<IntrestObj> gpoIntrest = new GenericPacketObj<>(action, packetObj.getRecievedFromNode(), intrestObj);
			//add it to the Update Queue
			packetQueue2.addToRoutingQueue(gpoIntrest);
			break;

		case "data" :
			DataObj dataObj = parse.parseDataJson(jsonObject, packetObj.getPacket());
			GenericPacketObj<DataObj> gpoData= new GenericPacketObj<DataObj>(action, packetObj.getRecievedFromNode(), dataObj);
			//add it to the Update Queue
			packetQueue2.addToRoutingQueue(gpoData);
			break;

		default :
			System.out.println("Invalid route packet action");
			break;

		}


	}
}

