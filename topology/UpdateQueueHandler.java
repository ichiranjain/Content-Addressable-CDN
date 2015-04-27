package topology;

import packetObjects.GenericPacketObj;
import packetObjects.PacketObj;

public class UpdateQueueHandler implements Runnable {
	PacketQueue2 packetQueue2;
	NodeRepository nodeRepo;
	FIB fib;
	DirectlyConnectedNodes directlyConnectedNodes;
	UpdateMsgsSeen updateMsgsSeen;

	//Parse parse;
	volatile boolean running;
	PacketObj packetObj;
	@SuppressWarnings("rawtypes")
	GenericPacketObj genericPacketObj;

	public UpdateQueueHandler(PacketQueue2 packetQueue2, 
			NodeRepository nodeRepo, 
			FIB fib, 
			DirectlyConnectedNodes directlyConnectedNodes,
			UpdateMsgsSeen updateMsgsSeen,
			boolean running) {

		this.packetQueue2 = packetQueue2;
		this.nodeRepo = nodeRepo;
		this.fib = fib;
		this.directlyConnectedNodes = directlyConnectedNodes;
		this.updateMsgsSeen = updateMsgsSeen;

		//parse = new Parse();
		this.running = running;
	}

	public void killUpdateHandler(){
		running = false;
	}

	@Override
	public void run() {

		//loop endlessly
		while(running){

			//remove a packet from the queue
			//because this is a blocking queue, this will block until 
			//something is placed in the queue
			//System.out.println("update thread blocking");
			genericPacketObj = packetQueue2.removeFromUpdateQueue();

			//System.out
			//	.println("Handling update queue packet::genericPacketObj::"
			//		+ genericPacketObj);

			//System.out.println("removed packet from update queue");
			if(genericPacketObj != null){
				//give to the thread pool for processing 
				//executer service == java's thread pool
				//System.out.println("packet recieved in update handler");
				Thread thread = new Thread(new UpdateSwitch(genericPacketObj, 
						nodeRepo, 
						fib, 
						directlyConnectedNodes, 
						updateMsgsSeen));
				thread.start();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}//if the packet was null, drop it 


		}//end while loop

	}
}
