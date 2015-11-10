package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.licensius.model.LicensiusError;
import oeg.licensius.model.LicensiusResponse;
import main.ServiceCommons;
import oeg.license.LicenseFinder;

/**
 *
 * @author vrodriguez
 */
public class LicenseFindlicenseinrdf extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseFindlicenseinrdf.class.getName());

    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String uri = req.getParameter("uri");
            LicenseFinder lf = new LicenseFinder();
            LicensiusResponse le = lf.findLicenseInRDF(uri);
            if (le.getClass().equals(LicensiusError.class))
            {
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
    
}
