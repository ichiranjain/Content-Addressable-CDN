package caching;

import overlay.Message;
import overlay.ServerLinks;
import overlay.SocketContainer;
import packetObjects.DataObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;
import topology.GeneralQueueHandler;
import topology.PacketQueue2;
import topology.SendPacket;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


/**
 * Created by rushabhmehta91 on 5/4/15.
 */
public class ServerLFS implements Serializable {
    public static HashMap<String, SocketContainer> isConnected;
    public static ArrayList<String> deadCacheNodes;
    public static HashMap<String, SocketContainer> listOfConnection;
    public static HashMap<String, Content> store;
    public static ArrayList<String> storeList;
    public static SendPacket sendPacketObj;
    public static String ID;
    public static String serverNameID;
    public static GeneralQueueHandler gqh;
    public static PacketQueue2 pq2;
    public static ProcessData pd;
    static Runtime r = Runtime.getRuntime();
    private static ObjectOutputStream oos = null;
    private static ObjectInputStream ois = null;

    public static void main(String args[]) {
        ServerLFS s1 = new ServerLFS();
        try {
            serverNameID = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        s1.fillStore();
        s1.initialize();
        s1.connectNetwork();

    }


    public static long generateID(String IP) throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        if (!IP.equals("")) {
            hostAddress = IP;
        }
        hostAddress = getIP(hostAddress);
        System.out.println("Generating ID... (" + hostAddress + ")");
        long prime1 = 105137;
        long prime2 = 179422891;
        long ID = 0;
        for (int i = 0; i < hostAddress.length(); i++) {
            char c = hostAddress.charAt(i);
            if (c != '.') {
                ID += (prime1 * hostAddress.charAt(i)) % prime2;
            }
        }
        System.out.println("ID: " + ID);

        return ID;
    }

    public static String getIP(String port) {
        int i = 0;
        int slash = 0;
        int end = port.length();
        for (; i < port.length(); i++) {
            if (port.charAt(i) == '/') {
                slash = i + 1;
            } else if (port.charAt(i) == ':') {
                end = i;
                break;
            }
        }
        return port.substring(slash, end);
    }

    @SuppressWarnings("rawtypes")
    public static boolean sendMessage(Message m) {
        try {
            oos.writeObject(m);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Method to be called by upper layers to send a message to a particular<br/>
     * neighbor.<br/>
     * <p/>
     * Message type should be set to 7.
     *
     * @param ID
     * @param m
     * @return
     */
    public static boolean sendMessage(String ID, Message m) {
        try {
            SocketContainer sc = listOfConnection.get(ID);
            sc.oos.writeObject(m);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Method to be called by upper layers to send a message to a list of<br/>
     * neighbors.<br/>
     * <p/>
     * Message type should be set to 7.
     *
     * @param IDs
     * @param m
     * @return
     * @throws IOException
     */
    public static boolean sendMessage(List<String> IDs, Message m)
            throws IOException {
        for (String id : IDs) {
            if (!sendMessage(id, m)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to be called by upper layers to send a message to all<br/>
     * neighbors except ID.<br/>
     * <p/>
     * Message type should be set to 7.
     *
     * @param ID
     * @param m
     * @return
     * @throws IOException
     */
    public static boolean sendMessageToAllBut(String ID, Message m)
            throws IOException {
        List<String> IDs = new ArrayList<String>(listOfConnection.keySet());
        for (String id : IDs) {
            if (!("" + ID).equals(id)) {
                if (!sendMessage(id, m)) {
                    return false;
                }
            }
        }
        return true;
    }


    public static Content serveRequest(String fileName) {
//        String fileName = packet2.getContentName();
//        Integer interfaceId=packet2;
        if (store.containsKey(fileName)) {
            System.out.println("Request content found!!!!!");
            return store.get(fileName);
//            sendData(store.get(fileName));
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
            System.out.println("Request content not found on server. sending 404");
            return null;
        }


    }

    public static void sendDataObj(Content sendingContent, String fromNode, String receivedFromNode, boolean copyFlag) {
        System.out.println("Sending requested content");
        byte copyFlagValue;
        if (copyFlag) {
            copyFlagValue = (byte) 2;
        } else {
            copyFlagValue = (byte) 1;
        }
        DataObj dataObj = new DataObj(sendingContent.getContentName(), fromNode, (byte) 1, convertContentToString(sendingContent), copyFlagValue, true);
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
            return true;
        } else {
            if (store.replace(receivedContent.getContentName(), receivedContent) != null) {
                return true;
            } else {
                return false;
            }
        }

    }

    private void fillStore() {
        Content c1 = new Content("firstContent", null, 200, "updatedSecondContent1");
        Content c2 = new Content("secondContent", null, 200, "updatedSecondContent2");
        Content c3 = new Content("thirdContent", null, 200, "updatedSecondContent3");
        Content c4 = new Content("forthContent", null, 200, "updatedSecondContent4");
        store.put(c1.getContentName(), c1);
        store.put(c2.getContentName(), c2);
        store.put(c3.getContentName(), c3);
        store.put(c4.getContentName(), c4);
    }

    private void addContentToStore(String key, String value) {
        long size = value.length();
        ArrayList<Integer> trail = new ArrayList<Integer>();
        trail.add(-1);
        Content contentToBeInserted = new Content(key, trail, size, value);
        store.put(key, contentToBeInserted);
        storeList.add(key);
        advertiseNewlyAdded(contentToBeInserted);
    }

    private void advertise(ArrayList<String> contentList, String cacheServerAddress) {

        PrefixListObj list = new PrefixListObj(contentList, serverNameID, true, serverNameID + System.nanoTime());
        sendPacketObj.createPrefixListPacket(list);
        sendPacketObj.forwardPacket(list.getOriginalPacket(), cacheServerAddress);

    }

    private void advertiseNewlyAdded(Content content) {
        //write code to advertize single prefixObj
        PrefixObj list = new PrefixObj(content.getContentName(), serverNameID, serverNameID + System.nanoTime(), true);
        sendPacketObj.createPrefixPacket(list);
        for (String e : listOfConnection.keySet()) {
            sendPacketObj.forwardPacket(list.getOriginalPacket(), e);
        }


    }


    ///only required for content server

    private void initialize() {
        listOfConnection = new HashMap<String, SocketContainer>();
        isConnected = new HashMap<String, SocketContainer>();
        deadCacheNodes = new ArrayList<String>();
    }

    private void connectNetwork() {
        Scanner sc = new Scanner(System.in);
        sendPacketObj = new SendPacket();
        boolean serverStarted = true;
        boolean connected = false;

        pq2 = new PacketQueue2();
        gqh = new GeneralQueueHandler(pq2, true);
        Thread gqhThread = new Thread(gqh);
        gqhThread.start();
        pd = new ProcessData();
        pd.start();

        while (serverStarted) {
            while (!connected) {
                try {
                    System.out.print("Enter cache server to connect to: ");
                    String cacheServerAddress = sc.nextLine();
                    Socket cacheServer = null;
                    try {
                        cacheServer = new Socket(cacheServerAddress, 43125);
                        ois = new ObjectInputStream(cacheServer.getInputStream());
                        oos = new ObjectOutputStream(cacheServer.getOutputStream());
                        Message<String> joinMessage = new Message<String>(400); // handle
                        // in
                        // Peer
                        oos.writeObject(joinMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ServerLinks link = new ServerLinks(cacheServerAddress, ois);
                    link.start();
                    ID = generateID(getIP(cacheServerAddress)) + "";
                    connected = true;
                    // oos.writeObject("joining client");
                    isConnected.put(cacheServerAddress, new SocketContainer(cacheServer, ois, oos, link));
                    advertise(storeList, cacheServerAddress);
                } catch (UnknownHostException e) {
                    System.out.println("Connection error.. Please try again..");
                }
            }
        }
    }



}


