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
    
        public void handle(String string, Request baseRequest, HttpServletRequest hsr, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled() || !string.startsWith("/service")) {
            return;
        }
        logger.info("Service handler");
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Service Hello World</h1>");
    }   
}
