package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.licensius.core.LicenseFinder;
import oeg.licensius.core.Licensius;
import oeg.licensius.model.LicensiusResponse;

/** 
 * Test service to be docummented by swagger, deployed in Tomcat and running at www.licensius.com.
 * You can try it here: http://www.licensius.com/api/test
 * Using some good resources from: http://www.jqueryscript.net/
 * @author vroddon
 */
public class Test extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(Test.class.getName());

    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(200);
            resp.setContentType("text/html");
            PrintWriter w = resp.getWriter();
//            w.println("Thanks for testing our licensius 2 (build 106 "+DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) +" ) " + req.getRequestURL());
            w.println("Thanks for testing our licensius 2 (build 108) " + req.getRequestURL());
            return;
        }

    /**
     * You may want to run this main as to verify that the service is working and fine.
     */
    public static void main(String[] args) {
        Licensius.init();
         
        String uri = "https://spec.edmcouncil.org/fibo/ontology/master/latest/BE/Corporations/Corporations/"; //https here
//        String uri = "http://purl.org/wf4ever/ro"; // No licenses here
        LicenseFinder lf = new LicenseFinder();
        LicensiusResponse le = lf.findLicenseInRDF(uri);
        System.out.println(le.getJSON());
    }

    
}
