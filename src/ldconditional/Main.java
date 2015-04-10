package ldconditional;

import ldserver.GeneralHandler;
import ldserver.ServiceHandler;
import ldserver.MainHandler;
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
 * This project is documented making use of 
 *  
 * @author Victor
 */
public class Main {

    static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        initLogger();
        
        initDatasets();
        
        initServer();

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
<<<<<<< HEAD
     * Initializes the server and enters in wait state
=======
     * Initializes the web server
>>>>>>> 0981c2eee2c2f70aba2ba64e7747551c51447e8a
     */
    public static void initServer() throws Exception
    {
        int port = Integer.parseInt(LDRConfig.getPort());
        logger.info("Starting the server in " + port);
        int puerto = Integer.parseInt(LDRConfig.getPort());
        Server server = new Server(puerto);
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
    
    
    public static void initDatasets()
    {
        ConditionalDatasets.loadDatasets();
    }
    
}
