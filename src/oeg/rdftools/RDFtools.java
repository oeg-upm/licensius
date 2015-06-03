package oeg.rdftools;

//APACHE CLI
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * Entry point
 * @author vroddon
 */
public class RDFtools {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initLogger();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        parsear(args);
//        new Validator().validate("C:\\desktop\\clarin.ttl");
//        new Transcode().toRDFXML("C:\\desktop\\clarin.ttl");
    }

    public static String parsear(String[] args) {
        try {
            Options options = new Options();
            options.addOption("help", false, "Shows help (Help)");
            options.addOption("validate", true, "Validates an RDF file");
            options.addOption("ttl", true, "Converts to TURTLE");
            options.addOption("rdfxml", true, "Converts to RDF/XML");
            options.addOption("ntriples", true, "Converts to NTRIPLES");
            options.addOption("nquads", true, "Converts to NQUADS");
            options.addOption("hdt", true, "Converts to HDT");
            CommandLineParser parser = new BasicParser();
            CommandLine cli = parser.parse(options, args);
            if (cli.hasOption("help")) {
                new HelpFormatter().printHelp(RDFtools.class.getCanonicalName(), options);
            }
            if (cli.hasOption("validate")) {
                String file = cli.getOptionValue("validate");
                Validator v = new Validator();
                String s = v.validate(file);
                JOptionPane.showMessageDialog(null, s, "RDFTools", JOptionPane.INFORMATION_MESSAGE);
            }
            else if(cli.hasOption("ttl")) {
                String file = cli.getOptionValue("ttl");
                Transcoder t = new Transcoder();
                String s = t.toTTL(file);
                JOptionPane.showMessageDialog(null, s, "RDFTools", JOptionPane.INFORMATION_MESSAGE);
            }
            else if(cli.hasOption("rdfxml")) {
                String file = cli.getOptionValue("rdfxml");
                Transcoder t = new Transcoder();
                String s = t.toRDFXML(file);
                JOptionPane.showMessageDialog(null, s, "RDFTools", JOptionPane.INFORMATION_MESSAGE);
            }            
            else if(cli.hasOption("ntriples")) {
                String file = cli.getOptionValue("ntriples");
                Transcoder t = new Transcoder();
                String s = t.toNTriples(file);
                JOptionPane.showMessageDialog(null, s, "RDFTools", JOptionPane.INFORMATION_MESSAGE);
            }            
            else if(cli.hasOption("nquads")) {
                String file = cli.getOptionValue("nquads");
                Transcoder t = new Transcoder();
                String s = t.toNQuads(file, "default");
                JOptionPane.showMessageDialog(null, s, "RDFTools", JOptionPane.INFORMATION_MESSAGE);
            }            
            else if(cli.hasOption("hdt")) {
                String file = cli.getOptionValue("hdt");
                Transcoder t = new Transcoder();
                String s = t.toHDT(file);
                JOptionPane.showMessageDialog(null, s, "RDFTools", JOptionPane.INFORMATION_MESSAGE);
            }            
            else {
                JOptionPane.showMessageDialog(null, "This is an auxiliary file, not intended to be run\n@vroddon, (C) Ontology Engineering Group 2015", "RDFTools", JOptionPane.INFORMATION_MESSAGE);
                new HelpFormatter().printHelp(RDFtools.class.getCanonicalName(), options);
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * Initializes the logger with some predefined parameters
     */
    public static void initLogger() {
        try {
            PatternLayout layout = new PatternLayout("%d{ABSOLUTE} %5p %C{1}:%L - %m%n");
            ConsoleAppender console = new ConsoleAppender();
            console.setName("console");
            console.setTarget("System.out");
            console.setLayout(layout);
            console.setThreshold(Level.INFO); //Level.DEBUG
            console.activateOptions();
            Logger.getRootLogger().addAppender(console);
            RollingFileAppender file;
            file = new RollingFileAppender(layout, "./logs.txt");
            file.setMaxFileSize("1MB");
            file.setName("file");
            file.setLayout(layout);
            file.setThreshold(Level.DEBUG);
            file.activateOptions();
            Logger.getRootLogger().addAppender(file);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
