package handlers;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import handlers.HandlerIndex;
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
import ldconditional.LDRConfig;
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
        body =HandlerManager.serveManager(cd, request, response, dataset);
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
     * Serves the manager
     */
    public static String serveManager(ConditionalDataset cd, HttpServletRequest request, HttpServletResponse response, String dataset) {
        String lan = "en";
        String uri = request.getRequestURI();

        int index = uri.indexOf(dataset+"/", 0)+ dataset.length()+1;

        uri = uri.substring(index, uri.length());

        //Function to select a different graph
        if (uri.contains("selectGrafo")) {
            String sel = request.getParameter("grafo");
            try {
                sel = URLDecoder.decode(sel, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //         view.addInfo("Seleccionado el grafo " + sel);
            selectedGrafo = sel;

        }//function to selecte a different license for the given license
        else if (uri.contains("changeLicense") ) {
            selectedGrafo = request.getParameter("selectedGrafo");
            try {
                selectedGrafo = URLDecoder.decode(selectedGrafo, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            String licencia = request.getParameter("licencia");
            String action = request.getParameter("action");
            try {
                licencia = URLDecoder.decode(licencia, "UTF-8");
                action = URLDecoder.decode(action, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (action != null && selectedGrafo != null && licencia != null) {
                logger.debug("Accion " + action + " sobre " + selectedGrafo + "  licencia " + licencia);
                if (action.equals("Add")) {
                    cd.getDatasetVoid().addLicense(selectedGrafo, licencia);
                    
                } else if (action.equals("View")) {
                    Policy policy = AssetManager.findPolicyByLabel(licencia);
                    if (policy != null) {
                        try {
                            response.sendRedirect(policy.uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        response.setStatus(HttpServletResponse.SC_FOUND);
                        return "";
                    }
                } else if (action.equals("Remove")) {
                    logger.info("Action remove");
                    cd.getDatasetVoid().removeLicense(selectedGrafo, licencia);
                } else if (action.equals("Restore default")) {
                    logger.info("Action restore default");
                    cd.getDatasetVoid().restoreDefault();
                } else {
                    AssetManager.setPolicyByLabel(selectedGrafo, licencia);
                }
            } 
        }
        String html = "";
        String templatefile = "./htdocs/manager.html";
        try {
            html = FileUtils.readFileToString(new File(templatefile));
            String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
            html = html.replace("<!--TEMPLATEFOOTER-->", footer);
        } catch (Exception e) {
            return "template not found";
        }
        String grafos = getHTMLforGrafos(dataset);
        html = html.replace("<!--TEMPLATEHERE1-->", grafos);
        Grafo graf = cd.getDatasetVoid().getGrafo(selectedGrafo);
        String grafinfo = (graf==null) ? "" : getHTMLforGrafo(graf);
        html = html.replace("<!--TEMPLATEHERE2-->", grafinfo);
        html = html.replaceAll("<!--TEMPLATEHERE3-->", selectedGrafo);

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
        String base = LDRConfig.getServer()+dataset;
        for (Grafo g : lg) {
            try {
                String urigrafo = URLEncoder.encode(g.getURI(), "UTF-8");
                String enlace = "";
                String label = g.getLabel();
                enlace = base + "/manageren/selectGrafo?grafo=" + urigrafo;
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
        for(Policy p : lp)
        {
            slicencia1+=p.getLabel("en")+" ";
        }


    //    slicencia1 += AssetManager.getPolicyLabelsForAssetURI(selectedGrafo);
        churro += slicencia1 + "<br/>\n";
        String slicencia = "";
        churro += slicencia;

        String url = LDRConfig.getServer()+dataset + "/manageren/changeLicense";

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
