package UnitTests;

import java.util.ArrayList;

import packetObjects.DataObj;
import packetObjects.IntrestObj;
import packetObjects.LinkObj;
import packetObjects.ModifyNodeObj;
import packetObjects.NeighborRequestObj;
import packetObjects.PrefixListObj;
import packetObjects.PrefixObj;
import topology.NeighborAndCostStrings;
import topology.Parse;
import topology.SendPacket;

public class SendPacketTest {

	SendPacket sendPacket;
	Parse parse;

	public SendPacketTest(){
		sendPacket = new SendPacket();
		this.parse = new Parse();
	}

	public void testSendPacket(){


		LinkObj linkObj = new LinkObj("A", 12);
		LinkObj linkObj2;
		sendPacket.createAddLinkPacket(linkObj);
		System.out.println("creat add link packet: " + linkObj.getOriginalPacket());
		linkObj2 = parse.parseAddLink(linkObj.getOriginalPacket());
		sendPacket.createAddLinkPacket(linkObj2);
		System.out.println("creat add link packet: " + linkObj2.getOriginalPacket() + "\n");


		sendPacket.createRemoveLinkPacket(linkObj);
		System.out.println("creat remove link: " + linkObj.getOriginalPacket());
		linkObj2 = parse.parseRemoveLink(linkObj.getOriginalPacket());
		sendPacket.createRemoveLinkPacket(linkObj2);
		System.out.println("creat remove link: " + linkObj2.getOriginalPacket() + "\n");

		sendPacket.createModifyLinkPacket(linkObj);
		System.out.println("creat modify link: " + linkObj.getOriginalPacket());
		linkObj2 = parse.parseModifyLink(linkObj.getOriginalPacket());
		sendPacket.createModifyLinkPacket(linkObj2);
		System.out.println("creat modify link: " + linkObj2.getOriginalPacket() + "\n");

		sendPacket.createAddClient(linkObj);
		System.out.println("creat add client: " + linkObj.getOriginalPacket());
		linkObj2 = parse.parseClientAddNodeJson(linkObj.getOriginalPacket());
		sendPacket.createAddClient(linkObj2);
		System.out.println("creat add client: " + linkObj2.getOriginalPacket() + "\n");

		sendPacket.createRemoveClient(linkObj);
		System.out.println("creat remove client: " + linkObj.getOriginalPacket());
		linkObj2 = parse.parseClientRemoveNodeJson(linkObj.getOriginalPacket());
		sendPacket.createRemoveClient(linkObj2);
		System.out.println("creat remove client: " + linkObj2.getOriginalPacket() + "\n");


		PrefixObj prefixObj = new PrefixObj("prefix1", "MSGID", "D", true);
		PrefixObj prefixObj2;
		sendPacket.createClientPrefix(prefixObj);
		System.out.println("creat client prefix: " + prefixObj.getOriginalPacket());
		prefixObj2 = parse.parsePrefixJson(prefixObj.getOriginalPacket());
		System.out.println("creat client prefix: " + prefixObj2.getOriginalPacket() + "\n");

		sendPacket.createPrefixPacket(prefixObj);
		System.out.println("creat prefix packet: " + prefixObj.getOriginalPacket());
		prefixObj2 = parse.parsePrefixJson(prefixObj.getOriginalPacket());
		System.out.println("creat prefix packet: " + prefixObj2.getOriginalPacket() + "\n");


		ArrayList<NeighborAndCostStrings> neighbors = new ArrayList<NeighborAndCostStrings>();
		neighbors.add(new NeighborAndCostStrings("X", 11));
		neighbors.add(new NeighborAndCostStrings("Y", 22));
		neighbors.add(new NeighborAndCostStrings("W", 33));
		ModifyNodeObj modifyNodeObj = new ModifyNodeObj("Z", neighbors, "MSGID3" );
		ModifyNodeObj modifyNodeObj2;
		sendPacket.createModifyNodePacket(modifyNodeObj);
		System.out.println("creat modify node packet: " + modifyNodeObj.getOriginalPacket() );
		modifyNodeObj2 = parse.parseModifyNodeJson(modifyNodeObj.getOriginalPacket());
		System.out.println("creat modify node packet: " + modifyNodeObj2.getOriginalPacket() + "\n");


		ArrayList<String> prefixList = new ArrayList<String>();
		prefixList.add("prefix1");
		prefixList.add("prefix2");
		prefixList.add("prefix3");
		PrefixListObj prefixListObj = new PrefixListObj(prefixList, "M", true, "D3408583940");
		PrefixListObj prefixListObj2;
		sendPacket.createClientPrefixList(prefixListObj);
		System.out.println("creat client prefix list: " + prefixListObj.getOriginalPacket());
		prefixListObj2 = parse.parsePrefixListJson(prefixListObj.getOriginalPacket());
		sendPacket.createClientPrefixList(prefixListObj2);
		System.out.println("creat client prefix list: " + prefixListObj2.getOriginalPacket() + "\n");

		sendPacket.createPrefixListPacket(prefixListObj);
		System.out.println("creat prefix list packet: " + prefixListObj.getOriginalPacket());
		prefixListObj2 = parse.parsePrefixListJson(prefixListObj.getOriginalPacket());
		sendPacket.createClientPrefixList(prefixListObj2);
		System.out.println("creat prefix list packet: " + prefixListObj2.getOriginalPacket() + "\n");


		IntrestObj intrestObj = new IntrestObj("prefix11"	, "A", 1234);
		IntrestObj intrestObj2;
		sendPacket.createRequestNeighborsIntrestPacket(intrestObj);
		System.out.println("creat request neighbors intrest packet: " + intrestObj.getOriginalPacket());
		intrestObj2 = parse.parseIntrestJson(intrestObj.getOriginalPacket());
		System.out.println("creat request neighbors intrest packet: " + intrestObj2.getOriginalPacket() + "\n");


		sendPacket.createIntrestPacket(intrestObj);
		System.out.println("creat intrest packet: " + intrestObj.getOriginalPacket());
		intrestObj2 = parse.parseIntrestJson(intrestObj.getOriginalPacket());
		System.out.println("creat intrest packet: " + intrestObj2.getOriginalPacket() + "\n");

		byte b = 0;
		DataObj dataObj = new DataObj("prefix22", "B", b, "datadatadatadata", "");
		DataObj dataObj2;
		sendPacket.createNeighborsDataPacket(dataObj);
		System.out.println("creat neighbors data packet: " + dataObj.getOriginalPacket());
		dataObj2 = parse.parseDataJson(dataObj.getOriginalPacket());
		System.out.println("creat neighbors data packet: " + dataObj2.getOriginalPacket() + "\n");

		sendPacket.createPrefixListDataPacket(dataObj);
		System.out.println("creat prefix list data packet: " + dataObj.getOriginalPacket());
		dataObj2 = parse.parseDataJson(dataObj.getOriginalPacket());
		System.out.println("creat prefix list data packet: " + dataObj2.getOriginalPacket() + "\n");

		sendPacket.createDataPacket(dataObj);
		System.out.println("creat data packet: " + dataObj.getOriginalPacket());
		dataObj2 = parse.parseDataJson(dataObj.getOriginalPacket());
		System.out.println("creat data packet: " + dataObj2.getOriginalPacket() + "\n");

		NeighborRequestObj neighborRequestObj = new NeighborRequestObj("J");
		NeighborRequestObj neighborRequestObj2;
		sendPacket.createNeighborRequestPacket(neighborRequestObj);
		System.out.println("creat neighbor request packet: " + neighborRequestObj.getOriginalPacket());
		neighborRequestObj2 = parse.parseRequestNeighbors(neighborRequestObj.getOriginalPacket());
		sendPacket.createNeighborRequestPacket(neighborRequestObj2);
		System.out.println("creat neighbor request packet: " + neighborRequestObj2.getOriginalPacket() + "\n");

		sendPacket.createPrefixResponsePacket(prefixListObj);
		System.out.println("creat prefix response packet: " + prefixListObj.getOriginalPacket());
		prefixListObj2 = parse.parsePrefixListJson(prefixListObj.getOriginalPacket());
		sendPacket.createPrefixResponsePacket(prefixListObj2);
		System.out.println("creat prefix response packet: " + prefixListObj2.getOriginalPacket() + "\n");

		sendPacket.createNeighborResponsePacket(modifyNodeObj);
		System.out.println("creat neighbor response packet: " + modifyNodeObj.getOriginalPacket());
		modifyNodeObj2 = parse.parseModifyNodeJson(modifyNodeObj.getOriginalPacket());
		System.out.println("creat neighbor response packet: " + modifyNodeObj2.getOriginalPacket() + "\n");

	}

}
