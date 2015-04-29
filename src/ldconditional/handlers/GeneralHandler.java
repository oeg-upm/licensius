package ldconditional.handlers;

import ldconditional.handlers.HandlerAccount;
import ldconditional.handlers.HandlerOffers;
import ldconditional.handlers.HandlerIndex;
import ldconditional.handlers.HandlerLinkedData;
import ldconditional.handlers.HandlerManager;
import ldconditional.handlers.HandlerPolicy;
import ldconditional.handlers.HandlerPortal;
import ldconditional.handlers.HandlerResource;
import ldconditional.handlers.HandlerSPARQL;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import ldconditional.auth.AuthorizationResponse;
import ldconditional.auth.GoogleAuthHelper;
import ldconditional.LDRConfig;
import ldconditional.auth.Evento;
import ldconditional.ldserver.MimeManager;
import ldconditional.model.Grafo;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.rdf.ODRLRDF;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
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

        //Gestión del usuario de sesión
        String sesState = (String) request.getSession().getAttribute("state");
        if (sesState == null) {
            final GoogleAuthHelper helper = new GoogleAuthHelper();
            request.getSession().setAttribute("state", helper.getStateToken());
        }
        //Fin de gestión de usuario de sesión


        


        String folder = string;
        boolean ok = sortQuery(baseRequest, request, response, folder);
        if (ok) {
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
        } else //error 404
        {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("404 not found");
            response.sendRedirect("/404.html");
            baseRequest.setHandled(true);
        }
    }

    /**
     * Sirve un ana query estándar
     */
    private boolean sortQuery(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String folder) {
//        response.setContentType("text/html;charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
        try {
            String requestUri = request.getRequestURI(); //por ejemplo, /geo/service?getOffers o /img/logo.png
            String sLocalfile = requestUri;              //Local path for the resource
            ConditionalDataset cd = null;                //Dataset on which we operate
            String resto ="";
            int ixndex = requestUri.indexOf("/", 1);
            if (ixndex != -1) {
                String sx = requestUri.substring(1, ixndex);
                cd = ConditionalDatasets.getDataset(sx);
                if (cd==null)
                {
                    sLocalfile = "htdocs"+ requestUri;
                }
                else
                {
                    resto = requestUri.substring(ixndex, requestUri.length());
                    sLocalfile = "htdocs" + resto;
                }
            }else
            {
                sLocalfile = "htdocs"+requestUri;
            }
            if (request.getRequestURI().endsWith("languages.png"))
            {
      //          sLocalfile = "htdocs"+requestUri;
            }
            if (requestUri.contains("/service"))
            {
                return true;
            }

            if (requestUri.equals("/user"))
            {
                cd = ConditionalDatasets.getSelectedDataset();
                if (cd==null)
                {
                    response.sendRedirect("/404.html");
                    return false;
                }
                String inituser = "/"+cd.name+"/linkeddata.html";
//                requestUri = inituser;
                response.sendRedirect(inituser);
                return true;
            }

            if (requestUri.endsWith("/accountability.html")) {
                String token="";
                token = FileUtils.readFileToString(new File("./htdocs/template_accountability.html"));
                String html = Evento.getEventosHTML();
                token=token.replaceAll("<!--TEMPLATEHERE1-->", html);
                String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
                html=html.replace("<!--TEMPLATEFOOTER-->", footer);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().print(token);
                baseRequest.setHandled(true);
                return true;
            }

            if (requestUri.startsWith("/oauth2callback")) {
                System.out.println("Recibida una autenticación en Google");
                String nuevaurl = "account.html";
                URIBuilder builder = new URIBuilder();
                builder.setPath("account.html");
                Enumeration<String> enume = request.getParameterNames();
                while (enume.hasMoreElements()) {
                    String p = enume.nextElement();
                    String values[] = request.getParameterValues(p);
                    for (int i = 0; i < values.length; i++) {
                        System.out.println("Param " + p + " " + values[i]);
                        builder.addParameter(p, values[i]);
                    }
                }
                String uri = "";
                try {
                    uri = builder.build().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("URI " + uri);
                response.sendRedirect(uri);
                response.setStatus(HttpServletResponse.SC_FOUND);
                baseRequest.setHandled(true);
                return true;
            }

            if (requestUri.equals("/favicon.ico")) {
                ServletOutputStream output = response.getOutputStream();
                InputStream input = new FileInputStream("./htdocs/favicon.ico");
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                response.setContentType("image/x-icon");
                return true;
            }


            //CASE 1: LOGO OF A DATASET
            if (requestUri.startsWith("/datasets/") && requestUri.endsWith("logo.png")) {
                sLocalfile = requestUri.substring(1, requestUri.length());
            }
            else if (requestUri.endsWith("languages.png"))
            {
//                sLocalfile = LDRConfig.getServer()+requestUri;
 //               System.out.println("repx ");

            }


            //CASE 2: A FOLDER
            File f = new File(sLocalfile);
            if (f.isDirectory()) {
                logger.info("Directory. Trying to retrieve a dataset " + requestUri);
                requestUri = requestUri + "/index.html";
                requestUri = requestUri.replace("//", "/"); //por si acaso había la barra al final
                response.sendRedirect(requestUri);
                return true;
            }
            if (requestUri.equals("/index.html"))
            {
                HandlerPortal hp = new HandlerPortal();
                hp.serve(request, response, "");
                return true;
            }

            //CASE 3: A RESOURCE (e.g. "Alicante")
            if (requestUri.contains("/resource/")) {
                logger.info("Serving a resource " + request.getRequestURI());
                int index = requestUri.indexOf("/", 1);
                if (index != -1 && index != 0) {
                    HandlerResource hr = new HandlerResource();
                    String dataset = requestUri.substring(1, index);
                    hr.serveResource(baseRequest, request, response, dataset);
                    return true;
                }
            }
            if (requestUri.contains("query")) {
                if (cd==null)
                    cd = ConditionalDatasets.getSelectedDataset();

                if  (requestUri.startsWith("/query"))
                {
                    requestUri = cd.name+requestUri;
                    response.sendRedirect(requestUri);
                    return true;
                }
                 int index = requestUri.indexOf("/", 1);
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    HandlerSPARQL sparql = new HandlerSPARQL();
                    sparql.handle(cd, baseRequest, request, response, dataset);
                }
                return true;
            }

            // PAGINA PRINCIPAL TENEMOS /geo/index.html
            // CASE 4. MAIN PAGE OF A DATASET
            if (requestUri.endsWith("index.html")) {
                /*
                logger.info("Serving the index " + request.getRequestURI());
                int index = requestUri.lastIndexOf("/");
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    HandlerIndex hi = new HandlerIndex();
                    hi.serveIndex(baseRequest, response, dataset);
                    return true;
                }*/
            }

            //CASE 5
            if (requestUri.endsWith("linkeddata.html")) {
                logger.info("Serving the main resources " + request.getRequestURI());
                cd = ConditionalDatasets.getSelectedDataset();
                if (cd==null)
                {
                    response.sendRedirect("/404.html");
                    return false;
                }
                if  (requestUri.startsWith("/linkeddata.html"))
                {
                    requestUri = cd.name+requestUri;
                    response.sendRedirect(requestUri);
                    return true;
                }


                int index = requestUri.lastIndexOf("/");
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    HandlerLinkedData hld = new HandlerLinkedData();
                    hld.serveLinkeddata(baseRequest, response, dataset);
                    return true;
                }
            }

            //CASE 6 - DATASETS
            if (requestUri.endsWith("offers.html")) {
                logger.info("Serving the main offers " + request.getRequestURI());

                cd = ConditionalDatasets.getSelectedDataset();
                if (cd==null)
                {
                    response.sendRedirect("/404.html");
                    return false;
                }
                if  (requestUri.startsWith("/offers.html"))
                {
                    requestUri = cd.name+requestUri;
                    response.sendRedirect(requestUri);
                    return true;
                }


                int index = requestUri.lastIndexOf("/");
                if (index != -1 && index != 0) {
                    String dataset = requestUri.substring(1, index);
                    HandlerOffers hd = new HandlerOffers();
                    hd.serveOffers(baseRequest, request, response, dataset);
                    return true;
                }
            }

            if (requestUri.endsWith("account.html") || requestUri.endsWith("account")) {
                logger.info("Serving the account " + request.getRequestURI());
                HandlerAccount ha = new HandlerAccount();
                ha.serveAccount(baseRequest, request, response);
                return true;
            }

            if (requestUri.contains("manageren") || (requestUri.equals("/admin"))) {
                logger.info("Serving the manager " + request.getRequestURI());
                int index = requestUri.indexOf("/", 1);
                String dataset="";
                if (index != -1 && index != 0) {
                    dataset = requestUri.substring(1, index);
                    cd = ConditionalDatasets.getDataset(dataset);
                }
                if (cd==null)
                    cd = ConditionalDatasets.getSelectedDataset();
                if (dataset.isEmpty())
                    dataset= cd.name;
                HandlerManager hm = new HandlerManager();
                hm.serveManager(baseRequest, request, response, dataset);
                    return true;
            }

            if (requestUri.startsWith("/policy/") || requestUri.startsWith("/license/")) {
                logger.info("Serving a policy offer " + request.getRequestURI());
                int index = requestUri.indexOf("/", 1);
                if (index != -1 && index != 0) {
                    String policyuri = LDRConfig.getServer() + requestUri.substring(1, requestUri.length());
                    boolean human = true;
                    if (policyuri.endsWith(".ttl") || policyuri.endsWith(".rdf")) {
                        policyuri = policyuri.substring(0, policyuri.length() - 4);

                        human = false;
                    }
                    Policy policy = PolicyManagerOld.getPolicy(policyuri);
                    if (policy == null) {
                        response.sendRedirect("/404.html");
                    }
                    if (!human) {
                        String html = toTTL(policy);
                        response.getWriter().println(html);
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("text/turtle;charset=utf-8");
                    } else {
                        HandlerPolicy hm = new HandlerPolicy();
                        hm.servePolicy(policy, baseRequest, request, response);
                    }
                    return true;
                }
            }

            if (f.exists())
                serveGeneralFile(f, requestUri, request, response);
            else
                response.sendRedirect("/404.html");
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
        return true;
    }

    public String toTTL(Policy policy) {
//        Resource rpolicy = ODRLRDF.getResourceFromPolicy(policy);
        return ODRLRDF.getRDF(policy);

    }

    public boolean serveGeneralFile(File f, String requestUri, HttpServletRequest request, HttpServletResponse response) {
        if (!f.exists()) {
            logger.warn("The requested file "+f.getPath()+"does not exist");
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
            logger.warn(e.getMessage());
        }
        return true;
    }

    public static boolean contiene(List<Grafo> lg, String grafo) {
        for (Grafo g : lg) {
            if (g.getURI().equals(grafo)) {
                return true;
            }
        }
        return false;
    }

    public static Grafo getGrafo(List<Grafo> lg, String s) {
        for (Grafo g : lg) {
            if (g.getURI().equals(s)) {
                return g;
            }
        }
        return null;
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
            if (policy.hasPlay() && policy.hasFirstTarget(grafo.getURI())) {
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
            if (policyFromStore.hasPlay() && policyFromStore.isOpen()) {
                ar.ok = true;
                ar.policies.add(policyFromStore);
                return ar;
            } else if (policyFromStore.hasPlay()) {
                ar.policies.add(policyFromStore);
            }
        }



        return ar;

    }

    /**
     * Determina si la petición ha de ser servida a un humano o directamente el RDF
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
}
