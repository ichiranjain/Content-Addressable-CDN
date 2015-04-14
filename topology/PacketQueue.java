package topology;

import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ConcurrentLinkedQueue;

import packetObjects.PacketObj;


public class PacketQueue {
	//ConcurrentLinkedQueue<PacketObj> clq = new ConcurrentLinkedQueue<PacketObj>();
	ArrayBlockingQueue<PacketObj> generalQueue;
	ArrayBlockingQueue<PacketObj> updateQueue;
	ArrayBlockingQueue<PacketObj> routingQueue;


	public PacketQueue(){

		generalQueue  = new ArrayBlockingQueue<PacketObj>(100, true);
		updateQueue = new ArrayBlockingQueue<PacketObj>(100, true);
		routingQueue = new ArrayBlockingQueue<PacketObj>(100, true);

	}

	public void addToGeneralQueue(PacketObj packet){
		try {
			generalQueue.put(packet);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public PacketObj removeFromGeneralQueue(){
		try {
			return generalQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isGeneralQueueEmpty(){
		return generalQueue.isEmpty();
	}

	public void addToUpdateQueue(PacketObj packet){
		try {
			updateQueue.put(packet);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public PacketObj removeFromUpdateQueue(){
		try {
			return updateQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isUpdateQueueEmpty(){
		return updateQueue.isEmpty();
	}

	public void addToRoutingQueue(PacketObj packet){
		try {
			routingQueue.put(packet);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public PacketObj removeFromRoutingQueue(){
		try {
			return routingQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isRoutingQueueEmpty(){
		return routingQueue.isEmpty();
	}
}
