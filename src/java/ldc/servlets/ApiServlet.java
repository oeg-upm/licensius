package ldc.servlets;

import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldc.Ldc;
import ldc.LdcConfig;
import ldc.Main;
import ldc.auth.GoogleAuthHelper;
import ldc.auth.Portfolio;
import ldc.model.ConditionalDataset;
import ldc.auth.SQLite;
import static ldc.servlets.ManagerServlet.logger;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

//LOG4J
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

/**
 *
 * @author vroddon
 */
public class ApiServlet extends HttpServlet {

    static final Logger logger = Logger.getLogger("ldc");

    /*
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Enumeration appenders = logger.getAllAppenders();
        if (appenders==null || !appenders.hasMoreElements())
            Main.startLogger();
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String uri = request.getRequestURI();
        logger.info("Api servlet procesando: " + uri);
        String txt = "";
        String google = (String) request.getSession().getAttribute("google");
        logger.trace("Usuario: " + google);
        
        ConditionalDataset cd = null;
        String dsx = request.getParameter("dataset");
        if (dsx!=null)
        {
            dsx = URLDecoder.decode(dsx, "UTF-8");
            cd = Ldc.getDataset(dsx);
        }
        String lasturi = (String) request.getSession().getAttribute("lasturi");
        if (cd==null)
        {
            if (lasturi!=null && !lasturi.isEmpty())
            {
                int index = lasturi.lastIndexOf("/");
                if (index!=-1)
                {
                    String ds = lasturi.substring(index+1, lasturi.length());
                    cd = Ldc.getDataset(ds);
                }
            }
        }   
        if (cd==null)
            cd = Ldc.getSelectedDataset();
        logger.trace("Last URI: " + lasturi);
        String referer = request.getHeader("referer");
        logger.trace("Referrer: " + referer);
        String dname = cd==null ? "noname" : cd.name;
        logger.info("Dataset loaded: " + dname);
        String trace = LdcConfig.get("store", "filesystem");
        logger.info(uri);

        if (uri.endsWith("/api/getDatasets")) {
            response.setContentType("application/json");
            String s = Api.getDatasets(google);
            out.print(s);
            response.setStatus(HttpServletResponse.SC_OK);
        }

        if (uri.endsWith("/api/getDatasetMetadata")) {
            String ds = request.getParameter("dataset");
            if ((google == null) || google.isEmpty()) {

            }
            response.setContentType("application/json");
            String s = Api.getDataset(ds);
            out.print(s);
            response.setStatus(HttpServletResponse.SC_OK);
        }

        //Se invoca cuando se crea un nuevo dataset o se modifica los metadatos de uno exisente
        if (uri.endsWith("/api/setDatasetMetadata")) {
            String datas = request.getParameter("dataset");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String newlabel = request.getParameter("newlabel");
            logger.info("Editando metadatos del dataset " + datas);
            ConditionalDataset ds = Ldc.getDataset(datas);
            if (ds == null || ds.dsVoid==null) { logger.info("A new dataset is being created. " + datas);
                ds = Ldc.addDataset(newlabel);
                if (ds == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                ds.getDatasetVoid().init();
            }
            ds.getDatasetVoid().setProperty("http://purl.org/dc/terms/title", title);
            ds.getDatasetVoid().setProperty("http://www.w3.org/2000/01/rdf-schema#comment", description);
            SQLite.setOwner(google, ds.name);
            
            response.setStatus(HttpServletResponse.SC_OK);
            return;

        }

        if (uri.endsWith("/api/datasetRemove")) {
            String datas = request.getParameter("dataset");
            Ldc.deleteDataset(datas);
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (uri.endsWith("/api/datasetUpload")) {
            boolean ok = Api.uploadFile(request, "/data.nq");
            if (!ok) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            return;
        }

        if (uri.endsWith("/api/logoUpload")) {
            logger.info("logoUpload");
            boolean ok = Api.uploadFile(request, "/logo.png");
            return;
        }

        if (uri.endsWith("/api/datasetRebase")) {
            logger.info("datasetRebase");
            String datas = request.getParameter("dataset");
            String datasuri = request.getParameter("uri");
            ConditionalDataset ds = Ldc.getDataset(datas);
            if (ds == null) {
                logger.info("No se pudo hacer rebase porque no había parámetro dataset");
                return;
            }
            boolean ok = ds.rebase(datasuri);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        //http://localhost:8080/ldc/api/getResources?current=1&rowCount=10&sort[resource]=desc&searchPhrase=
        //http://tbx2rdf.lider-project.eu/converter/service/getResources?current=1&rowCount=10&sort[resource]=desc&searchPhrase=
        if (uri.endsWith("/api/suggestURI")) {
            String dataset = request.getParameter("dataset");
            ConditionalDataset ds = Ldc.getDataset(dataset);
            if (ds==null)
            {
                out.print("http://");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/html");
                return;
            }
            String s = ds.getSuggestedURI();
            logger.info("Se va a devolver " + s);
            out.print(s);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
        }

        
        if (uri.endsWith("/api/viewPolicy")) {
            String licencia = request.getParameter("policy");
            String target = request.getParameter("target");
            String s2 = request.getRequestURI();
            String context = LdcConfig.get("context", "/ldc");
            String server = LdcConfig.get("server","");
            String urlcompleta = server + s2;
            String user = getUser(request);
            Policy policy = PolicyManagerOld.findPolicyByLabel(licencia);
            if (policy == null) {
                policy = PolicyManagerOld.getPolicy(licencia);
            }
            if (policy != null) {
                
                Portfolio portfolio = Portfolio.getPortfolio(user);
                String html =  Api.servePolicy(policy, target, portfolio);
                out.print(html);
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }            
            return;
        }
        
        if (uri.endsWith("/api/getResources")) {
            String offset = request.getParameter("current");
            String limit = request.getParameter("rowCount");
            String searchFrase = request.getParameter("searchPhrase");
            int current=1;
            int ilimit=10;
            try{current = Integer.parseInt(offset);}catch(Exception e){}
            try{ilimit = Integer.parseInt(limit);}catch(Exception e){}
            
            if (cd==null)
                return;
            logger.info("Serving "+ilimit+" resources of dataset " + cd.name + " from number" + offset);
            
            String s = Api.getSomeEntities(cd==null ? "" : cd.name, current, ilimit, searchFrase);
            out.print(s);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
        }
        
        if (uri.endsWith("/api/logout")) {
            request.getSession().setAttribute("google","");
            response.sendRedirect("/ldc/");
            return;
        }
        //http://localhost:8080/ldc/api/resetGraphsInDataset?dataset=geo
        if (uri.endsWith("/api/resetGraphsInDataset")) {
            logger.info("resets graphs in dataset");
//            String datas = request.getParameter("dataset");
//            ConditionalDataset cd = Ldc.getDataset(datas);
            cd.resetGraphsInDataset();
            return;
        }
        if (uri.endsWith("/api/resetDataset")) {
            String datas = request.getParameter("dataset");
            logger.info("Reset dataset " + datas);
            ConditionalDataset ds = Ldc.getDataset(datas);
            ds.resetGraphsInDataset();
            return;
        }
        
        if (uri.endsWith("/api/datasetIndex")) {
            logger.info("datasetIndex");
            String datas = request.getParameter("dataset");
            ConditionalDataset ds = Ldc.getDataset(datas);
            boolean ok = Api.datasetIndex(ds);
            return;
        }
        
        if (uri.endsWith("/api/setPartition")) {
            String dataset = request.getParameter("dataset");
            String s = request.getParameter("s");
            String p = request.getParameter("p");
            String o = request.getParameter("o");
            String g = request.getParameter("g");
            String e = request.getParameter("e");
            logger.info("setPartition: " + dataset+" "+ s+ " " + p + " " + o+" "+ g+" " +e);
//            ConditionalDataset cd = Ldc.getDataset(dataset);
            if (cd==null)
            {
                Api.serveError(request, response);
                return;
            }
            cd.createGraph(s,p,o,g,e);
            
            Api.datasetIndex(cd);
            
            return;
            
        }
        if (uri.endsWith("/api/signup")) {
            String u = request.getParameter("user");
            String p = request.getParameter("password");
            SQLite.addUser(u, p);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
        }
        
        if (uri.endsWith("/api/downloadDataset")) {
            String DATASET = request.getParameter("dataset");
            logger.info(DATASET);
            cd = Ldc.getDataset(DATASET);
            String filename =cd.getDumpPath();
            File f = new File(filename);
            try {
                String token = FileUtils.readFileToString(f);
                response.setHeader("Content-Disposition","attachment;filename=data.nq");
                response.setContentType("application/n-quads");
                response.getWriter().print(token);
                response.setStatus(HttpServletResponse.SC_FOUND);
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
            return;
        }
        if (uri.endsWith("/api/downloadVoid")) {
            String DATASET = request.getParameter("dataset");
            logger.info(DATASET);
            cd = Ldc.getDataset(DATASET);
            String filename =cd.getVoidPath();
            File f = new File(filename);
            try {
                String token = FileUtils.readFileToString(f);
                response.setHeader("Content-Disposition","attachment;filename=void.ttl");
                response.setContentType("application/n-quads");
                response.getWriter().print(token);
                response.setStatus(HttpServletResponse.SC_FOUND);
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
            return;
        }
                
        
        
        if (uri.contains("managePolicy")) {
            String dataset = request.getParameter("dataset");
            dataset = URLDecoder.decode(dataset,"UTF-8");
//            ConditionalDataset cd = Ldc.getDataset(dataset);
            String res = ManagerServlet.managePolicy(cd, request, response, dataset);
            if (res.equals("ok")) {
                return;
            }
            String contexto = LdcConfig.get("context", "/ldc");
            String refresco = contexto+"/manage/"+cd.name;
            response.sendRedirect(refresco);           
        }        
        
        //http://localhost:8080/ldc/api/graphDescribe
        if (uri.contains("/api/graphDescribe")) {
            String datas = request.getParameter("dataset");
            String grafo = request.getParameter("grafo");
            grafo = URLDecoder.decode(grafo);
            datas = URLDecoder.decode(datas, "UTF-8");
            
            String description = request.getParameter("description");
            logger.info("graphDescribe " + datas + " " + grafo +" "+description);
            ConditionalDataset ds = Ldc.getDataset(datas);
            if (ds==null)
                return;
            try{
                ds.getDatasetVoid().describeGrafo(grafo,RDFS.comment.getURI(), description);
//                ds.getDatasetVoid().setMetadataLiteral(grafo, description);
            }catch(Exception eas)
            {
                logger.warn("Graph could not be described");
            }
            ds.getDatasetVoid().write();
        }        
        
        if (uri.endsWith("/api/getUser")) {
            out.print(getUser(request));
            
/*            logger.trace("Invoked " + uri);
            out.print(google);
            logger.trace("Returned " + google);
            */
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
        }
        if (uri.contains("/api/fakePayment")) {
            String paramPago = request.getParameter("payment");
            String paramPolicy = request.getParameter("policy");
            String paramName = request.getParameter("target");
            try {
                paramPolicy = URLDecoder.decode(paramPolicy, "UTF-8");
                paramName = URLDecoder.decode(paramName, "UTF-8");
                paramPago = URLDecoder.decode(paramPago, "UTF-8"); //YES
            } catch (Exception e) {
                e.printStackTrace();
            }
            String user = getUser(request);
            Portfolio portfolio = Api.fakePayment(user, paramPago, paramPolicy, paramName);
            if (portfolio!=null)
                request.getSession().setAttribute("portfolio", portfolio);
            response.sendRedirect("/ldc/account");
        }
        
        if (uri.contains("/api/showPayment")) {
            logger.info("showPayment");
            String policyName = request.getParameter("policy");
            String targetName = request.getParameter("target");
            try {
                policyName = URLDecoder.decode(policyName, "UTF-8");
                targetName = URLDecoder.decode(targetName, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String body = Api.getHTMLPayment(policyName, targetName);
            out.print(body);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");
            return;
        }
        
        
        if (uri.endsWith("/api/login")) {
            String u = request.getParameter("user");
            String p = request.getParameter("password");
            boolean ok = SQLite.authenticate(u, p);
            if (!ok) {
                out.print("403");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/html");
                return;
            } else {
                out.print("200");
                request.getSession().setAttribute("google", u);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/html");
                return;
            }
        }

    }

    public String getUser(HttpServletRequest request)
    {
        String google = (String) request.getSession().getAttribute("google");
        if (google==null)
        {
            String email = GoogleAuthHelper.getMail(request);
            logger.info("no ha iniciado sesión pero sí goole " + email);
        }
        return google;
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

}
