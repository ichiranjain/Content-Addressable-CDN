package UnitTests;

import java.util.ArrayList;

import topology.DirectlyConnectedNodes;
import topology.FIB;
import topology.NodeRepository;
import topology.PIT;

public class FIBTest {

	FIB fib;
	NodeRepository nodeRepo;

	public FIBTest(){
		nodeRepo = new NodeRepository("A");
		DirectlyConnectedNodes directlyConnectedNodes = new DirectlyConnectedNodes();
		fib = new FIB(nodeRepo, new PIT(), directlyConnectedNodes);

		nodeRepo.HMaddNode("A");
		nodeRepo.HMaddNeighbor("A", "B", 20);
		nodeRepo.HMaddNeighbor("A", "D", 80);
		nodeRepo.HMaddNeighbor("A", "G", 90);
		nodeRepo.HMgetNode("A").setBestCost(0);
		nodeRepo.HMgetNode("A").setOriginNextHop("A");

		nodeRepo.HMaddNode("B");
		nodeRepo.HMaddNeighbor("B", "F", 10);
		nodeRepo.HMgetNode("B").setBestCost(20);
		nodeRepo.HMgetNode("B").setOriginNextHop("B");

		nodeRepo.HMaddNode("C");
		nodeRepo.HMaddNeighbor("C", "F", 50);
		nodeRepo.HMaddNeighbor("C", "H", 20);
		nodeRepo.HMaddNeighbor("C", "D", 10);
		nodeRepo.HMgetNode("C").setBestCost(40);
		nodeRepo.HMgetNode("C").setOriginNextHop("B");

		nodeRepo.HMaddNode("D");
		nodeRepo.HMaddNeighbor("D", "C", 10);
		nodeRepo.HMaddNeighbor("D", "G", 20);
		nodeRepo.HMgetNode("D").setBestCost(50);
		nodeRepo.HMgetNode("D").setOriginNextHop("B");

		nodeRepo.HMaddNode("E");
		nodeRepo.HMaddNeighbor("E", "B", 50);
		nodeRepo.HMaddNeighbor("E", "G", 30);
		nodeRepo.HMgetNode("E").setBestCost(1000);
		nodeRepo.HMgetNode("E").setOriginNextHop("B");

		nodeRepo.HMaddNode("F");
		nodeRepo.HMaddNeighbor("F", "C", 10);
		nodeRepo.HMaddNeighbor("F", "D", 40);
		nodeRepo.HMgetNode("F").setBestCost(30);
		nodeRepo.HMgetNode("F").setOriginNextHop("B");

		nodeRepo.HMaddNode("G");
		nodeRepo.HMaddNeighbor("G", "A", 20);
		nodeRepo.HMgetNode("G").setBestCost(70);
		nodeRepo.HMgetNode("G").setOriginNextHop("B");

		nodeRepo.HMaddNode("H");
		nodeRepo.HMgetNode("H").setBestCost(60);
		nodeRepo.HMgetNode("H").setOriginNextHop("B");

	}

	public void testFIB(){

		fib.addPrefixLengthHashMap(1);
		fib.addPrefixLengthHashMap(2);
		fib.addPrefixLengthHashMap(3);

		fib.removeLastPrefixLengthHashMap();

		fib.removePrefixLengthHashMap(2);
		fib.addPrefixLengthHashMap(2);

		System.out.println("does prefix length hash map exist: " + fib.doesPrefixLengthHashMapExist(2));

		fib.setLongestPrefixLength(3);
		fib.setLongestPrefixLength(2);

		System.out.println("size of prefix length hash map: " + fib.sizeOfPrefixLengthHashMap());

		System.out.println("longest prefix length seen: " + fib.longestPrefixCurrentlySeen());

		//getBestCostNode(int, String)

		fib.addPrefixToHashMap(1, "prefix1");
		fib.addPrefixToHashMap(1, "prefix2");
		fib.addPrefixToHashMap(1, "prefix3");

		fib.removePrefixFromHashMap(1, "prefix3");

		System.out.println("does hash map contain prefix: " + fib.doesHashMapContainPrefix(1, "prefix2"));

		ArrayList<String> advertisers = new ArrayList<String>(); 
		advertisers.add("B");
		advertisers.add("C");
		advertisers.add("D");
		fib.setAdvertisers(1, "prefix1", advertisers);


		fib.addAdvertiserToHashMap(1, "prefix2", "F");

		fib.removeAdvertiserFromHashMap(1, "prefix1", "D");

		System.out.println("does hash map contain prefix: " + fib.doesHashMapContainAdvertiser(1, "prefix1", "C"));

		System.out.println("size of advertisers list: " + fib.getSizeOfAdvertisersList(1, "prefix1"));

		System.out.println("get best cost advertiser: " + fib.getBestCostAdvertiser(1, "prefix1"));

		//fib.findBestCostAdvertisers();

		fib.addPrefixLengthBloomFilter(1);
		fib.addPrefixLengthBloomFilter(2);
		fib.addPrefixLengthBloomFilter(3);

		fib.removeLastPrefixLengthBloomFilter();

		fib.removePrefixLengthBloomFilter(3);

		System.out.println("does prefix length bloom filter exist: " + fib.doesPrefixLengthBloomFilterExist(2));

		System.out.println("size of the prefix length bloom filter: " + fib.sizeOfPrefixLengthBloomFilter());

		fib.addPrefixToBloomFilter(1, "prefix1");
		fib.addPrefixToBloomFilter(1, "prefix2");
		fib.addPrefixToBloomFilter(1, "prefix3");

		fib.removePrefixFromBloomFIlter(1, "prefix3");

		System.out.println("does bloom filter contain prefix: " + fib.doesBloomFilterConteinPrefix(1, "prefix1"));

		fib.addPrefixToFIB("prefix4", "G");
		fib.removePrefixFromFIB("prefix4", "G");

		System.out.println("search fib: " + fib.searchFIB("prefix1"));

		System.out.println("fib longest prefix length: " + fib.getLongestPrefixLength());
		ArrayList<String> prefixes = fib.getPrefixesForLength(1);
		for(int i = 0; i < prefixes.size(); i++){
			System.out.print(prefixes.get(i) + " ");
		}
	}

}