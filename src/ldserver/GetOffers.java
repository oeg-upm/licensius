package ldserver;


import java.io.IOException;
import javax.servlet.http.*;
import java.util.List;
import ldconditional.ConditionalDataset;
import ldconditional.Main;
import odrlmodel.Policy;
import oeg.ldconditional.client.TestClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @api {get} /{dataset}/getOffers getOffers
 * @apiName /getOffers
 * @apiGroup ConditionalLinkedData
 * @apiVersion 1.0.0
 * @apiDescription Gets the policy offers for a given dataset
 * 
 * @apiParam {String} dataset One word name of the dataset
 *
 * @apiSuccess {String} licenseURI URI of the recognized license
 * @author Victor at OEG
 * 
 * 
 * For example:
 */
public class GetOffers extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetOffers.class.getName());

    /**
     * Serves requests like: http://localhost/geo/service/getOffers
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        String uri = req.getRequestURI();
        int index=uri.indexOf('/',1);
        String sdataset = uri.substring(1,index);
        
        String out ="";
        
        ConditionalDataset dataset = new ConditionalDataset(sdataset);
        List<String> graphs = dataset.getGraphs();
        
        JSONArray list = new JSONArray();
        for(String grafo : graphs)
        {
            List<Policy> policies = dataset.getPoliciesForGraph(grafo);
            Recurso recurso = dataset.getDatasetPolicy().getRecurso(grafo);
            if (recurso==null)
                continue;
            
            for(Policy p : policies)
            {
                String nombre = p.getTitle();
                if (nombre.isEmpty())
                    nombre = p.getLabel("en");
                Oferta offer = new Oferta();
                offer.setLicense_title(nombre);
                offer.setLicense_link(p.uri);
                offer.setLicense_price("OpenData");
                JSONObject obj = new JSONObject();
                obj.put("resource", recurso.toJSON());
                obj.put("policy", offer.toJSON());
                list.add(obj);
            }
        }
        out = list.toJSONString();
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        
        resp.getWriter().println(out);
    }
    
    public static void main(String[] args) throws Exception {
        Main.initLogger();
        TestClient.testGetOffers("geo");
    }
    
    
    
}
