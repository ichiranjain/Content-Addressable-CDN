package topology;

import packetObjects.PacketObj;

public class GeneralQueueHandler implements Runnable{

	//Parse parse;
	PacketQueue2 packetQueue2;
	volatile boolean running;
	GenericParser genericParser;

	public GeneralQueueHandler(PacketQueue2 packetQueue2, boolean running) {
		//parse = new Parse();
		this.packetQueue2 = packetQueue2;
		this.running = running;
		genericParser = new GenericParser(packetQueue2);
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
			PacketObj packetObj = packetQueue2.removeFromGeneralQueue();
			System.out.println("packet picked from general queue:: "
					+ packetObj.getPacket());
			if(packetObj != null){


				/*
				 * could parse the whole json string here
				 */
				//System.out.println("generic queue handler called parse");
				genericParser.parsePacket(packetObj);



				//				//place in the corresponding queue
				//				if(type.equals("update") == true){
				//					//place in the update queue
				//					packetQueue2.addToUpdateQueue(packetObj);
				//				}else{
				//					//place in the routing queue
				//					packetQueue2.addToRoutingQueue(packetObj);
				//				}
			}

		}//end while loop

	}

}
