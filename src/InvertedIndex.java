import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> location;

	/**
	 * This constructor for the InvertedIndex. it initialize index and location data
	 * structure
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
		this.location = new TreeMap<>();
	}

	/**
	 * This method will add the word, and the path of this word and the position to
	 * the index data structure
	 * 
	 * @param word     The word in the file
	 * @param path     path of the file in directory
	 * @param position The position in this file
	 */
	public void add(String word, String path, int position) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(path, new TreeSet<>());
		index.get(word).get(path).add(position);
		if (location.containsKey(path)) {
			location.put(path, location.get(path) + 1);
		} else {
			location.put(path, 1);
		}

	}

	/**
	 * test if that word is in your index
	 * 
	 * @param word The word in the file
	 * @return return True if contain the word otherwise return false.
	 */
	public boolean contains(String word) {
		return this.index.containsKey(word);
	}

	/**
	 * Tests whether the index contains the specified word at the specified file.
	 *
	 * @param word     Word to look for
	 * @param position position to look for word
	 * @return true if the word is stored in the index at the specified file
	 */
	public boolean contains(String word, String location) {
		return contains(word) && this.index.get(word).containsKey(location);
	}

	/**
	 * check if it is contains position.
	 * 
	 * @param word     Word to look for
	 * @param location The location of the word in the file
	 * @param position Position to look for word
	 * @return true if the word is stored in the index at the specified file
	 */
	public boolean contains(String word, String location, int position) {
		return contains(word, location) && this.index.get(word).get(location).contains(position);
	}

	/**
	 * It will do the exactSearch to find the right result in the invertedIndex data
	 * structure
	 * 
	 * @param arrayline TreeSet of all the string that you need to search
	 * @return Arraylist of result object
	 */
	public ArrayList<Result> exactSearch(TreeSet<String> arrayline) {
		ArrayList<Result> resultList = new ArrayList<Result>();
		HashMap<String, Result> lookup = new HashMap<String, Result>();

		for (String word : arrayline) {
			if (index.containsKey(word)) {
				searchHelper(lookup, resultList, word);
			}
		}

		Collections.sort(resultList);
		return resultList;
	}

	/**
	 * Put the search result into the Arraylist.
	 * 
	 * @param lookup     the HashMap to check the location
	 * @param resultList the data structure to contain the result
	 * @param word       The word that you need to search in the invertIndex。
	 */
	private void searchHelper(HashMap<String, Result> lookup, ArrayList<Result> resultList, String word) {
		for (String indexPath : index.get(word).keySet()) {
			int totalword = location.get(indexPath);
			int matchword = index.get(word).get(indexPath).size();
			boolean exist = lookup.containsKey(indexPath);

			if (exist == false) {
				Result result = new Result(totalword, matchword, indexPath);
				resultList.add(result);
				lookup.put(indexPath, result);
			} else {
				Result result = lookup.get(indexPath);
				result.updateMatches(matchword);
			}
		}

	}

	/**
	 * It will do the partial Search to find the right answer in the inverted index
	 * data structure
	 * 
	 * @param arrayline TreeSet of all the string that you need to search
	 * @return Arraylist of result object
	 */
	public ArrayList<Result> partialSearch(TreeSet<String> arrayline) {
		ArrayList<Result> resultList = new ArrayList<Result>();
		HashMap<String, Result> lookup = new HashMap<String, Result>();

		for (String word : arrayline) {
			for (String indexword : index.tailMap(word).keySet()) {
				if (indexword.startsWith(word)) {
					searchHelper(lookup, resultList, indexword);
				} else {
					break;
				}
			}
		}

		Collections.sort(resultList);
		return resultList;

	}

	/***
	 * This method will make a check output is valid or not. Then it will put on a
	 * asDoubleNestedObject to convert to JON object.
	 * 
	 * @param output the file's path that need to write to
	 */
	public void toJSON(Path output) throws IOException {
		TreeJSONWriter.asDoubleNestedObject(index, output);
	}

	/**
	 * It will write the location into the JSON file
	 * 
	 * @param output The path file that you will write to
	 * @throws IOException IOException if the writer encounters any issues
	 */
	public void toLocationJSON(Path output) throws IOException {
		if (output != null) {
			TreeJSONWriter.asObject(location, output);
		}
	}

	/**
	 * Add every thread InvertedIndex data structure to the overall invertedIndex
	 * data structure
	 * 
	 * @param other Every thread's InvertedIndex data structure.
	 */
	public void addAll(InvertedIndex other) {
		for (String word : other.index.keySet()) {
			if (this.index.containsKey(word)) {
				for (String path : other.index.get(word).keySet()) {
					if (this.index.get(word).containsKey(path)) {
						this.index.get(word).get(path).addAll(other.index.get(word).get(path));
					} else {
						this.index.get(word).put(path, other.index.get(word).get(path));
					}
				}
			} else {
				this.index.put(word, other.index.get(word));
			}

		}

		for (String x : other.location.keySet()) {
			if (this.location.containsKey(x)) {
				this.location.put(x, other.location.get(x) + this.location.get(x));
			}
			this.location.put(x, other.location.get(x));
		}
	}

}
