package ldconditional;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

//JAVA
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//APACHE COMMONS
import java.util.Map;
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

    Map<String, List<Policy> > policies = new HashMap();
    private static final Logger logger = Logger.getLogger(DatasetPolicy.class);    
    Model model = null;
    
    public DatasetPolicy(String _filename)
    {
        model = RDFDataMgr.loadModel(_filename);
        policies = loadPolicies();
    }
    
    /**
     * Gets a map that for each dataset has a list of the graphs under control
     */
    private Map<String, List<Policy> > loadPolicies()
    {
        Map<String, List<Policy> > map = new HashMap();
        model.createResource();
        ResIterator it = model.listResourcesWithProperty(RDF.type, RDFUtils.RDATASET);
        while(it.hasNext())
        {
            List<Policy> policies = new ArrayList();
            Resource res = it.next();
            ResIterator itl = model.listResourcesWithProperty(RDFUtils.PLICENSE);
            while(itl.hasNext())
            {
                Resource rlic = itl.next();
                Policy policy = ODRLRDF.getPolicyFromResource(rlic);
                policies.add(policy);
            }
            map.put(res.getURI(), policies);
        }
        return map;
    }
    
    /**
     * Reads policies found in the given file
     * Each policy is supposed to be in a TTL file placed in the folder
     * @return A list of ODRL policies
     */
    public List<Policy> readPolicies() {
        List<Policy> politicas=new ArrayList();
        try{
            List<Resource> ls = PolicyManagerOld.findPolicies(model);
            for (Resource rpolicy : ls) {
                Policy policy = ODRLRDF.getPolicyFromResource(rpolicy);
                policy.fileName = rpolicy.getLocalName();
                politicas.add(policy);
            }
        }catch(Exception e)
        {
            logger.warn("No se pudo cargar la licencia " + e.getMessage());
            e.printStackTrace();
        }
        return politicas;
    }
    
}
