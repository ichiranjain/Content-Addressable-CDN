package topology;

import java.io.IOException;
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

/**
 * This class processes all update packets and applies the updates to the graph</br>
 * This is when the FIB is updated and DIjkstra's is run</br>
 * @author spufflez
 *
 */
public class ProcessUpdates {

	NodeRepository nodeRepo;
	UpdateMsgsSeen upDatesSeen;
	SendPacket sendPacket;
	FIB fib;
	DirectlyConnectedNodes directlyConnectedNodes;
	Dijkstras dijkstras;

	/**
	 * Constructor
	 * @param nodeRepo
	 * @param upDatesSeen
	 * @param fib
	 * @param directlyConnectedNodes
	 */
	public ProcessUpdates(NodeRepository nodeRepo, UpdateMsgsSeen upDatesSeen, FIB fib, DirectlyConnectedNodes directlyConnectedNodes){
		this.nodeRepo = nodeRepo;
		this.upDatesSeen = upDatesSeen;
		this.fib = fib;
		this.directlyConnectedNodes = directlyConnectedNodes;
		this.sendPacket = new SendPacket();
		this.dijkstras = new Dijkstras();
	}

	/**
	 * Add a new link to a cache server </br>
	 * This will add a neighbor to this cache server</br>
	 * @param linkObj
	 * @throws IOException
	 */
	public void addLink(LinkObj linkObj) throws IOException {
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
			//having this here may be inefficient, updating all the entries once the FIB gets large will not be efficient.
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

		upDatesSeen.addMsgID(modifyNodeObj.getMsgID(), System.nanoTime());

		//forward to all routers
		sendPacket.forwardToAllRouters(modifyNodeObj.getOriginalPacket());

	}

	/**
	 * Remove a directly connected cache server</br>
	 * This will remove the cache server as a neighbor</br>
	 * @param linkObj
	 * @throws IOException
	 */
	public void removeLink(LinkObj linkObj) throws IOException {
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

		upDatesSeen.addMsgID(modifyNodeObj.getMsgID(), System.nanoTime());

		//forward to all routers
		sendPacket.forwardToAllRouters(modifyNodeObj.getOriginalPacket());
	}

	/**
	 * Add a client/server to the cache server.</br>
	 * Clients and servers use this for connecting because they will not </br>
	 * receive routing updates</br> 
	 * @param linkObj
	 * @throws IOException
	 */
	public void addClientLink(LinkObj linkObj) throws IOException {
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

			upDatesSeen.addMsgID(prefixObj.getMsgID(), System.nanoTime());

			//sendPacket
			sendPacket.forwardToAllRouters(prefixObj.getOriginalPacket());
		}
	}

	/**
	 * This will remove a directly connected client/server
	 * @param linkObj
	 * @throws IOException
	 */
	public void removeClientLink(LinkObj linkObj) throws IOException {

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

			upDatesSeen.addMsgID(prefixListObj.getMsgID(), System.nanoTime());

			//sendPacket
			sendPacket.forwardToAllRouters(prefixListObj.getOriginalPacket());
		}
	}

	/**
	 * If a links cost to a cache server changes, modify link will apply the </br>
	 * cost change and update the graph
	 * @param linkObj
	 * @throws IOException
	 */
	public void modifyLink(LinkObj linkObj) throws IOException {

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

			upDatesSeen.addMsgID(modifyNodeObj.getMsgID(), System.nanoTime());

			//sendPacket
			sendPacket.forwardToAllRouters(modifyNodeObj.getOriginalPacket());

		}//if the neighbor does no exist... this packet was sent by mistake



	}

	/**
	 * This is called by the server to advertise a single content
	 * @param prefixObj
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void addCLientPrefix(PrefixObj prefixObj, String doNotSendToNode)
			throws IOException {

		//try to add the prefix, if the prefix and advertiser already exist, it will return false, else it will be added
		if(fib.addPrefixToFIB(prefixObj.getPrefixName(), prefixObj.getAdvertiser()) == true){

			//add the prefix to the clients list of prefixes 
			directlyConnectedNodes.getDirectlyConnectedClient(prefixObj.getAdvertiser()).addPrefix(prefixObj.getPrefixName());
		}

		//forward the prefix update using this router name, because it was a client prefix 
		prefixObj.setAdvertiser(nodeRepo.getThisMachinesName());
		//send the packet update to the rest of the graph 
		sendPacket.createPrefixPacket(prefixObj);

		upDatesSeen.addMsgID(prefixObj.getMsgID(), System.nanoTime());

		sendPacket.forwardUpdate(prefixObj.getOriginalPacket(), doNotSendToNode);
	}

	/**
	 * This is called by the server to advertise several content names
	 * @param prefixListObj
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void addClientPrefixList(PrefixListObj prefixListObj,
			String doNotSendToNode) throws IOException {

		ArrayList<String> prefixList = prefixListObj.getPrefixList();

		//for each prefix in the list try to add it 
		for(int i = 0; i < prefixListObj.getPrefixListLength(); i++){

			System.out.println("in for loop");
			//try to add the prefix, if the prefix and advertiser already exist, it will return false, else it will be added
			if(fib.addPrefixToFIB(prefixList.get(i), prefixListObj.getAdvertiser()) == true){
				System.out.println("added the prefix to fib");
				//add the prefix to the clients list of prefixes 
				directlyConnectedNodes.getDirectlyConnectedClient(prefixListObj.getAdvertiser()).addPrefix(prefixList.get(i));
			}

		}//end for loop	

		PrefixListObj sendPrefixListObj = new PrefixListObj(prefixList, nodeRepo.getThisMachinesName(),
				prefixListObj.getAddRemoveFlag(), nodeRepo.getThisMachinesName() + System.nanoTime());
		//send the packet update to the rest of the graph 
		sendPacket.createPrefixListPacket(sendPrefixListObj);

		upDatesSeen.addMsgID(sendPrefixListObj.getMsgID(), System.nanoTime());

		sendPacket.forwardUpdate(sendPrefixListObj.getOriginalPacket(), doNotSendToNode);
	}

	/**
	 * Removes a content name when an advertiser sends remove content name
	 * @param prefixObj
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void removeClientPrefix(PrefixObj prefixObj, String doNotSendToNode)
			throws IOException {

		//remove the prefix from the fib, this will return false if the prefix or advertiser does not exist
		if(fib.removePrefixFromFIB(prefixObj.getPrefixName(), prefixObj.getAdvertiser()) == true){

			//remove the prefix from the client list
			directlyConnectedNodes.getDirectlyConnectedClient(prefixObj.getAdvertiser()).removePrefix(prefixObj.getPrefixName());
		}

		//forward the prefix update
		prefixObj.setAdvertiser(nodeRepo.getThisMachinesName());

		//send the packet update to the rest of the graph 
		sendPacket.createPrefixPacket(prefixObj);

		upDatesSeen.addMsgID(prefixObj.getMsgID(), System.nanoTime());

		sendPacket.forwardUpdate(prefixObj.getOriginalPacket(), doNotSendToNode);

	}

	/**
	 * Removes a list of content names when an advertiser sends remove the content 
	 * @param prefixListObj
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void removeClientPrefixList(PrefixListObj prefixListObj,
			String doNotSendToNode) throws IOException {

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
		upDatesSeen.addMsgID(prefixListObj.getMsgID(), System.nanoTime());
		sendPacket.forwardUpdate(prefixListObj.getOriginalPacket(), doNotSendToNode);

	}

	/**
	 * adds a node to the graph </br>
	 * This would pertain to cache servers only
	 * @param addNodeObj
	 */
	public void addNode(AddNodeObj addNodeObj){

		if(nodeRepo.HMdoesNodeExist(addNodeObj.getName()) == false){			

			nodeRepo.HMaddNode(addNodeObj.getName());

		}
	}

	/**
	 * Removes a node from the graph </br>
	 * This would pertain to cache servers only
	 * @param removeNodeObj
	 */
	public void removeNode(RemoveNodeObj removeNodeObj){

		if(nodeRepo.HMdoesNodeExist(removeNodeObj.getName()) == true){

			//remove the node
			nodeRepo.HMremoveNode(removeNodeObj.getName());

			//run Dijkstra
			dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

			//sort FIB entries
			fib.findBestCostAdvertisers();
		}
	}

	/**
	 * This method is called with updates are received</br>
	 * It will get the node in the modify packet and updates is neighbors</br>
	 * and there costs, then call Dijkstra's to update the graph
	 * @param modifyNodeObj
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void modifyNode(ModifyNodeObj modifyNodeObj, String doNotSendToNode)
			throws IOException {

		//check the previous seen update messages IDs to make sure it is a new update
		//if it was seen ... drop packet

		if(upDatesSeen.doesMsgIDExist(modifyNodeObj.getMsgID()) == false){
			upDatesSeen.addMsgID(modifyNodeObj.getMsgID(), System.nanoTime());

			if(nodeRepo.HMdoesNodeExist(modifyNodeObj.getName()) == true){
				ArrayList<String> neighborsToRequest = new ArrayList<String>();
				ArrayList<String> neighbors = modifyNodeObj.getNeighborsNames();
				for(int i = 0; i < neighbors.size(); i++){

					//check if the node exist for the neighbor
					if(nodeRepo.HMdoesNodeExist(neighbors.get(i)) == false){

						//if the neighboring node is not in the graph
						//add the neighbor to the graph
						addNode(new AddNodeObj(neighbors.get(i)));

						fib.addPrefixToFIB(neighbors.get(i), neighbors.get(i));

						//request the newly added nodes neighbors
						// requestNeighbors(neighbors.get(i));
						neighborsToRequest.add(neighbors.get(i));
					}
				}

				//update the neighbors list for the given node
				nodeRepo.HMsetNeighborList(modifyNodeObj.getName(), modifyNodeObj.getNeighbors());

				//run Dijkstra
				dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

				//sort FIB entries
				fib.findBestCostAdvertisers();

				if (neighborsToRequest.size() > 0) {
					for (String request : neighborsToRequest) {
						requestNeighbors(request);
					}
				}

			}

			//send updates or forward updates
			sendPacket.createModifyNodePacket(modifyNodeObj);
			//upDatesSeen.addMsgID(modifyNodeObj.getMsgID(), System.nanoTime());
			sendPacket.forwardUpdate(modifyNodeObj.getOriginalPacket(), doNotSendToNode);
		}
		else {
			//The modify Node message was already seen
		}

	}

	/**
	 * This is called when a packet requesting neighbors and content names </br>
	 * @param sendToNode
	 */
	public void requestNeighbors(String sendToNode){

		//send request for neighbors 
		IntrestObj intrestObj = new IntrestObj(sendToNode + "/np", nodeRepo.getThisMachinesName(), 0);
		sendPacket.createRequestNeighborsIntrestPacket(intrestObj);

		String nextHop = fib.searchFIB(intrestObj.getContentName());

		if(nextHop.equals("broadcast") == false){
			//a route exists
			//set the sender name to this router
			intrestObj.setOriginRouterName(nodeRepo.getThisMachinesName());

			//modified the packet then forward it 
			sendPacket.createIntrestPacket(intrestObj);
			sendPacket.forwardPacket(intrestObj.getOriginalPacket(), nextHop);

		}
	}


	/**
	 * This is called to process a request for neighbors</br>
	 * once the data packet with the neighbors information is received</br>
	 * this method is called to process the packet
	 * @param modifyNodeObj
	 */
	public void processNeighborsResponse(ModifyNodeObj modifyNodeObj){

		//if(upDatesSeen.doesMsgIDExist(modifyNodeObj.getMsgID()) == false){
		//	upDatesSeen.addMsgID(modifyNodeObj.getMsgID(), System.nanoTime());

		if(nodeRepo.HMdoesNodeExist(modifyNodeObj.getName()) == true){

			ArrayList<String> neighborsToRequest = new ArrayList<String>();
			ArrayList<String> neighbors = modifyNodeObj.getNeighborsNames();

			for(int i = 0; i < neighbors.size(); i++){

				//check if the node exist for the neighbor
				if(nodeRepo.HMdoesNodeExist(neighbors.get(i)) == false){

					//if the neighboring node is not in the graph
					//add the neighbor to the graph
					addNode(new AddNodeObj(neighbors.get(i)));
					fib.addPrefixToFIB(neighbors.get(i), neighbors.get(i));

					neighborsToRequest.add(neighbors.get(i));
				}
			}

			//update the neighbors list for the given node
			nodeRepo.HMsetNeighborList(modifyNodeObj.getName(), modifyNodeObj.getNeighbors());

			//run Dijkstra
			dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

			// sort FIB entries
			fib.findBestCostAdvertisers();

			if (neighborsToRequest.size() > 0) {
				for (String request : neighborsToRequest) {
					requestNeighbors(request);
				}
			}
		}

	}

	/**
	 * This is called to process a request for content names</br>
	 * once the data packet with the content names is received</br>
	 * this method is called to process the packet
	 * @param prefixListObj
	 */
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

	/**
	 * This is called to process a request for neighbors and content names</br>
	 * This will send out a data packet with the neighbors and </br>
	 * a data packet with the content names</br>
	 * Be careful if last packet is set on the first data packet sent, the </br>
	 * second data packet will be dropped 
	 * @param neighborRequestObj
	 */
	public void processIntrestRequestForNeighbors(NeighborRequestObj neighborRequestObj){
		//get the neighbors 
		ModifyNodeObj modifyNodeObj = getMyNeighbors();
		//get the prefixes 
		PrefixListObj prefixListObj = getMyDirectlyConnectedPrefixes();

		byte b = 0;
		DataObj dataObj = new DataObj(neighborRequestObj.getContentName(), 
				neighborRequestObj.getOriginRouter(),
				b, 
				modifyNodeObj.getOriginalPacket(), 
				"",
				b,
				true);

		sendPacket.createDataPacket(dataObj);
		//send out 1 neighbors data packet
		sendPacket.forwardPacket(dataObj.getOriginalPacket(), neighborRequestObj.getNextHop());


		dataObj.setData(prefixListObj.getOriginalPacket());
		sendPacket.createDataPacket(dataObj);
		//send out 1 prefix data packet 
		sendPacket.forwardPacket(dataObj.getOriginalPacket(), neighborRequestObj.getNextHop());
	}

	/**
	 * This method is called an update from another cache server is received</br>
	 * and a content name needs to be added to the FIB</br>
	 * This method will also forward the packet after adding the content name.
	 * @param prefixObj
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void addPrefix(PrefixObj prefixObj, String doNotSendToNode)
			throws IOException {

		//if the prefix update has been seen already, do nothing 
		if(upDatesSeen.doesMsgIDExist(prefixObj.getMsgID()) == false){

			//add the msgID to the update list 
			upDatesSeen.addMsgID(prefixObj.getMsgID(), System.nanoTime());

			//try to add the prefix to the fib 
			fib.addPrefixToFIB(prefixObj.getPrefixName(), prefixObj.getAdvertiser());

			//forward the pack to the rest of the graph
			sendPacket.createPrefixPacket(prefixObj);
			upDatesSeen.addMsgID(prefixObj.getMsgID(), System.nanoTime());
			sendPacket.forwardUpdate(prefixObj.getOriginalPacket(), doNotSendToNode);
		}

	}

	/**
	 * This method is called an update from another cache server is received</br>
	 * and a list of content names needs to be added to the FIB</br>
	 * This method will also forward the packet after adding the content names.
	 * @param prefixListObj
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void addPrefixList(PrefixListObj prefixListObj,
			String doNotSendToNode) throws IOException {

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
			upDatesSeen.addMsgID(prefixListObj.getMsgID(), System.nanoTime());
			sendPacket.forwardUpdate(prefixListObj.getOriginalPacket(), doNotSendToNode);
		}
	}

	/**
	 * This method is called an update from another cache server is received</br>
	 * and a content name needs to be removed to the FIB</br>
	 * This method will also forward the packet after removing the content name.
	 * @param prefixObj
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void removePrefix(PrefixObj prefixObj, String doNotSendToNode)
			throws IOException {
		//if the entry exists remove it 

		if(upDatesSeen.doesMsgIDExist(prefixObj.getMsgID()) == false){

			//add the msgID to the update list
			upDatesSeen.addMsgID(prefixObj.getMsgID(), System.nanoTime());

			//try to remove the prefix for the fib
			fib.removePrefixFromFIB(prefixObj.getPrefixName(), prefixObj.getAdvertiser());

			//forward the update to the rest of the graph
			sendPacket.createPrefixPacket(prefixObj);
			upDatesSeen.addMsgID(prefixObj.getMsgID(), System.nanoTime());
			sendPacket.forwardUpdate(prefixObj.getOriginalPacket(), doNotSendToNode);

		}

	}

	/**
	 * This method is called an update from another cache server is received</br>
	 * and a list of content names needs to be removed to the FIB</br>
	 * This method will also forward the packet after removing the content names.
	 * @param prefixListObj
	 * @param doNotSendToNode
	 * @throws IOException
	 */
	public void removePrefixList(PrefixListObj prefixListObj,
			String doNotSendToNode) throws IOException {

		if(upDatesSeen.doesMsgIDExist(prefixListObj.getMsgID()) == false){

			//add the msgID to the update list 
			upDatesSeen.addMsgID(prefixListObj.getMsgID(), System.nanoTime());

			ArrayList<String> prefixList = prefixListObj.getPrefixList();
			for(int i = 0; i < prefixListObj.getPrefixListLength(); i++){
				fib.removePrefixFromFIB(prefixList.get(i), prefixListObj.getAdvertiser());
			}

			//forward the update to the rest of the graph
			sendPacket.createPrefixListPacket(prefixListObj);
			upDatesSeen.addMsgID(prefixListObj.getMsgID(), System.nanoTime());
			sendPacket.forwardUpdate(prefixListObj.getOriginalPacket(), doNotSendToNode);

		}

	}

	/**
	 * Gets this cache servers neighbors, this is used when a request </br>
	 * for neighbors is received
	 * @return modify node object
	 */
	public ModifyNodeObj getMyNeighbors(){
		ModifyNodeObj modifyNodeObj = new ModifyNodeObj(nodeRepo.getThisMachinesName(),
				nodeRepo.HMgetNode(nodeRepo.getThisMachinesName()).getNeighbors(), 
				(nodeRepo.getThisMachinesName() + System.nanoTime()));
		sendPacket.createNeighborResponsePacket(modifyNodeObj);
		return modifyNodeObj;
	}

	/**
	 * Gets the content names for any directly connected clients/servers</br>
	 * this is called when a request for content names is recieved.
	 * @return prefix list object 
	 */
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
