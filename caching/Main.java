package caching;

import java.util.HashMap;

public class Main {

    public static ContentStore storeObj = new ContentStore();
    static Main m = new Main();


    public static void main(String[] args) {

        storeObj.store = new HashMap<String, Content>();
        Content c1 = new Content("firstContent", null, 200, "updatedSecondContent1");
        Content c2 = new Content("secondContent", null, 200, "updatedSecondContent2");
        Content c3 = new Content("thirdContent", null, 200, "updatedSecondContent3");
        Content c4 = new Content("forthContent", null, 200, "updatedSecondContent4");
        m.fillContentStore(c1);
        m.fillContentStore(c2);
        m.printStore();
        System.out.println();
        m.delete(c1);
        m.printStore();
        System.out.println();
        m.testReplyContentRequest(c2);
        System.out.println();
        m.testPlace(c3);
        m.testPlace(c4);
        ContentPacket cp1 = new ContentPacket(2, c1);
        m.testIncomingContent(cp1);
        m.testIncomingReplyContent(cp1);


    }

    private void fillContentStore(Content c) {
        if (c != null)
            storeObj.store.put(c.getContentName(), c);

    }

    private void delete(Content c) {
        storeObj.deleteContent(c);
    }


    private void printStore() {
        for (String s : storeObj.store.keySet()) {
            System.out.println(s + " " + storeObj.store.get(s));
        }
    }

    private void testReplyContentRequest(Content c) {
        if (storeObj != null) {
            ContentPacket cp = storeObj.replyContentRequest(c.getContentName());
            System.out.println(cp.toString(cp));
        }
    }

    private void testPlace(Content receivedContent) {
        if (storeObj.place(receivedContent)) {
            m.printStore();
            System.out.println();
        } else {
            System.out.println("Not placed " + receivedContent.getContentName());
        }
    }

    private void testIncomingContent(ContentPacket cp) {
        if (storeObj.incomingContent(cp)) {
            m.printStore();
            System.out.println();
        }

    }

    private void testIncomingReplyContent(ContentPacket cp) {
        Content c = storeObj.incomingReplyContent(cp);
        System.out.println(c.getContentName());
        System.out.println();
    }

    private void testUpdateScoreOnIterface(){
    	
    }
}
