package oeg.rdftools;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import java.io.File;
import org.apache.log4j.Logger;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdtjena.HDTGraph;

/**
 *
 * @author vroddon
 */
public class Validator {

    static final Logger logger = Logger.getLogger(Validator.class);

    public Model getModel(String filename)
    {
        String fileNameOrUri = filename;
        fileNameOrUri = "file:" + fileNameOrUri;
        logger.debug("Parsing " + fileNameOrUri);
        Model model = null;
        System.out.println("Iniciando la conversión");
        try {
            model = ModelFactory.createDefaultModel();
            try {
                model = model.read(fileNameOrUri, null, "RDF/XML");
                logger.debug("Read RDF/XML");
            } catch (Exception e) {
                System.err.println(e.getMessage());
                model.close(); model = ModelFactory.createDefaultModel();
                logger.debug("It is not RDF/XML " + e.getMessage());
                try {
                    model = model.read(fileNameOrUri,null,  "TURTLE");
                    logger.debug("Read Turtle");
                } catch (Exception e2) {
                    System.err.println(e2.getMessage());
                    logger.info("It is not TURTLE " + e2.getMessage());
                    try {
                        model.read(fileNameOrUri,null,  "N-TRIPLE");
                        logger.info("Read N-TRIPLE");
                    } catch (Exception e3) {
                        System.err.println(e3.getMessage());
                        Logger.getLogger("licenser").debug("It is not N-TRIPLE " + e3.getMessage());
                        try {
                            model.read(fileNameOrUri, "N3");
                            logger.debug("Read N3");
                        } catch (Exception e4) {
                            System.err.println(e4.getMessage());
                            Logger.getLogger("licenser").debug("It is not N3 " + e.getMessage());
                            try{
                                HDT hdt = HDTManager.loadHDT(filename, null);
                                HDTGraph graph = new HDTGraph(hdt);
                                model = ModelFactory.createModelForGraph(graph);     
                                logger.info("Read HDT");
                            }catch(Exception e5){
                                System.err.println(e5.getMessage());
                                logger.debug("It is nothing we know!");
                            return null;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.warn("Cannot read or understand- " + fileNameOrUri + "\n" + e.getMessage());
            return null;
        }
        return model;        
    }
    
    
    /**
     */
    public String validate(String fileNameOrUri) {
        Model model = getModel(fileNameOrUri);
        if (model==null)
            return "The file does not validate";
        return "The file validates";
    }
                
                
    
}
