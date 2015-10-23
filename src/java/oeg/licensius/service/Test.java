package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.license.LicenseFinder;
import oeg.license.LicenseFinderWeb;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;

/**
 * Test service to be docummented by swagger, deployed in Tomcat and running at www.licensius.com
 * @author vrodriguez
 */
public class Test extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(Test.class.getName());

    /**
     * 
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(200);
            resp.setContentType("text/html");
            PrintWriter w = resp.getWriter();
            w.println("ok");
            return;
        }

    
}
