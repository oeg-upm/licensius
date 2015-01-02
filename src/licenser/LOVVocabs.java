package licenser;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import vroddon.sw.Vocab;

/**
 * Vocab Manager
 * @author Victor
 */
public class LOVVocabs {

    /**
     * Obtiene una lista de datasets leyéndolos del archivo "datasets.txt" en este mismo paquete
     * 
     * @return Lista de strings identificando 
     */
    public static List<Vocab> getPreloadedVocabs(String slocalfile) {
        List<Vocab> ls = new ArrayList();
        try {
            InputStream is = EndPointExplorer.class.getResourceAsStream(slocalfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(strLine, "\t");
                if (st.hasMoreTokens()) {
                    String vocab = st.nextToken();
                    String uri = st.nextToken();
                    Vocab v = new Vocab(vocab, uri);
                    ls.add(v);
                }
            }
            is.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        Logger.getLogger("licenser").info("Precargados " + ls.size() + " vocabularios");
        return ls;
    }

    public static List<Vocab> queryLOVForVocabs() {
        String endpoint = "http://lov.okfn.org/endpoint/lov";
        List<Vocab> lv = new ArrayList();
        String squery = "PREFIX vann:<http://purl.org/vocab/vann/>\n"
                + "PREFIX voaf:<http://purl.org/vocommons/voaf#>\n"
                + "SELECT ?vocabPrefix ?vocabURI WHERE {\n"
                + "?vocabURI a voaf:Vocabulary. ?vocabURI vann:preferredNamespacePrefix ?vocabPrefix.}\n"
                + "ORDER BY ?vocabPrefix \n";

        Query query = QueryFactory.create(squery);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        try {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution qs = results.next();
                String uri=qs.get("?vocabURI").toString();
                String prefix=qs.get("?vocabPrefix").toString();
                Vocab v = new Vocab(prefix, uri);
                lv.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            qexec.close();
        }

        return lv;
    }

    public static void main(String[] args) {
        List<Vocab> lv = queryLOVForVocabs();
        System.out.println(lv);

    }
}
