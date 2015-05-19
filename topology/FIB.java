package topology;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import orestes.bloomfilter.CountingBloomFilter;
import orestes.bloomfilter.FilterBuilder;

/**
 * FIB is the forwarding table </br>
 * THis class is responsible to mapping content to advertisers.</br>
 * 
 * 
 * hmOfPrefixLengths: is a hash map of integers to hash maps.</br>
 * the integer is the length of the prefix and the hash map</br> 
 * stores all the content names of the specified length.</br>
 * 
 * hmOfBloomFilters: is a hash map of integer to bloom filters. The integer</br>
 * is for the length of the content and the bloom filter stores the different </br>
 * content names. This is set up for multi threading but current is NOT multi </br>
 * threaded.</br>
 * 
 * longestPrefixLength: keeps track of the longest prefix length stored in the</br> 
 * FIB.</br>
 * 
 * @author spufflez
 *
 */
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


	/**
	 * Constructor
	 * @param nodeRepo
	 * @param pit
	 * @param directlyConnectedNodes
	 */
	public FIB(NodeRepository nodeRepo, PIT pit, DirectlyConnectedNodes directlyConnectedNodes){
		hmOfPrefixLengths = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ArrayList<String>>>();
		hmOfBloomFilters = new ConcurrentHashMap<Integer, CountingBloomFilter<String>>();
		longestPrefixLength = 0;
		this.nodeRepo = nodeRepo; 
		this.pit = pit;
		this.directlyConnectedNodes = directlyConnectedNodes;
	}

	/**
	 * Adds a new length to the hash map </br>
	 * key = length </br>
	 * value = hash map of content to advertisers</br>
	 * @param prefixLength
	 */
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


	/**
	 * Removes a length from the hash map and the associated hash map for the length</br>
	 * @param prefixLength
	 */
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

	/**
	 * Checks if the provided prefix length is in the hash map 
	 * @param prefixLength
	 * @return true if it exists and false if dne
	 */
	public boolean doesPrefixLengthHashMapExist(int prefixLength){
		if(hmOfPrefixLengths.containsKey(prefixLength)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Sets the longest prefix length
	 * @param prefixLength
	 */
	public void setLongestPrefixLength(int prefixLength){
		longestPrefixLength = prefixLength;
	}

	/**
	 * Gets the size of lengths hash map
	 * @return
	 */
	public int sizeOfPrefixLengthHashMap(){
		return hmOfPrefixLengths.size();
	}

	/**
	 * Gets the length of the longest content name currently seen
	 * @return longest prefix seen 
	 */
	public int longestPrefixCurrentlySeen(){
		return longestPrefixLength;
	}

	/**
	 * Gets the best code node for a given content name 
	 * @param prefixLength
	 * @param prefix
	 * @return the best cost node if available or empty if there are no 
	 * advertisers for the content 
	 */
	public String getBestCostNode(int prefixLength, String prefix){
		//call does an entry for the first element in the array exist
		if(hmOfPrefixLengths.get(prefixLength).get(prefix).isEmpty() == false){			
			return hmOfPrefixLengths.get(prefixLength).get(prefix).get(0);
		}else{
			return "empty";
		}
	}

	/**
	 * Adds a content name to the hash map at the provided length
	 * @param prefixLength
	 * @param prefix
	 */
	public void addPrefixToHashMap(int prefixLength, String prefix){
		hmOfPrefixLengths.get(prefixLength).put(prefix, new ArrayList<String>());
	}

	/**
	 * Removes the content name from the hash map 
	 * @param prefixLength
	 * @param prefix
	 */
	public void removePrefixFromHashMap(int prefixLength, String prefix){
		hmOfPrefixLengths.get(prefixLength).remove(prefix);
	}

	/**
	 * Checks if the hash map contains the given content name
	 * @param prefixLength
	 * @param prefix
	 * @return true if it exists and false if dne
	 */
	public boolean doesHashMapContainPrefix(int prefixLength, String prefix){
		return hmOfPrefixLengths.get(prefixLength).containsKey(prefix);
	}

	/**
	 * Sets the list of advertisersfor a content name
	 * @param prefixLength
	 * @param prefix
	 * @param advertisers
	 */
	public void setAdvertisers(int prefixLength, String prefix, ArrayList<String> advertisers){
		hmOfPrefixLengths.get(prefixLength).put(prefix, advertisers);
	}

	/**
	 * Adds a single advertiser to the list of advertisers for a content name
	 * @param prefixLength
	 * @param prefix
	 * @param advertiser
	 */
	public void addAdvertiserToHashMap(int prefixLength, String prefix, String advertiser){
		hmOfPrefixLengths.get(prefixLength).get(prefix).add(advertiser);
		//sort the list... to make sure the least cost node is first
	}

	/**
	 * Removes an advertiser for a given content name 
	 * @param prefixLength
	 * @param prefix
	 * @param advertiser
	 */
	public void removeAdvertiserFromHashMap(int prefixLength, String prefix, String advertiser){
		int index = doesHashMapContainAdvertiser(prefixLength, prefix, advertiser);
		if(index != -1){			
			hmOfPrefixLengths.get(prefixLength).get(prefix).remove(index);
		}
	}

	/**
	 * Checks if the advertiser is present in the list of advertisers for a given content
	 * @param prefixLength
	 * @param prefix
	 * @param advertiser
	 * @return true if present and false if dne
	 */
	public int doesHashMapContainAdvertiser(int prefixLength, String prefix, String advertiser){
		for(int i = 0; i < hmOfPrefixLengths.get(prefixLength).get(prefix).size(); i++){

			if(hmOfPrefixLengths.get(prefixLength).get(prefix).get(i).equals(advertiser) == true){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the size of the advertiser list for a given content name
	 * @param prefixLength
	 * @param prefix
	 * @return size fo advertiser list 
	 */
	public int getSizeOfAdvertisersList(int prefixLength, String prefix){
		return hmOfPrefixLengths.get(prefixLength).get(prefix).size();
	}

	/**
	 * Gets the best cost advertiser out of the list of advertisers. 
	 * The best cost advertiser is the first advertiser in the list.
	 * This will perform checks if the advertiser exists and replace
	 * the best cost advertise if it does not exist.
	 * @param prefixLength
	 * @param prefix
	 * @return
	 */
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


	/**
	 * Finds the best cost advertiser
	 * The best cost advertiser is the first advertiser in the list.
	 * This will perform checks if the advertiser exists and replace
	 * the best cost advertise if it does not exist.
	 * 
	 */
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

	/**
	 * Adds a prefix length to the bloom filter hash map 
	 * @param prefixLength
	 */
	public void addPrefixLengthBloomFilter(int prefixLength){
		hmOfBloomFilters.putIfAbsent(prefixLength, new FilterBuilder(10000, 0.01).buildCountingBloomFilter());
	}
	/**
	 * removes the longest prefix length from the bloom filter hash map 
	 */
	public void removeLastPrefixLengthBloomFilter(){
		hmOfBloomFilters.remove(longestPrefixLength);
	}
	/**
	 * removes the specified length from the bloom filter hash map
	 * @param prefixLength
	 */
	public void removePrefixLengthBloomFilter(int prefixLength){
		hmOfBloomFilters.remove(prefixLength);
	}
	/**
	 * Checks if the length exists 
	 * @param prefixLength
	 * @return true if it exists and false if it dne
	 */
	public boolean doesPrefixLengthBloomFilterExist(int prefixLength){
		return hmOfBloomFilters.containsKey(prefixLength);
	}
	/**
	 * Gets the size of the bloom filter hash map 
	 * @return size of the bloom filter hash map 
	 */
	public int sizeOfPrefixLengthBloomFilter(){
		return hmOfBloomFilters.size();
	}
	/**
	 * Adds a content name to the bloom filter
	 * @param prefixLength
	 * @param prefix
	 */
	public void addPrefixToBloomFilter(int prefixLength, String prefix){
		hmOfBloomFilters.get(prefixLength).add(prefix);
	}
	/**
	 * Removes a content name from the bloom filter
	 * @param prefixLength
	 * @param prefix
	 */
	public void removePrefixFromBloomFIlter(int prefixLength, String prefix){
		hmOfBloomFilters.get(prefixLength).remove(prefix);
	}
	/**
	 * Checks if the bloom filter contains the content name
	 * @param prefixLength
	 * @param prefix
	 * @return true if it exists and false if dne
	 */
	public boolean doesBloomFilterConteinPrefix(int prefixLength, String prefix){
		return hmOfBloomFilters.get(prefixLength).contains(prefix);
	}

	/**
	 * Adds a content name to the FIB</br>
	 * This will add the content name to the appropriate hash maps according to </br>
	 * the content name's length.
	 * @param prefix
	 * @param advertiser
	 * @return true if content name was added, false if it was not added
	 */
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

		System.out.println("check point 3");
		//does the hash map contain the prefix 
		if(doesHashMapContainPrefix(prefixLength, prefix) == false){
			System.out.println("check point 4");
			//does the advertiser node exist in the graph, if not skip this advertiser
			if((nodeRepo.HMdoesNodeExist(advertiser) == true) || 
					(directlyConnectedNodes.doesDirectlyConnectedClientExist(advertiser) ==true)){

				System.out.println("in fib b4 add prefix");
				//add the content name and an empty list of advertisers 
				addPrefixToHashMap(prefixLength, prefix);

				//add the prefix to the Counting BLoom Filter 
				addPrefixToBloomFilter(prefixLength, prefix);

				addAdvertiserToHashMap(prefixLength, prefix, advertiser);

				prefixAdded = true;
			}


		}else{
			System.out.println("check point 2");
			//if the content name DOES EXIST the just add the new advertisers 

			//does the advertiser node exist in the graph 
			if((nodeRepo.HMdoesNodeExist(advertiser) == true )|| 
					(directlyConnectedNodes.doesDirectlyConnectedClientExist(advertiser) ==true)){
				System.out.println("check point 1");
				//is the advertiser listed under the given prefix, -1 is returned if the advertiser does not exist
				if( doesHashMapContainAdvertiser(prefixLength, prefix, advertiser) == -1){
					System.out.println("check point 0");
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

	/**
	 * Removes a prefix from the FIB
	 * @param prefix
	 * @param advertiser
	 * @return true if the content name was removed and false if it was not removed
	 */
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

	/**
	 * Searches the FIB for the best cost advertiser for the given content name </br>
	 * and returns the next hop to get to that best cost advertiser.
	 * @param contentName
	 * @return next hop if available or broadcast if not available 
	 */
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

	/**
	 * Gets the length of the longest content name stored
	 * @return length of the longest content name stored
	 */
	public int getLongestPrefixLength(){
		return hmOfPrefixLengths.size();
	}

	/**
	 * Gets all the content names for a given length
	 * @param length
	 * @return array list of content names for the given length
	 */
	public ArrayList<String> getPrefixesForLength(int length){
		Set<String> keys = hmOfPrefixLengths.get(length).keySet();
		ArrayList<String> prefixList = new ArrayList<String>(keys.size());
		for(String key : keys){
			prefixList.add(key);
		}

		return prefixList;
	}

	/**
	 * Gets all the FIB entries in the entire table</br>
	 * Use this when printing the entire FIB table.
	 * @return array list of all the content names and advertisers in the FIB
	 */
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
