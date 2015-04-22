package topology;

public class MainEntryPoint implements Runnable{

	String thisMachinesName;

	public MainEntryPoint(String thisMachinesName) {
		this.thisMachinesName = thisMachinesName;
	}

	@Override
	public void run() {

		PacketQueue2 packetQueue2 = new PacketQueue2();
		NodeRepository nodeRepo = new NodeRepository(thisMachinesName);
		PIT pit = new PIT();
		DirectlyConnectedNodes directlyConnectedNodes = new DirectlyConnectedNodes();
		UpdateMsgsSeen updateMsgsSeen = new UpdateMsgsSeen();
		FIB fib = new FIB(nodeRepo, pit, directlyConnectedNodes);

		//start the handlers
		//general
		//update
		//routing

		//start the removal threads
		//update msagId's seen
		//PIT entries
		//FIB



		boolean alive = true;
		while(alive == true){

		}

	}

}
