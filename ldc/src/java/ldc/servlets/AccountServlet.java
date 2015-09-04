package ldc.servlets;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ldc.LdcConfig;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 *
 * @author vroddon
 */
public class AccountServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String usuario = (String) request.getSession().getAttribute("usuario");
        if (usuario != null && !usuario.isEmpty()) {

        }
        String google = (String) request.getSession().getAttribute("google");
        String context = LdcConfig.get("context", "/ldc");
        if (google == null || google.isEmpty()) {
            google = "anon";
            response.sendRedirect(context+"/login.html");
        } else {
            try (PrintWriter out = response.getWriter()) {
                InputStream input = getClass().getResourceAsStream("template_empty.html");
                BufferedInputStream bis = new BufferedInputStream(input);
                String body = IOUtils.toString(bis, "UTF-8");
                String mensaje = "You are logged in as: " + google +"<br>";
                String uri= context+"/api/logout";
                mensaje+="<a href=\""+uri+"\">logout</a>";
                body = body.replace("<!--TEMPLATE_CONTAINER-->", mensaje);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().print(body);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().print("Error: " + e.getMessage());
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
