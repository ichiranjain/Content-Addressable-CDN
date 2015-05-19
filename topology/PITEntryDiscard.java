package topology;

import java.util.ArrayList;

/**
 * This class removes old PIT entries that have been in the table to long</br>
 * sleep time: is the amount of time in milliseconds the thread will sleep</br>
 * keepMsgTime: is the amount of the time the PIT entry is allowed to stay in the table</br>
 * @author spufflez
 *
 */
public class PITEntryDiscard implements Runnable{

	PIT pit;
	int sleepTime;
	long keepMsgTime;
	volatile boolean keepRunning;


	/**
	 * Constructor
	 * @param pit
	 * @param sleepTime
	 * @param keepMsgTime
	 * @param keepRunning
	 */
	public PITEntryDiscard(PIT pit, int sleepTime, long keepMsgTime, boolean keepRunning) {
		this.pit = pit;
		this.sleepTime = sleepTime;
		this.keepMsgTime = keepMsgTime;
		this.keepRunning = keepRunning;
	}

	@Override
	public void run() {
		while(keepRunning){

			ArrayList<String> pitEntries = pit.getPitEntries();
			for(int i = 0; i < pitEntries.size(); i++){
				if((System.nanoTime() - pit.getTime(pitEntries.get(i))) >= keepMsgTime){
					pit.removeEntry(pitEntries.get(i));
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
