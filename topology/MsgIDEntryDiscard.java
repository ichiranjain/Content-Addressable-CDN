package topology;

import java.util.ArrayList;

/**
 * this class discards old message ID entries</br>
 * 
 * sleep time: is the amount of time in milliseconds the thread should sleep</br>
 * keepMsgTIme: is the amount of time in nano seconds the message entry should be kept </br> 
 * @author spufflez
 *
 */
public class MsgIDEntryDiscard implements Runnable{

	UpdateMsgsSeen updateMsgsSeen;

	//and remember keep smiling 
	volatile boolean keepSmiling;
	int sleepTime;
	long keepMsgTime;

	/**
	 * Constructor
	 * @param updateMsgsSeen
	 * @param sleepTime
	 * @param keepMsgTime
	 * @param keepRunning
	 */
	public MsgIDEntryDiscard(UpdateMsgsSeen updateMsgsSeen, int sleepTime, long keepMsgTime, boolean keepRunning){
		this.updateMsgsSeen = updateMsgsSeen;
		this.sleepTime = sleepTime;
		this.keepMsgTime = keepMsgTime;
		this.keepSmiling = keepRunning;
	}

	@Override
	public void run() {

		while(keepSmiling){

			ArrayList<String> msgIDs = updateMsgsSeen.getListOfMsgIDs();
			for(int i = 0; i < msgIDs.size(); i++){
				if((System.nanoTime() - updateMsgsSeen.getMsgIDTime(msgIDs.get(i))) >= keepMsgTime){
					updateMsgsSeen.removeMsgID(msgIDs.get(i));
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

	public void stopRunning() {
		keepSmiling = false;
	}

}
