package observatory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import vroddon.sw.Dataset;
import vroddon.sw.SemanticWeb;
import vroddon.sw.Vocab;

/**
 *
 * @author Victor
 */
public class Observatory {
 
    public static boolean gui=true;
    public static Observation observation=new Observation();

    static String toRDF() {
        String rdf="";
        Model model = ModelFactory.createDefaultModel();
        for(Dataset ds : observation.datasets)
        {
            Model m=Dataset.Dataset2DCAT(ds);
            if (m!=null)
                model.add(m);
        }
        for(Vocab v : observation.vocabs)
        {
            Model m=Vocab.Vocab2RDF(v);
            if (m!=null)
                model.add(m);
            
        }
        
        rdf=SemanticWeb.toString(model);
        return rdf;
    }
    
}
