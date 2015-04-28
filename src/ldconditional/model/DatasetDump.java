package ldconditional.model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import oeg.rdf.commons.NQuad;
import oeg.rdf.commons.NQuadRawFile;

/**
 *
 * @author vroddon
 */
public class DatasetDump extends NQuadRawFile {

    public DatasetDump(ConditionalDataset cd)
    {
        super("datasets/"+cd.name+"/data.nq");
    }

    //Devuelve las entradas para el indice
    public Set<String> getEntradas() {
        Set set = new HashSet<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String s = NQuad.getSubject(line);
                set.add(s);
            //    StringUtils.
                String p=NQuad.getPredicate(line);
                set.add(p);
                String o=NQuad.getObject(line);
                set.add(o);
                String g=NQuad.getGraph(line);
                set.add(g);
//                String lan = NQuad.getObjectLangTag(line);

            }
        }catch(Exception e)
        {

        }
        return set;
    }





}
