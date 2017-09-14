package oeg.odrlapi.rdf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import oeg.odrlapi.validator.ODRL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * Parsing of RDF ODRL Policies.
 * @author vroddon
 */
public class ODRLParser {

    
    /**
     * Returns all the policies in the model
     */
    public static Set<Resource> getPolicies(Model model)
    {
        Set<Resource> rpolicies = new HashSet();
        rpolicies.addAll(RDFSugar.getResourcesOfType(model, ODRL.RPOLICY));
        rpolicies.addAll(RDFSugar.getResourcesOfType(model, ODRL.RAGREEMENT));
        rpolicies.addAll(RDFSugar.getResourcesOfType(model, ODRL.ROFFER));
        rpolicies.addAll(RDFSugar.getResourcesOfType(model, ODRL.RSET));
        return rpolicies;
    }        
    public static Set<Model> getRootRules(Model model, Resource politica)
    {
        Set<Model> rules = new HashSet();
        
        NodeIterator ni = model.listObjectsOfProperty(politica, ODRL.PPERMISSION);
        while(ni.hasNext())
        {
            RDFNode node = ni.next();
            Resource permiso = node.asResource();
            Model racimo = RDFUtils.getRacimo(model, permiso, new ArrayList<Resource>());

            racimo.add(politica, ODRL.PPERMISSION, permiso);
            
            rules.add(racimo);
        }
        
        ni = model.listObjectsOfProperty(politica, ODRL.POBLIGATION);
        while(ni.hasNext())
        {
            RDFNode node = ni.next();
            Resource permiso = node.asResource();
            Model racimo = RDFUtils.getRacimo(model, permiso, new ArrayList<Resource>());
            racimo.add(politica, ODRL.POBLIGATION, permiso);
            rules.add(racimo);
        }
       
        ni = model.listObjectsOfProperty(politica, ODRL.PPROHIBITION);
        while(ni.hasNext())
        {
            RDFNode node = ni.next();
            Resource permiso = node.asResource();
            Model racimo = RDFUtils.getRacimo(model, permiso, new ArrayList<Resource>());
            racimo.add(politica, ODRL.PPROHIBITION, permiso);
            rules.add(racimo);
        }
        return rules;
    }       

    public static Resource getRuleId(Model rule, Resource politica) {
        
        NodeIterator ni = rule.listObjectsOfProperty(politica, ODRL.PPERMISSION);
        if (ni.hasNext())
            return ni.next().asResource();
        ni = rule.listObjectsOfProperty(politica, ODRL.POBLIGATION);
        if (ni.hasNext())
            return ni.next().asResource();
        ni = rule.listObjectsOfProperty(politica, ODRL.PPROHIBITION);
        if (ni.hasNext())
            return ni.next().asResource();
        return null;
    }

}
