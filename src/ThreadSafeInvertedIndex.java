import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeSet;

public class ThreadSafeInvertedIndex extends InvertedIndex {
	private final ReadWriteLock lock;

	/**
	 * @Override The constructor of the ThreadSafeInvertedIndex
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

	/**
	 * @Override This method will add the word, and the path of this word and the
	 *           position to the index data structure
	 * 
	 * @param word     The word in the file
	 * @param path     path of the file in directory
	 * @param position The position in this file
	 */
	public void add(String word, String path, int position) {
		lock.lockReadWrite();
		try {
			super.add(word, path, position);
		} finally {
			lock.unlockReadWrite();
		}
	}

	/**
	 * @Override test if that word is in your index
	 * 
	 * @param word The word in the file
	 * @return return True if contain the word otherwise return false.
	 */
	public boolean contains(String word) {
		lock.lockReadOnly();
		try {
			return super.contains(word);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * @Override Tests whether the index contains the specified word at the
	 *           specified file.
	 *
	 * @param word     Word to look for
	 * @param position position to look for word
	 * @return true if the word is stored in the index at the specified file
	 */
	public boolean contains(String word, String location) {
		lock.lockReadOnly();
		try {
			return super.contains(word, location);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * @Override check if it is contains position.
	 * 
	 * @param word     Word to look for
	 * @param location The location of the word int the file
	 * @param position Position to look for word
	 * @return true if the word is stored in the index at the specified file
	 */
	public boolean contains(String word, String location, int position) {
		lock.lockReadOnly();
		try {
			return super.contains(word, location, position);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * @Override It will do the exactSearch to find the right result in the
	 *           invertedIndex data structure
	 */
	public ArrayList<Result> exactSearch(TreeSet<String> arrayline) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(arrayline);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * @Override It will do the partial Search to find the right answer in the
	 *           inverted index data structure
	 */
	public ArrayList<Result> partialSearch(TreeSet<String> arrayline) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(arrayline);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/***
	 * @Override This method will make a check output is valid or not. Then it will
	 *           put on a asDoubleNestedObject to convert to JON object.
	 * 
	 * @param output the file's path that need to write to
	 */
	public void toJSON(Path output) throws IOException {
		lock.lockReadOnly();
		try {
			super.toJSON(output);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * @Override It will write the location into the JSON file
	 * 
	 * @param output The path file that you will write to
	 * @throws IOException IOException if the writer encounters any issues
	 */
	public void toLocationJSON(Path output) throws IOException {
		lock.lockReadOnly();
		try {
			super.toLocationJSON(output);
			;
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * @Override Helper method return a TreeSet of given word and path
	 * 
	 * @param word key for the TreeMap
	 * @param path key for the TreeMap
	 * @return TreeSet<Integer>
	 */
	public void addAll(InvertedIndex temp) {
		lock.lockReadWrite();
		try {
			super.addAll(temp);
		} finally {
			lock.unlockReadWrite();
		}
	}

}
