package ldserver;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.apache.log4j.Logger;

import ldconditional.Main;


/**
 * Handler that processes commands.
 * 
 * @author vroddon
 */
public class ServiceHandler extends AbstractHandler {
    
    static final Logger logger = Logger.getLogger(Main.class);
    
        public void handle(String string, Request baseRequest, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (baseRequest.isHandled() || !string.contains("/service")) {
            return;
        }
        logger.info("Serving a general file");
        
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Max-Age", "3600");
        resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");        
        
        if (string.contains("/service/getOffers"))
        {
            GetOffers go = new GetOffers();
            go.doGet(req, resp);
            baseRequest.setHandled(true);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            return;
        }
        if (string.contains("/service/getResources"))
        {
            GetResources go = new GetResources();
            go.doGet(req, resp);
            baseRequest.setHandled(true);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            return;
        }
        if (string.contains("/service/getOpenResource"))
        {
            GetOpenResource go = new GetOpenResource();
            go.doGet(req, resp);
            baseRequest.setHandled(true);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            return;
        }
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        resp.getWriter().println("<h1>Service Hello World</h1>");
    }   
}
