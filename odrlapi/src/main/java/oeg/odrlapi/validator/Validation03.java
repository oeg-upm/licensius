package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.List;
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
 * Warning for same class Offer/Agreement/Set and Permission/Prohibition/Duty.
 *
 * @author vroddon
 */
public class Validation03 implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = RDFSugar.getModel(turtle);
        if (model == null) {
            return new ValidatorResponse(false, 415, "not valid. The input could not be parsed as RDF Turtle, RDF/XML or NTRIPLES...");
        }

        Set<String> lista = Preprocessing.getPoliticas(model);
        for (String politica : lista) {
            List<String> clases = RDFUtils.getObjectsGivenSP(model, politica, RDF.type.getURI());
            int cuenta = 0;
            for (String clase : clases) {
                if (clase.equals(ODRL.RAGREEMENT.getURI())) {
                    cuenta++;
                }
                if (clase.equals(ODRL.ROFFER.getURI())) {
                    cuenta++;
                }
                if (clase.equals(ODRL.RSET.getURI())) {
                    cuenta++;
                }
            }
            if (cuenta > 1) {
                return new ValidatorResponse(true, 200, "Warning: agreements, offers and sets are incompatibles");
            }

            //Aquie mpieza la segunda validación
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            Set<Resource> lr = Preprocessing.getReglasDirectas(model, rpolitica);
            for (Resource r : lr) {
                int cuenta2 = 0;
                NodeIterator nx = model.listObjectsOfProperty(r, RDF.type);
                while(nx.hasNext())
                {
                    RDFNode nodex = nx.next();
                    if (!nodex.isResource())
                        continue;
                    if (nodex.asResource().getURI().equals(ODRL.RPERMISSION.getURI())) {
                        cuenta2++;
                    }
                    if (nodex.asResource().getURI().equals(ODRL.RPROHIBITION.getURI())) {
                        cuenta2++;
                    }
                    if (nodex.asResource().getURI().equals(ODRL.RDUTY.getURI())) {
                        cuenta2++;
                    }
                }
                if (cuenta2 > 1) {
                    return new ValidatorResponse(true, 200, "Warning: permissions, prohibitions and duties must be disjoint");
                }

            }
            
            
        }

        if (lista.isEmpty()) {
            return new ValidatorResponse(false, 415, "not valid. There is no policy, aggrement, offer or set");
        }

        return new ValidatorResponse(true, 200, "ok");
    }

}
