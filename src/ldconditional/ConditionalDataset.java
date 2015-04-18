package ldconditional;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ldrauthorizer.ws.LicensedTriple;
import ldserver.Recurso;


import odrlmodel.Policy;
import odrlmodel.rdf.RDFUtils;
import oeg.rdf.commons.NQuad;
import oeg.rdf.commons.NQuadRawFile;
import oeg.rdf.commons.NTriple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

/**
 * This class represents a linked data dataset.
 * The info must be represented as NQuads
 * @author Victor
 */
public class ConditionalDataset {
    private static final Logger logger = Logger.getLogger(ConditionalDataset.class);
    
    
    Model metadata = null;
    String BASE="http://conditional.linkeddata.es/";
    Resource dataset = null;
    DatasetPolicy dpolicy = null; 
    NQuadRawFile dump = null;

    public String name = "";
    
    /**
     * A conditional dataset is initialized with a _name
     * The _name must be a simple string, with no blank spaces nor other non-URI characters.
     */
    public ConditionalDataset(String _name)
    {
        name = _name;
        BASE = LDRConfig.getServer();

        try{
            String filename = "datasets/"+_name+"/void.ttl";
            metadata = RDFDataMgr.loadModel(filename) ;
            RDFUtils.addPrefixesToModel(metadata);
            dataset = metadata.createResource(BASE+_name);
            dpolicy = new DatasetPolicy(filename);
        }catch(Exception e)
        {
            logger.error("Error processing " + _name + ". "+ e.getMessage());
        }
        addMetadata("http://purl.org/dc/terms/title",name);
       // addMetadataResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://www.w3.org/ns/dcat#Dataset");

        dump = new NQuadRawFile("datasets/"+_name+"/data.nq");
    }

    public String getNamespace() {
        return dataset.getURI();
    }


    public int getDumpTriples(String recurso)
    {
        return dump.getNumTriples(recurso);
    }
    
    public Set<Policy> getPolicies(Recurso r)
    {
        Set<Policy> lp=new HashSet();
        for(String g:r.graphs)
        {
            List<Policy> politicas = dpolicy.policies.get(g);
            if (politicas!=null)
                lp.addAll(politicas);
        }
        return lp;
    }

    public String toString()
    {
        String s ="";
        s += "Dataset URI: " + dataset.getURI() + "\n";
        s += "Title: " + getTitle()+ "\n";
        s += "File name: " + dump.getFileName()+ "\n";
        s += "Graphs in dump: " + dump.getGraphs().size()+ "\n";
        s += "Licensed graphs: " + dpolicy.policies.size()+ "\n";
        return s;
    }

    public String getTitle()
    {
        return getMetadata("http://purl.org/dc/terms/title");
    }


    public DatasetPolicy getDatasetPolicy()
    {
        return dpolicy;
    }
    
    public List<Policy> getPoliciesForGraph(String grafo)
    {
        return dpolicy.getPolicies(grafo);
    }
    
    /**
     * Obtiene los recursos del min-esimo al max-esimo. 
     * 
     */
    public List<Recurso> getResourcesInDataset(int min, int max)
    {
        List<String> ls = dpolicy.getObjectsForProperty("http://www.w3.org/ns/ldp#contains");
        List<Recurso> recursos = new ArrayList();
        int i=0;
        for(String s : ls)
        {
            Recurso r = new Recurso(s);
            if (i>=min && i<=max)
                recursos.add(r);
            i++;
        }
       /* String patron = dpolicy.getFirstObjectForProperty("http://www.w3.org/ns/ldp#contains");
        String o = NTriple.getObject(patron);
        recursos = dump.getRecursosWithObject(o, min, max);*/
        recursos = enrichWithLabels(recursos);
        recursos = enrichWithGraphs(recursos);
        return recursos;
    }
    
    
    /**
     * Returns the named graphs present in the dataset
     */
    public List<String> getGraphs()
    {
        List<String> grafos = dump.getGraphs();
        return grafos;
    }
    
    
    /**
     * Examnple:
     * "http://purl.org/dc/terms/title
     */
    public void addMetadata(String sprop, String val)
    {
        Property prop = metadata.createProperty(sprop);
        metadata.add(dataset, prop, val);
    }

    /**
     * Examnple:
     * "http://purl.org/dc/terms/title"
     */
    public void addMetadataResource(String sprop, String val)
    {
        Property prop = metadata.createProperty(sprop);
        Resource res = metadata.createResource(val);
        metadata.add(dataset, prop, res);
    }

    
    /**
     * The first metadata field
     */
    public String getMetadata(String sprop)
    {
        Property prop = metadata.createProperty(sprop);
        Statement st = dataset.getProperty(prop);
        if (st==null)
            return "";
        return st.getObject().toString();
    }
    
    
    
    public String toRDF()
    {
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, metadata, Lang.TTL);
        return sw.toString();
    }

    private List<Recurso> enrichWithLabels(List<Recurso> recursos) {
        for(Recurso r : recursos)
        {
            String uri = r.getUri();
            String label = dump.getFirstObject(uri, "http://www.w3.org/2000/01/rdf-schema#label");
            if (label.startsWith("\""))
            {
                int i1=label.lastIndexOf("\"");
                label = label.substring(1, i1);
            }
            r.setLabel(label);
        }
        return recursos;
    }

    public Model getModelRecurso(String res)
    {
        return dump.getRecurso(res);
    }

    public String getRecurso(String subject) {
        Model model = dump.getRecurso(subject);
        StringWriter sw = new StringWriter();
   //    JenaJSONLD.init();
        org.openjena.riot.RIOT.init(); 
        model.write(sw, "RDF/JSON", "");
//        RDFDataMgr.write(sw, model, Lang.JSONLD);//.JSONLD
        return sw.toString();
    }

    /**
     * Enriquece los recursos dados con grafos
     */
    private List<Recurso> enrichWithGraphs(List<Recurso> recursos) {
        for(Recurso r : recursos)
        {
            String uri = r.getUri();
            Set<String> lg=dump.getGraphsForSubject(r.getUri());
            r.setGraphs(lg);
        }
        return recursos;        
    }

    public String getComment() {
        String tmp = this.toRDF();
        System.out.println(tmp);
        return getMetadata("http://www.w3.org/2000/01/rdf-schema#comment");
    }

    public String getHTMLMainResources() {
        String html ="";
        Property prop = metadata.createProperty("http://www.w3.org/ns/ldp#contains");
        NodeIterator it = metadata.listObjectsOfProperty(dataset, prop);
        while(it.hasNext())
        {
            RDFNode rdf=it.next();
            Resource res = rdf.asResource();
            String label = dump.getFirstObject(res.getURI(), "http://www.w3.org/2000/01/rdf-schema#label");
            label = label.substring(1, label.lastIndexOf("\""));
            String s = "<a href=\""+res.getURI()+"\">" + label+ "</a>";
            html+=s+"\n";
        }
        return html;
    }

    /**
     * Obtains the first object
     */
    public String getFirstObject(String recurso, String propiedad)
    {
        String o = dump.getFirstObject(recurso, propiedad);
//        int index = o.indexOf("^^");
//        o = o.substring(1, index);
        return o;
    }

    public List<LicensedTriple> getLicensedTriples(String recurso) {
        return dump.getLicensedTriples(recurso);


    }

    
    
}
