package ldconditional.auth;
import ldconditional.auth.AuthorizationResponse;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import ldconditional.handlers.HandlerResource;
import java.util.ArrayList;
import java.util.List;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.Grafo;
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
 *
 * @author vroddon
 */
public class Authorizer {
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
            if (policy.hasPlay() && policy.hasFirstTarget(resource))
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
            if (policyFromStore.hasPlay() && policyFromStore.isOpen())
            {
                ar.ok=true;
                ar.policies.add(policyFromStore);
                return ar;
            }
            else if(policyFromStore.hasPlay())
            {
                ar.policies.add(policyFromStore);
            }
        }
*/


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
    public boolean pagado = false;
    public static List<LicensedTriple> Authorize2(ConditionalDataset cd, List<LicensedTriple> lst, Portfolio portfolio) {
        List<LicensedTriple> validos = new ArrayList();

        List<Grafo> lg = cd.getDatasetVoid().getGrafos();

        for (LicensedTriple lt : lst) {
            Grafo grafo = HandlerResource.getGrafo(lg, lt.g);
            if (grafo == null) {
                continue;
            }
            if (grafo.isOpen()) {
                validos.add(lt);
                continue;
            }
            AuthorizationResponse ar = new AuthorizationResponse();
            ar.ok = false;
            ar.policies.clear();
            for (Policy policy : portfolio.policies) {
                if (policy.hasPlay() && policy.hasFirstTarget(lt.g)) {
                    ar.ok = true;
                    ar.policies.add(policy);
                    lt.pagado=true;
                    validos.add(lt);
                    continue;
                }
            }
            if (ar.ok == false) {
                List<LicensedTriple> llt = LicensedTriple.concealInformation(lt, grafo.getPolicies());
                validos.addAll(llt);
            }
        }

        return validos;
    }

    public static List<String> PolicyMatcher(Portfolio p)
    {
        List<String> grafos = new ArrayList();


        List<Asset> assets = AssetManager.readAssets();
        for (Asset asset : assets)
        {
            AuthorizationResponse r = Authorizer.AuthorizeResource(asset.getURI(), p.policies);
            if (r.ok)
                grafos.add(asset.getURI());
        }

    //        grafos.add("http://salonica.dia.fi.upm.es/ldr/dataset/default");
    //        grafos.add("http://salonica.dia.fi.upm.es/ldr/dataset/Geo");

        return grafos;
    }
   
}
