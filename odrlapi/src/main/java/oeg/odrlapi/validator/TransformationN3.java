package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import oeg.odrlapi.rdf.RDFUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Inheritance transformation. 
 * This transformation applies the inheritance rules defined IM Section 2.9. The transformation is necessary because a non-valid Policy may become valid 
 * upon application of these rules.
 * Given a set of policies (instances of Policy), this step applies the inheritance mechanism making the policies self-contained 
 * (i.e. after this step no inheritsFrom exists in the set of policies). The set of policies and their relations constitutes an acyclic directed graph, 
 * and it can be traversed in topological order so that every inheritance transformations is made in the correct order (see Kahn's or DFS algorithms). 
 * The original triple with inheritFrom is removed in this transformation (or moved to a metadata property).
 * @author vroddon
 */
public class TransformationN3 implements Transformation {

    @Override
    public Model transform(Model model) throws Exception {

        // Model nuevo = ModelFactory.createDefaultModel();
        Set<String> politicas = Preprocessing.getPoliticas(model);
        Map<Integer, String> mapa = new HashMap();
        Map<String, Integer> rmapa = new HashMap();
        PolicyGraph g = new PolicyGraph(politicas.size());
        Integer contador = 0;
        for (String politica : politicas) {
            mapa.put(contador, politica);
            rmapa.put(politica, contador);
            contador++;
        }
        //   System.out.println(Collections.singletonList(mapa)); 

        //Generamos el grafo!        
        for (String politica : politicas) {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            NodeIterator it = model.listObjectsOfProperty(rpolitica, ODRL.PINHERITFROM);
            while (it.hasNext()) {
                RDFNode nodo = it.nextNode();
                if (nodo.isLiteral()) {
                    throw new Exception("not valid. warning. inheritsFrom must be a URI");
                }
                String padre = nodo.asResource().getURI();
                if (!politicas.contains(padre)) {
                    throw new Exception("not valid. warning. inheritsFrom must be used pointing to an existing policy");
                   
                }
                g.addEdge(rmapa.get(padre), rmapa.get(politica));
//                System.out.println(politica +" <-- " + padre );
//                System.out.println(rmapa.get(politica) +" <-- " + rmapa.get(padre) +"          "+ (politica +" <-- " + padre));
            }
        }
        if (g.isCyclic()) {
            throw new Exception("not valid. Policy inheritance graph cannot be cyclic!");
        }

        //Aqui se ordena el grafo topológicamente
        int resultado[] = g.topologicalSort();

        List<String> orderedpolicies = new ArrayList();
        for (Integer i = 0; i < resultado.length; i++) {
            String politica = mapa.get(resultado[i]);
            orderedpolicies.add(politica);
   //         System.out.println(politica);
        }

        //Aquí se aplican las herencias.
        for (String politica : politicas) {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            List<Resource> padres = getPadresHerencia(model, rpolitica);
            for (Resource padre : padres) {
                Model racimopadre = RDFUtils.getRacimoExceptProperty(model, padre, new ArrayList());
                racimopadre = encastrar(racimopadre, rpolitica, padre);
                model.add(racimopadre);
            }
        }

        model.removeAll(null, ODRL.PINHERITFROM, null);

        return model;
    }
    
    private static List<Resource> getPadresHerencia(Model model, Resource hijo) {
        List<Resource> lis = new ArrayList();
        NodeIterator ni = model.listObjectsOfProperty(hijo, ODRL.PINHERITFROM);
        while (ni.hasNext()) {
            RDFNode n = ni.next();
            if (n.isResource()) {
                lis.add(n.asResource());
            }
        }
        return lis;
    }
    
    private static Model encastrar(Model model, Resource newpadre, Resource oldpadre) {
        StmtIterator sit = model.listStatements(new SimpleSelector(oldpadre, null, (RDFNode) null));
        while (sit.hasNext()) {
            Statement st = sit.next();
            Resource s = st.getSubject();
            Property p = st.getPredicate();
            RDFNode o = st.getObject();
            model.add(newpadre, p, o);
        }
        return model;
    }
    
}
