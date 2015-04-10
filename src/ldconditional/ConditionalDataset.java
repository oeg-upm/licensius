package ldconditional;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import ldserver.Recurso;


import odrlmodel.Policy;
import odrlmodel.rdf.RDFUtils;
import oeg.rdf.commons.NQuad;
import oeg.rdf.commons.NQuadRawFile;
import oeg.rdf.commons.NTriple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 * This class represents a linked data dataset
 * The info must be represented as NQuads
 * @author Victor
 */
public class ConditionalDataset {
    
    Model metadata = null;
    String BASE="http://conditional.linkeddata.es/";
    Resource dataset = null;
    DatasetPolicy pm = null; 
    NQuadRawFile dump = null;
            
    
    /**
     * A conditional dataset is initialized with a name
     * The name must be a simple string, with no blank spaces nor other non-URI characters.
     */
    public ConditionalDataset(String name)
    {
        metadata = ModelFactory.createDefaultModel();
        RDFUtils.addPrefixesToModel(metadata);
        dataset = metadata.createProperty(BASE+name);
        addMetadataResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://www.w3.org/ns/dcat#Dataset");
        pm = new DatasetPolicy("datasets/"+name+"/void.ttl");
        dump = new NQuadRawFile("datasets/"+name+"/data.nq");
    }
    
    public DatasetPolicy getDatasetPolicy()
    {
        return pm;
    }
    
    public List<Policy> getPoliciesForGraph(String grafo)
    {
        return pm.getPolicies(grafo);
    }
    
    public List<Recurso> getRecursos(int min, int max)
    {
        String patron = pm.getFirstObjectForProperty("http://www.w3.org/ns/ldp#contains");
        String o = NTriple.getObject(patron);
        List<Recurso> recursos = dump.getRecursosWithObject(o, min, max);
        recursos = enrichWithLabels(recursos);
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
     * "http://purl.org/dc/terms/title
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
        NodeIterator it = metadata.listObjectsOfProperty(prop);
        if (it.hasNext())
            return it.next().toString();
        return "";
    }
    
    
    public String toString()
    {
        return getMetadata("http://purl.org/dc/terms/title");
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
    
    
}
