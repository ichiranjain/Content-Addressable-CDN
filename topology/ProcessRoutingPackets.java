package topology;

import java.io.IOException;
import java.util.ArrayList;

import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.PITEntry;

public class ProcessRoutingPackets {

	//String packet;
	NodeRepository nodeRepo;
	//Parse parse;
	FIB fib;
	PIT pit;
	SendPacket sendPacket;
	DirectlyConnectedNodes directlyConnectedNodes;
	String recievedFromNode;

	public ProcessRoutingPackets( 
			NodeRepository nodeRepo,
			FIB fib,
			PIT pit,
			DirectlyConnectedNodes directlyConnectedNodes,
			String recievedFromNode){

		//this.packet = packet;
		this.nodeRepo = nodeRepo;
		this.fib = fib;
		this.pit = pit;
		this.directlyConnectedNodes = directlyConnectedNodes;
		this.recievedFromNode = recievedFromNode;
		//this.parse = new Parse();
		this.sendPacket = new SendPacket();

	}

	public void processIntrest(IntrestObj intrestObj) throws IOException {
		//check the cs
		//		if( check cs == true ){
		//			intrestObj.getContentName() need received from node
		//if the content is in the cs.... send the data out and return 
		//dataObj()
		//sendPacket.createDataPacket();
		//sendPacket.sendPacket (Received from node);
		//		}


		if(intrestObj.getOriginRouterName().equals("") == true){
			//this is a client	
			PITEntry pitEntry = pit.addClientEntryIfItDoesntExist(intrestObj.getContentName());
			if(pitEntry != null){	

				//if the pit entry exists 

				//does the requester exist in the requesters list 
				if(pit.doesClientRequesterExist(intrestObj.getContentName(), recievedFromNode) == false){

					//if the client requester does not exist, add to the client to the client requester list 
					pit.addCLientRequester(intrestObj.getContentName(), recievedFromNode);
				}

				//30000000000L == 30sec in nano time 
				//if 20 seconds has not elapsed do not re send
				if(System.nanoTime() - pit.getTime(intrestObj.getContentName()) < 5000000000L ){
					return;				
				}else{
					pit.setTime(intrestObj.getContentName());
					intrestObj.setOriginRouterName(nodeRepo.getThisMachinesName());
					sendPacket.createIntrestPacket(intrestObj);
				}

			}else{
				//add to the router requester list 
				pit.addCLientRequester(intrestObj.getContentName(), recievedFromNode);

				//add to client requesters list
				intrestObj.setOriginRouterName(nodeRepo.getThisMachinesName());
				sendPacket.createIntrestPacket(intrestObj);

			}
		}else{
			//this is from another router
			//this will add the pit entry if it does not exist
			//if it doesn't exists the returned value will be null
			//else it will return the value the hash map has for that key 
			PITEntry pitEntry = pit.addEntryIfItDoesntExist(intrestObj.getContentName());

			//the pit entry already exists
			//if the entry is not null, it means someone already requested this content, 
			//so add the new requester
			if(pitEntry != null){	

				//does the requester exist in the requesters list 
				if(pit.doesRequesterExist(intrestObj.getContentName(), recievedFromNode) == false){

					pit.addRequester(intrestObj.getContentName(), recievedFromNode);
				}

				//30000000000L == 30sec in nano time 
				//if 20 seconds has not elapsed do not re send
				if(System.nanoTime() - pit.getTime(intrestObj.getContentName()) < 5000000000L ){
					return;				
				}else{
					pit.setTime(intrestObj.getContentName());
					//intrestObj.setOriginRouterName(nodeRepo.getThisMachinesName());
					sendPacket.createIntrestPacket(intrestObj);
				}

			}else{

				//add to the router requester list 
				pit.addRequester(intrestObj.getContentName(), recievedFromNode);

				//add to client requesters list
				//intrestObj.setOriginRouterName(nodeRepo.getThisMachinesName());
				sendPacket.createIntrestPacket(intrestObj);

			}
		}

		String nextHop = fib.searchFIB(intrestObj.getContentName());

		if(nextHop.equals("broadCast") == true){
			//broad cast
			sendPacket.forwardToAllRouters(intrestObj.getOriginalPacket());

			//if the nextHop == "" the node could have been deleted, drop packet 
		}else if(!nextHop.equals("") == true){

			sendPacket.forwardPacket(intrestObj.getOriginalPacket(), nextHop);

		}
	}

	public void processData0(DataObj dataObj){

		//0
		//check the cs flag
		if(dataObj.getCacheFlag() == 2){
			//pass to CS
			//return;
		}

		//check the pit
		if(pit.doesEntryExist(dataObj.getContentName()) ==  true){

			//update the pit entry time
			pit.setTime(dataObj.getContentName());

			ArrayList<String> requesters = pit.getRequesters(dataObj.getContentName()).getRequesters();

			//remove the pit entry if this was the last chunk packet 
			//			if(dataObj.getLastChunk() == true){
			//				pit.removeEntry(dataObj.getContentName());
			//			}

			boolean alternativePathUsed = false;
			for(int i = 0; i < requesters.size(); i++){

				//requesters are always directly connected
				//does the requester (next hop node) exist 
				//if requester is down ... set to 1 AND boolean is not set 
				if((nodeRepo.HMdoesNodeExist(requesters.get(i)) == true)){
					//	 || (directlyConnectedNodes.doesDirectlyConnectedClientExist(requesters.get(i)) == true) ){

					//forward the packet to each of the requester
					sendPacket.forwardPacket(dataObj.getOriginalPacket(), requesters.get(i));

				}else{

					//if the original server is not equal to this routers name, try to forward the packet
					if( !nodeRepo.getThisMachinesName().equals(dataObj.getOriginRouterName()) ){

						//set to boolean and call processData1 and send to origin router 
						//this way the packet isn't sent to origin multiple times
						if(alternativePathUsed == false ){
							alternativePathUsed = true;
							processData1(dataObj);
						}
					}
				}
			}

			ArrayList<String> clientRequesters = pit.getClientRequesters(dataObj.getContentName()).getClientRequesters();
			//System.out.println("clientRequesters: " + clientRequesters);
			//System.out.println("size: " + clientRequesters.size());
			for(int i = 0; i < clientRequesters.size(); i++){
				System.out.println("in for loop");
				if(directlyConnectedNodes.doesDirectlyConnectedClientExist(clientRequesters.get(i)) == true){
					//System.out.println("client existed");
					//System.out.println(dataObj.getOriginalPacket());
					//System.out.println(clientRequesters.get(i));
					//forward the packet to each of the requester
					sendPacket.forwardPacket(dataObj.getOriginalPacket(), clientRequesters.get(i));

				}
			}
		}else{
			//if no pit entry exists ... drop the packet
			return;
		}
	}

	public void processData1(DataObj dataObj){

		/*
		 * add ttl to kill packet
		 */

		//1
		//if the origin router is this machine ... call process data 2
		//and send to all the pit requesters
		if(nodeRepo.getThisMachinesName().equals(dataObj.getOriginRouterName()) == true){


			//send to all the requesters
			//ArrayList<String> requesters = pit.getRequesters(dataObj.getContentName()).getRequesters();

			//			for(String req : requesters){
			//				System.out.println("req: " + req);
			//}

			//set the flag to 2
			byte b = 2;
			dataObj.setFlag(b);
			//set the flag to 2
			//this will forward to pit entries and forward to the server
			processData2(dataObj);

			//			//remove the pit entry if this was the last chunk
			//			if(dataObj.getLastChunk() == true){
			//				pit.removeEntry(dataObj.getContentName());
			//			}
			//
			//			for(int i = 0; i < requesters.size(); i++){
			//				sendPacket.createDataPacket(dataObj);
			//				sendPacket.forwardPacket(dataObj.getOriginalPacket(), requesters.get(i));
			//			}
		}else{

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

	}

	public void processData2(DataObj dataObj){
		//2
		//check if a pit entry exists 
		if(pit.doesEntryExist(dataObj.getContentName()) == true){

			//update the pit entry time if the entry exists 
			pit.setTime(dataObj.getContentName());

			//send to all the requesters
			ArrayList<String> requesters = pit.getRequesters(dataObj.getContentName()).getRequesters();
			ArrayList<String> clientRequesters = pit.getClientRequesters(dataObj.getContentName()).getRequesters();
			//remove the pit entry if this was the last chunk
			//			if(dataObj.getLastChunk() == true){
			//				pit.removeEntry(dataObj.getContentName());
			//			}


			for(int i = 0; i < requesters.size(); i++){
				if(nodeRepo.HMdoesNodeExist(requesters.get(i)) == true){
					dataObj.setFlag((byte)0);
					sendPacket.createDataPacket(dataObj);
					sendPacket.forwardPacket(dataObj.getOriginalPacket(), requesters.get(i));
				}
			}

			for(int i = 0; i < clientRequesters.size(); i++){
				if(directlyConnectedNodes.doesDirectlyConnectedClientExist(clientRequesters.get(i)) == true){
					dataObj.setFlag((byte)0);
					sendPacket.createDataPacket(dataObj);
					sendPacket.forwardPacket(dataObj.getOriginalPacket(), requesters.get(i));
				}
			}


			//forward towards the server 
			//String nextHop = checkFIB(dataObj.getContentName());
			String nextHop = fib.searchFIB(dataObj.getContentName());
			//sendPacket
			dataObj.setFlag((byte)2);
			sendPacket.createDataPacket(dataObj);
			sendPacket.forwardPacket(dataObj.getOriginalPacket(), nextHop);

		}else{
			//drop the packet
		}
	}

	public void preocessPingRequest(IntrestObj intrestObj){
		byte b = 0;
		String data = nodeRepo.getThisMachinesName() + "/ping";
		DataObj dataObj = new DataObj(intrestObj.getContentName(), 
				intrestObj.getOriginRouterName(), b, data, b, true);

		sendPacket.createDataPacket(dataObj);
		sendPacket.forwardPacket(dataObj.getOriginalPacket(), recievedFromNode);
	}

	public void processPingReply(DataObj dataObj){
		//print data portion of data obj
		System.out.println("ping response: " + dataObj.getData());
	}
}
