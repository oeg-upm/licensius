package oeg.odrlapi.validator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;

/**
 * Implements the logic of the ODRL Validation
 * @author vroddon
 */
public class ODRLValidator {

    private static final Logger logger = Logger.getLogger(ODRLValidator.class.getName());
    
    public static ValidatorResponse validate(String rdf) {
        System.out.println(rdf);
        ValidatorResponse vrok = new ValidatorResponse(true, 200, "valid");
        
        Model model = getModel(rdf);
        if (model==null)
            return new ValidatorResponse(false, 415,"not valid<br>999 The input could not be parsed as RDF Turtle, RDF/XML or NTRIPLES..." );

        Validation v = new Validation01();
        ValidatorResponse vr = v.validate(rdf);
        if (vr.valid == false)
            return vr;

        v = new Validation02();
        vr = v.validate(rdf);
        if (vr.valid == false)
            return vr;
        
        return vrok;
    }
    
    
    public static Model getModel(String rdf)
    {
        logger.info("Obteniendo el modelo de " + rdf);
        Model model = ModelFactory.createDefaultModel();
        model = ModelFactory.createDefaultModel();
        InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        logger.info("Parsing input of size: " + rdf.length());
        try {
            model.read(is, null, "RDF/XML");
            logger.debug("Read as RDF/XML");
            return model;
        } catch (Exception e) {
            logger.debug("Failed as RDF/XML. " + e.getMessage());
            try {
                is.close();
                is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
                model.read(is, null, "TURTLE");
                logger.debug("Read as TURTLE");
                return model;
            } catch (Exception e2) {
                logger.debug("Failed as TURTLE. " + e2.getMessage());
            }
            return null;
        }        
    }

}
