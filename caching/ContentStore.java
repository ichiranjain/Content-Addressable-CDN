package caching;

import overlay.Peer;
import packetObjects.DataObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;
import topology.SendPacket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rushabhmehta91 on 4/6/15.
 */
public class ContentStore {

    public static HashMap<String, Content> store;
    public static ArrayList<String> storeList;
    public static SendPacket sendPacketObj;
    static Runtime r = Runtime.getRuntime();
    private static ObjectOutputStream oos = null;
    private static ObjectInputStream ois = null;

    static {
        storeList = new ArrayList<String>();
        store = new HashMap<String, Content>();

    }


    public static Content serveRequest(String fileName) {
        //        String fileName = packet2.getContentName();
        //        Integer interfaceId=packet2;
        if (store.containsKey(fileName)) {
            System.out.println("Request content found!!!!!");
            return store.get(fileName);
        } else {
            System.out.println("Request content not found on server. sending 404");
            return null;
        }


    }

    public static void sendDataObj(Content sendingContent, String originRouter, String receivedFromNode, boolean copyFlag) {
        System.out.println("Sending requested content");
        byte copyFlagValue;
        if (copyFlag) {
            copyFlagValue = (byte) 2;
        } else {
            copyFlagValue = (byte) 1;
        }
        DataObj dataObj = new DataObj(sendingContent.getContentName(), originRouter, (byte) 0, convertContentToString(sendingContent), copyFlagValue, true);
        sendPacketObj.createDataPacket(dataObj);
        sendPacketObj.forwardPacket(dataObj.getOriginalPacket(), receivedFromNode);
    }

    /**
     * Update N score of the interface and check for interface score score if it is zero than initiale copy and delete
     * depending to N score on rest all interface
     *
     * @param contentStoreCopy - content in content store
     * @param interfaceId      - interface Id on which content is requested
     * @return
     * @throws Exception
     */
    public static void updateScoreOnIterface(Content contentStoreCopy, String interfaceId) throws Exception {
        System.out.println("updating score");
        if (!contentStoreCopy.listofScoreOnInterfaces.containsKey(interfaceId)) {
            contentStoreCopy.listofScoreOnInterfaces.put(interfaceId, contentStoreCopy.getMaxNScore());
        } else {
            contentStoreCopy.listofScoreOnInterfaces.replace(interfaceId, contentStoreCopy.listofScoreOnInterfaces.get(interfaceId) - 1);
        }
    }

    public static boolean shouldCopy(Content contentStoreCopy, String interfaceId) {
        boolean copyFlag = false;
        if (contentStoreCopy.listofScoreOnInterfaces.get(interfaceId) == 0) {
            copyFlag = true;
        }
        return copyFlag;
    }

    public static boolean shouldDelete(Content contentStoreCopy, String interfaceId) {
        boolean deleteFlag = true;
        for (String index : contentStoreCopy.listofScoreOnInterfaces.keySet()) {
            if (contentStoreCopy.listofScoreOnInterfaces.get(index) < contentStoreCopy.getMaxNScore() / 2) {
                deleteFlag = false;
            }
        }
        return deleteFlag;
    }

    public static String convertContentToString(Content myObject) {
        String serializedObject = "";

        // serialize the object
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(myObject);
            so.flush();
            serializedObject = bo.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return serializedObject;
    }

    public static Content convertStringToContent(String serializedObject) {
        Content contentObj = null;
        try {
            // deserialize the object
            byte b[] = serializedObject.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            contentObj = (Content) si.readObject();
        } catch (Exception e) {
            System.out.println(e);
        }
        return contentObj;
    }

    /**
     * When incoming packet have content which to be stored in content store than check the size of the the store
     * if store has required size then place it in store else replace.
     *
     * @param packet - incoming packet
     * @param packet
     * @return
     */
    public static boolean incomingContent(String packet) {
        System.out.println("incoming content received");
        Content receivedContent = convertStringToContent(packet);
        if (receivedContent.getSizeInBytes() <= r.freeMemory()) {
            return place(receivedContent);
        } else {
            return replace(receivedContent);
        }
    }

    /**
     * If content store has no space then replace the least recently used content from content store with new content
     *
     * @param receivedContent
     * @return
     */
    private static boolean replace(Content receivedContent) {
        return false;
    }

    /**
     * Place the incoming content in the store. If content is in the store than replace the content else just add the
     * content in the store
     *
     * @param receivedContent - incoming content
     * @return
     */
    public static boolean place(Content receivedContent) {
        if (!store.containsKey(receivedContent.getContentName())) {
            store.put(receivedContent.getContentName(), receivedContent);
            System.out.println("content placed");
            try {
                advertiseNewlyAdded(receivedContent);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            if (store.replace(receivedContent.getContentName(), receivedContent) != null) {
                return true;
            } else {
                return false;
            }
        }

    }

    private static void fillStore() {
        Content c1 = new Content("firstContent", null, 200, "updatedSecondContent1");
        Content c2 = new Content("secondContent", null, 200, "updatedSecondContent2");
        Content c3 = new Content("thirdContent", null, 200, "updatedSecondContent3");
        Content c4 = new Content("forthContent", null, 200, "updatedSecondContent4");
        storeList.add(c1.getContentName());
        storeList.add(c2.getContentName());
        storeList.add(c3.getContentName());
        storeList.add(c4.getContentName());
        store.put(c1.getContentName(), c1);
        store.put(c2.getContentName(), c2);
        store.put(c3.getContentName(), c3);
        store.put(c4.getContentName(), c4);
    }

    private static void advertise(ArrayList<String> contentList,
                                  String cacheServerAddress) throws UnknownHostException {

        PrefixListObj list = new PrefixListObj(contentList,
                Peer.generateID(Peer.getIP(Peer.IP)) + "", true,
                Peer.generateID(Peer.getIP(Peer.IP)) + System.nanoTime() + "");
        //sendPacketObj.createPrefixListPacket(list);
        sendPacketObj.createClientPrefixList(list);
        sendPacketObj.forwardPacket(list.getOriginalPacket(), cacheServerAddress);
    }

    private static void advertiseNewlyAdded(Content content)
            throws UnknownHostException {
        System.out.println("advertizing newly added content");
        //write code to advertize single prefixObj
        PrefixObj list = new PrefixObj(content.getContentName(),
                Peer.generateID(Peer.getIP(Peer.IP)) + System.nanoTime() + "",
                Peer.generateID(Peer.getIP(Peer.IP)) + "", true);
        //sendPacketObj.createPrefixPacket(list);
        sendPacketObj.createClientPrefix(list);
        for (String e : Peer.neighbors.keySet()) {
            sendPacketObj.forwardPacket(list.getOriginalPacket(), e);
        }
    }

    /**
     * delete content from current content store
     *
     * @param content - content requested
     * @return
     */
    public boolean deleteContent(Content content) {
        if (store.remove(content.getContentName()) != null) {
            return true;
        } else {
            return false;
        }

        }
}
