package topology;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import packetObjects.PacketObj;

public class RoutingQueueHandler implements Runnable{

	PacketQueue packetQueue;
	NodeRepository nodeRepo;
	FIB fib;
	PIT pit;
	DirectlyConnectedNodes directlyConnectedNodes;
	Parse parse;
	volatile boolean running;

	public RoutingQueueHandler(PacketQueue packetQueue, 
			NodeRepository nodeRepo, 
			FIB fib, 
			PIT pit, 
			DirectlyConnectedNodes directlyConnectedNodes) {

		this.packetQueue = packetQueue;
		this.nodeRepo = nodeRepo;
		this.fib = fib;
		this.pit = pit;
		this.directlyConnectedNodes = directlyConnectedNodes;
		parse = new Parse();
		running = true;
	}

	public void killRoutingHandler(){
		running = false;
	}

	@Override
	public void run() {

		ExecutorService executor = Executors.newFixedThreadPool(20);

		//loop endlessly
		while(running){

			//remove a packet from the queue
			//because this is a blocking queue, this will block until 
			//something is placed in the queue
			PacketObj packetObj = packetQueue.removeFromRoutingQueue();
			if(packetObj != null){
				//give to the thread pool for processing 
				//executer service == java's thread pool

				//Runnable worker = new WorkerThread(i);
				//WorkerThread worker = new WorkerThread(i);
				RoutingSwitch routingSwitch = new RoutingSwitch(packetObj.getPacket(), 
						fib,  
						pit, 
						directlyConnectedNodes,
						nodeRepo,
						packetQueue);
				executor.execute(routingSwitch);

			}//if the packet was null, drop it 


		}//end while loop

		executor.shutdown();
	}
}
