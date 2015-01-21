package ldrauthorizer.ws;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


///JETTY
import ldrauthorizerold.GoogleAuthHelper;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Satisface las peticiones de recursos.
 * 
 * @author Victor
 */
public class CLDHandlerResource extends AbstractHandler {

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!string.startsWith("/ldr/resource")) {
            return;
        }
        String body = "";
        String uri = request.getRequestURI();
        
        
        Logger.getLogger("ldr").debug("Procesando el recurso que originalmente es " + uri);
        
        uri = uri.replaceAll(",", "%2C");
        
        Logger.getLogger("ldr").debug("Transformada la petición a " + uri);
        
        
       String lan="en";

        //1. OBTENEMOS LOS TRIPLES QUE NOS ESTÁN PIDIENDO
        List<LicensedTriple> lst = LDRServer.searchTriplesFor(uri);
        

        Logger.getLogger("ldr").debug("Hay un total de " + lst.size() + " triples para el recurso " + uri);

        Portfolio p = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
//        Portfolio p = (Portfolio)request.getSession().getAttribute("portfolio");
        if (p==null) p = new Portfolio();
        request.getSession().setAttribute("portfolio",p);
        
        //2. FILTRAMOS LOS QUE DEBERÍAN ESTAR OCULTOS
        lst = LicensedTriple.Authorize(lst, p);

        Logger.getLogger("ldr").debug("Se van a mostrar " + lst.size() + " triples para el recurso " + uri);
        
        
        String label1 = LDRServer.getLabelOf(uri);
        String type = LDRServer.getTipoOf(uri);
        String label = label1 + " (" + type + ")";
        if (!lst.isEmpty()) {
            //3. LOS FORMATEAMOS DEBIDAMENTE
            if (!LDRServer.isHuman(request)) {
                body = LDRServer.formatTTLTriples(label, lst);
                response.setContentType("text/turtle");
            } else {
                body = LDRServer.formatHTMLTriples(label, lst, lan);
                response.setContentType("text/html;charset=utf-8");
            }
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(body);
        baseRequest.setHandled(true);
        Evento.addEvento("Resource " + uri + " served", 0.0);

    }
}
