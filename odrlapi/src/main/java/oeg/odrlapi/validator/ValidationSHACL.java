package oeg.odrlapi.validator;

import java.util.List;
import oeg.odrlapi.rdf.RDFUtils;
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
 *
 * @author vroddon
 */
public class ValidationSHACL implements Validation {

    @Override
    public ValidatorResponse validate(String turtle) {
        Model policy = ODRLValidator.getModel(turtle);
        Model shapes = JenaUtil.createMemoryModel();
        shapes.read(ValidationSHACL.class.getResourceAsStream("/shapes.ttl"), "urn:dummy", FileUtils.langTurtle);
        Resource report = ValidationUtil.validateModel(policy, shapes, true);
        Model resultado = report.getModel();
        String informe = ModelPrinter.get().print(resultado);
        
        String mensaje ="";
        List<String> ls = RDFUtils.getObjectsForProperty(resultado, "http://www.w3.org/ns/shacl#resultMessage");
        for(String s : ls)
        {
            mensaje+=s+" \n";
        }
 //       System.out.println(informe);
        

        if (!informe.contains("<http://www.w3.org/ns/shacl#Violation>")) {
            if (!informe.contains("<http://www.w3.org/ns/shacl#Warning>")) {
                return new ValidatorResponse(true, 200, "ok");
            }
            return new ValidatorResponse(true, 200, "warning. " + mensaje);
        } else {
            return new ValidatorResponse(false, 415, "not valid. " + mensaje);
        }
    }

    public static void main(String[] args) {
        String rdf = "as<vvdssd B sdl";
        System.out.println(rdf);

        Model shapes = JenaUtil.createMemoryModel();
        shapes.read(ValidationSHACL.class.getResourceAsStream("/shape.ttl"), "urn:dummy", FileUtils.langTurtle);
        Model policy = JenaUtil.createMemoryModel();
        policy.read(ValidationSHACL.class.getResourceAsStream("/policy02.ttl"), "urn:dummy", FileUtils.langTurtle);
//                policy.read(ValidationExample.class.getResourceAsStream("/policy01.ttl"), "urn:dummy", FileUtils.langTurtle);
        Resource report = ValidationUtil.validateModel(policy, shapes, true);
        System.out.println(ModelPrinter.get().print(report.getModel()));
    }

}
