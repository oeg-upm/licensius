package oeg.rdflicense;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 * Class centralizing methods around the RDFLicenseDataset dataset
 * @author Victor oeg 
 */
public class RDFLicenseDataset {

    private static Model modelTotal = null;
    private static String raw = "";
    private static final Logger logger = Logger.getLogger(RDFLicenseDataset.class.getName());

    /**
     * Initializes and loads the RDFLicense dataset
     */
    public RDFLicenseDataset() {
        readRDFLicense();
    }
    
    public boolean isValid()
    {
        return modelTotal!=null;
    }

    /**
     * Gets an RDFLicense given its URI
     * @param rdfuri URI of an RDFLicense
     */
    public RDFLicense getRDFLicense(String rdfuri) {
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

    /*****************************************************************
     * 
     * PRIVATE METHODS
     * 
     *****************************************************************/
    /**
     * This method reads the local rdflicense.ttl file
     */
    private void readRDFLicense() {
        try {
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
            logger.info("rdflicense read with " + raw.length() + " bytes");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }
    }
    
    
    
}
