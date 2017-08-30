package oeg.rdflicense.servlet;

import javax.servlet.http.HttpServlet;
//JAVA
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;

//LOG4J

/**
 * This servlet is used by the user interface to show paginated answers
 * @author Victor
 */
public class ListServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ListServlet.class.getName());

    //Static so that it is read only one
    static private RDFLicenseDataset dataset = null;
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
        int current=1;
        int ilimit=10;
        int init=0;
        if (offset!=null)
            current = Integer.parseInt(offset);
        if (limit!=null)
            ilimit = Integer.parseInt(limit);
        init = (current - 1) * ilimit;
        
        System.err.println("Licenses nulas: "+ (licenses==null));
        System.err.println("Ahora mismo hay cargadas "+ licenses.size()+" licencias!");
        if (licenses.isEmpty()) {
            dataset = new RDFLicenseDataset();
            licenses = dataset.getRDFLicenses();
        }
        List<RDFLicense> filtradas = new ArrayList();
        filtradas.addAll(licenses);
        
        String search = request.getParameter("searchPhrase");
        int total = filtradas.size();
        ilimit = Math.min(ilimit, total);
        
        
        ilimit = init+10;
        
        logger.info("XXX: "+total+" "+current+" "+ilimit);
        System.err.println("TOTAL:  "+total+"CURRENT: "+ current +" INIT: "+init+" LIMIT: "+ilimit);
        
        if (total==0)
        {
            current=0;
            ilimit=0;
        }
        
        
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
            if (filtradas.size()<=i)
                continue;
            RDFLicense license = filtradas.get(i);
            if (conta != 0) {
                s += ",\n";
            }
            
            String titulo = license.getLabel() +" "+license.getVersion();
            
            s += "    {\n"
                    + "      \"license\": \"" + titulo + "\",\n"
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
