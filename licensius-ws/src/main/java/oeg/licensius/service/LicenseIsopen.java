
package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.licensius.core.Licensius;
import oeg.licensius.model.LicensiusSimpleResponse;
import oeg.licensius.rdflicense.RDFLicense;
import oeg.licensius.rdflicense.RDFLicenseCheck;
import org.apache.log4j.Logger;

/**
 * Determines if a license is open or not
 * Working in 2.0
 * 
 * @author vroddon
 */
public class LicenseIsopen extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseIsopen.class.getName());

    /**
     * Determina si una licencia es abierta o no.
     * http://www.licensius.com/api/license/isopen?uri=http%3A%2F%2Fpurl.org%2FNET%2Frdflicense%2Fcc-by3.0
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            Licensius.init();
            PrintWriter w = resp.getWriter();
            String uri = req.getParameter("uri");
            logger.info("Me han preguntado si es abierto: " + uri);
            System.out.println("consola Me han preguntado si es abierto: " + uri);
            RDFLicense rdflicense = new RDFLicense(uri);
            if (rdflicense.isOK())
            {
                RDFLicenseCheck check = new RDFLicenseCheck(rdflicense);
                LicensiusSimpleResponse ls = new LicensiusSimpleResponse();
                ls.text=""+check.isOpen();
                resp.setStatus(200);
                resp.setContentType("text/plain");
                w.println(ls.getJSON());
            }
            else
            {
                resp.setStatus(200);
                resp.setContentType("text/plain");
                w.println("License not found.");
            }
            return;             
        }

}
