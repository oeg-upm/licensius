
package oeg.odrlapi.validator;

import java.util.List;
import java.util.Set;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.validator.Preprocessing;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * INTRODUCES THE GENERAL PROPERTIES (DEFINED AT POLICY LEVEL) IN EACH RULE LEVEL.
 * asset, target, assigner, assignee
 * @author vroddon
 */
public class TransformationN4 implements Transformation{

    @Override
    public Model transform(Model model) throws Exception {
        model = interiorizar(model, ODRL.PTARGET.getURI());
        model = interiorizar(model, ODRL.PASSIGNER.getURI());
        model = interiorizar(model, ODRL.PASSIGNEE.getURI());
        model = interiorizar(model, ODRL.PACTION.getURI());
        return model;
    }
    
    private static Model interiorizar(Model model, String sproperty) {
        Property rpropiedad = ModelFactory.createDefaultModel().createProperty(sproperty);

        Set<String> politicas = Preprocessing.getPoliticas(model);
        for (String politica : politicas) {
            //COMIENZO DEL INTENTO DE OBTENER TODAS LAS REGLAS DE UNA POLITICA
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            Set<Resource> rreglas = Preprocessing.getDirectRules(model, rpolitica);
            //AHORA, PARA CADA UNA DE LAS PROPIEDADES QUE HAY QUE INTERIORIZAR...
            List<String> items = RDFUtils.getObjectsGivenSP(model, politica, sproperty);
            for (String item : items) {
                for (Resource rregla : rreglas) {
                    if (item.startsWith("http")) {
                        model.add(rregla, rpropiedad, ModelFactory.createDefaultModel().createResource(item));
                    } else {
                        model.add(rregla, rpropiedad, item);
                    }
                }
                if (item.startsWith("http")) {
                    model.remove(rpolitica, rpropiedad, ModelFactory.createDefaultModel().createResource(item));
                } else {
                    model.remove(rpolitica, rpropiedad, ModelFactory.createDefaultModel().createLiteral(item));
                }
            }
        }
        return model;

    }    
        
    
}
