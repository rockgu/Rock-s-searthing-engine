import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class MultiThreadQueryBuilder implements QueryBuilderInterface {

	private final TreeMap<String, ArrayList<Result>> results;
	private final ThreadSafeInvertedIndex index;
	private WorkQueue queue;

	/**
	 * The constructor of the multithreadQueryBuilder
	 * 
	 * @param index  The invertedIndex DataStructure
	 * @param thread The number of thread that you need to use
	 */
	public MultiThreadQueryBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		this.results = new TreeMap<String, ArrayList<Result>>();
		this.index = index;
		this.queue = queue;
	}

	/**
	 * It is a method that will choose the exactSearch or partialSearch to do the
	 * mutithread search
	 * 
	 * @param path       The path of file
	 * @param exactCheck Cheek it is partial search or exactedsearch
	 * @throws IOException reader cann't find the right path
	 */
	public void addSearchResult(Path path, boolean exactCheck) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
			String contentLine;
			while ((contentLine = br.readLine()) != null) {
				queue.execute(new searchTask(contentLine, exactCheck));
			}
		}
		queue.finish();
	}

	private class searchTask implements Runnable { 
		private final String line;
		private final boolean exact;

		public searchTask(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			TreeSet<String> words = new TreeSet<>();

			for (String s : TextParser.parse(line)) {
				String word = stemmer.stem(s).toString();
				if (!word.isEmpty()) {
					words.add(word);
				}
			}

			String oneLine = String.join(" ", words);

			if (!words.isEmpty()) {
				synchronized (results) {
					if (results.containsKey(oneLine)) {
						return;
					}
				}

				ArrayList<Result> result;
				if (exact) {
					result = index.exactSearch(words);
				} else {
					result = index.partialSearch(words);
				}
				synchronized (results) {
					results.put(oneLine, result);
				}
			}
		}

	}

	/**
	 * Write the search result to the JSON file
	 * 
	 * @param output The path that will write the result.
	 * @throws IOException IOException if the writer encounters any issues
	 */
	public void toResultJSON(Path output) throws IOException {
		synchronized (results) {
			TreeJSONWriter.searchNestedObject(results, output);
		}
	}

}
