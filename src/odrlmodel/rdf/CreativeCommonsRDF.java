package odrlmodel.rdf;


//JAVA
import oeg.rdf.commons.RDFUtils;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

//JENA
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import org.apache.log4j.Logger;

//ODRL
import odrlmodel.Action;
import odrlmodel.Asset;
import odrlmodel.Constraint;
import odrlmodel.MetadataObject;
import odrlmodel.Policy;
import odrlmodel.Rule;

/**
 * This class converts from/to CreativeCommons to RDF
 * @author Victor
 */
public class CreativeCommonsRDF {
    
    /**
     * Returns a CreativeCommons rule from a Resource
     * @param rpolicy A Jena resource with a CreativeCommons resource
     * @return an ODRL Rule compatible 
     */
    public static Rule findCreativeCommons(Resource rpolicy)
    {
        List<String> permisosCC = RDFUtils.getAllPropertyStrings(rpolicy, ODRLModel.PCCPERMISSION);
        List<String> constraintsCC = RDFUtils.getAllPropertyStrings(rpolicy, ODRLModel.PCCREQUIRES);
        //IF HANDLING A CREATIVE COMMONS CASE
        if (!permisosCC.isEmpty()) {
            Rule rulecc = new Rule();
            rulecc.uri = "";
            for (String permisoCC : permisosCC) {
                Action actionCC = new Action(permisoCC);
                actionCC = ODRLModel.enrichAction(actionCC);
                rulecc.actions.add(actionCC);
            }
            for (String sconstraint : constraintsCC) {
                Constraint constraint = new Constraint(sconstraint);
                constraint=ODRLModel.enrichConstraint(constraint);
                rulecc.constraints.add(constraint);
            }
            return rulecc;
        }    
        return null;
    }

    
}
