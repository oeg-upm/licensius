package oeg.rdflicense.servlet;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;


/**
 * This class serves files like .png, .jpg, etc. 
 * Typicially this is done with the default servlet at javax.servlet-api. 
 * But this is not accepted by appspot
 */
public class DefaultWrapperServlet extends HttpServlet
{   private static final Logger logger = Logger.getLogger(DefaultWrapperServlet.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
        logger.info("GRAPER2 siendo ejecutado por alguna razón misteriosa " + req.getRequestURI());
        
        String uri = req.getRequestURI(); 
        
        if (uri.equals("/favicon.ico"))
        {
            return;
        }

       if (uri.endsWith(".png")) {
            resp.setContentType("image/png");
            
            
            
            ServletOutputStream output = resp.getOutputStream();
            String filename = uri.substring("/rdflicense/".length());
            logger.info("GRAPER3 intentando abrir " + filename);
//            InputStream input = new FileInputStream(filename);
            InputStream input = this.getClass().getResourceAsStream("rdflicense32.png");
            if (input==null)
                return;
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return;
        }
       // if (true)
       //     return; 
        
    	HttpServletRequest wrapped = new HttpServletRequestWrapper(req) {
    		public String getServletPath() { return ""; }
    	};
 
    	rd.forward(wrapped, resp);
    }
}    

