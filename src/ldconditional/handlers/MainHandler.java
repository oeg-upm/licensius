package ldconditional.handlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Main handler, distributing tasks to the other handlers
 * 
 * @author vroddon
 */
public class MainHandler extends AbstractHandler {

    static final Logger logger = Logger.getLogger(MainHandler.class);

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled())
            return;
        logQuery(request);
    }

    
    /**
     * Stores a query.
     * @param request A HttpServletRequest
     */
    private void logQuery(HttpServletRequest request)
    {
        String sinfo = "Recibida la consulta " + request.getMethod() + " ";
        sinfo += request.getRequestURI() + " ";
        String sid="";
        try {
            sid=request.getSession(true).getId();
            sinfo = sinfo + " de "+ request.getRemoteAddr() + " " + sid;
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        logger.info(sinfo);
    }
    
}
