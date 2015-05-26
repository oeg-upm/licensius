package ldconditional.ldserver.ws;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.*;
import java.util.List;
import java.util.Map;
import ldconditional.model.ConditionalDataset;
import ldconditional.Main;
import odrlmodel.Policy;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ldconditional.test.*;


/**
 * 
 * @api {get} /{dataset}/service/getResource getResource
 * @apiName /getResource
 * @apiGroup ConditionalLinkedData
 * @apiVersion 1.0.0
 * @apiDescription It currently implements both getOpenResource and getClosedResource. 
 * Gets an open resource
 * promoted: <dataseturi> schema:makesOffer <policy>
 * 
 * @apiParam {String} uri Resource to be retrieved.
 *
 * @apiSuccess {String} data JSON-LD with the triples related to that uri -in principle all the triples of which the resource is subject.
 *
 * @author Victor Rodriguez, OEG-UPM, 2015. 
*
* Example:
* http://salonica.dia.fi.upm.es/geo/service/getResource?uri=http://salonica.dia.fi.upm.es/geo/resource/Provincia/Valladolid ? 
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
