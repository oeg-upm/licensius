package ldrauthorizer.ws;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Satisface las peticiones al llegar a la página principal
 * - O bien llega a /ldr (o /ldr/) en cuyo caso se mira el idioma del navegador
 * - O bien llega a /ldr/index.html 
 * - O bien llega a /ldr/en/index.html
 * @author Victor
 */
public class CLDHandlerIndex extends AbstractHandler {

    
    
    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!string.equals("/ldr/") && !string.equals("/ldr") && !string.equals("/ldr/index.html") && !string.equals("/ldr/en/index.html") )  {
            return;
        }
        String body="";
        String s = request.getPathInfo();

        if (s.equals("/"))
        {
             Logger.getLogger("ldr").info("Se ha entrado en el raíz");
        }
        
        if (s.equals("/ldr/index.html")) {
            body = FileUtils.readFileToString(new File("../htdocs/index.html"));
      //      view.addInfo("Servida al humano la pagina de presentacion");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(body);
      //      Cookie cookie = new Cookie("lan", "es");
      //      response.addCookie(cookie);
            baseRequest.setHandled(true);
            return;
        }
        if (s.equals("/ldr/en/index.html")) {
            body = FileUtils.readFileToString(new File("../htdocs/en/index.html"));
            
            String footer = FileUtils.readFileToString(new File("../htdocs/en/footer.html"));
            body=body.replace("<!--TEMPLATEFOOTER-->", footer);
            
      //      view.addInfo("Servida al humano la pagina de presentacion en inglés");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(body);
     //       Cookie cookie = new Cookie("lan", "en");
     //       response.addCookie(cookie);
     //       System.out.println(body);
            baseRequest.setHandled(true);
            return;
        }
       
       
        
        
        //SI ENTRAMOS EN LA PÁGINA PRINCIPAL, HAY QUE REDIRIGIR EN FUNCION DEL IDIOMA
        if (!isSpanish(request)) {
            try {
                s = s.replace("/ldr/", "/ldr/en/index.html");
                response.sendRedirect(s);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (isSpanish(request)) {
            try {
                s = s.replace("/ldr/", "/ldr/index.html");
                response.sendRedirect(s);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        response.setStatus(HttpServletResponse.SC_FOUND);
        baseRequest.setHandled(true);
    }
    
    
    /**
     * Determina si la petición es de un navegador en español o no, 
     * considering the "Accept-Language" header element.
     * @param request
     */
    public static boolean isSpanish(HttpServletRequest request) {
        Enumeration en = request.getHeaderNames();
        while (en.hasMoreElements()) {
            String param = (String) en.nextElement();
            if (param.equals("Accept-Language")) {
                Enumeration<String> values = request.getHeaders(param);
                while (values.hasMoreElements()) {
                    String value = values.nextElement();
                    if (value.startsWith("es")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }    
}
