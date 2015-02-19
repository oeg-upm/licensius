package ldconditional;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

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

    Map<Recurso, List<Policy>> policies = new HashMap();
    private static final Logger logger = Logger.getLogger(DatasetPolicy.class);
    Model model = null;

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
            Recurso r = (Recurso) e.getKey();
            if (r.getUri().equals(grafo)) {
                return r;
            }
        }
        return null;
    }
    
    public List<Policy> getPolicies(String grafo) {
        List<Policy> lista = new ArrayList();
        Iterator it = policies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            Recurso r = (Recurso) e.getKey();
            if (r.getUri().equals(grafo)) {
                List<Policy> politicas = (List<Policy>)e.getValue();
                lista.addAll(politicas);
            }
        }
        return lista;
    }

    /**
     * Gets a map that for each dataset has a list of the graphs under control
     */
    private Map<Recurso, List<Policy>> loadPolicies() {
        Map<Recurso, List<Policy>> map = new HashMap();
        model.createResource();
        ResIterator it = model.listResourcesWithProperty(RDF.type, RDFUtils.RDATASET);
        while (it.hasNext()) {
            List<Policy> policies = new ArrayList();
            Resource res = it.next();
            NodeIterator itl = model.listObjectsOfProperty(res, RDFUtils.PLICENSE);
            while (itl.hasNext()) {
                RDFNode rlic = itl.next();
                if (rlic.isResource()) {
                    Resource rrlic = rlic.asResource();
                    Policy policy = ODRLRDF.getPolicyFromResource(rrlic);
                    if (policy != null) {
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
            map.put(r, policies);
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
