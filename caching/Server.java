package caching;

import overlay.Message;
import overlay.ServerLinks;
import overlay.SocketContainer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

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

    public static void main(String args[]){

        Server o1 = new Server();
        o1.initialize();
        o1.connectNetwork();
        advertise();

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

    public static void serveRequest(){

    }



}
