
package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.List;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

/**
 * Preprocesses a policy
 * @author vroddon
 */
public class Preprocessing {

    public static String preprocess(String rdf)
    {
        String out="";
        
        Model model = ODRLValidator.getModel(rdf);
        if (model==null)
            return rdf;
        
        List<String> lista = RDFUtils.getSubjectsForProperty(model, ODRL.PASSIGNEROF.getURI());
        for(String party : lista)
        {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PASSIGNEROF.getURI());
            for(String politica : politicas)
            {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                model.add(rpolitica, ODRL.PASSIGNER, rparty);
            }
        }
        lista = RDFUtils.getSubjectsForProperty(model, ODRL.PASSIGNEEOF.getURI());
        for(String party : lista)
        {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PASSIGNEEOF.getURI());
            for(String politica : politicas)
            {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                model.add(rpolitica, ODRL.PASSIGNEE, rparty);
            }
        }
        lista = RDFUtils.getSubjectsForProperty(model, ODRL.PHASPOLICY.getURI());
        for(String party : lista)
        {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PHASPOLICY.getURI());
            for(String politica : politicas)
            {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                model.add(rpolitica, ODRL.PTARGET, rparty);
            }
        }
        
        model = RDFUtils.inferClassFromRange(model, ODRL.PCONSTRAINT.getURI(), ODRL.RCONSTRAINT.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PPERMISSION.getURI(), ODRL.RPERMISSION.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PPROHIBITION.getURI(), ODRL.RPROHIBITION.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.POBLIGATION.getURI(), ODRL.RDUTY.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PDUTY.getURI(), ODRL.RDUTY.getURI());
        
        
        
        out = RDFUtils.getString(model);
        return out;
    }
    
}
