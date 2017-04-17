package oeg.rdflicense.servlet;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

/**
 * Different methods useful for handling servlets
 * @author vrodriguez
 */
public class ServletUtils {
    
    /**
     * Determines whether a HTTP Servlet Request is demanding Turtle
     *
     * @param request HTTP request
     * @retur true if demanding turtle
     */
    public static boolean isTurtle(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.endsWith(".ttl")) {
            return true;
        }
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equals("Accept")) {
                    if (valor.contains("text/turtle") || valor.contains("application/x-turtle")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines whether a HTTP Servlet Request is demanding RDF/XML
     *
     * @param request HTTP request
     * @return true if demanding RDF/XML
     */
    public static boolean isRDFXML(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.endsWith(".rdf")) {
            return true;
        }
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equals("Accept")) {
                    if (valor.contains("application/rdf+xml")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
