package ldserver;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import javax.servlet.http.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ldconditional.ConditionalDataset;
import ldconditional.Main;
import odrlmodel.Policy;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ldconditional.test.*;

/**
 * @api {get} /{dataset}/service/getResources getResources
 * @apiName /getResources
 * @apiGroup ConditionalLinkedData
 * @apiVersion 1.0.0
 * @apiDescription Gets the resources in a dataset. Dataset resources are those declared with the ldp:contains.
 * Once declared, they are check for their label (to be diplayed) and for their policies
 * 
 * @apiParam {String} dataset This is not a parameter, but appears in the URI string immediatly before the service/method.
 * @apiParam {String} page Page of results to be returned (e.g. 1)
 * @apiParam {String} size Number of rows to be returned (e.g. 20)
 *
 * @apiSuccess {String} An array of offers in JSON as follows: 
 * [{"licenses":[],"numTriples":7,"label":"Lleida","uri":"http:\/\/salonica.dia.fi.upm.es\/geo\/resource\/Provincia\/Lleida"}]
 *
 * @author Victor Rodriguez, OEG-UPM, 2015. 
 * 
 * Llamada tipo que se nos hará: geo/service/getResources?page=1&size=100
 * http://salonica.dia.fi.upm.es/geo/service/getResources?page=1&size=100
 * 
 * Obtiene la lista de provincias, (Almeria, Albacete...)
 * {label1, uri, numeroTriples, licenses: [{uri:'ffaf', 'label', 'color'''}, {uri:'ffaf', label, color''}]}  },
 * {label1, uri, openOrclosed}.... 
 * 
 * @prefix dcterms: <http://purl.org/dc/terms/>.
 * @prefix ldp: <http://www.w3.org/ns/ldp#>.
 * <http://example.org/c1/>
 *    a ldp:BasicContainer;
 *    dcterms:title "A very simple container";
 *    ldp:contains <r1>, <r2>, <r3>.
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
     * {label1, uri, numeroTriples, licenses: [{uri:'ffaf', 'label', 'color'''}, {uri:'ffaf', label, color''}]}  }, 
     */
    public String generarJson(String sdataset, int page, int size)
    {
        ConditionalDataset dataset = new ConditionalDataset(sdataset);
        JSONArray list = new JSONArray();
        int ini = (page-1)*size;
        int fin = (page-1)*size+size-1;
        List<Recurso> lr=dataset.getResourcesInDataset(ini, fin);
        for(Recurso r : lr)
        {
            JSONObject obj = new JSONObject();
            obj.put("uri", r.getUri());
            obj.put("label", r.getLabel());
            obj.put("numTriples", dataset.getDumpTriples(r.getUri()));
            JSONArray licencias = new JSONArray();
            Set<Policy> policies = dataset.getPolicies(r);
            for(Policy p : policies)
            {
                licencias.add(p.getURI());
            }
            obj.put("licenses", licencias);
            list.add(obj);
        }
        return list.toJSONString();
    }
    
    public JSONObject getLicencia()
    {
        JSONObject res = new JSONObject();
        res.put("name", "Etiqueta");
        res.put("uri", "http://creativecommons.org/cc-by");
        res.put("color", "#FF8888");
        return res;
        
    }
    
    public static void main(String[] args)
    {
        GetResources x = new GetResources();
        String s= x.generarJson("geo", 1, 20);
        System.out.println(s);
    }
    
}
