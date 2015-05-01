package caching;

import overlay.Message;
import overlay.ServerLinks;
import overlay.SocketContainer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import packetObjects.IntrestObj;

/**
 * Created by Chiran on 4/26/15.
 */
public class Server implements Serializable{
    private Socket s;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    static  HashMap<String, SocketContainer> listOfConnection;
    public static HashMap<String, SocketContainer> isConnected;
    public static ArrayList<String> deadCacheNodes;
    public HashMap<String, Content> store;

    public static void main(String args[]){

        Server o1 = new Server();
        o1.fillStore();
        o1.initialize();
        o1.connectNetwork();
        advertise();

    }

    private void fillStore() {
		
	}

	private void initialize() {
        listOfConnection = new HashMap<String, SocketContainer>();
        //listOfConnection.put("10.0.0.1", new SocketContainer(s,ois,oos));
        isConnected = new HashMap<String, SocketContainer>();
        deadCacheNodes = new ArrayList<String>();
    }

    private static void advertise() {

        

    }

    private void connectNetwork() {

        for(String i: listOfConnection.keySet()){
            try{
                Socket s = new Socket(i, 43125);
                Message m = new Message(400);
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(m);
                ois = new ObjectInputStream(s.getInputStream());
                ServerLinks link = new ServerLinks(i, ois);
                link.start();
                isConnected.put(i, new SocketContainer(s,ois,oos,link));
                advertise();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void serveRequest(IntrestObj packet2){
    	  String fileName = packet2.getContentName();
          if (store.containsKey(fileName)) {
              try {
                  //place content returned
                  ContentPacket packet = updateScoreOnIterface(store.get(fileName), interfaceId); //packet type : 2 = incoming packet
                  if (packet != null) {
                      return packet;
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              }
              return replyContentRequest(fileName);//packet type : 1 = reply
          } else {
              return null;
          }
    	

    }



}
