package oeg.licensius.rdflicense;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.Set;


import org.apache.commons.io.IOUtils;

import org.apache.log4j.Logger;

import oeg.licensius.core.Licensius;
import oeg.licensius.util.URLutils;

/**
 * Class centralizing methods around the RDFLicenseDataset dataset
 *
 * @author Victor
 */
public class RDFLicenseDataset {
 
    public static Model modelTotal = null;
    public static String raw = "";
    private static final Logger logger = Logger.getLogger(RDFLicenseDataset.class.getName());
    public static Map<String, RDFLicense> glicenses = new HashMap();

    public static void main(String[] args) {
        Licensius.init();
        RDFLicense rdflicense = null;
//        rdflicense = RDFLicenseDataset.getRDFLicenseByURI("http://creativecommons.org/publicdomain/zero/1.0/");

        //rdflicense = getRDFLicense("http://purl.org/NET/rdflicense/cc-by4.0");
        rdflicense = RDFLicenseDataset.getRDFLicenseByURI("https://www.gnu.org/licenses/lgpl.html");

        if (rdflicense != null) {
            System.out.println(rdflicense.toTTL());
        }
    }

    /**
     * Intenta cargarlo del local. Y si no sabe, lo coge del remoto
     */
    public RDFLicenseDataset() {
        System.out.println("Modelo total vacio: " + (modelTotal == null));
        init();
    }

    public static synchronized void init()
    {
        if (modelTotal == null) {
            modelTotal = readRDFLicenseLocal();
            if (modelTotal == null) {
                System.err.println("Leyendo el dataset de remoto");
                modelTotal = readRDFLicenseRemote();
            }
        }
    }
    
    
    public boolean isValid() {
        return modelTotal != null;
    }
    
    
    public List<RDFLicense> listRDFLicenses() {
        List<RDFLicense> ret = new ArrayList();
        Iterator it = glicenses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();        
            RDFLicense rdflicense = (RDFLicense)e.getValue();
            ret.add(rdflicense);
        }
        return ret;
    }

    public Set<String> listURILicenses()
    {
        return glicenses.keySet();
    }

    

    /**
     * Retrieves a list of all the RDFLicenses present in
     * http://rdflicense.appspot.com/
     */
 /*   public List<RDFLicense> listRDFLicenses() {
        List<RDFLicense> licenses = new ArrayList();
        List<Resource> rdflicenses = new ArrayList();
        List<Resource> licenseclasses = ODRL.getCommonPolicyClasses();
        for (Resource licenseclass : licenseclasses) {
            ResIterator it = modelTotal.listSubjectsWithProperty(RDF.type, licenseclass);
            while (it.hasNext()) {
                Resource res = it.next();
                rdflicenses.add(res);
            }
        }
        for (Resource res : rdflicenses) {
            RDFLicense rdflicense = getRDFLicense(res.getURI());
            if (rdflicense != null) {
                licenses.add(rdflicense);
            }
        }
        return licenses;
    }*/

    /**
     * Obtiene un RDFLicense dado su URI. Lo busca en el modelo total pero de
     * una manera muy muy muy chapucera.
     */
    public static RDFLicense getRDFLicense(String rdfuri) {
        init();
        
        RDFLicense rdflicense = glicenses.get(rdfuri);
        if (rdflicense != null) {
            return rdflicense;
        }

        String abuscar = "\n" + "<" + rdfuri + ">";
        logger.debug("Searching: " + abuscar);
        int index = raw.indexOf(abuscar);

        if (index == -1) {
            String xx = RDFUtils.getLastPart(rdfuri);
            abuscar = ":" + xx;
            index = raw.indexOf(abuscar);
        }

        String extension = "ttl";
        if (abuscar.length() < 3) {
            return null;
        }
        extension = abuscar.substring(abuscar.length() - 4, abuscar.length() - 1);
        if (index == -1) {
            logger.warn("License " + rdfuri + " not found");
            return null;
        }
        int index2 = raw.indexOf(". \n", index);
        if (index2 == -1) {
            logger.warn("License " + rdfuri + " end is not found");
            return null;
        }
        //fragmento is the fragment in the raw file.
        String fragmento = raw.substring(index, index2 + 1);
        fragmento = RDFUtils.getLicensePrefixes(fragmento) + fragmento;

        rdflicense = new RDFLicense();
        rdflicense.model = RDFUtils.parseFromText(fragmento);
        rdflicense.uri = rdfuri;
        return rdflicense;
    }

    /**
     * Dada una URI (por ejemplo)
     */
    public static RDFLicense getRDFLicenseByURI(String uri) {
        init();

        String rdfuri = getRDFLicenseByLicenseLax(uri);
        if (rdfuri.isEmpty()) {
            return null;
        }
        RDFLicense license = getRDFLicense(rdfuri);
        return license;
    }

    /**
     * Busca cualquier licencia que tiene como propiedad al texto dado
     */
    public static String getRDFLicenseByLicenseLax(String texto) {
        String uri = "";
        String[] parts = texto.split("\\s+");
        for (String item : parts) {
            try {
                URL url = new URL(item);
                uri = url.toString();
                break;
            } catch (MalformedURLException e) {
            }
        }
        init();

        if (uri.isEmpty()) {
            return "";
        }

//        RDFDataMgr.write(System.out, modelTotal, Lang.TURTLE);
//        List<Resource> lresx = RDFUtils.getAllSubjectsWithObject(modelTotal, "http://creativecommons.org/licenses/by/4.0/");
//        System.out.println(lresx.size());
        // COMBINACION0: TAL Y COMO ESTÁ
        List<Resource> lres = RDFUtils.guessRDFLicenseFromURI(modelTotal, uri);
        if (lres.isEmpty()) {
            if (uri.endsWith("/")) //COMBINACION1: COMO ESTÁ PERO CAMBIANDO LA BARRA FINAL
            {
                String uri2 = uri.substring(0, uri.length() - 1);
                lres = RDFUtils.guessRDFLicenseFromURI(modelTotal, uri2);
            } else {
                String uri2 = uri + "/";
                lres = RDFUtils.guessRDFLicenseFromURI(modelTotal, uri2);
            }

            //COMBINACION2: COMO ESTA PERO CAMBIANDO http y https
            if (lres.isEmpty()) //probamos http y https
            {
                String uri2 = "";
                if (uri.contains("http:")) {
                    uri2 = uri.replace("http:", "https:");
                } else if (uri.contains("https:")) {
                    uri2 = uri.replace("https:", "http:");
                }
                lres = RDFUtils.guessRDFLicenseFromURI(modelTotal, uri2);
            }
            if (lres.isEmpty()) //COMBINACION3: CAMBIANDO HTTP/HTTPS Y CAMBIANDO LA BARRA FINAL
            {
                String uri2 = "";
                if (uri.contains("http:")) {
                    uri2 = uri.replace("http:", "https:");
                } else if (uri.contains("https:")) {
                    uri2 = uri.replace("https:", "http:");
                }
                if (uri2.endsWith("/")) {
                    uri2 = uri2.substring(0, uri2.length() - 1);
                } else {
                    uri2 = uri2 + "/";
                }
                lres = RDFUtils.guessRDFLicenseFromURI(modelTotal, uri2);

            }

        }
        if (lres.isEmpty()) {
            return "";
        }
        return lres.get(0).getURI();
    }

    public static String getRDFLicenseByLicense(String uri) {
        init();
        List<Resource> lres = RDFUtils.guessRDFLicenseFromURI(modelTotal, uri);
        if (lres.isEmpty()) {
            return "";
        }
        return lres.get(0).getURI();
    }

    public static synchronized Model readRDFLicenseLocal() {
        Model modelx = ModelFactory.createDefaultModel();
        glicenses.clear();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("rdflicenses/list.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = "";
            if (is == null) {
                System.err.println("No se ha podido abrir el archivo list.txt");
            }
            if (is != null) {
                int conta = 0;
                while ((str = reader.readLine()) != null) {
                    if (str.isEmpty()) {
                        continue;
                    }
                    try {
                        String uri = "http://purl.org/NET/rdflicense/" + str;
                        uri = uri.replace(".ttl", "");
                        RDFLicense rdflicense = new RDFLicense(uri);
                        if (rdflicense.isOK())
                        {
                            System.out.println("OK "+conta+" " + uri);
                            glicenses.put(uri, rdflicense);
                            conta++;
                            modelx.add(rdflicense.getModel());
                        }
                        else
                        {
                            System.out.println("NOT OK" + uri);
                        }
                    } catch (Exception e) {
                        System.err.println("La licencia " + str + " no contenía un RDF correcto");
                        System.err.println(e.getMessage());
                    }
                }
                System.err.println("Total licencias correctas: " + conta);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return modelx;
    }

    public static synchronized Model readRDFLicenseRemote() {
        try {
            Model modelx = ModelFactory.createDefaultModel();
            raw = URLutils.getFile("http://rdflicense.appspot.com/rdflicense/"); //http://rdflicense.linkeddata.es/dataset/rdflicense.ttl
            InputStream is = new ByteArrayInputStream(raw.getBytes("UTF-8"));
            modelx.read(is, null, "TURTLE");
            return modelx;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all the licenses as a single Turtle string text
     */
    public String toTTL() {
        init();
        StringWriter sw = new StringWriter();
        modelTotal.write(sw, "TURTLE");
        return sw.toString();
    }

    /**
     * Obtains a list with all the licenses in the dataset. IT Loads the
     */
    public List<RDFLicense> getRDFLicenses() {
        System.err.println("WE SHOULD NOT BE HERE THIS IS DEPRECATED");
        List<RDFLicense> licenses = new ArrayList();
        if (!glicenses.isEmpty()) {
            Iterator it = glicenses.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                RDFLicense l = (RDFLicense) e.getValue();
                licenses.add(l);
            }
            return licenses;
        }

        List<Resource> rdflicenses = new ArrayList();
        List<Resource> licenseclasses = ODRL.getCommonPolicyClasses();
        for (Resource licenseclass : licenseclasses) {
            ResIterator it = modelTotal.listSubjectsWithProperty(RDF.type, licenseclass);
            while (it.hasNext()) {
                Resource res = it.next();
                rdflicenses.add(res);
            }
        }
        for (Resource res : rdflicenses) {
//            System.out.println("Loading: " + res.getURI());
            RDFLicense rdflicense = getRDFLicense(res.getURI());
//            System.out.println(rdflicense.getLabel());
            if (rdflicense != null) {
                licenses.add(rdflicense);
            }
        }
        return licenses;
    }
}
