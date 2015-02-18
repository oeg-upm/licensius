package ldconditional;

//JENA
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMaker;
import com.hp.hpl.jena.sparql.core.DatasetImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldrauthorizerold.GoogleAuthHelper;
import ldrauthorizerold.Multilingual;

import odrlmodel.Asset;
import ldconditional.LDRConfig;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.RDFUtils;
import oeg.rdf.commons.NQuadRawFile;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

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
            
    
    public ConditionalDataset(String name)
    {
        metadata = ModelFactory.createDefaultModel();
        RDFUtils.addPrefixesToModel(metadata);
        dataset = metadata.createProperty(BASE+name);
        addMetadataResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://www.w3.org/ns/dcat#Dataset");
        pm = new DatasetPolicy("datasets/"+name+"/assets.ttl");
        dump = new NQuadRawFile("datasets/"+name+"/data.nq");
    }
    
    
    public Policy getPolicyForGraph(String graph)
    {
        return null;
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
    
    
}
