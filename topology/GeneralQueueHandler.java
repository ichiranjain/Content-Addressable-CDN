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

			if(packetObj != null){

				genericParser.parsePacket(packetObj);

			}

		}//end while loop

	}

}
