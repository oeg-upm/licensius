package ldconditional.model;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ldconditional.LDRConfig;
import ldconditional.Main;
import ldconditional.auth.LicensedTriple;
import ldconditional.ldserver.Recurso;
import odrlmodel.Policy;
import oeg.utils.ExternalSort;
import org.apache.log4j.Logger;

/**
 * This class represents a dataset with conditional access. It is made of: - A
 * dataset void The info must be as Void.ttl. - A dataset dump. The info must be
 * represented as NQuads
 *
 * @author Victor
 */
public class ConditionalDataset {

    private static final Logger logger = Logger.getLogger(ConditionalDataset.class);

    DatasetVoid dsVoid = null;
    DatasetDump dsDump = null;
    DatasetIndex dsIndex = null;

    //1 word name
    public String name = "";

    /**
     * A conditional dataset is initialized with a _name The _name must be a
     * simple string, with no blank spaces nor other non-URI characters.
     */
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

    public DatasetVoid getDatasetVoid() {
        return dsVoid;
    }

    public DatasetDump getDatasetDump() {
        return dsDump;
    }

    public String getUri() {
        return LDRConfig.getServer() + name;
    }

    public DatasetIndex getDatasetIndex() {
        return dsIndex;
    }

    public Set<Policy> getPolicies(Recurso r) {
        Set<Policy> lp = new HashSet();
        for (String g : r.graphs) {
            List<Policy> politicas = dsVoid.policies.get(g);
            if (politicas != null) {
                lp.addAll(politicas);
            }
        }
        return lp;
    }

    public String toString() {
        String s = "";
        s += "Dataset URI: " + getUri() + "\n";
        s += "Title: " + dsVoid.getTitle() + "\n";
        s += "File name: " + dsDump.getFileName() + "\n";
        s += "Graphs in dump: " + dsDump.getGraphs().size() + "\n";
        s += "Licensed graphs: " + dsVoid.policies.size() + "\n";
        return s;
    }

    public List<Policy> getPoliciesForGraph(String grafo) {
        return dsVoid.getPolicies(grafo);
    }

    /**
     * Obtiene los recursos del min-esimo al max-esimo.
     *
     */
    public List<Recurso> getResourcesInDataset(int min, int max) {
        List<String> ls = dsVoid.getObjectsForProperty("http://www.w3.org/ns/ldp#contains");
        List<Recurso> recursos = new ArrayList();
        int i = 0;
        for (String s : ls) {
            Recurso r = new Recurso(s);
            if (i >= min && i <= max) {
                recursos.add(r);
            }
            i++;
        }
        /* String patron = dsVoid.getFirstObjectForProperty("http://www.w3.org/ns/ldp#contains");
         String o = NTriple.getObject(patron);
         recursos = dsDump.getRecursosWithObject(o, min, max);*/
        recursos = enrichWithLabels(recursos);
        recursos = enrichWithGraphs(recursos);
        return recursos;
    }

    /**
     * Returns the named graphs present in the dataset
     */
    public List<String> getGraphs() {
        List<String> grafos = dsDump.getGraphs();
        return grafos;
    }

    /**
     * Returns the named graphs present in the dataset
     */
    public List<Grafo> getGrafos() {
        return dsVoid.getGrafos();
    }

    private List<Recurso> enrichWithLabels(List<Recurso> recursos) {
        for (Recurso r : recursos) {
            String uri = r.getUri();
            String label = dsDump.getFirstObject(uri, "http://www.w3.org/2000/01/rdf-schema#label");
            if (label.startsWith("\"")) {
                int i1 = label.lastIndexOf("\"");
                label = label.substring(1, i1);
            }
            r.setLabel(label);
        }
        return recursos;
    }

    public Model getModelRecurso(String res) {
        return dsDump.getRecurso(res);
    }

    public String getRecurso(String subject) {
        Model model = dsDump.getRecurso(subject);
        StringWriter sw = new StringWriter();
        //    JenaJSONLD.init();
        org.openjena.riot.RIOT.init();
        model.write(sw, "RDF/JSON", "");
//        RDFDataMgr.write(sw, model, Lang.JSONLD);//.JSONLD
        return sw.toString();
    }

    /**
     * Enriquece los recursos dados con grafos
     */
    private List<Recurso> enrichWithGraphs(List<Recurso> recursos) {
        for (Recurso r : recursos) {
            String uri = r.getUri();
            Set<String> lg = dsDump.getGraphsForSubject(r.getUri());
            r.setGraphs(lg);
        }
        return recursos;
    }

    /**
     * Obtains the first object
     */
    public String getFirstObject(String recurso, String propiedad) {
        String o = dsDump.getFirstObject(recurso, propiedad);
        return o;
    }

    public List<LicensedTriple> getLicensedTriples(String recurso) {
        return dsDump.getLicensedTriples(recurso);

    }

    public static void main(String[] args) {
        Main.standardInit();
        ConditionalDataset cd = ConditionalDatasets.getDataset("geo");
        List<Grafo> lg = cd.getGrafos();
        for (Grafo g : lg) {
            System.out.println(g);
        }

        System.out.println(cd.getDatasetVoid().toRDF());
    }

    public String getDumpPath() {
        String sfolder = getFolder();
        String filename = sfolder + "/data.nq";
        return filename;
    }

    public String getFolder() {
        String sfolder = LDRConfig.get("datasetsfolder", "datasets");
        if (!sfolder.endsWith("/")) {
            sfolder += "/";
        }
        sfolder = sfolder + name;
        return sfolder;

    }

    public boolean rebase(String datasuri) {
        //This operation can be terrible ong

        boolean ok = getDatasetDump().rebase(datasuri);
        if (!ok) {
            return false;
        }
        ok = getDatasetVoid().rebase(datasuri);
        return ok;
    }

}
