package ldserver;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.ConditionalDataset;
import ldconditional.ConditionalDatasets;
import ldrauthorizer.ws.AuthorizationResponse;
import ldrauthorizer.ws.LDRServer;
import ldrauthorizer.ws.LicensedTriple;
import ldrauthorizer.ws.ODRLAuthorizer;
import ldrauthorizer.ws.Portfolio;
import ldrauthorizerold.GoogleAuthHelper;
import ldrauthorizerold.Multilingual;
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
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
     * Sirve un ana query estándar
     */
    private boolean processQuery(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String folder) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            String token = "";
            String requestUri = request.getRequestURI();
            String suri = requestUri;
            int ini = requestUri.indexOf("/", 1);
            suri = "htdocs" + requestUri.substring(ini, requestUri.length());
            if (requestUri.startsWith("/datasets/") && requestUri.endsWith("logo.png")) {
                suri = requestUri.substring(1, requestUri.length());
            }

            if (requestUri.contains("/resource/")) {
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

            if (requestUri.endsWith("datasets.html")) {
                logger.info("Serving the main offers " + request.getRequestURI());
                int index = requestUri.lastIndexOf("/");
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    serveOffers(baseRequest, request, response, dataset);
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
                logger.info("Serving a standard file " + request.getRequestURI() + ((tipo == null) ? " de tipo desconocido " : (" de tipo " + tipo)));
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
            logger.error("Error serving index.html " + e.getMessage());
        }
    }

    public void serveResource(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String dataset) {

        String recurso = baseRequest.getRootURL().toString() + request.getRequestURI();
        ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
//        Model m = cd.getModelRecurso(recurso);
        List<LicensedTriple> llt = cd.getLicensedTriples(recurso);
        String label = cd.getFirstObject(recurso, "http://www.w3.org/2000/01/rdf-schema#label");
        String html = formatHTMLTriplesNew(label, llt);
        try {
            response.getWriter().print(html);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
        }
        baseRequest.setHandled(true);
        response.setContentType("text/html;charset=utf-8");
//        GetOpenResource gr = new GetOpenResource();
//        String json = gr.generarJson(dataset, recurso);
//        System.out.println(json);
        //  formatHTMLTriples();
    }

    public void serveOffers(Request baseRequest, HttpServletRequest request,  HttpServletResponse response, String dataset) {
        String body = "";
        try {
            String token = "";
            File f = new File("datasets.html");
            token = FileUtils.readFileToString(f);
            String footer = FileUtils.readFileToString(new File("footer.html"));
            token = token.replace("<!--TEMPLATEFOOTER-->", footer);
            Portfolio por = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
            String htmlOffers = "";
            htmlOffers = getHTMLForDatasets(por.policies);
            token = token.replace("<!--TEMPLATEHERE1-->", htmlOffers);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(token);
            baseRequest.setHandled(true);


            if (true)
                return;


            ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
            List<String> graphs = cd.getGraphs();
            for (String grafo : graphs) {
                Recurso recurso = cd.getDatasetPolicy().getRecurso(grafo);
                if (recurso == null) {
                    continue;
                }
                List<Policy> policies = cd.getPoliciesForGraph(grafo);
                for (Policy p : policies) {
                    String nombre = p.getTitle();
                    if (nombre.isEmpty()) {
                        nombre = p.getLabel("en");
                    }
                    if (!p.inOffer) {
                        continue;
                    }
                    Oferta offer = new Oferta();
                    offer.setLicense_title(nombre);
                    offer.setLicense_link(p.uri);
                    offer.setLicense_price("OpenData");

                }
                // LO DEJO POR AQUI PERO ES QUE ESTOY MUERTO DE SUEÑO
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(body);
            baseRequest.setHandled(true);
        } catch (Exception e) {
            logger.error("Error serving index.html " + e.getMessage());
        }
    }

    /**
     * Formats in HTML the triples to be shown.
     * @param label
     * @param ls List of licensed triples
     * @param lan Language
     */
    public static String formatHTMLTriplesNew(String label, List<LicensedTriple> ls) {
        String html = "";
        String tabla = "";
        String lan = "en";

        String templatefile = "htdocs/resource.html";

        try {
            html = FileUtils.readFileToString(new File(templatefile));
            String footer = FileUtils.readFileToString(new File("htdocs/footer.html"));
            html = html.replace("<!--TEMPLATEFOOTER-->", footer);
        } catch (Exception e) {
            return "page not found " + e.getMessage();
        }

        // html = readTextFile("template.html");
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

    private static String getHTMLForDatasets(List<Policy> pPagadas) {
        String html = "";
        //Para cada dataset...
        List<Asset> assets = AssetManager.readAssets();
        html += "<div style=\"color:#AA2222 !important;\"><tr style=\"height=10px;font-weight: bold;color:#AA2222 !important;\"><td>Dataset";
        html += "<td>RDF dump</td><td>Licenses</td><td>Description</td></div>\n";
        for (Asset asset : assets) {
            if (!asset.isInOffer()) {
                continue;
            }
            String labelAsset = asset.getLabel("en");               //nombre
            String commentAsset = asset.getComment();               //descripcion
            String servicio = getServicioGetDatabaseURL(asset);     //enlace
            boolean bOpen = asset.isOpen();                         //si es o no abierto
            String sopena = "<img src=\"/ldr/img/arrowdown32green.png\">";
            String scloseda = "<img src=\"/ldr/img/arrowdown32red.png\">";



            String sopen = "<button class=\"cupid-blue\" onclick=\"location.href='linkeddata'\">Download</button>";
            String sclosed = "";

            String color = bOpen ? sopen : sclosed;
            AuthorizationResponse r = ODRLAuthorizer.AuthorizeResource(asset.getURI(), pPagadas);
            if (r.ok == true) {
                color = sopen;
            }

            String htmlpol = "<td>";
            List<Policy> policies = asset.getPolicies();
            int conta=0;
            for (Policy policy : policies) {
                Policy policy2 = policy;
                if (policy2 == null) {
                    continue;
                }
                String uri = policy2.getURI();
                String target = "";
                try {
                    target = URLEncoder.encode(asset.getURI(), "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                uri += "?target=" + target;
                conta++;

                htmlpol+=conta+"- ";
                htmlpol += "<a href=\"" + uri + "\"/>";
                if (policy2.isOpen()) {
                    htmlpol+="<span>"+policy.getLabel("en")+"<br/></span>";
//                    htmlpol += "<img src=\"/ldr/img/license32green.png\">";
                } else {
                    htmlpol+="<span>"+policy.getLabel("en")+"<br/></span>";
//                    htmlpol += "<img src=\"/ldr/img/license32red.png\">";
                }
                htmlpol += "</a>";
            }
            htmlpol += "</td>";


            html += "<tr style=\"height=20px;\">";

            html += "<td> <strong>" + labelAsset + "</strong></td>";

            html += "<td> <a href=\"" + servicio + "\">" + color + "</a></td>";

            html += htmlpol;

            html += "<td>" + commentAsset + "</td></tr>\n";


        }

        return html;
    }

    private static String getServicioGetDatabaseURL(Asset asset) {
        String datasetEncoded = asset.getURI();
        try {
            datasetEncoded = URLEncoder.encode(asset.getURI(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String servicio = "service/getDataset?dataset=" + datasetEncoded;
        return servicio;

    }
}
