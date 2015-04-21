package ldserver.ws;


import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.http.*;
import java.util.List;
import model.ConditionalDataset;
import ldconditional.Main;
import odrlmodel.Policy;
import ldconditional.test.*;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Victor
 */
public class GetClosedResource extends HttpServlet {

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
        GetClosedResource x = new GetClosedResource();
        String s= x.generarJson("geo", "http://salonica.dia.fi.upm.es/geo/resource/Provincia/Burgos");
        System.out.println(s);
        
        
//        /service/getResource
//        TestClient.testGetOffers("geo");
    }    
}
