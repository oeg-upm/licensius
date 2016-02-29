package oeg.licensius.core;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import oeg.licensius.model.LicensiusError;
import oeg.licensius.model.LicensiusResponse;
import oeg.licensius.model.LicensiusSimple;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseCheck;
import oeg.rdflicense.RDFLicenseDataset;
import org.apache.log4j.LogManager;

import oeg.vroddon.util.SystemInformation;
import org.apache.log4j.BasicConfigurator;
import org.json.simple.JSONArray;

/**
 * This is the entry point for the core funcionality of licensius. Licensius
 * funcitonality is offered from the command line. This is also the core library
 * powering the licensius services. The execution of the methods here may be
 * delayed up to one minute, as fresh information is retrieved from the web.
 *
 * java -jar licensius-core.jar -legalcode
 * http://purl.org/NET/rdflicense/ukogl-nc2.0
 *
 * @author vrodriguez
 */
public class Licensius {

    static final Logger logger = Logger.getLogger(Licensius.class);
    private static final String version = "1.0.0";
    private static final String name = "Licensius core";

    public static void main(String[] args) {
        String s = parseCommandLineParams(args);
        System.out.println(s);
    }

    /**
     */
    public static void init() {
        initLogger(true, false);
    }

    /**
     * Parses the command line parameters, invoking the proper methods
     */
    private static String parseCommandLineParams(String[] args) {
        StringBuilder res = new StringBuilder();
        CommandLineParser parser = null;
        CommandLine cli = null;
        try {
            Options options = new Options();
            options.addOption("help", false, "shows help (Help)");
            options.addOption("version", false, "shows the version info");
            options.addOption("nologs", false, "disables the logging funcionality");
            options.addOption("listlicenses", false, "shows the list of RDFLicenses available at http://rdflicense.linkeddata.es/. Takes no input. Output is json");
            options.addOption("legalcode", true, "shows the legal text of a license in English if available.  Input is RDFLicense URI. Output is json.");
            options.addOption("getinfolicense", true, "shows basic information of a license in json. Input is RDFLicense URI. Output is json.");
            options.addOption("findlicenseinrdf", true, "finds possible licenses in a RDF document. Input is URI pointing to an online RDF document. Output is json.");
            options.addOption("findlicenseintxt", true, "finds possible licenses in a TEXT document. Input is URI pointing to an online TXT document. Output is json.");
            options.addOption("isopen", true, "determines if a license is 'open' according to the http://www.opendefinition.com. Output is 'true' or 'false'");

            parser = new BasicParser();
            cli = parser.parse(options, args);

            ///HELP
            if (cli.hasOption("help") || (args.length == 0)) {
                new HelpFormatter().printHelp(Licensius.class.getCanonicalName(), options);
                return "";
            }

            ///NOLOGS
            if (cli.hasOption("nologs")) {
                initLoggerDisabled();
            } else {
                initLogger(true, true);
            }

            //VERSION
            // java -jar licensius-core.jar -version -nologs
            if (cli.hasOption("version")) {
                res.append(name + " version ").append(version).append("\n");
                res.append("Last compiled: ").append(SystemInformation.getCompileTimeStamp(Licensius.class)).append(" en ").append(System.getenv("COMPUTERNAME")).append("\n");
                res.append("(C) 2016 Ontology Engineering Group (Universidad Politécnica de Madrid)").append("\n");
                res.append(SystemInformation.getSystemInfo());
            }

            //LEGALCODE
            // java -jar licensius-core.jar -legalcode http://purl.org/NET/rdflicense/ukogl-nc2.0
            if (cli.hasOption("legalcode")) {
                String uri = cli.getOptionValue("legalcode");
                RDFLicense lic = new RDFLicense(uri);
                res.append(lic.getLegalCode("en"));
            }

            //LISTLICENSES
            // java -jar licensius-core.jar -listlicences
            if (cli.hasOption("listlicenses")) {
                JSONArray array = new JSONArray();
                RDFLicenseDataset dataset = new RDFLicenseDataset();
                List<RDFLicense> list = dataset.listRDFLicenses();
                for (RDFLicense rdf : list) {
                    String uri = rdf.getURI();
                    if (uri != null) {
                        array.add(rdf.getURI());
                    }
                }
                res.append(array.toJSONString());
            }

            //GETINFOLICENSE
            //java -jar licensius-core.jar -getinfolicense http://purl.org/NET/rdflicense/ukogl-nc2.0
            if (cli.hasOption("getinfolicense")) {
                String uri = cli.getOptionValue("getinfolicense");
                RDFLicense rdf = RDFLicenseDataset.getRDFLicense(uri);
                if (rdf == null) {
                    res.append("License not found");
                } else {
                    res.append(rdf.toLongJSON());
                }
            }

            //FINDLICENSEINRDF
            //java -jar licensius-core.jar -findlicenseinrdf http://purl.org/NET/p-plan
            if (cli.hasOption("findlicenseinrdf")) {
                String uri = cli.getOptionValue("findlicenseinrdf");
                LicenseFinder lf = new LicenseFinder();
                LicensiusResponse le = lf.findLicenseInRDF(uri);
                res.append(le.getJSON());
            }

            //FINDLICENSEINTXT
            //java -jar licensius-core.jar -findlicenseintxt https://github.com/pyvandenbussche/lov
            if (cli.hasOption("findlicenseintxt")) {
                String uri = cli.getOptionValue("findlicenseintxt");
                LicenseGuess lg = new LicenseGuess();
                String s=lg.licenseguessfromuri(uri); 
                res.append(s);
            }
            
            //ISOPEN
            //java -jar licensius-core.jar -isopen http://purl.org/NET/rdflicense/ukogl-nc2.0
            if (cli.hasOption("isopen")) {
                String uri = cli.getOptionValue("isopen");
                RDFLicense lic = RDFLicenseDataset.getRDFLicense(uri);
                if (lic==null)
                    res.append("License not found");
                else
                {
                    RDFLicenseCheck check = new RDFLicenseCheck(lic);
                    if (check.isOpen())
                        res.append("true");
                    else 
                        res.append("false");
                }
            }

            
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return res.toString();
    }

    /**
     * Initalizes the loggers completely muted. Does not need config file.
     */
    public static void initLoggerDisabled() {
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger log : loggers) {
            log.setLevel(Level.OFF);
        }
    }

    /**
     * Initializes the logger It is configured by default so that there is an
     * output in console and in file
     *
     * @param on Indica si se desean logs (true) o no (false)
     * @param console Indica si se desea además una salida por consola o no
     */
    public static void initLogger(boolean on, boolean console) {
        if (!on) {
            logger.setLevel(Level.OFF);
        } else {
            BasicConfigurator.configure();
            PatternLayout layout = new PatternLayout("%d{ABSOLUTE} %5p %C{1}:%L - %m%n");
            FileAppender appender = null;
            try {
                appender = new FileAppender(layout, "licensius-core.log", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.addAppender(appender);

            if (console) {
                ConsoleAppender ca = new ConsoleAppender();
                ca.setWriter(new OutputStreamWriter(System.out));
                ca.setLayout(layout);
                ca.setName("consola");
                logger.addAppender(ca);
            }

            logger.setLevel((Level) Level.DEBUG);
        }
        logger.info("Executed at " + new Date().toString() + " in " + SystemInformation.nombrePC() + " - Starting logger");
        Date d = SystemInformation.getCompileTimeStamp(Licensius.class);
        logger.info("Compiled at: " + d.toString());
        logger.info("=========================================================");
    }

}
