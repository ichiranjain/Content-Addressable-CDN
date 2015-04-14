package caching;

import java.util.HashMap;

/**
 * Created by rushabhmehta91 on 4/6/15.
 */
public class ContentStore {
    private HashMap<String, Content> store;
    private long storeSize = 999999;

    private Object packetHandler(ContentPacket packet, int interfaceId) {
        try {
            switch (packet.getIncomingPacketType()) {
                case 0:
                    return incomingContentRequest((String) packet.getData(), interfaceId);
                case 1:
                    return incomingReplyContent(packet);
                case 2:
                    return incomingContent(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private ContentPacket incomingContentRequest(String fileName, int interfaceId) throws Exception {
        if (store.containsKey(fileName)) {
            updateScoreOnIterface(store.get(fileName), interfaceId);
            return replyContentRequest(fileName);
        } else {
            return null;
        }
    }


    private Content incomingReplyContent(ContentPacket packet) {
        return (Content) packet.getData();
    }

    private boolean incomingContent(ContentPacket packet) {
        Content receivedContent = (Content) packet.getData();
        if (store.put(receivedContent.getContentName(), receivedContent) != null) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Reply in form of ContentPacket to incoming request
     *
     * @param fileName - name of the content
     * @return ContentPacket
     */
    private ContentPacket replyContentRequest(String fileName) {

        return new ContentPacket(1, store.get(fileName));
    }


    private ContentPacket updateScoreOnIterface(Content contentStoreCopy, Integer interfaceId) throws Exception {
        if (!contentStoreCopy.listofScoreOnInterfaces.containsKey(interfaceId)) {
            contentStoreCopy.listofScoreOnInterfaces.put(interfaceId, contentStoreCopy.getMaxNScore());
        } else {
            contentStoreCopy.listofScoreOnInterfaces.replace(interfaceId, contentStoreCopy.listofScoreOnInterfaces.get(interfaceId) - 1);
        }
        boolean copyFlag = false;
        boolean deleteFlag = true;
        for (Integer index : contentStoreCopy.listofScoreOnInterfaces.keySet()) {
            if (contentStoreCopy.listofScoreOnInterfaces.get(index) == 0) {
                copyFlag = true;
            } else {
                if (contentStoreCopy.listofScoreOnInterfaces.get(index) < contentStoreCopy.getMaxNScore() / 2) {
                    deleteFlag = false;
                }
            }
        }

        if (copyFlag) {
            return copyContent(contentStoreCopy);
        }
        if (copyFlag && deleteFlag) {
            if (!deleteContent(contentStoreCopy)) {
                throw new Exception("unable to delete content");
            }
        }
        return null;
    }

    private ContentPacket copyContent(Content content) {
        return new ContentPacket(3,content);
    }

    private boolean deleteContent(Content content) {
        if (store.remove(content.getContentName()) != null) {
            return true;
        } else {
            return false;
        }

    }
}
