package main;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URI;
import java.util.logging.Level;
import javax.servlet.ServletException;
import oeg.license.LicenseFinder;
import oeg.rdflicense.RDFLicenseDataset;
import oeg.rdflicense.RDFUtils;

/**
 * @api {get} /getLicenseTitle getLicenseTitle 
 * @apiName /getLicenseTitle
 * @apiGroup Licensius
 * @apiVersion 1.0.0
 * @apiDescription Gets the license title from an online RDF document.  
 * The most famous predicates are explored (dct:license, dc:rights, cc:license, etc.) searching for a predicate qualifying the OWL Ontology class (if present), the RDF document (if the URI is found) or, in the last case, any other statement.
 * @apiParam {String} content Encoded URI of the RDF document if it is online
 *
 * @apiSuccess {String} licenseTitle Title of the License
 */
public class GetLicenseTitleServlet extends HttpServlet {

    /**
     * Ejemplo de URI que prueba esto
     * http://licensius.appspot.com/getLicense?content=http%3A%2F%2Fwww.geonames.org%2Fontology%2Fontology_v3.1.rdf
     * http://licensius.appspot.com/getLicense?content=http%3A%2F%2Fpurl.org%2Fgoodrelations%2Fv1.owl
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uritoscan = req.getParameter("content");

        boolean isURI = false;
        try {
            URI uri = new URI(uritoscan);
            isURI = true;
        } catch (Exception e) {
            isURI = false;
        }
        LicenseFinder lf = new LicenseFinder();
        String resultado = lf.findLicenseTitle(uritoscan);
        resp.setStatus(200);
        resp.setContentType("text/html");
        PrintWriter w = resp.getWriter();
        w.println(resultado);
    }

    public static void main(String[] args) {
        String uritoscan = "http://purl.org/net/p-plan";
        String resultado = "Unknown";
        LicenseFinder lf = new LicenseFinder();
        String license = lf.findLicense(uritoscan);
        if (!license.isEmpty()) {
            System.out.println("License: " + license);
            String rdflicense = RDFLicenseDataset.getRDFLicenseByLicense(license);
            if (!rdflicense.isEmpty()) {
                System.out.println("RDFLicense: " + rdflicense);
                resultado = RDFUtils.getLabel(RDFLicenseDataset.modelTotal, rdflicense);
            }
        }
        System.out.println("Label: " + resultado);
    }
}
