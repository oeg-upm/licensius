package main;

//import com.google.appengine.labs.repackaged.org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;

import oeg.license.LicenseFinder;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;
import org.apache.log4j.Logger;
/**
 *
 * @author Victor
 */
public class ServiceCommons {
    /**
     * Determines whether a HTTP Servlet Request is demanding Turtle
     * @param request HTTP request
     * @retur true if demanding turtle
     */
    public static boolean isJSON(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equals("Accept")) {
                    if (valor.contains("application/json")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
}
