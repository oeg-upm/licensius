package ldconditional;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

//JAVA
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//APACHE COMMONS
import java.util.Map;
import ldserver.Recurso;
import odrlmodel.rdf.ODRLRDF;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.RDFUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

/**
 * This class represets the policies applying to a dataset
 * @author Victor
 */
public class DatasetPolicy {

    //Mapa que mapea Grafos a Politicas
    Map<String, List<Policy>> policies = new HashMap();
    private static final Logger logger = Logger.getLogger(DatasetPolicy.class);
    Model model = null;

    /**
     * The constructor loads the policy found in the given file 
     * @param _filename: Name of the metadata of the dataset. For example, datasets/geo/void.ttl
     */
    public DatasetPolicy(String _filename) {
        File f = new File(_filename);
        if (f.exists()) {
            model = RDFDataMgr.loadModel(_filename);
            policies = loadPolicies();
        }
    }
    
    

    public Recurso getRecurso(String grafo)
    {
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
                List<Policy> politicas = (List<Policy>)e.getValue();
                lista.addAll(politicas);
            }
        }
        return lista;
    }

    public List<String> getObjectsForProperty(String sprop)
    {
        List<String> ls = new ArrayList();
        Property prop = model.createProperty(sprop);

        NodeIterator it = model.listObjectsOfProperty(prop);
        while(it.hasNext())
            ls.add(it.next().toString());
        return ls;
    }
    
    /**
     * The first metadata field
     */
    public String getFirstObjectForProperty(String sprop)
    {
        Property prop = model.createProperty(sprop);
        NodeIterator it = model.listObjectsOfProperty(prop);
        if (it.hasNext())
            return it.next().toString();
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
            while (otl.hasNext()) {
                RDFNode rlic = otl.next();
                if (rlic.isResource())
                   lo.add(rlic.asResource().getURI()); 
            }
            
            //iteramos por las licencias.
            NodeIterator itl = model.listObjectsOfProperty(res, RDFUtils.PLICENSE);
            while (itl.hasNext()) {
                RDFNode rlic = itl.next();
                if (rlic.isResource()) {
                    Resource rrlic = rlic.asResource();
                    Policy policy = ODRLRDF.getPolicyFromResource(rrlic);
                    if (policy != null) {
                        if (lo.contains(policy.getURI()))
                            policy.setInOffer(true);
                        policies.add(policy);
                        int count = RDFUtils.countStatements(rrlic.getModel());
                        logger.info("Remotely loaded policy " + policy.uri + " with " + count + " statements");
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
}
