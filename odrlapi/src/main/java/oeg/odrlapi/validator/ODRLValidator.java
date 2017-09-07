package oeg.odrlapi.validator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.WeakHashMap;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;

/**
 * Implements the logic of the ODRL Validation
 *
 * @author vroddon
 */
public class ODRLValidator {

    private static final Logger logger = Logger.getLogger(ODRLValidator.class.getName());

    public static ValidatorResponse validate(String rdf) {
   //     System.out.println("==============RDF a ser validado:\n" + rdf);

        Model model = getModel(rdf);
        if (model == null) {
            return new ValidatorResponse(false, 415, "not valid.The input could not be parsed as RDF Turtle, RDF/XML or NTRIPLES...");
        }
        
        try{
            String rdf2 = Preprocessing.preprocess(rdf);
            model = ODRLValidator.getModel(rdf2);
        }catch(Exception e)
        {
            String message = "not valid.The input could not be parsed as RDF Turtle, RDF/XML or NTRIPLES...";
            if (e!=null && !e.getMessage().isEmpty())
                message = e.getMessage();
            return new ValidatorResponse(false, 415, message);
        }

        ValidatorResponse response = validateSingle(new Validation01(), null, rdf);
//        response = validateSingle(new Validation02(), response, rdf);
        response = validateSingle(new Validation03(), response, rdf);
        response = validateSingle(new Validation04(), response, rdf);
        response = validateSingle(new Validation05(), response, rdf);
        response = validateSingle(new Validation06(), response, rdf);
        response = validateSingle(new Validation07(), response, rdf);
        response = validateSingle(new Validation08(), response, rdf);
        response = validateSingle(new ValidationSHACL(), response, rdf);
        return response;
    }

    public static ValidatorResponse validateSingle(Validation validation, ValidatorResponse response, String rdf) {

        if (response==null)
            response = new ValidatorResponse(true, 200, "valid");

        try {
            ValidatorResponse vr = validation.validate(rdf);
            if (vr.valid == false) {
                return vr;
            }
            if (vr.getText().contains("warning") || vr.getText().contains("Warning")) {
                response.setText(response.getText() + "\n " + vr.getText());
                return response;
            }
            return response;
            
        } catch (Exception e) {
    //        System.err.println("Ha fallado la validación ... " + e.getMessage());
            return new ValidatorResponse(true, 200, "valid");
        }
        
    }

    // Sistema de caché para almacenar modelos
    public static Map<Integer, Model> cache = new WeakHashMap();
    public static Model getModel(String rdf) {
        int hash = rdf.hashCode();
        if (cache.size() > 50) {
            cache.clear();
        }
        Model model = cache.get(hash);
        if (model == null) {
            logger.info("Obteniendo el modelo de " + rdf);
            model = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
            logger.info("Parsing input of size: " + rdf.length());
            try {
                model.read(is, null, "RDF/XML");
                logger.debug("Read as RDF/XML");
                cache.put(hash, model);
                return model;
            } catch (Exception e) {
                logger.debug("Failed as RDF/XML. " + e.getMessage());
                try {
                    is.close();
                    is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
                    model.read(is, null, "TURTLE");
                    logger.debug("Read as TURTLE");
                    cache.put(hash, model);
                    return model;
                } catch (Exception e2) {
                    logger.debug("Failed as TURTLE. " + e2.getMessage());
                    return null;
                }
            }
        }
        return model;

    }

}
