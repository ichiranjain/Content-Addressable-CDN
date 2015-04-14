package UnitTests;

import packetObjects.PITEntry;
import topology.PIT;

public class PITTest {

	PIT pit = new PIT();

	public PITTest(){

	}

	public void testPIT(){


		pit.addEntry("prefix1", "D");
		pit.addEntry("prefix3", "D");
		pit.addEntry("prefix2", "G");

		pit.removeEntry("prefix2");

		System.out.println("does entry exist: " + pit.doesEntryExist("prefix1"));

		pit.addRequester("prefix1", "E");
		pit.addRequester("prefix1", "F");
		pit.removeRequester("prefix1", "D");
		PITEntry pitEntry = pit.getRequesters("prefix1");
		for(int i = 0; i < pitEntry.getSizeOfRequesters(); i++){
			System.out.print("requester: " + pitEntry.getRequesters().get(i) + " " );
		}
		System.out.println("");

		System.out.println("does requester exist: " + pit.doesRequesterExist("prefix1", "E"));
		System.out.println("Size of requesters: " + pit.sizeOfRequestersList("prefix1"));

		pit.getTime("prefix1");
		pit.setTime("prefix1");
		pit.getPitEntries();
	}
}
