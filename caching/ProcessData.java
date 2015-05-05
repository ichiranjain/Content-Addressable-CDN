package caching;

import packetObjects.IntrestObj;
import packetObjects.GenericPacketObj;

public class ProcessData extends Thread {

    public ProcessData() {
        System.out.println("server process data constructer:");
    }

    @Override
    public void run() {
        while (true) {
            GenericPacketObj<IntrestObj> gpo = ServerLFS.pq2.removeFromRoutingQueue();
            IntrestObj intrestObj = null;
            System.out.println("server process data action: " + gpo.getAction());
            String receivedFromNode = gpo.getRecievedFromNode();
            if (gpo.getAction().equals("intrest")) {
                intrestObj = (IntrestObj) gpo.getObj();

            } else {
                intrestObj = null;

            }
            if (intrestObj == null) {
                continue;
            }

            processIntrestObj(intrestObj,receivedFromNode);
        }
    }

    public void processIntrestObj(IntrestObj intrestObj, String receivedFromNode) {
        String contentName = null;
        if (intrestObj != null) {
            contentName = intrestObj.getContentName();
            Content requestedContent = ServerLFS.serveRequest(contentName);
            ServerLFS.sendDataObj(requestedContent,receivedFromNode,intrestObj.getOriginRouterName());
        }
        System.out.println("Content name: " + contentName);
    }
}