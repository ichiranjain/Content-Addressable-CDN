package topology;

import packetObjects.PacketObj;

public class GeneralQueueHandler implements Runnable{

	Parse parse;
	PacketQueue packetQueue;
	volatile boolean running;

	public GeneralQueueHandler(PacketQueue packetQueue) {
		parse = new Parse();
		this.packetQueue = packetQueue;
		running = true;
	}

	public void killGeneralHandler(){
		running = false;
	}

	@Override
	public void run() {

		//loop endlessly
		while(running){

			//remove a packet from the queue
			//because this is a blocking queue, this will block until 
			//something is placed in the queue
			PacketObj packetObj = packetQueue.removeFromGeneralQueue();
			if(packetObj != null){

				//parse the type field of the packet
				String type = parse.parseType(packetObj.getPacket());

				/*
				 * could parse the whole json string here
				 */

				//place in the corresponding queue
				if(type.equals("update") == true){
					//place in the update queue
					packetQueue.addToUpdateQueue(packetObj);
				}else{
					//place in the routing queue
					packetQueue.addToRoutingQueue(packetObj);
				}
			}

		}//end while loop

	}

}
