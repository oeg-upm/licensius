package ldrauthorizer.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldrauthorizerold.GoogleAuthHelper;
import ldconditional.LDRConfig;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Default handler for all the requests
 * Ejemplo de licencia: http://rdflicense.linkeddata.es/rdflicense/odbl1.0.ttl
 */
public class CLDHandlerDefault extends AbstractHandler {

    public CLDHandlerDefault() {
    }

    /**
     * target–the target of the request, which is either a URI or a name from a named dispatcher.
     * baseRequest–the Jetty mutable request object, which is always unwrapped.
     * request–the immutable request object, which might have been wrapped.
     * response–the response, which might have been wrapped.
     * The handler sets the response status, content-type and marks the request as handled before it generates the body of the response using a writer.
     */
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String sinfo = "Recibida la consulta " + request.getMethod() + " ";
        sinfo += request.getRequestURI() + " ";
        try {
            sinfo = sinfo; // + " de " + request.getRemoteHost() + " " + request.getRemoteAddr();
        } catch (Exception e) {
        }
        Logger.getLogger("ldr").info(sinfo);

        //Gestión del usuario de sesión
        String sesState = (String) request.getSession().getAttribute("state");
        if (sesState == null) {
            final GoogleAuthHelper helper = new GoogleAuthHelper();
            request.getSession().setAttribute("state", helper.getStateToken());
        }
        //Fin de gestión de usuario de sesión
        if (target.equals("/") || target.equals("/ldr/")) //Raízzz . @todo la segund aprte del if está mal
        {
            String rootfolder=LDRConfig.getRootfolder();
            Logger.getLogger("ldr").info("Se quiso acceder a la raiz " + rootfolder);
            response.sendRedirect(rootfolder);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            baseRequest.setHandled(true);            
        }
        else if (target.startsWith("/ldr/rdflicense") || target.startsWith("/rdflicense") || target.equals("/rdflicense")) {
            Logger.getLogger("ldr").info("Procesando licencia RDF ");
            if (target.startsWith("/ldr/rdflicense"))
                target=target.substring(4);
            
            CLDHandlerRDFLicense hRdflic = new CLDHandlerRDFLicense();
            hRdflic.handle(target, baseRequest, request, response);
//            response.setStatus(HttpServletResponse.SC_FOUND);
        } else if (target.startsWith("/dataset/") || target.startsWith("/ldr/dataset/")) {
            Logger.getLogger("ldr").info("Procesando web de la descripcion");
            boolean ok = parseHTML(request, response, "../dataset");
            if (ok) // esto es una página web estándar
            {

                response.setStatus(HttpServletResponse.SC_FOUND);
                baseRequest.setHandled(true);
            } else //no se ha encontrado
            {
                Logger.getLogger("ldr").warn("OJO: page not found " + target);
                response.sendRedirect("/ldr/en/404.html");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                baseRequest.setHandled(true);
            }

        } else if (target.startsWith("/ldr/")) {

            CLDHandlerIndex handler2 = new CLDHandlerIndex();
            handler2.handle(target, baseRequest, request, response);
            CLDHandlerPolicy handler1 = new CLDHandlerPolicy();
            handler1.handle(target, baseRequest, request, response);
            CLDHandlerDatasets handler7 = new CLDHandlerDatasets();
            handler7.handle(target, baseRequest, request, response);
            CLDHandlerManager handler3 = new CLDHandlerManager();
            handler3.handle(target, baseRequest, request, response);
            CLDHandlerResource handler4 = new CLDHandlerResource();
            handler4.handle(target, baseRequest, request, response);
            CLDHandlerService handler5 = new CLDHandlerService();
            handler5.handle(target, baseRequest, request, response);
            CLDHandlerAccount handler6 = new CLDHandlerAccount();
            handler6.handle(target, baseRequest, request, response);
            CLDHandlerSPARQL handler99 = new CLDHandlerSPARQL();
            handler99.handle(target, baseRequest, request, response);

            if (!baseRequest.isHandled()) {
                LDRServer.parseResource(baseRequest, request, response);
            }
        } else if (target.startsWith("/oauth2callback")) {
            System.out.println("Recibida una autenticación en Google");
            String nuevaurl = "/ldr/en/account.html";

            URIBuilder builder = new URIBuilder();
            builder.setPath("/ldr/en/account.html");

            Enumeration<String> enume = request.getParameterNames();
            while (enume.hasMoreElements()) {
                String p = enume.nextElement();
                String values[] = request.getParameterValues(p);
                for (int i = 0; i < values.length; i++) {
                    System.out.println("Param " + p + " " + values[i]);
                    builder.addParameter(p, values[i]);
                }
            }
            String uri = "";
            try {
                uri = builder.build().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("URI " + uri);
            response.sendRedirect(uri);
            response.setStatus(HttpServletResponse.SC_FOUND);
            baseRequest.setHandled(true);
        } else if (target.equals("/favicon.ico")) {
            response.setContentType("image/x-icon");
            ServletOutputStream output = response.getOutputStream();
            InputStream input = new FileInputStream("../htdocs/favicon.ico");
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } else if (target.equals("/ODRLAuthorize/testGetTicket")) {
            String resource = request.getParameter("resource");
            String userid = request.getParameter("userid");
            resource = URLDecoder.decode(resource, "UTF-8");
            userid = URLDecoder.decode(userid, "UTF-8");
            String token = ODRLAuthorizer.GiveToken(resource, userid);
            response.setContentType("text/turtle");
            response.getWriter().print(token);
            //      view.addInfo("PAID TIKET. Reponded a " + token);
        } else if (target.equals("/ODRLAuthorize/testAuthorize")) {
            if (request.getMethod().equals("GET")) {
                String recurso = request.getParameter("resource");
                recurso = URLDecoder.decode(recurso, "UTF-8");
                boolean ok = ODRLAuthorizer.Authorize(recurso);
                if (ok == false) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
                //          view.addInfo("GET AUTHORIZATION. Reponded a " + response.getStatus());
            } else if (request.getMethod().equals("POST")) {
                String mensaje = "";
                String inputline = "";
                BufferedReader br = request.getReader();
                try {
                    while ((inputline = br.readLine()) != null) {
                        mensaje += inputline;
                        //          System.out.println("I got a message from a client: " + inputline);
                    }
                } catch (IOException e) {
                    System.err.println("Error: " + e);
                }
                //      view.addInfo("POST AUTHORIZATION. Reponded a " + response.getStatus());
            }
            //   view.addInfo("WELL PROCESSED: " + target);
        } else {
            Logger.getLogger("ldr").warn("OJO: page not found " + target);
            response.sendRedirect("/ldr/en/404.html");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            baseRequest.setHandled(true);
            //   view.addInfo("UNKNOWN REQUEST: " + target);

        }
        baseRequest.setHandled(true);

    }

    public boolean parseHTML(HttpServletRequest request, HttpServletResponse response, String folder) {
        response.setContentType("text/html;charset=utf-8");
        Logger.getLogger("ldr").info("Parsing a standard file in folder " + folder);
        response.setStatus(HttpServletResponse.SC_OK);
        boolean binario = false;
        try {

            String token = "";
            String s2 = request.getRequestURI();
            String s3 = s2.substring(4);
            s3 = "../htdocs" + s3;
            Logger.getLogger("ldr").info("Looking for file " + s3);
            if (s3.equals("../htdocs/dataset") || s3.equals("../htdocs/dataset/"))
                s3="../htdocs/dataset/index.html";
            
            File f = new File(s3);
            if (!f.exists()) {
                return false;
            }
            if (s3.endsWith(".htm") || s3.endsWith(".html") || s3.endsWith(".css") || s3.endsWith(".js") || s3.endsWith(".ttl") || s3.endsWith(".rdf")) {
                token = FileUtils.readFileToString(f);
                if (s3.endsWith(".css")) {
                    response.setContentType("text/css");
                }
                if (s3.endsWith(".js")) {
                    response.setContentType("application/x-javascript");
                }
                if (s3.endsWith(".ttl")) {
                    response.setContentType("text/css");
                }


            } else if (s3.endsWith(".png")) {
                response.setContentType("image/png");
                ServletOutputStream output = response.getOutputStream();
                InputStream input = new FileInputStream(s3);
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                binario = true;
            } else if (s3.endsWith(".ico")) {
                response.setContentType("image/x-icon");
                ServletOutputStream output = response.getOutputStream();
                InputStream input = new FileInputStream(s3);
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                binario = true;
            } else if (s3.endsWith(".swf")) {
                response.setContentType("application/x-shockwave-flash");
                ServletOutputStream output = response.getOutputStream();
                InputStream input = new FileInputStream(s3);
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                binario = true;
            } else if (s3.endsWith(".nq")) {
                response.setContentType("application/n-quads");
                token = FileUtils.readFileToString(f);
            } else {
                return false;
            }
            if (!binario) {
                response.getWriter().print(token);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}