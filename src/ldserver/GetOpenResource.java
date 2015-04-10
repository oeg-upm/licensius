package ldserver;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.*;
import java.util.List;
import java.util.Map;
import ldconditional.ConditionalDataset;
import ldconditional.Main;
import odrlmodel.Policy;
import oeg.ldconditional.client.TestClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * 
 * @api {get} /{dataset}/getOffers getOffers
 * @apiName /getOffers
 * @apiGroup ConditionalLinkedData
 * @apiVersion 1.0.0
 * @apiDescription Gets the policy offers for a given dataset. An offer is a 
 * policy applied to a given resource (named graph) that has an attribute to be
 * promoted: <dataseturi> schema:makesOffer <policy>
 * 
 * @apiParam {String} dataset One word name of the dataset
 *
 * @apiSuccess {String} An array of offers in JSON as follows: 
 * {recurso1:  label, uri, comment} {license1: label, uri, precio, imgsrc}
 * {recurso2:  label, uri, comment} {license2: label, uri, precio, imgsrc}
 *
 * @author Victor Rodriguez, OEG-UPM, 2015. 
*
* geo/service/getOpenResource?uri=http://geo/Almeria ?
* geo/service/getResource?uri=http://geo/Almeria ? 
* http://salonica.dia.fi.upm.es/geo/service/getOpenResources?uri=http://salonica.dia.fi.upm.es/geo/
* it returns json-ld
* @author Victor
*/
public class GetOpenResource extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        int index=uri.indexOf('/',1);
        String sdataset = uri.substring(1,index);
//        ConditionalDataset dataset = new ConditionalDataset(sdataset);        
//        String resource ="";
        
        
            String uriToScan = req.getParameter("uri");
            try {
                uriToScan = URLDecoder.decode(uriToScan, "UTF-8");
                String json = generarJson(sdataset, uriToScan); 
                resp.getWriter().print(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
            resp.getWriter().print("not found");
            
  
    }

    public String generarJson(String sdataset, String uri)
    {
        ConditionalDataset dataset = new ConditionalDataset(sdataset);
        String json = dataset.getRecurso(uri);
        
        return json;
    }

    
    

    
    
    
    public static void main(String[] args) throws Exception {
        Main.initLogger();
        GetOpenResource x = new GetOpenResource();
        String s= x.generarJson("geo", "http://salonica.dia.fi.upm.es/geo/resource/Provincia/Burgos");
        System.out.println(s);
        
        
//        /service/getResource
//        TestClient.testGetOffers("geo");
    }
    
    
}
