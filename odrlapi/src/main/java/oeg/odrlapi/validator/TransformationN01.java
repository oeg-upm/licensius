package oeg.odrlapi.validator;

import java.util.List;
import oeg.odrlapi.rdf.RDFUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

/**
 * Transformation N1.
 * N1. Internalization of parties and assets declared out of the policy
 * The transformation replicates external references to the Policy, to the Assignee or to the Assigner 
 * (through the odrl:hasPolicy, odrl:assignerOf and odrl:assigneeOf respectively), asserting that information within the Policy, i.e., 
 * new properties with odrl:target, odrl:assigner and odrl:assignee are created, where the subject is the referred Policy. 
 * Please note that more than one target, assigner or assignee can exits.
 * External references to the policy, to the assignee or to the assigner are
 * declared inside the policy.
 *
 * @author vroddon
 */
public class TransformationN01 implements Transformation {

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
