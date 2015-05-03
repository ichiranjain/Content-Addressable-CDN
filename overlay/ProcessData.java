package overlay;

import packetObjects.DataObj;
import packetObjects.GenericPacketObj;

public class ProcessData extends Thread {

	public ProcessData() {

	}

	public void run() {
		while (true) {
			GenericPacketObj<DataObj> gpo = Client.pq2.removeFromRoutingQueue();
			DataObj dataObj = null;
			switch (gpo.getAction()) {
			case "data":
				dataObj = (DataObj) gpo.getObj();
				break;
			default:
				dataObj = null;
				break;
			}
			if (dataObj == null) {
				continue;
			}
			processDataObj(dataObj);
		}
	}

	public void processDataObj(DataObj dataObj) {
		String contentName = null;
		if (dataObj != null) {
			contentName = dataObj.getContentName();
		}
		System.out.println("Content name: " + contentName);
	}
}
