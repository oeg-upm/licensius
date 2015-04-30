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
import org.apache.log4j.Logger;

/**
 *
 * @author vroddon
 */
public class DatasetDump extends NQuadRawFile {

    private static final Logger logger = Logger.getLogger(NQuadRawFile.class);

    public DatasetDump(ConditionalDataset cd) {
        super("datasets/" + cd.name + "/data.nq");
    }
    //Devuelve las entradas para el indice

    public Set<String> getGrafos() {
        Set set = new HashSet<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String g = NQuad.getGraph(line);
                set.add(g);
            }
        } catch (Exception e) {
        }
        return set;
    }


    //Devuelve las entradas para el indice
    // REQUIERE QUE EL DUMP ESTE ORDENADO ALFABETICAMENTE. OJO....

    public Map<String, List<Integer>> getSujetos() {
        Map<String, List<Integer>> map = new HashMap();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            String lasts = "";
            int ini = 0;
            while ((line = br.readLine()) != null) {
                i++;
                String s = NQuad.getSubject(line);
                if (s.equals(lasts)) {
                    continue;
                }
                if (i == 0) {
                    lasts=s;
                    continue;
                }
                List<Integer> li = new ArrayList();
                li.add(ini);
                li.add(i - 1);
                map.put(lasts, li);
                lasts = s;
                ini = i;
                
            }
        } catch (Exception e) {
        }
        return map;
    }

    List<Integer> getLineas(String clave) {
        List list = new ArrayList<Integer>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            int conta = -1;
            while ((line = br.readLine()) != null) {
                conta++;
                String s = NQuad.getSubject(line);
                if (s.equals(clave)) {
                    list.add(conta);
                    continue;
                }
                String slocal = oeg.utils.StringUtils.getLocalName(s);
                if (slocal.equals(clave)) {
                    list.add(conta);
                    continue;
                }

                String p = NQuad.getPredicate(line);
                if (p.equals(clave)) {
                    list.add(conta);
                    continue;
                }
                String o = NQuad.getObject(line);
                if (o.equals(clave)) {
                    list.add(conta);
                    continue;
                }
                String g = NQuad.getGraph(line);
                if (g.equals(clave)) {
                    list.add(conta);
                    continue;
                }
//                String lan = NQuad.getObjectLangTag(line);

            }
        } catch (Exception e) {
        }
        return list;
    }

    String getTextBetweenLines(Integer g0, Integer g1) {
        String str = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;

            while ((line = br.readLine()) != null) {
                i++;
                if (i > g1) {
                    return str;
                }
                if (i >= g0) {
                    str += line + "\n";
                }
            }
        } catch (Exception e) {
            logger.debug("mal");
        }
        return str;
    }
    List<String> getNQuadsBetweenLines(Integer g0, Integer g1) {
        List<String> str = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;

            while ((line = br.readLine()) != null) {
                i++;
                if (i > g1) {
                    return str;
                }
                if (i >= g0) {
                    str.add(line);
                }
            }
        } catch (Exception e) {
            logger.debug("mal");
        }
        return str;
    }
}
