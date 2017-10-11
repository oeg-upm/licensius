package oeg.odrlapi.validator;

import java.util.Set;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * For policies without explicit type, it asserts that they are Set.
 * @author vroddon
 */
public class TransformationN2a implements Transformation {

    @Override
    public Model transform(Model model) throws Exception {
        Set<String> politicas = Preprocessing.getPoliticas(model);
        for (String politica : politicas) {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            NodeIterator nx = model.listObjectsOfProperty(rpolitica, RDF.type);
            int count=0;
            while (nx.hasNext()) {
                RDFNode nodex = nx.next();
                if (!nodex.isResource()) {
                    continue;
                }
                Resource tipo = nodex.asResource();
                if (tipo.getURI().equals(ODRL.RSET.getURI()))
                    count++;
                if (tipo.getURI().equals(ODRL.ROFFER.getURI()))
                    count++;
                if (tipo.getURI().equals(ODRL.RAGREEMENT.getURI()))
                    count++;
            }
            if (count==0)
                model.add(rpolitica, RDF.type, ODRL.RSET);
        }
        return model;        
    }
    
}
