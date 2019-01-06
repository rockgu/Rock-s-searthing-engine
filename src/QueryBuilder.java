import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryBuilder implements QueryBuilderInterface {

	private final TreeMap<String, ArrayList<Result>> results;
	private final InvertedIndex index;

	/**
	 * The constructor of QueryBuilder and initialize result and index data
	 * structure.
	 */
	public QueryBuilder(InvertedIndex index) {
		this.results = new TreeMap<String, ArrayList<Result>>();
		this.index = index;
	}

	/**
	 * Stem the result and choose the exactSearch or partialSearch.
	 * 
	 * @param index      The InvertedIndex data structure
	 * @param path       The path of file that need to search
	 * @param exactCheck Check it is exactSearch or partialSearch.
	 */
	public void addSearchResult(Path path, boolean exactCheck) throws FileNotFoundException, IOException {
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String contentLine;
			while ((contentLine = br.readLine()) != null) {
				TreeSet<String> words = new TreeSet<>();

				for (String s : TextParser.parse(contentLine)) {
					String word = stemmer.stem(s).toString();
					if (!word.isEmpty()) {
						words.add(word);
					}
				}

				String queryLine = String.join(" ", words);

				if (words.isEmpty() || results.containsKey(queryLine)) {
					continue;
				} else {
					if (exactCheck) {
						results.put(queryLine, index.exactSearch(words));
					} else {
						results.put(queryLine, index.partialSearch(words));
					}
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
		TreeJSONWriter.searchNestedObject(results, output);
	}
}