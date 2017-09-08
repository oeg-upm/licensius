
package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.List;
import oeg.odrlapi.rdf.RDFUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author vroddon
 */
public class Transformation03 implements Transformation  {

    @Override
    public Model transform(Model model) throws Exception {
        //MAKES SURE THAT THE IMPORTANT CLASSES ARE WELL INFERRED
        //      model = RDFUtils.inferClassFromRange(model, ODRL.PCONSTRAINT.getURI(), ODRL.RCONSTRAINT.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PPERMISSION.getURI(), ODRL.RPERMISSION.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PPROHIBITION.getURI(), ODRL.RPROHIBITION.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.POBLIGATION.getURI(), ODRL.RDUTY.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PDUTY.getURI(), ODRL.RDUTY.getURI());
        model = Transformation03.addConstraintTypes(model);
        return model;
    }
    /**
     * Determines if the constraints are Constraints or LogicalConstraints.
     * Asserts the RDF statement explicitly for its easier validation. If the
     * type is asserted, then nothing is done. It works for every possible
     * object of odrl:constraint or odrl:refinement
     *
     * @param model RDF model
     * @return Modified RDF model.
     */
    private static Model addConstraintTypes(Model model) {
        NodeIterator ni = model.listObjectsOfProperty(ODRL.PREFINEMENT);
        List<Statement> nuevas = new ArrayList();
        while (ni.hasNext()) {
            RDFNode node = ni.next();
            if (!node.isResource()) {
                continue;
            }
            Resource rnode = node.asResource();
            StmtIterator li = model.listStatements(new SimpleSelector(rnode, null, (RDFNode) null));
            boolean normal = false;

            while (li.hasNext()) {
                Statement st = li.next();
                Resource rpropiedad = st.getPredicate();
                int cuenta = 0;
                if (rpropiedad.getURI().equals(RDF.type.getURI())) {
                    continue;
                }
                if (rpropiedad.getURI().equals(ODRL.POPERATOR.getURI())) {
                    cuenta++;
                }
                if (rpropiedad.getURI().equals(ODRL.PLEFTOPERAND.getURI())) {
                    cuenta++;
                }
                if (rpropiedad.getURI().equals(ODRL.PRIGHTOPERAND.getURI())) {
                    cuenta++;
                }
                if (cuenta > 0) {
                    normal = true;
                    Statement sn = ResourceFactory.createStatement(rnode, RDF.type, ODRL.RCONSTRAINT);
                    nuevas.add(sn);
                }
            }
            if (!normal) {
                Statement sn = ResourceFactory.createStatement(rnode, RDF.type, ODRL.RLOGICALCONSTRAINT);
                nuevas.add(sn);
            }
        }
        ni = model.listObjectsOfProperty(ODRL.PCONSTRAINT);
        while (ni.hasNext()) {
            RDFNode node = ni.next();
            if (!node.isResource()) {
                continue;
            }
            Resource rnode = node.asResource();
            StmtIterator li = model.listStatements(new SimpleSelector(rnode, null, (RDFNode) null));
            boolean normal = false;
            while (li.hasNext()) {
                Statement st = li.next();
                Resource rpropiedad = st.getPredicate();
                int cuenta = 0;
                if (rpropiedad.getURI().equals(RDF.type.getURI())) {
                    continue;
                }
                if (rpropiedad.getURI().equals(ODRL.POPERATOR.getURI())) {
                    cuenta++;
                }
                if (rpropiedad.getURI().equals(ODRL.PLEFTOPERAND.getURI())) {
                    cuenta++;
                }
                if (rpropiedad.getURI().equals(ODRL.PRIGHTOPERAND.getURI())) {
                    cuenta++;
                }
                if (cuenta > 0) {
                    normal = true;
                    Statement sn = ResourceFactory.createStatement(rnode, RDF.type, ODRL.RCONSTRAINT);
                    nuevas.add(sn);

                }
            }
            if (!normal) {
                Statement sn = ResourceFactory.createStatement(rnode, RDF.type, ODRL.RLOGICALCONSTRAINT);
                nuevas.add(sn);

            }
        }
        for (Statement st : nuevas) {
            model.add(st);
        }

        return model;
    }    
}
