package oeg.licensius.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import main.Licensius;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;

/**
 * Returns a list of licenses
 * @author vrodriguez
 */
public class LicenseList extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(Test.class.getName());

    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setStatus(200);
            resp.setContentType("application/json");
            PrintWriter w = resp.getWriter();
            String s= get();
            w.println(s);
            return;
        }
    
    
    public String get()
    {
        String s="[";
        RDFLicenseDataset dataset = new RDFLicenseDataset();
        List<RDFLicense> list = dataset.listRDFLicenses();
        for(RDFLicense rdf : list)
        {
            if (rdf!=null)
                s+=rdf.toMicroJSON()+",\n";
        }
        s+="]";
        return s;
        
    }
    
    public static void main(String[] args)
    {
        Licensius.initLogger();
        LicenseList l = new LicenseList();
        String s = l.get();
        System.out.println(s);
    }
    
    
}
