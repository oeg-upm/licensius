package ldrauthorizer.ws;

//JAVA
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldrauthorizerold.GoogleAuthHelper;
import ldrauthorizer.ws.LDRServer;

//ODRLMODEL
import odrlmodel.LDRConfig;
import odrlmodel.Policy;
import odrlmodel.managers.HTMLODRLManager;
import odrlmodel.managers.PolicyManager;
import odrlmodel.rdf.ODRLRDF;

//JETTY
import org.apache.jena.riot.Lang;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Satisface las peticiones de políticas.
 * Whenever a policy (any URL starting with ldr/policy) is requested, this class plays a role
 * For example, http://salonica.dia.fi.upm.es/ldr/policy/cdaddba4-fc2e-4ee0-a784-e62f1db259bf
 * @author Victor
 */
public class CLDHandlerPolicy extends AbstractHandler {

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (!string.startsWith("/ldr/policy") && !string.startsWith("/ldr/license")) {
            return;
        }

        String target = request.getParameter("target");
        if (target == null) {
            target = "";
        } else {
            try {
                target = URLDecoder.decode(target, "UTF-8"); //YES
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //      baseRequest.setSessionManager(LDRAuthorizerView.globalSessionManager);
        //      HttpSession xexion = baseRequest.getSession();

        String token = "";
        response.setStatus(HttpServletResponse.SC_OK);
        String s2 = request.getRequestURI();
        String SERVER = LDRConfig.getServer();
        String urlcompleta = SERVER + s2;
        boolean turtle = false;
        if (urlcompleta.endsWith(".ttl")) {
            urlcompleta = urlcompleta.substring(0, urlcompleta.length() - 4);
            turtle = true;
        }
        String licensesfolder = LDRConfig.getLicensesfolder();
        PolicyManager.setPolicies(PolicyManager.readPolicies(licensesfolder)); //"../LDREditor/licenses"


        Policy policy = PolicyManager.getPolicy(urlcompleta);
        Portfolio portfolio = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
        for (Policy policyx : portfolio.policies) {
            if (policyx.getURI().equals(urlcompleta)) {
                policy = policyx;
                break;
            }
        }

        if (policy != null) {
            if (!target.isEmpty()) {
                policy.setTargetInAllRules(target);
            }
            if (LDRServer.isTurtle(request)) {
                token = ODRLRDF.getRDF(policy, Lang.TTL);
                response.setContentType("text/turtle");
            } else if (LDRServer.isRDFXML(request)) {
                token = ODRLRDF.getRDF(policy, Lang.RDFXML);
                response.setContentType("application/rdf+xml");
            } else // Hay que mostrar al humano una política
            {
                response.setContentType("text/html;charset=utf-8");
                //               view.addInfo("Servida al humano la licencia " + urlcompleta);
                String html = HTMLODRLManager.htmlPolicy(policy, "en");
                if (!target.isEmpty()) {
                    String urilicense = URLEncoder.encode(policy.getURI(), "UTF-8");
                    String uritarget = URLEncoder.encode(request.getParameter("target"), "UTF-8");
                    String urioferta = "/ldr/service/showPayment?policy=" + urilicense + "&target=" + uritarget;
                    String oferta = "<center><img width=\"64\" src=\"/ldr/img/carrito.png\"/><a href=\"" + urioferta + "\">purchase</a></center>";
                    html = html.replaceAll("<!--TEMPLATEHERE1-->", oferta);
                }
                token = html;
            }
            response.getWriter().print(token);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        baseRequest.setHandled(true);

    }

    public static String get() {
        return "";
    }
}
