package oeg.rdflicense;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

//JAVA
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//LOG4J
import org.apache.log4j.Logger;

/**
 * Class centralizing methods around the RDFLicenseDataset dataset. Served
 * under: http://localhost:8053/
 *
 * @author Victor oeg
 */
public class RDFLicenseDataset {

    private static Model modelTotal = null;
    private static String raw = "";
    private static final Logger logger = Logger.getLogger(RDFLicenseDataset.class.getName());

    Map<String, Model> models = new HashMap();

    /**
     * Initializes and loads the RDFLicense dataset
     */
    public RDFLicenseDataset() {
        readRDFLicense();
    }

    public boolean isValid() {
        return modelTotal != null;
    }

    /**
     * Gets an RDFLicense given its URI. It does not extract this information
     * from the model, but searchs for it independently
     *
     * @param rdfuri URI of an RDFLicense
     */
    public RDFLicense getRDFLicense(String rdfuri) {

        Model m = models.get(rdfuri);
        if (m != null) {
            RDFLicense rdflicense = new RDFLicense(m);
            rdflicense.uri = rdfuri;
            return rdflicense;
        }

        if (modelTotal == null) {
            readRDFLicense();
        }
        if (modelTotal == null) {
            return null;
        }

        String abuscar = "\n<" + rdfuri + ">";
        logger.debug("Searching: " + abuscar);
        int index = raw.indexOf(abuscar);
        if (index == -1) {
            logger.warn("License " + rdfuri + " not found within " + raw.length() + " bytes");
            return null;
        }
        int index2 = raw.indexOf(". ", index);
        if (index2 == -1) {
            logger.warn("License " + rdfuri + " not foundwithin " + raw.length() + " bytes");
            return null;
        }
        String fragmento = raw.substring(index, index2 + 1);
        fragmento = RDFUtils.getLicensePrefixes() + fragmento;
        RDFLicense rdflicense = new RDFLicense();
        rdflicense.model = RDFUtils.parseFromText(fragmento);
        rdflicense.uri = rdfuri;
        return rdflicense;
    }

    /**
     * Obtains a list with all the licenses in the dataset. IT Loads the
     */
    public List<RDFLicense> getRDFLicenses() {
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
//            System.out.println("Loading: " + res.getURI());
            RDFLicense rdflicense = getRDFLicense(res.getURI());
//            System.out.println(rdflicense.getLabel());
            licenses.add(rdflicense);
        }
        return licenses;
    }

    /**
     * Gets a TTL representation of the RDFLicense
     */
    public String toTTL() {
        if (modelTotal == null) {
            readRDFLicense();
        }
        if (modelTotal == null) {
            return null;
        }

        StringWriter sw = new StringWriter();
        modelTotal.write(sw, "TURTLE");
        return sw.toString();
    }

    /**
     * Gets an RDFXML representation of the RDFLicense
     */
    public String toRDFXML() {
        if (modelTotal == null) {
            readRDFLicense();
        }
        if (modelTotal == null) {
            return null;
        }

        StringWriter sw = new StringWriter();
        modelTotal.write(sw, "RDF/XML");
        return sw.toString();
    }

    /**
     * ***************************************************************
     *
     * PRIVATE METHODS
     *
     ****************************************************************
     */
    /**
     * This method reads the local rdflicense.ttl file and loads it in memory
     * ADDITIONALLY, NOW IT READS THE LICENSES PRESENT IN THE FOLDER
     * RESOURCES.RDFLICNESES. THEY MUST BE TURTLE VALID FILES
     */
    private void readRDFLicense() {
        try {
            // THE MAIN FILE WITH LICENSES IS READ
            modelTotal = ModelFactory.createDefaultModel();
            InputStream in = this.getClass().getResourceAsStream("rdflicense.ttl");
            if (in == null) {
                logger.error("could not open stream");
            }
            raw = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str = "";
            while ((str = br.readLine()) != null) {
                raw += str + " \n";
            }
            InputStream is = new ByteArrayInputStream(raw.getBytes());
            modelTotal.read(is, null, "TURTLE");
            logger.info("Global rdflicense read OK with " + raw.length() + " bytes");

            //AND NOW ADDITIONAL LICENSE IN THE RESOURCES.RDFLICENSES FOLDER IS BEING READ.
            /*final String path = "resources/rdflicenses";
             final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
             */
            String r = "";
            URL url = RDFLicenseDataset.class.getResource("../../resources/rdflicenses");
            if (url == null) {
            } else {
                File dir = new File(url.toURI());
                for (File f : dir.listFiles()) {
                    String ruta = "../../resources/rdflicenses/" + f.getName();
                    logger.info("About to read"+ruta);
                    try {
                        Model parcial = ModelFactory.createDefaultModel();
                        in = this.getClass().getResourceAsStream(ruta);
                        if (in == null) {
                            logger.error("could not open stream");
                            continue;
                        }
                        r = "";
                        br = new BufferedReader(new InputStreamReader(in));
                        str = "";
                        while ((str = br.readLine()) != null) {
                            r += str + " \n";
                        }
                        is = new ByteArrayInputStream(r.getBytes());
                        parcial.read(is, null, "TURTLE");
                        if (parcial.size()==0)
                            continue;

                        RDFLicense lic = new RDFLicense(parcial);
                        if (lic==null)
                        {
                            System.out.println("HA FALLADO: " + ruta);
                            continue;
                        }
                        String uri = "http://purl.org/NET/rdflicense/" + f.getName();
                        uri = uri.replace(".ttl", "");
                        models.put(uri, parcial);
                        logger.info("rdflicense read OK with " + r.length() + " bytes");
                        modelTotal.add(parcial);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }
    }

    List<RDFLicense> filterBy(List<RDFLicense> original, String search) {
        List<RDFLicense> filtradas = new ArrayList();
        if (search == null || search.isEmpty()) {
            return original;
        }
        for (RDFLicense o : original) {
            if (o.getLabel() == null) {
                continue;
            }
            if (o.getLabel().contains(search)) {
                filtradas.add(o);
            }
        }
        return filtradas;
    }

    /**
     * Exports all the licenses to a folder
     * Each license, a file
     * The folder must exist
     * The format is turtle
     */
    public static void exportDatasetToFolder(String folderpath) {
        RDFLicenseDataset ds = new RDFLicenseDataset();
        List<RDFLicense> list = ds.getRDFLicenses();
        for (RDFLicense lic : list) {
            try {
                String corto = RDFUtils.getLastBitFromUrl(lic.getURI());
                File file = new File(folderpath + "/" + corto+ ".ttl");
                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(lic.toTTL());
                bw.close();
            } catch (Exception e) {
            }
        }

    }

    public static void main(String[] args) {
        RDFLicenseDataset ds = new RDFLicenseDataset();

//        RDFLicenseDataset.exportDatasetToFolder("C:\\tmp");

        RDFLicense lx = ds.getRDFLicense("http://purl.org/NET/rdflicense/APACHE2.0");
        String s =ODRL.getFirstComment("http://purl.oclc.org/NET/ldr/ns#DatabaseRight");
 //        System.out.println(lx.getLabel());
 //        String legal = lx.getLegalCode();
//         System.out.println(legal);
         System.out.println(s);
    }

}
