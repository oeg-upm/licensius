package ldrauthorizerold;

import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import ldrauthorizer.ws.JettyServer;
import ldconditional.LDRConfig;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class LDRAuthorizerApp extends SingleFrameApplication {

    //Determina si hay o no interfaz gráfico
    public static boolean gui = false;
    
    public static LDRCore core = new LDRCore();
    
    
    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        initLogger();
        LDRConfig.Load();
        
        if (gui)
            show(new LDRAuthorizerView(this));
        else
            core.StartServer();
    }

    @Override
    public void shutdown() {
        LDRCore core=LDRAuthorizerApp.getApplication().core;
        if (core.server.isStarted()) {
            try {
                core.server.stop();
            } catch (Exception ex) {
                Logger.getLogger("ldr").info("Server could not be stopped on exit");
            }
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of LDRAuthorizerApp
     */
    public static LDRAuthorizerApp getApplication() {
        return Application.getInstance(LDRAuthorizerApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        if (args.length>0)
        {
            if (args[0].equals("-gui"))
                gui=true;
        }
            launch(LDRAuthorizerApp.class, args);
    }
    
    
    /**
     * Inicializa el logger
     */
    public static void initLogger() {

        try{
            //Primero apagamos todos los logs que no sean los nuestros, si es que los hay
            List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
            loggers.add(LogManager.getRootLogger());
            for ( Logger logger : loggers ) 
                logger.setLevel(Level.OFF);
//          PropertyConfigurator.configure("log4j.properties");                         // este es el que está en la carpeta
            URL bundledProperties = LDRAuthorizerApp.class.getResource("log4j.properties"); // este es el local, junto al código fuente
            PropertyConfigurator.configure(bundledProperties);
        }catch(Exception e){e.printStackTrace();} //bueno, si no está no pasa nada
        
        //LOGGER
        Logger.getLogger("ldr").setLevel((Level) Level.DEBUG);
        Logger.getLogger("ldr").info("Ejecutado: " + new Date().toString() + " - Iniciando logger");
        Logger.getLogger("ldr").info("=========================================================");
    }
            
}
