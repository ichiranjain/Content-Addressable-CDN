package caching;

import overlay.Message;
import overlay.ServerLinks;
import overlay.SocketContainer;
import packetObjects.IntrestObj;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by rushabhmehta91 on 5/4/15.
 */

public class ServerLFS implements Serializable {
    public static HashMap<String, SocketContainer> isConnected;
    public static ArrayList<String> deadCacheNodes;
    static HashMap<String, SocketContainer> listOfConnection;
    public HashMap<String, Content> store;
    public ArrayList<String> storeList;
    private String name;
    private Socket s;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;

    public static void main(String args[]) {

        Server o1 = new Server();
        o1.fillStore();
        o1.initialize();
        o1.connectNetwork();
        Server.advertise();

    }

    private void fillStore() {

    }

    private void addStore(String key, String value) {
        long size = value.length();
        ArrayList<Integer> trail = new ArrayList<Integer>();
        trail.add(-1);
        Content contentToBeInserted = new Content(key, trail, size, value);
        store.put(key, contentToBeInserted);
        storeList.add(key);
        advertiseNewlyAdded(key);
    }

    private void advertiseNewlyAdded(String key) {
        //write code to advertize single prefixObj
    }

    private void initialize() {
        listOfConnection = new HashMap<String, SocketContainer>();
        //listOfConnection.put("10.0.0.1", new SocketContainer(s,ois,oos));
        isConnected = new HashMap<String, SocketContainer>();
        deadCacheNodes = new ArrayList<String>();
    }


    private void connectNetwork() {

        for (String i : listOfConnection.keySet()) {
            try {
                Socket s = new Socket(i, 43125);
                Message m = new Message(400);
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(m);
                ois = new ObjectInputStream(s.getInputStream());
                ServerLinks link = new ServerLinks(i, ois);
                link.start();
                isConnected.put(i, new SocketContainer(s, ois, oos, link));
                advertise();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void advertise() {
        //write advertize code here
    }

    public void serveRequest(IntrestObj packet2) {
        String fileName = packet2.getContentName();
//        Integer interfaceId=packet2;
        if (store.containsKey(fileName)) {
            sendData(store.get(fileName));
//            try {
//                //place content returned
//                Content sendingData = updateScoreOnIterface(store.get(fileName), interfaceId); //packet type : 2 = incoming packet
//                if (sendingData != null) {
//                    sendData(sendingData);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
        } else {
            //sendData("Error 404");
        }


    }

    private void sendData(Content sendingData) {
        //write function to send data
    }

//    /**
//     * Update N score of the interface and check for interface score score if it is zero than initiale copy and delete
//     * depending to N score on rest all interface
//     *
//     * @param contentStoreCopy - content in content store
//     * @param interfaceId      - interface Id on which content is requested
//     * @return
//     * @throws Exception
//     */
//    private Content updateScoreOnIterface(Content contentStoreCopy, Integer interfaceId) throws Exception {
//        if (!contentStoreCopy.listofScoreOnInterfaces.containsKey(interfaceId)) {
//            contentStoreCopy.listofScoreOnInterfaces.put(interfaceId, contentStoreCopy.getMaxNScore());
//        } else {
//            contentStoreCopy.listofScoreOnInterfaces.replace(interfaceId, contentStoreCopy.listofScoreOnInterfaces.get(interfaceId) - 1);
//        }
//        boolean copyFlag = false;
//        if (contentStoreCopy.listofScoreOnInterfaces.get(interfaceId) == 0) {
//            copyFlag = true;
//        }
//        if (copyFlag) {
//            return copyContent(contentStoreCopy);
//        }
//
//        return null;
//    }
//
//    /**
//     * Reply in form of ContentPacket to incoming request
//     *
//     * @param fileName - name of the content
//     * @return ContentPacket
//     */
//    public ContentPacket replyContentRequest(String fileName) {
//
//        return new ContentPacket(1, store.get(fileName));
//    }
//
//    /**
//     * send content in the form of content packet
//     *
//     * @param content - content requested
//     * @return
//     */
//    private Content copyContent(Content content) {
//        return content;
//    }
//
//    /**
//     * delete content from current content store
//     *
//     * @param content - content requested
//     * @return
//     */
//    public boolean deleteContent(Content content) {
//        if (store.remove(content.getContentName()) != null) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }

}


