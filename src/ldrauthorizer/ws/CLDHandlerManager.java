package ldrauthorizer.ws;

import java.io.File;
import java.net.URLDecoder;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Manager of the control panel of resources and licenses
 * @author Victor
 */
public class CLDHandlerManager extends AbstractHandler {

    static List<String> grafos = new ArrayList();
    public static String selectedGrafo = "";

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!string.startsWith("/ldr/manager") && !string.startsWith("/ldr/manageren") && !string.startsWith("/ldr/en/manager")) {
            return;
        }
        String body = "";
        response.setStatus(HttpServletResponse.SC_OK);
//        LDRAuthorizerView view = (LDRAuthorizerView) LDRAuthorizerApp.getApplication().getMainView();
        String uri = request.getRequestURI();
//      String lan = uri.startsWith("/ldr/manageren") ? "en" : "es";
        String lan = "en";
        Cookie cookies[] = request.getCookies();
        if (cookies!=null)
        {
            for (Cookie c : cookies) {
                if (c.getName().equals("lan")) {
                    lan = c.getValue();
                }
            }
        }
        if (string.startsWith("/ldr/manageren") || (string.startsWith("/ldr/en/manager"))) {
            lan = "en";
        }
        body = CLDHandlerManager.serveManager(request, response, lan);
        if (body.equals("")) {
            baseRequest.setHandled(true);
            return;
        }
        //      view.addInfo("Servida la info del panel de control " + uri);
        response.getWriter().print(body);
        baseRequest.setHandled(true);

    }

    /**
     * Obtiene la lista interna de grafos disponibles (según ofrecidos por el AssetManager)
     * Resetea el grafo seleccionado al grafo inicial
     */
    public static void refreshGrafos() {
        grafos = AssetManager.getGrafosURIs();
        selectedGrafo = grafos.isEmpty() ? "" : grafos.get(0);
    }

    /**
     * Processes the control panel requests
     */
    public static String serveManager(HttpServletRequest request, HttpServletResponse response, String lan) {
//        LDRAuthorizerView view = (LDRAuthorizerView) LDRAuthorizerApp.getApplication().getMainView();
        String uri = request.getRequestURI();
        refreshGrafos();

        //Function to select a different graph
        if (uri.startsWith("/ldr/manager/selectGrafo") || uri.startsWith("/ldr/manageren/selectGrafo")) {
            String sel = request.getParameter("grafo");
            try {
                sel = URLDecoder.decode(sel, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //         view.addInfo("Seleccionado el grafo " + sel);
            selectedGrafo = sel;

        }//function to selecte a different license for the given license
        else if (uri.startsWith("/ldr/manager/managePolicy") || uri.startsWith("/ldr/manageren/managePolicy")) {
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
                Logger.getLogger("ldr").debug("Accion " + action + " sobre " + selectedGrafo + "  licencia " + licencia);
                if (action.equals("Add")) {
//                    AssetManager.addPolicyByTitle(selectedGrafo, licencia);
                    AssetManager.addPolicyByLabel(selectedGrafo, licencia);
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
                    AssetManager.removePolicyByLabel(selectedGrafo, licencia);
                } else if (action.equals("Restore default")) {
                    AssetManager.restoreDefaultPolicyAssignment();
                } else {
                    AssetManager.setPolicyByLabel(selectedGrafo, licencia);
                }
            }
            //}
            //  }
        }
        String html = "";
        String templatefile = "";


        if (lan.equals(
                "es")) {
            templatefile = "../htdocs/manager.html";

        } else {
            templatefile = "../htdocs/en/manager.html";
        }



        try {
            html = FileUtils.readFileToString(new File(templatefile));

            String footer = FileUtils.readFileToString(new File("../htdocs/en/footer.html"));
            html = html.replace("<!--TEMPLATEFOOTER-->", footer);

        } catch (Exception e) {
            return "page not found";
        }
        ;

        String grafos = WebPolicyManager.getHTMLforGrafos(lan);
        html = html.replace("<!--TEMPLATEHERE1-->", grafos);
        String grafinfo = WebPolicyManager.getHTMLforGrafo(selectedGrafo, lan);
        html = html.replace("<!--TEMPLATEHERE2-->", grafinfo);
        html = html.replaceAll("<!--TEMPLATEHERE3-->", selectedGrafo);

        return html;
    }
}
