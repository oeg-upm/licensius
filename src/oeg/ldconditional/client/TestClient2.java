package oeg.ldconditional.client;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.net.URI;
import ldconditional.Main;
import org.apache.log4j.Logger;

/**
 *
 * @author vroddon
 */
public class TestClient2 {

    static final Logger logger = Logger.getLogger(TestClient2.class);

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
     //   browse("http://localhost/geo/service/getOffers");
        browse("http://localhost/geo/service/getResources?page=1&size=100");
     //   browse("http://localhost/geo/service/getResource?uri=http://geo/Almeria");
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
