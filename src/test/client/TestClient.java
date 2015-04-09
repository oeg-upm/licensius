package test.client;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.net.URI;
import ldconditional.Main;
import org.apache.log4j.Logger;

/**
 *
 * @author vroddon
 */
public class TestClient {

    static final Logger logger = Logger.getLogger(TestClient.class);

    public static void main(String[] args) throws Exception {
//        test0();
        test1();
    }
    
    

    /**
     * Launches the server and opens the main webpag
     */
    public static void test0() {
        browse("http://localhost:80");

    }
    
    public static void test1() {
        browse("http://localhost:80/geo/service/getOffers");
    }
    
    public static void browse(String uri)
    {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(uri));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
    }
    
    

}
