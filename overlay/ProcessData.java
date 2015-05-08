package overlay;

import java.util.concurrent.ConcurrentHashMap;

import packetObjects.DataObj;
import packetObjects.GenericPacketObj;

public class ProcessData extends Thread {
	ConcurrentHashMap<String, Long> rtt;

	public ProcessData(ConcurrentHashMap<String, Long> rtt) {
		this.rtt = rtt;
		System.out.println("client process data constructer:");
	}

	@Override
	public void run() {
		while (true) {
			GenericPacketObj<DataObj> gpo = Client.pq2.removeFromRoutingQueue();
			DataObj dataObj = null;
			System.out.println("client process data action: " + gpo.getAction());
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

		//used for rtt, can be removed
		if(rtt.containsKey(contentName) == true){
			System.out.println("rtt mili seconds: " + (System.currentTimeMillis() - rtt.get(contentName)) );
		}
		System.out.println("Content name: " + contentName);
	}
}
