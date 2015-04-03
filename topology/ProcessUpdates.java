package topology;

import java.util.ArrayList;

import packetObjects.AddNodeObj;
import packetObjects.HelloObj;
import packetObjects.ModifyNodeObj;
import packetObjects.PrefixObj;
import packetObjects.RemoveNodeObj;
import packetObjects.TableObj;

public class ProcessUpdates {

	NodeRepository nodeRepo;
	UpdateMsgsSeen upDatesSeen;
	SendPacket sendPacket;
	FIB fib;
	Dijkstras dijkstras;

	public ProcessUpdates(NodeRepository nodeRepo, UpdateMsgsSeen upDatesSeen, FIB fib){
		this.nodeRepo = nodeRepo;
		this.upDatesSeen = upDatesSeen;
		this.fib = fib;
		this.sendPacket = new SendPacket();
		this.dijkstras = new Dijkstras();
	}

	public void addNode(AddNodeObj addNodeObj){

		//check the previous seen update messages IDs to make sure it is a new update
		if(upDatesSeen.doesMsgIDExist(addNodeObj.getMsgID()) == false){
			upDatesSeen.addMsgID(addNodeObj.getMsgID(), System.nanoTime());

			if(nodeRepo.HMdoesNodeExist(addNodeObj.getName()) == false){			

				//add the new node
				nodeRepo.HMaddNode(addNodeObj.getName());

				//add its neighbors
				nodeRepo.HMgetNode(addNodeObj.getName()).setNeighborArray(addNodeObj.getNeighbors());

				//run Dijkstra
				dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

				//sort FIB entries
				fib.findBestCostAdvertisers();


				//send updates or forward updates
				sendPacket.forwardAddNodePacket(addNodeObj.getOriginalPacket());
			}else{
				//forward 
				sendPacket.forwardAddNodePacket(addNodeObj.getOriginalPacket());
			}
		}else{
			//drop the packet
		}

	}

	public void removeNode(RemoveNodeObj removeNodeObj){

		//check the previous seen update msgs IDs to make sure it is a new update
		if(upDatesSeen.doesMsgIDExist(removeNodeObj.getMsgID()) == false){
			upDatesSeen.addMsgID(removeNodeObj.getMsgID(), System.nanoTime());

			if(nodeRepo.HMdoesNodeExist(removeNodeObj.getName()) == true){
				nodeRepo.HMremoveNode(removeNodeObj.getName());

				//run Dijkstra
				dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

				//sort FIB entries
				fib.findBestCostAdvertisers();

				//send updates or forward updates
				sendPacket.forwardAddNodePacket(removeNodeObj.getOriginalPacket());
			}else{
				//forward msg
				sendPacket.forwardAddNodePacket(removeNodeObj.getOriginalPacket());
			}
		}else{
			//drop packet
		}

	}

	public void modifyNode(ModifyNodeObj modifyNodeObj){

		//check the previous seen update msgs IDs to make sure it is a new update
		//if it was seen ... drop packet
		if(upDatesSeen.doesMsgIDExist(modifyNodeObj.getMsgID()) == false){
			upDatesSeen.addMsgID(modifyNodeObj.getMsgID(), System.nanoTime());

			if(nodeRepo.HMdoesNodeExist(modifyNodeObj.getName()) == true){
				nodeRepo.HMsetNeighborList(modifyNodeObj.getName(), modifyNodeObj.getNeighbors());

				//run Dijkstra
				dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

				//sort FIB entries
				fib.findBestCostAdvertisers();

				//send updates or forward updates
				sendPacket.forwardModifyNodePacket(modifyNodeObj.getOriginalPacket());
			}else{
				sendPacket.forwardModifyNodePacket(modifyNodeObj.getOriginalPacket());
			}
		}

	}

	public void addPrefix(PrefixObj prefixObj){

		//if the prefix update has been seen already, do nothing 
		if(upDatesSeen.doesMsgIDExist(prefixObj.getMsgID()) == false){
			String[] contentNameSplit = prefixObj.getContentName().split("/");
			int prefixLength = contentNameSplit.length;
			//does the fib contain a hashmap for the content length
			//if not make one
			if(fib.doesPrefixLengthHashMapExist(prefixLength) == false){
				fib.addPrefixLengthHashMap(prefixLength);
				//fib.addDefualtRoute(ContentNameLength, defaultNode);
			}


			if(fib.doesHashMapContainPrefix(prefixLength, prefixObj.getContentName()) == false){

				//add the content name and an empty list of advertisers 
				fib.addPrefixToHashMap(prefixLength, prefixObj.getContentName());

				//add the prefix to the Counting BLoom Filter 
				fib.addPrefixToBloomFilter(prefixLength, prefixObj.getContentName());

				//for each advertiser in the list 
				//this is normally a list of size 1 
				for(int i = 0; i < prefixObj.getAdvertisers().size(); i++){

					//does the advertiser node exist in the graph, if not skip this advertiser
					if(nodeRepo.HMdoesNodeExist(prefixObj.getAdvertiser(i)) == true){

						//does the advertiser exist, -1 is returned if the advertiser does not exist 
						if( fib.doesHashMapContainAdvertiser(prefixLength, prefixObj.getContentName(), prefixObj.getAdvertiser(i)) == -1){

							//if the advertiser does not exist, add it
							fib.addAdvertiserToHashMap(prefixLength, prefixObj.getContentName(), prefixObj.getAdvertiser(i));
						}
					}


				}
				//send here
			}else{
				//if the content name does exist the just add the new advertisers
				//for each advertiser in the list, the bloom filter does not need to be altered
				for(int i = 0; i < prefixObj.getAdvertisers().size(); i++){


					//does the advertiser node exist in the graph 
					if(nodeRepo.HMdoesNodeExist(prefixObj.getAdvertiser(i)) == true){

						//does the advertiser exist, -1 is returned if the advertiser does not exist
						if( fib.doesHashMapContainAdvertiser(prefixLength, prefixObj.getContentName(), prefixObj.getAdvertiser(i)) == -1){

							//if the advertiser does not exist, add it
							fib.addAdvertiserToHashMap(prefixLength, prefixObj.getContentName(), prefixObj.getAdvertiser(i));
						}
					}
				}
				//send here

			}
		}

	}

	public void removePrefix(PrefixObj prefixObj){
		//if the entry exists remove it 

		if(upDatesSeen.doesMsgIDExist(prefixObj.getMsgID()) == false){
			String[] contentNameSplit = prefixObj.getContentName().split("/");
			int prefixLength = contentNameSplit.length;
			if(fib.doesPrefixLengthHashMapExist(prefixLength) == true){
				if(fib.doesHashMapContainPrefix(prefixLength, prefixObj.getContentName()) == true){
					fib.removePrefixFromHashMap(prefixLength, prefixObj.getContentName());
					fib.removePrefixFromBloomFIlter(prefixLength, prefixObj.getContentName());

				}

			}
			//send here
		}



	}

	public void processHelloHeartBeat(HelloObj helloObj){											
		//respond to the heart beat 
		sendPacket.sendHeartBeatReponse();
	}
	public void processHelloTableRequest(HelloObj helloObj){

		//send the current graph
		sendPacket.sendTable();
	}

	public void processTable(TableObj tableObj){

		//for each node in the list
		//does the graph contain the node
		//yes update the nodes fields
		//no add the node 
		ArrayList<Node> graph = tableObj.getGraph();

		for( int i = 0; i < graph.size(); i++){

			if(nodeRepo.HMdoesNodeExist(graph.get(i).getName()) == true){

				//for each neighbor ...add the new neighbor if it is not in the nodes 
				//neighbor list 
				for(int j = 0; j < graph.get(i).sizeOfNeighborList(); j++){

					String neighborName = graph.get(i).getName();

					if(nodeRepo.HMgetNode(neighborName).doesNeighborExist(graph.get(i).getNeighbor(j).getNeighborName()) == false){
						//the neighbor does not exist  
						//add the neighbor (node to have the neighbor added, name of the neighbor, cost of the neighbor
						nodeRepo.HMaddNeighbor(graph.get(i).getName(), neighborName, graph.get(i).getNeighbor(j).getCost());
					}

				}

			}else{
				//add the new node
				nodeRepo.HMaddNode(graph.get(i).getName());

				//add its neighbors
				nodeRepo.HMgetNode(graph.get(i).getName()).setNeighborArray(graph.get(i).getNeighbors());

			}
		}

		//run Dijkstra
		dijkstras.runDijkstras(nodeRepo.getGraph(), nodeRepo.getThisMachinesName());

		//sort FIB entries
		fib.findBestCostAdvertisers();

	}


}
