package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import main.LicenseGuess;
import oeg.licensius.model.LicensiusError;
import oeg.licensius.model.LicensiusResponse;
import main.ServiceCommons;
import oeg.license.LicenseFinder;

/**
 * 
 * @author vrodriguez
 */
public class LicenseFindlicenseintxt extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseFindlicenseinrdf.class.getName());

    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String txt = req.getParameter("txt");
            if (ServiceCommons.isURI(txt))
            {
                txt = ServiceCommons.downloadFromURL(txt);
            }
            LicenseFinder lf = new LicenseFinder();
            LicensiusResponse le = LicenseGuess.guessLicense2(txt);
            if (le.getClass().equals(LicensiusError.class))
            {
                LicensiusError e = (LicensiusError) le;
                resp.setStatus(500);
                PrintWriter w = resp.getWriter();
                w.println(e.getJSON());
                return;
            }
            resp.setStatus(200);
            resp.setContentType("application/json");
            PrintWriter w = resp.getWriter();
            w.println(le.getJSON());
            return;
        }    
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String txt = ServiceCommons.getBody(req);
            //String txt = req.getParameter("txt");
            LicenseFinder lf = new LicenseFinder();
            LicensiusResponse le = LicenseGuess.guessLicense2(txt);
            if (le.getClass().equals(LicensiusError.class))
            {
                LicensiusError e = (LicensiusError) le;
                resp.setStatus(500);
                PrintWriter w = resp.getWriter();
                w.println(e.getJSON());
                return;
            }
            resp.setStatus(200);
            resp.setContentType("application/json");
            PrintWriter w = resp.getWriter();
            w.println(le.getJSON());
            return;
    }

    public static void main(String[] args) {
        String uri = "Creative commons cc-by";
        LicenseFinder lf = new LicenseFinder();
        LicensiusResponse le = LicenseGuess.guessLicense2(uri);
        System.out.println(le.getJSON());
        
    }    
}
