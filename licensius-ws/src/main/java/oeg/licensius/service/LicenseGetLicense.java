package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DEPRECATED METHODS
 * @author Victor
 */
public class LicenseGetLicense extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseGetLicense.class.getName());
    /**
     * Discovers a license in a piece of text 
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String txt = req.getParameter("txt");
            String respuesta="g";
            resp.setStatus(200);
            resp.setContentType("text/plain;charset=utf-8");//application/json
            resp.setCharacterEncoding("utf-8");
            PrintWriter w = resp.getWriter();
            w.println(respuesta);
    }
    /**
     * Discovers a license in a piece of text 
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String txt = req.getParameter("txt");
            String respuesta="p";
            resp.setStatus(200);
            resp.setContentType("text/plain;charset=utf-8");//application/json
            resp.setCharacterEncoding("utf-8");
            PrintWriter w = resp.getWriter();
            w.println(respuesta);
    }    
}
