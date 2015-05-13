package caching;

import java.util.HashMap;

/**
 * Created by rushabhmehta91 on 4/6/15.
 */
public class ContentStore {
	static Runtime r = Runtime.getRuntime();
	public HashMap<String, Content> store;
	{
		store = new HashMap<String, Content>();
	}
	//    /**
	//     * Handles different type of incoming packet and behaves accordingly
	//     *
	//     * @param packet      - incoming packet
	//     * @param interfaceId - incoming Interface Id
	//     * @return
	//     */
	//    private Object packetHandler(ContentPacket packet, int interfaceId) {
	//        try {
	//            switch (packet.getIncomingPacketType()) {
	//                case 0:
	//                    return incomingContentRequest(packet, interfaceId);
	//                case 1:
	//                    return incomingReplyContent(packet);
	//                case 2:
	//                    return incomingContent(packet);
	//            }
	//        } catch (Exception e) {
	//            e.printStackTrace();
	//        }
	//
	//        return null;
	//    }

	//    private ContentPacket forwardingContent(ContentPacket packet) {
	//        return new ContentPacket(1, packet);
	//    }

	/**
	 * Checks for content which is requested if available returns back the content in Content P acket and then updates
	 * the score on interfaces. Depending on scores Copy and Delete functions are called.
	 *
	 * @param incomingPacket - incoming packet
	 * @param interfaceId    - interfaceId of incoming packet
	 * @return
	 * @throws Exception
	 */
	private ContentPacket incomingContentRequest(ContentPacket incomingPacket, String interfaceId) {
		String fileName = (String) incomingPacket.getData();
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

	/**
	 * CLIENT SIDE: Elliot
	 * When incoming packet is a reply then get the content and return is to the client
	 *
	 * @param packet - incoming packet
	 *
	 * @return
	 */
	public Content incomingReplyContent(ContentPacket packet) {
		return (Content) packet.getData();
	}

	/**
	 * When incoming packet have content which to be stored in content store than check the size of the the store
	 * if store has required size then place it in store else replace.
	 *
	 * @param packet - incoming packet
	 * @return
	 */
	public boolean incomingContent(ContentPacket packet) {
		Content receivedContent = (Content) packet.getData();
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
	private boolean replace(Content receivedContent) {
		return false;
	}

	/**
	 * Place the incoming content in the store. If content is in the store than replace the content else just add the
	 * content in the store
	 *
	 * @param receivedContent - incoming content
	 * @return
	 */
	public boolean place(Content receivedContent) {
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

	/**
	 * Reply in form of ContentPacket to incoming request
	 *
	 * @param fileName - name of the content
	 * @return ContentPacket
	 */
	public ContentPacket replyContentRequest(String fileName) {

		return new ContentPacket(1, store.get(fileName));
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
	private ContentPacket updateScoreOnIterface(Content contentStoreCopy, String interfaceId) throws Exception {
		if (!contentStoreCopy.listofScoreOnInterfaces.containsKey(interfaceId)) {
			contentStoreCopy.listofScoreOnInterfaces.put(interfaceId, contentStoreCopy.getMaxNScore());
		} else {
			contentStoreCopy.listofScoreOnInterfaces.replace(interfaceId, contentStoreCopy.listofScoreOnInterfaces.get(interfaceId) - 1);
		}
		boolean copyFlag = false;
		boolean deleteFlag = true;
		if (contentStoreCopy.listofScoreOnInterfaces.get(interfaceId) == 0) {
			copyFlag = true;
			for (String index : contentStoreCopy.listofScoreOnInterfaces.keySet()) {
				if (contentStoreCopy.listofScoreOnInterfaces.get(index) < contentStoreCopy.getMaxNScore() / 2) {
					deleteFlag = false;
				}
			}
		}
		if (copyFlag && deleteFlag) {
			if (!deleteContent(contentStoreCopy)) {
				throw new Exception("unable to delete content");
			}
		}

		if (copyFlag) {
			return copyContent(contentStoreCopy);
		}

		return null;
	}

	/**
	 * send content in the form of content packet
	 *
	 * @param content - content requested
	 * @return
	 */
	private ContentPacket copyContent(Content content) {
		return new ContentPacket(2, content);
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
