package oeg.license;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.Exception;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//JENA
import java.util.List;
import main.LicensesFound;
import main.LicensiusError;
import main.LicensiusResponse;
import oeg.rdflicense.RDFLicenseDataset;
import oeg.rdflicense.RDFUtils;
import org.apache.log4j.Logger;
import vroddon.utils.URLutils;

/**
 *
 * @author Victor
 */
public class LicenseFinder {

    public Model model = null;
    static String serror = "";
    private static final Logger logger = Logger.getLogger(LicenseFinder.class.getName());

    /**
     * Parsea un archivo RDF y lo carga en memoria
     */
    public boolean parseAfterDownload(String uri) {
        Logger.getLogger("licenser").info("Parsing " + uri);
        model = ModelFactory.createDefaultModel();
        String txt = URLutils.browseSemanticWeb(uri);
        return parseFromText(txt);
    }

    /**
     * Parses the ontology from a text
     * @param txt Text with an ontology
     */
    public boolean parseFromText(String txt) {
        model = ModelFactory.createDefaultModel();
        InputStream is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
        logger.info("Size: " + txt.length());
        try {
            model.read(is, null, "RDF/XML");
            logger.debug("Read as RDF/XML");
            return true;
        } catch (Exception e) {
            logger.debug("Failed as RDF/XML. " + e.getMessage());
            try {
                is.close();
                is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
                model.read(is, null, "TURTLE");
                logger.debug("Read as TURTLE");
                return true;
            } catch (Exception e2) {
                logger.debug("Failed as TURTLE. " + e2.getMessage());
            }
            return false;
       }
    }

    public boolean parseNew(String fileNameOrUri) {
//        Model modelx = ModelFactory.createDefaultModel();
//        RDFDataMgr.read(modelx, "http://rdflicense.linkeddata.es/dataset/rdflicense.ttl");
        return false;
    }

    /**
     * Parsea un archivo RDF y lo carga en memoria
     * @param fileNameOrUri
     */
    public boolean parseFromJena(String fileNameOrUri) {
        Logger.getLogger("licenser").info("Parsing " + fileNameOrUri);
        try {
            model = ModelFactory.createDefaultModel();
            try {
                model = model.read(fileNameOrUri, "RDF/XML");
                Logger.getLogger("licenser").info("Read RDF/XML");
            } catch (Exception e) {
                Logger.getLogger("licenser").info("It is not RDF/XML " + e.getMessage());
                try {
                    model = model.read(fileNameOrUri, "TURTLE");
                    Logger.getLogger("licenser").info("Read Turtle");
                } catch (Exception e2) {
                    Logger.getLogger("licenser").info("It is not TURTLE " + e2.getMessage());
                    try {
                        model.read(fileNameOrUri, "N-TRIPLE");
                        Logger.getLogger("licenser").info("Read N-TRIPLE");
                    } catch (Exception e3) {
                        Logger.getLogger("licenser").info("It is not N-TRIPLE " + e3.getMessage());
                        try {
                            model.read(fileNameOrUri, "N3");
                            Logger.getLogger("licenser").info("Read N3");
                        } catch (Exception e4) {
                            Logger.getLogger("licenser").info("It is nothing we know!");
                        }

                    }
                }
            }
            
            int count = RDFUtils.getNumberOfTriples(model);
            
            logger.info(count + " triples have been read");
            if (count == 0) {
                return false;
            }
//            is.close();
        } catch (Exception e) {
            logger.warn("Cannot read or understand- " + fileNameOrUri + "\n" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Busca una licencia a partir de un texto
     * @param txt String with an RDF document
     * @return A string with the URI of the famous license.
     */
    public String findLicenseFromText(String txto) {
        String salida = "";
        String txt = txto;
        Logger.getLogger("licenser").info("Parsing " + txto.length() + " bytes");
        boolean ok = parseFromText(txt);
        if (!ok) {
            return "Model not OK";
        }
        List<String> predicados = getRightsPredicates();
        for (String predicado : predicados) {
            String licencia = getLicense(predicado);
            if (!licencia.isEmpty()) {
                salida += licencia;
                break;
            }
        }
        return salida;
    }

    public static void main(String[] args) {
        String uritoscan = "http://data.semanticweb.org/ns/swc/swc_2009-05-09.rdf";
        uritoscan="http://purl.org/net/p-plan";
        String resultado = "Unknown";
        LicenseFinder lf = new LicenseFinder();
        resultado = lf.findLicenseTitle(uritoscan);
        System.out.println(resultado);
    }
    
    /**
     * Busca una licencia a partir de una URI
     * @param uri String URI resoluble
     */
    public String findLicense(String uri) {
        String salida = "";
        Logger.getLogger("licenser").info("Finding license in " + uri);
        boolean ok = false;

        ok = parseFromJena(uri);
        if (!ok) {
            ok = this.parseAfterDownload(uri);
        }
        if (!ok) {
            Logger.getLogger("licenser").info("Jena has failed to load the model");
            return "error. The model could not be loaded. The ontology is not valid";
        }
        List<String> predicados = getRightsPredicates();
        for (String predicado : predicados) {
//            logger.info("Testing "+predicado);
            String licencia = getLicense(predicado);
            if (!licencia.isEmpty()) {
                salida += licencia;
                break;
            }
        }
        if (salida.isEmpty())
            salida = "unknown";
        return salida;
    }
 /**
     * Busca una licencia a partir de una URI
     * @param uri String URI resoluble
     */
    public String findLicenseTitleNew(String uri) {
        String salida = "";
        Logger.getLogger("licenser").info("Finding license in " + uri);
        boolean ok = false;

        ok = parseFromJena(uri);
        if (!ok) {
            ok = this.parseAfterDownload(uri);
        }
        if (!ok) {
            Logger.getLogger("licenser").info("Jena has failed to load the model");
            return "error. The model could not be loaded. The ontology is not valid";
        }
        List<String> predicados = getRightsPredicates();
        for (String predicado : predicados) {
//            logger.info("Testing "+predicado);
            String licencia = getLicense(predicado);
            if (!licencia.isEmpty()) {
                salida += licencia;
                break;
            }
        }
        if (salida.isEmpty())
            salida = "unknown";
        return salida;
    }
    public String findLicenseTitle(String uritoscan) {
        String resultado = "unknown";
        LicenseFinder lf = new LicenseFinder();
        String license = lf.findLicense(uritoscan);
        if (!license.isEmpty()) {
            System.out.println("License: " + license);
            String rdflicense = RDFLicenseDataset.getRDFLicenseByLicense(license);
            if (rdflicense.isEmpty()) {
                System.out.println("No estamos muy seguros del resultado pero bueno");
                rdflicense = RDFLicenseDataset.getRDFLicenseByLicenseLax(license);
            }
            if (!rdflicense.isEmpty()) {
                System.out.println("RDFLicense: " + rdflicense);
                resultado = RDFUtils.getLabel(RDFLicenseDataset.modelTotal, rdflicense);
            }
        }
        return resultado;
    }

    /**
     * Looks for rights in the Model, with different combinations
     * @param URIrights
     */
    private String getLicense(String URIrights) {
        String out = "";
        List<String> ls = RDFUtils.getObjectsForProperty(model, URIrights);
        if (!ls.isEmpty()) {
            out = ls.get(0);
        }
        return out;
    }

    /**
     * Predicados conocidos que buscan licencias
     */
    public static List<String> getRightsPredicates() {
        List<String> predicados = new ArrayList();
        predicados.add("http://purl.org/dc/terms/rights");
        predicados.add("http://purl.org/dc/terms/license");
        predicados.add("http://creativecommons.org/ns#license");
        predicados.add("http://www.w3.org/1999/xhtml/vocab#license");
        predicados.add("http://purl.org/dc/elements/1.1/rights");
        return predicados;
    }
    
    
    /**
     * Busca una licencia a partir de una URI
     * @param uri String URI resoluble
     */
    public LicensiusResponse findLicenseEx(String uri) {
        LicensesFound lf = new LicensesFound();
        String salida = "";
        Logger.getLogger("licenser").info("Finding license in " + uri);
        boolean ok = false;

        ok = parseFromJena(uri);
        if (!ok) {
            ok = this.parseAfterDownload(uri);
        }
        if (!ok) {
            Logger.getLogger("licenser").info("Jena has failed to load the model");
            LicensiusError error = new LicensiusError(-5, "The model could not be loaded. The ontology is not valid");
            return error;
        }
        List<String> predicados = getRightsPredicates();
        for (String predicado : predicados) {
//            logger.info("Testing "+predicado);
            String licencia = getLicense(predicado);
            if (!licencia.isEmpty()) {
                salida += licencia;
                break;
            }
        }
        if (salida.isEmpty())
            salida = "unknown";
        return lf;
    }    
}
