package caching;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateChunk {
	
	private static String FILE_NAME;
	private static short CHUNK_SIZE = 1000;
	
			
	
	public void splitFile(File file, String contentName){
		 String FILE_NAME = contentName;
		 FileInputStream inputStream;
		 FileOutputStream filePart;
		 String newFileName;
		 byte[] byteChunkPart;
		 int fileSize = (int) file.length();
		 int nChunks = 0, read = 0, readLength = CHUNK_SIZE;
		try {
	            inputStream = new FileInputStream(file);
	            while (fileSize > 0) {
	                if (fileSize <= CHUNK_SIZE) {
	                    readLength = fileSize;
	                }
	                byteChunkPart = new byte[readLength];
	                read = inputStream.read(byteChunkPart, 0, readLength);
	                fileSize -= read;
	                assert (read == byteChunkPart.length);
	                nChunks++;
	                newFileName = FILE_NAME + ".part"
	                        + Integer.toString(nChunks - 1);
	                filePart = new FileOutputStream(new File(newFileName));
	                filePart.write(byteChunkPart);
	                filePart.flush();
	                filePart.close();
	                byteChunkPart = null;
	                filePart = null;
	            }
	            inputStream.close();
	        } catch (IOException exception) {
	            exception.printStackTrace();
	        }
		 
	}

}
