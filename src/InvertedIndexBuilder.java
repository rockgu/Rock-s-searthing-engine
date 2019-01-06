import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;


public class InvertedIndexBuilder {
	
/**
 * read all the path of file from the directory recursively. After the got 
 * all the path of file, it will put to the fileReader method
 * 
 * @param directory     The directory that need to  traverse
 * @param invert    The invertIndex object to pass into fileReader method
 * @throws IOException  When file is not exist
 * 
 * @see {@link DirectoryStream}
 */
	public static void traverse(Path directory, InvertedIndex invert) throws IOException {
		if (Files.isDirectory(directory)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
				Iterator<Path> list = listing.iterator();
				Path currentFile;
				while (list.hasNext()) {
					currentFile = list.next();
					traverse(currentFile, invert);
				}
			}
		} else {
			String path1 = directory.toString().toLowerCase();
			if (path1.endsWith(".text") || path1.endsWith(".txt")) {
				fileReader(directory, invert);
			}
		}
	}

	/**
	 * This method will put path to the bufferReader. Then use stemLine method got
	 * the a list of string . We loop the list of string and put the path ,word and position 
	 * in to invert object's add method. 
	 * @param path The path of the file that we need to read
	 * @param invert The Invertedindex object 
	 */
	public static void fileReader(Path path, InvertedIndex invert) throws FileNotFoundException, IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String contentLine;
			int position = 1; 
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			String fileLocation = path.toString();
			while ((contentLine = reader.readLine()) != null) {
				String[] words = TextParser.parse(contentLine);
				for (String word : words) {
					String stem = stemmer.stem(word).toString();
					invert.add(stem, fileLocation, position);
					position++;
				}
			}
		}
	}
}
