package oeg.rdflicense;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import main.Licensius;
import org.apache.log4j.Logger;
import vroddon.utils.URLutils;

/**
 * Class centralizing methods around the RDFLicenseDataset dataset
 * @author Victor
 */
public class RDFLicenseDataset {

    public static Model modelTotal = null;
    public static String raw = "";
    private static final Logger logger = Logger.getLogger(RDFLicenseDataset.class.getName());
    
    public static void main(String[] args) {
        Licensius.initLogger();
        RDFLicense rdflicense = null;
//        rdflicense = RDFLicenseDataset.getRDFLicenseByURI("http://creativecommons.org/publicdomain/zero/1.0/");
        
        //rdflicense = getRDFLicense("http://purl.org/NET/rdflicense/cc-by4.0");
        rdflicense = RDFLicenseDataset.getRDFLicenseByURI("https://www.gnu.org/licenses/lgpl.html");
        
        if (rdflicense!=null)
        {
            System.out.println(rdflicense.toTTL());
        }
    }
    
    public RDFLicenseDataset()
    {
        if (modelTotal==null)
            modelTotal = readRDFLicense();
    }
    
    public boolean isValid()
    {
        return modelTotal!=null;
    }    
    
    
    /**
     * Retrieves a list of all the RDFLicenses present in http://rdflicense.appspot.com/
     */
    public List<RDFLicense> listRDFLicenses()
    {
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
            RDFLicense rdflicense=getRDFLicense(res.getURI());
            if (rdflicense!=null)
                licenses.add(rdflicense);
        }
        return licenses;
    }
    
    
    
    /**
     * Obtiene un RDFLicense dado su URI
     */
    public static RDFLicense getRDFLicense(String rdfuri)
    {
        if (modelTotal==null)
            modelTotal = readRDFLicense();
        if (modelTotal==null)
            return null;
        
        String abuscar="\n"+"<"+rdfuri+">";
        logger.debug("Searching: " + abuscar);
        int index=raw.indexOf(abuscar);
        String extension ="ttl";
        if (abuscar.length()<3)
            return null;
        extension = abuscar.substring(abuscar.length()-4, abuscar.length()-1);
        if (index==-1)
        {
            logger.warn("License " + rdfuri +" not found");
            return null;
        }
        int index2 = raw.indexOf(". ", index);
        if (index2==-1)
        {
            logger.warn("License " + rdfuri +" end is not found");
            return null;
        }
        //fragmento is the fragment in the raw file.
        String fragmento=raw.substring(index, index2+1);
        fragmento = RDFUtils.getLicensePrefixes(fragmento) + fragmento;
        
        RDFLicense rdflicense = new RDFLicense();
        rdflicense.model = RDFUtils.parseFromText(fragmento); 
        rdflicense.uri = rdfuri;
        return rdflicense;
    }
    
    

    /**
     * Dada una URI (por ejemplo)
     */
    public static RDFLicense getRDFLicenseByURI(String uri)
    {
        if (modelTotal==null)
            modelTotal = readRDFLicense();
        if (modelTotal==null)
            return null;
        String rdfuri = getRDFLicenseByLicenseLax(uri);
        if (rdfuri.isEmpty())
            return null;
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
        if (modelTotal == null) {
            modelTotal = RDFLicenseDataset.readRDFLicense();
        }
        if (modelTotal == null) {
            return "";
        }
        if (uri.isEmpty())
            return "";
        List<Resource> lres = RDFUtils.getAllSubjectsWithObject(modelTotal, uri);
        if (lres.isEmpty()) {
            if (uri.endsWith("/"))
            {
                uri = uri.substring(0, uri.length()-2);
                lres = RDFUtils.getAllSubjectsWithObject(modelTotal, uri);
            }
            else
            {
                uri = uri+"/";
                lres = RDFUtils.getAllSubjectsWithObject(modelTotal, uri);
            }
        }
        if (lres.isEmpty())
            return "";
        return lres.get(0).getURI();
    }

    public static String getRDFLicenseByLicense(String uri) {
        if (modelTotal == null) {
            modelTotal = RDFLicenseDataset.readRDFLicense();
        }
        if (modelTotal == null) {
            return "";
        }
        List<Resource> lres = RDFUtils.getAllSubjectsWithObject(modelTotal, uri);
        if (lres.isEmpty()) {
            return "";
        }
        return lres.get(0).getURI();
    }

    public static Model readRDFLicense() {
        try {
            Model modelx = ModelFactory.createDefaultModel();
            raw = URLutils.getFile("http://rdflicense.appspot.com/rdflicense/"); //http://rdflicense.linkeddata.es/dataset/rdflicense.ttl
            InputStream is = new ByteArrayInputStream(raw.getBytes());
            modelx.read(is, null, "TURTLE");
            return modelx;
        } catch (Exception e) {
            return null;
        }
    }
}
