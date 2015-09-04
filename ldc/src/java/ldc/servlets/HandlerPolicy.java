package ldc.servlets;

import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldc.LdcConfig;
import ldc.auth.GoogleAuthHelper;
import ldc.auth.Portfolio;
import org.eclipse.jetty.server.Request;
import odrlmodel.Policy;
import odrlmodel.managers.HTMLODRLManager;
import org.apache.log4j.Logger;

/**
 * This class serves policies
 * @author vroddon
 */
public class HandlerPolicy {

    static final Logger logger = Logger.getLogger(HandlerPolicy.class);

    /**
     * Target: where the policy applies
     * User: user that is invoking the call
     */
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
        String context = LdcConfig.get("context", "/ldc");
        String SERVER = LdcConfig.get("server","");
        String urlcompleta = SERVER + s2;
        if (urlcompleta.endsWith(".ttl")) {
            urlcompleta = urlcompleta.substring(0, urlcompleta.length() - 4);
        }
        
        String mail = GoogleAuthHelper.getMail(request);
        Portfolio portfolio = Portfolio.getPortfolio(mail);
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
        try {
            String html = HTMLODRLManager.htmlPolicy(policy, "en");
            String urilicense = URLEncoder.encode(policy.getURI(), "UTF-8");

            if (target != null && !target.isEmpty()) {
                String uritarget = URLEncoder.encode(request.getParameter("target"), "UTF-8");
                String urioferta = "/ldc/api/showPayment?policy=" + urilicense + "&target=" + uritarget;
                
                String oferta = "<center><img width=\"64\" src=\"" + context + "/img/carrito.png\"/><a href=\"" + urioferta + "\">purchase</a></center>";
                String price=policy.getPriceString();
                System.out.println(price);
                if (!price.isEmpty())
                    html = html.replaceAll("<!--TEMPLATEHERE1-->", oferta);
            }
            response.getWriter().print(html);

        } catch (Exception e) {
            logger.warn("Error con " + e.getMessage());
        }
    }
}
