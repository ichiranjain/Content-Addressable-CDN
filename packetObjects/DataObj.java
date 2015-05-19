package packetObjects;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * This is the object used to create data packets. 
 * When being processed by a cache server data packets are parsed into data objects
 * 
 * contentName: is the name of the content that was requested</br>
 * originRouter: is the ID of the first cache server the interest packet passed through</br>
 * flag is the fault tolerance flag which can be set to 0,1,2</br>
 * 0: means route the data packet like normal</br>
 * 1: means route the data packet back to the origin cache server</br>
 * 2: means forward the data packet back towards the server </br>
 * data: is the pay load of the data packet</br>
 * original packet: is this class represented as a json string </br>
 * cache flag: is a flag set to 1 or 2, 1 don't send data packet to the cache layer, </br>
 * and 2 send the packet to the caching layer </br>
 * last Chunk: states if the data packet is the last chunk, if the data </br>
 * requested spanned several chunks
 * @author spufflez
 *
 */
public class DataObj {

	String contentName;
	String originRouter;
	byte flag;
	String data;
	String originalPacket;
	byte cacheFlag;
	boolean lastChunk;

	/**
	 * Constructor that accepts all parameters 
	 * @param contentName
	 * @param originRouter
	 * @param flag
	 * @param data
	 * @param originalPacket
	 * @param cacheFlag
	 * @param lastChunk
	 */
	public DataObj(String contentName, String originRouter, 
			byte flag, String data, String originalPacket, 
			byte cacheFlag, boolean lastChunk){

		this.contentName = contentName;
		this.originRouter = originRouter;
		if(flag > 2){
			flag = 2;
		}
		if(flag < 0){
			flag = 0;
		}
		this.flag = flag;
		this.data = data;
		this.originalPacket = originalPacket;
		this.cacheFlag = cacheFlag;
		this.lastChunk = lastChunk;
	}


	/**
	 * constructor that does not require the original packet 
	 * @param contentName
	 * @param originRouter
	 * @param flag
	 * @param data
	 * @param cacheFlag
	 * @param lastChunk
	 */
	public DataObj(String contentName, String originRouter, byte flag, String data, byte cacheFlag, boolean lastChunk){
		this.contentName = contentName;
		this.originRouter = originRouter;
		if(flag > 2){
			flag = 2;
		}
		if(flag < 0){
			flag = 0;
		}
		this.flag = flag;
		this.data = data;
		this.originalPacket = "";
		this.cacheFlag = cacheFlag;
		this.lastChunk = lastChunk;
	}


	/**
	 * constructor that accepts the data as a byte array and does not require an original packet 
	 * @param contentName
	 * @param originRouter
	 * @param flag
	 * @param data
	 * @param cacheFlag
	 * @param lastChunk
	 */
	public DataObj(String contentName, String originRouter, byte flag, byte[] data, byte cacheFlag, boolean lastChunk){
		this.contentName = contentName;
		this.originRouter = originRouter;
		if(flag > 2){
			flag = 2;
		}
		if(flag < 0){
			flag = 0;
		}
		this.flag = flag;
		this.data = convertToString(data);
		this.cacheFlag = cacheFlag;
		this.lastChunk = lastChunk;

	}

	/**
	 * get content name
	 * @return content name
	 */
	public String getContentName() {
		return contentName;
	}

	/**
	 * set content name
	 * @param contentName
	 */
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}


	/**
	 * get the origin cache server ID, this is the ID of the first cache server to handle the packet 
	 * @return origin router ID as a String
	 */
	public String getOriginRouterName() {
		return originRouter;
	}


	/**
	 * set the origin cache server ID
	 * @param senderName
	 */
	public void setOriginRouterName(String senderName) {
		this.originRouter = senderName;
	}

	/**
	 * get the fault tolerance flag 
	 * @return byte flag
	 */
	public byte getFlag() {
		return flag;
	}

	/**
	 * set the fault tolerance falg to 0,1,2
	 * @param flag
	 */
	public void setFlag(byte flag) {
		if(flag > 2){
			flag = 2;
		}
		if(flag < 0){
			flag = 0;
		}
		this.flag = flag;
	}

	/**
	 * set the data string 
	 * @param data
	 */
	public void setData(String data){
		this.data = data;
	}

	/**
	 * get the data string
	 * @return data string
	 */
	public String getData(){
		return data;
	}


	/**
	 * gets the json representation of the object 
	 * @return original packet string
	 */
	public String getOriginalPacket(){
		return originalPacket;
	}


	/**
	 * set the original packet to a json string
	 * @param originalPacket
	 */
	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}

	/**
	 * converts the data string to a byte array 
	 * @return byte[] of the data string
	 */
	public byte[] getDataBytes(){
		return convertToByteArray(data);
	}

	/**
	 * converts UTF-8 byte array to a string
	 * @param data
	 * @return data string
	 */
	private String convertToString(byte[] data){
		return new String(data, Charset.forName("UTF-8"));
	}


	/**
	 * converts a data string to a UTF-8 byte array 
	 * @param dataString
	 * @return byte array
	 */
	private byte[] convertToByteArray(String dataString){
		try {
			return dataString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new byte[0];
	}


	/**
	 * set the cache flag to 1 or 2
	 * @param cacheFlag
	 */
	public void setCacheFlag(byte cacheFlag){
		this.cacheFlag = cacheFlag;
	}

	/**
	 * get the cache flag
	 * @return byte cache flag
	 */
	public byte getCacheFlag(){
		return cacheFlag;
	}


	/**
	 * set the last chunk boolean, determines if the data packet is the last data packet being sent
	 * @param lastChunk
	 */
	public void setLastChunk(boolean lastChunk){
		this.lastChunk = lastChunk;
	}


	/**
	 * get the last chunk boolean
	 * @return last chunk boolean
	 */
	public boolean getLastChunk(){
		return lastChunk;
	}


}
