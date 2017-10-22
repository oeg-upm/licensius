package observatory;

import java.util.Date;
import observatorio.utils.MainLicenser;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import vroddon.hilos.Reportador;

/**
 * The main class of the application.
 */
public class ObservatoryApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new ObservatoryView(this));
    }
    
    @Override
    public void shutdown()
    {    
        Logger.getLogger("licenser").info("Finalizado: " + new Date().toString() + " - Cerrando aplicación");
    }
    
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of ObservatoryApp
     */
    public static ObservatoryApp getApplication() {
        return Application.getInstance(ObservatoryApp.class);
    }


    /**
     * Obtiene el Reportador.
     * Los algoritmos podrán informar de su estado, errores, etc. usando los métodos de reportador
     * Cuando sea posible se mostrarán con ventanitas, y si no por pantalla, pero en cualquier caso no petará.
     * @return Reportador
     */
    public static Reportador getReportador()
    {
        try{Reportador r = (ObservatoryView)getApplication().getMainView();return r;}
        catch(Exception e) {Logger.getLogger("licenser").warn("Sin reportador");return new ReportadorNull();}
    }    
    
    
    /**
     * Main method launching the application.
     * -i being -f
     */
    public static void main(String[] args) {
        //SI HAY ARGUMENTOS, NO EJECUTAR LA INTERFAZ GRÁFICA
        MainLicenser.initLogger();
        if (args.length!=0)
        {
            MainLicenser m = new MainLicenser();
            m.Parser(args); 
            return;
        }
        if (!Observatory.gui)
        {
            System.out.println("Please execute java -jar dist/Licenser.jar -gui");
            return;
        }        
        launch(ObservatoryApp.class, args);
    }
    /*
    public static void initLogger() {
        try {
            PatternLayout layout = new PatternLayout("%d{ABSOLUTE} %5p %C{1}:%L - %m%n");
            ConsoleAppender console = new ConsoleAppender();
            console.setName("console");
            console.setTarget("System.out");
            console.setLayout(layout);
            console.setThreshold(Level.DEBUG); //Level.DEBUG
            console.activateOptions();
            Logger.getRootLogger().addAppender(console);

//                FileAppender file = new FileAppender(layout, "inspecteelogs.txt");
            RollingFileAppender file;
            file = new RollingFileAppender(layout, "logs.txt");
            file.setMaxFileSize("256MB");
            file.setName("file");
            file.setLayout(layout);
            file.setThreshold(Level.DEBUG);
            //   file.setFile("inspecteelogsx.txt", true, false, 1024);
            file.activateOptions();
            Logger.getRootLogger().addAppender(file);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }    */
    
}
