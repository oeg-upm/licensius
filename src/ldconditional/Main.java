package ldconditional;

import ldconditional.model.ConditionalDatasets;
import ldconditional.handlers.GeneralHandler;
import ldconditional.handlers.ServiceHandler;
import ldconditional.handlers.MainHandler;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;

/**
 * Main class, entry point of the Linked Data Web Server.
 * APIs are documented making use of apidoc http://apidocjs.com/
 *  Most requests are processed by the GeneralHandler class
 * MUY INTERESANTE:
 * https://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty#Setting_a_Web_Application_Context
 * @author Victor
 */
public class Main {

    static final Logger logger = Logger.getLogger(Main.class);
    public static Server server = null;

    public static void main(String[] args) throws Exception {

        standardInit();
        initServer();
    }

    public static void standardInit()
    {
        initLogger();

        LDRConfig.Load();

        ConditionalDatasets.loadDatasets();

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
            file = new RollingFileAppender(layout, "./logs/logs.txt");
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
    
    /**
     * Initializes the server and enters in wait state
     */
    public static void initServer() throws Exception
    {
        int port = Integer.parseInt(LDRConfig.getPort());
        logger.info("Starting the server in " + port);
        int puerto = Integer.parseInt(LDRConfig.getPort());
        server = new Server(puerto);
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        HandlerCollection handlers = new HandlerCollection();
        HashSessionManager manager = new HashSessionManager();
        SessionHandler sessions = new SessionHandler(manager);
        HashSessionIdManager idmanager = new HashSessionIdManager();
        server.setSessionIdManager(idmanager);
        ServiceHandler serviceHandler = new ServiceHandler();
        GeneralHandler generalHandler = new GeneralHandler();
        MainHandler mainHandler = new MainHandler();
        ContextHandler contextGeneral = new ContextHandler("/");
        contextGeneral.setHandler(sessions);
        sessions.setHandler(mainHandler);
        handlers.addHandler(requestLogHandler);
        handlers.addHandler(contextGeneral);
        handlers.addHandler(serviceHandler);
        handlers.addHandler(generalHandler);
        NCSARequestLog requestLog = new NCSARequestLog("./logs/jetty-yyyy_mm_dd.request.log");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLogHandler.setRequestLog(requestLog);
        server.setHandler(handlers);
        server.start();
        server.join();        
    }


    
}


/**

 http://es.dbpedia.org/sparql

SELECT *  WHERE {
      ?uri geo:lat ?lat .
      ?uri geo:long ?lon .
      ?uri rdf:type ?thetype .
      FILTER ( regex(?thetype,'^http://schema.org'))

}
http://oeg-dev.dia.fi.upm.es/nor2o/
 *
 *
 *
 */