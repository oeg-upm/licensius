package oeg.rdftools;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMaker;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import oeg.rdf.commons.RDFUtils;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.options.HDTSpecification;

/**
 *
 * @author vroddon
 */
public class Transcoder {

    static final Logger logger = Logger.getLogger(Transcoder.class);

    public String toTTL(String filename) {
        Validator v = new Validator();
        try {
            Model model = v.getModel(filename);
            if (model == null) {
                return "Transcoding could not be done";
            }
            ByteArrayOutputStream sos = new ByteArrayOutputStream();
            model.write(sos, "TTL");
            String s = sos.toString("UTF-8");
            int i=filename.length()-filename.lastIndexOf('.');
            i = (i==filename.length()+1) ? 1 : i;
            filename = filename.substring(0, filename.length() - i);
            filename += "(trans).ttl";
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.println(s);
            writer.close();
            return "File transcoded OK";
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return "Transcoding could not be done";
        }
    }

    public String toNTriples(String filename) {
        Validator v = new Validator();
        try {
            Model model = v.getModel(filename);
            if (model == null) {
                return "Transcoding could not be done";
            }
            ByteArrayOutputStream sos = new ByteArrayOutputStream();
            model.write(sos, "N-Triples");
            String s = sos.toString("UTF-8");
            int i=filename.length()-filename.lastIndexOf('.');
            i = (i==filename.length()+1) ? 1 : i;
            filename = filename.substring(0, filename.length() - i);
            filename += "(trans).nt";
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.println(s);
            writer.close();
            return "File transcoded OK";
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return "Transcoding could not be done";
        }
    }

    public String toNQuads(String filename, String grafo) {
        Validator v = new Validator();
        try {
//            Model model = v.getModel(filename);
            String rdf= FileUtils.readFileToString(new File(filename));
            Model model = RDFUtils.parseFromText(rdf);
            if (model == null) {
                return "Transcoding could not be done";
            }
            DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {
                public Graph create() {
                    return new GraphMem();
                }
            };
            Dataset dataset = DatasetImpl.wrap(new DatasetGraphMaker(maker));
            dataset.addNamedModel("http://localhost/datasets/default", model);
            OutputStream out = new FileOutputStream(filename);
            RDFDataMgr.write(out, dataset, Lang.NQUADS) ;
            out.close();
            
//            File f = new File(filename);
            
            return "File transcoded OK";
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return "Transcoding could not be done";
        }
    }

    public String toRDFXML(String filename) {
        Validator v = new Validator();
        try {
            Model model = v.getModel(filename);
            if (model == null) {
                return "Transcoding could not be done";
            }
            ByteArrayOutputStream sos = new ByteArrayOutputStream();
            model.write(sos, "RDF/XML");
            String s = sos.toString("UTF-8");
            int i=filename.length()-filename.lastIndexOf('.');
            i = (i==filename.length()+1) ? 1 : i;
            filename = filename.substring(0, filename.length() - i);
            filename += "(trans).rdf";
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.println(s);
            writer.close();
            return "File transcoded OK";
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return "Transcoding could not be done";
        }
    }

    /**
    // OPTIONAL: Add additional domain-specific properties to the header:
    //Header header = hdt.getHeader();
    //header.insert("myResource1", "property" , "value");
     */
    public String toHDT(String filename) {
        try {
            String baseURI = "http://www.oeg-upm.net";
            String rdfInput = filename;
            String hdtOutput = filename;
            int i=filename.length()-filename.lastIndexOf('.');
            i = (i==filename.length()+1) ? 1 : i;
            hdtOutput = hdtOutput.substring(0, filename.length() - i);
            hdtOutput += "(trans).hdt";
            System.out.println(i);
            
            try {
                String inputType = "ntriples";
                HDT hdt = HDTManager.generateHDT(rdfInput, baseURI, RDFNotation.parse(inputType), new HDTSpecification(), null);
                hdt.saveToHDT(hdtOutput, null);
            } catch (Exception e) {
                try {
                    String inputType = "rdfxml";
                    HDT hdt = HDTManager.generateHDT(rdfInput, baseURI, RDFNotation.parse(inputType), new HDTSpecification(), null);
                    hdt.saveToHDT(hdtOutput, null);
                } catch (Exception e2) {
                    try {
                        String inputType = "turtle";
                        HDT hdt = HDTManager.generateHDT(rdfInput, baseURI, RDFNotation.parse(inputType), new HDTSpecification(), null);
                        hdt.saveToHDT(hdtOutput, null);
                    } catch (Exception e3) {
                        String inputType = "nquads";
                        HDT hdt = HDTManager.generateHDT(rdfInput, baseURI, RDFNotation.parse(inputType), new HDTSpecification(), null);
                        hdt.saveToHDT(hdtOutput, null);
                    }
                }

            }
            return "File transcoded OK";
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return "Transcoding could not be done";
        }
    }
}
