/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oeg.licensius.service;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.openjena.atlas.json.io.parser.JSONParser;

/**
 *
 * @author vrodriguez
 */
public class LicenseGetlicensetext extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LicenseGetlicensetext.class.getName());
    /**
     * Sirve una cadena de prueba.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String uri = req.getParameter("uri");
            String lan = req.getParameter("lan");
            RDFLicense rdf = new RDFLicense(uri);
            if(rdf==null)
            {
                PrintWriter w = resp.getWriter();
                w.println(rdf.getLegalCode("Could not find that license " + uri));                
                resp.setStatus(404);
                return;
            }
            if (lan==null || lan.isEmpty())
            {
                String json=rdf.getLegalCodeLanguages();
                logger.error(json);
                resp.setStatus(200);
                resp.setContentType("text/plain;charset=utf-8");//application/json
                resp.setCharacterEncoding("utf-8");
                PrintWriter w = resp.getWriter();
                w.println(json);
                return;
            }
            else
            {
                String txt=rdf.getLegalCode(lan);
                resp.setStatus(200);
                resp.setContentType("text/plain;charset=utf-8");
                resp.setCharacterEncoding("utf-8");
                PrintWriter w = resp.getWriter();
                w.println(txt);
                return;
                
            }
    }
     public static void main(String[] args) throws ParseException {
        String uri = "http://purl.org/NET/rdflicense/NDL1.0";
//        RDFLicense rdf = RDFLicenseDataset.getRDFLicense(uri);
        RDFLicense rdf = new RDFLicense(uri);
        String s = rdf.getLegalCodeLanguages();
        System.out.println(s);
        org.json.simple.parser.JSONParser parser=new org.json.simple.parser.JSONParser();
        JSONArray obj=(JSONArray)parser.parse(s);
        for(int i=0;i<obj.size();i++)
        {
            String x = (String)obj.get(i);
            System.out.println(x);
            String y = rdf.getLegalCode(x);
            System.out.println(y);
        }
// JSONParser.parse(s);
    }
   
}
