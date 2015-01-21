package ldrauthorizer.ws;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

///JETTY
import ldrauthorizerold.GoogleAuthHelper;
import odrlmodel.LDRConfig;
import odrlmodel.managers.AssetManager;
import odrlmodel.rdf.Authorization;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *SELECT * {<http://salonica.dia.fi.upm.es/ldr/resource/Provincia/Barcelona>  ?p ?o}

SELECT * FROM NAMED <http://salonica.dia.fi.upm.es/ldr/dataset/default> 
WHERE {GRAPH ?g {<http://salonica.dia.fi.upm.es/ldr/resource/Provincia/Barcelona> ?p ?o}}

 * @author vroddon
 */
public class CLDHandlerSPARQL extends AbstractHandler {

    public void handle(String string, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!string.startsWith("/ldr/query")) {
            return;
        }
        if (string.startsWith("/ldr/query.html")) {
            return;
        }
        
        
        Portfolio p = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
        String body = "";
        response.setStatus(HttpServletResponse.SC_OK);
//        LDRAuthorizerView view = (LDRAuthorizerView) LDRAuthorizerApp.getApplication().getMainView();
        String uri = request.getRequestURI();

        if (uri.startsWith("/ldr/query")) {
            String squery = request.getParameter("query");
            String superuser = request.getParameter("superuser");

            
            
            
            List<String> grafos = Authorization.PolicyMatcher(p);

            String squeryold = squery;
            if (superuser == null) {
                squery = Authorization.rewriteQuery2(squery, grafos);
                String descripcion = squeryold + " rewritten as " + squery;
                System.out.println(descripcion);
                String escaped = escapeHtml(descripcion);
                body += escaped + "<hr>";

            }
            if (!squery.isEmpty()) {
                Logger.getLogger("ldr").debug("MAKING SPARQL QUERY: " + squery);
                Query query = QueryFactory.create(squery);

//            query.addNamedGraphURI("http://salonica.dia.fi.upm.es/ldr/dataset/Madrid");

              //    So qioero lanzarlo contra otro endpoing
              //    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
              //    Model results = qexec.execConstruct();
                
                DatasetGraph datasetgraph = RDFDataMgr.loadDatasetGraph(LDRConfig.getDataset()) ;
                Dataset dataset = RDFDataMgr.loadDataset(LDRConfig.getDataset()) ;
//                Dataset dataset = AssetManager.getFullDatasetNew();
                QueryExecution qdataset = QueryExecutionFactory.create(query, dataset);
                ResultSet results = qdataset.execSelect();
                
                
                
             //  Model model = RDFDataMgr.loadModel(LDRConfig.getDataset());
             //   QueryExecution qmodel = QueryExecutionFactory.create(query, model);
    //            ResultSet results =  qmodel.execSelect();
                //String salida = ResultSetFormatter.asXMLString(results);
                String salida = ResultSetFormatter.asText(results);
                salida = escapeHtml(salida);
                salida = salida.replaceAll("\n", "<br>");
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ResultSetFormatter.out(os, results, query);
//            String salida = new String(os.toByteArray(),"UTF-8");
//            qmodel.close();

                List<QuerySolution> lqs = ResultSetFormatter.toList(results);
                for (QuerySolution qs : lqs) {
                    System.out.println(qs.toString());
                }

                qdataset.close();
                body += salida;
                response.getWriter().print(body);
                baseRequest.setHandled(true);
            }

        }



    }
}
