import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Craw the seed url base on given limit number
 * 
 *
 */
public class WebCrawler {

	private final HashSet<URL> allUrl;
	private final WorkQueue worker;
	private final ThreadSafeInvertedIndex index;

	public WebCrawler(WorkQueue worker, ThreadSafeInvertedIndex threadSafe) {
		this.worker = worker;
		this.allUrl = new HashSet<URL>();
		this.index = threadSafe;
	}

	/**
	 * Craw the given seed
	 * @param seed url seed
	 * @param limit the maximum number of url to craw
	 */
	public void craw(URL seed, int limit) {
			allUrl.add(seed);
			worker.execute(new WebCrawlerTask(seed, limit));
			worker.finish();
	}

	/**
	 * Craw the url and the link in this url if the limit has not exceeded
	 * 
	 *
	 */
	private class WebCrawlerTask implements Runnable {

		private final URL Url;
		private final int limit;

		public WebCrawlerTask(URL url, int limit) {
			this.Url = url;
			this.limit = limit;
		}

		@Override
		public void run() {
			try {

				var html = HTMLFetcher.fetchHTML(Url, 3);
				if(html == null) {
					return;
				}

				InvertedIndex temp = new InvertedIndex();
				var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
				int start = 1;
				for(String s: TextParser.parse(HTMLCleaner.stripHTML(html))) {
					temp.add(stemmer.stem(s).toString(), Url.toString(), start++);
				}
				index.addAll(temp);

				if(allUrl.size() < limit) {
					ArrayList<URL> links = LinkParser.listLinks(Url, LinkParser.fetchHTML(Url));
					for (URL link : links) {
						synchronized (allUrl) {
							if (allUrl.size() >= limit) {
								break;
							} else {
								if (!allUrl.contains(link)) {
									allUrl.add(link);
									worker.execute(new WebCrawlerTask(link, limit));
								}
							}
						}
					}
				}
			} catch (IOException e) {
				System.err.println("Unable to read the page: " + Url.toString());
			}
		}
	}
}