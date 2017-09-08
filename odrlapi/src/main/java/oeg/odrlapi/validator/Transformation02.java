package oeg.odrlapi.validator;

import java.util.List;
import oeg.odrlapi.rdf.RDFUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

/**
 * External references to the policy, to the assignee or to the assigner are
 * declared inside the policy.
 *
 * @author vroddon
 */
public class Transformation02 implements Transformation {

    @Override
    public Model transform(Model model) throws Exception {
        //PROCESSES ASSIGNEROF OUT OF THE POLICY
        List<String> lista = RDFUtils.getSubjectsForProperty(model, ODRL.PASSIGNEROF.getURI());
        for (String party : lista) {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PASSIGNEROF.getURI());
            for (String politica : politicas) {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                model.add(rpolitica, ODRL.PASSIGNER, rparty);
            }
        }

        //PROCESSES ASSIGNEEOF OUT OF THE POLICY
        lista = RDFUtils.getSubjectsForProperty(model, ODRL.PASSIGNEEOF.getURI());
        for (String party : lista) {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PASSIGNEEOF.getURI());
            for (String politica : politicas) {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                model.add(rpolitica, ODRL.PASSIGNEE, rparty);
            }
        }

        //PROCESSES HASPOLICY OUT OF THE POLICY
        lista = RDFUtils.getSubjectsForProperty(model, ODRL.PHASPOLICY.getURI());
        for (String party : lista) {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PHASPOLICY.getURI());
            for (String politica : politicas) {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                model.add(rpolitica, ODRL.PTARGET, rparty);
            }
        }
        return model;
    }

}
