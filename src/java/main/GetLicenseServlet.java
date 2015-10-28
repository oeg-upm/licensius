package main;

//import com.google.appengine.labs.repackaged.org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;

import oeg.license.LicenseFinder;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;

import org.apache.log4j.Logger;

// apiSampleRequest http://licensius.appspot.com/getLicense
/**
 * @api {get} /getLicense getLicense
 * @apiName /getLicense
 * @apiGroup Licensius
 * @apiVersion 1.0.0
 * @apiDescription Gets the license from an online RDF document. The most famous predicates are explored (dct:license, dc:rights, cc:license, etc.) searching for a predicate
 * qualifying the OWL Ontology class (if present), the RDF document (if the URI is found) or, in the last case, any other statement.
 * <br/>For HTTP requests that accept JSON, a JSON is provided.
 * For the rest of HTTP requests, only the URI of the famous license is returned 
 * <br/>In particular, the following properties are searched: http://purl.org/dc/terms/rights, http://purl.org/dc/terms/license, 
 * http://creativecommons.org/ns#license, http://www.w3.org/1999/xhtml/vocab/#license, http://purl.org/dc/elements/1.1/rights.
 * <br>Other properties that could have been scanned (and are NOT) include: doap:license, premis:licenseTerms,omv:hasLicense, mo:License, vaem:hasLicenseType, dcterms:RightsStatement, dcterms:LicenseDocument.

* @apiParam {String} content Encoded URI of the RDF document if it is online
 *
 * @apiSuccess {String} licenseURI URI of the recognized license, or a JSON if requested {URI of the famous license, URI to license in RDF, License Title}
 */
public class GetLicenseServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetLicenseServlet.class.getName());
    static boolean bFirst = false;

    /**
     * Exemplary URI testing this:
     * http://licensius.appspot.com/getLicense?content=http%3A%2F%2Fwww.geonames.org%2Fontology%2Fontology_v3.1.rdf;
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String resultado = "unknown";
        String content = req.getParameter("content");
        boolean isURI = false;
        try {
            URI uri = new URI(content);
            isURI = true;
        } catch (Exception e) {
            isURI = false;
            logger.warn(e.getMessage());
        }
        LicenseFinder lf = new LicenseFinder();
        if (isURI) {
            resultado = lf.findLicense(content);
        } else {
            resultado = lf.findLicenseFromRDFText(content);
            resp.setStatus(200);
            resp.setContentType("text/html");
            PrintWriter w = resp.getWriter();
            w.println(resultado);
            return;
        }

        if (ServiceCommons.isJSON(req) && !resultado.isEmpty()) {
            logger.info("json has been requested");
            RDFLicense rdflicense = RDFLicenseDataset.getRDFLicenseByURI(resultado);
            if (rdflicense!=null)
            {
                logger.info("rdflicense could be found!");
                resp.setStatus(200);
                resp.setContentType("application/json");
                resultado = rdflicense.toJSON();
                PrintWriter w = resp.getWriter();
                w.println(resultado);
                return;
            }
        }
        resp.setStatus(200);
        resp.setContentType("text/html");
        PrintWriter w = resp.getWriter();
        w.println(resultado);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        String content = req.getParameter("content");
        System.out.println("Content: " + content);
        content.trim();
        LicenseFinder lf = new LicenseFinder();
        String resultado = lf.findLicense(content);
        String message = resultado;
        if (resultado.isEmpty()) {
            message = " - No license found";
        }
        req.setAttribute("message1", "<strong>License analysis of " + content + " - " + message + "</strong>");
        try {
            req.getRequestDispatcher("/licensefinder.jsp").forward(req, resp);
        } catch (ServletException ex) {
            logger.warn(ex.getMessage());
        }
    }
}


