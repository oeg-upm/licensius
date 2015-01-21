package ldconditional;

import java.io.IOException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * Main class, entry point of the Web Server
 * 
 * @author Victor
 */
public class Main {

    static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        initLogger();
        logger.info("Starting the application");
    }

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
    }
}
