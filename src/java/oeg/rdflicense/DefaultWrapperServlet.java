package oeg.rdflicense;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;


/**
 * @todo Document the mission of this class
 */
public class DefaultWrapperServlet extends HttpServlet
{   private static final Logger logger = Logger.getLogger(DefaultWrapperServlet.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    	throws ServletException, IOException
    {
    	RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
        logger.info("GRAPER siendo ejecutado por alguna razón misteriosa " + req.getRequestURI());
        if (req.getRequestURI().equals("/favicon.ico"))
            return;

    	HttpServletRequest wrapped = new HttpServletRequestWrapper(req) {
    		public String getServletPath() { return ""; }
    	};

    	rd.forward(wrapped, resp);
    }
}    

