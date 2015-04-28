package handlers;

import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import java.io.ByteArrayOutputStream;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

///JETTY
import auth.GoogleAuthHelper;
import ldconditional.LDRConfig;
import ldconditional.Main;
import ldrauthorizer.ws.Portfolio;
import model.ConditionalDataset;
import model.ConditionalDatasets;
import odrlmodel.managers.AssetManager;
import odrlmodel.rdf.Authorization;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author vroddon
 */
public class HandlerSPARQL {

    static final Logger logger = Logger.getLogger(HandlerSPARQL.class);

    /**
     * http://stackoverflow.com/questions/15328666/run-a-sparql-query-against-two-graphs
     */
    public void handle(ConditionalDataset cd, Request baseRequest, HttpServletRequest request, HttpServletResponse response, String xdataset) throws IOException, ServletException {
        String squery = request.getParameter("query");
        String superuser = request.getParameter("superuser");
        Portfolio p = Portfolio.getPortfolio(GoogleAuthHelper.getMail(request));
        String body = "";
        response.setStatus(HttpServletResponse.SC_OK);
        //SI NO HAY CONSULTA
            try {
                body = FileUtils.readFileToString(new File("./htdocs/template_query.html"));
                String footer = FileUtils.readFileToString(new File("./htdocs/footer.html"));
                body = body.replace("<!--TEMPLATEFOOTER-->", footer);
            } catch (Exception e) {
            }
        if (squery == null) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().print(body);
            baseRequest.setHandled(true);
            return;
        }

        logger.info("Procesando una QUERY de verdad!");
        


        List<String> grafos = p.getGrafos(cd);         //GRAFOS A LOS CUALES PODEMOS ACCEDER
        for (String grafo : grafos) {
            System.out.println("Comprado: " + grafo);
        }
        List<String> grafosabiertos = cd.getDatasetVoid().getGrafosAbiertos();
        grafos.addAll(grafosabiertos);
        System.out.println("Query PREVIA: " + squery);
        String querymodificada = rewriteQuery(squery, grafos);
        System.out.println("Query REESCRITA: " + querymodificada);
        Query query = QueryFactory.create(querymodificada);
        Dataset dataset = RDFDataMgr.loadDataset(cd.getDatasetDump().getFileName());
        QueryExecution qdataset = QueryExecutionFactory.create(query, dataset);
        ResultSet results = qdataset.execSelect();
        int vez=0;
        
        String html="<table class=\"table table-condensed\">";
        List<String> headers = new ArrayList();
        while(results.hasNext())
        {
            QuerySolution qs = results.next();
            if (vez==0)
            {
                html+="<thead><tr>";
                Iterator<String> is = qs.varNames();
                while (is.hasNext())
                {
                    String head=is.next();
                    headers.add(head);
                    if (head.equals("g"))
                        continue;
                    html+="<td>"+head+"</td>";
                }
                html+="</tr></thead>";
                vez++;
            }
            String row = "";
            row+="<tr>";
            for(String header : headers)
            {
               RDFNode nodo = qs.get(header);
               if (header.equals("g"))
                   continue;
               row+="<td>"+nodo+"</td>";
            }
            row+="</tr>";
            RDFNode nodo = qs.get("g");
            if (grafos.contains(nodo.toString()))
                html+=row;
        }
        html+="</table>";
        String sr = "";
        sr += html;
        sr+="<p>The original query was:</p>";
        sr+="<pre>"+ escapeHtml(squery) + "</pre>";
        sr+="<p>The modified query was:</p>";
        sr+="<pre>"+ escapeHtml(querymodificada) + "</pre>";

        body = body.replace("<!--TEMPLATERESULTS-->",sr);
        response.getWriter().print(body);
        baseRequest.setHandled(true);
    }

    public static String rewriteQuery(String q, List<String> grafos) {

        Query squery = QueryFactory.create(q);
        if (!squery.isSelectType()) {
            return "";
        }
        Element where = squery.getQueryPattern();

        String query = "SELECT * ";
        for (String g : grafos) {
            query += "FROM NAMED <" + g + "> ";
        }
        query += "WHERE { GRAPH ?g ";
        query += where.toString();
        query += " }";
        return query;
    }

    public static ResultSet execute(ConditionalDataset cd, String q, List<String> grafos) {
        Dataset dataset = RDFDataMgr.loadDataset(cd.getDatasetDump().getFileName());
        Iterator<String> is = dataset.listNames();
        /*        while (is.hasNext()) {
        String sg = is.next();
        lg.add(sg);
        logger.info("Consultando sobre: " + sg);
        }*/
        q = rewriteQuery(q, grafos);
        System.out.println("Query: " + q);
        Query query = QueryFactory.create(q);
        QueryExecution qdataset = QueryExecutionFactory.create(query, dataset);
        ResultSet results = qdataset.execSelect();
        return results;
    }

    public static void main(String[] args) throws Exception {
        Main.standardInit();
        String query = "select * where {<http://localhost/geo/resource/Provincia/Barcelona> ?p ?o}";
        ConditionalDataset cd = ConditionalDatasets.getDataset("geo");


    }
    //   }
}
