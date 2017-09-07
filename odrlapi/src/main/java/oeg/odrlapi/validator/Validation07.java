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

    public static Set<String> getOperators() {
        Set<String> set = new HashSet();
        set.add("http://www.w3.org/ns/odrl/2/eq");
        set.add("http://www.w3.org/ns/odrl/2/gt");
        set.add("http://www.w3.org/ns/odrl/2/gteq");
        set.add("http://www.w3.org/ns/odrl/2/lt");
        set.add("http://www.w3.org/ns/odrl/2/lteq");
        set.add("http://www.w3.org/ns/odrl/2/neq");
        set.add("http://www.w3.org/ns/odrl/2/isA");
        set.add("http://www.w3.org/ns/odrl/2/hasPart");
        set.add("http://www.w3.org/ns/odrl/2/isPartOf");
        set.add("http://www.w3.org/ns/odrl/2/isAllOf");
        set.add("http://www.w3.org/ns/odrl/2/isAnyOf");
        set.add("http://www.w3.org/ns/odrl/2/isNoneOf");
        return set;
    }

    public static Set<String> getLeftOperands() {
        Set<String> set = new HashSet();
        set.add("http://www.w3.org/ns/odrl/2/absolutePosition");
        set.add("http://www.w3.org/ns/odrl/2/absoluteSpatialPosition");
        set.add("http://www.w3.org/ns/odrl/2/absoluteTemporalPosition");
        set.add("http://www.w3.org/ns/odrl/2/absoluteSize");
        set.add("http://www.w3.org/ns/odrl/2/count");
        set.add("http://www.w3.org/ns/odrl/2/dateTime");
        set.add("http://www.w3.org/ns/odrl/2/delayPeriod");
        set.add("http://www.w3.org/ns/odrl/2/deliveryChannel");
        set.add("http://www.w3.org/ns/odrl/2/elapsedTime");
        set.add("http://www.w3.org/ns/odrl/2/event");
        set.add("http://www.w3.org/ns/odrl/2/fileFormat");
        set.add("http://www.w3.org/ns/odrl/2/industry");
        set.add("http://www.w3.org/ns/odrl/2/language");
        set.add("http://www.w3.org/ns/odrl/2/media");
        set.add("http://www.w3.org/ns/odrl/2/meteredTime");
        set.add("http://www.w3.org/ns/odrl/2/payAmount");
        set.add("http://www.w3.org/ns/odrl/2/percentage");
        set.add("http://www.w3.org/ns/odrl/2/product");
        set.add("http://www.w3.org/ns/odrl/2/purpose");
        set.add("http://www.w3.org/ns/odrl/2/recipient");
        set.add("http://www.w3.org/ns/odrl/2/relativePosition");
        set.add("http://www.w3.org/ns/odrl/2/relativeSpatialPosition");
        set.add("http://www.w3.org/ns/odrl/2/relativeTemporalPosition");
        set.add("http://www.w3.org/ns/odrl/2/relativeSize");
        set.add("http://www.w3.org/ns/odrl/2/resolution");
        set.add("http://www.w3.org/ns/odrl/2/spatial");
        set.add("http://www.w3.org/ns/odrl/2/spatialCoordinates");
        set.add("http://www.w3.org/ns/odrl/2/systemDevice");
        set.add("http://www.w3.org/ns/odrl/2/timeInterval");
        set.add("http://www.w3.org/ns/odrl/2/unitOfCount");
        set.add("http://www.w3.org/ns/odrl/2/version");
        set.add("http://www.w3.org/ns/odrl/2/virtualLocation");

        return set;
    }

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = ODRLValidator.getModel(turtle);
        Set acciones = getActions();
        Set lefts = getLeftOperands();
        Set operators = getOperators();

        //Para cada politica
        Set<String> politicas = Preprocessing.getPoliticas(model);
        for (String politica : politicas) {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            if (Preprocessing.hasProfile(model, rpolitica)) {
                continue;
            }
            Model racimopadre = RDFUtils.getRacimo(model, rpolitica, new ArrayList());

            //VALIDACION DE LAS ACCIONES
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

            //VALIDACION DE LOS LEFTOPERANDS
            ni = racimopadre.listObjectsOfProperty(ODRL.PLEFTOPERAND);
            if (ni.hasNext()) {
                RDFNode node = ni.next();
                if (node.isResource() && !node.isAnon()) {
                    String saction = node.asResource().getURI();
                    if (!lefts.contains(saction)) {
                        return new ValidatorResponse(false, 415, "not valid. Undefined left operand while the policy does not use a profile.");
                    }
                }
            }

            //VALIDACION DE LOS OPERATORS
            ni = racimopadre.listObjectsOfProperty(ODRL.POPERATOR);
            if (ni.hasNext()) {
                RDFNode node = ni.next();
                if (node.isResource() && !node.isAnon()) {
                    String saction = node.asResource().getURI();
                    if (!operators.contains(saction)) {
                        return new ValidatorResponse(false, 415, "not valid. Undefined operator while the policy does not use a profile.");
                    }
                }
            }
            
            //VALIDACION DE LOS CONFLICTTERMS
            ni = racimopadre.listObjectsOfProperty(ODRL.PCONFLICT);
            if (ni.hasNext()) {
                RDFNode node = ni.next();
                if (node.isResource() && !node.isAnon()) {
                    String saction = node.asResource().getURI();
                    if (!operators.contains(saction)) {
                        return new ValidatorResponse(false, 415, "not valid. Undefined conflict strategy while the policy does not use a profile.");
                    }
                }
            }            

        }

        return new ValidatorResponse(true, 200, "valid");
    }

}
