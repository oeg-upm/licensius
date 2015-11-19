package oeg.rdflicense;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

//JAVA
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

//LOG4J
import org.apache.log4j.Logger;

/**
 * Class centralizing methods around the RDFLicenseDataset dataset.
 * Served under: http://localhost:8053/
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
     * Gets an RDFLicense given its URI. It does not extract this information from the model, but searchs for it independently
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
     * Obtains a list with all the licenses in the dataset
     */
    public List<RDFLicense> getRDFLicenses() {
        List<RDFLicense> licenses = new ArrayList();
        List<Resource> rdflicenses = new ArrayList();
        List<Resource> licenseclasses = ODRL.getCommonPolicyClasses();
        for(Resource licenseclass : licenseclasses)
        {
            ResIterator it = modelTotal.listSubjectsWithProperty(RDF.type,  licenseclass);
            while(it.hasNext())
            {
                Resource res = it.next();
                rdflicenses.add(res);
            }
        }
        for(Resource res : rdflicenses)
        {
//            System.out.println("Loading: " + res.getURI());
            RDFLicense rdflicense=getRDFLicense(res.getURI());
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

    /*****************************************************************
     * 
     * PRIVATE METHODS
     * 
     *****************************************************************/
    /**
     * This method reads the local rdflicense.ttl file and loads it in memory
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
            logger.info("rdflicense read OK with " + raw.length() + " bytes");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }
    }

    List<RDFLicense> filterBy(List<RDFLicense> original, String search) {
        List<RDFLicense> filtradas = new ArrayList();
        if (search==null || search.isEmpty())
            return original;
        for(RDFLicense o : original)
        {
            if (o.getLabel()==null)
                continue;
            if (o.getLabel().contains(search))
                filtradas.add(o);
        }
        return filtradas;
    }


    
    
    
}
