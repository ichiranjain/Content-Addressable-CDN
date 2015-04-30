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

		//check the pit
		//System.out.println(intrestObj.getContentName());
		//System.out.println("does entry exist: " + pit.doesEntryExist(intrestObj.getContentName()));
		//if(pit.doesEntryExist(intrestObj.getContentName()) == true){
		PITEntry pitEntry = pit.addEntryIfItDoesntExist(intrestObj.getContentName(), recievedFromNode);

		//if the entry is not null, it means someone already requested this content, so add the new requester
		if(pitEntry != null){	

			if(pit.doesRequesterExist(intrestObj.getContentName(), recievedFromNode) == false){
				pit.addRequester(intrestObj.getContentName(), recievedFromNode);
			}


			//			if(pit.doesRequesterExist(intrestObj.getContentName(), recievedFromNode) == true){
			//				
			//			}
			//30000000000L == 30sec in nano time 
			//if 20 sec has not elapsed do not resend
			//			if(System.nanoTime() - pit.getTime(intrestObj.getContentName()) < 20000000000L ){
			//				return;				
			//			}else{
			//				pit.setTime(intrestObj.getContentName());

			//			}
		}
		//		else{
		//			pit.addEntry(intrestObj.getContentName(), recievedFromNode);
		//		}

		String nextHop = fib.searchFIB(intrestObj.getContentName());

		if(nextHop.equals("broadCast") == true){
			//broad cast
			sendPacket.forwardToAllRouters(intrestObj.getOriginalPacket());

		}else if(!nextHop.equals("") == true){
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
		//check the cs flag
		if(dataObj.getCacheFlag() == 2){
			//pass to CS
			//return
		}

		//check the pit
		if(pit.doesEntryExist(dataObj.getContentName()) ==  true){
			ArrayList<String> requesters = pit.getRequesters(dataObj.getContentName()).getRequesters();

			//remove the pit entry if this was the last chunk packet 
			if(dataObj.getLastChunk() == true){
				pit.removeEntry(dataObj.getContentName());
			}

			boolean alternativePathUsed = false;
			for(int i = 0; i < requesters.size(); i++){

				//requesters are always directly connected
				//does the requester (next hop node) exist 
				//if requester is down ... set to 1 AND boolean is not set 
				if((nodeRepo.HMdoesNodeExist(requesters.get(i)) == true) || 
						(directlyConnectedNodes.doesDirectlyConnectedClientExist(requesters.get(i)) == true) ){

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
			//if no pit entry exists ... drop the packet
			return;
		}
	}

	public void processData1(DataObj dataObj){

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

			//send to all the requesters
			ArrayList<String> requesters = pit.getRequesters(dataObj.getContentName()).getRequesters();

			//remove the pit entry if this was the last chunk
			if(dataObj.getLastChunk() == true){
				pit.removeEntry(dataObj.getContentName());
			}

			for(int i = 0; i < requesters.size(); i++){
				sendPacket.createDataPacket(dataObj);
				sendPacket.forwardPacket(dataObj.getOriginalPacket(), requesters.get(i));
			}


			//forward towards the server 
			//String nextHop = checkFIB(dataObj.getContentName());
			String nextHop = fib.searchFIB(dataObj.getContentName());
			//sendPacket
			sendPacket.forwardPacket(dataObj.getOriginalPacket(), nextHop);

		}else{
			//drop the packet
		}
	}
}
