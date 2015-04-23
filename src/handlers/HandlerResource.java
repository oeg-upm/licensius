package handlers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import handlers.HandlerAccount;
import handlers.HandlerOffers;
import handlers.HandlerIndex;
import handlers.HandlerLinkedData;
import handlers.HandlerManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ConditionalDataset;
import model.ConditionalDatasets;
import ldrauthorizer.ws.AuthorizationResponse;
import ldrauthorizer.ws.LDRServer;
import ldrauthorizer.ws.LicensedTriple;
import ldrauthorizer.ws.ODRLAuthorizer;
import ldrauthorizer.ws.Portfolio;
import ldrauthorizerold.GoogleAuthHelper;
import languages.Multilingual;
import ldrauthorizer.ws.CLDHandlerManager;
import ldrauthorizer.ws.Evento;
import ldrauthorizer.ws.WebPolicyManager;
import ldserver.GeneralHandler;
import model.DatasetVoid;
import model.Grafo;
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author vroddon
 */
public class HandlerResource {
    public void serveResource(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String dataset) {
        String recurso = baseRequest.getRootURL().toString() + request.getRequestURI();
        ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
        List<LicensedTriple> llt = cd.getLicensedTriples(recurso);

        Portfolio p = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
        if (p==null) p = new Portfolio();
        request.getSession().setAttribute("portfolio",p);
        llt = LicensedTriple.Authorize2(cd, llt, p);


        String label = cd.getDatasetDump().getFirstObject(recurso, "http://www.w3.org/2000/01/rdf-schema#label");
        String body ="";
        if (!GeneralHandler.isHuman(request)) {
            body = LDRServer.formatTTLTriples(label, llt);
            response.setContentType("text/turtle");
        } else {
            body = formatHTMLTriplesNew(recurso, cd, label, llt);
            response.setContentType("text/html;charset=utf-8");
        }
        try {
            response.getWriter().print(body);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
        }
        baseRequest.setHandled(true);
        Evento.addEvento("Resource " +request.getRequestURI() + " served", 0.0);

    }

    /**
     * Formats in HTML the triples to be shown.
     * @param label
     * @param ls List of licensed triples
     * @param lan Language
     */
    public static String formatHTMLTriplesNew(String recurso, ConditionalDataset cd, String label, List<LicensedTriple> ls) {
        String html = "";
        String tabla = "";

        String templatefile = "htdocs/template_resource.html";
        try {
            html = FileUtils.readFileToString(new File(templatefile));
            String footer = FileUtils.readFileToString(new File("htdocs/footer.html"));
            html = html.replace("<!--TEMPLATEFOOTER-->", footer);
            html = html.replace("<!--TEMPLATEMENU-->", getHTMLMenu(recurso));
        } catch (Exception e) {
            return "page not found " + e.getMessage();
        }

        // html = readTextFile("template.html");
        label = "<h2>" + label + "</h2>";
        html = html.replace("<!--TEMPLATEHERE1-->", label);

        tabla += "<h4>Open triples</h4>\n";
        tabla += "<table><tr><td><strong>Property</strong></td><td><strong>Value</strong></td></tr>\n";
        List<LicensedTriple> open = getOpenTriples(cd, ls);
        Collections.sort(open, LicensedTriple.PREDICATECOMPARATOR);
        for (LicensedTriple lt : open) {
            tabla += lt.toHTMLRowNew(cd) + "\n";
        }
        tabla += "</table>\n";

        tabla += "<p style=\"margin-bottom: 2cm;\"></p>";

        tabla += "<h4>Limited access triples</h4>\n";
        tabla += "<table><tr><td><strong>Property</strong></td><td><strong>Value</strong></td></tr>\n";
        List<LicensedTriple> closed = getClosedTriples(cd, ls);
        for (LicensedTriple lt : closed) {
            tabla += lt.toHTMLRowNew(cd) + "\n";
        }
        tabla += "</table>\n";


        html = html.replace("<!--TEMPLATEHERE2-->", tabla);

        return html;
    }

    public static String getHTMLMenu(String recurso)
    {
        String ruta = "../";
        int index = recurso.indexOf("resource/");
        if (index!=-1)
            ruta = recurso.substring(0, index);

        String html = "<div id=\"nav\" class=\"menu_div\"> <ul>";
        html+= "<li class=\"titulomenu\">What is this?</li>";
        html+= "<li><a href=\"" + ruta + "index.html\">Introduction</a></li>";
        html+= "<li ><a href=\"" + ruta + "infotecnica.html\">Technical details</a></li>";
        html+="<li class=\"titulomenu\">User's view (demo)</li>";
        html+= "<li id=\"active\"><a href=\""+ ruta+ "linkeddata.html\">Browse dataset</a></li>";
        html += "<li><a href=\""+ruta+"offers.html\">Offers</a></li>";
        html += "<li><a href=\""+ruta+"account.html\">Account</a></li>";
        html+= "<li class=\"titulomenu\">Admin's view (demo)</li>";
        html += "<li><a href=\""+ ruta  +"manageren\">Control panel</a></li>";
        html += "<li><a href=\""+ ruta  +"accountability.html\">Accountability</a></li>";
        html+= "</ul></div>";
        return html;
    }

    public static List<LicensedTriple> getClosedTriples(ConditionalDataset cd, List<LicensedTriple> ls) {
        List<Grafo> lg = cd.getGrafos();
        List<LicensedTriple> lop = new ArrayList();
        for (LicensedTriple lt : ls) {
            Grafo g = getGrafo(lg, lt.g);
            if (g==null)     //Si no tiene ningun grafo, ni siquiera se muestra como cerrado
                continue;
            if (lt.hasPolicyAsObject())
                lop.add(lt);
//            if (g.isOpen())
//                continue;
//            List<LicensedTriple> llt = LicensedTriple.concealInformation(lt, g.getPolicies());
 //           lop.addAll(llt);
        }
        return lop;
    }
    
    public static List<LicensedTriple> getOpenTriples(ConditionalDataset cd, List<LicensedTriple> ls) {
        List<Grafo> lg = cd.getGrafos();
        List<LicensedTriple> lop = new ArrayList();
        for (LicensedTriple lt : ls) {
            System.out.println(lt);
            Grafo g = getGrafo(lg, lt.g);
            if (g==null)     //Si no tiene ningun grafo, la politica por defecto es NO mostrar
                continue;

            if (!lt.hasPolicyAsObject())
                lop.add(lt);
//            if (g.isOpen())
//                lop.add(lt);
        }
        return lop;
    }
        public static Grafo getGrafo(List<Grafo> lg, String s)
    {
        for(Grafo g : lg)
            if (g.getURI().equals(s))
                return g;
        return null;
    }
}
