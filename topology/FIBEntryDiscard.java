package topology;

import java.util.ArrayList;

/**
 * This class removes FIB entries who's nodes have been removed from the graph.</br>
 * sleep time: determines how long the thread should sleep for in milliseconds</br>
 * keep running : is a boolean that is checked, this boolean if set to false </br>
 * by the main will cause this thread to terminate
 * @author spufflez
 *
 */
public class FIBEntryDiscard implements Runnable{

	FIB fib;
	int sleepTime;
	volatile boolean keepRunning;
	NodeRepository nodeRepo;

	/**
	 * Constructor
	 * @param fib
	 * @param nodeRepo
	 * @param sleepTime
	 * @param keepRunning
	 */
	public FIBEntryDiscard(FIB fib, NodeRepository nodeRepo, int sleepTime, boolean keepRunning) {
		this.fib = fib;
		this.sleepTime = sleepTime;
		this.nodeRepo = nodeRepo;
		this.keepRunning = keepRunning;

	}

	@Override
	public void run() {
		while(keepRunning){

			//System.out.println("fib discard");
			int longestPrefixLength = fib.getLongestPrefixLength();
			for(int i = 1; i <= longestPrefixLength; i++){
				if(fib.doesPrefixLengthHashMapExist(i) == true){
					ArrayList<String> prefixList = fib.getPrefixesForLength(i);
					for(int j = 0; j < prefixList.size(); j++){						
						if(fib.doesHashMapContainPrefix(i, prefixList.get(j)) == true){

							//when get best cost advertiser is called the value it returns is checked 
							//to make sure the node exists before returning it
							//if the node does not exist, the function removes it 
							//System.out.println("fib discard running");
							fib.getBestCostAdvertiser(i, prefixList.get(j));
						}
					}
				}
			}

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	public void stopRuning(){
		keepRunning = false;
	}

}
