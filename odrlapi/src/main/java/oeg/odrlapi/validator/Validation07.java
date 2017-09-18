package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import oeg.odrlapi.rdf.RDFSugar;
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
 * Can this validation be made by a SHACL Shape?
 * @author vroddon
 */
public class Validation07 implements Validation {


    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = RDFSugar.getModel(turtle);
        Set acciones = ODRL.getActions();
        Set lefts = ODRL.getLeftOperands();
        Set operators = ODRL.getOperators();

        //Para cada politica
        Set<String> politicas = Preprocessing.getPoliticas(model);
        for (String politica : politicas) {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            if (Validation07.hasProfile(model, rpolitica)) {
                continue;
            }
            Model racimopadre = RDFUtils.getRacimoExceptProperty(model, rpolitica, new ArrayList());

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

    public static boolean hasProfile(Model model, Resource rpolitica) {
        NodeIterator ni = model.listObjectsOfProperty(rpolitica, ODRL.PPROFILE);
        return ni.hasNext();
    }
    
    
}
