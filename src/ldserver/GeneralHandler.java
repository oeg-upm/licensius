package ldserver;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import handlers.HandlerAccount;
import handlers.HandlerDatasets;
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
import ldrauthorizer.ws.WebPolicyManager;
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
                    HandlerIndex hi = new HandlerIndex();
                    hi.serveIndex(baseRequest, response, dataset);
                    return true;
                }
            }

            if (requestUri.endsWith("linkeddata.html")) {
                logger.info("Serving the main resources " + request.getRequestURI());
                int index = requestUri.lastIndexOf("/");
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    HandlerLinkedData hld = new HandlerLinkedData();
                    hld.serveLinkeddata(baseRequest, response, dataset);
                    return true;
                }
            }

            if (requestUri.endsWith("datasets.html")) {
                logger.info("Serving the main offers " + request.getRequestURI());
                int index = requestUri.lastIndexOf("/");
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    HandlerDatasets hd = new HandlerDatasets();
                    hd.serveOffers(baseRequest, request, response, dataset);
                    return true;
                }
            }

            if (requestUri.endsWith("account.html")) {
                logger.info("Serving the account " + request.getRequestURI());
                int index = requestUri.lastIndexOf("/");
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    HandlerAccount ha = new HandlerAccount();
                    ha.serveAccount(baseRequest, request, response, dataset);
                    return true;
                }
            }

            if (requestUri.contains("manageren")) {
                logger.info("Serving the manager " + request.getRequestURI());
                int index = requestUri.indexOf("/",1);
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    HandlerManager hm = new HandlerManager();
                    hm.serveManager(baseRequest, request, response, dataset);
                    return true;
                }
            }

            serveGeneralFile(f, requestUri, baseRequest, request, response, folder);

        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean serveGeneralFile(File f, String requestUri, Request baseRequest, HttpServletRequest request, HttpServletResponse response, String folder) {
        if (!f.exists()) {
            logger.warn("The requested file does not exist");
            return false;
        }

        try {
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
                        String token = FileUtils.readFileToString(f);
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
        }
        return true;
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


    /**
     * Autoriza un recurso, dado un conjunto de políticas compradas en el portfolio
     */
    public static AuthorizationResponse AuthorizeResource(Grafo grafo, List<Policy> portfolioPolicies) {
        AuthorizationResponse ar = new AuthorizationResponse();
        ar.ok = false;
        ar.policies.clear();
        //y si no, miramos ALGUNA DEL PORTFOLIO
        for (Policy policy : portfolioPolicies) {
            if (policy.isInOffer() && policy.hasFirstTarget(grafo.getURI())) {
                ar.ok = true;
                ar.policies.add(policy);
                return ar;
            }
        }

        List<Policy> policies = grafo.getPolicies();
        if (policies.isEmpty()) {
            return ar;
        }

        //SI HAY ALGUNA POLITICA ABIRTA, PERFECTO
        for (Policy policy : policies) {
            Policy policyFromStore = PolicyManagerOld.getPolicy(policy.getURI());
            if (policyFromStore == null) {
                continue;
            }
            if (policyFromStore.isInOffer() && policyFromStore.isOpen()) {
                ar.ok = true;
                ar.policies.add(policyFromStore);
                return ar;
            } else if (policyFromStore.isInOffer()) {
                ar.policies.add(policyFromStore);
            }
        }



        return ar;

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
