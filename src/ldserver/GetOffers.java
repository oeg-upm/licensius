package ldserver;


import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import ldconditional.ConditionalDataset;
import ldconditional.Main;
import odrlmodel.Policy;
import oeg.ldconditional.client.TestClient;
import org.apache.log4j.Logger;

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
 */
public class GetOffers extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetOffers.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        String uri = req.getRequestURI();
        int index=uri.indexOf('/',1);
        String sdataset = uri.substring(1,index);
        
        String out ="";
        
        ConditionalDataset dataset = new ConditionalDataset(sdataset);
        List<String> graphs = dataset.getGraphs();
        for(String grafo : graphs)
        {
            out+=grafo+" ";
            List<Policy> policies = dataset.getPoliciesForGraph(grafo);
            for(Policy p : policies)
            {
                String nombre = p.getTitle();
                if (nombre.isEmpty())
                    nombre = p.getLabel("en");
                out+= nombre + " ";
            }
            out+="\n";
        }
        
        
        
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println(out);
    }
    
    public static void main(String[] args) throws Exception {
        Main.initLogger();
        TestClient.testGetOffers("geo");
    }
    
    
    
}
