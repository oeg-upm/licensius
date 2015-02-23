package oeg.lov;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMaker;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import licenser.EndPointExplorer;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import vroddon.sw.RDFUtils;
import vroddon.sw.Vocab;

/**
 * Vocab Manager
 * @author Victor
 */
public class LOVVocabs {

    public static void main(String[] args) {
//        List<Vocab> lv = queryLOVForVocabs();
//        System.out.println(lv);
        
        List<Vocab> vocabs = LOVVocabs.readVocabs("D:\\svn\\observatorio\\data\\lov.nq");
        for(Vocab v : vocabs)
        {
            System.out.println(v.uri+"\t"+v.license);
        }
            
        
    }
    
    public static List<Vocab> readVocabs(String filename)
    {
        List<Vocab> ls = new ArrayList();
//        Model model = RDFDataMgr.loadModel(filename) ;
        Model model = ModelFactory.createDefaultModel();
//        model.read(filename);
        
/*        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {
            public Graph create() {
                return new GraphMem();
            }
       };
        Dataset ds = DatasetImpl.wrap(new DatasetGraphMaker(maker));        */
        
        RDFDataMgr.read(model, "file:"+filename);
        if (model==null || model.isEmpty())
            System.out.println("joder");
        Resource RVOCAB = model.createResource("http://purl.org/vocommons/voaf#Vocabulary");
        List<Resource> lres= RDFUtils.getResourcesOfType(model, RVOCAB);
        for(Resource res : lres)
        {
            Vocab v = new Vocab(res.getLocalName(), res.getURI());
            StmtIterator iter2 = res.listProperties();
            while (iter2.hasNext()) {
                Statement stmt2 = iter2.nextStatement();
                RDFNode nodo = stmt2.getObject();
                Property prop = stmt2.getPredicate();
                if (prop.getURI().equals(RDFUtils.RLICENSE.getURI()))
                {
                    v.license = nodo.toString();
                }
            }        
            ls.add(v);
        }
        return ls;
    }
    
    
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

    /**
     * Makes a query to the LOV endpoint to get a list of vocabs.
     */
    public static List<Vocab> queryLOVForVocabs() {
//        String endpoint = "http://lov.okfn.org/endpoint/lov";
        String endpoint = "http://lov.okfn.org/dataset/lov/sparql";
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

}
