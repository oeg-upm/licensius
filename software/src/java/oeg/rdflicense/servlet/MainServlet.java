package oeg.rdflicense.servlet;

//JAVA
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.*;

//LOG4J
import odrlmodel.Policy;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseCheck;
import oeg.rdflicense.RDFLicenseDataset;
import oeg.rdflicense.RDFUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * This class represents the main Servlet serving the RDFLicenses Examples:
 * http://localhost:8083/rdflicense/cc-by4.0
 * http://rdflicense.appspot.com/rdflicense/cc-by4.0
 */
public class MainServlet extends HttpServlet {

    //Static so that it is read only one
    static private RDFLicenseDataset dataset = null;
    private static final Logger logger = Logger.getLogger(MainServlet.class.getName());

    /**
     * Handles queries. We are able to handle: -
     * http://purl.org/NET/rdflicense/cc-by3.0gr --> has content negotiation -
     * http://purl.org/NET/rdflicense/cc-by3.0gr.ttl --> delivers always the TTL
     * - http://purl.org/NET/rdflicense/cc-by3.0gr.rdf --> delivers always the
     * RDF/XML version - http://purl.org/NET/rdflicense/cc-by3.0gr.html -->
     * delivers a human readable version - http://purl.org/NET/rdflicense/ or
     * http://purl.org/NET/rdflicense --> we serve the whole dataset
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();

        //lo que emipeza por RDFLicense
        if (uri.startsWith("/rdflicense")) {
            String rdflicenseuri = "http://purl.org/NET" + uri;
            logger.info("We have been requested " + rdflicenseuri);
            boolean bTurtle = false;
            boolean bRDF = false;
            boolean bHTML = false;

            if (dataset == null) {
                dataset = new RDFLicenseDataset();
            }
            if (dataset == null) {
                logger.warn("The dataset has failed to load: " + rdflicenseuri);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            //we have to serve the whole dataset!
            //We should look here at the request, if 
            if (rdflicenseuri.equals("http://purl.org/NET/rdflicense/") || rdflicenseuri.equals("http://purl.org/NET/rdflicense")) {
                resp.setContentType("text/turtle");
                dataset = new RDFLicenseDataset();
                resp.getWriter().println(dataset.toTTL());
                return;
            }

            if (rdflicenseuri.endsWith(".ttl")) {
                rdflicenseuri = rdflicenseuri.substring(0, rdflicenseuri.length() - 4);
                logger.info("We believe they mean " + rdflicenseuri);
                bTurtle = true;
            }
            if (rdflicenseuri.endsWith(".rdf")) {
                rdflicenseuri = rdflicenseuri.substring(0, rdflicenseuri.length() - 4);
                logger.info("We believe they mean " + rdflicenseuri);
                bRDF = true;
            }
            if (rdflicenseuri.endsWith(".html")) {
                rdflicenseuri = rdflicenseuri.substring(0, rdflicenseuri.length() - 5);
                logger.info("We believe they mean " + rdflicenseuri);
                bHTML = true;
            }

            RDFLicense license = dataset.getRDFLicense(rdflicenseuri);
            if (license == null) {
                logger.warn("We have not found " + rdflicenseuri + " ...");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (bHTML) {
                Policy policy = license.getPolicy();
                if (policy != null) {
                    String html = HTMLODRLManager.htmlPolicy(policy, "en");
                    resp.setContentType("text/html;charset=utf-8");
                    resp.getWriter().println(html.toString());
                } else {
                    resp.sendRedirect(license.getLegalCode());
                }
            } else if (bTurtle || ServletUtils.isTurtle(req)) {
                resp.setContentType("text/turtle");
                resp.getWriter().println(license.toTTL());
            } else if (bRDF || ServletUtils.isRDFXML(req)) {
                resp.setContentType("application/rdf+xml");
                resp.getWriter().println(license.toRDFXML());
            } else {
                resp.sendRedirect(license.getLegalCode());
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
           String sfile = uri.substring(1,uri.length());
           if (sfile.isEmpty())
               sfile="rdflicense.jsp";
           logger.info("Serving a general file " + sfile);
           File f = new File(sfile);
           if (!f.exists() || f.isDirectory()) {
                logger.warn("Does not exist " + sfile);
                resp.setContentType("text/html;charset=utf-8");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("404 not found");
            }
            else
            {
                String token = FileUtils.readFileToString(f);
                resp.getWriter().print(token);
                resp.setContentType("text/html;charset=utf-8");
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }

    private void logRequest(String page, String ip) {
        try {
            File file = new File("visitas.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            //true = append file
            FileWriter fileWritter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(page + "\t" + ip + "\n");
            bufferWritter.close();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
    

    
    /**
     * Obtains the HTML table to present the dataset in the webpage
     */
    public static String getTable() 
    {
        RDFLicenseDataset dataset = new RDFLicenseDataset();
        List<RDFLicense> licenses = dataset.getRDFLicenses();
        try{
        Collections.sort(licenses, RDFLicense.COMPARE_PUBLISHER);
        }catch(Exception e)
        {
            logger.warn("Licenses could not be sorted");
        }
        String s="";
        
        
        for (RDFLicense license : licenses) {
            if (license==null)
                continue;
            
            s+= "<tr>";
            s+= "<td>"+license.getPublisher()+"</td>";
            s+= "<td>"+license.getLabel()+"</td>";
            s+= "<td>"+license.getURI()+"</td>";
            s+= "<td>"+license.getVersion()+"</td>";
            s+= "<td><a href=\""+  license.getURI() +"\"><img src=\"img/rdflicense32.png\"/></a>"; 
            s+= "<a href=\""+  license.getURI() +".ttl\"><img src=\"img/rdf32.png\"/></a>";
            s+="</td>"; 
            s+="<td><p>";
            
            List<String> perm = RDFLicenseCheck.getPermissions(license);
            for(String p : perm)
            {
                String tip ="";
//                String tip=ODRL.getFirstComment(p);
                for(int i=0;i<10;i++)
                    tip = tip.replace("\t", "").replace("\n", "").replace("  ", " ");
                p = RDFUtils.getLastBitFromUrl(p);
                int in=p.indexOf("#");
                if (in!=-1) 
                {
                    try{p= p.substring(in+1);}catch(Exception e){}
                }
                
                s+="<span class=\"label label-primary\" rel=\"tooltip\" style=\"margin: 10px; \" data-toggle=\"tooltip\" title=\""+ tip +"\">"+p+"</span>";
            }
            s+="</p><p>";
            List<String> pro = RDFLicenseCheck.getProhibitions(license);
            for(String p : pro)
            {
          //      String tip=ODRL.getFirstComment(p);
                String tip = "";
                p = RDFUtils.getLastBitFromUrl(p);
                int in=p.indexOf("#");
                if (in!=-1)
                {
                    try{   p= p.substring(in+1);}catch(Exception e){}
                }
                s+="<span class=\"label label-danger\" rel=\"tooltip\" style=\"margin: 10px; data-toggle=\"tooltip\" title=\""+ tip +"\">"+p+"</span>";
            }
            List<String> dut = RDFLicenseCheck.getDuties(license);
            for(String p : dut)
            {
                String tip ="";
         //       String tip=ODRL.getFirstComment(p);
                p = RDFUtils.getLastBitFromUrl(p);
                int in=p.indexOf("#");
                if (in!=-1)
                {
                    try{   p= p.substring(in+1);}catch(Exception e){}
                }
                s+="<span class=\"label label-warning\" rel=\"tooltip\" style=\"margin: 10px; data-toggle=\"tooltip\" title=\""+ tip +"\">"+p+"</span>";
            }

            
            s+="</p></td>"; 
            s+="</tr>\n";
        }      
        return s;
    }     
}
