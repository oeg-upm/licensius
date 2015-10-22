package oeg.license;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import main.Licensius;
import org.apache.log4j.Logger;

/**
 *http://datos.bne.es/resource/XX947766
 * @author Victor
 */
public class LicenseTest {
    private static final Logger logger = Logger.getLogger(LicenseTest.class.getName());
    
    public static void main(String[] args) {

        Licensius.initLogger();
        logger.debug("Starting license test");
        test();
        
    }
    
    /**
     * http://purl.org/wf4ever/roevo
     */
    public static void test()
    {
        LicenseFinder lf = new LicenseFinder();
        String s = lf.findLicense("http://datos.bne.es/resource/XX947766");
        System.out.println(s);
        
    }
    
    public static void testPost() {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("C:\\Desktop\\lemonTranslation2.owl") );
            String s = new String(encoded, StandardCharsets.UTF_8);  //StandardCharsets.UTF_8
//            s = URLEncoder.encode(s, "UTF-8");

            logger.info("Size: " + s.length());
            LicenseFinder lf = new LicenseFinder();
//          String resultado = lf.findLicense("C:///desktop///lemonTranslation.owl");
            String resultado = lf.findLicenseFromText(s);
            System.out.println(resultado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
}
