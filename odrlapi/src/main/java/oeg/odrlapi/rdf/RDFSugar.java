package oeg.odrlapi.rdf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * Collection of static methods of usefulness.
 *
 * @author vroddon
 */
public class RDFSugar {
    // Sistema de caché para almacenar modelos

    public static Map<Integer, Model> cache = new WeakHashMap();

    public static Model getModel(String rdf) {
        int hash = rdf.hashCode();
        if (cache.size() > 50) {
            cache.clear();
        }
        Model model = cache.get(hash);
        if (model == null) {
            model = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
            try {
                model.read(is, null, "TURTLE");
                cache.put(hash, model);
                return model;
            } catch (Exception e) {
                try {
                    is.close();
                    is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
                    model.read(is, null, "RDF/XML");
                    cache.put(hash, model);
                    return model;
                } catch (Exception e2) {

                    try {
                        is.close();
                        is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
                        model.read(is, null, "JSON-LD");
                        System.out.println("turtle "+RDFUtils.getString(model));
                        cache.put(hash, model);
                        return model;
                    } catch (Exception e3) {

                        return null;
                    }
                }
            }
        }
        return model;
    }

    /**
     * Obtiene todos los recursos que son de un tipo dado
     */
    public static List<Resource> getResourcesOfType(Model model, Resource resource) {
        List<Resource> policies = new ArrayList();
        ResIterator it = model.listResourcesWithProperty(RDF.type, resource);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            Resource sujeto = s.getSubject();
            policies.add(sujeto);
        }
        return policies;
    }

    public static Set<RDFNode> getObjects(Model model, Resource regla, Property prop) {
        Set<RDFNode> res = new HashSet();
        StmtIterator si = model.listStatements(regla, prop, (RDFNode) null);
        while (si.hasNext()) {
            Statement st = si.next();
            RDFNode node = st.getObject();
            res.add(node);
        }
        return res;
    }

    public static Set<Resource> getResourceObjects(Model model, Resource regla, Property prop) {
        Set<Resource> res = new HashSet();
        StmtIterator si = model.listStatements(regla, prop, (RDFNode) null);
        while (si.hasNext()) {
            Statement st = si.next();
            RDFNode node = st.getObject();
            if (node.isResource()) {
                res.add(node.asResource());
            }
        }
        return res;
    }

    public static Set<String> getLiteralObjects(Model model, Resource regla, Property prop) {
        Set<String> res = new HashSet();
        StmtIterator si = model.listStatements(regla, prop, (Literal) null);
        while (si.hasNext()) {
            Statement st = si.next();
            RDFNode node = st.getObject();
            if (node.isLiteral()) {
                res.add(node.asLiteral().toString());
            }
        }
        return res;
    }
}
