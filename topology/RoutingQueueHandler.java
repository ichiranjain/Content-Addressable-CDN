package topology;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import packetObjects.GenericPacketObj;

/**
 * This class takes objects from the routing queue and pass them to a thread pool </br>
 * where the objects are processed
 * @author spufflez
 *
 */
public class RoutingQueueHandler implements Runnable{

	PacketQueue2 packetQueue2;
	NodeRepository nodeRepo;
	FIB fib;
	PIT pit;
	DirectlyConnectedNodes directlyConnectedNodes;
	//Parse parse;
	volatile boolean running;

	/**
	 * Constructor
	 * @param packetQueue2
	 * @param nodeRepo
	 * @param fib
	 * @param pit
	 * @param directlyConnectedNodes
	 * @param running
	 */
	public RoutingQueueHandler(PacketQueue2 packetQueue2, 
			NodeRepository nodeRepo, 
			FIB fib, 
			PIT pit, 
			DirectlyConnectedNodes directlyConnectedNodes,
			boolean running) {

		this.packetQueue2 = packetQueue2;
		this.nodeRepo = nodeRepo;
		this.fib = fib;
		this.pit = pit;
		this.directlyConnectedNodes = directlyConnectedNodes;
		//parse = new Parse();
		this.running = running;
	}

	public void killRoutingHandler(){
		running = false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void run() {

		ExecutorService executor = Executors.newFixedThreadPool(20);

		//loop endlessly
		while(running){

			//remove a packet from the queue
			//because this is a blocking queue, this will block until 
			//something is placed in the queue
			GenericPacketObj genericPacketObj = packetQueue2.removeFromRoutingQueue();
			if(genericPacketObj != null){
				//System.out.println("recieved in routing handler");
				//give to the thread pool for processing 
				//executer service == java's thread pool

				//Runnable worker = new WorkerThread(i);
				//WorkerThread worker = new WorkerThread(i);
				RoutingSwitch routingSwitch = new RoutingSwitch(genericPacketObj, 
						fib,  
						pit, 
						directlyConnectedNodes,
						nodeRepo,
						packetQueue2);
				executor.execute(routingSwitch);

			}//if the packet was null, drop it 


		}//end while loop

		executor.shutdown();
	}
}
