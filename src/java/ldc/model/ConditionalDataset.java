package ldc.model;

import com.hp.hpl.jena.vocabulary.RDF;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ldc.Ldc;
import ldc.LdcConfig;
import ldc.Main;
import ldc.auth.LicensedTriple;
import odrlmodel.Policy;
import oeg.utils.rdf.NQuad;
import oeg.utils.strings.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author vroddon
 */
public class ConditionalDataset {

    static final Logger logger = Logger.getLogger("ldc");

    public String name = "";

    public DatasetVoid dsVoid = null;
    DatasetDump dsDump = null;
    DatasetIndex dsIndex = null;

    public ConditionalDataset(String _name) {
        name = _name;
        try {
            dsVoid = new DatasetVoid(this);
            dsDump = new DatasetDump(this);
            dsIndex = new DatasetIndex(this);

        } catch (Exception e) {
            name = null;
            logger.error("Error processing " + _name + ". " + e.getMessage());
        }

    }

    public DatasetDump getDatasetDump() {
        return dsDump;
    }

    public DatasetVoid getDatasetVoid() {
        return dsVoid;
    }

    public String getURI() {
        String s = LdcConfig.get("server", "http://localhost:8080");
        String c = LdcConfig.get("context", "/ldc");
        return s + c + "/data/" + name;
    }

    public boolean verify() {
        if (dsVoid == null || dsVoid.model == null) {
            return false;
        }
        return true;
    }

    public List<LicensedTriple> getLicensedTriples(String recurso) {

        List<LicensedTriple> llt = new ArrayList();
        List<String> ls = dsIndex.getNQuadsForSujeto(recurso);
        for (String line : ls) {
            String s = NQuad.getSubject(line);
            if (!s.equals(recurso)) {
                continue;
            }
            String o = NQuad.getObject(line);
            String p = NQuad.getProperty(line);
            String g = NQuad.getGraph(line);
            String l = NQuad.getObjectLangTag(line);
            LicensedTriple lt = new LicensedTriple(s, p, o, l, g);
            llt.add(lt);
        }
        return llt;
    }

    public boolean rebase(String datasuri) {
        
        boolean ok = getDatasetDump().rebase(datasuri);
        if (!ok) {
            logger.warn("Rebase operation has failed for the dump");
            return false;
        }
        ok = getDatasetVoid().rebase(datasuri);
        if (!ok)
        {
            logger.warn("Rebase operation has failed for the void");            
            return false;
        }
        return ok;
    }

    public String getDumpPath() {
        String sfolder = getFolder();
        String filename = sfolder + "/data.nq";
        return filename;
    }

    public DatasetIndex getDatasetIndex() {
        return dsIndex;
    }

    public String getFolder() {
        String sfolder = LdcConfig.get("datasetsfolder", "D:\\data\\ldc");
        if (!sfolder.endsWith("/")) {
            sfolder += "/";
        }
        sfolder = sfolder + name;
        return sfolder;

    }

    /**
     * Returns the named graphs present in the dataset
     */
    public List<Grafo> getGrafos() {
        return dsVoid.getGrafos();
    }

    public List<Policy> getPoliciesForGraph(String grafo) {
        return dsVoid.getPolicies(grafo);
    }

    public void resetGraphsInDataset() {
        dsDump.resetGraphs(getURI() + "/default");
        dsVoid.resetDataset(getURI());
        cleanGraphs();
    }

    public void cleanGraphs() {
        Set<String> ls = dsDump.getGrafos();
        //
        logger.info("En el dump");
        for (String g : ls) {
            logger.info(g);
        }
        logger.info("En el vopid");
        List<Grafo> lg = dsVoid.getGrafos();
        for (Grafo g : lg) {
            logger.info(g.uri);
            if (!ls.contains(g.uri)) {
                dsVoid.model.removeAll(dsVoid.model.createResource(g.uri), null, null);
                dsVoid.write();
            }
        }
        lg = dsVoid.getGrafos();
        for (String s : ls) {
            Grafo x = new Grafo(dsVoid, s);
            if (!lg.contains(x)) {
                dsVoid.model.add(dsVoid.model.createResource(s), RDF.type, dsVoid.model.createResource("http://www.w3.org/ns/dcat#Dataset"));
                dsVoid.write();
            }
        }

    }
    
    public static void main(final String[] args) throws IOException {
        Main.standardInit();
        ConditionalDataset cd = Ldc.getDataset("rdflicense");
        cd.getSuggestedURI();
    }


    //Devuelve la que parece actualmente la URI del dataset
    //Esta funcion no deberia indexar todo si es que no eta indexado, sino solo una fraccion prqueña
    public String getSuggestedURI() {
        String best = "";
        if (getDatasetIndex().getIndexedSujetos().isEmpty()) {
            getDatasetIndex().indexar();
        }
        List<String> ls = getDatasetIndex().getIndexedSujetos();
        Map<String, Integer> mapa = new HashMap<String, Integer>();

        //Creación del mapa
        for (String s : ls) {
            List<String> subcadenas = StringUtils.superset(s);
            for (String token : subcadenas) {
                if (token.isEmpty()) {
                    continue;
                }
                int k=0;
                if (mapa.containsKey(token))
                    k = mapa.get(token);
                k++;
                mapa.put(token, k);
            }
        }
        mapa = StringUtils.sortByValue(mapa);
        
        //Resultados del mapa
        Iterator it = mapa.entrySet().iterator();
        int conta=0;
        int maximo=-1;
        int largo =-1;
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            int valor = (Integer)e.getValue();
            String clave = (String) e.getKey();
        //    System.out.println(clave+" "+valor);
            if (conta==0)
            {
                maximo = valor;
                best = clave;
            }
            if (valor==maximo && clave.length()>best.length())
                best = clave;
            conta++;
        }
        logger.info("El URI candidato es: " +  best);
        return best;
    }

    /**
     * Crea un nuevo grafo en el sistema.
     * @param exclusive: on, off
     */
    public void createGraph(String s, String p, String o, String g, String exclusive) {
        logger.info("Exclusive: " + exclusive);
        g = getURI()+"/"+g;
        if (exclusive.equals("on"))
            getDatasetDump().setGraph(s, p, o, g);
        else
            getDatasetDump().addGraph(s, p, o, g);
     
        
        getDatasetVoid().ensureGrafo(g);
        
    }

}
