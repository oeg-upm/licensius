package oeg.licensius.core;

import oeg.licensius.model.LicensiusResponse;
import oeg.licensius.model.LicensiusError;
import oeg.licensius.model.LicensiusFound;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import oeg.licensius.rdflicense.RDFLicense;
import oeg.licensius.rdflicense.RDFLicenseDataset;
import oeg.licensius.rdflicense.RDFUtils;
import java.util.List;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import oeg.licensius.util.URLutils;
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
public class LicenseGuess 
{
    private static final Logger logger = Logger.getLogger(Licensius.class);

    static Map<String, String> mapa = null ;

    /**
     * Gets a license given a fragment of a uri
     */
    public String licenseguessfromuri(String uri)  {
        if (mapa == null) 
            mapa = loadMap();
        String out = "unknown";
        if (mapa==null) {
            out = "Could not load the info";
        }
        
        logger.info("Downloading document from " + uri +" ...");
        String doc = URLutils.downloadFromURL(uri);
        logger.info("We have downloaded " + doc.length() +" characters");
        
        String license = guessLicense(doc);
        if (license.isEmpty())
            out = "unknown";
        else
        {
            RDFLicense licencia = RDFLicenseDataset.getRDFLicenseByURI(license);
            if (licencia!=null)
            {
//              out = licencia.toJSON();
                out = licencia.getURI();
            }
            else
                out = license;
        }
        return out;
    }
    

    

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
        
        //FIRST we check if there are TEXTS that are totally equivalent
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
        
        //FIRST.5 we check if there are URLs that are totally equivalent (ignoring http by https)
        String unknownText2 = unknownText.replace("https", "");
        unknownText2 = unknownText2.replace("http", "");
        it = mapa.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String licenseText = (String) e.getValue();
            licenseText = licenseText.trim();
            licenseText = licenseText.toLowerCase();
            licenseText = licenseText.replace("https", "");
            licenseText = licenseText.replace("http", "");
            if (unknownText2.contains(licenseText)) {
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
     * Guesses the license from an uncertain text
     * @param unknownText A text. For example "CC", "CCZero", "This document is licensed under a GNU GDL" etc.
     * @return Uri of a well-known license
     */
    public static LicensiusResponse guessLicense2(String unknownText)
    {
        LicensiusFound lf = new LicensiusFound();
         
        if (mapa == null) 
            mapa = loadMap();
        if (mapa == null)
        {
            LicensiusError error = new LicensiusError(404, "Internal file not found");
            return error;
        }
        if (unknownText==null)
            unknownText="";
        String out = "";
        unknownText = unknownText.trim();
        unknownText = unknownText.replace("\"", "");
        unknownText = unknownText.replace("\"", "");
        unknownText = unknownText.toLowerCase();
        
        
        //para evitar duplicados
        Set<String> encontradas = new HashSet();
        
        
        //FIRST we check if they are totally equivalent
        int n = l1.size();
        for(int i=0;i<n;i++)
        {
            String licenseText = (String) l1.get(i);
            String lic = (String)l2.get(i);
            licenseText = licenseText.trim();
            licenseText = licenseText.toLowerCase();
            if (licenseText.equalsIgnoreCase(unknownText)) {
                out = lic;
                String rdf = RDFLicenseDataset.getRDFLicenseByLicense(out);
            if (rdf!=null && !rdf.isEmpty())
                if (!encontradas.contains(rdf))
                {
                    encontradas.add(rdf);
                    lf.add(rdf,"","90");
                }
            }
        }
         
        //SECOND iteration, with and without empty spaces
        for(int i=0;i<n;i++)
        {
            String licenseText = (String) l1.get(i);
            String lic = (String)l2.get(i);
            String cmp2=unknownText;
            licenseText = licenseText.trim();
            licenseText = licenseText.toLowerCase();
            licenseText = licenseText.replace(" ", "");
            cmp2 = cmp2.replace(" ", "");
            if (licenseText.equalsIgnoreCase(cmp2)) {
                out = lic;
                
            String rdf = RDFLicenseDataset.getRDFLicenseByLicense(out);
            if (rdf!=null && !rdf.isEmpty())
            {
                if (!encontradas.contains(rdf))
                {
                    encontradas.add(rdf);
                lf.add(rdf,"","70");
                }
            }
                
                
            }
        }

        // FINALLY, WE CHECK IF ONE STRING CONTAINS THE OTHER OR VICEVERSA
        if (unknownText.length()<5)
        {
            return lf;
        }
         for(int i=0;i<n;i++)
        {
            String licenseText = (String) l1.get(i);
            String lic = (String)l2.get(i);
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
                out = lic;

                String rdf = RDFLicenseDataset.getRDFLicenseByLicense(out);
            if (rdf!=null && !rdf.isEmpty())
            {
                if (!encontradas.contains(rdf))
                {
                    encontradas.add(rdf);
                    lf.add(rdf,"","50");
                }
            }
                
            }
        }
        return lf;
    }    

 
    /***************************************************************************/
    /***************************************************************************/
    /***************************************************************************/
    
    
    static List<String> l1 = new ArrayList(); 
    static List<String> l2 = new ArrayList();
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
                l1.add(s1);
                l2.add(s0);
            }
            return mapa;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return null;
        }
    }
    
    public static void main(String[] args) {    
        Licensius.init();
        LicenseGuess lg = new LicenseGuess();
        String s=lg.licenseguessfromuri("https://github.com/pyvandenbussche/lov"); 
        System.out.println(s);
    }
    
    public static void managemassive(String[] args) {
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
                                oficial = rdflicense.getLegalCodeLanguages();
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
