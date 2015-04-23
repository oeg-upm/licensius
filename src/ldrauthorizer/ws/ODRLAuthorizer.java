package ldrauthorizer.ws;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.util.List;
import odrlmodel.Action;
import odrlmodel.Asset;
import odrlmodel.managers.AssetManager;
import odrlmodel.Constraint;
import odrlmodel.rdf.ODRLRDF;
import odrlmodel.Policy;
import odrlmodel.Rule;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

/**
 * Authorizer class 
 * @author Victor
 */
public class ODRLAuthorizer {

    private static final String ASSETS_FILE = "../data/void.ttl";


    /**
     * Simplest authorization: only dependant on the requested URI
     */
    public static boolean Authorize(String resource)
    {
        Logger.getLogger("ldr").info("Trying to authorize the resource " + resource);
        
        if (resource.equals("http://linguistic.linkeddata.es/data/terminesp/lexicon/lexiconEN"))
            return false;
        if (resource.equals("http://linguistic.linkeddata.es/data/terminesp/lexicon/lexiconES"))
            return true;
        return true;
    }

    public static String GiveToken(String recurso, String user) {
        Logger.getLogger("ldr").info("Trying to give a token to the user " + user + " and resource " + recurso);
        String token = "";
        token = ODRLAuth.getToken(recurso, user);
        return token;
    }


     /**
     * Simplest authorization: only dependant on the requested URI. Returns 
     */
    public static AuthorizationResponse Authorize2(String resource)
    {
        AuthorizationResponse ardefault = new AuthorizationResponse();
        ardefault.ok=false;
        ardefault.policies.clear();
        
        Logger.getLogger("ldr").info("Trying to authorize the resource " + resource);
        
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, ASSETS_FILE);
        Asset asset = AssetManager.findAsset(resource);
        if (asset==null)
            return ardefault;
        List<Policy> policies = asset.getPolicies();
        if (policies.isEmpty())
            return ardefault;
    
        AuthorizationResponse ar = new AuthorizationResponse();
        for(Policy policy : policies)
        {
            Policy policyFromStore = PolicyManagerOld.getPolicy(policy.getURI());
            List<Rule> rules=policyFromStore.getRules();
            for(Rule r : rules)
            {
                boolean containsPlay=false;
                for(Action action : r.actions)
                {
                    if (action.uri.equals("http://creativecommons.org/ns#Reproduction"))
                        containsPlay=true;
                    if(action.uri.equals("http://www.w3.org/ns/odrl/2/reproduce"))
                        containsPlay=true;
                }
                if (containsPlay==false)
                    continue;
                
                //SI ESTAMOS AQUI ES PORQUE ESTA LICENCIA TIENE PLAY
                List<Constraint> constraints = r.getConstraints();
                for(Constraint c : constraints)
                {
                   // System.out.println(resource + " Con: "+c);
                    if (c.isOpen()) //&& (r.getActions().contains("Reproduction")||r.getActions().contains("reproduce"))
                    {
                        ar.ok = true;
                        ar.policies.add(policy);
                     }
                    if (!c.isOpen())
                    {
                        
                    //    String s= ODRLRDF.getRDF(policy);
                        ar.ok = false;
                        ar.policies.add(policy);
                        
                    }
                }
            }
        }
//        AuthorizationResponse ar = new AuthorizationResponse();
        if (ar.policies.isEmpty())
        {
            ar.ok = false;
        }
//        ar.policies.clear();
        return ar;
   }

      /**
     * Autoriza un recurso, dado un conjunto de políticas compradas en el portfolio
     */
    static AuthorizationResponse AuthorizeStatement(LicensedTriple lt, List<Policy> portfolioPolicies) {
        AuthorizationResponse ar = new AuthorizationResponse();
        ar.ok=false;
        ar.policies.clear();
        
        String recurso = lt.toChurro();
        
        String sport = ""; for (Policy pq : portfolioPolicies) sport+=" " + pq.getURI();
        
      //  Logger.getLogger("ldr").debug("Autorizando el statement " + lt + " con " + sport);
        
        ar=AuthorizeResource(recurso,portfolioPolicies);
        if (ar.ok==true)
            return ar;
        else
            return AuthorizeResource(lt.g,portfolioPolicies);
        

    } 
    /**
     * Autoriza un recurso, dado un conjunto de políticas compradas en el portfolio
     */
    public static AuthorizationResponse AuthorizeResource(String resource, List<Policy> portfolioPolicies) {

        AuthorizationResponse ar = new AuthorizationResponse();
        ar.ok=false;
        ar.policies.clear();

        //y si no, miramos ALGUNA DEL PORTFOLIO
        for(Policy policy : portfolioPolicies)
        {
            if (policy.isInOffer() && policy.hasFirstTarget(resource))
            {
                ar.ok=true;
                ar.policies.add(policy);
                return ar;
            }
        }        
        /*
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, ASSETS_FILE);
        Asset asset = AssetManager.findAsset(resource);
        if (asset==null)
            return ar;
        
        List<Policy> policies = asset.getPolicies();
        if (policies.isEmpty())
            return ar;
    
        //SI HAY ALGUNA POLITICA ABIRTA, PERFECTO
        for(Policy policy :policies)
        {
            Policy policyFromStore = PolicyManagerOld.getPolicy(policy.getURI());
            if (policyFromStore==null)
                continue;
            if (policyFromStore.isInOffer() && policyFromStore.isOpen())
            {
                ar.ok=true;
                ar.policies.add(policyFromStore);
                return ar;
            }
            else if(policyFromStore.isInOffer())
            {
                ar.policies.add(policyFromStore);
            }
        }
*/
        
        
        return ar;
        
    }
   
}
