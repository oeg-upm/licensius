package ldserver;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
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
 * Llamada tipo que se nos hará: geo/service/getResources?page=1&size=100
 * http://salonica.dia.fi.upm.es/geo/service/getResources?page=1&size=100
 * 
 * Obtiene la lista de provincias, (Almeria, Albacete...)
 * {label1, uri, numeroTriples, licenses: [{uri:'ffaf', 'label', 'color'''}, {uri:'ffaf', label, color''}]}  },
 * {label1, uri, openOrclosed}.... 
 * 
 * @author Victor
 */
public class GetResources extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        int index = uri.indexOf('/', 1);
        String sdataset = uri.substring(1, index);
        
        int page=1;
        int size=100;
        Map<String, String> mapa = ServerUtils.getparams(req);
        if (mapa.get("page")!=null)
            page =  Integer.parseInt(mapa.get("page"));
        if (mapa.get("size")!=null)
            size =  Integer.parseInt(mapa.get("size"));        
        
        String json = generarJson(sdataset, page, size);
        

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println(json);  
    }


    /**
     * Genera el Json correspondiente al dataset
     */
    public String generarJson(String sdataset, int page, int size)
    {
        ConditionalDataset dataset = new ConditionalDataset(sdataset);
        List<String> graphs = dataset.getGraphs();
        JSONArray list = new JSONArray();
        int i=0;
        int ini = page*size;
        int fin = page*size+size-1;
        
        
        for(String grafo : graphs)
        { 
            JSONObject obj = new JSONObject();
            obj.put("name", grafo);
            obj.put("uri", grafo);
            obj.put("triples", 564);
            JSONArray licencias = new JSONArray();
            licencias.add(getLicencia());
            licencias.add(getLicencia());
            obj.put("licenses", licencias);
            list.add(obj);
            i++;
        }       
        return list.toJSONString();
    }
    
    public JSONObject getLicencia()
    {
        JSONObject res = new JSONObject();
        res.put("name", "Etiqueta");
        res.put("uri", "http://asd");
        res.put("color", "fúscia");
        return res;
        
    }
    
    public static void main(String[] args)
    {
        GetResources x = new GetResources();
        String s= x.generarJson("geo", 1, 20);
        System.out.println(s);
    }
    
}
