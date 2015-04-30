package ldconditional.model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public Set<String> getGrafos() {
        Set set = new HashSet<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            while ((line = br.readLine()) != null) {

    //            String s = NQuad.getSubject(line);
    //            set.add(s);
    //            String slocal=oeg.utils.StringUtils.getLocalName(s);
    //            if (!slocal.isEmpty())
    //                set.add(s);

       //         String p=NQuad.getPredicate(line);
       //         set.add(p);
         //       String o=NQuad.getObject(line);
         //       set.add(o);
                String g=NQuad.getGraph(line);
                set.add(g);
//                String lan = NQuad.getObjectLangTag(line);

            }
        }catch(Exception e)
        {

        }
        return set;
    }
    //Devuelve las entradas para el indice
    // REQUIERE QUE EL DUMP ESTE ORDENADO ALFABETICAMENTE. OJO....
    public Map<String, List<Integer>> getSujetos() {
        Map<String, List<Integer>> map = new HashMap();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            String lasts = "";
            int ini=0;
            while ((line = br.readLine()) != null) {
                i++;
                String s = NQuad.getSubject(line);
                if (s.equals(lasts))
                    continue;
                if (i==0)continue;
                List<Integer> li = new ArrayList();
                li.add(ini);
                li.add(i-1);
                ini = i;
                map.put(s,li);
            }
        }catch(Exception e)
        {

        }
        return map;
    }

    List<Integer> getLineas(String clave) {
        List list = new ArrayList<Integer>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            int conta=-1;
            while ((line = br.readLine()) != null) {
                conta++;
                String s = NQuad.getSubject(line);
                if (s.equals(clave))
                {
                    list.add(conta);
                    continue;
                }
                String slocal=oeg.utils.StringUtils.getLocalName(s);
                if (slocal.equals(clave))
                {
                    list.add(conta);
                    continue;
                }

                String p=NQuad.getPredicate(line);
                if (p.equals(clave))
                {
                    list.add(conta);
                    continue;
                }
                String o=NQuad.getObject(line);
                if (o.equals(clave))
                {
                    list.add(conta);
                    continue;
                }
                String g=NQuad.getGraph(line);
                if (g.equals(clave))
                {
                    list.add(conta);
                    continue;
                }
//                String lan = NQuad.getObjectLangTag(line);

            }
        }catch(Exception e)
        {

        }
        return list;
    }





}
