package handlers;


import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Request;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import ldrauthorizer.ws.Portfolio;
import ldrauthorizerold.GoogleAuthHelper;
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;

/**
 * This Class handles the calls for managing the account.
 * https://console.developers.google.com/project/conditionallinkeddata/permissions
 * @author vroddon
 */
public class HandlerAccount {
    public void serveAccount(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            String body = FileUtils.readFileToString(new File("./htdocs/template_account.html"));
            try {
                String account = generateAccountHTML(request, response);
                body = body.replace("<!--TEMPLATEACCOUNT-->", account);


                String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
                body = body.replace("<!--TEMPLATEFOOTER-->", footer);


            } catch (Exception e) {
                e.printStackTrace();
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(body);
            baseRequest.setHandled(true);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return;
    }
    /**
     * Generates HTML for the Account management
     */
    private String generateAccountHTML(HttpServletRequest request, HttpServletResponse response) {
        String body = "";
        String usuario = (String) request.getSession().getAttribute("usuario");
        if (usuario != null && !usuario.isEmpty()) {
            body += usuario;
        } else {
            String login = "";
            try {

                String isLogout = request.getParameter("action");
                if (isLogout != null) {
                    System.out.println("logging out");
                    request.getSession().removeAttribute("state");
                    request.getSession().removeAttribute("google");
                    request.getSession().invalidate();
                }

                String reqState = request.getParameter("state");
                String reqCode = (String) request.getParameter("code");
                String sesState = (String) request.getSession().getAttribute("state");
                String sesGoogle = (String) request.getSession().getAttribute("google");
//        if (reqCode == null || reqState == null) {
                if (reqCode != null && reqState != null && reqState.equals(sesState)) // se acaba de logear
                {
                    final GoogleAuthHelper helper = new GoogleAuthHelper(sesState);
                    String json = helper.getUserInfoJson(request.getParameter("code"));
                    Object obj = JSONValue.parse(json);
                    JSONObject jobj = (JSONObject) obj;
                    String email = (String) jobj.get("email");
                    String name = (String) jobj.get("name");
                    String accountInfo="";
                    accountInfo+="<div class=\"well\">";
                    accountInfo += "Logged in as <strong>" + email + "</strong></br>User: <strong>" + name + "</strong>";
//                    accountInfo += " <a href=\"account.html?action=logout\">logout</a>";
                    accountInfo += "<br><a href=\"account.html?action=logout\" class=\"btn btn-default\" role=\"button\" >Logout</a>";
                    accountInfo +="</div>";
                    request.getSession().setAttribute("google", accountInfo);
                    login += accountInfo;
                    Portfolio portfolio = Portfolio.ReadPortfolio(GoogleAuthHelper.getMail(request));
                    Portfolio.setPortfolio(GoogleAuthHelper.getMail(request), portfolio);

                } else if (sesState == null || sesGoogle == null) {
                    final GoogleAuthHelper helper = new GoogleAuthHelper(sesState);
                    String neochurro = "";
                    neochurro += "Logged in as <strong>guest</strong></br>";
                    neochurro += "<a href='" + helper.buildLoginUrl() + "'><img src=\"/img/googleplus.png\" height=40/> </a>";
                    //       request.getSession().setAttribute("state", helper.getStateToken());
                    login += neochurro;
                } else if (sesState != null) {
                    String accountInfo = (String) request.getSession().getAttribute("google");
                    login += sesGoogle;
                }

                String str = "";
                str += "<hr/>";

                String email = GoogleAuthHelper.getMail(request);
                Portfolio portfolio = Portfolio.getPortfolio(email);
                if (portfolio == null || portfolio.policies == null || portfolio.policies.isEmpty()) {
                    str += "You have purchased no permissions.<br/>";
                    //       str+=StringUtils.readTestFile();
                    login += str;
                } else {
                    str += "<p>You have purchased the following permissions:</p><div class=\"well\">";

//                    str += "<table border=\"1\" style=\"width:300px;\" ><tr style=\"font-weight:bold;\"><td >Price</td><td>Asset</td><td>License</td></tr>";
                    str += "<table class=\"table\"><thead><tr><th>Price</th><th>Asset</th><th>License</th></tr></thead>";

                    for (Policy policy : portfolio.policies) {
                        String starget = policy.getFirstTarget();
                        Asset a = AssetManager.getAsset(starget);
                        if (a != null) {
                            starget = a.getLabel("en");
                        }
//                        String llic = " <a href=\"" + policy.getURI() + "\">(view)</a>";

                        String llic = HandlerOffers.getPolicyHTMLTag(policy, null);

                        String precio = String.format("%.02f %s", policy.getFirstPrice(), policy.getFirstCurrency());
                        str += "<tr><td>" + precio + "</td><td>" + starget + "</td><td>" + llic + "</td></tr>";
                    }
                    str += "</table></div>";
                    login += str;
                    login += "<a href=\"service/resetPortfolio\" class=\"btn btn-default\" role=\"button\" >Reset portfolio</a>";
                    login += "<a href=\"service/exportPortfolio?signed=false\" class=\"btn btn-default\" role=\"button\" >View portfolio</a>";
                    login += "<a href=\"service/exportPortfolio?signed=true\" class=\"btn btn-default\" role=\"button\" >View signed portfolio</a>";
                    //login += "<a href=\"service/resetPortfolio\"><button>Reset portfolio</button> </a>";
                    //login += "<a href=\"service/exportPortfolio?signed=false\"><button>View portfolio</button></a>";
                    //login += "<a href=\"service/exportPortfolio?signed=true\"><button>View signed portfolio</button></a>";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            login += "";
            body += login;
        }
        return body;
    }
}
