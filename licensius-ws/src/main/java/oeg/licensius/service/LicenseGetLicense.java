package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.licensius.core.LicenseFinder;
import oeg.licensius.model.LicensiusResponse;

/**
 * DEPRECATED METHODS
 * @author Victor
 */
public class LicenseGetLicense extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseGetLicense.class.getName());
    
    
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("fuck u");
        String uritoscan = req.getParameter("content");
        if (uritoscan==null)
            doPost1(req, resp);
        else
            doGet1(req,resp);
    }
    
    /**
     * Discovers a license in a piece of text 
     */
    public void doGet1(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String uritoscan = req.getParameter("content");
            String resultado = "unknown";
            if (uritoscan!=null)
            {
                LicenseFinder lf = new LicenseFinder();
                resultado = lf.findLicenseTitle(uritoscan);
            }
            
//            LicensiusResponse le = lf.findLicenseInRDF(uritoscan);
//            resultado = le.getJSON();
            
            
            String respuesta=resultado;
            resp.setStatus(200);
            resp.setContentType("text/plain;charset=utf-8");//application/json
            resp.setCharacterEncoding("utf-8");
            PrintWriter w = resp.getWriter();
            w.println(respuesta);
    }
    /**
     * Discovers a license in a piece of text 
     */
    public void doPost1(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//            String txt = req.getParameter("rdf");
            String body = ServiceCommons.getBody(req);

            LicenseFinder lf = new LicenseFinder();
            lf.parseFromText(body); 
            String resultado = lf.findLicenseTitleRaw(body);
            
            String respuesta=resultado;
            
            
            resp.setStatus(200);
            resp.setContentType("text/plain;charset=utf-8");//application/json
            resp.setCharacterEncoding("utf-8");
            PrintWriter w = resp.getWriter();
            w.println(respuesta);
    }    
}
