package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * Profile-type validations.
 *
 * @author vroddon
 */
public class Validation07 implements Validation {

    /**
     * Returns the ODRL actions. Does NOT load the ontology in order to speed
     * up.
     */
    public static Set<String> getActions() {
        Set<String> set = new HashSet();
        set.add("http://www.w3.org/ns/odrl/2/acceptTracking");
        set.add("http://www.w3.org/ns/odrl/2/aggregate");
        set.add("http://www.w3.org/ns/odrl/2/annotate");
        set.add("http://www.w3.org/ns/odrl/2/anonymize");
        set.add("http://www.w3.org/ns/odrl/2/archive");
        set.add("http://www.w3.org/ns/odrl/2/attribute");
        set.add("http://creativecommons.org/ns#Attribution");
        set.add("http://creativecommons.org/ns#CommericalUse");
        set.add("http://www.w3.org/ns/odrl/2/compensate");
        set.add("http://www.w3.org/ns/odrl/2/concurrentUse");
        set.add("http://www.w3.org/ns/odrl/2/delete");
        set.add("http://www.w3.org/ns/odrl/2/derive");
        set.add("http://creativecommons.org/ns#DerivativeWorks");
        set.add("http://www.w3.org/ns/odrl/2/digitize");
        set.add("http://www.w3.org/ns/odrl/2/display");
        set.add("http://www.w3.org/ns/odrl/2/distribute");
        set.add("http://creativecommons.org/ns#Distribution");
        set.add("http://www.w3.org/ns/odrl/2/ensureExclusivity");
        set.add("http://www.w3.org/ns/odrl/2/execute");
        set.add("http://www.w3.org/ns/odrl/2/extract");
        set.add("http://www.w3.org/ns/odrl/2/give");
        set.add("http://www.w3.org/ns/odrl/2/grantUse");
        set.add("http://www.w3.org/ns/odrl/2/include");
        set.add("http://www.w3.org/ns/odrl/2/index");
        set.add("http://www.w3.org/ns/odrl/2/inform");
        set.add("http://www.w3.org/ns/odrl/2/install");
        set.add("http://www.w3.org/ns/odrl/2/modify");
        set.add("http://www.w3.org/ns/odrl/2/move");
        set.add("http://www.w3.org/ns/odrl/2/nextPolicy");
        set.add("http://creativecommons.org/ns#Notice");
        set.add("http://www.w3.org/ns/odrl/2/obtainConsent");
        set.add("http://www.w3.org/ns/odrl/2/play");
        set.add("http://www.w3.org/ns/odrl/2/present");
        set.add("http://www.w3.org/ns/odrl/2/print");
        set.add("http://www.w3.org/ns/odrl/2/read");
        set.add("http://www.w3.org/ns/odrl/2/reproduce");
        set.add("http://creativecommons.org/ns#Reproduction");
        set.add("http://www.w3.org/ns/odrl/2/reviewPolicy");
        set.add("http://www.w3.org/ns/odrl/2/sell");
        set.add("http://creativecommons.org/ns#ShareAlike");
        set.add("http://creativecommons.org/ns#Sharing");
        set.add("http://creativecommons.org/ns#SourceCode");
        set.add("http://www.w3.org/ns/odrl/2/stream");
        set.add("http://www.w3.org/ns/odrl/2/synchronize");
        set.add("http://www.w3.org/ns/odrl/2/textToSpeech");
        set.add("http://www.w3.org/ns/odrl/2/transform");
        set.add("http://www.w3.org/ns/odrl/2/translate");
        set.add("http://www.w3.org/ns/odrl/2/uninstall");
        set.add("http://www.w3.org/ns/odrl/2/watermark");
        return set;
    }

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = ODRLValidator.getModel(turtle);
        Set acciones = getActions();

        //Para cada politica
        Set<String> politicas = Preprocessing.getPoliticas(model);
        for (String politica : politicas) {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            if (Preprocessing.hasProfile(model, rpolitica)) {
                continue;
            }
            Model racimopadre = RDFUtils.getRacimo(model, rpolitica, new ArrayList());
            NodeIterator ni = racimopadre.listObjectsOfProperty(ODRL.PACTION);
            if (ni.hasNext()) {
                RDFNode node = ni.next();
                if (node.isResource() && !node.isAnon()) {
                    String saction = node.asResource().getURI();
                    if (!acciones.contains(saction)) {
                        return new ValidatorResponse(false, 415, "not valid. Undefined action while the policy does not use a profile.");
                    }
                }
                if (node.isResource() && node.isAnon()) {
                    NodeIterator ni2 = racimopadre.listObjectsOfProperty(node.asResource(), RDF.value);
                    while (ni2.hasNext()) {
                        RDFNode node2 = ni2.next();
                        if (node2.isResource()) {
                            String saction2 = node.asResource().getURI();
                            if (!acciones.contains(saction2)) {
                                return new ValidatorResponse(false, 415, "not valid. Undefined action while the policy does not use a profile.");
                            }

                        }
                    }
                }
            }
        }

        return new ValidatorResponse(true, 200, "valid");
    }

}
