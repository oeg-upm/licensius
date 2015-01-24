package ldrauthorizerold;

//LDRAUTHORIZER
import ldrauthorizer.ws.CLDHandlerDefault;
import ldrauthorizer.ws.JettyServer;

//APACHE LOG4J
import ldconditional.LDRConfig;
import org.apache.log4j.Logger;

//JETTY
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;


/**
 * Core class to start the server
 * @author Victor
 */
public class LDRCore {
    
    JettyServer server = null;
    
    /**
     * Starts the server
     * http://stackoverflow.com/questions/19718159/java-lang-illegalstateexception-no-sessionmanager
     * https://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty
     */
    public void StartServer() {
        try {
            int puerto = Integer.parseInt(LDRConfig.getPort());
            server = new JettyServer(puerto);
            HandlerCollection handlers = new HandlerCollection();
            HashSessionManager manager = new HashSessionManager();
            SessionHandler sessions = new SessionHandler(manager);
            HashSessionIdManager idmanager = new HashSessionIdManager();
            server.server.setSessionIdManager(idmanager);
            CLDHandlerDefault servletHandler = new CLDHandlerDefault();
            ContextHandler contextGeneral = new ContextHandler("/");
            contextGeneral.setHandler(sessions);
            sessions.setHandler(servletHandler);
            handlers.addHandler(contextGeneral);
            server.setHandler(handlers);
            server.start();
        } catch (Exception e) {
            Logger.getLogger("ldr").info("Excepción al arrancar el servidor: " + e.getMessage());
        }
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