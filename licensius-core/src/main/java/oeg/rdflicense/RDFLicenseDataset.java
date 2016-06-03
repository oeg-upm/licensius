package oeg.rdflicense;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
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
import oeg.licensius.core.Licensius;
import oeg.vroddon.util.URLutils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Class centralizing methods around the RDFLicenseDataset dataset
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
        
        if (rdflicense!=null)
        {
            System.out.println(rdflicense.toTTL());
        }
    }

    /**
     * Intenta cargarlo del local. Y si no sabe, lo coge del remoto
     */
    public RDFLicenseDataset()
    {
        System.err.println("Modelo total vacío: "+ (modelTotal==null));
        if (modelTotal==null)
        {
            modelTotal = readRDFLicenseLocal();
            if (modelTotal==null)
            {
                System.err.println("Leyendo el dataset de remoto");
                modelTotal = readRDFLicense();
            }
        }
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
     * Obtiene un RDFLicense dado su URI. 
     * Lo busca en el modelo total pero de una manera muy muy muy chapucera.
     */
    public static RDFLicense getRDFLicense(String rdfuri)
    {
        RDFLicense rdflicense = glicenses.get(rdfuri);
        if (rdflicense!=null)
            return rdflicense;
        
        if (modelTotal==null)
            modelTotal = readRDFLicense();
        if (modelTotal==null)
            return null;
        
        String abuscar="\n"+"<"+rdfuri+">";
        logger.debug("Searching: " + abuscar);
        int index=raw.indexOf(abuscar);
        
        if (index==-1)
        {
            String xx=RDFUtils.getLastPart(rdfuri);
            abuscar = ":"+xx;
            index=raw.indexOf(abuscar);
        }
        
        
        String extension ="ttl";
        if (abuscar.length()<3)
            return null;
        extension = abuscar.substring(abuscar.length()-4, abuscar.length()-1);
        if (index==-1)
        {
            logger.warn("License " + rdfuri +" not found");
            return null;
        }
        int index2 = raw.indexOf(". \n", index);
        if (index2==-1)
        {
            logger.warn("License " + rdfuri +" end is not found");
            return null;
        }
        //fragmento is the fragment in the raw file.
        String fragmento=raw.substring(index, index2+1);
        fragmento = RDFUtils.getLicensePrefixes(fragmento) + fragmento;
        
        rdflicense = new RDFLicense();
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

    public static Model readRDFLicenseLocal() {
        Model modelx = ModelFactory.createDefaultModel();
        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("rdflicenses/list.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str="";
            if (is==null)
                System.err.println("No se ha podido abrir el archivo list.txt");
            if (is != null) {                            
                int conta=0;
                while ((str = reader.readLine()) != null) {
                    if (str.isEmpty())
                        continue;
                    Model model = ModelFactory.createDefaultModel();
                    InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("rdflicenses/"+str);
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(is2, writer, "UTF-8");
                    String miraw = writer.toString();     
                    InputStream is4 = new ByteArrayInputStream(miraw.getBytes("UTF-8"));
                    if (miraw.length()<5)
                        continue;
                    String mirawm = miraw.substring(0, miraw.length()-4);
                    raw+="\n"+mirawm+" \n";
                    try{
                    model.read(is4, null, "TURTLE");
                    if (model!=null)
                    {
                        String uri = "http://purl.org/NET/rdflicense/"+str;
                        RDFLicense license = new RDFLicense(model);
                        uri = uri.substring(0,uri.length()-4);
                        license.uri=uri;
                        glicenses.put(uri,license);
                        modelx.add(model); 
                        conta++;

                    }
                    }catch(Exception e)
                    {
                        System.err.println("La licencia " + str + " no contenía un RDF correcto");
                        System.err.println(e.getMessage());
                    }
                }                
                System.err.println("Total licencias correctas: " + conta);
            }            
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
        return modelx;
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

    /**
     * Returns all the licenses as a single Turtle string text
     */
    public String toTTL() {
        if (modelTotal == null) 
            readRDFLicense();
        if (modelTotal == null) 
            return null;
        StringWriter sw = new StringWriter();
        modelTotal.write(sw, "TURTLE");
        return sw.toString();
    }
    
    /**
     * Obtains a list with all the licenses in the dataset. IT Loads the
     */
    public List<RDFLicense> getRDFLicenses() {
        List<RDFLicense> licenses = new ArrayList();
        if (!glicenses.isEmpty())
        {
            Iterator it = glicenses.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry)it.next();
                RDFLicense l = (RDFLicense)e.getValue();
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
            if (rdflicense!=null)
                licenses.add(rdflicense);
        }
        return licenses;
    }    
}
