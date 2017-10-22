package licenser;

//JAVA
import oeg.ckan.CKANExplorer;
import oeg.ckan.CKANExplorerold;
import oeg.ckan.CKANDatasets;
import java.util.Date;
import java.util.List;

//APACHE CLI
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

//APACHE LOG4J
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import vroddon.sw.Modelo;
import vroddon.web.utils.Downloader;

/**
 * Clase con el método main
 * @author vroddon
 */
public class MainLicenser {

    private CommandLineParser parser = null;
    private CommandLine cmdLine = null;
    private static Options options = new Options();

    public static void main(String[] args) {
        //apagamos los logs.
        //org.apache.log4j.BasicConfigurator.configure(new NullAppender());

        MainLicenser m = new MainLicenser();
        m.Parser(args);
        System.exit(0);
    }

    public MainLicenser() {
        initLogger();
        Logger.getLogger("inspectee").info("Licenser has started");
    }

    public boolean Parser(String[] args) {

        //DOS LINEAS PARA DEPURAR. COMENTAR CUANDO NO SE ESTÉ DEPURANDO
//      String test[]={"-w", "-i", "http://www.w3.org/wiki/DataSetRDFDumps"};args=test;
//        String test[]={"-k"};args=test;
//        String test[]={"-i", "data/lexvo.rdf", "-o", "data/lexvo2.rdf", "-a", "ccby"};args=test;

        String input = "";
        String output = "";
        try {

            options.addOption("sp", false, "Search predicate");
            options.addOption("a", true, "Adds a license (given the URL as a parameter) in a given RDF");
            options.addOption("e", false, "Explores massively the SPARQL endpoints looking for licensing terms");
            options.addOption("h", false, "shows this (Help)");
            options.addOption("i", true, "specifies the Input (either a filename or a URI)");
            options.addOption("o", true, "specifies the Output file");
            options.addOption("f", false, "Finds a license in the given input dataset");
            options.addOption("q", false, "finds the licenses in a given sparQl end-point");
            options.addOption("s", false, "gets URLs of possible datasets in a webpage (in RDF/XML, Turtle or N3, including compressed)");
            options.addOption("w", false, "scans the given Webpage for licenses by searching for typical keywords");
            options.addOption("k", false, "checks the cKan datasets and gets statistics of license use");
            options.addOption("l", false, "crawls the web to find illegal copies of your data STILL IN PROGRESS");
            options.addOption("j", false, "Joker option (TEST PURPOSES)");
            options.addOption("X", false, "JOKER2");


            parser = new BasicParser();
            cmdLine = parser.parse(options, args);


            if (args.length == 0) {
                showHelp();
            }
            if (cmdLine.hasOption("X")) {
                List<String> ldatasets = CKANDatasets.getPreloadedDatasets("loddatasets.txt");
                for (String dataset : ldatasets) {
                    String extension = "";
                    String url = CKANExplorerold.getVoidURI(dataset);
                    System.out.println(dataset + "\t" + url);
                    int i = url.lastIndexOf('/');
                    if (i > 0) {
                        extension = url.substring(i + 1);
                    }
                    if (!url.isEmpty()) {
                        Downloader.Descargar(url, "void/" + dataset + ".rdf");
                    }
                }

                System.exit(0);
            }
            //TRATAMOS LOS PARÁMETROS...
            if (cmdLine.hasOption("i")) {
                input = cmdLine.getOptionValue("i");
                boolean ok = Modelo.parser(input);
                if (!ok) {
                    return false;
                }
            }
            if (cmdLine.hasOption("o")) {
                output = cmdLine.getOptionValue("o");
            }
            if (cmdLine.hasOption("f")) {
                if (input.isEmpty()) {
                    System.err.println("Error. Use the command -f together with the parameter -i <filename>");
                    return false;
                }
                LicenseFinder lf = new LicenseFinder();
                lf.findLicenseInOntology();
                if (true) {
                    return true;
                }

                boolean bOK = true;
                if (!bOK) {
                    Logger.getLogger("licenser").warn("Error while processing the model");
                } else {
                    String license = lf.findLicense();
                    if (license.isEmpty()) {
                        System.out.println("No license found");
                    } else {
                        System.out.println("Licensed as: " + license);
                    }
                }
            }
            if (cmdLine.hasOption("h")) {
                showHelp();
            }

            if (cmdLine.hasOption("s")) {
                if (input.isEmpty()) {
                    System.err.println("Error. Use the command -s together with the parameter -i <filename>");
                    return false;
                }
                WebLicenseAnalyzer wla = new WebLicenseAnalyzer();
                //http://www.w3.org/wiki/DataSetRDFDumps
                List<String> uris = WebLicenseAnalyzer.getLinkedRDF(input);
                System.out.println("Found " + uris.size() + " candidates to be RDF datadumps");
                for (String s : uris) {
                    System.out.println(s);
                }
            }
            if (cmdLine.hasOption("s")) {
                if (input.isEmpty()) {
                    System.err.println("Error. Use the command -w together with the parameter -i <filename>");
                    return false;
                }
                WebLicenseAnalyzer wla = new WebLicenseAnalyzer();
                //http://www.w3.org/wiki/DataSetRDFDumps
                String licencia = WebLicenseAnalyzer.checkForLicenseHint(input);
                System.out.println(licencia);
            }
            if (cmdLine.hasOption("k")) {
                CKANExplorer.listLicenses(659); //659
            }
            if (cmdLine.hasOption("e")) {
                EndPointExplorer.init();
                EndPointExplorer.launchMassiveQueries("select * where {?s <http://purl.org/dc/terms/license> ?o} limit 100");
            }


            if (cmdLine.hasOption("sp")) {
                LargeFileParser.searchPredicatesInLargeFile("data1.nq", "<http://purl.org/dc/terms/license>");
            }

            if (cmdLine.hasOption("a")) {
                String s = "Error";
                boolean ok = LicenseAdder.addLicense(input, output, cmdLine.getOptionValue("a"));
                if (ok) {
                    s = "Done";
                }
                System.out.println(s);
            }

        } catch (Exception e) {
            Logger.getLogger("licenser").warn("Error parsing parameters");
            e.printStackTrace();
            return true;
        }
        return true;
    }

    private void showHelp() {
        System.out.println("licenser Utility for licensing, verifying license use and crawling for non-licensed data uses in RDF datasets and SPARQL endpoints. \n2013 Ontology Engineering Group - Universidad Politecnica de Madrid");
        new HelpFormatter().printHelp(MainLicenser.class.getCanonicalName(), options);
    }

    /**
     * Inicializa el logger. 
     * Esta es la versión más depurada que tengo a fecha de 10.2013
     */
    public static void initLogger() {
        PatternLayout layout = new PatternLayout("%d{ABSOLUTE} %5p %C{1}:%L - %m%n");
        try {
            FileAppender appender = new FileAppender(layout, "logs.txt", false); //appender de archivo de logs
            ConsoleAppender cappender = new ConsoleAppender(layout);
            Logger.getRootLogger().addAppender(appender);
            Logger.getRootLogger().addAppender(cappender);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.getRootLogger().setLevel((Level) Level.DEBUG);
        Logger.getRootLogger().info("Ejecutado: " + new Date().toString() + " - Iniciando logger");
        Logger.getRootLogger().info("=========================================================");

//        BasicConfigurator.configure();
        Logger.getLogger("com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler").setLevel(Level.OFF);
        Logger.getLogger("com.hp.hpl.jena").setLevel(Level.OFF);
        Logger.getRootLogger().info("Internet connection " + vroddon.system.InformacionSistema.hasInternetConnection());
    }
}
