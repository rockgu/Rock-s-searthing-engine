import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {
	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException when the file is not exist
	 */
	public static void main(String[] args) {
		InvertedIndex index = null;
		ThreadSafeInvertedIndex threadSafe = null;
		ArgumentMap map = new ArgumentMap(args);

		boolean threadCheck = map.hasFlag("-threads");
		QueryBuilderInterface query = null;

		WorkQueue queue = null;

		boolean checkPort = map.hasFlag("-port");
		boolean checkUrl = map.hasFlag("-url");
		WebCrawler crawler = null;
		
		if(checkPort)
		{
			threadCheck = true; 
		}
		
		if (checkUrl)
		{
			threadCheck = true; 
		}
		
		if (threadCheck != true) {
			index = new InvertedIndex();
			query = new QueryBuilder(index);

		} else {

			int thread = Integer.parseInt(map.getString("-threads", "5"));
			queue = new WorkQueue(thread);
			threadSafe = new ThreadSafeInvertedIndex();
			index = threadSafe;
			query = new MultiThreadQueryBuilder(threadSafe, queue);
			crawler = new WebCrawler(queue, threadSafe);

		}
		
		if (checkUrl)
		{
			String seedStr = map.getString("-url");
			URL seed;
			int limit;
			try {
				seed = new URL(map.getString("-url"));
				limit = Integer.parseInt(map.getString("-limit", "50"));
			} catch (MalformedURLException e) {
				System.err.println("Illegal url: " + seedStr + " please check your argument");
				return;
			} catch (NumberFormatException numEx) {
				System.err.println("Illegal limit number: " + map.getString("-limit", "50"));
				return;
			}
			
			crawler.craw(seed, limit);
		   
		   
		}
		

		if (map.hasFlag("-path")) {
			Path path = map.getPath("-path");
			if (path != null) {
				try {
					if (threadSafe != null) {

						MultiThreadInvertedIndexBuilder.traverse(path, threadSafe, queue);

					} else {

						InvertedIndexBuilder.traverse(path, index);
					}
				} catch (IOException e) {
					System.out.println("The writer can't write to the file" + path);
				} catch (InterruptedException e) {
					System.out.println("The one of thread got interrupted");
				}

			}
		}

		if (map.hasFlag("-index")) {
			Path path = map.getPath("-index", Paths.get("index.json"));
			try {
				index.toJSON(path);
			} catch (IOException e) {
				System.out.println("The writer can't write to the file" + path);
			}
		}
		
		if(checkPort)
		{
			int PORT = Integer.parseInt(map.getString("-port"))	;
			try {
				System.out.print("********");
				SearchServer.startServer(threadSafe,PORT);
			} catch (Exception e) {
				System.out.println("Web problem");
			}
		}

		if (map.hasFlag("-locations")) {
			Path path = map.getPath("-locations", Paths.get("index.json"));
			try {
				index.toLocationJSON(path);
			} catch (IOException e) {
				System.out.println("The writer can't write to the file" + path);
			}
		}

		if (map.hasFlag("-search")) {
			Path path = map.getPath("-search");
			boolean exactCheck = map.hasFlag("-exact");
			try {
				query.addSearchResult(path, exactCheck);
			} catch (FileNotFoundException e) {
				System.out.println("we can not find the right path for this file " + path);
			} catch (IOException e) {
				System.out.println("The writer can't write to the file" + path);
			}
		}

		if (map.hasFlag("-results")) {
			Path path = map.getPath("-results", Paths.get("results.json"));
			try {
				query.toResultJSON(path);
			} catch (IOException e) {
				System.out.println("The writer can't write to the file" + path);
			}
		}
		
		if (queue != null) {
			queue.shutdown();
		}
	}

}