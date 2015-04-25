package oeg.rdf.commons;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.ConditionalDataset;
import model.ConditionalDatasets;
import ldconditional.Main;
import ldrauthorizer.ws.LicensedTriple;
import ldserver.Recurso;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

/**
 * This class represents a NQuads raw file
 *
 * @author Victor
 */
public class NQuadRawFile {

    private String filename;
    private static final Logger logger = Logger.getLogger(NQuadRawFile.class);

    public NQuadRawFile(String _filename) {
        filename = _filename;
    }

    /**
     * POTENCIALMENTE COSTOSÍSIMO!!
     */
    public Model loadModel() {

        return RDFDataMgr.loadModel(filename);
    }
    /**
     * Gets a list with the graphs in the raw file.
     */
    public List<String> getGraphs() {
        List<String> lgrafos = new ArrayList();
        Set<String> grafos = new HashSet();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                String grafo = NQuad.getGraph(line);
                if (!grafo.isEmpty()) {
                    grafos.add(grafo);
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        for (String grafo : grafos) {
            lgrafos.add(grafo);
        }
        return lgrafos;
    }
    
    public int getNumTriples(String recurso)
    {
    int contador=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            while ((line = br.readLine()) != null) {        
                String nqs = NQuad.getSubject(line);
                if (!nqs.equals(recurso))
                    continue;
                contador++;
            }
        }catch(Exception e)
        {
            contador=-1;
        }
        return contador;
    }

    /**
     * 
     */
    public String getFirstObject(String s, String p) {
        String o = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            while ((line = br.readLine()) != null) {        
                String nqs = NQuad.getSubject(line);
                if (!nqs.equals(s))
                    continue;
                String nqp = NQuad.getPredicate(line);
                if (!nqp.equals(p))
                    continue;
                return NQuad.getObject(line);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return o;
    }
    
    
    /**
     * Obtiene una lista de recursos entre min y max
     */
    public List<Recurso> getRecursosWithObject(String o, int min, int max) {
        List<Recurso> recursos = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String object = NQuad.getObject(line);
                if (!object.equals(o))
                    continue;
                i++;
                if (i<min)
                    continue;
                if (i>max)
                    break;
                String subject = NQuad.getSubject(line);
                Recurso r = new Recurso();
                r.setUri(subject);
                recursos.add(r);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return recursos;
    }

    /**
     *
     */
    public void rebase(String from, String to) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename + "_rebased"));
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.replace(from, to);
                bw.write(line + "\n");
            }
            bw.close();
            br.close();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        Main.initLogger();
        NQuadRawFile raw = new NQuadRawFile("datasets/geo/data.nq");
     //   raw.rebase("http://conditional.linkeddata.es/ldr/", "http://salonica.dia.fi.upm.es/geo/");

    }

    public Model getRecurso(String subject) {
        Model modelo = ModelFactory.createDefaultModel();
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            while ((line = br.readLine()) != null) {        
                String s = NQuad.getSubject(line);
                if (!s.equals(subject))
                    continue;
                Model mm=NQuad.getStatement(line);
                modelo.add(mm);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return modelo;
    }

    /**
     * Gets the graphs in which the given URI appears as object.
     */
    public Set<String> getGraphsForSubject(String uri) {
        Set<String> ls = new HashSet();
      
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                String s= NQuad.getSubject(line);
                if (!s.equals(uri))
                    continue;
                ls.add(NQuad.getGraph(line));
            }
        }catch(Exception e)
        {
            logger.warn(e.getMessage());
        }
        
        return ls;
    }

    public String getFileName()
    {
        return filename;
    }

    /**
     * Obtains the licensed triples
     */
    public List<LicensedTriple> getLicensedTriples(String recurso) {
        List<LicensedTriple> llt = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String s = NQuad.getSubject(line);
                if (!s.equals(recurso))
                    continue;
                String o = NQuad.getObject(line);
                String p = NQuad.getPredicate(line);
                String g = NQuad.getGraph(line);
                LicensedTriple lt = new LicensedTriple(s,p,o,g);
                llt.add(lt);
            }
        }catch(Exception e)
        {

        }
        return llt;
    }
    public List<Statement> getTriplesInGrafo(String uri) {
    List<Statement> llt = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i=-1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String g = NQuad.getGraph(line);
                if (!g.equals(uri))
                    continue;
                String s = NQuad.getSubject(line);
                RDFNode o = NQuad.getObjectRDFNode(line);
                String p = NQuad.getPredicate(line);
                Model m = ModelFactory.createDefaultModel();
                Statement stmt =m.createStatement(m.createResource(s), m.createProperty(p), o);
                llt.add(stmt);
            }
        }catch(Exception e)
        {

        }
        return llt;
    }

}
