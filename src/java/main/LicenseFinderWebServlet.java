package main;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import javax.servlet.ServletException;

import oeg.license.LicenseFinder;
import oeg.license.LicenseFinderWeb;

/**
 * Obtains the blablabla
 */
public class LicenseFinderWebServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(GetLicenseServlet.class.getName());
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
    {
        req.getRequestDispatcher("/licensefinder.jsp").forward(req, resp);
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

//        UserService userService = UserServiceFactory.getUserService();
//        User user = userService.getCurrentUser();

        String content = req.getParameter("contentweb");
        content.trim();
        System.out.println(content);
        String resultado = LicenseFinderWeb.checkForLicenseHint(content);
        String message = resultado;
        if (resultado.isEmpty())
        {   
            message="No license found";
        }
        req.setAttribute("message", "License analysis of " +content +"<br>" + message);        
        try {
            req.getRequestDispatcher("/licensefinder.jsp").forward(req, resp);
        } catch (ServletException ex) {
            log.log(Level.SEVERE, null, ex);
        }

//        resp.sendRedirect("/vroddonf.jsp");
    }
}
