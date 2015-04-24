package topology;

import java.lang.reflect.Type;
import java.util.ArrayList;

import packetObjects.DataObj;
import packetObjects.GenericPacketObj;
import packetObjects.IntrestObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PrefixListObj;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


public class RoutingSwitch implements Runnable{

	//String packet;
	@SuppressWarnings("rawtypes")
	GenericPacketObj genericPacketObj;
	FIB fib;
	ProcessRoutingPackets process;
	PIT pit;
	//Parse parse;
	NodeRepository nodeRepo;
	DirectlyConnectedNodes directlyConnectedNodes;
	PacketQueue2 packetQueue2;
	String recievedFromNode;


	@SuppressWarnings("rawtypes")
	public RoutingSwitch(GenericPacketObj genericPacketObj,
			FIB fib,
			PIT pit,
			DirectlyConnectedNodes directlyConnectedNodes,
			NodeRepository nodeRepo,
			PacketQueue2 packetQueue2){

		//this.packet = packet;
		this.genericPacketObj = genericPacketObj;
		this.fib = fib;
		this.pit = pit;
		this.directlyConnectedNodes = directlyConnectedNodes;
		this.nodeRepo = nodeRepo;
		this.packetQueue2 = packetQueue2;
		this.recievedFromNode = genericPacketObj.getRecievedFromNode();

		//parse = new Parse();
		process = new ProcessRoutingPackets(nodeRepo, fib, pit, directlyConnectedNodes, recievedFromNode);
	}


	@Override
	public void run() {
		String action = genericPacketObj.getAction();


		switch(action){
		case "intrest" :
			IntrestObj intrestObj = (IntrestObj) genericPacketObj.getObj();
			if(intrestObj.getContentName().equals(nodeRepo.getThisMachinesName()) == false){
				process.processIntrest(intrestObj);
			}else{
				SendPacket sendPacket = new SendPacket();
				//the packet is for this node
				//parse the neighbor request 
				NeighborRequestObj neighborRequestObj = new NeighborRequestObj(genericPacketObj.getRecievedFromNode());

				//create the packet
				sendPacket.createNeighborRequestPacket(neighborRequestObj);

				//add to the update queue
				GenericPacketObj<NeighborRequestObj> genericPacketObjUpdate = 
						new GenericPacketObj<NeighborRequestObj>("neighborRequest", 
								genericPacketObj.getRecievedFromNode(), 
								neighborRequestObj);

				packetQueue2.addToUpdateQueue(genericPacketObjUpdate);
			}
			break;

		case "data" :
			DataObj dataObj = (DataObj) genericPacketObj.getObj();
			if(dataObj.getContentName().equals(nodeRepo.getThisMachinesName()) == false){
				switch(dataObj.getFlag()){
				case 0 :
					process.processData0(dataObj);
					break;

				case 1 :
					process.processData1(dataObj);
					break;

				case 2 :
					process.processData2(dataObj);
					break;

				default : 
					System.out.println("data flag set to an incorrect value");
					break;
				}
			}else{
				//the packet is for this node
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(dataObj.getData(), JsonObject.class);
				JsonElement jsonActionElement = jsonObject.get("action");
				String nestedAction = jsonActionElement.getAsString();
				SendPacket sendPacket = new SendPacket();

				if(nestedAction.equals("prefixResponse") == true){
					//call prefix function
					JsonElement jsonIDElement = jsonObject.get("msgID");
					String msgID = jsonIDElement.getAsString();


					JsonElement jsonAdvertiserElement = jsonObject.get("advertiser");
					String advertiser = jsonAdvertiserElement.getAsString();

					JsonElement JE = jsonObject.get("prefixList");
					Type TYPE = new TypeToken<ArrayList<String>>(){}.getType();
					ArrayList<String> prefixList = new Gson().fromJson(JE.getAsString(), TYPE);

					PrefixListObj prefixListObj = new PrefixListObj(prefixList, advertiser, true, msgID);


					//create the update packet
					sendPacket.createPrefixResponsePacket(prefixListObj);

					//add to update queue
					//packetObj = new PacketObj(prefixListObj.getOriginalPacket(), nodeRepo.getThisMachinesName(), false);
					GenericPacketObj<PrefixListObj> genericPacketObjPrefix = 
							new GenericPacketObj<PrefixListObj>("prefixResponse", 
									genericPacketObj.getRecievedFromNode(), 
									prefixListObj);

					packetQueue2.addToUpdateQueue(genericPacketObjPrefix);
				}else{
					//call neighbors function
					//ModifyNodeObj modifyNodeObj = parse.parseModifyNodeJson(dataObj.getData());

					JsonElement jsonNameElement = jsonObject.get("nodeName");
					String nodeName = jsonNameElement.getAsString();

					JsonElement jsonIDElement = jsonObject.get("msgID");
					String msgID = jsonIDElement.getAsString();

					JsonElement jsonNeighborsElement = jsonObject.get("neighbors");
					String neighborsString = jsonNeighborsElement.getAsString();
					Type neighborsType = new TypeToken<ArrayList<NeighborAndCostStrings>>(){}.getType();
					ArrayList<NeighborAndCostStrings> neighborsList = gson.fromJson(neighborsString, neighborsType);


					ModifyNodeObj modifyNodeObj = new ModifyNodeObj(nodeName, neighborsList, msgID);
					//create the update packet
					sendPacket.createNeighborResponsePacket(modifyNodeObj);

					//add to update queue
					//packetObj = new PacketObj(modifyNodeObj.getOriginalPacket(), nodeRepo.getThisMachinesName(), false);
					GenericPacketObj<ModifyNodeObj> genericPacketObjNeighbors = 
							new GenericPacketObj<ModifyNodeObj>("neighborResponse", 
									genericPacketObj.getRecievedFromNode(), modifyNodeObj);
					packetQueue2.addToUpdateQueue(genericPacketObjNeighbors);

				}
				break;
			}
			break;
		default : 
			System.out.println("Invalid route action");
			break;
		}
	}
}
