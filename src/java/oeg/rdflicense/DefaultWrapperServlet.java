package oeg.rdflicense;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;



public class DefaultWrapperServlet extends HttpServlet
{   
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    	throws ServletException, IOException
    {
    	RequestDispatcher rd = getServletContext().getNamedDispatcher("default");

    	HttpServletRequest wrapped = new HttpServletRequestWrapper(req) {
    		public String getServletPath() { return ""; }
    	};

    	rd.forward(wrapped, resp);
    }
}    

