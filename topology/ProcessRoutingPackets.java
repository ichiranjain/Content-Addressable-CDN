package topology;

import java.io.IOException;
import java.util.ArrayList;

import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.PITEntry;
import caching.Content;
import caching.ContentStore;

/**
 * This class is used to process interest and data packets.</br>
 * Ping request are processed and sent from this class</br>
 * 
 * @author spufflez
 *
 */
public class ProcessRoutingPackets {

	//String packet;
	NodeRepository nodeRepo;
	//Parse parse;
	FIB fib;
	PIT pit;
	SendPacket sendPacket;
	DirectlyConnectedNodes directlyConnectedNodes;
	String recievedFromNode;

	/**
	 * Constructor
	 * @param nodeRepo
	 * @param fib
	 * @param pit
	 * @param directlyConnectedNodes
	 * @param recievedFromNode
	 */
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

	/**
	 * Process Interest packets
	 * If the packet is a directly connected client, it is processed</br>
	 * 
	 * slightly different
	 * @param intrestObj
	 * @param receivedFromNode
	 * @throws IOException
	 */
	public void processIntrest(IntrestObj intrestObj, String receivedFromNode) throws IOException {

		//check the cache
		String contentName = null;
		boolean copyFlag = false;
		boolean deleteFlag = false;

		if (intrestObj != null) {
			contentName = intrestObj.getContentName();
			Content requestedContent = ContentStore.serveRequest(contentName);
			if (requestedContent != null) {
				try {
					ContentStore.updateScoreOnIterface(requestedContent, receivedFromNode);
					if (ContentStore.shouldCopy(requestedContent, receivedFromNode)) {
						copyFlag = true;
					}
					if (ContentStore.shouldDelete(requestedContent)) {
						deleteFlag = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				ContentStore.sendDataObj(requestedContent, intrestObj.getOriginRouterName(), receivedFromNode, copyFlag);
				if (deleteFlag) {
					ContentStore.deleteContent(requestedContent);
				}
				return;
			}

		}
		System.out.println("Content name: " + contentName);



		if(intrestObj.getOriginRouterName().equals("") == true){
			//this is a client	
			PITEntry pitEntry = pit.addClientEntryIfItDoesntExist(intrestObj.getContentName());
			if(pitEntry != null){	

				//if the pit entry exists 

				//does the client requester exist in the client requesters list 
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
					//since the packet was from a client ... add this machines ID 
					//as the origin router
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
			//this is from another cache server
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

	/**
	 * Process a data packet with the flag set to zero</br>
	 * forward the data packet in accordance to the pit entry (normal routing)
	 * @param dataObj
	 */
	public void processData0(DataObj dataObj){

		//check the pit
		if(pit.doesEntryExist(dataObj.getContentName()) ==  true){

			//0
			//check the cs flag
			if (dataObj != null && dataObj.getCacheFlag() == 2) {
				String content = dataObj.getData();
				System.out.println(content);
				ContentStore.incomingContent(content, recievedFromNode);
				System.out.println("Content with name " + content + "is placed in cached");
				dataObj.setCacheFlag((byte) 1);
				sendPacket.createDataPacket(dataObj);

			}

			//update the pit entry time
			pit.setTime(dataObj.getContentName());

			ArrayList<String> requesters = pit.getRequesters(dataObj.getContentName()).getRequesters();

			// this can be added if needed but the pit removal thread works
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
			for(int i = 0; i < clientRequesters.size(); i++){
				System.out.println("in for loop");
				if(directlyConnectedNodes.doesDirectlyConnectedClientExist(clientRequesters.get(i)) == true){

					//forward the packet to each of the client requesters
					sendPacket.forwardPacket(dataObj.getOriginalPacket(), clientRequesters.get(i));

				}
			}
		}else{
			//if no pit entry exists ... drop the packet
			return;
		}
	}

	/**
	 * Process a data packet with the flag set to 1. </br>
	 * Route the data packet to the origin cache server</br>
	 * @param dataObj
	 */
	public void processData1(DataObj dataObj){

		/*
		 * add ttl to kill packet
		 */

		//1
		//if the origin router is this machine ... call process data 2
		//and send to all the pit requesters
		if(nodeRepo.getThisMachinesName().equals(dataObj.getOriginRouterName()) == true){

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

	/**
	 * Process a data packet with the flag set to 2.</br>
	 * Send the data packet towards the server
	 * @param dataObj
	 */
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


			//send to the cache server requesters
			for(int i = 0; i < requesters.size(); i++){
				if(nodeRepo.HMdoesNodeExist(requesters.get(i)) == true){
					dataObj.setFlag((byte)0);
					sendPacket.createDataPacket(dataObj);
					sendPacket.forwardPacket(dataObj.getOriginalPacket(), requesters.get(i));
				}
			}

			//send to the client requesters
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


	/**
	 * Process a ping interest packet
	 * 
	 * @param intrestObj
	 */
	public void preocessPingRequest(IntrestObj intrestObj){
		byte b = 0;
		String data = nodeRepo.getThisMachinesName() + "/ping";
		DataObj dataObj = new DataObj(intrestObj.getContentName(), 
				intrestObj.getOriginRouterName(), b, data, b, true);

		sendPacket.createDataPacket(dataObj);
		sendPacket.forwardPacket(dataObj.getOriginalPacket(), recievedFromNode);
	}


	/**
	 * Process a ping data packet
	 * @param dataObj
	 */
	public void processPingReply(DataObj dataObj){
		//print data portion of data obj
		System.out.println("ping response: " + dataObj.getData());
	}
}
