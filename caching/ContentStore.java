package caching;

import java.util.HashMap;

/**
 * Created by rushabhmehta91 on 4/6/15.
 */
public class ContentStore {
    private HashMap<String, Content> store;
    private long storeSize = 999999;

    private void incomingRequest(String fileName, int interfaceId) throws Exception {
        if (store.containsKey(fileName)) {
            reply(fileName);
            requestProcessing(fileName, interfaceId);
        } else {
            forwardRequest(fileName);
//            return false;
        }
    }

    private void incomingContent() {

    }

    private Object reply(String fileName) {
        return store.get(fileName).getContentCache();
    }

    private void forwardRequest(String fileName) {

    }

    private void requestProcessing(String fileName, Integer interfaceId) throws Exception {
        Content contentStoreCopy = store.get(fileName);
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
            copyContent(interfaceId);
        }
        if (copyFlag && deleteFlag) {
            if (!deleteContent(fileName)) {
                throw new Exception("unable to delete content");
            }
        }
    }

    private boolean deleteContent(String fileName) {
        if (store.remove(fileName) != null) {
            return true;
        } else {
            return false;
        }

    }
}
