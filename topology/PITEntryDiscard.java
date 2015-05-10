package topology;

import java.util.ArrayList;

public class PITEntryDiscard implements Runnable{

	PIT pit;
	int sleepTime;
	long keepMsgTime;
	volatile boolean keepRunning;


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
