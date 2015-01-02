package vroddon.sw;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Victor
 */
public class Vocab implements Serializable {
    
    public String vocab;
    public String uri;
    
    
    public boolean alive=false;
    
    /** 
     * Constructor vacío
     */
    private Vocab() {
        vocab="";
        uri ="";
    }
    
    /**
     * Constructor habitual
     */
    public Vocab(String _vocab, String _uri)
    {
        vocab=_vocab;
        uri=_uri;
    }

    
    public String toString()
    {
        return vocab;
    }
    
    /**
     * Express the vocabulary as RDF
     * @param v Vocabulary to be represented as RDF
     */
    public static Model Vocab2RDF(Vocab v) {

        Model model = ModelFactory.createDefaultModel();
        if (v.uri==null)
            return null;
        try {
            URI uri = new URI (v.uri);
        } catch (URISyntaxException ex) {
            return null;
        }
        Resource rmyvocab = model.createResource(v.uri);
        rmyvocab.addProperty(RDF.type, SemanticWeb.VOCABULARY);
        rmyvocab.addProperty(SemanticWeb.PREFIX, v.vocab);
        rmyvocab.addProperty(SemanticWeb.ALIVE, v.alive?"true":"false");
        return model;
    }
    
    /**
     * From model to datasets
     */
    public static List<Vocab> toVocabs(Model model) {
        String dcat = SemanticWeb.toString(model);
        List<Vocab> vocabs = new ArrayList();

        StmtIterator iter = model.listStatements(null, RDF.type, SemanticWeb.VOCABULARY);
        while (iter.hasNext()) {
            Vocab v = new Vocab();
            Statement stmt = iter.nextStatement();
            Resource res = stmt.getSubject();
            v.uri = res.getURI();

            StmtIterator iter2 = res.listProperties();
            while (iter2.hasNext()) {
                Statement stmt2 = iter2.nextStatement();
                RDFNode nodo = stmt2.getObject();
                Property prop = stmt2.getPredicate();
/*                if (prop.equals(SemanticWeb.TITLE)) {
                    v.title = nodo.toString();
                }
                if (prop.equals(SemanticWeb.LICENSE)) {
                    v.license = nodo.toString();
                }
                if (prop.equals(SemanticWeb.ALIVE)) {
                    v.alive = nodo.toString().equals("true");
                }
                if (prop.equals(SemanticWeb.DESCRIPTION)) {
                    v.description = nodo.toString();
                }*/
             
                
            }



            vocabs.add(v);
        }
        return vocabs;
    }
    
}
