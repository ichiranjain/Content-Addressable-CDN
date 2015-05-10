package topology;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import orestes.bloomfilter.CountingBloomFilter;
import orestes.bloomfilter.FilterBuilder;

public class FIB{

	//hash map mapping prefix a length to a hash map
	//the hash map for a given prefix length maps prefix's to a list of advertisers
	ConcurrentHashMap<Integer, ConcurrentHashMap<String, ArrayList<String>>> hmOfPrefixLengths;

	//filter builder gets passed the expected elements and the false positive rate
	ConcurrentHashMap<Integer, CountingBloomFilter<String>> hmOfBloomFilters;

	//length of the longest prefix encountered
	int longestPrefixLength;

	//graph of all the nodes
	NodeRepository nodeRepo;

	PIT pit;

	DirectlyConnectedNodes directlyConnectedNodes;


	public FIB(NodeRepository nodeRepo, PIT pit, DirectlyConnectedNodes directlyConnectedNodes){
		hmOfPrefixLengths = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ArrayList<String>>>();
		hmOfBloomFilters = new ConcurrentHashMap<Integer, CountingBloomFilter<String>>();
		longestPrefixLength = 0;
		this.nodeRepo = nodeRepo; 
		this.pit = pit;
		this.directlyConnectedNodes = directlyConnectedNodes;
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

	public int getSizeOfAdvertisersList(int prefixLength, String prefix){
		return hmOfPrefixLengths.get(prefixLength).get(prefix).size();
	}

	public String getBestCostAdvertiser(int prefixLength, String prefix){
		//this returns error if the advertiser does not exist

		//check if there are any advertisers for the prefix
		if(hmOfPrefixLengths.get(prefixLength).get(prefix).isEmpty() == false){

			//** or is a client
			if((nodeRepo.HMdoesNodeExist(hmOfPrefixLengths.get(prefixLength).get(prefix).get(0)) == true) || 
					(directlyConnectedNodes.doesDirectlyConnectedClientExist(hmOfPrefixLengths.get(prefixLength).get(prefix).get(0)) == true) ){
				return  hmOfPrefixLengths.get(prefixLength).get(prefix).get(0);
			}else{

				//check if all the advertiser nodes exist, if it doesn't exist not it
				ArrayList<String> advertisers = hmOfPrefixLengths.get(prefixLength).get(prefix);
				for(int j = 0; j < advertisers.size(); j++){

					//** and clients
					if(nodeRepo.HMdoesNodeExist(advertisers.get(j)) == false || 
							(directlyConnectedNodes.doesDirectlyConnectedClientExist(hmOfPrefixLengths.get(prefixLength).get(prefix).get(0)) == false)){
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

					for(int i = 0; i < arraySize; i++){
						if(directlyConnectedNodes.doesDirectlyConnectedClientExist(advertisers.get(i)) == true){
							//swap to 1 index position
							temp = advertisers.get(0);
							advertisers.set(0, advertisers.get(i));
							advertisers.set(i, temp);

							//over write the list with the new list
							setAdvertisers(prefixLength, prefix, advertisers);
							return hmOfPrefixLengths.get(prefixLength).get(prefix).get(0);
						}
					}

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

		}else{
			//remove the prefix, from the hash map and bloom filter
			removePrefixFromBloomFIlter(prefixLength, prefix);
			removePrefixFromHashMap(prefixLength, prefix);
			return "error";
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

	public boolean addPrefixToFIB(String prefix, String advertiser){

		boolean prefixAdded = false;
		String[] contentNameSplit = prefix.split("/");
		int prefixLength = contentNameSplit.length;
		//does the fib contain a hashmap for the content length
		//if not make one
		if(doesPrefixLengthHashMapExist(prefixLength) == false){
			addPrefixLengthHashMap(prefixLength);
			addPrefixLengthBloomFilter(prefixLength);
		}


		//does the hash map contain the prefix 
		if(doesHashMapContainPrefix(prefixLength, prefix) == false){

			//does the advertiser node exist in the graph, if not skip this advertiser
			if((nodeRepo.HMdoesNodeExist(advertiser) == true) || 
					(directlyConnectedNodes.doesDirectlyConnectedClientExist(advertiser) ==true)){

				//add the content name and an empty list of advertisers 
				addPrefixToHashMap(prefixLength, prefix);

				//add the prefix to the Counting BLoom Filter 
				addPrefixToBloomFilter(prefixLength, prefix);

				addAdvertiserToHashMap(prefixLength, prefix, advertiser);

				prefixAdded = true;
			}


		}else{
			//if the content name DOES EXIST the just add the new advertisers 

			//does the advertiser node exist in the graph 
			if((nodeRepo.HMdoesNodeExist(advertiser) == true )|| 
					(directlyConnectedNodes.doesDirectlyConnectedClientExist(advertiser) ==true)){

				//is the advertiser listed under the given prefix, -1 is returned if the advertiser does not exist
				if( doesHashMapContainAdvertiser(prefixLength, prefix, advertiser) == -1){

					////add the prefix to the Counting BLoom Filter 
					//addPrefixToBloomFilter(prefixLength, prefix);

					//if the advertiser does not exist, add it
					addAdvertiserToHashMap(prefixLength, prefix, advertiser);

					prefixAdded = true;
				}
			}
		}
		return prefixAdded;
	}

	public boolean removePrefixFromFIB(String prefix, String advertiser){

		boolean prefixRemoved = false;
		String[] contentNameSplit = prefix.split("/");
		int prefixLength = contentNameSplit.length;
		if(doesPrefixLengthHashMapExist(prefixLength) == true){
			if(doesHashMapContainPrefix(prefixLength, prefix) == true){

				//if the advertiser list is of length 1... remove it ... else remove the advertiser
				if(getSizeOfAdvertisersList(prefixLength, prefix) <= 1){

					//remove the prefix 
					removePrefixFromHashMap(prefixLength, prefix);
					removePrefixFromBloomFIlter(prefixLength, prefix);
					prefixRemoved = true;

				}else{
					//remove the advertiser from the prefix
					removeAdvertiserFromHashMap(prefixLength, prefix, advertiser);
					prefixRemoved = true;
				}

			}

		}
		return prefixRemoved;
	}

	public String searchFIB(String contentName){

		//get the length of the prefix 
		String[] prefixSplit = contentName.split("/");
		String prefix = prefixSplit[0];
		ArrayList<Integer> hashMapsToSearch = new ArrayList<Integer>();

		//look up in the fib bloom filter, cant have a prefix of zero
		//this can be searched in parallel

		if(doesPrefixLengthBloomFilterExist(1) == true){	
			if(doesBloomFilterConteinPrefix(1, prefix) == true){
				hashMapsToSearch.add(1);
			}
		}

		for(int i = 1; i < prefixSplit.length; i++){

			prefix = prefix + "/" + prefixSplit[i];
			if(doesPrefixLengthBloomFilterExist(i + 1) == true){	
				if(doesBloomFilterConteinPrefix(i + 1, prefix) == true){
					hashMapsToSearch.add(i + 1);
				}
			}
			prefixSplit[i] = prefix;
		}

		//search the hash maps returned
		String bestCostNode = "";
		String nextHop = "";

		//search through the longest matching prefix hash map first
		for(int i = hashMapsToSearch.size() - 1; i >= 0; i--){

			//does the hash map for "x" length exist
			if(doesPrefixLengthHashMapExist(hashMapsToSearch.get(i)) == true){

				//does the prefix in this hash map exist
				if(doesHashMapContainPrefix(hashMapsToSearch.get(i), prefixSplit[hashMapsToSearch.get(i) - 1]) == true){

					//** if there are multiple advertisers 
					//** if the packet can't get to one advertiser... do not use the other advertiser
					//** this is just kept if the advertiser dies.. not used in routing?

					//try the next advertiser in the list 
					bestCostNode = getBestCostAdvertiser(hashMapsToSearch.get(i), prefixSplit[hashMapsToSearch.get(i) - 1]);

					//if best cost == error the node does not exist 
					if(bestCostNode.equals("error") == false){


						if(directlyConnectedNodes.doesDirectlyConnectedClientExist(bestCostNode) == true){
							nextHop = bestCostNode;
						}else{	
							// there was no error
							nextHop = nodeRepo.HMgetNode(bestCostNode).getOriginNextHop();
						}

						if(nextHop.equals("")){
							nextHop = "broadCast";
						}

						if(pit.doesEntryExist(contentName) == true){

							//ensure the packet is not being forwarded to a node that sent the interest
							//-1 means the requester does not exist 
							if(pit.getRequesters(contentName).doesRequesterExist(nextHop) != -1){

								//the interest was sent form the next hop ... the packet can not be forwarded
								nextHop = "broadCast";	
							}else{	
								//the entry exists but the next hop was not a requester
								//break cause the next hop is found
								break;
							}
						}else{
							//no pit entry exists for the content
							//break cause the next hop is found
							break;
						}
					}else{
						//the next hop does not exist... broad cast 
						nextHop = "broadCast";
					}
				}//if the prefix is not in the hash map ... it might have been a false positive


			}//the hash map did not exist
		}//end for loop

		return nextHop;
	}

	public int getLongestPrefixLength(){
		return hmOfPrefixLengths.size();
	}

	public ArrayList<String> getPrefixesForLength(int length){
		Set<String> keys = hmOfPrefixLengths.get(length).keySet();
		ArrayList<String> prefixList = new ArrayList<String>(keys.size());
		for(String key : keys){
			prefixList.add(key);
		}

		return prefixList;
	}

	public ArrayList<String> getFIBEntries(){
		Set<Integer> lengths = hmOfPrefixLengths.keySet();
		ArrayList<String> entries = new ArrayList<String>();
		for(Integer length : lengths){
			Set<String> prefixes = hmOfPrefixLengths.get(length).keySet();

			for(String prefix : prefixes){
				entries.add("Prefix: " + length + ": " + prefix + " |Advertisers|: " + hmOfPrefixLengths.get(length).get(prefix).toString());
			}
		}

		return entries;
	}


}
