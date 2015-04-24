package topology;

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

public class ProcessUpdates {

	NodeRepository nodeRepo;
	UpdateMsgsSeen upDatesSeen;
	SendPacket sendPacket;
	FIB fib;
	DirectlyConnectedNodes directlyConnectedNodes;
	Dijkstras dijkstras;

	public ProcessUpdates(NodeRepository nodeRepo, UpdateMsgsSeen upDatesSeen, FIB fib, DirectlyConnectedNodes directlyConnectedNodes){
		this.nodeRepo = nodeRepo;
		this.upDatesSeen = upDatesSeen;
		this.fib = fib;
		this.directlyConnectedNodes = directlyConnectedNodes;
		this.sendPacket = new SendPacket();
		this.dijkstras = new Dijkstras();
	}

	public void addLink(LinkObj linkObj){
		//if the node does not exist .. add it to the graph 
		if(nodeRepo.HMdoesNodeExist(linkObj.getNeighboringNode()) == false){

			//add the node to the graph
			addNode(new AddNodeObj(linkObj.getNeighboringNode()));

			//add new node name to prefix hash map
			fib.addPrefixToFIB(linkObj.getNeighboringNode(), linkObj.getNeighboringNode());

			//add the new node as a neighbor, the add neighbor does nothing if it already exists
			nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).addNeighbor(linkObj.getNeighboringNode(), linkObj.getCost());

			//add the link to my directly connected list, if it already exists the method does nothing
			directlyConnectedNodes.addDirectlyConnectedRouter(linkObj.getNeighboringNode());

			//run Dijkstra
			dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

			//sort FIB entries
			fib.findBestCostAdvertisers();

			//send the packet asking for its neighbors
			requestNeighbors(linkObj.getNeighboringNode());
		}else{

			//the node exists already, just add as a neighbor, if the neighbor already exists, the method will do nothing
			nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).addNeighbor(linkObj.getNeighboringNode(), linkObj.getCost());

			//add the link to my directly connected list, if it already exists the method does nothing
			directlyConnectedNodes.addDirectlyConnectedRouter(linkObj.getNeighboringNode());

			//run Dijkstra
			dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

			//sort FIB entries
			fib.findBestCostAdvertisers();
		}

		//send modify update to the rest of the graph ... telling them about the new connection
		ModifyNodeObj modifyNodeObj = new ModifyNodeObj(nodeRepo.getThisMachinesName(), 
				nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).getNeighbors(), 
				(nodeRepo.getThisMachinesName() + System.nanoTime()) );
		sendPacket.createModifyNodePacket(modifyNodeObj);

		//forward to all routers
		sendPacket.forwardToAllRouters(modifyNodeObj.getOriginalPacket());
		//sendPacket.forwardToAllRouters(modifyNodeObj.getOriginalPacket(), directlyConnectedNodes.getDirectlyConnectedRoutersList());

	}

	public void removeLink(LinkObj linkObj){
		if(nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).doesNeighborExist(linkObj.getNeighboringNode()) == true){
			nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).removeNeighbor(linkObj.getNeighboringNode());
		}

		if(directlyConnectedNodes.doesDirectlyConnectedRouterExist(linkObj.getNeighboringNode()) == true){
			directlyConnectedNodes.removeDirectlyConnectedRouter(linkObj.getNeighboringNode());
		}

		//run Dijkstra
		dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

		//sort FIB entries
		fib.findBestCostAdvertisers();

		//send modify update to the rest of the graph ... telling them about the new connection
		ModifyNodeObj modifyNodeObj = new ModifyNodeObj(nodeRepo.getThisMachinesName(), 
				nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).getNeighbors(), 
				(nodeRepo.getThisMachinesName() + System.nanoTime()) );
		sendPacket.createModifyNodePacket(modifyNodeObj);

		//forward to all routers
		sendPacket.forwardToAllRouters(modifyNodeObj.getOriginalPacket());
		//sendPacket.forwardToAllRouters(modifyNodeObj.getOriginalPacket(), directlyConnectedNodes.getDirectlyConnectedRoutersList());
	}

	public void addClientLink(LinkObj linkObj){
		if(directlyConnectedNodes.doesDirectlyConnectedClientExist(linkObj.getNeighboringNode()) == false){

			//this will add the clients name to the clients prefix list
			directlyConnectedNodes.addDirectlyConnectedClient(linkObj.getNeighboringNode());

			//add its name as a prefix to the FIB
			fib.addPrefixToFIB(linkObj.getNeighboringNode(), linkObj.getNeighboringNode());

			//add the clients name to his list of prefixes he servers
			directlyConnectedNodes.getDirectlyConnectedClient(linkObj.getNeighboringNode()).addPrefix(linkObj.getNeighboringNode());

			//send update add these prefixes
			PrefixObj prefixObj = new PrefixObj(linkObj.getNeighboringNode(), 
					(nodeRepo.getThisMachinesName() + System.nanoTime()), 
					nodeRepo.getThisMachinesName(), true);
			sendPacket.createPrefixPacket(prefixObj);

			//sendPacket
			sendPacket.forwardToAllRouters(prefixObj.getOriginalPacket());
			//sendPacket.forwardToAllRouters(prefixObj.getOriginalPacket(), directlyConnectedNodes.getDirectlyConnectedRoutersList());
		}
	}

	public void removeClientLink(LinkObj linkObj){

		if(directlyConnectedNodes.doesDirectlyConnectedClientExist(linkObj.getNeighboringNode()) == true){

			//get the prefixes associated with the client
			ArrayList<String> prefixList = directlyConnectedNodes.getDirectlyConnectedClient(linkObj.getNeighboringNode()).getPrefixArrayList();

			//remove the prefixes (this could be multiple prefixes) from the FIB
			for(int i = 0; i < prefixList.size(); i++){
				fib.removePrefixFromFIB(prefixList.get(i), linkObj.getNeighboringNode());
			}

			directlyConnectedNodes.removeDirectlyConnectedClient(linkObj.getNeighboringNode());

			//send update add these prefixes
			PrefixListObj prefixListObj = new PrefixListObj(prefixList, 
					nodeRepo.getThisMachinesName(), 
					false,
					(nodeRepo.getThisMachinesName() + System.nanoTime()) );

			sendPacket.createPrefixListPacket(prefixListObj);

			//sendPacket
			sendPacket.forwardToAllRouters(prefixListObj.getOriginalPacket());
			//sendPacket.forwardToAllRouters(prefixListObj.getOriginalPacket(), directlyConnectedNodes.getDirectlyConnectedRoutersList());
		}
	}

	public void modifyLink(LinkObj linkObj){

		//does the neighbor exist
		int index = nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).getNeighborIndex(linkObj.getNeighboringNode());

		//if the index is -1 then the neighbor does not exist
		if(index != -1){

			//set the new cost 
			nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).getNeighbor(index).setCost(linkObj.getCost());

			//run Dijkstra
			dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

			//sort FIB entries
			fib.findBestCostAdvertisers();

			//send modify update to the rest of the graph ... telling them about the link change
			ModifyNodeObj modifyNodeObj = new ModifyNodeObj(nodeRepo.getThisMachinesName(), 
					nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).getNeighbors(), 
					(nodeRepo.getThisMachinesName() + System.nanoTime()));

			sendPacket.createModifyNodePacket(modifyNodeObj);

			//sendPacket
			sendPacket.forwardToAllRouters(modifyNodeObj.getOriginalPacket());
			//sendPacket.forwardToAllRouters(modifyNodeObj.getOriginalPacket(), directlyConnectedNodes.getDirectlyConnectedRoutersList());

		}//if the neighbor does no exist... this packet was sent by mistake



	}

	public void addCLientPrefix(PrefixObj prefixObj, String doNotSendToNode){

		//try to add the prefix, if the prefix and advertiser already exist, it will return false, else it will be added
		if(fib.addPrefixToFIB(prefixObj.getPrefixName(), prefixObj.getAdvertiser()) == true){

			//add the prefix to the clients list of prefixes 
			directlyConnectedNodes.getDirectlyConnectedClient(prefixObj.getAdvertiser()).addPrefix(prefixObj.getPrefixName());
		}

		//forward the prefix update using this router name, because it was a client prefix 
		prefixObj.setAdvertiser(nodeRepo.getThisMachinesName());

		//send the packet update to the rest of the graph 
		sendPacket.createPrefixPacket(prefixObj);
		sendPacket.forwardUpdate(prefixObj.getOriginalPacket(), doNotSendToNode);
	}

	public void addClientPrefixList(PrefixListObj prefixListObj, String doNotSendToNode){

		ArrayList<String> prefixList = prefixListObj.getPrefixList();

		//for each prefix in the list try to add it 
		for(int i = 0; i < prefixListObj.getPrefixListLength(); i++){

			//try to add the prefix, if the prefix and advertiser already exist, it will return false, else it will be added
			if(fib.addPrefixToFIB(prefixList.get(i), prefixListObj.getAdvertiser()) == true){

				//add the prefix to the clients list of prefixes 
				directlyConnectedNodes.getDirectlyConnectedClient(prefixListObj.getAdvertiser()).addPrefix(prefixList.get(i));
			}

		}//end for loop	

		//forward the prefixList update using this router name, because it was a client prefix 
		prefixListObj.setAdvertiser(nodeRepo.getThisMachinesName());

		//send the packet update to the rest of the graph 
		sendPacket.createPrefixListPacket(prefixListObj);
		sendPacket.forwardUpdate(prefixListObj.getOriginalPacket(), doNotSendToNode);
	}

	public void removeClientPrefix(PrefixObj prefixObj, String doNotSendToNode){

		//remove the prefix from the fib, this will return false if the prefix or advertiser does not exist
		if(fib.removePrefixFromFIB(prefixObj.getPrefixName(), prefixObj.getAdvertiser()) == true){

			//remove the prefix from the client list
			directlyConnectedNodes.getDirectlyConnectedClient(prefixObj.getAdvertiser()).removePrefix(prefixObj.getPrefixName());
		}

		//forward the prefix update
		prefixObj.setAdvertiser(nodeRepo.getThisMachinesName());

		//send the packet update to the rest of the graph 
		sendPacket.createPrefixPacket(prefixObj);
		sendPacket.forwardUpdate(prefixObj.getOriginalPacket(), doNotSendToNode);

	}

	public void removeClientPrefixList(PrefixListObj prefixListObj, String doNotSendToNode){

		ArrayList<String> prefixList = prefixListObj.getPrefixList();

		//for each prefix in the list try to remove it 
		for(int i = 0; i < prefixListObj.getPrefixListLength(); i++){

			//remove the prefix from the fib
			if(fib.removePrefixFromFIB(prefixList.get(i), prefixListObj.getAdvertiser())){

				//remove the prefix from the client list
				directlyConnectedNodes.getDirectlyConnectedClient(prefixListObj.getAdvertiser()).removePrefix(prefixList.get(i));
			}

		}//end for loop

		//forward the prefix update
		prefixListObj.setAdvertiser(nodeRepo.getThisMachinesName());

		//send the packet update to the rest of the graph 
		sendPacket.createPrefixListPacket(prefixListObj);
		sendPacket.forwardUpdate(prefixListObj.getOriginalPacket(), doNotSendToNode);

	}

	public void addNode(AddNodeObj addNodeObj){

		if(nodeRepo.HMdoesNodeExist(addNodeObj.getName()) == false){			

			//TODO add the new node, should I provide a cost to the node as well
			nodeRepo.HMaddNode(addNodeObj.getName());

		}
	}

	public void removeNode(RemoveNodeObj removeNodeObj){

		if(nodeRepo.HMdoesNodeExist(removeNodeObj.getName()) == true){

			//remove the node
			nodeRepo.HMremoveNode(removeNodeObj.getName());

			//run Dijkstra
			dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

			//sort FIB entries
			fib.findBestCostAdvertisers();

			//TODO pass the name of the removed node to the thread removing prefixes. associated with this node
		}
	}

	public void modifyNode(ModifyNodeObj modifyNodeObj, String doNotSendToNode){

		//check the previous seen update msgs IDs to make sure it is a new update
		//if it was seen ... drop packet
		if(upDatesSeen.doesMsgIDExist(modifyNodeObj.getMsgID()) == false){
			upDatesSeen.addMsgID(modifyNodeObj.getMsgID(), System.nanoTime());

			if(nodeRepo.HMdoesNodeExist(modifyNodeObj.getName()) == true){

				ArrayList<String> neighbors = modifyNodeObj.getNeighborsNames();
				for(int i = 0; i < neighbors.size(); i++){

					//check if the node exist for the neighbor
					if(nodeRepo.HMdoesNodeExist(neighbors.get(i)) == false){

						//if the neighboring node is not in the graph
						//add the neighbor to the graph
						addNode(new AddNodeObj(neighbors.get(i)));

						//request the newly added nodes neighbors
						requestNeighbors(neighbors.get(i));
					}
				}

				//update the neighbors list for the given node
				nodeRepo.HMsetNeighborList(modifyNodeObj.getName(), modifyNodeObj.getNeighbors());

				//run Dijkstra
				dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

				//sort FIB entries
				fib.findBestCostAdvertisers();

			}
			//send updates or forward updates
			sendPacket.createModifyNodePacket(modifyNodeObj);
			sendPacket.forwardUpdate(modifyNodeObj.getOriginalPacket(), doNotSendToNode);
		}

	}

	public void requestNeighbors(String sendToNode){
		//TODO
		//send request for neighbors 
		IntrestObj intrestObj = new IntrestObj(sendToNode, nodeRepo.getThisMachinesName(), 0);
		sendPacket.createRequestNeighborsIntrestPacket(intrestObj);

		//		//find a next hop to send the packet to 
		//		String[] prefixSplit = intrestObj.getContentName().split("/");
		//		String prefix = prefixSplit[0];
		//		ArrayList<Integer> hashMapsToSearch = new ArrayList<Integer>();
		//
		//		//look up in the fib bloom filter, cant have a prefix of zero
		//		//this can be searched in parallel
		//		for(int i = 1; i < prefixSplit.length; i++){
		//
		//			if(fib.doesPrefixLengthBloomFilterExist(i) == true){				
		//				if(fib.doesBloomFilterConteinPrefix(i, prefix) == true){
		//					hashMapsToSearch.add(i);
		//				}
		//			}
		//			prefixSplit[i] = prefix = prefix + "/" + prefixSplit[i];
		//		}
		//
		//		//search the hash maps returned
		//		//ArrayList<String> bestMatch = new ArrayList<String>();
		//		String bestCostNode = "";
		//		String nextHop = "";
		//
		//		//search through the longest matching prefix hash map first
		//		for(int i = hashMapsToSearch.size(); i > 0; i--){
		//
		//			//does the hash map for "x" length exist
		//			if(fib.doesPrefixLengthHashMapExist(hashMapsToSearch.get(i)) == true){
		//
		//				//does the prefix in this hash map exist
		//				if(fib.doesHashMapContainPrefix(hashMapsToSearch.get(i), prefixSplit[hashMapsToSearch.get(i)]) == true){
		//
		//					//** if there are multiple advertisers 
		//					//** if the packet can't get to one advertiser... do not use the other advertiser
		//					//** this is just kept if the advertiser dies.. not used in routing?
		//
		//					//try the next advertiser in the list 
		//					bestCostNode = fib.getBestCostAdvertiser(hashMapsToSearch.get(i), prefixSplit[hashMapsToSearch.get(i)]);
		//
		//					//if best cost == error the node does not exist 
		//					if(bestCostNode.equals("error") == false){
		//
		//						// there was no error
		//						nextHop = nodeRepo.HMgetNode(bestCostNode).getOriginNextHop();
		//						break;
		//					}
		//				}//if the prefix is not in the hash map ... it might have been a false positive
		//			}//the hash map did not exist
		//		}//end for loop	

		String nextHop = fib.searchFIB(intrestObj.getContentName());

		if(nextHop.equals("broadcast") == false){
			//a route exists
			//set the sender name to this router

			//fix parse ... add check for blank sender name
			intrestObj.setOriginRouterName(nodeRepo.getThisMachinesName());

			//modified the packet then forward it 
			sendPacket.createIntrestPacket(intrestObj);
			sendPacket.forwardPacket(intrestObj.getOriginalPacket(), nextHop);

		}
	}

	public void processNeighborsResponse(ModifyNodeObj modifyNodeObj){

		//TODO altered modify node function
		//if(upDatesSeen.doesMsgIDExist(modifyNodeObj.getMsgID()) == false){
		//	upDatesSeen.addMsgID(modifyNodeObj.getMsgID(), System.nanoTime());

		if(nodeRepo.HMdoesNodeExist(modifyNodeObj.getName()) == true){

			ArrayList<String> neighbors = modifyNodeObj.getNeighborsNames();
			for(int i = 0; i < neighbors.size(); i++){

				//check if the node exist for the neighbor
				if(nodeRepo.HMdoesNodeExist(neighbors.get(i)) == false){

					//if the neighboring node is not in the graph
					//add the neighbor to the graph
					addNode(new AddNodeObj(neighbors.get(i)));

					//request the newly added nodes neighbors
					requestNeighbors(neighbors.get(i));
				}
			}

			//update the neighbors list for the given node
			nodeRepo.HMsetNeighborList(modifyNodeObj.getName(), modifyNodeObj.getNeighbors());

			//run Dijkstra
			dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

			//sort FIB entries
			fib.findBestCostAdvertisers();

		}
		//}
	}

	public void processPrefixListResponse(PrefixListObj prefixListObj){

		//TODO altered prefix list function
		//if(upDatesSeen.doesMsgIDExist(prefixListObj.getMsgID()) == false){

		//add the msgID to the update list 
		upDatesSeen.addMsgID(prefixListObj.getMsgID(), System.nanoTime());

		ArrayList<String> prefixList = prefixListObj.getPrefixList();

		for(int i = 0; i < prefixListObj.getPrefixListLength(); i++){
			fib.addPrefixToFIB(prefixList.get(i), prefixListObj.getAdvertiser());
		}
		//}
	}

	public void processIntrestRequestForNeighbors(NeighborRequestObj neighborRequestObj){
		//get the neighbors 
		ModifyNodeObj modifyNodeObj = getMyNeighbors();
		//get the prefixes 
		PrefixListObj prefixListObj = getMyDirectlyConnectedPrefixes();

		byte b = 0;
		DataObj dataObj = new DataObj(neighborRequestObj.getFromName(), 
				neighborRequestObj.getFromName(), 
				b, 
				modifyNodeObj.getOriginalPacket(), 
				"",
				b,
				true);

		sendPacket.createDataPacket(dataObj);
		//send out 1 neighbors data packet
		sendPacket.forwardPacket(dataObj.getOriginalPacket(), neighborRequestObj.getFromName());


		dataObj.setData(prefixListObj.getOriginalPacket());
		sendPacket.createDataPacket(dataObj);
		//send out 1 prefix data packet 
		sendPacket.forwardPacket(dataObj.getOriginalPacket(), neighborRequestObj.getFromName());
	}

	public void addPrefix(PrefixObj prefixObj, String doNotSendToNode){

		//if the prefix update has been seen already, do nothing 
		if(upDatesSeen.doesMsgIDExist(prefixObj.getMsgID()) == false){

			//add the msgID to the update list 
			upDatesSeen.addMsgID(prefixObj.getMsgID(), System.nanoTime());

			//try to add the prefix to the fib 
			fib.addPrefixToFIB(prefixObj.getPrefixName(), prefixObj.getAdvertiser());

			//forward the pack to the rest of the graph
			sendPacket.createPrefixPacket(prefixObj);
			sendPacket.forwardUpdate(prefixObj.getOriginalPacket(), doNotSendToNode);
		}

	}

	public void addPrefixList(PrefixListObj prefixListObj, String doNotSendToNode){

		//if the prefix update has been seen already, do nothing 
		if(upDatesSeen.doesMsgIDExist(prefixListObj.getMsgID()) == false){

			//add the msgID to the update list 
			upDatesSeen.addMsgID(prefixListObj.getMsgID(), System.nanoTime());

			ArrayList<String> prefixList = prefixListObj.getPrefixList();

			for(int i = 0; i < prefixListObj.getPrefixListLength(); i++){
				fib.addPrefixToFIB(prefixList.get(i), prefixListObj.getAdvertiser());
			}

			//forward the prefix update
			sendPacket.createPrefixListPacket(prefixListObj);
			sendPacket.forwardUpdate(prefixListObj.getOriginalPacket(), doNotSendToNode);
		}
	}

	public void removePrefix(PrefixObj prefixObj, String doNotSendToNode){
		//if the entry exists remove it 

		if(upDatesSeen.doesMsgIDExist(prefixObj.getMsgID()) == false){

			//add the msgID to the update list
			upDatesSeen.addMsgID(prefixObj.getMsgID(), System.nanoTime());

			//try to remove the prefix for the fib
			fib.removePrefixFromFIB(prefixObj.getPrefixName(), prefixObj.getAdvertiser());

			//forward the update to the rest of the graph
			sendPacket.createPrefixPacket(prefixObj);
			sendPacket.forwardUpdate(prefixObj.getOriginalPacket(), doNotSendToNode);

		}

	}

	public void removePrefixList(PrefixListObj prefixListObj, String doNotSendToNode){

		if(upDatesSeen.doesMsgIDExist(prefixListObj.getMsgID()) == false){

			//add the msgID to the update list 
			upDatesSeen.addMsgID(prefixListObj.getMsgID(), System.nanoTime());

			ArrayList<String> prefixList = prefixListObj.getPrefixList();
			for(int i = 0; i < prefixListObj.getPrefixListLength(); i++){
				fib.removePrefixFromFIB(prefixList.get(i), prefixListObj.getAdvertiser());
			}

			//forward the update to the rest of the graph
			sendPacket.createPrefixListPacket(prefixListObj);
			sendPacket.forwardUpdate(prefixListObj.getOriginalPacket(), doNotSendToNode);

		}

	}

	//	public void sendHelloHeartBeat(String sendToNode){	
	//
	//		HelloObj helloObj = new HelloObj(nodeRepo.getThisMachinesName(), false);
	//		sendPacket.sendHelloPacket(helloObj);
	//		sendPacket.forwardPacket(helloObj.getOriginalPacket(), sendToNode);
	//	}
	//
	//
	//	public void preocessHelloHeartBeatResponse(HelloObj helloObj){
	//
	//		//does not send anything out ... just resets the still alive timer... 
	//		sendPacket.sendHeartBeatReponsePacket(helloObj);
	//		sendPacket.forwardPacket(helloObj.getOriginalPacket(), helloObj.getFromName());
	//	}

	//	public ArrayList<String> getNeighbors(){
	//		String[] neighboringRouters = directlyConnectedNodes.getDirectlyConnectedRoutersList();
	//		ArrayList<String> neighbors = new ArrayList<String>();
	//		for(int i = 0; i < neighboringRouters.length; i++){
	//			neighbors.add(neighboringRouters[i]);
	//		}
	//		return neighbors;
	//	}

	public ModifyNodeObj getMyNeighbors(){
		ModifyNodeObj modifyNodeObj = new ModifyNodeObj(nodeRepo.getThisMachinesName(),
				nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).getNeighbors(), 
				(nodeRepo.getThisMachinesName() + System.nanoTime()));
		sendPacket.createNeighborResponsePacket(modifyNodeObj);
		return modifyNodeObj;
	}

	public PrefixListObj getMyDirectlyConnectedPrefixes(){
		ArrayList<String> prefixList = new ArrayList<String>();
		String[] clientNeighbors = directlyConnectedNodes.getDirectlyConnectedClientsList();
		for(int i = 0; i < clientNeighbors.length; i++){
			prefixList.addAll(directlyConnectedNodes.getDirectlyConnectedClient(clientNeighbors[i]).getPrefixArrayList());
		}
		PrefixListObj prefixListObj = new PrefixListObj(prefixList, nodeRepo.getThisMachinesName(), true, (nodeRepo.getThisMachinesName() + System.nanoTime()));
		sendPacket.createPrefixResponsePacket(prefixListObj);
		return prefixListObj;
	}

}
