import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * CookieIndexServlet that get stored cookies information and make response to the users
 *
 * @see CookieBaseServlet
 * @see CookieIndexServlet
 * @see CookieConfigServlet
 */
@SuppressWarnings({ "serial", "deprecation" })
public class CookieIndexServlet extends CookieBaseServlet {

	public static final String QUERY = "query";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info("GET " + request.getRequestURL().toString());

		if (request.getRequestURI().endsWith("favicon.ico")) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Cookie cookie = null;
		Cookie[] cookies = null;

		// Get an array of Cookies associated with this domain
		cookies = request.getCookies();

		// Set response content type
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		
		if(cookies != null) {
			out.println("<h2> Found Cookies Queries</h2>");
			for (int i = 0; i < cookies.length; i++) {
				cookie = cookies[i];
				String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
				String escaped = StringEscapeUtils.escapeHtml4(decoded);
				log.info("Encoded: " + cookie.getValue() + ", Decoded: " + decoded + ", Escaped: " + escaped);
				out.print("Query String : ");
				out.print(escaped + " <br/>");
			}
		} else {
			out.println("<h2>No cookies founds</h2>");
		}
		
		out.println("</body>");
		out.println("</html>");

		finishResponse(request, response);
	}
}
