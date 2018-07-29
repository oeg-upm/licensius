package oeg.licensius.service;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

import oeg.licensius.rdflicense.RDFLicenseDataset;

/**
 * Returns a list of licenses. 
 * Working in Licensius 2.0
 * 
 * @author vroddon
 */
public class LicenseList extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseList.class.getName());

    /**
     * Sirve una lista con las URIs que se manejan
     * http://www.licensius.com/api/license/list
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(200);
            resp.setContentType("application/json");
            logger.info("Sirviendo una peticion de license list de " + req.getRemoteHost());
            PrintWriter w = resp.getWriter();
            JSONArray array = new JSONArray();
            RDFLicenseDataset dataset = new RDFLicenseDataset();
            for(String lic : dataset.listURILicenses())
                array.add(lic);
    
            String s = array.toJSONString();
            w.println(s);
            return;
        }
    
}
