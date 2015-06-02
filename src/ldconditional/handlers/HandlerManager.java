package ldconditional.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
import ldconditional.LDRConfig;
import ldconditional.auth.Evento;
import ldconditional.model.DatasetVoid;
import ldconditional.model.Grafo;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

/**
 *
 * @author vroddon
 */
public class HandlerManager {

    static final Logger logger = Logger.getLogger(HandlerManager.class);
    public static String selectedGrafo = "";

    public void serveManager(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String dataset) {
        String body = "";
        response.setStatus(HttpServletResponse.SC_OK);
        String uri = request.getRequestURI();
        String lan = "en";
        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("lan")) {
                    lan = c.getValue();
                }
            }
        }
        ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
        body = HandlerManager.serveManager(cd, request, response, dataset);
        if (body.equals("")) {
            baseRequest.setHandled(true);
            return;
        }
        try {
            response.getWriter().print(body);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        baseRequest.setHandled(true);
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
                Policy policy = PolicyManagerOld.findPolicyByLabel(licencia);
                if (policy == null) {
                    policy = PolicyManagerOld.getPolicy(licencia);
                }
//                    cd.getDatasetVoid().viewLicense(policy);
//                    Policy policy = AssetManager.findPolicyByLabel(licencia);
                if (policy != null) {
                    try {
                        HandlerPolicy hp = new HandlerPolicy();
                        hp.servePolicy(policy, null, request, response);
                        //           response.sendRedirect(policy.uri);
                        response.setStatus(HttpServletResponse.SC_FOUND);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "ok";
                }
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
            logger.info("Action downloadData");
            String filename = ConditionalDatasets.getSelectedDataset().getDumpPath();
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
            String sfolder = LDRConfig.get("datasetsfolder", "datasets");
            if (!sfolder.endsWith("/")) sfolder+="/";
            String filename = sfolder + ConditionalDatasets.getSelectedDataset().name + "/void.ttl";
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
            cd.getDatasetVoid().restoreDefault();
            try {
                response.sendRedirect("manageren");
            } catch (IOException ex) {
            }
        }
        /*if (action != null && action.equals("Index")) {
            cd.getDatasetIndex().indexar();
//            cd.getDatasetVoid().recalcularTriples();
            try {
                response.sendRedirect("manageren");
            } catch (IOException ex) {
            }
        }*/

        return "";
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
        String templatefile = "./htdocs/template_manager.html";
        try {
            html = FileUtils.readFileToString(new File(templatefile));
            html = html.replace("<!--TEMPLATEFOOTER-->", FileUtils.readFileToString(new File("./htdocs/footer.html")));
        } catch (Exception e) {
            return "template not found";
        }
        String tabla = getHTMLforTable(cd);
        html = html.replace("<!--TEMPLATETABLA-->", tabla);
        html = html.replaceAll("<!--TEMPLATEHERE3-->", selectedGrafo);
        html = html.replaceAll("<!--TEMPLATELICENSES-->", listLicenses());
        String even = Evento.getEventosHTML();
        html = html.replace("<!--TEMPLATEACCOUNTABILITY-->", even);
        return html;
    }

    public static String getHTMLforTable(ConditionalDataset cd) {
        String servidor = LDRConfig.getServer();
        if (!servidor.endsWith("/"))
            servidor+="/";

        String html = "";
        html += "<table class=\"table table-bordered table-hover table-condensed \">";

        html += "<thead><tr><th>Short</th><th>Comment</th><th>Triples</th><th>Policies</th><th>Actions</th></tr></thead>";

        List<Grafo> grafos = cd.getDatasetVoid().getGrafos();
        int cona = -1;
        for (Grafo g : grafos) {
            cona++;
            html += "<tr>";

            html += "<td>" + g.getLabel() + "</td>";
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

            String url = servidor + cd.name + "/manageren/managePolicy";
            String form = "<form name=\"input" + cona + "\" action=\"" + url + "\" method=\"get\">";
            form += "<input type=\"hidden\" id=\"selectedGrafo\" name=\"selectedGrafo\" value=\"" + selg + "\">";
            html += form;
            html += "<a href=\"#\" onclick=\"recipient='" + cona + "'\" class=\"btn btn-sm btn-default btn-block\" data-toggle=\"modal\" data-target=\"#largeModal\" data-miid=\"" + cona + "\">Add policy</a>";
            html += "<button  name=\"action\" type=\"submit\" value=\"RemoveAll\" class=\"btn btn-default btn-sm btn-block\">Remove all</button>";
            html += "<input type=\"hidden\" name=\"licencia\" id=\"licencia" + cona + "\"></input>";
            html += "<input type=\"hidden\" name=\"xaction\" id=\"xaction" + cona + "\"></input>";

            
            html += "</form>";
            html += "<button class=\"btn btn-default btn-sm\" name=\"setDescription\" onclick=\"setDescription('"+cd.name+"','"+ g.getLabel() +"')\"><span class=\"glyphicon glyphicon-tags\"></span> Describe</button> "; 
            
            
            html += "</td>";
            html += "</tr>\n";
        }
        html += "</table>";




        //Restore default
        String url = servidor + cd.name + "/manageren/managePolicy";
        String form = "<form name=\"input\" action=\"" + url + "\" method=\"get\">";
        form += "<input class=\"btn btn-default\"  name=\"action\" type=\"submit\" value=\"Restore default\"/>";
        html += form+"</form>";

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
        ConditionalDataset cd = ConditionalDatasets.getDataset(dataset);
        String churro = "";
        List<Grafo> lg = cd.getDatasetVoid().getGrafos();
        String base = LDRConfig.getServer() + dataset;
        for (Grafo g : lg) {
            try {
                String urigrafo = URLEncoder.encode(g.getURI(), "UTF-8");
                String enlace = "";
                String label = g.getLabel();
                enlace = base + "/manageren/selectGraph?grafo=" + urigrafo;
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

        String url = LDRConfig.getServer() + dataset + "/manageren/managePolicy";
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

        churro += "<input type=\"hidden\" id=\"selectedGrafo\" name=\"selectedGrafo\" value=\"<!--TEMPLATEHERE3-->\">";
        churro += "<input name=\"action\" type=\"submit\" value=\"Add\"/>";                   //Añadir
        churro += "<input name=\"action\" type=\"submit\" value=\"Remove\"/>";                //Quitar
        churro += "<input name=\"action\" type=\"submit\" value=\"View\"/>";                  //Ver
        churro += "<input name=\"action\" type=\"submit\" value=\"Restore default\"/>";
        return churro;
    }
}
