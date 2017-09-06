package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oeg.odrlapi.rdf.RDFUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * Preprocesses a policy
 *
 * @author vroddon
 */
public class Preprocessing {

    public static String preprocess(String rdf) throws Exception {
        String out = "";

        Model model = ODRLValidator.getModel(rdf);
        if (model == null) {
            return rdf;
        }
        System.out.println("Paso 0: RDF de partida " + model.size());

        //PROCESSES ASSIGNEROF OUT OF THE POLICY
        List<String> lista = RDFUtils.getSubjectsForProperty(model, ODRL.PASSIGNEROF.getURI());
        for (String party : lista) {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PASSIGNEROF.getURI());
            for (String politica : politicas) {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                model.add(rpolitica, ODRL.PASSIGNER, rparty);
            }
        }

        //PROCESSES ASSIGNEEOF OUT OF THE POLICY
        lista = RDFUtils.getSubjectsForProperty(model, ODRL.PASSIGNEEOF.getURI());
        for (String party : lista) {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PASSIGNEEOF.getURI());
            for (String politica : politicas) {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                model.add(rpolitica, ODRL.PASSIGNEE, rparty);
            }
        }

        //PROCESSES HASPOLICY OUT OF THE POLICY
        lista = RDFUtils.getSubjectsForProperty(model, ODRL.PHASPOLICY.getURI());
        for (String party : lista) {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PHASPOLICY.getURI());
            for (String politica : politicas) {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                model.add(rpolitica, ODRL.PTARGET, rparty);
            }
        }

       System.out.println("Paso 1 terminado: Externo->Politica " + model.size());

        //MAKES SURE THAT THE IMPORTANT CLASSES ARE WELL INFERRED
  //      model = RDFUtils.inferClassFromRange(model, ODRL.PCONSTRAINT.getURI(), ODRL.RCONSTRAINT.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PPERMISSION.getURI(), ODRL.RPERMISSION.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PPROHIBITION.getURI(), ODRL.RPROHIBITION.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.POBLIGATION.getURI(), ODRL.RDUTY.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PDUTY.getURI(), ODRL.RDUTY.getURI());
        model = Preprocessing.addConstraintTypes(model);

        System.out.println("Paso 2 terminado: Regla recibe clase " + model.size());

        out = RDFUtils.getString(model);
//        System.out.println("INTERMEDIO :\n" + out);

        //INTRODUCES THE GENERAL PROPERTIES (DEFINED AT POLICY LEVEL) IN EACH RULE LEVEL.
        //asset, target, assigner, assignee
        model = interiorizar(model, ODRL.PTARGET.getURI());
        model = interiorizar(model, ODRL.PASSIGNER.getURI());
        model = interiorizar(model, ODRL.PASSIGNEE.getURI());
        model = interiorizar(model, ODRL.PACTION.getURI());

        System.out.println("Paso 3 terminado: Politica->Regla " + model.size());
        System.out.println("ANTES DE HEREDAR:\n" + RDFUtils.getString(model));
        model = heredar(model);
        System.out.println("Paso 3 terminado: Herencia realizada " + model.size());

        out = RDFUtils.getString(model);
        System.out.println("FINAL:\n" + RDFUtils.getString(model));
        return out;
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

    private static Model heredar(Model model) throws Exception {
        
        // Model nuevo = ModelFactory.createDefaultModel();
        List<String> politicas = getPoliticas(model);
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
                    throw new Exception("warning. inheritsFrom must be a URI");
                }
                String padre = nodo.asResource().getURI();
                if (!politicas.contains(padre)) {
                    throw new Exception("warning. inheritsFrom must be used pointing to an existing policy");
                }
                g.addEdge(rmapa.get(padre), rmapa.get(politica));
//                System.out.println(politica +" <-- " + padre );
//                System.out.println(rmapa.get(politica) +" <-- " + rmapa.get(padre) +"          "+ (politica +" <-- " + padre));
            }
        }
        if (g.isCyclic())
            throw new Exception("not valid. Policy inheritance graph cannot be cyclic!");
        

        //Aqui se ordena el grafo topológicamente
        int resultado[] = g.topologicalSort();

        List<String> orderedpolicies = new ArrayList();
        for (Integer i = 0; i < resultado.length; i++) {
            String politica = mapa.get(resultado[i]);
            orderedpolicies.add(politica);
            System.out.println(politica);
        }

        //Aquí se aplican las herencias.
        for (String politica : politicas) {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            List<Resource> padres = getPadresHerencia(model, rpolitica);
            for (Resource padre : padres) {
                Model racimopadre = RDFUtils.getRacimo(model, padre, new ArrayList());
                racimopadre = encastrar(racimopadre, rpolitica, padre);
                model.add(racimopadre);
            }
        }

        model.removeAll(null, ODRL.PINHERITFROM, null);

        return model;
    }

    private static Model interiorizar(Model model, String sproperty) {
        Property rpropiedad = ModelFactory.createDefaultModel().createProperty(sproperty);

        List<String> politicas = getPoliticas(model);
        for (String politica : politicas) {
            //COMIENZO DEL INTENTO DE OBTENER TODAS LAS REGLAS DE UNA POLITICA
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            List<Resource> rreglas = getReglasDirectas(model, rpolitica);
            //AHORA, PARA CADA UNA DE LAS PROPIEDADES QUE HAY QUE INTERIORIZAR...
            List<String> items = RDFUtils.getObjectsGivenSP(model, politica, sproperty);
            for (String item : items) {
                for (Resource rregla : rreglas) {
                    if (item.startsWith("http")) {
                        model.add(rregla, rpropiedad, ModelFactory.createDefaultModel().createResource(item));
                    } else {
                        model.add(rregla, rpropiedad, item);
                    }
                }
                if (item.startsWith("http")) {
                    model.remove(rpolitica, rpropiedad, ModelFactory.createDefaultModel().createResource(item));
                } else {
                    model.remove(rpolitica, rpropiedad, ModelFactory.createDefaultModel().createResource(item));
                }
            }
        }
        return model;

    }

    //This is ok, because policies MUST Have an ID
    static List<String> getPoliticas(Model model) {
        List<String> lista = new ArrayList();
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.RPOLICY));
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.RAGREEMENT));
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.ROFFER));
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.RSET));
        return lista;
    }

    public static List<Resource> getReglasDirectas(Model model, Resource rpolitica) {
        List<Resource> rreglas = new ArrayList();//quiero obetner toads las relgas
        NodeIterator it = model.listObjectsOfProperty(rpolitica, ODRL.PPERMISSION);
        while (it.hasNext()) {
            RDFNode nodo = it.nextNode();
            if (nodo.isResource()) {
                rreglas.add(nodo.asResource());
            }
        }
        it = model.listObjectsOfProperty(rpolitica, ODRL.POBLIGATION);
        while (it.hasNext()) {
            RDFNode nodo = it.nextNode();
            if (nodo.isResource()) {
                rreglas.add(nodo.asResource());
            }
        }
        it = model.listObjectsOfProperty(rpolitica, ODRL.PPROHIBITION);
        while (it.hasNext()) {
            RDFNode nodo = it.nextNode();
            if (nodo.isResource()) {
                rreglas.add(nodo.asResource());
            }
        }
        return rreglas;
    }

    /**
     * Determines if the constraints are Constraints or LogicalConstraints.
     * Asserts the RDF statement explicitly for its easier validation.
     * If the type is asserted, then nothing is done.
     * It works for every possible object of odrl:constraint or odrl:refinement 
     * @param model RDF model
     * @return Modified RDF model. 
     */
    private static Model addConstraintTypes(Model model) {
        NodeIterator ni = model.listObjectsOfProperty(ODRL.PREFINEMENT);
        List<Statement> nuevas = new ArrayList();
        while(ni.hasNext())
        {
            RDFNode node = ni.next();
            if (!node.isResource())
                continue;
            Resource rnode = node.asResource();
            StmtIterator li = model.listStatements(new SimpleSelector(rnode,null,(RDFNode)null));
            boolean normal = false;
            
            while(li.hasNext())
            {
                Statement st = li.next();
                Resource rpropiedad = st.getPredicate();
                int cuenta =0;
                if (rpropiedad.getURI().equals(RDF.type.getURI()))
                    continue;
                if (rpropiedad.getURI().equals(ODRL.POPERATOR))
                    cuenta++;
                if (rpropiedad.getURI().equals(ODRL.PLEFTOPERAND))
                    cuenta++;
                if (rpropiedad.getURI().equals(ODRL.PRIGHTOPERAND))
                    cuenta++;
                if (cuenta>0)
                {
                    normal=true;
                    Statement sn = ResourceFactory.createStatement(rnode, RDF.type, ODRL.RCONSTRAINT);
                    nuevas.add(sn);
                }
            }            
            if (!normal)
            {
                Statement sn = ResourceFactory.createStatement(rnode, RDF.type, ODRL.RLOGICALCONSTRAINT);
                nuevas.add(sn);
            }
        }  
        ni = model.listObjectsOfProperty(ODRL.PCONSTRAINT);
        while(ni.hasNext())
        {
            RDFNode node = ni.next();
            if (!node.isResource())
                continue;
            Resource rnode = node.asResource();
            StmtIterator li = model.listStatements(new SimpleSelector(rnode,null,(RDFNode)null));
            boolean normal = false;
            while(li.hasNext())
            {
                Statement st = li.next();
                Resource rpropiedad = st.getPredicate();
                int cuenta =0;
                if (rpropiedad.getURI().equals(RDF.type.getURI()))
                    continue;
                if (rpropiedad.getURI().equals(ODRL.POPERATOR))
                    cuenta++;
                if (rpropiedad.getURI().equals(ODRL.PLEFTOPERAND))
                    cuenta++;
                if (rpropiedad.getURI().equals(ODRL.PRIGHTOPERAND))
                    cuenta++;
                if (cuenta>0)
                {
                    normal=true;
                    Statement sn = ResourceFactory.createStatement(rnode, RDF.type, ODRL.RCONSTRAINT);
                    nuevas.add(sn);
                    
                }
            }
            if (!normal)
            {
                    Statement sn = ResourceFactory.createStatement(rnode, RDF.type, ODRL.RLOGICALCONSTRAINT);
                    nuevas.add(sn);
                    
            }
        }  
        for(Statement st : nuevas)
            model.add(st);

         
        
        return model;
    }

}
