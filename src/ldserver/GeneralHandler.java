package ldserver;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.ConditionalDataset;
import ldconditional.ConditionalDatasets;
import ldrauthorizer.ws.LDRServer;
import ldrauthorizer.ws.LicensedTriple;
import ldrauthorizerold.Multilingual;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Handler that processes the rest of resources
 * 
 * http://conditional.linkeddata.es/iate                              -- intro
 * http://conditional.linkeddata.es/iate/sparql                       -- sparql endpoint
 * http://conditional.linkeddata.es/iate/resource/myresource          -- linked data
 * http://conditional.linkeddata.es/iate/service?listResources        -- 
 * http://conditional.linkeddata.es/iate/service?getOffers            -- 
 * 
 * @author vroddon
 */
public class GeneralHandler extends AbstractHandler {

    static final Logger logger = Logger.getLogger(GeneralHandler.class);

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled()) {
            return;
        }
        logger.info("General handler processing this URI " + request.getRequestURI());

        String folder = string;
        boolean ok = processQuery(baseRequest, request, response, folder);
        if (ok) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            baseRequest.setHandled(true);
        } else //error 404
        {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("404 not found");
            baseRequest.setHandled(true);
        }
    }

    /**
     * Sirve un archivo estándar
     */
    private boolean processQuery(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String folder) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        try {

            String token = "";
            String requestUri = request.getRequestURI();
            String suri = requestUri;
            int ini = requestUri.indexOf("/", 1);
            String dataset2 = requestUri.substring(1, ini);
            //suri = "./datasets" + requestUri;
            suri = "htdocs" + requestUri.substring(ini, requestUri.length());

            if (requestUri.startsWith("/datasets/") && requestUri.endsWith("logo.png")) {
                suri = requestUri.substring(1, requestUri.length());
            }

            if (requestUri.contains("/resource/"))
            {
                logger.info("Serving a resource " + request.getRequestURI());
                int index = requestUri.indexOf("/", 1);
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);

                    serveResource(baseRequest, request, response, dataset);
                    return true;
                }
            }



            File f = new File(suri);
            if (!f.exists()) {
                logger.warn("The requested file does not exist");
                return false;
            }
            if (f.isDirectory()) {
                logger.info("Directory. Trying to retrieve a dataset " + requestUri);
                requestUri = requestUri + "/index.html";
                requestUri = requestUri.replace("//", "/"); //por si acaso había la barra al final
                response.sendRedirect(requestUri);
                return true;
            }
            // PAGINA PRINCIPAL TENEMOS /geo/index.html
            //veamos si es el índice de un dataset:
            if (requestUri.endsWith("index.html")) {
                logger.info("Serving the index " + request.getRequestURI());
                int index = requestUri.lastIndexOf("/");
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    serveIndex(baseRequest, response, dataset);
                    return true;
                }
            }

            if (requestUri.endsWith("linkeddata.html")) {
                logger.info("Serving the main resources " + request.getRequestURI());
                int index = requestUri.lastIndexOf("/");
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    serveLinkeddata(baseRequest, response, dataset);
                    return true;
                }
            }



            int i = requestUri.lastIndexOf("/");
            if (i == -1) {
                return false;
            }
            i = requestUri.lastIndexOf(".");
            if (i != -1) {
                String extension = requestUri.substring(i);
                MimeManager mm = new MimeManager();
                String tipo = mm.mapa.get(extension);
                logger.info("Serving a standard file " + request.getRequestURI() + ((tipo==null) ? " de tipo desconocido " : (" de tipo " + tipo) ));
                if (tipo != null) {
                    response.setContentType(tipo);
                    if (tipo.startsWith("text")) {
                        token = FileUtils.readFileToString(f);
                        response.getWriter().print(token);
                    } else {
                        ServletOutputStream output = response.getOutputStream();
                        InputStream input = new FileInputStream(f);
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    }
                } else {
                    response.setContentType("application/octet-stream");
                    ServletOutputStream output = response.getOutputStream();
                    InputStream input = new FileInputStream(f);
                    byte[] buffer = new byte[2048];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
        return true;
    }

    public void serveIndex(Request baseRequest, HttpServletResponse response, String dataset) {
        String body = "";
        try {
            ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
            String comment = cd.getComment();
            body = FileUtils.readFileToString(new File("./htdocs/index.html"));
            String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
            body = body.replace("<!--TEMPLATEFOOTER-->", footer);
            body = body.replace("<!--TEMPLATEDATASETCOMMENT-->", comment);

            String img = "<img src=\"../datasets/" + cd.name + "/logo.png\" style=\"vertical-align:middle;width:200px;float:left;margin:8px;\"/>";
            body = body.replace("<!--TEMPLATEDATASETIMAGE-->", img);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(body);
            baseRequest.setHandled(true);
        } catch (Exception e) {
            logger.warn("Error serving index.html " + e.getMessage());
        }
    }
    public void serveResource(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String dataset) {

        String recurso =baseRequest.getRootURL().toString() + request.getRequestURI();
        ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
//        Model m = cd.getModelRecurso(recurso);
        List<LicensedTriple> llt = cd.getLicensedTriples(recurso);
        String label = cd.getResourceFirstProperty(recurso);
        String html = formatHTMLTriplesNew(label, llt, label);
        try{
        response.getWriter().print(html);
        response.setStatus(HttpServletResponse.SC_OK);

        }catch(Exception e){}
        baseRequest.setHandled(true);
        response.setContentType("text/html;charset=utf-8");
//        GetOpenResource gr = new GetOpenResource();
//        String json = gr.generarJson(dataset, recurso);
//        System.out.println(json);
      //  formatHTMLTriples();
    }

    public void serveLinkeddata(Request baseRequest, HttpServletResponse response, String dataset) {
        String body = "";
        try {
            ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
            String comment = cd.getComment();
            body = FileUtils.readFileToString(new File("./htdocs/linkeddata.html"));
            String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
            body = body.replace("<!--TEMPLATEFOOTER-->", footer);
            body = body.replace("<!--TEMPLATEDATASETCOMMENT-->", comment);

            String mainres = cd.getHTMLMainResources();
            body = body.replace("<!--TEMPLATEMAINRESOURCES-->", mainres);


            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(body);
            baseRequest.setHandled(true);
        } catch (Exception e) {
            logger.warn("Error serving index.html " + e.getMessage());
        }
    }



    /**
     * Formats in HTML the triples to be shown.
     * @param label
     * @param ls List of licensed triples
     * @param lan Language
     */
    public static String formatHTMLTriplesNew(String label, List<LicensedTriple> ls, String NOTUSED) {
        String html = "";
        String tabla = "";
        String lan = "en";

        String templatefile = "htdocs/resource.html";

        try {
            html = FileUtils.readFileToString(new File(templatefile));
            String footer = FileUtils.readFileToString(new File("htdocs/footer.html"));
            html=html.replace("<!--TEMPLATEFOOTER-->", footer);
        } catch (Exception e) {
            return "page not found " + e.getMessage();
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

}
