package ldserver;

import java.io.IOException;
import java.net.URLDecoder;
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
 *
 * geo/service/getOpenResource?uri=http://geo/Almeria
 * http://salonica.dia.fi.upm.es/geo/service/getOpenResources?uri=http://salonica.dia.fi.upm.es/geo/
 * @author Victor
 */
public class GetOpenResource extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        int index=uri.indexOf('/',1);
        String sdataset = uri.substring(1,index);
        ConditionalDataset dataset = new ConditionalDataset(sdataset);        
        
        
        
            String uriToScan = req.getParameter("uri");
            try {
                uriToScan = URLDecoder.decode(uriToScan, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            resp.getWriter().print("");
            
  
    }

    public static void main(String[] args) throws Exception {
        Main.initLogger();
        TestClient.testGetOffers("geo");
    }
    
    
}
