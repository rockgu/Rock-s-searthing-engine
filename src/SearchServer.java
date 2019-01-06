import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class SearchServer {
	
	//public static final int PORT = 8080;
	
	

	public static void startServer(ThreadSafeInvertedIndex threadSafe,int PORT) throws Exception {
		// TODO Auto-generated method stub
		
		Server server = new Server(PORT);

		ServletHandler handler = new ServletHandler();
		
		handler.addServletWithMapping(new ServletHolder(new SearchServlet(threadSafe)), "/simple");
		handler.addServletWithMapping(new ServletHolder(new BulmaSearchServlet(threadSafe)), "/bulma");
		handler.addServletWithMapping(new ServletHolder(new CookieIndexServlet()), "/history");
		handler.addServletWithMapping(new ServletHolder(new CookieConfigServlet()), "/clear");
		
		server.setHandler(handler);
		server.start();
		server.join();
	}

	

}
