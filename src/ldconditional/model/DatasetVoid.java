package ldconditional.model;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

//JAVA
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//APACHE COMMONS
import java.util.Map;
import java.util.logging.Level;
import ldconditional.LDRConfig;
import ldconditional.ldserver.Recurso;
import odrlmodel.rdf.ODRLRDF;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.RDFUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * This class represents the policies applying to the different graphs in a a dataset.
 * @author Victor
 */
public class DatasetVoid {

    private static final Logger logger = Logger.getLogger(DatasetVoid.class);

    //link back to the container
    ConditionalDataset conditionalDataset = null;
    
    //politicas aplicando a cada grafo
    Map<String, List<Policy>> policies = new HashMap();

    String name = "";
    String uri = "";
    Model model = null;

    /**
     * The constructor loads the policy found in the given file 
     * @param _filename: Name of the metadata of the dataset. For example, datasets/geo/void.ttl
     */
    public DatasetVoid(ConditionalDataset cd) {
        conditionalDataset = cd;
        name = cd.name;
        uri = LDRConfig.getServer() + name;
        load();
    }

    public void load()
    {
        String sfolder = LDRConfig.get("datasetsfolder", "datasets");
        if (!sfolder.endsWith("/")) sfolder+="/";
        String filename = sfolder + name + "/void.ttl";
        File f = new File(filename);
        if (f.exists()) {
            model = RDFDataMgr.loadModel(filename);
            addPrefixes();
            policies = loadPolicies();
        }
    }
    
    public void init() {
        model = ModelFactory.createDefaultModel();
        model.add(model.createResource(uri), RDF.type, model.createResource("http://www.w3.org/ns/dcat#Catalog"));
        write();
    }
    
    
    public ConditionalDataset getConditionalDataset() {
        return conditionalDataset;
    }

    public void reloadPolicies() {
        policies = loadPolicies();
    }

    public void removeAllLicenses(String selectedGrafo) {
        List<Policy> licencias = policies.get(selectedGrafo);
        for(Policy licencia : licencias)
            removeLicense(selectedGrafo, licencia.getLabel("en"));
    }
    /**
     * @param licencia label
     * Removes a license
     */
    public boolean removeLicense(String selectedGrafo, String licencia) {
        List<Policy> lp = policies.get(selectedGrafo);
        Policy p = PolicyManagerOld.findPolicyByLabel(licencia);
        if (p==null)
            return false;
        logger.info("Quitando " + selectedGrafo + " y " + p.uri);
        logger.info("Tenemos ahora mismo " + lp.size() + " licencias, que son");
        for (Policy px : lp) {
            logger.info(px.getURI());
        }
        model.remove(model.createResource(selectedGrafo), RDFUtils.PLICENSE, model.createResource(p.uri));
        write();
        reloadPolicies();
        lp = policies.get(selectedGrafo);
        System.out.println("Tenemos ahora mismo " + lp.size() + " licencias");
        return true;
    }

    /**
     * Adds a license
     */
    public void addLicense(String selectedGrafo, String licencia) {
        List<Policy> lp = policies.get(selectedGrafo);
        Policy p = PolicyManagerOld.findPolicyByLabel(licencia);
        if (p == null) {
            return;
        }
        System.out.println("Poniendo " + selectedGrafo + " y " + p.uri);
        model.add(model.createResource(selectedGrafo), RDFUtils.PLICENSE, model.createResource(p.uri));
        write();
        reloadPolicies();
        lp = policies.get(selectedGrafo);

    }

    public void write() {
        try {
            StringWriter sw = new StringWriter();
            RDFDataMgr.write(sw, model, Lang.TTL);
            String s = sw.toString();
            
            String sfolder = LDRConfig.get("datasetsfolder", "datasets");
            if (!sfolder.endsWith("/")) sfolder+="/";
            String filename = sfolder + name + "/void.ttl";
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.write(s);
            writer.close();
        } catch (Exception e) {
            //          view.flashStatus("Error saving file");
        }

    }

    private void addPrefixes() {
        model.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/"); //http://w3.org/ns/odrl/2/
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("cc", "http://creativecommons.org/ns#");
        model.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        model.setNsPrefix("void", "http://rdfs.org/ns/void#");
        model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        model.setNsPrefix("ldp", "http://www.w3.org/ns/ldp#");
        model.setNsPrefix("foaf", "http://xmlns.com/foaf/spec/");
        model.setNsPrefix("schema", "http://schema.org/");
    }

    /**
     * Examnple:
     * "http://purl.org/dc/terms/title
     */
    public void addMetadata(String sprop, String val) {
        Property prop = model.createProperty(sprop);
        model.add(getDatasetRes(), prop, val);
    }
    
    public void setMetadataLiteral(String sprop, String val)
    {
        if (model==null)
            this.init();
        Property prop = model.createProperty(sprop);
        Statement st=model.getProperty(getDatasetRes(), prop);
        if (st!=null)
            model.remove(st);
        model.add(getDatasetRes(), prop, val);
    }
    

    /**
     * Examnple:
     * "http://purl.org/dc/terms/title"
     */
    public void addMetadataResource(String sprop, String val) {
        Property prop = model.createProperty(sprop);
        Resource res = model.createResource(val);
        model.add(getDatasetRes(), prop, res);
    }

    public String getComment(String grafo) {
        NodeIterator nit = model.listObjectsOfProperty(model.createResource(grafo), RDFS.comment);
        if (nit.hasNext()) {
            RDFNode nodo = nit.next();
            return nodo.toString();
        }
        return "";
    }

    public Recurso getRecurso(String grafo) {
        Iterator it = policies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String r = (String) e.getKey();
            if (r.equals(grafo)) {
                Recurso re = new Recurso(r);
                return re;
            }
        }
        return null;
    }

    /**
     * Gets the policies for 
     */
    public List<Policy> getPolicies(String grafo) {
        List<Policy> lista = new ArrayList();
        Iterator it = policies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String r = (String) e.getKey();
            if (r.equals(grafo)) {
                List<Policy> politicas = (List<Policy>) e.getValue();
                lista.addAll(politicas);
            }
        }
        return lista;
    }

    public List<String> getObjectsForProperty(String sprop) {
        List<String> ls = new ArrayList();
        Property prop = model.createProperty(sprop);

        NodeIterator it = model.listObjectsOfProperty(prop);
        while (it.hasNext()) {
            ls.add(it.next().toString());
        }
        return ls;
    }

    /**
     * The first metadata field
     */
    public String getFirstObjectForProperty(String sprop) {
        Property prop = model.createProperty(sprop);
        NodeIterator it = model.listObjectsOfProperty(prop);
        if (it.hasNext()) {
            return it.next().toString();
        }
        return "";
    }

    /**
     * Gets a map where for each graph
     */
    private Map<String, List<Policy>> loadPolicies() {
        Map<String, List<Policy>> map = new HashMap();
        model.createResource();
        ResIterator it = model.listResourcesWithProperty(RDF.type, RDFUtils.RDATASET);
        while (it.hasNext()) {
            List<Policy> policies = new ArrayList();
            Resource res = it.next();
            NodeIterator otl = model.listObjectsOfProperty(res, RDFUtils.PMAKESOFFER);
            List<String> lo = new ArrayList();
            int nofertas = 0;
            while (otl.hasNext()) {
                RDFNode rlic = otl.next();
                nofertas++;
                if (rlic.isResource()) {
                    lo.add(rlic.asResource().getURI());
                }
            }
            logger.info("Parseando " + res.getURI() + " que tiene " + nofertas + " ofertas");

            //iteramos por las licencias.
            NodeIterator itl = model.listObjectsOfProperty(res, RDFUtils.PLICENSE);
            while (itl.hasNext()) {
                RDFNode rlic = itl.next();
                if (rlic.isResource()) {
                    Resource rrlic = rlic.asResource();
//                    System.out.println("HAY UN TOTOAL DEX " + PolicyManagerOld.getPolicies().size());

                    //IMPORTANTE: SE INTENTA PRIMERO EN LA CACHÉ PARA QUE TODO VAYA COMO UN TIRO
                    Policy policy = PolicyManagerOld.getPolicy(rrlic.getURI());
                    if (policy == null) {
                        policy = ODRLRDF.getPolicyFromResource(rrlic);
                        if (policy != null) {
                            logger.info("Parsed remotelly policy " + policy.getURI());
                        }
                    } else {
                        logger.info("Parsed locally policy " + policy.getURI());
                    }

                    if (policy != null) {
                        if (lo.contains(policy.getURI())) {
                            policy.setInOffer(true);
                        }
                        policies.add(policy);
                        int count = RDFUtils.countStatements(rrlic.getModel());
                    } else {
                        logger.warn("Failed to remotely load policy " + policy.uri);
                    }
                }
            }

            Recurso r = new Recurso();
            r.setUri(res.getURI());
            r.setComment(RDFUtils.getFirstPropertyValue(res, RDFS.comment));
            r.setLabel(RDFUtils.getFirstPropertyValue(res, RDFS.label));
            map.put(r.getUri(), policies);
        }
        return map;
    }

    /**
     * Reads policies found in the given file
     * Each policy is supposed to be in a TTL file placed in the folder
     * @return A list of ODRL policies
     */
    public List<Policy> readPolicies() {
        List<Policy> politicas = new ArrayList();
        try {
            List<Resource> ls = PolicyManagerOld.findPolicies(model);
            for (Resource rpolicy : ls) {
                Policy policy = ODRLRDF.getPolicyFromResource(rpolicy);
                policy.fileName = rpolicy.getLocalName();
                politicas.add(policy);
            }
        } catch (Exception e) {
            logger.warn("No se pudo cargar la licencia " + e.getMessage());
            e.printStackTrace();
        }
        return politicas;
    }

    /**
     * Gets a list with the datasets in the catalog
     */
    public List<Grafo> getGrafos() {
        List<Grafo> grafos = new ArrayList();
        ResIterator rit = model.listSubjectsWithProperty(RDF.type, RDFUtils.RDATASET);
        while (rit.hasNext()) {
            Resource res = rit.next();
            Grafo g = new Grafo(this, res.getURI());
            grafos.add(g);
        }
        return grafos;
    }

    public Grafo getGrafo(String uri) {
        ResIterator rit = model.listSubjectsWithProperty(RDF.type, RDFUtils.RDATASET);
        while (rit.hasNext()) {
            Resource res = rit.next();
            if (res.getURI().equals(uri)) {
                return new Grafo(this, res.getURI());
            }
        }
        return null;
    }

    public Resource getDatasetRes() {
        return model.createResource(uri);
    }

    public List<String> getMetadataValues(String sprop) {
        List<String> ls = new ArrayList();
        Property prop = model.createProperty(sprop);
        StmtIterator list = model.listStatements(new SimpleSelector(getDatasetRes(), prop, (RDFNode)null));
        while(list.hasNext())
        {
            Statement st = list.next();
            Resource res=st.getObject().asResource();
            ls.add(res.toString());
        }
        return ls;
    }
    
    
    /**
     * The first model field
     */
    public String getMetadata(String sprop) {
        Property prop = model.createProperty(sprop);
        Statement st = getDatasetRes().getProperty(prop);
        if (st == null) {
            return "";
        }
        return st.getObject().toString();
    }

    public String toRDF() {
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, Lang.TTL);
        return sw.toString();
    }

    public String getComment() {
        String tmp = this.toRDF();
        return getMetadata("http://www.w3.org/2000/01/rdf-schema#comment");
    }

    public int getNumTriples(String s) {
        String nt=getMetadata("http://rdfs.org/ns/void#triples");
        int i=0;
        if (nt!=null && !nt.isEmpty())
            i=Integer.valueOf(nt);
        return i;
    }

    public String getHTMLMainResources() {
        String html = "";
        Property prop = model.createProperty("http://www.w3.org/ns/ldp#contains");
        NodeIterator it = model.listObjectsOfProperty(this.getDatasetRes(), prop);
        while (it.hasNext()) {
            RDFNode rdf = it.next();
            Resource res = rdf.asResource();
            int i1 = res.getURI().lastIndexOf("#");
            int i2 = res.getURI().lastIndexOf("/");
            int i = Math.max(i1, i2);
            String label = res.getURI().substring(i + 1, res.getURI().length());
            try {
                label = URLDecoder.decode(label, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                java.util.logging.Logger.getLogger(DatasetVoid.class.getName()).log(Level.SEVERE, null, ex);
            }
//DEMADIADO LENTA ERA LA SIGUIENTE.
// String label = dsDump.getFirstObject(res.getURI(), "http://www.w3.org/2000/01/rdf-schema#label");
            String s = "<a href=\"" + res.getURI() + "\">" + label + "</a>";
            html += s + "\n";
        }
        return html;
    }

    public String getTitle() {
        return getMetadata("http://purl.org/dc/terms/title");
    }

    public void restoreDefault() {
        try {
            
            String sfolder = LDRConfig.get("datasetsfolder", "datasets");
            if (!sfolder.endsWith("/")) sfolder+="/";
            String filename1 = sfolder + this.getConditionalDataset().name + "/voidoriginal.ttl";
            String filename2 = sfolder + this.getConditionalDataset().name + "/void.ttl";
            
            copyFileUsingStream(new File(filename1), new File(filename2));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(DatasetVoid.class.getName()).log(Level.SEVERE, null, ex);
        }
        ConditionalDatasets.loadDatasets();

    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public List<String> getGrafosAbiertos() {
        List<String> grafos = new ArrayList();
        Iterator it = policies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String grafo = (String)e.getKey();
            List<Policy> lp = (List<Policy>) e.getValue();
            for(Policy p : lp)
            {
                if (p.hasPlay() && p.isOpen())
                {
                    grafos.add(grafo);
                    break;
                }
            }
        }
        return grafos;
    }

    public void recalcularTriples() {
        int triples = conditionalDataset.dsDump.countNumTriples();
        Statement st = model.getProperty(getDatasetRes(), model.createProperty("http://rdfs.org/ns/void#triples"));
        model.remove(st);
        model.add(getDatasetRes(), model.createProperty("http://rdfs.org/ns/void#triples"), ""+triples);
        /*
        List<String> grafos = conditionalDataset.dsIndex.getIndexedGrafos();
        for(String grafo : grafos)
        {
            triples=conditionalDataset.dsIndex.getIndexedTriplesPerGrafo(grafo);
            if (triples==-1)
                continue;
            st = model.getProperty(model.createResource(grafo), model.createProperty("http://rdfs.org/ns/void#triples"));
            model.remove(st);
            model.add(model.createResource(grafo), model.createProperty("http://rdfs.org/ns/void#triples"), ""+triples);
        }*/
        write();
    }
    
    public String getJSON()
    {
            JSONObject obj = new JSONObject();
            String suri = "";
            try{suri=URLEncoder.encode(conditionalDataset.getUri(), "UTF-8");}catch(Exception e){}
            obj.put("uri",suri);
            obj.put("label", conditionalDataset.name);
            obj.put("title", getTitle());
            obj.put("description", getComment());
            return obj.toJSONString();
    }

    public void setDescription(String description) {
        setMetadataLiteral("http://www.w3.org/2000/01/rdf-schema#comment", description);
        write();
        load();
    }
    public void setTitle(String description) {
        setMetadataLiteral("http://purl.org/dc/terms/title", description);
        write();
        load();
    }

    public boolean rebase(String datasuri) {
     try {
            String sfolder = LDRConfig.get("datasetsfolder", "datasets");
            if (!sfolder.endsWith("/")) sfolder+="/";
            sfolder=sfolder+conditionalDataset.name;
            String filename=sfolder+"/void.ttl";
            
            
            
            BufferedReader br = new BufferedReader(new FileReader(filename));
            File dest=new File(filename+"tmp");
            if(!dest.exists()) {
                dest.createNewFile();
            }            
            OutputStream os = new FileOutputStream(dest);
            int i = -1;
            String line = null;
            String newbase = LDRConfig.get("server", "http://localhost/");
            if (!newbase.endsWith("/"))
                newbase+="/";
            newbase+=conditionalDataset.name+"/resource";
            while ((line = br.readLine()) != null) {
                line = line.replace(datasuri, newbase);
                line+="\n";
                os.write(line.getBytes("UTF-8"));
            }
            os.close();
            br.close();
            File fin = new File(filename);
            fin.delete();
            fin = new File(filename);
            dest.renameTo(fin);
            return true;
            
        } catch (Exception e) {
            logger.warn("Error mientras se hacia rebase " + e.getMessage());
            return false;
        }              
    }


    
}
