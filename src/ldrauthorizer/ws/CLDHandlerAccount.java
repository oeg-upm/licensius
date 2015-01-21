package ldrauthorizer.ws;

//JAVA
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

//LDR
import ldrauthorizerold.GoogleAuthHelper;

///JETTY
import odrlmodel.Asset;
import odrlmodel.Policy;
import odrlmodel.managers.AssetManager;
import odrlmodel.managers.StringUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.json.simple.*;

/**
 * This is the handler for the account manager.
 * http://ocpsoft.org/java/setting-up-google-oauth2-with-java/#section-7
 * @author Victor
 */
class CLDHandlerAccount extends AbstractHandler {

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!string.startsWith("/ldr/account.html") && !string.startsWith("/ldr/en/account.html")) {
            return;
        }
        String body = FileUtils.readFileToString(new File("../htdocs/en/account.html"));
        try {
            String footer = FileUtils.readFileToString(new File("../htdocs/en/footer.html"));
            body = body.replace("<!--TEMPLATEFOOTER-->", footer);

            String account = generateHTML(request, response);
            body = body.replace("<!--TEMPLATEACCOUNT-->", account);

        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().print(body);
        baseRequest.setHandled(true);
        return;
    }

    
    
    
    
    /**
     * Generates HTML for the Account management
     */
    private String generateHTML(HttpServletRequest request, HttpServletResponse response) {
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
                String sesGoogle = (String)request.getSession().getAttribute("google");
//        if (reqCode == null || reqState == null) {                
                if (reqCode != null && reqState != null && reqState.equals(sesState)) // se acaba de logear
                {
                    final GoogleAuthHelper helper = new GoogleAuthHelper(sesState);
                    String json = helper.getUserInfoJson(request.getParameter("code"));
                    Object obj = JSONValue.parse(json);
                    JSONObject jobj = (JSONObject) obj;
                    String email = (String) jobj.get("email");
                    String name = (String) jobj.get("name");
                    String accountInfo = "Logged in as <strong>" + email + "</strong></br>User: <strong>" + name + "</strong>";
                    accountInfo += " <a href=\"/ldr/en/account.html?action=logout\">logout</a>";
                    request.getSession().setAttribute("google", accountInfo);
                    login += accountInfo;
                    Portfolio portfolio = Portfolio.ReadPortfolio(GoogleAuthHelper.getMail(request));
                    Portfolio.setPortfolio(GoogleAuthHelper.getMail(request), portfolio);
                    
                }
                else if (sesState == null || sesGoogle==null) {
                    final GoogleAuthHelper helper = new GoogleAuthHelper(sesState);
                    String neochurro = "";
                    neochurro += "Logged in as <strong>guest</strong></br>";
                    neochurro += "<a href='" + helper.buildLoginUrl() + "'><img src=\"/ldr/img/googleplus.png\" height=40/> </a>";
             //       request.getSession().setAttribute("state", helper.getStateToken());
                    login += neochurro;
                } 
                else if (sesState!=null)
                {
                    String accountInfo = (String)request.getSession().getAttribute("google");
                    login += sesGoogle;
                }

                String str="";
                str+="<hr/>";
                 
                String email=GoogleAuthHelper.getMail(request);
                Portfolio portfolio = Portfolio.getPortfolio(email);
                if (portfolio==null || portfolio.policies==null || portfolio.policies.isEmpty())
                {
                    str+="You have purchased no permissions.<br/>";
              //       str+=StringUtils.readTestFile();
                    login+=str;
                }
                else
                {
                    str+="You have purchased the following permissions:<br/>";

                    str+="<table border=\"1\" style=\"width:300px;\" ><tr style=\"font-weight:bold;\"><td >Price</td><td>Asset</td><td>License</td></tr>";
                    
                    for(Policy policy : portfolio.policies)
                    {
                        String starget=policy.getFirstTarget();
                        Asset a=AssetManager.getAsset(starget);
                        if (a!=null)
                            starget=a.getLabel("en");
                        String llic=" <a href=\""+ policy.getURI()+"\">(view)</a>";
                        String precio =String.format("%.02f %s",policy.getFirstPrice(), policy.getFirstCurrency());
                        str+="<tr><td>"+precio+"</td><td>"+starget+"</td><td>"+policy.getLabel("en")+llic+"</td></tr>";
                    }
                   str+="</table>";
                    

                  /*for(Policy policy : portfolio.policies)
                    {
//                        if(policy.isPerTriple())
                        {
                            str+="<p style=\"border:1px solid gray;width=250px;float:left\">";
                            str+="<img src=\"/ldr/img/llave16.png\"/>";
                            str+=policy.getLabel("en");
                            str+="</p>";
                            str+=" <a href=\""+ policy.getURI()+"\">view</a>";
                            str+="<br/>";
                        }
                    }*/
                    login+=str;
                    login+="<a href=\"/ldr/service/resetPortfolio\"><button>Reset portfolio</button> </a>";
                    login+="<a href=\"/ldr/service/exportPortfolio?signed=false\"><button>View portfolio</button></a>";
                    login+="<a href=\"/ldr/service/exportPortfolio?signed=true\"><button>View signed portfolio</button></a>";
                    
                    
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
