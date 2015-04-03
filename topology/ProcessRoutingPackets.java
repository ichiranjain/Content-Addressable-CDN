package topology;

import java.util.ArrayList;

import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.PITEntry;

public class ProcessRoutingPackets {

	String packet;
	NodeRepository nodeRepo;
	Parse parse;
	FIB fib;
	PIT pit;
	SendPacket sendPacket;

	public ProcessRoutingPackets(String packet, 
			NodeRepository nodeRepo,
			Parse parse,
			FIB fib,
			PIT pit,
			SendPacket sendPacket){

		this.packet = packet;
		this.nodeRepo = nodeRepo;
		this.parse = parse;
		this.fib = fib;
		this.pit = pit;
		this.sendPacket = sendPacket;

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

		//get the length of the prefix 
		String[] prefixSplit = intrestObj.getContentName().split("/");
		String prefix = prefixSplit[0];
		ArrayList<Integer> hashMapsToSearch = new ArrayList<Integer>();

		//look up in the fib bloom filter, cant have a prefix of zero
		//this can be searched in parallel
		for(int i = 1; i < prefixSplit.length; i++){

			if(fib.doesPrefixLengthBloomFilterExist(i) == true){				
				if(fib.doesBloomFilterConteinPrefix(i, prefix) == true){
					hashMapsToSearch.add(i);
				}
			}
			prefixSplit[i] = prefix = prefix + "/" + prefixSplit[i];
		}

		//search the hash maps returned
		//ArrayList<String> bestMatch = new ArrayList<String>();
		String bestCostNode = "";
		String nextHop = "";
		for(int i = hashMapsToSearch.size(); i > 0; i--){

			//does the hash map for "x" length exist
			if(fib.doesPrefixLengthHashMapExist(hashMapsToSearch.get(i)) == true){

				//does the prefix in this hash map exist
				if(fib.doesHashMapContainPrefix(hashMapsToSearch.get(i), prefixSplit[hashMapsToSearch.get(i)]) == true){

					//if this advertiser can not be used due to a conflicting next hop 
					//try the next advertiser in the list 
					bestCostNode = fib.getBestCostAdvertiser(hashMapsToSearch.get(i), prefixSplit[hashMapsToSearch.get(i)]);

					if(bestCostNode.equals("error") == false){
						// there was no error
						nextHop = nodeRepo.HMgetNode(bestCostNode).getOriginNextHop();
						if(pit.doesEntryExist(intrestObj.getContentName()) == true){
							//ensure the packet is not being forwarded to a node that sent the interest
							if(pit.getRequesters(intrestObj.getContentName()).doesRequesterExist(nextHop) != -1){
								//the intrest was sent form the next hop ... the packet can not be forwaded
								nextHop = "";
							}
						}
						break;
					}else{
						//there was an error
						bestCostNode = "";
					}
				}


			}
		}	

		if(nextHop.equals("") == true){
			//send no route exists
		}else{
			//a route exists

			if(intrestObj.getSenderName().equals("") == true){
				//sent the sender name to this router

				//fix parse ... add check for blank sender name
				intrestObj.setSenderName(nodeRepo.getThisMachinesName());
			}

			//record in the pit
			pit.addEntry(intrestObj.getContentName(), interface that sent intrest;

			//forward
			//send packet
		}

	}
	public void processData(DataObj dataObj){
		//check the flag
		//0
		//check the pit
		if(pit.doesEntryExist(dataObj.getContentName()) ==  true){
			ArrayList<String> requesters = pit.getRequesters(dataObj.getContentName()).getRequesters();

			for(int i = 0; i < requesters.size(); i++){
				//send the packet to each of the requesters

			}
		}else{
			//if no entry exists ... drop the packet
			return;
		}


		//1
		//check the fib
		//try to forward to the original router

		//2
		//check fib
		//forward to server
	}
	public void processRouteDNE(){

		//the node can be down 
		//the link the node uses could be down 


		//get the advertisers for the content
		//
		//remove route from fib
		//check fib

	}



}
