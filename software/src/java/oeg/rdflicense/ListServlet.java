package oeg.rdflicense;

import javax.servlet.http.HttpServlet;
//JAVA
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.*;

//LOG4J
import odrlmodel.Policy;
import oeg.rdflicense.servlet.MainServlet;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Victor
 */
public class ListServlet extends HttpServlet {

    //Static so that it is read only one
    static private RDFLicenseDataset dataset = null;
    private static final Logger logger = Logger.getLogger(MainServlet.class.getName());
    private static List<RDFLicense> licenses = new ArrayList();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        String offset = request.getParameter("current");
        String limit = request.getParameter("rowCount");
        int current = Integer.parseInt(offset);
        
        if (licenses.isEmpty()) {
            RDFLicenseDataset dataset = new RDFLicenseDataset();
            licenses = dataset.getRDFLicenses();
        }
        List<RDFLicense> filtradas = new ArrayList();
        filtradas.addAll(licenses);
        
        int ilimit = Integer.parseInt(limit);
        int init = (current - 1) * ilimit;
        String search = request.getParameter("searchPhrase");
        int total = filtradas.size();

/*
        filtradas = dataset.filterBy(filtradas, search);
        init = Math.min(init, total-1);
        current = Math.min(current, total-1);
        ilimit = Math.min(ilimit, total-1);
*/
        

        String s = "{\n"
                + "  \"current\": " + current + ",\n"
                + "  \"rowCount\": " + ilimit + ",\n"
                + "  \"rows\": [\n";
        int conta = 0;
        for (int i = init; i < ilimit; i++) {
            RDFLicense license = filtradas.get(i);
            if (conta != 0) {
                s += ",\n";
            }
            s += "    {\n"
                    + "      \"license\": \"" + license.getLabel() + "\",\n"
                    + "      \"licenseurl\": \"" + license.getURI() + "\"\n"
                    + "    } ";
            conta++;
        }
        s += "  ],\n"
                + "  \"total\": " + total + "\n"
                + "}    ";

        PrintWriter out = resp.getWriter();
        out.print(s);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
    }
}
