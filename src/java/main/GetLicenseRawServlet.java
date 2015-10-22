package main;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import oeg.license.LicenseFinder;


/**
 * This class represents a license servlet.
 * 
 * @api {post} /getLicenseRaw getLicenseRaw 
 * @apiName getLicenseRaw
 * @apiGroup Licensius
 * @apiVersion 1.0.0
 * @apiDescription Gets the license URL from a raw RDF document. The most famous predicates are explored (dct:license, dc:rights, cc:license, etc.) searching for a predicate
 * qualifying the OWL Ontology class (if present), the RDF document (if the URI is found) or, in the last case, any other statement.
 * In particular, the following properties are searched: http://purl.org/dc/terms/rights, http://purl.org/dc/terms/license, 
 * http://creativecommons.org/ns#license, http://www.w3.org/1999/xhtml/vocab#license, http://purl.org/dc/elements/1.1/rights.
 * <br>Other properties that could have been scanned (and are NOT) include: doap:license, premis:licenseTerms,omv:hasLicense, mo:License, vaem:hasLicenseType, dcterms:RightsStatement, dcterms:LicenseDocument.

 * 
 * @apiParam {String} None. The whole RDF document has to be passed in the body of the call. 
 * @apiSuccess {String} licenseURI URI of the recognized license
 * 
 * @author Victor
 */

public class GetLicenseRawServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetLicenseRawServlet.class.getName());

    /**
     * 
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.info("Recibido el post");
        String content = getBody(req);
        PrintWriter w = resp.getWriter();
        logger.info("Contenido a analizar: " + content);
        LicenseFinder lf = new LicenseFinder();
        String resultado = lf.findLicenseFromText(content);
        if (resultado.isEmpty())
        resultado="unknown";
        w.println(resultado);
    }

    /***
     * Obtains the body from a HTTP request. Period. No more, no less.
     */
    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        body = stringBuilder.toString();
        return body;
    }

}
