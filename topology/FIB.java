package topology;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import orestes.bloomfilter.CountingBloomFilter;
import orestes.bloomfilter.FilterBuilder;

public class FIB{

	//hash map mapping prefix a length to a hash map
	//the hash map for a given prefix length maps prefixs to a list of advertisers
	ConcurrentHashMap<Integer, ConcurrentHashMap<String, ArrayList<String>>> hmOfPrefixLengths;

	//filter builder gets passed the expected elements and the false positive rate
	ConcurrentHashMap<Integer, CountingBloomFilter<String>> hmOfBloomFilters;

	//length of the longest prefix encountered
	int longestPrefixLength;

	//graph of all the nodes
	NodeRepository nodeRepo;


	public FIB(NodeRepository nodeRepo){
		hmOfPrefixLengths = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ArrayList<String>>>();
		hmOfBloomFilters = new ConcurrentHashMap<Integer, CountingBloomFilter<String>>();
		longestPrefixLength = 0;
		this.nodeRepo = nodeRepo; 
	}

	public void addPrefixLengthHashMap(int prefixLength){
		hmOfPrefixLengths.put(prefixLength, new ConcurrentHashMap<String, ArrayList<String>>());
		setLongestPrefixLength(prefixLength);
	}

	public void removeLastPrefixLengthHashMap(){
		hmOfPrefixLengths.remove(longestPrefixLength);

		Set<Integer> keys = hmOfPrefixLengths.keySet();
		int tempLongestPrefixLength = 0;

		for(int key : keys){
			if(tempLongestPrefixLength < key){
				tempLongestPrefixLength = key;
			}
		}

		longestPrefixLength = tempLongestPrefixLength;
	}

	public void removePrefixLengthHashMap(int prefixLength){
		hmOfPrefixLengths.remove(prefixLength);

		Set<Integer> keys = hmOfPrefixLengths.keySet();
		int tempLongestPrefixLength = 0;

		for(int key : keys){
			if(tempLongestPrefixLength < key){
				tempLongestPrefixLength = key;
			}
		}

		longestPrefixLength = tempLongestPrefixLength;

	}

	public boolean doesPrefixLengthHashMapExist(int prefixLength){
		if(hmOfPrefixLengths.containsKey(prefixLength)){
			return true;
		}else{
			return false;
		}
	}

	public void setLongestPrefixLength(int prefixLength){
		longestPrefixLength = prefixLength;
	}

	public int sizeOfPrefixLengthHashMap(){
		return hmOfPrefixLengths.size();
	}

	public int longestPrefixCurrentlySeen(){
		return longestPrefixLength;
	}

	public String getBestCostNode(int prefixLength, String prefix){
		//call does an entry for the first element in the array exist
		if(hmOfPrefixLengths.get(prefixLength).get(prefix).isEmpty() == false){			
			return hmOfPrefixLengths.get(prefixLength).get(prefix).get(0);
		}else{
			return "empty";
		}
	}

	public void addPrefixToHashMap(int prefixLength, String prefix){
		hmOfPrefixLengths.get(prefixLength).put(prefix, new ArrayList<String>());
	}

	public void removePrefixFromHashMap(int prefixLength, String prefix){
		hmOfPrefixLengths.get(prefixLength).remove(prefix);
	}

	public boolean doesHashMapContainPrefix(int prefixLength, String prefix){
		return hmOfPrefixLengths.get(prefixLength).containsKey(prefix);
	}

	public void setAdvertisers(int prefixLength, String prefix, ArrayList<String> advertisers){
		hmOfPrefixLengths.get(prefixLength).put(prefix, advertisers);
	}

	public void addAdvertiserToHashMap(int prefixLength, String prefix, String advertiser){
		hmOfPrefixLengths.get(prefixLength).get(prefix).add(advertiser);
		//sort the list... to make sure the least cost node is first
	}

	public void removeAdvertiserFromHashMap(int prefixLength, String prefix, String advertiser){
		int index = doesHashMapContainAdvertiser(prefixLength, prefix, advertiser);
		if(index != -1){			
			hmOfPrefixLengths.get(prefixLength).get(prefix).remove(index);
		}
	}

	public int doesHashMapContainAdvertiser(int prefixLength, String prefix, String advertiser){
		for(int i = 0; i < hmOfPrefixLengths.get(prefixLength).get(prefix).size(); i++){

			if(hmOfPrefixLengths.get(prefixLength).get(prefix).get(i).equals(advertiser) == true){
				return i;
			}
		}
		return -1;
	}

	public String getBestCostAdvertiser(int prefixLength, String prefix){

		if(nodeRepo.HMdoesNodeExist(hmOfPrefixLengths.get(prefixLength).get(prefix).get(0)) == true){
			return  hmOfPrefixLengths.get(prefixLength).get(prefix).get(0);
		}else{

			//check if all the advertiser nodes exist, if it doesn't exist not it
			ArrayList<String> advertisers = hmOfPrefixLengths.get(prefixLength).get(prefix);
			for(int j = 0; j < advertisers.size(); j++){
				if(nodeRepo.HMdoesNodeExist(advertisers.get(j)) == false){
					advertisers.remove(j);
				}
			}	

			int arraySize = advertisers.size();

			if(arraySize == 0){
				//remove the prefix, from the hash map and bloom filter
				removePrefixFromBloomFIlter(prefixLength, prefix);
				removePrefixFromHashMap(prefixLength, prefix);

				return "error";

			}else if(arraySize == 1){
				//over write the list with the new list
				setAdvertisers(prefixLength, prefix, advertisers);
				return hmOfPrefixLengths.get(prefixLength).get(prefix).get(0);

			}else{
				int index = 0;
				int bestCost = 0;
				String temp = "";
				index = 0;

				//find the best cost node in the list of advertisers
				bestCost = nodeRepo.HMgetNode(advertisers.get(0)).getBestCost();
				for(int i = 1; i < arraySize; i++){

					if(nodeRepo.HMgetNode(advertisers.get(i)).getBestCost() > bestCost){
						bestCost = nodeRepo.HMgetNode(advertisers.get(i)).getBestCost();
						index = i;
					}
				}

				//set the first element in the array to the best cost advertiser
				if(index != 0){

					temp = advertisers.get(0);
					advertisers.set(0, advertisers.get(index));
					advertisers.set(index, temp);

					//over write the list with the new list
					setAdvertisers(prefixLength, prefix, advertisers);

				}

				return hmOfPrefixLengths.get(prefixLength).get(prefix).get(0);
			}

		}


	}

	public void addDefualtRoute(int prefixLength, String defaultNode){
		ArrayList<String> defaultRoute = new ArrayList<String>();
		defaultRoute.add(defaultNode);
		hmOfPrefixLengths.get(prefixLength).put("default", defaultRoute);
	}

	public void findBestCostAdvertisers(){

		/*
		 * does the node still exist
		 */

		int size = 0;
		int index = 0;
		int bestCost = 0;
		String temp = "";
		ArrayList<String> advertisers;

		for(int i = 0; i < longestPrefixLength; i++){
			if(hmOfPrefixLengths.contains(i) == true){
				//get keys for a prefix hash map
				Set<String> keys = hmOfPrefixLengths.get(i).keySet();

				//for each key check the nodes in the corresponding array
				//and find the best cost node. 
				for( String key : keys){

					advertisers = hmOfPrefixLengths.get(i).get(key);
					for(int j = 0; j < advertisers.size(); j++){
						if(nodeRepo.HMdoesNodeExist(advertisers.get(j)) == false){
							advertisers.remove(j);
						}
					}	

					//
					size = advertisers.size();
					if(size == 0){
						//remove the prefix, from the hash map and bloom filter
						removePrefixFromBloomFIlter(i, key);
						removePrefixFromHashMap(i, key);

					}else if(size == 1){
						//over write the list with the new list
						setAdvertisers(i, key, advertisers);

					}else{
						index = 0;

						//find the best cost node in the list of advertisers
						bestCost = nodeRepo.HMgetNode(advertisers.get(0)).getBestCost();
						for(int j = 1; j < size; j++){

							if(nodeRepo.HMgetNode(advertisers.get(j)).getBestCost() > bestCost){
								bestCost = nodeRepo.HMgetNode(advertisers.get(j)).getBestCost();
								index = j;
							}
						}

						//set the first element in the array to the best cost advertiser
						if(index != 0){

							temp = advertisers.get(0);
							advertisers.set(0, advertisers.get(index));
							advertisers.set(index, temp);

							//over write the list with the new list
							setAdvertisers(i, key, advertisers);

						}
					}

				}//end for each
			}//end if
		}//end for

	}

	public void addPrefixLengthBloomFilter(int prefixLength){
		hmOfBloomFilters.putIfAbsent(prefixLength, new FilterBuilder(10000, 0.01).buildCountingBloomFilter());
	}
	public void removeLastPrefixLengthBloomFilter(){
		hmOfBloomFilters.remove(longestPrefixLength);
	}
	public void removePrefixLengthBloomFilter(int prefixLength){
		hmOfBloomFilters.remove(prefixLength);
	}
	public boolean doesPrefixLengthBloomFilterExist(int prefixLength){
		return hmOfBloomFilters.containsKey(prefixLength);
	}
	public int sizeOfPrefixLengthBloomFilter(){
		return hmOfBloomFilters.size();
	}
	public void addPrefixToBloomFilter(int prefixLength, String prefix){
		hmOfBloomFilters.get(prefixLength).add(prefix);
	}
	public void removePrefixFromBloomFIlter(int prefixLength, String prefix){
		hmOfBloomFilters.get(prefixLength).remove(prefix);
	}
	public boolean doesBloomFilterConteinPrefix(int prefixLength, String prefix){
		return hmOfBloomFilters.get(prefixLength).contains(prefix);
	}



}
