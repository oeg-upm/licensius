package ldserver;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.Main;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Handler that processes commands
 * @author vroddon
 */
public class ServiceHandler extends AbstractHandler {
    
    static final Logger logger = Logger.getLogger(Main.class);
    
        public void handle(String string, Request baseRequest, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (baseRequest.isHandled() || !string.contains("/service")) {
            return;
        }
        logger.info("Service handler");
        
        if (string.contains("/service/getOffers"))
        {
            GetOffers go = new GetOffers();
            go.doGet(req, resp);
            baseRequest.setHandled(true);
            return;
        }
        
        
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        resp.getWriter().println("<h1>Service Hello World</h1>");
    }   
}
