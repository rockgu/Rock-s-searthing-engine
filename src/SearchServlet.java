
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// http://commons.apache.org/proper/commons-lang/download_lang.cgi

@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {
	private static final String TITLE = "Messages";
	private static Logger log = Log.getRootLogger();
	private final ThreadSafeInvertedIndex threadSafe;
	private String url;
	private WorkQueue queue;
	private ConcurrentLinkedQueue<String> messages;
	private boolean check;

	public SearchServlet(ThreadSafeInvertedIndex threadSafe) {
		super();
		this.threadSafe = threadSafe;
		messages = new ConcurrentLinkedQueue<>();
		queue = new WorkQueue(10);
		url = "";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body>%n");

		out.printf("<h1>Seaching Engine</h1>%n%n");
		
		if (!url.trim().isEmpty()) {
//			URL newurl = new URL(url);
			try {
				URL newurl = new URL(url);
				WebCrawler wc = new WebCrawler(queue, threadSafe);
				wc.craw(newurl, 50);
			} catch (Exception e) {
				System.out.println("invalid url!! ");
			}
		}

		// Keep in mind multiple threads may access at once

		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

		for (String message : messages) {
			TreeSet<String> words = new TreeSet<>();
			for (String s : TextParser.parse(message)) {
				String word = stemmer.stem(s).toString();
				if (!word.isEmpty()) {
					words.add(word);
				}
			}
			ArrayList<Result> results;
			if (check == true) {
				results = threadSafe.partialSearch(words);
			} else {
				results = threadSafe.exactSearch(words);
			}
			out.printf("<a> %s</a >%n<br>", message);
			for (Result x : results) {
				String path = x.getPath();
				out.printf("<a href= \"%s\"> %s</a >%n%n<br>", path, path);
			}

		}

		printForm(request, response);

		out.printf("<p>This request was handled by thread %s.</p>%n", Thread.currentThread().getName());

		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");
		String partialSearch = request.getParameter("Partial search");
		url = request.getParameter("newurl");
		String partial = request.getParameter("partial");
		String exact = request.getParameter("exacted");

		if (partial != null) {
			check = true;
			partial = null;
		}
		if (exact != null) {
			check = false;
			exact = null;
		}

		System.out.println("******" + url);

		partialSearch = partialSearch == null ? "" : partialSearch;
		url = url == null ? "" : url;
		partialSearch = StringEscapeUtils.escapeHtml4(partialSearch);

		System.out.println("search  " + partialSearch);
		messages.add(partialSearch);

		// Only keep the latest 5 messages
		if (messages.size() > 1) {
			String first = messages.poll();
			log.info("Removing message: " + first);
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>New Search Url:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"newurl\" maxlength=\"100\" size=\"60\">%n");
		out.printf("<td>Click Exact button to search for results Exactly match your search. </td>%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Partial search:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"Partial search\" maxlength=\"50\" size=\"20\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		out.printf("<p><input type=\"submit\"name=\"partial\" value=\"partial searching \"></p>\n%n");
		out.printf("<p><input type=\"submit\"name=\"exacted\" value=\"exacted searching \"></p>\n%n");
		out.printf("</form>\n%n");
	}

}
