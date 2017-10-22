package observatory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import vroddon.sw.Dataset;
import vroddon.sw.SemanticWeb;
import vroddon.sw.Vocab;

/**
 * Claes principal.
 * En 2017 hago una importación del repo de bitbucket, mergeandolo en licensius y con las instrucciones que se muestran más abajo
 * https://stackoverflow.com/questions/1683531/how-to-import-existing-git-repository-into-another
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
