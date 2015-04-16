package ldserver;


import java.io.IOException;
import javax.servlet.http.*;
import java.util.List;
import ldconditional.ConditionalDataset;
import ldconditional.Main;
import odrlmodel.Policy;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ldconditional.test.*;


/**
 * @api {get} /{dataset}/service/getOffers getOffers
 * @apiName /getOffers
 * @apiGroup ConditionalLinkedData
 * @apiVersion 1.0.0
 * @apiDescription Gets the policy offers for a given dataset. An offer is a 
 * policy applied to a given resource (named graph) that has an attribute to be
 * promoted: <dataseturi> schema:makesOffer <policy>
 * 
 * @apiParam {String} dataset One word name of the dataset
 *
 * @apiSuccess {String} offers An array of offers in JSON as follows: <br/>
 * <pre>
 * "offers":[
 * resource: {link, comment, label} license: {label, uri, precio, imgsrc},<br/>
 * resource: {link, comment, label} license: {label, uri, precio, imgsrc} <br/>
 * ]
 * </pre>
 * 
 * @example
 * http://salonica.dia.fi.upm.es/geo/service/getOffers
 * 
 * @author Victor Rodriguez, OEG-UPM, 2015. 
 */
public class GetOffers extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetOffers.class.getName());

    /**
     * Serves requests like: http://localhost/geo/service/getOffers
     * http://salonica.dia.fi.upm.es/geo/service/getOffers
     * 
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {        
        String uri = req.getRequestURI();
        int index=uri.indexOf('/',1);
        String sdataset = uri.substring(1,index);
        String out ="";
        
        out = generarJson(sdataset);

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);       
        resp.getWriter().println(out);
    }
    
    /**
     * Genera el json que toca
     */
    public String generarJson(String sdataset)
    {
        ConditionalDataset dataset = new ConditionalDataset(sdataset);
        List<String> graphs = dataset.getGraphs();
        JSONArray list = new JSONArray();
        //for each graph...
        for(String grafo : graphs)
        {
            List<Policy> policies = dataset.getPoliciesForGraph(grafo);
            Recurso recurso = dataset.getDatasetPolicy().getRecurso(grafo);
            if (recurso==null)
                continue;     
            //for each policy
            for(Policy p : policies)
            {
                String nombre = p.getTitle();
                if (nombre.isEmpty())
                    nombre = p.getLabel("en");
                if (!p.inOffer)
                    continue;
                
                //CREA LA OFERTA DE CADA POLÍTICA
                Oferta offer = new Oferta();
                offer.setLicense_title(nombre);
                offer.setLicense_link(p.uri);
                offer.setLicense_price("OpenData");
                
                //Y LA VA CONVIRTIENDO A JSON
                JSONObject obj = new JSONObject();
                obj.put("resource", recurso.toJSON());
                obj.put("policy", offer.toJSON());
                list.add(obj);
            }
        }
        return list.toJSONString();
    }
    
    public static void main(String[] args) throws Exception {
        Main.initLogger();
        
        GetOffers x = new GetOffers();
        String s= x.generarJson("geo");
        System.out.println(s);        
//        TestClient.testGetOffers("geo");
    }
    
    
    
}
