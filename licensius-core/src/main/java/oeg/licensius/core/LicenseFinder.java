package oeg.licensius.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//LICENSIUS
import oeg.licensius.model.LicensiusFound;
import oeg.licensius.model.LicensiusError;
import oeg.licensius.model.LicensiusResponse;
import oeg.licensius.rdflicense.RDFLicense;
import oeg.licensius.rdflicense.RDFLicenseDataset;
import oeg.licensius.rdflicense.RDFUtils;
import oeg.licensius.util.URLutils;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 * This class implements some basic functionality to search licenses
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
        logger.info("Se ha descargado una cadena de " + txt.length() + " caracteres");
        return parseFromText(txt);
    }

    /**
     * Parses the ontology from a text
     *
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

    /**
     * Parsea un archivo RDF y lo carga en memoria
     *
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
     *
     * @param txt String with an RDF document
     * @return A string with the URI of the famous license.
     */
    public String findLicenseFromRDFText(String txto) {
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

    /**
     * Parsea un archivo RDF y lo carga en memoria
     * https://cloud.google.com/appengine/docs/standard/java/issue-requests
     *
     * @param fileNameOrUri
     */
    public boolean parseFromGoogleThenJena(String fileNameOrUri) {
        Logger.getLogger("licenser").info("Parsing " + fileNameOrUri);
        try {
            model = ModelFactory.createDefaultModel();

            URL url = new URL(fileNameOrUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer json = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();
            String modelostr = json.toString();
            model.read(new ByteArrayInputStream(modelostr.getBytes()), null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Busca una licencia a partir de una URI
     *
     * @param uri String URI resoluble
     */
    public String findLicense(String uri) {
        String salida = "";
        Logger.getLogger("licenser").info("Finding license in " + uri);
        boolean ok = false;

//        ok = parseFromGoogleThenJena(uri);
//          OK y bueno si no es para google
//        ok = parseFromJena(uri);
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
        if (salida.isEmpty()) {
            salida = "unknown";
        }
        return salida;
    }

    /**
     * Busca una licencia a partir de una URI
     *
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
        if (salida.isEmpty()) {
            salida = "unknown";
        }
        return salida;
    }

    public String findLicenseTitleRaw(String rawrdf) {
        String resultado = "unknown";
        LicenseFinder lf = new LicenseFinder();
        String license = lf.findLicenseFromRDFText(rawrdf);
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
     *
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
     *
     * @param uri String URI resoluble
     */
    public LicensiusResponse findLicenseInRDF(String uri) {
        LicensiusFound lf = new LicensiusFound();
        Logger.getLogger("licenser").info("Finding license in " + uri);

//        testSendMail("finding license in " + uri);

        boolean ok = false;

        //LA SIGUIENTE LINEA SOLO ESTA COMENTADA PARA QUE PUEDA FUNCIONAR EN GOOGLE. SI NO, SERIA ESTUPENDO TODO
        //ok = parseFromJena(uri);
        if (!ok) {
            Logger.getLogger("licenser").info("Could not be parsed by jena " + uri + ". Instead, lets try to download it...");
            ok = this.parseAfterDownload(uri);
        }
        if (!ok) {
            Logger.getLogger("licenser").info("Jena has failed to load the model");
            LicensiusError error = new LicensiusError(-5, "The model could not be loaded. The ontology is not valid");
            return error;
        }
        List<String> predicados = getRightsPredicates();
        for (String predicado : predicados) {
            String licencia = getLicense(predicado);
            if (!licencia.isEmpty()) {
                String urix = licencia;

                String urimencionada = RDFUtils.extractURIFromText(urix);
                RDFLicense rdfl = RDFLicenseDataset.getRDFLicenseByURI(urimencionada);      //ESTA BÚSQUEDA SE HACE ONLINE. ESO NO ES ÓPTIMO!
                if (rdfl == null) {
                    urix = "";
                } else {
                    urix = rdfl.getURI();
                }
                lf.add(urix, predicado + " " + licencia, "100");
            }
        }
        return lf;
    }

    /**
     * Busca una licencia a partir de un texto
     *
     * @param txt String with an RDF document
     * @return A string with the URI of the famous license.
     */
    public LicensiusResponse findLicenseFromRDFText2(String txt) {
        LicensiusFound lf = new LicensiusFound();
        String salida = "";
        boolean ok = parseFromText(txt);
        if (!ok) {
            LicensiusError error = new LicensiusError(-9, "Model not valid");
            return error;
        }
        List<String> predicados = getRightsPredicates();
        for (String predicado : predicados) {
            String licencia = getLicense(predicado);
            if (!licencia.isEmpty()) {
                salida += licencia;
                String urix = licencia;
                RDFLicense rdfl = RDFLicenseDataset.getRDFLicenseByURI(licencia);
                if (rdfl == null) {
                    urix = "";
                }
                lf.add(urix, predicado + " " + licencia, "100");
            }
        }
        return lf;
    }

    static Map<String, String> mapa = null;

    /**
     * Busca una licencia a partir de un texto
     *
     * @param txt Texto en el cual ha de buscarse.
     */
    /*    public LicensiusResponse findLicenseInText(String txt) {
        LicensiusFound lf = new LicensiusFound();
        String s = LicenseGuess.guessLicense(txt);
        if (s == null || s.isEmpty())
        {
            LicensiusError error = new LicensiusError(-7, "Internal error");
            return error;
        }
        String rdf = RDFLicenseDataset.getRDFLicenseByLicense(s);
        if (rdf!=null && !rdf.isEmpty())
            lf.add(rdf,"","100");
        else
            lf.add("",s,"100");
        return lf;
    }        
     */
    
    
    public static void main(String[] args) {
        String uritoscan = "http://data.semanticweb.org/ns/swc/swc_2009-05-09.rdf";
        uritoscan = "http://purl.org/net/p-plan";
        String resultado = "Unknown";
        LicenseFinder lf = new LicenseFinder();
        
        String url = "http://datos.gob.es/es/catalogo/l01280066-seguridad-vial-accidentes-de-trafico-2014";
        
        resultado = lf.findLicenseTitle(uritoscan);
        System.out.println(resultado);
    }

    public static void testSendMail(String s) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        System.out.println("Vamos a intentar enviar un correo");
        logger.info("Enviando correo electrónico");

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("debug@licensius.appspotmail.com", "Licensius Debug"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("vrodriguez@mirubee.com", "Victor"));
            msg.setSubject("Debug info");
            msg.setText(s);
            Transport.send(msg);
        } catch (AddressException e) {
            logger.warn(e.getMessage());
            System.err.println(e.getMessage());
        } catch (MessagingException e) {
            logger.warn(e.getMessage());
            System.err.println(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage());
            System.err.println(e.getMessage());
        }
    }
}
