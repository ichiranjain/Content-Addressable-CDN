package caching;

public class Chunk {
	
	private int chunkNumber;
	private int totalNumberOfChunks;
	private byte [] data;
	private int sizeOfChunk;
	private int totalSize;
	
	public Chunk(int chunkNumber, int totalNumberOfChunks, byte[] data,
			int sizeOfChunk, int totalSize) {
		this.chunkNumber = chunkNumber;
		this.totalNumberOfChunks = totalNumberOfChunks;
		this.data = data;
		this.sizeOfChunk = sizeOfChunk;
		this.totalSize = totalSize;
	}
	
	public int getChunkNumber() {
		return chunkNumber;
	}

	public void setChunkNumber(int chunkNumber) {
		this.chunkNumber = chunkNumber;
	}

	public int getTotalNumberOfChunks() {
		return totalNumberOfChunks;
	}

	public void setTotalNumberOfChunks(int totalNumberOfChunks) {
		this.totalNumberOfChunks = totalNumberOfChunks;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getSizeOfChunk() {
		return sizeOfChunk;
	}

	public void setSizeOfChunk(int sizeOfChunk) {
		this.sizeOfChunk = sizeOfChunk;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public Chunk(){
		
	}
	
	

}
