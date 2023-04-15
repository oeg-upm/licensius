package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oeg.licensius.core.LicenseFinder;
import oeg.licensius.core.LicenseGuess;
import oeg.licensius.model.LicensiusError;
import oeg.licensius.model.LicensiusResponse;

/**
 * Discovers a license in a piece of text 
 * @author vroddon
 */
public class LicenseFindlicenseintxt extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseFindlicenseinrdf.class.getName());

    /**
     * Discovers a license in a piece of text 
     * 
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
                w.println(txt +" " + e.getJSON());
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
            LicenseFinder lf = new LicenseFinder();
            LicensiusResponse le = LicenseGuess.guessLicense2(txt);
            resp.setStatus(200);
            resp.setContentType("application/json");
            PrintWriter w = resp.getWriter();
            w.println(le.getJSON());
            w.println(txt);
            return;
    }

    public static void main(String[] args) {
        String uri = "Academic Free License";
        LicenseFinder lf = new LicenseFinder();
        LicensiusResponse le = LicenseGuess.guessLicense2(uri);
        System.out.println(le.getJSON());
        
    }    
}
