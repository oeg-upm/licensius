
package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.licensius.model.LicensiusFound;
import oeg.licensius.model.LicensiusError;
import oeg.licensius.model.LicensiusResponse;
import oeg.licensius.model.LicensiusSimpleResponse;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseCheck;
import oeg.rdflicense.RDFLicenseDataset;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

/**
 * Determines if a license is open or not
 * 
 * @author vrodriguez
 */
public class LicenseIsopen extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseIsopen.class.getName());

    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(200);
            String uri = req.getParameter("uri");
            LicensiusResponse le = get(uri);
            if (le.getClass().equals(LicensiusError.class))
            {
                LicensiusError e = (LicensiusError) le;
                resp.setStatus(500);
                PrintWriter w = resp.getWriter();
                w.println(e.getJSON());
                return;
            }
            resp.setStatus(200);
            resp.setContentType("text/plain");
            PrintWriter w = resp.getWriter();
            w.println(le.getJSON());
            return;             
        }
    
    public LicensiusResponse get(String uri)
    {
        JSONArray array = new JSONArray();
        RDFLicense lic = RDFLicenseDataset.getRDFLicense(uri);
        if (lic==null)
        {
            LicensiusError error = new LicensiusError(404, "License not found.");
            return error;
        }
        LicensiusSimpleResponse ls = new LicensiusSimpleResponse();
        RDFLicenseCheck check = new RDFLicenseCheck(lic);
        ls.text=""+check.isOpen();
        return ls;

    }
    
     public static void main(String[] args) throws ParseException {
        String uri = "http://purl.org/NET/rdflicense/NDL1.0";
        RDFLicense rdflicense = new RDFLicense(uri);
        if (rdflicense==null)
        {
            LicensiusError error = new LicensiusError(404, "License not found.");
            System.out.println("error");
        }
        LicensiusSimpleResponse ls = new LicensiusSimpleResponse();
        RDFLicenseCheck check = new RDFLicenseCheck(rdflicense);
        ls.text=""+check.isOpen();
        
        System.out.println(ls.getJSON());

    }    
    
}
