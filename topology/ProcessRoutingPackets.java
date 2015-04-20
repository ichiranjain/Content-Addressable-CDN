package topology;

import java.util.ArrayList;

import packetObjects.DataObj;
import packetObjects.IntrestObj;

public class ProcessRoutingPackets {

	String packet;
	NodeRepository nodeRepo;
	Parse parse;
	FIB fib;
	PIT pit;
	SendPacket sendPacket;
	DirectlyConnectedNodes directlyConnectedNodes;

	public ProcessRoutingPackets(String packet, 
			NodeRepository nodeRepo,
			FIB fib,
			PIT pit,
			DirectlyConnectedNodes directlyConnectedNodes){

		this.packet = packet;
		this.nodeRepo = nodeRepo;
		this.fib = fib;
		this.pit = pit;
		this.directlyConnectedNodes = directlyConnectedNodes;
		this.parse = new Parse();
		this.sendPacket = new SendPacket();

	}

	public void processIntrest(IntrestObj intrestObj){
		//check the cs
		//		if( check cs == true ){
		//			
		//		}


		//check the pit
		if(pit.doesEntryExist(intrestObj.getContentName()) == true){
			//add info to the pit entry if it is new... if it is in the pit already
			//resend the packet
		}

		//**check local connections... non nodes
		//if == to local connection ... forward to local connection
		//local conection can be servers or clients

		//		//get the length of the prefix 
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
		//
		//						//what if there is another way to get to the desired content 
		//						//broad cast
		//
		//						if(pit.doesEntryExist(intrestObj.getContentName()) == true){
		//
		//							//ensure the packet is not being forwarded to a node that sent the interest
		//							if(pit.getRequesters(intrestObj.getContentName()).doesRequesterExist(nextHop) != -1){
		//
		//								//the interest was sent form the next hop ... the packet can not be forwarded
		//								nextHop = "broadCast";	
		//							}
		//						}
		//						break;
		//					}else{
		//						//the next hop does not exist... broad cast 
		//						bestCostNode = "broadCast";
		//					}
		//				}//if the prefix is not in the hash map ... it might have been a false positive
		//
		//
		//			}//the hash map did not exist
		//		}//end for loop	

		String nextHop = fib.searchFIB(intrestObj.getContentName());

		if(nextHop.equals("broadCast") == true){
			//broad cast
			//sendPacket.broadcast();

		}else{
			//a route exists

			//check if this is the first router to handle the packet
			if(intrestObj.getOriginRouterName().equals("") == true){
				//set the sender name to this router

				//fix parse ... add check for blank sender name
				intrestObj.setOriginRouterName(nodeRepo.getThisMachinesName());

				//modified the packet then forward it 
				sendPacket.createIntrestPacket(intrestObj);
				sendPacket.forwardPacket(intrestObj.getOriginalPacket(), nextHop);

			}else{
				//record in the pit
				//pit.addEntry(intrestObj.getContentName(), interface that sent intrest;

				//forward
				sendPacket.forwardPacket(intrestObj.getOriginalPacket(), nextHop);
			}


		}

	}


	public void processData0(DataObj dataObj){

		//0
		//check the cs

		//check the pit
		if(pit.doesEntryExist(dataObj.getContentName()) ==  true){
			ArrayList<String> requesters = pit.getRequesters(dataObj.getContentName()).getRequesters();

			boolean alternativePathUsed = false;
			for(int i = 0; i < requesters.size(); i++){

				//requesters are always directly connected
				//does the requester (next hop node) exist 
				//if requester is down ... set to 1 AND boolean is not set 
				if(nodeRepo.HMdoesNodeExist(requesters.get(i)) == true){

					//forward the packet to each of the requesters
					sendPacket.forwardPacket(dataObj.getOriginalPacket(), requesters.get(i));

				}else{

					//set to boolean and call processData1 and send to origin router 
					//this way the packet isn't sent to origin multiple times
					if(alternativePathUsed == false ){
						alternativePathUsed = true;
						processData1(dataObj);
					}
				}
			}
		}else{
			//if no entry exists ... drop the packet
			return;
		}
	}

	public void processData1(DataObj dataObj){

		//1
		//if the origin router is this machine ... call process data 2
		//and send to all the pit requesters
		if(nodeRepo.getThisMachinesName().equals(dataObj.getOriginRouterName()) == true){
			//set the flag to 2
			processData2(dataObj);
			//send to all the requesters

			ArrayList<String> requesters = pit.getRequesters(dataObj.getContentName()).getRequesters();
			for(int i = 0; i < requesters.size(); i++){
				sendPacket.createDataPacket(dataObj);
				sendPacket.forwardPacket(dataObj.getOriginalPacket(), requesters.get(i));
			}
		}

		//try to forward to the origin router
		String nextHop = "";
		if(nodeRepo.HMdoesNodeExist(dataObj.getOriginRouterName()) == true){
			nextHop = nodeRepo.HMgetNode(dataObj.getOriginRouterName()).getOriginNextHop();
			if(nodeRepo.HMdoesNodeExist(nextHop) == true){
				//send the packet
				sendPacket.forwardPacket(dataObj.getOriginalPacket(), nextHop);
			}else{
				//dont send the packet, drop the packet
			}
		}else{
			//drop the packet
		}
	}

	public void processData2(DataObj dataObj){
		//2
		//check if a pit entry exists 
		if(pit.doesEntryExist(dataObj.getContentName()) == true){
			//forward towards the server 
			//String nextHop = checkFIB(dataObj.getContentName());
			String nextHop = fib.searchFIB(dataObj.getContentName());
			//sendPacket
			sendPacket.forwardPacket(dataObj.getOriginalPacket(), nextHop);

		}else{
			//drop the packet
		}

	}

	//	public String checkFIB(String contentName){
	//
	//		//get the length of the prefix 
	//		String[] prefixSplit = contentName.split("/");
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
	//
	//						//what if there is another way to get to the desired content 
	//						//broad cast
	//
	//						if(pit.doesEntryExist(contentName) == true){
	//
	//							//ensure the packet is not being forwarded to a node that sent the interest
	//							if(pit.getRequesters(contentName).doesRequesterExist(nextHop) != -1){
	//
	//								//the interest was sent form the next hop ... the packet can not be forwarded
	//								nextHop = "broadCast";	
	//							}
	//						}
	//						break;
	//					}else{
	//						//the next hop does not exist... broad cast 
	//						bestCostNode = "broadCast";
	//					}
	//				}//if the prefix is not in the hash map ... it might have been a false positive
	//
	//
	//			}//the hash map did not exist
	//		}//end for loop
	//
	//		//if the next hop .equals myrouter name 
	//		//then make it an update packet and pass it to the update queue
	//
	//
	//		return nextHop;
	//	}


}
