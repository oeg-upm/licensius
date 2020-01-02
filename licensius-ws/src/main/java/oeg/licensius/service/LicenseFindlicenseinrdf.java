package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.licensius.core.LicenseFinder;
import oeg.licensius.core.Licensius;
import oeg.licensius.model.LicensiusError;
import oeg.licensius.model.LicensiusResponse; 

/**
 * Discovers a license in a piece of RDF 
 * @author vroddon
 */
public class LicenseFindlicenseinrdf extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LicenseFindlicenseinrdf.class.getName());

    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//      the following line is not valid because we cannot open files!!
//      Licensius.init();
        System.out.println("Serving the service with the class: LicenseFindlicenseinrdf " + Licensius.version);
        String uri = req.getParameter("uri");
        LicenseFinder lf = new LicenseFinder();
        LicensiusResponse le = lf.findLicenseInRDF(uri);
        if (le.getClass().equals(LicensiusError.class)) {
            LicensiusError e = (LicensiusError) le;
            resp.setStatus(500);
            PrintWriter w = resp.getWriter();
            w.println(e.getJSON());
            return;
        }
        resp.setStatus(200);
        //resp.setContentType("application/json");
        resp.setContentType("text/html");
        PrintWriter w = resp.getWriter();
        w.println(le.getJSON());
        return;
    }

    /**
     * Sirve una cadena de prueba.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LicenseFinder lf = new LicenseFinder();
        String body = ServiceCommons.getBody(req);
        LicensiusResponse le = lf.findLicenseFromRDFText2(body);
        if (le.getClass().equals(LicensiusError.class)) {
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
        String uri = "http://purl.org/NET/p-plan";
        String urihttps = "https://spec.edmcouncil.org/fibo/ontology/master/latest/BE/Corporations/Corporations/";
        LicenseFinder lf = new LicenseFinder();
        LicensiusResponse le = lf.findLicenseInRDF(urihttps);
        System.out.println(le.getJSON());

    }

}
