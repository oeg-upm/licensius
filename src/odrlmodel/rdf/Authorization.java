package odrlmodel.rdf;

import java.util.ArrayList;
import java.util.List;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMaker;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class has no other dependency
 * @author Victor
 */
public class Authorization {

    public static void mainx(String args[]) {
        Logger.getRootLogger().setLevel(Level.OFF);

        //WE CREATE THE DATASET
        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {
            public Graph create() {
                return new GraphMem();
            }
        };
        Dataset dataset = DatasetImpl.wrap(new DatasetGraphMaker(maker));

        //WE ADD THE PROVINCES
        Model modelOpen = ModelFactory.createDefaultModel();
        Model modelClosed = ModelFactory.createDefaultModel();
        RDFDataMgr.read(modelOpen, "./data/provincias.rdf");

        /*       Model modelA = ModelFactory.createDefaultModel();        
        Resource ra1=modelA.createResource("apple");
        Property pa1=modelA.createProperty("definition");
        Literal la1=modelA.createLiteral("Delicious fruit");
        Statement st=modelA.createStatement(ra1, pa1, la1);
        modelA.add(st);
        
        Model modelB = ModelFactory.createDefaultModel();        
        Resource rb1=modelB.createResource("apple");
        Property pb1=modelB.createProperty("definition");
        Literal lb1=modelB.createLiteral("Fruta deliciosa");
        Statement st1=modelB.createStatement(rb1, pb1, lb1);
        modelB.add(st);
        
        
        dataset.addNamedModel("English", modelA);
        dataset.addNamedModel("Spanish", modelB);
         */

        dataset.addNamedModel("Grafo1", modelOpen);
        dataset.addNamedModel("Grafo2", modelClosed);

        Resource valladolid = modelOpen.getResource("http://geo.linkeddata.es/resource/Provincia/Valladolid");
        Resource provincia = modelOpen.getResource("http://geo.linkeddata.es/ontology/Provincia");
        Statement stmt = modelOpen.createStatement(valladolid, RDF.type, provincia);
        modelOpen.remove(stmt);
        modelClosed.add(stmt);

        //FILTRADO POR TIPO DE RECURSO
/*       NodeIterator rit=modelOpen.listObjectsOfProperty(RDF.type);
        while(rit.hasNext())
        {
        RDFNode res = (RDFNode) rit.next();
        if (res.isResource())
        System.out.println(((Resource)res).getLocalName());
        }*/



        //FILTRADO POR 

        //    RDFDataMgr.write(System.out, dataset, Lang.NQUADS) ;  
    }
    public static void main(String[] args) throws Exception {

        List<String> grafos=new ArrayList();
        grafos.add("http://salonica.dia.fi.upm.es/ldr/dataset/default");
        rewriteQuery("SELECT ?s ?p ?o WHERE {?s ?p ?o}", grafos);
        
    }
        /**
     * Implementa una reescritura de la consulta
     */
    public static String rewriteQuery2(String squery, List<String> grafos) {

        Query query = QueryFactory.create(squery);
        if (!query.isSelectType() || grafos.isEmpty()) {
            return "";
        }
        Element where = query.getQueryPattern();

        String from = "";

        String nueva = " WHERE {";
        int tot = grafos.size();
        int conta=0;
        for(String grafo : grafos)
        {
            conta++;
            nueva = nueva+ " { GRAPH <" + grafo + ">"+ where +"}" ;
            if (conta!=tot)
                nueva += " UNION ";
        }
//        nueva+="}";

        String qprima = query.toString();
        int ini=qprima.indexOf("WHERE");
        if (ini==-1)
            ini=qprima.indexOf("where");
        if (ini==-1)
            return "";
        
        int fin=findClosing(qprima.toCharArray(), ini+9);
        
        String s0=qprima.substring(0, ini)+" ";
        String s1=nueva;
        String s2=qprima.substring(fin, qprima.length()-1);
        
        qprima = s0 + s1+s2;

        return qprima;
    }
    /**
     * Implementa una reescritura de la consulta
     */
    public static String rewriteQuery(String squery, List<String> grafos) {

        Query query = QueryFactory.create(squery);
        if (!query.isSelectType() || grafos.isEmpty()) {
            return "";
        }
        Element where = query.getQueryPattern();

        String from = "";

        for (String grafo : grafos) {
            from += "FROM NAMED <" + grafo + "> ";
        }
        String nueva = from + " WHERE { GRAPH ?g " + where ;

        String qprima = query.toString();
        int ini=qprima.indexOf("WHERE");
        if (ini==-1)
            ini=qprima.indexOf("where");
        if (ini==-1)
            return "";
        
        int fin=findClosing(qprima.toCharArray(), ini+9);
        
        String s0=qprima.substring(0, ini)+" ";
        String s1=nueva;
        String s2=qprima.substring(fin, qprima.length()-1);
        
        qprima = s0 + s1+s2;

        return qprima;

        /*query.addNamedGraphURI(grafo);
        System.out.println(element.toString());
        ElementGroup eg=(ElementGroup) query.getQueryPattern();
         */

        /*
        List<Element> elements = eg.getElements();
        for (Element e : elements)
        {
        //            System.out.println(e.toString());
        }
        query.setQueryPattern(eg);
        Op compile = Algebra.compile(query);
        String cadena=query.toString();
         */

    }

    public static int findClosing(char[] text, int openPos) {
        int closePos = openPos;
        int counter = 1;
        while (counter > 0 && closePos!=text.length) {
            char c = text[++closePos];
            if (c == '{') {
                counter++;
            } else if (c == '}') {
                counter--;
            }
        }
        return closePos;
    }




}

