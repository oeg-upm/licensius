package oeg.licensius.service;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;
import org.json.simple.JSONArray;

/**
 * Returns a list of licenses
 * @author vrodriguez
 */
public class LicenseList extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseList.class.getName());

    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(200);
            resp.setContentType("application/json");
            System.out.println("Hello world");
            logger.info("hello world");
            PrintWriter w = resp.getWriter();
            String s= get();
            w.println(s);
            return;
        }
    
    /**
     */
    public String get()
    {
        JSONArray array = new JSONArray();
        RDFLicenseDataset dataset = new RDFLicenseDataset();
        List<RDFLicense> list = dataset.listRDFLicenses();
        for(RDFLicense rdf : list)
        {
            String uri = rdf.getURI();
            if (uri!=null)
                array.add(rdf.getURI());
        }
        return array.toJSONString();
    }

    
}
