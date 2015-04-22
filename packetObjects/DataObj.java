package packetObjects;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class DataObj {

	String contentName;
	String originRouter;
	byte flag;
	String data;
	String originalPacket;
	byte cacheFlag;
	boolean lastChunk;


	//** have data object accept a byte array for data and convert it to a string
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

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getOriginRouterName() {
		return originRouter;
	}

	public void setOriginRouterName(String senderName) {
		this.originRouter = senderName;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		if(flag > 2){
			flag = 2;
		}
		if(flag < 0){
			flag = 0;
		}
		this.flag = flag;
	}

	public void setData(String data){
		this.data = data;
	}

	public String getData(){
		return data;
	}

	public String getOriginalPacket(){
		return originalPacket;
	}

	public void setOriginalPacket(String originalPacket){
		this.originalPacket = originalPacket;
	}
	public byte[] getDataBytes(){
		return convertToByteArray(data);
	}

	private String convertToString(byte[] data){
		return new String(data, Charset.forName("UTF-8"));
	}

	private byte[] convertToByteArray(String dataString){
		try {
			return dataString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new byte[0];
	}

	public void setCacheFlag(byte cacheFlag){
		this.cacheFlag = cacheFlag;
	}

	public byte getCacheFlag(){
		return cacheFlag;
	}

	public void setLastChunk(boolean lastChunk){
		this.lastChunk = lastChunk;
	}

	public boolean getLastChunk(){
		return lastChunk;
	}


}
