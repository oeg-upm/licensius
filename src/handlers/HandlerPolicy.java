package handlers;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldconditional.LDRConfig;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Request;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import ldrauthorizer.ws.Portfolio;
import ldrauthorizerold.GoogleAuthHelper;
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.HTMLODRLManager;
import odrlmodel.managers.PolicyManagerOld;
import org.apache.log4j.Logger;

/**
 *
 * @author vroddon
 */
public class HandlerPolicy {

    static final Logger logger = Logger.getLogger(HandlerPolicy.class);

    public void servePolicy(Policy policy, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
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
        String s2 = request.getRequestURI();
        String SERVER = LDRConfig.getServer();
        String urlcompleta = SERVER + s2;
        if (urlcompleta.endsWith(".ttl")) {
            urlcompleta = urlcompleta.substring(0, urlcompleta.length() - 4);
//            turtle = true;
        }


//        Policy policy = PolicyManagerOld.getPolicy(urlcompleta);
        Portfolio portfolio = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
        for (Policy policyx : portfolio.policies) {
            if (policyx.getURI().equals(urlcompleta)) {
                policy = policyx;
                break;
            }
        }

        if (policy == null) {
            try {
                response.sendRedirect("/404.html");
            } catch (Exception e) {
            }
        }


        response.setContentType("text/html;charset=utf-8");
        //               view.addInfo("Servida al humano la licencia " + urlcompleta);
        try {
            String html = HTMLODRLManager.htmlPolicy(policy, "en");
            String urilicense = URLEncoder.encode(policy.getURI(), "UTF-8");

            if (target != null && !target.isEmpty()) {
                String uritarget = URLEncoder.encode(request.getParameter("target"), "UTF-8");
                String urioferta = "/service/showPayment?policy=" + urilicense + "&target=" + uritarget;
                String oferta = "<center><img width=\"64\" src=\"/carrito.png\"/><a href=\"" + urioferta + "\">purchase</a></center>";
                html = html.replaceAll("<!--TEMPLATEHERE1-->", oferta);
            }
            response.getWriter().print(html);

        } catch (Exception e) {
            logger.warn("error con " + e.getMessage());
        }
    }
}
