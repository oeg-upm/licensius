package oeg.rdf.commons;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ldconditional.Main;
import ldconditional.auth.LicensedTriple;
import ldconditional.ldserver.Recurso;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

/**
 * This class represents a NQuads raw file
 *
 * @author Victor
 */
public class NQuadRawFile {


    protected String filename;
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

    public void writeModel() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename + "_rebased"));
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                String s = NQuad.getSubject(line);
                String p = NQuad.getPredicate(line);
                String o = NQuad.getObject(line);
                String g = NQuad.getGraph(line);
                String lan = NQuad.getObjectLangTag(line);
                o = o.startsWith("http") ? ("<" + o + ">") : ("\"" + o + "\"");
                if (!lan.isEmpty()) {
                    o += "@" + lan;
                }
                bw.write("<" + s + "> <" + p + "> " + o + " <" + g + "> .\n");
            }
            bw.close();
            br.close();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    String getName(String uri) {
        int i1 = uri.lastIndexOf("#");
        int i2 = uri.lastIndexOf("/");
        int i = Math.max(i1, i2);
        if (i == -1) {
            return "";
        }
        String label = uri.substring(i + 1, uri.length());
        return label;
    }

    public void quitarHash() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename + "_rebased"));
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                Model m = NQuad.getStatement(line);

                String s = NQuad.getSubject(line);
                String p = NQuad.getPredicate(line);
                String o = NQuad.getObject(line);
                String g = NQuad.getGraph(line);
                String lan = NQuad.getObjectLangTag(line);

                s = s.replace("#", "/");
                if (o.startsWith("http://localhost")) {
                    o = o.replace("#", "/");
                }
                o = o.startsWith("http") ? ("<" + o + ">") : ("\"" + o + "\"");
                if (!lan.isEmpty()) {
                    o += "@" + lan;
                }
                bw.write("<" + s + "> <" + p + "> " + o + " <" + g + "> .\n");
            }
            bw.close();
            br.close();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
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

    public int getNumTriplesSinIndex(String recurso) {
        int contador = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String nqs = NQuad.getSubject(line);
                if (!nqs.equals(recurso)) {
                    continue;
                }
                contador++;
            }
        } catch (Exception e) {
            contador = -1;
        }
        return contador;
    }

    public String getFirstObject(Model minimodel,String s, String p) {
        String sol="";
        NodeIterator nit =minimodel.listObjectsOfProperty(ModelFactory.createDefaultModel().createResource(s), ModelFactory.createDefaultModel().createProperty(p));
        while(nit.hasNext())
        {
            RDFNode n = nit.next();
            return n.asLiteral().getString();
        }
        return sol;
    }
     
    
    /**
     * PROHIBIDO HACER ESTO. ES INCREIBLEMENTE LENGO PARA ARCHIVOS GRANDES.
     */
    public String getFirstObject(String s, String p) {
        String o = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String nqs = NQuad.getSubject(line);
                if (!nqs.equals(s)) {
                    continue;
                }
                String nqp = NQuad.getPredicate(line);
                if (!nqp.equals(p)) {
                    continue;
                }
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
            int i = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String object = NQuad.getObject(line);
                if (!object.equals(o)) {
                    continue;
                }
                i++;
                if (i < min) {
                    continue;
                }
                if (i > max) {
                    break;
                }
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
        NQuadRawFile raw = new NQuadRawFile("datasets/emn/mini.nq");
        raw.quitarHash();
//        raw.writeModel();

        //   raw.rebase("http://conditional.linkeddata.es/ldr/", "http://salonica.dia.fi.upm.es/geo/");

    }

    public Model getRecurso(String subject) {
        Model modelo = ModelFactory.createDefaultModel();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String s = NQuad.getSubject(line);
                if (!s.equals(subject)) {
                    continue;
                }
                Model mm = NQuad.getStatement(line);
                modelo.add(mm);
            }
        } catch (Exception e) {
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
                String s = NQuad.getSubject(line);
                if (!s.equals(uri)) {
                    continue;
                }
                ls.add(NQuad.getGraph(line));
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        return ls;
    }

    public String getFileName() {
        return filename;
    }

    /**
     * Obtains the licensed triples
     */
   /*private List<LicensedTriple> getLicensedTriplesLENTA(String recurso) {
        List<LicensedTriple> llt = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String s = NQuad.getSubject(line);
                if (!s.equals(recurso)) {
                    continue;
                }
                String o = NQuad.getObject(line);
                String p = NQuad.getPredicate(line);
                String g = NQuad.getGraph(line);
                String l = NQuad.getObjectLangTag(line);
                LicensedTriple lt = new LicensedTriple(s, p, o, l, g);
                llt.add(lt);
            }
        } catch (Exception e) {
        }
        return llt;
    }*/

    public List<Statement> getTriplesInGrafoSinIndexar(String uri) {
        List<Statement> llt = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String g = NQuad.getGraph(line);
                if (!g.equals(uri)) {
                    continue;
                }
                String s = NQuad.getSubject(line);
                RDFNode o = NQuad.getObjectRDFNode(line);
                String p = NQuad.getPredicate(line);
                Model m = ModelFactory.createDefaultModel();
                Statement stmt = m.createStatement(m.createResource(s), m.createProperty(p), o);
                llt.add(stmt);
            }
        } catch (Exception e) {
        }
        return llt;
    }

    public int countNumTriples() {
        int i = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                i++;
            }
        } catch (Exception e) {
        }
        return i;
    }

    public static Model getModel(List<String> triples) {
        Model model = ModelFactory.createDefaultModel();
        for(String triple : triples)
        {
            Model st = NQuad.getStatement(triple);
            model.add(st);
        }
        return model;
    }

}
