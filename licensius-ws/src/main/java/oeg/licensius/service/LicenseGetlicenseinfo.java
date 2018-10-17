package oeg.licensius.service;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.licensius.rdflicense.RDFLicense;
import oeg.licensius.rdflicense.RDFLicenseDataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 *
 * @author vroddon
 */
public class LicenseGetlicenseinfo extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseGetlicenseinfo.class.getName());

    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String uri = req.getParameter("uri");
            logger.info(uri);
            RDFLicense rdf = RDFLicenseDataset.getRDFLicense(uri);
            if(rdf==null)
            {
                resp.setStatus(404);
                return;
            }
            else
            {
                resp.setStatus(200);
                resp.setContentType("application/json");
                PrintWriter w = resp.getWriter();
                w.println(rdf.toLongJSON());
                return;
            }
        }
    
    public static void main(String[] args) {
        String uri = "http://purl.org/NET/rdflicense/gpl2.0";
        RDFLicense rdf = RDFLicenseDataset.getRDFLicense(uri);
        Model model = rdf.getModel();
        RDFDataMgr.write(System.out, model, Lang.TURTLE) ;
        String s = rdf.toLongJSON();
        System.out.println(s);
    }
    
    
}
