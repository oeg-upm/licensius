package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.licensius.core.LicenseFinder;
import oeg.licensius.core.Licensius;
import oeg.licensius.model.LicensiusResponse;

/** 
 * En Abril del 2017 me ha devuelto esto: 4/6f6seGcz5gvOtfxl4rwiUGot--86u4KQTYJwrinqFnU en salonica
 * 
 * 
 * Test service to be docummented by swagger, deployed in Tomcat and running at www.licensius.com
 * You can try it here: http://www.licensius.com/api/test
 * Buen puntero: http://www.jqueryscript.net/
 * @author vrodriguez
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
            w.println("Thanks for testing our licensius 2 " + req.getRequestURL());
            return;
        }

    public static void main(String[] args) {
        Licensius.init();
        String uri = "http://purl.org/wf4ever/ro";
        LicenseFinder lf = new LicenseFinder();
        LicensiusResponse le = lf.findLicenseInRDF(uri);
        System.out.println(le.getJSON());

    }

    
}