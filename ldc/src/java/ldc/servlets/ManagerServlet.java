package ldc.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldc.Ldc;
import ldc.LdcConfig;
import ldc.Main;
import ldc.auth.Evento;
import ldc.model.ConditionalDataset;
import ldc.model.DatasetVoid;
import ldc.model.Grafo;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

/**
 *
 * @author vroddon
 */
public class ManagerServlet extends HttpServlet {

    static final Logger logger = Logger.getLogger("ldc");
    public static String selectedGrafo = "";

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        Enumeration appenders = logger.getAllAppenders();

        request.getSession().setAttribute("lasturi", request.getRequestURI());

        if (!appenders.hasMoreElements())
            Main.startLogger(); 
        logger.info("Manager Servlet sirviendo: " + uri);
        String contexto = LdcConfig.get("context", "");
        int inid = uri.indexOf(contexto+"/manage")+contexto.length()+8;
        int find = uri.length();
        String dataset = uri.substring(inid,find);
        logger.info("Dataset: " + dataset);
        ConditionalDataset cd = Ldc.getDataset(dataset);
        String body = serveManager(cd, request, response, dataset);
        if (body.equals("")) {
            return;
        }
        try {
            response.getWriter().print(body);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Serves the manager
     */
    public static String serveManager(ConditionalDataset cd, HttpServletRequest request, HttpServletResponse response, String dataset) {
        String lan = "en";
        String uri = request.getRequestURI();
        int index = uri.indexOf(dataset + "/", 0) + dataset.length() + 1;
        uri = uri.substring(index, uri.length());
        //Function to select a different graph
        if (uri.contains("selectGraph")) {
            selectedGrafo = selectGrafo(cd, request, response, dataset);
        }//function to selecte a different license for the given license
        else if (uri.contains("managePolicy")) {
            String res = managePolicy(cd, request, response, dataset);
            if (res.equals("ok")) {
                return "";
            }
        }
        String html = "";
        
        InputStream input = ManagerServlet.class.getResourceAsStream("template_manager.html");
        BufferedInputStream bis = new BufferedInputStream(input);
        try {
            html = IOUtils.toString(bis, "UTF-8");
        } catch (IOException ex) {
        }
        String context = LdcConfig.get("context","/ldc");
        html = html.replace("TEMPLATE_CONTEXTO", context);
        
        String tabla = getHTMLforTable(cd);
        html = html.replace("<!--TEMPLATETABLA-->", tabla);
        html = html.replaceAll("<!--TEMPLATEHERE3-->", selectedGrafo);
        html = html.replaceAll("<!--TEMPLATELICENSES-->", listLicenses());
        String even = Evento.getEventosHTML();
        html = html.replace("<!--TEMPLATEACCOUNTABILITY-->", even);
        return html; 
    }    
    /**
     * Service to manage the fake payments
     * @api {get} /{dataset}/service/selectGraph selectGraph
     * @apiName /selectGraph
     * @apiGroup ConditionalLinkedData
     * @apiVersion 1.0.0
     * @apiDescription Chooses a graph to be operated with </br>
     * <font color="red">Authorization</font>. Needs being authorized. Invoker must have made authentication, otherwise the unnamed user will be used
     *
     * @apiParam {String} grafo Graph to be selected
     * @apiSuccess {String} void Nothing is returned
     */
    public static String selectGrafo(ConditionalDataset cd, HttpServletRequest request, HttpServletResponse response, String dataset) {
        String sel = request.getParameter("grafo");
        try {
            sel = URLDecoder.decode(sel, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sel;
    }

    /**
     * Manages the policy, executing one of the defined operations
     * @api {get} /{dataset}/service/managePolicy managePolicy
     * @apiName /managePolicy
     * @apiGroup ConditionalLinkedData
     * @apiVersion 1.0.0
     * @apiDescription Chooses a graph to be operated with </br>
     * <font color="red">Authorization</font>. Needs being authorized. Invoker must have made authentication, otherwise the unnamed user will be used
     *
     * @apiParam {String} action Action to be executed. This is a string taking one these values: <br/>
     * <ul>
     * <li>"Add": Attaches a policy to a graph </li>
     * <li>"View": Displays a policy </li>
     * <li>"Remove": Removes the policy attachment to the graph </li>
     * <li>"RemoveAll": Removes all the policies attached to the graph </li>
     * <li>"Restore default": Restores the default policies for a demo</li>
     * @apiParam {String} selectedGrafo Graph to be operated on
     * @apiParam {String} licencia Policy to be operated on
     * @apiSuccess {String} void Nothing is returned
     */
    public static String managePolicy(ConditionalDataset cd, HttpServletRequest request, HttpServletResponse response, String dataset) {
        selectedGrafo = request.getParameter("selectedGrafo");
        
        if (selectedGrafo != null && !selectedGrafo.isEmpty()) {
            try {
                selectedGrafo = URLDecoder.decode(selectedGrafo, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        

        String licencia = request.getParameter("licencia");
        String action = request.getParameter("action");
        if (licencia != null) {
            try {
                licencia = URLDecoder.decode(licencia, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (action == null) {
            action = request.getParameter("xaction");
        }
        if (action == null) {
            action = request.getParameter("xaction2");
        }

        if (action != null && selectedGrafo != null && licencia != null) {
            logger.info("Accion " + action + " sobre " + selectedGrafo + "  licencia " + licencia);
            if (action.equals("Add")) {
                cd.getDatasetVoid().addLicense(selectedGrafo, licencia);
            } else if (action.equals("View")) {

            } else if (action.equals("Remove")) {
                logger.info("Action remove");
                cd.getDatasetVoid().removeLicense(selectedGrafo, licencia);
            }
        }

        if (action != null && action.equals("RemoveAll")) {
            logger.info("Action remove all");
            cd.getDatasetVoid().removeAllLicenses(selectedGrafo);
        }
        if (action != null && action.equals("downloadData")) {
            
            String DATASET = request.getParameter("dataset");
            logger.info(DATASET);
            cd = Ldc.getDataset(DATASET);
            
            String nombre = cd==null ? "unknown" : cd.name;
            logger.info("Action downloadData " + nombre);

            String filename = Ldc.getSelectedDataset().getDumpPath();
            File f = new File(filename);
            try {
                String token = FileUtils.readFileToString(f);
                response.setHeader("Content-Disposition","attachment;filename=data.nq");
                response.setContentType("application/n-quads");
                response.getWriter().print(token);
                response.setStatus(HttpServletResponse.SC_FOUND);
                return "ok";
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
        if (action != null && action.equals("downloadMetadata")) {
            logger.info("Action downloadMetadata");
            String sfolder = LdcConfig.get("datasetsfolder", "datasets");
            if (!sfolder.endsWith("/")) sfolder+="/";
            String filename = sfolder + Ldc.getSelectedDataset() + "/void.ttl";
//            String filename="datasets/" + ConditionalDatasets.getSelectedDataset().name + "/void.ttl";
            File f = new File(filename);
            try {
                String token = FileUtils.readFileToString(f);
                response.setHeader("Content-Disposition","attachment;filename=void.ttl");
                response.getWriter().print(token);
                response.setStatus(HttpServletResponse.SC_FOUND);
                return "ok";
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
        if (action != null && action.equals("Restore default")) {
            logger.info("Action restore default");
            try {
                response.sendRedirect("manage");
            } catch (IOException ex) {
            }
        }
        return "";
    }



    /**
     * Creates the HTML for th table with the given graphs.
     */
    public static String getHTMLforTable(ConditionalDataset cd) {
        String html = "";
        html += "<table class=\"table table-bordered table-hover table-condensed \">";

        html += "<thead><tr><th>Short</th><th>Comment</th><th>Triples</th><th>Policies</th><th>Actions</th></tr></thead>";

        List<ldc.model.Grafo> grafos = cd.getDatasetVoid().getGrafos();
        int cona = -1;
        for (ldc.model.Grafo g : grafos) {
            cona++;
            html += "<tr>";

//            html += "<td>" + g.getLabel() + "</td>";
            html += "<td>" + g.getName() + "</td>";
            html += "<td>" + g.getComment() + "</td>";
            html += "<td>" + g.getNumTriples() + "</td>";

            html += "<td>";
            List<Policy> lp = g.getPolicies();
            for (Policy p : lp) {
                String label = HandlerOffers.getPolicyHTMLTag(p, g);
                html += label;
            }
            html += "</td>";

            html += "<td>";
            String selg = g.getURI();

            
            //cd.name
            String context = LdcConfig.get("context","");
            String url = LdcConfig.get("server","") + context + "/api/managePolicy";
            String form = "<form name=\"input" + cona + "\" action=\"" + url + "\" method=\"get\">";
            form += "<input type=\"hidden\" id=\"selectedGrafo\" name=\"selectedGrafo\" value=\"" + selg + "\">";
            html += form;
            html += "<a href=\"#\" onclick=\"recipient='" + cona + "'\" class=\"btn btn-sm btn-default\" data-toggle=\"modal\" data-target=\"#largeModal\" data-miid=\"" + cona + "\"><span class=\"glyphicon glyphicon-plus-sign\"></span>Add policy</a>";
            html += "<button  name=\"action\" type=\"submit\" value=\"RemoveAll\" class=\"btn btn-default btn-sm\"><span class=\"glyphicon glyphicon-remove-sign\"></span> Remove policies</button>";
            html += "<input type=\"hidden\" name=\"licencia\" id=\"licencia" + cona + "\"></input>";
            html += "<input type=\"hidden\" name=\"xaction\" id=\"xaction" + cona + "\"></input>";
            String encouri="";
            try {
                encouri=URLEncoder.encode(cd.getURI(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
            }
            html += "<input type=\"hidden\" name=\"dataset\" id=\"dataset\" value=\"" +  encouri + "\"></input>";

            
            html += "</form>";
            html += "<button class=\"btn btn-default btn-sm\" name=\"setDescription\" onclick=\"setDescription('"+cd.getURI()+"','"+ g.getURI() +"')\"><span class=\"glyphicon glyphicon-tags\"></span> Describe</button> "; 
            
            
            html += "</td>";
            html += "</tr>\n";
        }
        html += "</table>";
//        html += "<button class=\"btn btn-default btn-sm\" name=\"setDescription\" onclick=\"showPartitionDlg('"+cd.getURI() +"');\"><span class=\"glyphicon glyphicon-scissors\"></span> New Partition</button> "; 
//        html += "<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Indexes this dataset\" id=\"boton5\" class=\"btn btn-primary\" href=\"javascript: submitIndex(\\'' + ds.id + '\\', \\'index\\')\"><span class=\"glyphicon glyphicon-refresh\"></span> Index</a>";
        html += "<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Rebases this dataset\" id=\"boton4\" class=\"btn btn-primary\" href=\"javascript: submitRebase('" +    cd.getURI() +"');\"><span class=\"glyphicon glyphicon-link\"></span> Rebase</a>";
        html += "<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Creates a new partition\" id=\"boton4\" class=\"btn btn-primary\" href=\"javascript: showPartitionDlg('"+ cd.getURI() +"');\"><span class=\"glyphicon glyphicon-scissors\"></span> New Partition</a>";
        html += "<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Indexes this dataset\" id=\"boton4\" class=\"btn btn-primary\" href=\"javascript: indexDataset('"+     cd.getURI() +"');\"><span class=\"glyphicon glyphicon-refresh\"></span> Index</a>";
        html += "<a style=\"margin:2px;\" data-toggle=\"tooltip\" title=\"Resets the graphs assignment\" id=\"boton4\" class=\"btn btn-primary\" href=\"javascript: resetDataset('"+     cd.getURI() +"');\"><span class=\"glyphicon glyphicon-flash\"></span> Reset</a>";
        
        
        




        //Restore default
//        String context = LdcConfig.get("context","");
//        String url = LdcConfig.get("server","")+context + "/api/managePolicy"; //+ cd.name
//        String form = "<form name=\"input\" action=\"" + url + "\" method=\"get\">";
//        form += "<input class=\"btn btn-default\"  name=\"action\" type=\"submit\" value=\"Restore default\"/>";
//        html += form+"</form>";

        //INDEXAR
/*        url = LDRConfig.getServer() + cd.name + "/manageren/managePolicy";
        form = "<form name=\"input\" action=\"" + url + "\" method=\"get\">";
        form += "<input class=\"btn btn-default\"  name=\"action\" type=\"submit\" value=\"Index\"/>";
        html += form+"</form>";
        */

        return html;
    }

    public static String listLicenses() {
        String html = "";
        List<Policy> policies = PolicyManagerOld.getPolicies();
        for (Policy policy : policies) {
            String opcion = policy.getLabel("en");
            html += "<option value=\"" + opcion + "\">" + opcion + "</option>";
        }
        return html;
    }

    /**
     * Obtiene el HTML que presenta una lista de grafos (como una lista de botones redondeados donde hay uno seleccionado)
     * @param selectedGrafo Idioma: en, es
     * @param lan Idioma: en, es
     */
    public static String getHTMLforGrafos(String dataset) {
        ConditionalDataset cd = Ldc.getDataset(dataset);
        String churro = "";
        List<Grafo> lg = cd.getDatasetVoid().getGrafos();
        String base = LdcConfig.get("server", "") + dataset;
        for (Grafo g : lg) {
            try {
                String urigrafo = URLEncoder.encode(g.getURI(), "UTF-8");
                String enlace = "";
                String label = g.getLabel();
                enlace = base + "/api/selectGraph?grafo=" + urigrafo;
                enlace = "<a href=\"" + enlace + "\">" + label + "</a>";
                if (g.getURI().equals(selectedGrafo)) {
                    churro += "<div style=\"border:2px solid; border-radius:25px; margin:10px; padding: 8px 8px 8px 8px; background:#0A0;\"><center>" + enlace + "</center></div>\n";
                } else {
                    churro += "<div style=\"border:2px solid; border-radius:25px; margin:10px; padding: 8px 8px 8px 8px; \"><center>" + enlace + "</center></div>\n";
                }
            } catch (Exception e) {
                logger.debug("Problema formateando un grafo");
            }
        }
        return churro;
    }

    public static String getHTMLforGrafo(Grafo grafo) {
        String churro = "";
        String labelGrafo = grafo.getLabel();
        DatasetVoid dv = grafo.getDatasetVoid();
        String dataset = dv.getConditionalDataset().name;

        churro = "<big><strong>" + labelGrafo + "</big></strong><br/><hr/>";

        String comentario = "<strong>Description</strong>: " + grafo.getComment();
        churro += comentario + "<br/>\n";
        String estadistica = "<strong>Number of triples</strong>: " + grafo.getNumTriples();
        churro += estadistica + "<br/>\n";
        //Dataset license
        String slicencia1 = "<strong>Dataset license</strong>: ";   //datasetlicense

        List<Policy> lp = grafo.getPolicies();
        for (Policy p : lp) {
            slicencia1 += p.getLabel("en") + " ";
        }


        //    slicencia1 += AssetManager.getPolicyLabelsForAssetURI(selectedGrafo);
        churro += slicencia1 + "<br/>\n";
        String slicencia = "";
        churro += slicencia;

        //dataset
        String url = LdcConfig.get("server","") + LdcConfig.get("context","") + "/api/managePolicy";
        String form = "<form name=\"input\" action=\"" + url + "\" method=\"get\">";
        String opciones = form + "<select name=\"licencia\" id=\"licencia\">";
        String defecto = AssetManager.getPolicyLabelForAssetURI(selectedGrafo);

        List<Policy> policies = PolicyManagerOld.getPolicies();
        for (Policy policy : policies) {
            String opcion = policy.getLabel("en");
            if (opcion.equals(defecto)) {
                opciones += "<option selected value=\"" + opcion + "\">" + opcion + "</option>";
            } else {
                opciones += "<option value=\"" + opcion + "\">" + opcion + "</option>";
            }
        }
        opciones += "</select>\n";
        churro += opciones;

        churro += "<input type=\"hidden\" id=\"dataset\" name=\"dataset\" value=\""+dv.getConditionalDataset().name+"\">";
        churro += "<input type=\"hidden\" id=\"selectedGrafo\" name=\"selectedGrafo\" value=\"<!--TEMPLATEHERE3-->\">";
        churro += "<input name=\"action\" type=\"submit\" value=\"Add\"/>";                   //Añadir
        churro += "<input name=\"action\" type=\"submit\" value=\"Remove\"/>";                //Quitar
        churro += "<input name=\"action\" type=\"submit\" value=\"View\"/>";                  //Ver
        churro += "<input name=\"action\" type=\"submit\" value=\"Restore default\"/>";
        return churro;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }    
}
