package oeg.odrlapi.validator;

import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;

/**
 * Verifies that the ontology and the policy are consistent (OWL).
 *
 * @author vroddon
 */
public class Validation02 implements Validation {


//    public final static String ODRLONTO = "http://w3c.github.io/poe/vocab/ODRL22.ttl";
    public final static String ODRLONTO = "https://www.w3.org/ns/odrl/2/ODRL22.ttl";
    private static Model odrlmodel = null;

    /**
     *
     */
    public static Model getODRLModel() {
        if (odrlmodel != null) {
            return odrlmodel;
        }
        odrlmodel = ModelFactory.createDefaultModel();
        try {
            String rdf = new Scanner(new URL(ODRLONTO).openStream(), "UTF-8").useDelimiter("\\A").next();
            odrlmodel = RDFSugar.getModel(rdf);
        } catch (Exception e) {
            return null;
        }
        return odrlmodel;
    }

    @Override
    public ValidatorResponse validate(String turtle) {
        Model model = RDFSugar.getModel(turtle);
        model.add(getODRLModel());
        Model odrl = Validation02.getODRLModel();
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(odrl);
        InfModel infmodel = ModelFactory.createInfModel(reasoner, model);
        String reporte = "";
        ValidityReport validity = infmodel.validate();
        if (validity.isValid()) {
            return new ValidatorResponse(true, 200, "");
        } else {
    //        System.out.println("Conflicts");
            for (Iterator i = validity.getReports(); i.hasNext();) {
                reporte+=(" - " + i.next());
            }
            return new ValidatorResponse(false, 415, "not valid. OWL Inconsistent. " + reporte);
        }
    }

    public static void main(String[] args) {
        System.out.println("Validating consistency: ");
        String sample003 = "@prefix odrl: <http://www.w3.org/ns/odrl/2/> .\n"
                + "@prefix dct: <http://purl.org/dc/terms/> .\n"
                + "\n"
                + "#valid. \n"
                + "#This is not forbidden by the spec, although it is a clear contradiction: Offer and Agreement at the same time\n"
                + "<http://odrlapi.appspot.com/samples/sample003>\n"
                + "    a odrl:Offer, odrl:Agreement;\n"
                + "    dct:source \"victor\" ;\n"
                + "    odrl:permission [\n"
                + "        a odrl:Permission ;\n"
                + "        odrl:assigner <http://example.com/user:711> ;\n"
                + "        odrl:assignee <http://example.com/user:314> ;\n"
                + "        odrl:target <http://example.com/asset:9898> ;\n"
                + "        odrl:action odrl:reproduce\n"
                + "    ] .";
        Validation02 v = new Validation02();
        v.validate(sample003);
    }

}
