package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.List;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;


/**
 * THIS CLASS WILL BE REMOVED
 * THIS IS A VERY VERY FAST PATCH FOR COMPLIANCE
 * (UNEXPECTED BEHAVIOUR IN THE DHAPE TO BE CORRECTED)
 * this is about offers with exactly 1 assigner.
 * @author vroddon
 */
public class Validation05 implements Validation  {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = ODRLValidator.getModel(turtle);
        if (model==null)
            return new ValidatorResponse(false, 415,"not valid. The input could not be parsed as RDF Turtle, RDF/XML or NTRIPLES..." );
        ValidatorResponse vr = new ValidatorResponse(true, 200,"valid");

        List<String> politicas = RDFUtils.getSubjectOfType(model, ODRL.ROFFER);
        for (String politica : politicas) {
            //COMIENZO DEL INTENTO DE OBTENER TODAS LAS REGLAS DE UNA POLITICA
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            List<Resource> rreglas = new ArrayList();//quiero obetner toads las relgas
            NodeIterator it = model.listObjectsOfProperty(rpolitica, ODRL.PPERMISSION);
            while (it.hasNext()) {
                RDFNode nodo = it.nextNode();
                if (nodo.isResource()) {
                    rreglas.add(nodo.asResource());
                }
            }
            it = model.listObjectsOfProperty(rpolitica, ODRL.POBLIGATION);
            while (it.hasNext()) {
                RDFNode nodo = it.nextNode();
                if (nodo.isResource()) {
                    rreglas.add(nodo.asResource());
                }
            }
            it = model.listObjectsOfProperty(rpolitica, ODRL.PPROHIBITION);
            while (it.hasNext()) {
                RDFNode nodo = it.nextNode();
                if (nodo.isResource()) {
                    rreglas.add(nodo.asResource());
                }
            }
            //Aqui tengo todas las reglas ya
            for(Resource regla : rreglas)
            {
                int count =0;
                NodeIterator ni = model.listObjectsOfProperty(regla, ODRL.PASSIGNER);
                while(ni.hasNext())
                {
                    ni.next();
                    count++;
                }
                if (count!=1)
                    return new ValidatorResponse(false, 415,"not valid. Rule in offer without assigner" );
            }
        }
        
        return vr;
        
    }
    
}
