package ldc.model;

import java.io.File;
import ldc.LdcConfig;
import org.apache.jena.riot.RDFDataMgr;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.ODRLRDF;
import org.apache.jena.riot.Lang;
import org.json.JSONObject;
import oeg.utils.rdf.RDFPrefixes;
import oeg.utils.rdf.RDFUtils;
import oeg.utils.strings.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author vroddon
 */
public class DatasetVoid {
    String name="";
    String logo="";
    public Model model = null;
    static final Logger logger = Logger.getLogger("ldc");
    //politicas aplicando a cada grafo
    Map<String, List<Policy>> policies = new HashMap();

    //link back to the container
    ConditionalDataset conditionalDataset = null;
     public ConditionalDataset getConditionalDataset() {
         return conditionalDataset;
    }
   
    
    public DatasetVoid(ConditionalDataset cd) {
        conditionalDataset = cd;
        name = cd.name;
        load();
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
        logger.info("Poniendo licencia a " + selectedGrafo + " - " + p.uri);
        model.add(model.createResource(selectedGrafo), RDFUtils.PLICENSE, model.createResource(p.uri));
        write();
        reloadPolicies();
        lp = policies.get(selectedGrafo);

    }
    public void reloadPolicies() {
        policies = loadPolicies();
    }
    public String getURI()
    {
        String s = LdcConfig.getServer();
        String c = LdcConfig.get("context", "/ldc");
        return s+c+"/data/"+name;
    }
    
    public JSONObject getJSON()
    {
        if (model==null)
            return null;
        String title = RDFUtils.getFirstLiteral(model, getURI(), "http://purl.org/dc/terms/title");
        String comment = RDFUtils.getFirstLiteral(model, getURI(), "http://www.w3.org/2000/01/rdf-schema#comment");
        JSONObject obj = new JSONObject();
        obj.put("id",name);
        obj.put("title", title);
        obj.put("comment", comment);
        obj.put("uri", getURI());
        obj.put("logo", logo);
        return obj;
    }

    public void setProperty(String p, String o)
    {
        setProperty(getURI(), p, o);
    }
    public void setProperty(String s, String p, String o)
    {
        RDFUtils.setFirstLiteral(model, s, p, o);
        write();
        load();
    }
    
    public void init() {
        model = ModelFactory.createDefaultModel();
        model.add(model.createResource(getURI()), RDF.type, model.createResource("http://www.w3.org/ns/dcat#Catalog"));
        write();
    }


    
    
    public void load()
    {
        String sfolder = LdcConfig.getDataFolder();
        if (!sfolder.endsWith("/")) sfolder+="/";
        String filename = sfolder + name + "/void.ttl";
        File f = new File(filename);
        if (f.exists()) {
            logger.debug("Loading " + filename);
            model = RDFDataMgr.loadModel(filename);
            model = RDFPrefixes.addPrefixesIfNeeded(model);
        }
        
        filename = sfolder + name + "/logo.png";
        f = new File(filename);
        if (f.exists())
        {
            logo=null;
            try{
                BufferedImage bi = ImageIO.read(new File(filename));
                logo = StringUtils.getImage64(bi);
            }catch(Exception e){}
        }
    }
    public void write() {
        try {
            StringWriter sw = new StringWriter();
            model = RDFPrefixes.addPrefixesIfNeeded(model);
            RDFDataMgr.write(sw, model, Lang.TTL);
            String s = sw.toString();
        String sfolder = LdcConfig.getDataFolder();

            if (!sfolder.endsWith("/")) sfolder+="/";
            String filename = sfolder + name + "/void.ttl";
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.write(s);
            writer.close();
        } catch (Exception e) {
            //          view.flashStatus("Error saving file");
        }

    }    
    public boolean rebase(String datasuri) {
     try {
        String sfolder = LdcConfig.getDataFolder();

            if (!sfolder.endsWith("/")) sfolder+="/";
            sfolder=sfolder + name;
            String filename=sfolder+"/void.ttl";
            
            BufferedReader br = new BufferedReader(new FileReader(filename));
            File dest=new File(filename+"tmp");
            if(!dest.exists()) {
                dest.createNewFile();
            }            
            OutputStream os = new FileOutputStream(dest);
            int i = -1;
            String line = null;

            
            String newbase = LdcConfig.getServer();
            String context = LdcConfig.get("context", "/ldc");
            if (!newbase.endsWith("/"))
                newbase+="/";
            newbase+=context+"/data/"+conditionalDataset.name+"/";
            newbase = newbase.replace("//", "/");
            newbase = newbase.replace("http:/", "http://");
            logger.info("Rebasing " + datasuri + " por " + newbase);
            
            
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

    /**
     * Se asegura de que un grafo dado está presente en el archivo void
     */
    public void ensureGrafo(String grafo) {
        if (grafo.isEmpty())
            return;
        logger.info("Se invoca a ensureGrafo " + grafo);
        Resource rgrafo = model.getResource(grafo);
        Resource dcatds = model.getResource("http://www.w3.org/ns/dcat#Dataset");
        model.add(rgrafo, RDF.type,dcatds );
        long nt=conditionalDataset.getDatasetIndex().getIndexedTriplesPerGrafo(grafo);
        setProperty(grafo, "http://rdfs.org/ns/void#triples", ""+nt);
        
        NodeIterator nit = model.listObjectsOfProperty(rgrafo, RDFS.label);
        if (!nit.hasNext())
        {
            int i0=-1;
            int i1=grafo.lastIndexOf("/");
            int i2=grafo.lastIndexOf("#");
            int i = Math.max(i0, i1);
            i = Math.max(i+1, i2);
            
            String lastpart = grafo.substring(i, grafo.length());
            model.add(rgrafo, RDFS.label, lastpart);
        }
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
     * Gets a map where for each graph
     */
    private Map<String, List<Policy>> loadPolicies() {
        Map<String, List<Policy>> map = new HashMap();
        model.createResource();
        ResIterator it = model.listResourcesWithProperty(RDF.type, RDFUtils.RDATASET);
        while (it.hasNext()) {
            List<Policy> policies = new ArrayList();
            Resource res = it.next();
            
            //Iteramos por las ofertas
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
    public void removeAllLicenses(String selectedGrafo) {
        List<Policy> licencias = policies.get(selectedGrafo);
        if (licencias==null)
            return;
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
    
    /**
     * Gets the policies for 
     */
    public List<Policy> getPolicies(String grafo) {
        policies = loadPolicies();
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
    public Resource getDatasetRes() {
        return model.createResource(getURI());
    }
    public void describeGrafo(String grafo, String sprop, String description) {
        if (model==null)
            this.init();
        String urigrafo="";
//        List<String> grafos = conditionalDataset.getDatasetIndex().getIndexedGrafos();
        List<Grafo> grafos = getGrafos();
        for(Grafo gra : grafos)
        {
            if (gra.getURI().equals(grafo) || gra.getURI().endsWith(grafo))
                urigrafo=gra.getURI();
        }
        if (urigrafo.isEmpty())
            return;
        Property prop = model.createProperty(sprop);
        Statement st=model.getProperty(model.createResource(urigrafo), prop);
        if (st!=null)
            model.remove(st);
        model.add(model.createResource(urigrafo), prop, description);
    }    

    public String getProperty(String propiedad) {
        load();
        return RDFUtils.getFirstLiteral(model, getURI(), propiedad);        
    }

    void resetDataset(String uri) {
        load();
        List<Statement> sts = new ArrayList();
        ResIterator it = model.listSubjectsWithProperty(RDF.type, model.createResource("http://www.w3.org/ns/dcat#Catalog"));
        while(it.hasNext())
        {
            Resource r = it.next();
            if (!r.getURI().equals(uri))
            {
                Statement st = ResourceFactory.createStatement(r, RDF.type, model.createResource("http://www.w3.org/ns/dcat#Catalog"));
                sts.add(st);
            }
        }
        model.remove(sts);
        write();
    }
}
