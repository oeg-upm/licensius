package ldc.servlets;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

//JAVA
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//LDC
import ldc.LdcConfig;
import ldc.Main;
import ldc.Ldc;
import ldc.auth.GoogleAuthHelper;
import ldc.auth.LicensedTriple;
import ldc.auth.Portfolio;
import ldc.model.ConditionalDataset;
import ldc.model.Grafo;
import oeg.utils.rdf.RDFUtils;

//APACHE
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Servlet in charge of serving Linked Data
 * @author vroddon
 */
public class DataServlet extends HttpServlet {
 
    static final Logger logger = Logger.getLogger("ldc");

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Enumeration appenders = logger.getAllAppenders();
        if (appenders==null || !appenders.hasMoreElements())
            Main.startLogger();
       
        String peticion = request.getRequestURI();
        logger.info("Data servlet: " + peticion);
        request.getSession().setAttribute("lasturi", peticion);
        String google = (String) request.getSession().getAttribute("google");
        logger.info("User: " + google);
        StringTokenizer tokens = new StringTokenizer(peticion, "/");
        List<String> partes = new ArrayList();
        while (tokens.hasMoreTokens()) {
            partes.add(tokens.nextToken());
        }
        if (partes.size() < 2) {
            Api.serveError(request, response);
            return;
        }
        String last = partes.get(partes.size() - 1);
        String prelast = partes.get(partes.size() - 2);
        if (prelast.equals("data")) {
            logger.info("Listing Resources1");
            listResources(request, response, last);
            return;
        }
        if (peticion.endsWith("/data/")) {
            logger.info("Listing Resources2");
            listResources(request, response);
            return;
        }
        
        String server = LdcConfig.get("server", "http://localhost:8080");
        String context = LdcConfig.get("context","/ldc");
        String base = server + context + "/data/";
        int inidataset = peticion.lastIndexOf("data/")+5;
        int findataset = peticion.indexOf("/", inidataset);
        if (findataset==-1)
            findataset=peticion.length();
        String recurso = server + peticion;
        String dataset = peticion.substring(inidataset,findataset);
        recurso = recurso.replace("(", "%28");
        recurso = recurso.replace(")", "%29");       
        logger.info("Se cree que el dataset es " + dataset);
        logger.info("Se cree que el recurso es " + recurso);
        logger.info("Se cree que el usuario es " + google);
        
        ConditionalDataset cd = Ldc.getDataset(dataset);
        Ldc.setSelectedDataset(cd.name);
        logger.info("Fetching triples for " + recurso);
        List<LicensedTriple> llt = cd.getLicensedTriples(recurso);
        logger.info("Hay "+llt.size()+" triples para ese sujeto");
        Model minimodel = LicensedTriple.getModel(llt);        
        String mail = GoogleAuthHelper.getMail(request);
        Portfolio p = Portfolio.getPortfolio(mail);
        if (p == null) {
            p = new Portfolio();
        }
        request.getSession().setAttribute("portfolio", p);
        llt = LicensedTriple.Authorize2(cd, llt, p);
        String label = cd.getDatasetDump().getFirstObject(minimodel, recurso, "http://www.w3.org/2000/01/rdf-schema#label");
        String bodyr = "";
        String ntriples = LicensedTriple.getNTriples(llt);
        
        if(RDFUtils.isHuman(request))
        {
            bodyr = DataServlet.getTablaHTML(cd, llt);
            InputStream input = getClass().getResourceAsStream("template_ld.html");
            BufferedInputStream bis = new BufferedInputStream(input);
            String body = IOUtils.toString(bis, "UTF-8");
            body = body.replace("TEMPLATE_CONTEXTO", context);
            body = body.replace("<!--TEMPLATE_CONTENT-->", bodyr);
            
            Resource entidad = ModelFactory.createDefaultModel().createResource(recurso);
            String titulo = entidad.toString().substring(entidad.toString().lastIndexOf("/") + 1, entidad.toString().length());
            titulo = URLDecoder.decode(titulo, "UTF-8");
            body = body.replace("<!--TEMPLATE_TITLE-->", "\n" + titulo);
            response.getWriter().println(body);
            response.setContentType("text/html;charset=utf-8");
            return;
        }
        if (isRDFTTL(request)) {
            System.out.println("Serving TTL for " + recurso);
            Model model = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(ntriples.getBytes(StandardCharsets.UTF_8));
            RDFDataMgr.read(model, is, Lang.NT);
            StringWriter sw = new StringWriter();
            RDFDataMgr.write(sw, model, Lang.TTL);
            response.getWriter().println(sw);
            response.setContentType("text/turtle;charset=UTF-8");
            return;
        } else if (isRDFXML(request)) {
            System.out.println("Serving RDF/XML for " + recurso);
            System.out.println(ntriples);
            Model model = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(ntriples.getBytes(StandardCharsets.UTF_8));
            RDFDataMgr.read(model, is, Lang.NT);
            StringWriter sw = new StringWriter();
            RDFDataMgr.write(sw, model, Lang.RDFXML);
            response.getWriter().println(sw);
            response.setContentType("application/rdf+xml;charset=UTF-8");
            return;
        }        
    }

    private void listResources(HttpServletRequest request, HttpServletResponse response) {
        //SERVING THE LIST OF resources
        System.out.println("Serving HTML for resources");
        String email="nada";
        try {
            response.setContentType("text/html;charset=UTF-8");
            InputStream input = getClass().getResourceAsStream("template_ld.html");
            BufferedInputStream bis = new BufferedInputStream(input);
            String body = IOUtils.toString(bis, "UTF-8");
            String context = LdcConfig.get("context", "/ldc");
            body = body.replace("TEMPLATE_CONTEXTO", context);
            body = body.replace("<!--TEMPLATE_TITLE-->", "\n" + "List of concepts");
            String tabla = "<table id=\"grid-data\" class=\"table table-condensed table-hover table-striped\">\n"
                    + "        <thead>\n"
                    + "                <tr>\n"
                    + "                        <th data-column-id=\"resource\" data-formatter=\"link\" data-order=\"desc\">Resources</th>\n"
                    + "                </tr>\n"
                    + "        </thead>\n"
                    + "</table>	\n"
                    + "";
            
            tabla+=email;
            body = body.replace("<!--TEMPLATE_CONTENT-->", "<br>" + tabla);
            response.getWriter().println(body);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            Api.serveError(request, response);
        }
    }

    /**
     * Lista los recursos de un dataset dado
     */
    private void listResources(HttpServletRequest request, HttpServletResponse response, String dataset) {
        //SERVING THE LIST OF resources
        
        ConditionalDataset cd = Ldc.getDataset(dataset);
        logger.info("Listing resources of " + dataset);
        if (cd==null)
        {
            Api.serveError(request, response);
            return;
        }
        try {
            response.setContentType("text/html;charset=UTF-8");
            InputStream input = getClass().getResourceAsStream("template_ld.html");
            BufferedInputStream bis = new BufferedInputStream(input);
            String body = IOUtils.toString(bis, "UTF-8");
            String context = LdcConfig.get("context", "/ldc");
            body = body.replace("TEMPLATE_CONTEXTO", context);
            body = body.replace("<!--TEMPLATE_TITLE-->", "\n" + "List of entities in " + dataset);
            String tabla = "<table id=\"grid-data\" class=\"table table-condensed table-hover table-striped\">\n"
                    + "        <thead>\n"
                    + "                <tr>\n"
                    + "                        <th data-column-id=\"resource\" data-formatter=\"link\" data-order=\"desc\">Resources</th>\n"
                    + "                </tr>\n"
                    + "        </thead>\n"
                    + "</table>	\n"
                    + "";
            body = body.replace("<!--TEMPLATE_CONTENT-->", "<br>" + tabla);
            response.getWriter().println(body);
            response.setStatus(HttpServletResponse.SC_OK);
            logger.info("Serving HTML for resources");
        } catch (Exception e) {
            Api.serveError(request, response);
        }
    }    
    
    public static boolean isRDFTTL(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean human = true;
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            //      System.out.print(hname + "\t");
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equalsIgnoreCase("Accept")) {
                    if (valor.equals("text/turtle")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determina si la petición ha de ser servida a un humano o directamente el
     * RDF
     *
     * @param request HTTP request
     */
    public static boolean isRDFXML(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean human = true;
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            //      System.out.print(hname + "\t");
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (valor.contains("application/rdf+xml")) {
                    return true;
                }
                if (hname.equalsIgnoreCase("Accept")) {
                    if (valor.contains("application/rdf+xml")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }    
    public static String getTable(Model model, Resource res) {
        String tabla = "";
        

        return tabla;
    }    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
 
    public static String getTablaHTML(ConditionalDataset cd, List<LicensedTriple> ls)
    {
        String tabla="";
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
        return tabla;
    }
    public static List<LicensedTriple> getClosedTriples(ConditionalDataset cd, List<LicensedTriple> ls) {
        List<Grafo> lg = cd.getGrafos();
        List<LicensedTriple> lop = new ArrayList();
        for (LicensedTriple lt : ls) {
            Grafo g = RDFUtils.getGrafo(lg, lt.g);
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
            Grafo g = RDFUtils.getGrafo(lg, lt.g);
            if (g == null) 
            {
                lop.add(lt); //Si no tiene ningun grafo, la politica por defecto es mostrar. borrar esto para NO mostrar
                continue;
            }

            if (!lt.hasPolicyAsObject()) {
                lop.add(lt);
            }
        }
        return lop;
    }
    
}
