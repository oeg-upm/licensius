package ldc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import ldc.auth.SQLite;

import org.apache.log4j.BasicConfigurator;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


/**
 * Main entry point to manage the web application. 
 * If this class is executed, a Jetty server will run.
 * If this class is placed in a Tomcat, it will run therem
 * The war file is expected to be deployed as ldc.war 
 * 
 * TO LAUNCH FUSEKI:
 * - go to lider2, opt/fuseki
 * - execute sudo nohup java -jar ./fuseki-server.jar --update --loc=data --port 3031 /tbx&
 * @author admin
 */
public class Main {
    
    static final Logger logger = Logger.getLogger("ldc");

    public static void main(String[] argx) throws IOException {
//        String[] args = {"-count","foaf:Agent"};
//        String[] args = {"-dump"};
        standardInit();
        
        String[] args = {"-jetty"};
        BasicConfigurator.configure();
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(new NullAppender());
        StringBuilder sb = new StringBuilder();
        CommandLineParser clparser = null;
        CommandLine cl = null;
        try {
            Options options = new Options();
            options.addOption("countall", false, "Counts the number of class individuals");
            options.addOption("count", true, "Counts the number of class individuals of a given class");
            options.addOption("dump", false, "Dumps all the RDF in the server");
            options.addOption("clear", false, "Deletes everything");
            options.addOption("jetty", false, "Starts the application in a Jetty server");
            options.addOption("help", false, "shows this help (Help)");
            clparser = new BasicParser();
            cl = clparser.parse(options, args);
            if (cl.hasOption("help")) {
                System.out.println("tbx2rdfservice command line tool");
                new HelpFormatter().printHelp(Main.class.getCanonicalName(), options);
            }
            if (cl.hasOption("clear")) {
                System.out.println("ok");
            }
            if (cl.hasOption("dump")) {
                System.out.println("ok");
            }
            if (cl.hasOption("count")) {
                System.out.println("ok");
            }
            if (cl.hasOption("countall")) {
                System.out.println("ok");
            }
            if (cl.hasOption("jetty")) {
                startJetty();
            }
        } catch (Exception e) {
            System.err.println("error " + e.getMessage());
        }
    }    

    public static void standardInit()
    {
        initLogs(false);
        Ldc.loadDatasets();
        SQLite.createTables();
    }
    

    
    
    /***************************************************************************/
    private static void initLogs(boolean verbose)
    {
        //FIRST WE DISABLE THE HORRIBLE LOGS. WTF IS SLF4J? THIS IS THE WORST EVER DESIGN COMPONENT.
        PrintStream filterOut = new PrintStream(System.err) {
            public void println(String l) {
                if (! l.startsWith("SLF4J") && ! l.startsWith("log4j:"))
                    super.println(l);
            }
        };
        if (!verbose)
            System.setErr(filterOut);      
        
        //THEN WE ADD OUR OWN APPENDER
        startLogger();
        Date d = Main.getCompileTimeStamp(Main.class);
        logger.info("===============================================");
        logger.info("LDC Started. Version: " + d.toString() );
    }
    
    /**
     * Starts the logger
     */
    public static void startLogger()
    {
        try {
            List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
            loggers.add(LogManager.getRootLogger());
            for (Logger logger : loggers) {
                logger.setLevel(Level.OFF);
            }
            URL bundledProperties = Main.class.getResource("log4j.properties"); // este es el local, junto al código fuente
            PropertyConfigurator.configure(bundledProperties);
            PatternLayout layout = new PatternLayout("%d{ABSOLUTE} %5p %C{1}:%L - %m%n");
            FileAppender appender = null;
            try {
                String sfile=LdcConfig.getDataFolder()+"/logs.txt";
                appender = new FileAppender(layout, sfile, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logger.getLogger("ldc").addAppender(appender);
            Logger.getLogger("ldc").setLevel((Level) Level.DEBUG);
        } catch (Exception e) {
            e.printStackTrace();
        } //bueno, si no está no pasa nada
    }
            
            
    
    /**
     * Arranca una instancia de jetty
     */
    private static void startJetty() throws Exception
    {
        String jetty_home = System.getProperty("jetty.home",".");
        
        String war = jetty_home+"/dist/ldc.war";
        File f = new File(war);
        if (!f.exists())
            logger.error("Server not found");
        String sport = LdcConfig.getPort();
        Server server = new Server(Integer.parseInt(sport));
        WebAppContext webapp = new WebAppContext();
        String context = LdcConfig.get("context", "/ldc");
        webapp.setContextPath(context);
        webapp.setWar(war);
        server.setHandler(webapp);
        logger.info("Server started in port " + sport +" with context " + context);
        server.start();
        server.join();
    }
    
    /**
     * Get date a class was compiled by looking at the corresponding class file in the jar.
     * @author Zig http://mindprod.com/jgloss/compiletimestamp.html
     */
    private static Date getCompileTimeStamp(Class<?> cls) {
        ClassLoader loader = cls.getClassLoader();
        String filename = cls.getName().replace('.', '/') + ".class";
        // get the corresponding class file as a Resource.
        URL resource = (loader != null)
                ? loader.getResource(filename)
                : ClassLoader.getSystemResource(filename);
        try {
            URLConnection connection = resource.openConnection();
            long time = connection.getLastModified();
            return (time != 0L) ? new Date(time) : null;
        } catch (Exception e) {
            return null;
        }
    }    
}
