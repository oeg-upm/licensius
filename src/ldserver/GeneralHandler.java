package ldserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Handler that processes the rest of resources
 * 
 * http://conditional.linkeddata.es/iate                              -- intro
 * http://conditional.linkeddata.es/iate/resource/sparql              -- sparql endpoint
 * http://conditional.linkeddata.es/iate/resource/word                -- linked data
 * http://conditional.linkeddata.es/iate/service?listResources        -- (depende)
 * @author vroddon
 */
public class GeneralHandler extends AbstractHandler {

    static final Logger logger = Logger.getLogger(GeneralHandler.class);

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled()) {
            return;
        }
        response.setContentType("text/html;charset=utf-8");
        String folder = string;
        boolean ok = serveStandardFile(baseRequest, request, response, folder);
        if (ok) {
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
        } else //error 404
        {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("404 not found");
            baseRequest.setHandled(true);
        }
    }

    /**
     * Sirve un archivo estándar
     */
    public boolean serveStandardFile(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String folder) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        boolean binario = false;
        try {
                
            String token = "";
            if (request.getRequestURI().endsWith(".css"))
                token=token+"";
            if (request.getRequestURI().endsWith(".png"))
                token=token+"";
            
            String requestUri = request.getRequestURI();
  //          String referer = baseRequest.getHeader("Referer");
            String suri = requestUri;
/*            if (referer != null ) {
                URI uri = new URI(referer);
                String path = uri.getPath();
                String local=baseRequest.getLocalAddr();
               String s1=baseRequest.getPathTranslated();
               String s2=baseRequest.getPathInfo();
                int i=path.lastIndexOf(".");
            }*/
 //               logger.info("Referrer:" + referer + " Path:" +path + " RequestURI:"+requestUri);
 //               suri = "./datasets" + path + requestUri;
 //           } else {
                suri = "./datasets" + requestUri;
//                if (suri.equals("./datasets/")) {
//                    requestUri = "./datasets/index.html";
//                    suri = requestUri;
//                }
//            }
           File f = new File(suri);
            if (!f.exists()) {
                return false;
            }
            if (f.isDirectory()) {
                logger.info("Retrieving a dataset " + requestUri);
                requestUri = requestUri + "/index.html";
                requestUri = requestUri.replace("//", "/"); //por si acaso había la barra al final
                response.sendRedirect(requestUri);
                return true;
            }
            int i = requestUri.lastIndexOf("/");
            if (i == -1) {
                return false;
            }
            i = requestUri.lastIndexOf(".");
            if (i != -1) {
                String extension = requestUri.substring(i);
                MimeManager mm = new MimeManager();
                String tipo = mm.mapa.get(extension);
                if (tipo != null) {
                    response.setContentType(tipo);
                    if (tipo.startsWith("text")) {
                        token = FileUtils.readFileToString(f);
                        response.getWriter().print(token);
                    } else {
                        ServletOutputStream output = response.getOutputStream();
                        InputStream input = new FileInputStream(f);
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    }
                }
                else
                {
                    response.setContentType("application/octet-stream");
                        ServletOutputStream output = response.getOutputStream();
                        InputStream input = new FileInputStream(f);
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
