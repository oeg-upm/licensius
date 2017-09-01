package oeg.odrlapi.validator;

import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.topbraid.spin.util.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

/**
 * Validation using the SHACL constraints.
 * Double check at http://shacl.org/playground/
 * @author vroddon
 */
public class Validation02 implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model policy = ODRLValidator.getModel(turtle);
        Model shapes = JenaUtil.createMemoryModel();
        shapes.read(Validation02.class.getResourceAsStream("/shapes.ttl"), "urn:dummy", FileUtils.langTurtle);
        Resource report = ValidationUtil.validateModel(policy, shapes, true);
        String informe = ModelPrinter.get().print(report.getModel());
        
        String s = informe.replace(" ", "");
        s = s.replace("\t", "");
        s = s.replace("\n", "");
        s = s.replace("\r", "");
        if (s.contains("<http://www.w3.org/ns/shacl#conforms>true"))
            return new ValidatorResponse(true, 200, "ok");
        else
        {
            return new ValidatorResponse(false, 415, "not valid.  "+informe);
        }
    }

    public static void main(String[] args) {
        String rdf = "as<vvdssd  sdl";
        System.out.println(rdf);
        
		Model shapes = JenaUtil.createMemoryModel();
		shapes.read(Validation02.class.getResourceAsStream("/shape.ttl"), "urn:dummy", FileUtils.langTurtle);
		Model policy = JenaUtil.createMemoryModel();
                policy.read(Validation02.class.getResourceAsStream("/policy02.ttl"), "urn:dummy", FileUtils.langTurtle);
//                policy.read(ValidationExample.class.getResourceAsStream("/policy01.ttl"), "urn:dummy", FileUtils.langTurtle);
		Resource report = ValidationUtil.validateModel(policy, shapes, true);
                System.out.println(ModelPrinter.get().print(report.getModel()));
    }


}
