package main;

import java.io.IOException;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;

import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;
import oeg.rdflicense.RDFUtils;

import com.hp.hpl.jena.rdf.model.Model;

import org.apache.log4j.Logger;

/**
 * http://creativecommons.org/publicdomain/zero/1.0/
 * @author Victor
 */
/**
 * @api {get} /licenseguess licenseguess
 * @apiName /licenseguess
 * @apiGroup Licensius
 * @apiVersion 1.0.0
 * @apiDescription Tries to guess a famous license from a text fragment. This text fragment is compared against license titles, license URIs and finally license legal codes.
 * For HTTP requests that accept JSON, a JSON is provided. (Use "Accept: application/json") in the request<br>
 * For the rest of HTTP requests, only the URI of the famous license is returned 
 * 
 * 
 * @apiParam {String} txt Text to be guessed from
 *
 * @apiSuccess {String} license Famous license string or JSON string with {URI of the famous license, URI to license in RDF, License Title}
 */
public class LicenseGuess extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LicenseGuess.class.getName());


    /**
     * Gets a license given a fragment of a text
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (mapa == null) 
            mapa = loadMap();
        String out = "unknown";
        if (mapa==null) {
            out = "Could not load the info";
        }
        String content = req.getParameter("txt");
        
        String license = guessLicense(content);
        if (license.isEmpty())
            out = "unknown";
        else
        {
            RDFLicense licencia = RDFLicenseDataset.getRDFLicenseByURI(license);
            if (licencia!=null)
            {
                if (ServiceCommons.isJSON(req))
                    out = licencia.toJSON();
                else
                    out = licencia.getSeeAlso();
            }
        }
        resp.setStatus(200);
        resp.setContentType("text/html");
        PrintWriter w = resp.getWriter();
        w.println(out);
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String out = "I have not a clue";
        if (mapa == null) {
            mapa = loadMap();
        }
        String content = req.getParameter("txt");
        content.trim();
        
        String license = guessLicense(content);
        out = license;

        req.setAttribute("message4", "I guess this: " + out);
        
        try {
            req.getRequestDispatcher("/licensefinder.jsp").forward(req, resp);
        } catch (ServletException ex) {
            logger.warn(ex.getMessage());
        }
    }
    

    static Map<String, String> mapa = null ;
    /**
     * Guesses the license from an uncertain text
     * @param unknownText A text. For example "CC", "CCZero" etc.
     * @return Uri of a well-known license
     */
    public static String guessLicense(String unknownText)
    {
        if (mapa == null) 
            mapa = loadMap();
        if (mapa == null)
            return "";
        
        String out = "";
        unknownText = unknownText.trim();
        unknownText = unknownText.replace("\"", "");
        unknownText = unknownText.replace("\"", "");
        unknownText = unknownText.toLowerCase();
        
        
        //FIRST we check if they are totally equivalent
        Iterator it = mapa.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String licenseText = (String) e.getKey();
            licenseText = licenseText.trim();
            licenseText = licenseText.toLowerCase();
            if (licenseText.equalsIgnoreCase(unknownText)) {
                out = (String) e.getValue();
                return out;
            }
        }
        
        //SECOND iteration, with and without empty spaces
        it = mapa.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String licenseText = (String) e.getKey();
            String cmp2=unknownText;
            licenseText = licenseText.trim();
            licenseText = licenseText.toLowerCase();
            licenseText = licenseText.replace(" ", "");
            cmp2 = cmp2.replace(" ", "");
            if (licenseText.equalsIgnoreCase(cmp2)) {
                out = (String) e.getValue();
                return out;
            }
        }

        // FINALLY, WE CHECK IF ONE STRING CONTAINS THE OTHER OR VICEVERSA
        if (unknownText.length()<5)
            return out;
        it = mapa.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String licenseText = (String) e.getKey();
            String cmp2 = unknownText;
            cmp2 = cmp2.toLowerCase();
            
            if (licenseText.length()>5 && cmp2.length()<6)
                continue;
            
            
            licenseText = licenseText.trim();
            licenseText = licenseText.toLowerCase();

            licenseText = licenseText.replace(" ", "");
            cmp2 = cmp2.replace(" ", "");
            licenseText = licenseText.replace("-", "");
            cmp2 = cmp2.replace("-", "");
            licenseText = licenseText.replace(".", "");
            cmp2 = cmp2.replace(".", "");
            licenseText = licenseText.replace("/", "");
            cmp2 = cmp2.replace("/", "");
            
            if (licenseText.contains(unknownText) || cmp2.contains(licenseText)) {
                out = (String) e.getValue();
                return out;
            }
        }
        return out;
    }

    /**
     * Obtiene el título
     */
    public String getTitle(Model model, String uri) {
        String rdflicense = RDFLicenseDataset.getRDFLicenseByLicense(uri);
        String title = RDFUtils.getLabel(model, rdflicense);
        return title;
    }

    /***************************************************************************/
    /***************************************************************************/
    /***************************************************************************/
    
    /**
     * Loads the map from the given file
     * @return A map with two strings: candidate string and license to which it belongs to
     */
    private static Map<String, String> loadMap() {
        try {
            mapa = new LinkedHashMap();
            InputStream is = LicenseGuess.class.getResourceAsStream("strlic.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String linea = "";
            while ((linea = br.readLine()) != null) {
                int i = linea.indexOf("\t");
                if (i == -1) {
                    continue;
                }
                String s0 = linea.substring(0, i);
                String s1 = linea.substring(i + 1, linea.length());
                mapa.put(s1, s0);
            }
            return mapa;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return null;
        }
    }
    public static void main(String[] args) {
        LicenseGuess lg = new LicenseGuess();
//        String license = lg.guessLicense("creo que lgpl");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("C:\\Desktop\\rights.csv"));
            String line;
            while ((line = br.readLine()) != null) {
            // process the line.
                int i = line.indexOf(',');
                if(i!=-1)
                {
                    line = line.substring(i+1, line.length());
//                    line = "https://www.gnu.org/licenses/lgpl.html";
//                    line="This resource is licensed under a Creative Commons Attribution 3.0 Unported License (http://creativecommons.org/licenses/by/3.0/). The availability status of the resource is: 'available-unrestrictedUse'.";
                    line = line.replace("\"\"\"", "");
                    String lic = lg.guessLicense(line);
                    if (lic.isEmpty())
                        lic=line+"\tNONE";
                    else
                    {
                        RDFLicense rdflicense = RDFLicenseDataset.getRDFLicenseByURI(lic);
                        String x = rdflicense.toTTL();
                        if (rdflicense!=null)
                        {
                            String oficial = rdflicense.getSeeAlso();
                            if (oficial.isEmpty())
                                oficial = rdflicense.getLegalCode();
                            lic = line+"\t"+rdflicense.getLabel() + "\t" + oficial + "\t" + rdflicense.getURI();
                        }
                    }
                    System.out.println(lic);
                }
            }
            br.close();   
            } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
