package observatorio.utils;

//JAVA
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//JENA
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

//LOG4J
import org.apache.log4j.Logger;
import vroddon.sw.Vocab;

/**
 * Clase para explorar sistemáticamente una lista de Endpoints conocidos con una misma fsparql
 * 
 * @author Victor
 */
public class EndPointExplorer {

//    SELECT * WHERE {?s <http://purl.org/dc/elements/1.1/rights> ?o} limit 300
    ///Variable local que alberga a los endpoints
    static List<String> endpoints = new ArrayList();

    public static void main(String[] args) {
        EndPointExplorer.init();
        //  EndPointExplorer.launchMassiveQueries("select * where {?s <http://purl.org/dc/terms/license> ?o} limit 100");
        EndPointExplorer.launchMassiveQueries("select * where {?s <http://purl.org/dc/elements/1.1/rights> ?o} limit 100");
    }

    /**
     * Puebla de endpoints la variable estática endpoints
     */
    public static void init() {
        try {
            InputStream is = EndPointExplorer.class.getResourceAsStream("endpoints.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = "";
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    endpoints.add(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.getLogger("licenser").info("Se ha cargado una lista con " + endpoints.size() + " endpoints.");
    }

    /**
     * Lanza consultas masivas a la lista de endpoints SPARQL precargados
     * Va a saco, creando un hilo para cada endpoint
     * Saca por pantalla el resultado de cada consulta
     * @param sparql Consulta a lanzar.
     */
    public static void launchMassiveQueries(String sparql) {
        final String fsparql = sparql;
        Logger.getLogger("licenser").info("Abriendo " + endpoints.size() + " hilos.");
        for (String endpoint : endpoints) {
            final String fendpoint = endpoint;
            (new Thread() {

                public void run() {
                    String res = queryEndpointSubjectObject(fsparql, fendpoint);
                    System.out.print(res);
                }
            }).start();
        }
    }

    /**
     * Hace una fsparql con un predicado buscando sujetos y objetos que están por él relacionados
     * @param sparql Consulta
     * @param endpoint Ejemplo: http://bnb.data.bl.uk/sparql o http://dbpedia.org/sparql
     * @return Una cadena con ambas cosas separadas por una tabulación
     */
    public static String queryEndpointSubjectObject(String sparql, String endpoint) {
        String res = "";
        Query query = QueryFactory.create(sparql);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        if (qexec == null) {
            return res;
        }
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.next();
                RDFNode s = qs.get("s");
                RDFNode o = qs.get("o");
                res += endpoint + "\t" + s + "\t" + o + "\n";
            }
        } catch (Exception e) {
            return "";
        }
        qexec.close();
        return res;
    }

}
