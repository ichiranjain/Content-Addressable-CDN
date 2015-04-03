package topology;


public class RoutingSwitch implements Runnable{

	String packet;
	FIB fib;
	ProcessRoutingPackets process;
	PIT pit;
	Parse parse;
	NodeRepository nodeRepo;


	public RoutingSwitch(String packet,
			FIB fib,
			ProcessRoutingPackets process,
			PIT pit,
			Parse parse,
			NodeRepository nodeRepo){

		this.packet = packet;
		this.fib = fib;
		this.process = process;
		this.pit = pit;
		this.parse = parse;
		this.nodeRepo = nodeRepo;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		String action = parse.parseAction(packet);

		switch(action){
		case "intrest" :
			//do something
			//parse
			//process
			break;

		case "data" :
			//do something
			//parse
			//process
			break;

		case "routeDNE" :
			//do something
			//parse
			//process
			break;

		default :
			System.out.println("Error in UpdateSwitch - unrecognized packet: dropping packet");
			break;

		}

	}

}
