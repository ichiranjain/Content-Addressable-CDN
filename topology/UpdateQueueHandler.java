package topology;

import packetObjects.GenericPacketObj;
import packetObjects.PacketObj;

/**
 * This class pulls update objects from the queue and calls update switch</br>
 * This thread starts a thread to process the update
 * @author spufflez
 *
 */
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

	/**
	 * Constructor
	 * @param packetQueue2
	 * @param nodeRepo
	 * @param fib
	 * @param directlyConnectedNodes
	 * @param updateMsgsSeen
	 * @param running
	 */
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
			genericPacketObj = packetQueue2.removeFromUpdateQueue();

			if(genericPacketObj != null){
				//give to the thread pool for processing 
				//executer service == java's thread pool
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
