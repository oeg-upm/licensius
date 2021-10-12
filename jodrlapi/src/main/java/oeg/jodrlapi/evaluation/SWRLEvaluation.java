package oeg.jodrlapi.evaluation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

/**
 * This class demonstrates a sample reasoning with SWRL from a Java application
 * Dependencies: swrlapi and swrlapi-drools-engine, both in edu.stanford.swrl (see pom.xml)
 * 
 * @author vroddon
 */
public class SWRLEvaluation {

    String samplePolicy = "";

    public static void main(String[] args) {

        try {
            String spolicy = IOUtils.toString(SWRLEvaluation.class.getResourceAsStream("/samples/policy001.ttl"), "UTF-8");
            System.out.println("We have the following assertions:\n\n" + spolicy);
            InputStream is = new ByteArrayInputStream(spolicy.getBytes(StandardCharsets.UTF_8));
            OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
            OWLOntology policy = ontologyManager.loadOntologyFromOntologyDocument(is);
            SWRLRuleEngine engine = SWRLAPIFactory.createSWRLRuleEngine(policy);
            engine.createSWRLRule("q1", ":worksFor(?x1, ?x3) ^ :worksFor(?x2, ?x3) -> :colleageOf(?x1, ?x2)");
            engine.infer();
            Set<OWLAxiom> axiomas = engine.getInferredOWLAxioms();
            for(OWLAxiom axiom : axiomas)
            {
                String str = axiom.toString();
                if (str.contains("colleageOf"))
                    System.out.println(axiom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
