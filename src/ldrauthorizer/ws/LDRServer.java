package ldrauthorizer.ws;

//JENA
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMaker;
import com.hp.hpl.jena.sparql.core.DatasetImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldrauthorizerold.GoogleAuthHelper;
import ldrauthorizerold.Multilingual;

import odrlmodel.Asset;
import odrlmodel.LDRConfig;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManager;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

/**
 * This class is the server for the Conditional Access to Linked Data
 * Default server http://salonica.dia.fi.upm.es/ldr/
 * @author Victor
 */
public class LDRServer {
    
    public static Dataset dataset;
    
    static {
        String RDF_DUMP=LDRConfig.getDataset();  
        init(RDF_DUMP);
    }

    /**
     * Determines whether a HTTP Servlet Request is demanding Turtle
     * @param request HTTP request
     * @retur true if demanding turtle
     */
    public static boolean isTurtle(HttpServletRequest request)
    {
        String uri = request.getRequestURI();
        if (uri.endsWith(".ttl")) 
            return true;
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equals("Accept")) {
                    if (valor.equals("text/turtle") || valor.equals("application/x-turtle")) {
                        return true;
                    }
                }
            }
        }
        return false;        
    }
    

    /**
     * Determines whether a HTTP Servlet Request is demanding RDF/XML
     * @param request HTTP request
     * @return true if demanding RDF/XML
     */
    public static boolean isRDFXML(HttpServletRequest request)
    {
        String uri = request.getRequestURI();
        if (uri.endsWith(".rdf")) 
            return true;
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equals("Accept")) {
                    if (valor.equals("application/rdf+xml")) {
                        return true;
                    }
                }
            }
        }
        return false;        
    }
    
    
    /**
     * Determina si la petición ha de ser servida a un humano o directamente el RDF
     * @param request HTTP request
     */
    public static boolean isHuman(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.endsWith(".ttl") || uri.endsWith(".rdf")) {
            return false;
        }
        boolean human = true;
        Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            String hname = (String) enume.nextElement();
            Enumeration<String> enum2 = request.getHeaders(hname);
            //      System.out.print(hname + "\t");
            while (enum2.hasMoreElements()) {
                String valor = enum2.nextElement();
                if (hname.equals("Accept")) {
                    if (valor.equals("application/rdf+xml") || valor.equals("text/turtle") || valor.equals("application/x-turtle")) {
                        return false;
                    }
                }
            }
        }
        return human;
    }

    /**
     * La madre del cordero del Servidor de Linked Data. 
     * Determina si 
     */
    public static HttpServletResponse parseResource(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        boolean binario = false;
        try {
            
            String token = "";
            String s2 = request.getRequestURI();
            String s3 = s2.substring(4);
            s3 = "../htdocs" + s3;
            File f = new File(s3);
            
            if (s2.equals("/ldr/en/accountability.html"))
            {
                token = FileUtils.readFileToString(f);
                String html = Evento.getEventosHTML();
                token=token.replaceAll("<!--TEMPLATEHERE1-->", html);
                
                String footer = FileUtils.readFileToString(new File("../htdocs/en/footer.html"));
                html=html.replace("<!--TEMPLATEFOOTER-->", footer);
                
                
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().print(token);                
                baseRequest.setHandled(true);  
                return response;
            }
   /*         else if (s2.equals("/ldr/en/datasets.html")) {
                token = FileUtils.readFileToString(f);
                
                String footer = FileUtils.readFileToString(new File("../htdocs/en/footer.html"));
                token=token.replace("<!--TEMPLATEFOOTER-->", footer);
                
                Portfolio p = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
                String htmlOffers = "";
                htmlOffers = getHTMLForOffers(p.policies);
                token = token.replace("<!--TEMPLATEHERE1-->", htmlOffers);

            }*/
            else if (s2.startsWith("/ldr/")) { //cualquier otra cosa bajo el paraguas de /ldr/
                if (f.exists()) {
    //                view.addInfo("Sirviendo al humano la página general " + s2);
                    if (s3.endsWith(".html") || s3.endsWith(".css") || s3.endsWith(".js")) {
                        token = FileUtils.readFileToString(f);
                    } else if (s3.endsWith(".png")) {
                        response.setContentType("image/png");
                        ServletOutputStream output = response.getOutputStream();
                        InputStream input = new FileInputStream(s3);
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        binario = true;
                    } else if (s3.endsWith(".ico")) {
                        response.setContentType("image/x-icon");
                        ServletOutputStream output = response.getOutputStream();
                        InputStream input = new FileInputStream(s3);
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        binario = true;
                    } else if (s3.endsWith(".swf")) {
                        response.setContentType("application/x-shockwave-flash");
                        ServletOutputStream output = response.getOutputStream();
                        InputStream input = new FileInputStream(s3);
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        binario = true;
                    } else if (s3.endsWith(".nq")) {
                        response.setContentType("application/n-quads");
                        token = FileUtils.readFileToString(f);
                    } else {
          //              view.addInfo("404 " + s2);
                        response.sendRedirect("/ldr/en/404.html");
                        response.setStatus(HttpServletResponse.SC_FOUND);
                        baseRequest.setHandled(true);            
                        
                        
                        token = Multilingual.get(1, "en");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                }else {
                token = Multilingual.get(1, "es");
                response.sendRedirect("/ldr/en/404.html");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                baseRequest.setHandled(true);            
            }
            } 
            
            
            
            if (!binario) {
                response.getWriter().print(token);
            }
        } catch (IOException ex) {
            Logger.getLogger("ldr").warn(ex.getMessage());
            
        }
        return response;
    }

    /**
     * Reads a text file embedded in the package
     */
    public static String readTextFile(String slocalfile) {
        String str = "";
        try {
            InputStream is = LDRServer.class.getResourceAsStream(slocalfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                str += strLine + "\n";
            }
            is.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        Logger.getLogger("ldr").info("Precargados archivo");
        return str;
    }
    
    /**
     * Inicializa haciendo una lectura del archivo 
     */
    private static void init(String fileName) {
        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {
            
            public Graph create() {
                return new GraphMem();
            }
        };
        dataset = DatasetImpl.wrap(new DatasetGraphMaker(maker));
        File f = new File(fileName);
        if (!f.exists()) {
            System.err.println("archivo " + fileName + " no existe");
        }
        RDFDataMgr.read(dataset, fileName);
        
        String info="Datos procesados de " + fileName+" Grafos: \n";
        Iterator<String> it = dataset.listNames();
        while(it.hasNext())
            info+= it.next()+"\n";
        Logger.getLogger("ldr").info(info);
    }
    
    public static List<LicensedTriple> getOpenTriples(List<LicensedTriple> ls) {
        List<LicensedTriple> lop = new ArrayList();
        for (LicensedTriple lt : ls) {
            if (lt.isOpen()) {
                lop.add(lt);
            }
        }
        return lop;
    }
    
    public static List<LicensedTriple> getLicensedTriples(List<LicensedTriple> ls) {
        List<LicensedTriple> lop = new ArrayList();
        for (LicensedTriple lt : ls) {
            //       System.out.println(ls);
            if (!lt.isOpen()) {
                lop.add(lt);
            }
        }
        return lop;
    }
    
    /**
     * Formats in HTML the triples to be shown.
     * @param label
     * @param ls List of licensed triples
     * @param lan Language
     */
    public static String formatHTMLTriples(String label, List<LicensedTriple> ls, String lan) {
        String html = "";
        String tabla = "";
        
        String templatefile = "";
        if (lan.equals("es")) {
            templatefile = "../htdocs/resource.html";
        } else {
            templatefile = "../htdocs/en/resource.html";
        }
        
        try {
            html = FileUtils.readFileToString(new File(templatefile));
            String footer = FileUtils.readFileToString(new File("../htdocs/en/footer.html"));
            html=html.replace("<!--TEMPLATEFOOTER-->", footer);
        } catch (Exception e) {
            return "page not found";
        };

//        html = readTextFile("template.html");
        label = "<h2>" + label + "</h2>";
        html = html.replace("<!--TEMPLATEHERE1-->", label);
        
        tabla += "<h4>Open triples</h4>\n";
        tabla += "<table><tr><td><strong>" + Multilingual.get(10, lan) + "</strong></td><td><strong>" + Multilingual.get(11, lan) + "</strong></td></tr>\n";
        List<LicensedTriple> open = getOpenTriples(ls);
        Collections.sort(open, LicensedTriple.PREDICATECOMPARATOR);
        for (LicensedTriple lt : open) {
            tabla += lt.toHTMLRow(lan) + "\n";
        }
        tabla += "</table>\n";
        
        tabla += "<p style=\"margin-bottom: 2cm;\"></p>";
        
        tabla += "<h4>Limited access triples</h4>\n";
        tabla += "<table><tr><td><strong>" + Multilingual.get(10, lan) + "</strong></td><td><strong>" + Multilingual.get(11, lan) + "</strong></td></tr>\n";
        List<LicensedTriple> closed = getLicensedTriples(ls);
        for (LicensedTriple lt : closed) {
            tabla += lt.toHTMLRow(lan) + "\n";
        }
        tabla += "</table>\n";
        
        
        html = html.replace("<!--TEMPLATEHERE2-->", tabla);
        
        return html;
    }
    
    private static String formatHTMLTriplesSingleTable(String label, List<LicensedTriple> ls, String lan) {
        String html = "";
        String tabla = "";
        html = readTextFile("template.html");
        
        tabla += "<h4>Open triples</h4>\n";
        tabla += "<table><tr><td><strong>Propiedad</strong></td><td><strong>Valor</strong></td></tr>\n";
        for (LicensedTriple lt : ls) {
            tabla += lt.toHTMLRow(lan) + "\n";
        }
        tabla += "</table>\n";
        label = "<h2>" + label + "</h2>";
        html = html.replace("<!--TEMPLATEHERE1-->", label);
        html = html.replace("<!--TEMPLATEHERE2-->", tabla);
        
        return html;
    }

    /**
     * Busca todos los triples para los cuales uri es el sujeto
     * @param uri is a resource
     * @return Una lista de triples licenciados
     */
    public static List<LicensedTriple> searchTriplesFor(String uri) {
        
        if (uri.endsWith(".rdf") || uri.endsWith(".ttl")) {
            uri = uri.substring(0, uri.length() - 4);
        }
        
        List<LicensedTriple> statements = new ArrayList();
        
        String uricompleta = LDRConfig.getServer() + uri;
        
        String recurso = "<" + uricompleta + ">";
        String prefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
        prefix += "PREFIX  xsd:    <http://www.w3.org/2001/XMLSchema#>\n";
        prefix += "PREFIX  :       <.>\n";
        String queryString = prefix + "SELECT ?g ?p ?o WHERE {GRAPH ?g{" + recurso + " ?p ?o }} ";

//        LDRAuthorizerView view = (LDRAuthorizerView) LDRAuthorizerApp.getApplication().getMainView();
//        view.addInfo("Lanzando consulta: " + queryString);
        Logger.getLogger("ldr").debug(queryString);

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        try {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                RDFNode z = soln.get("?s");       // Get a result variable by name.
                RDFNode x = soln.get("?p");       // Get a result variable by name.
                RDFNode y = soln.get("?o");       // Get a result variable by name.
                RDFNode g = soln.get("?g");


                //         System.out.println(y + "                 " + g);

                Model mtmp = ModelFactory.createDefaultModel();
                Statement stmt = mtmp.createStatement(mtmp.createResource(uricompleta), mtmp.createProperty(x.asResource().getURI()), y);
                LicensedTriple lt = new LicensedTriple();
                lt.stmt = stmt;
                lt.g = g.toString();
                statements.add(lt);
            }
        } catch(Exception e ){
            e.printStackTrace();
        }
            finally {
            qexec.close();
        }
        return statements;
    }
    
    public static String getTipoOf(String s2) {
        String label = "";
        String s1 = "<" + LDRConfig.getServer(); //<http://salonica.dia.fi.upm.es";
        String recurso = s1 + s2 + ">";
//        System.out.println("Buscando labelAsset de " + recurso);
        String prefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
        prefix += "PREFIX  rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n";
        //   recurso = "<http://salonica.dia.fi.upm.es/ldr/resource/Provincia/Valladolid>";
        String queryString = prefix + "SELECT distinct ?z WHERE {GRAPH ?g{" + recurso + " ?p ?o .  " + recurso + " rdf:type ?z}} ";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        try {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                RDFNode z = soln.get("?z");
                if (z.isLiteral()) {
                    label = z.asLiteral().getString();
                }
                if (z.isResource()) {
                    label = z.asResource().getURI();
                    label = URLDecoder.decode(label, "UTF-8");
                    int index1 = label.lastIndexOf("/");
                    int index2 = label.lastIndexOf("#");
                    int index = Math.max(index1, index2);
                    label = label.substring(index + 1);
                    
                }
        //        System.out.println(z);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }

    /**
     * Obtiene la primera etiqueta para una uri dada
     */
    public static String getLabelOf(String uri) {
        String label = "";
        String s1 = "<" + LDRConfig.getServer();;
        String recurso = s1 + uri + ">";
//        System.out.println("Buscando labelAsset de " + recurso);
        String prefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
        prefix += "PREFIX  rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n";
        //   recurso = "<http://salonica.dia.fi.upm.es/ldr/resource/Provincia/Valladolid>";
        String queryString = prefix + "SELECT distinct ?z WHERE {GRAPH ?g{" + recurso + " ?p ?o .  " + recurso + " rdfs:label ?z}} ";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        try {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                RDFNode z = soln.get("?z");
                if (z.isLiteral()) {
                    label = z.asLiteral().getString();
                }
         //       System.out.println(z);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }
    
    public static void main(String[] args) throws IOException {
        String s = FileUtils.readFileToString(new File("../htdocs/index.html"));
        //    String s = LDRServer.readTextFile("../htdocs/index.html");
        System.out.println(s);
        
    }
    
    public static void testQuery2(Dataset dataset) {
        String municipio = "<http://salonica.dia.fi.upm.es/ldr/ontology/Provincia>";
        String recurso = "<http://geo.linkeddata.es/resource/Provincia/Valladolid>";
        String prefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
        prefix += "PREFIX  rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n";
        prefix += "PREFIX  xsd:    <http://www.w3.org/2001/XMLSchema#>\n";
        prefix += "PREFIX  :       <.>\n";

        //http://salonica.dia.fi.upm.es/ldr/ontology/Provincia
        //http://salonica.dia.fi.upm.es/ldr/resource/ComunidadAut%C3%B3noma
        String queryString = prefix + "SELECT * WHERE {?s a <http://salonica.dia.fi.upm.es/ldr/ontology/Provincia>. "
                + "?s rdfs:label ?l } ";
//                String queryString = prefix+"SELECT ?p ?o WHERE {"+ recurso +" ?p ?o} ";
//        String queryString = prefix+"SELECT * {    { ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } } } ";
//        String queryString = prefix+"SELECT ?p ?o WHERE {"+ recurso +" ?p ?o UNION { GRAPH ?g { ?s ?p ?o } }} ";
//        String queryString = prefix+"SELECT * {    { ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } } } ";

//        String queryString = prefix+"SELECT ?g ?p ?o WHERE {GRAPH ?g{"+ recurso +" ?p ?o }} ";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        try {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                RDFNode z = soln.get("?l");       // Get a result variable by name.
                RDFNode s = soln.get("?s");       // Get a result variable by name.
                //   System.out.println(s);
                if (z.isLiteral()) {
                    Literal lit = (Literal) z;
       //             System.out.println("<a href=\"" + s + "\">" + lit.getString() + "</a>");
                }
                
            }
        } finally {
            qexec.close();
        }
    }
    
    public static String formatTTLTriples(String label, List<LicensedTriple> lst) {
        String s = "";
        Model model = ModelFactory.createDefaultModel();
        for (LicensedTriple lt : lst) {
            model.add(lt.stmt);
        }
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, Lang.TTL);
        s = sw.toString();
        return s;
    }
    

}
