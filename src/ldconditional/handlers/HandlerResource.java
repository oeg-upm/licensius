package ldconditional.handlers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.LDRConfig;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import ldconditional.auth.LicensedTriple;
import ldconditional.auth.Portfolio;
import ldconditional.auth.GoogleAuthHelper;
import ldconditional.auth.Evento;
import static ldconditional.handlers.HandlerIndex.getImage64;
import ldconditional.model.Grafo;
import odrlmodel.rdf.RDFUtils;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.eclipse.jetty.server.Request;

/**
 *
 * @author vroddon
 */
public class HandlerResource {

    public void serveResource(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String dataset) {
        String recurso = baseRequest.getRootURL().toString() + request.getRequestURI();
        ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
        ConditionalDatasets.setSelectedDataset(cd.name);
        List<LicensedTriple> llt = cd.getDatasetDump().getLicensedTriples(recurso);

        Portfolio p = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
        if (p == null) {
            p = new Portfolio();
        }
        request.getSession().setAttribute("portfolio", p);
        llt = LicensedTriple.Authorize2(cd, llt, p);

        String label = cd.getDatasetDump().getFirstObject(recurso, "http://www.w3.org/2000/01/rdf-schema#label");
        String body = "";
        if (!GeneralHandler.isHuman(request)) {

            if (GeneralHandler.isRDFXML(request)) {
                body = formatRDFXMLTriples(llt);
                response.setContentType("application/rdf+xml");
            } else {
                body = formatTTLTriples(llt);
                response.setContentType("text/turtle");
            }
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
        Evento.addEvento("Resource " + request.getRequestURI() + " served", 0.0);

    }

    public static String formatTTLTriples(List<LicensedTriple> lst) {
        String s = "";
        Model model = ModelFactory.createDefaultModel();
        RDFUtils.addPrefixesToModel(model);
        for (LicensedTriple lt : lst) {
            model.add(lt.stmt);
        }
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, Lang.TTL);
        s = sw.toString();
        return s;
    }

    public static String formatRDFXMLTriples(List<LicensedTriple> lst) {
        String s = "";
        Model model = ModelFactory.createDefaultModel();
        for (LicensedTriple lt : lst) {
            model.add(lt.stmt);
            System.out.println(lt.stmt.toString());
        }
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, Lang.RDFXML);
        s = sw.toString();
        System.out.println(s);
        return s;
    }

    /**
     * http://datos.gob.es/recurso/Provincia/Santander
     */
    public static String getJumbotron(ConditionalDataset cd) {
        String titulo = cd.name;
        String subtitulo = cd.getDatasetVoid().getTitle();
        String jumbo = " <div class=\"jumbotron\">\n"
                + "	<center><h1>" + titulo + "</h1> \n"
                + "    <p>" + subtitulo + "\n";
        try{
                String sfolder = LDRConfig.get("datasetsfolder", "datasets");
                if (!sfolder.endsWith("/")) sfolder+="/";
                String filename = sfolder + cd.name + "/logo.png";
                BufferedImage bi = ImageIO.read(new File(filename));
                String churro = getImage64(bi);
                String img = "<img style=\"vertical-align:middle;width:100px;float:right;margin:8px;\" src=\"data:image/png;base64," + churro + "\">";
                jumbo+=img;
        }catch(Exception e){}
        
        jumbo+="</p></center></div>";
        
        return jumbo;
    }

    /**
     * Formats in HTML the triples to be shown.
     *
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
            html = html.replace("<!--TEMPLATEFOOTER-->",    FileUtils.readFileToString(new File("htdocs/footer.html")));
            html = html.replace("<!--TEMPLATEMENU-->",      getHTMLMenu(recurso));
            html = html.replace("<!--TEMPLATEJUMBOTRON-->", getJumbotron(cd));
        } catch (Exception e) {
            return "Internal component is missing " + e.getMessage();
        }

        if (label.isEmpty()) {
            label = recurso;
        }
        
        try{
        label = URLDecoder.decode(label, "UTF-8");
        }catch(Exception e){}

        if (label.length() < 10) {
            label = "<h2>" + label + "</h2>";
        } else {
            label = "<h4>" + label + "</h4>";
        }
        html = html.replace("<!--TEMPLATERESOURCETITLE-->", label);

//        tabla += "<h3>Open triples</h3>\n";
        tabla += "<table class=\"table table-condensed\">"; //table-striped 
        tabla += "<thead><tr><td width=\"30%\"><strong>Property</strong></td><td width=\"70%\"><strong>Value</strong></td></tr></thead>\n";
        List<LicensedTriple> open = getOpenTriples(cd, ls);
        Collections.sort(open, LicensedTriple.PREDICATECOMPARATOR);
        for (LicensedTriple lt : open) {
            tabla += lt.toHTMLRowNew(cd) + "\n";
        }

        List<LicensedTriple> closed = getClosedTriples(cd, ls);
        for (LicensedTriple lt : closed) {
            tabla += lt.toHTMLRowNew(cd) + "\n";
        }

        tabla += "</table>\n";

        /*        tabla += "<p style=\"margin-bottom: 2cm;\"></p>";

         tabla += "<h3>Limited access triples</h3>\n";
         tabla += "<table class=\"table table-striped table-condensed\">";
         tabla += "<thead><tr><td width=\"50%\"><strong>Property</strong></td><td width=\"50%\"><strong>Value</strong></td></tr></thead>\n";
         List<LicensedTriple> closed = getClosedTriples(cd, ls);
         for (LicensedTriple lt : closed) {
         tabla += lt.toHTMLRowNew(cd) + "\n";
         }
         */
        tabla += "</table>\n";

        String id = "verrdf";
        //   tabla+="<button type=\"button\" class=\"btn btn-primary btn-lg\" data-toggle=\"modal\" data-target=\"#"+id +"\">RDF</button>";
        tabla += "<div class=\"pull-right\"><a href=\"#\" data-toggle=\"modal\" data-target=\"#" + id + "\"><img src=\"/img/rdf24.png\"/></a></div>";

        String ttl = formatTTLTriples(ls);
        ttl = ttl.replaceAll("<", "&lt;");
        ttl = ttl.replaceAll(">", "&gt;");
        ttl = "<div class=\"panel panel-default\"><pre>" + ttl + "</pre></div>";
        tabla += getHTMLModal(id, "RDF", ttl);

        html = html.replace("<!--TEMPLATETABLERESOURCES-->", tabla);

        return html;
    }

    public static String getHTMLModal(String id, String titulo, String contenido) {

        String s = "";
        s += "<div class=\"modal fade\" id=\"" + id + "\">";
        s += "<div class=\"modal-dialog modal-lg\">";
        s += "<div class=\"modal-content\">";
        s += "<div class=\"modal-header\">";
        s += " <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>";
        s += "<h4 class=\"modal-title\">" + titulo + "</h4>";
        s += "</div><div class=\"modal-body\">";
        s += contenido;
        s += "</div>";
        s += "<div class=\"modal-footer\">";
//        s += "<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>";
        s += "<button type=\"button\" data-dismiss=\"modal\" class=\"btn btn-primary\">OK</button>";
        s += "</div></div></div></div>";

        return s;
    }

    /**
     * Obtains the HTML menu for the
     */
    public static String getHTMLMenu(String recurso) {
        String ruta = "../";
        int index = recurso.indexOf("resource/");
        if (index != -1) {
            ruta = recurso.substring(0, index);
        }
        String html = "<div id=\"nav\" class=\"menu_div\"> <ul>";
        html += "<li class=\"titulomenu\">What is this?</li>";
        html += "<li><a href=\"" + ruta + "index.html\">Introduction</a></li>";
        html += "<li ><a href=\"" + ruta + "develop.html\">Technical details</a></li>";
        html += "<li class=\"titulomenu\">User's view (demo)</li>";
        html += "<li id=\"active\"><a href=\"" + ruta + "linkeddata.html\">Browse dataset</a></li>";
        html += "<li><a href=\"" + ruta + "offers.html\">Offers</a></li>";
        html += "<li><a href=\"" + ruta + "account.html\">Account</a></li>";
        html += "<li class=\"titulomenu\">Admin's view (demo)</li>";
        html += "<li><a href=\"" + ruta + "manageren\">Control panel</a></li>";
        html += "<li><a href=\"" + ruta + "accountability.html\">Accountability</a></li>";
        html += "</ul></div>";
        return html;
    }

    public static List<LicensedTriple> getClosedTriples(ConditionalDataset cd, List<LicensedTriple> ls) {
        List<Grafo> lg = cd.getGrafos();
        List<LicensedTriple> lop = new ArrayList();
        for (LicensedTriple lt : ls) {
            Grafo g = getGrafo(lg, lt.g);
            if (g == null) //Si no tiene ningun grafo, ni siquiera se muestra como cerrado
            {
                continue;
            }
            if (lt.hasPolicyAsObject()) {
                lop.add(lt);
            }

        }
        return lop;
    }

    public static List<LicensedTriple> getOpenTriples(ConditionalDataset cd, List<LicensedTriple> ls) {
        List<Grafo> lg = cd.getGrafos();
        List<LicensedTriple> lop = new ArrayList();
        for (LicensedTriple lt : ls) {
            System.out.println(lt);
            Grafo g = getGrafo(lg, lt.g);
            if (g == null) 
            {
                lop.add(lt); //Si no tiene ningun grafo, la politica por defecto es mostrar. borrar esto para NO mostrar
                continue;
            }

            if (!lt.hasPolicyAsObject()) {
                lop.add(lt);
            }
//            if (g.isOpen())
//                lop.add(lt);
        }
        return lop;
    }

    public static Grafo getGrafo(List<Grafo> lg, String s) {
        for (Grafo g : lg) {
            if (g.getURI().equals(s)) {
                return g;
            }
        }
        return null;
    }
}
