package topology;

import java.util.ArrayList;

public class MsgIDEntryDiscard implements Runnable{

	UpdateMsgsSeen updateMsgsSeen;

	//and remember keep smiling 
	volatile boolean keepSmiling;
	int sleepTime;
	long keepMsgTime;

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
