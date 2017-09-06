package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oeg.odrlapi.rdf.RDFUtils;
import oeg.odrlapi.rest.server.resources.ValidatorResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import oeg.odrlapi.rdf.*;

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
        //PROCESSES ASSIGNEROF OUT OF THE POLICY
        List<String> lista = RDFUtils.getSubjectsForProperty(model, ODRL.PASSIGNEROF.getURI());
        for (String party : lista) {
            Resource rparty = ModelFactory.createDefaultModel().createResource(party);
            List<String> politicas = RDFUtils.getObjectsGivenSP(model, party, ODRL.PASSIGNEROF.getURI());
            for (String politica : politicas) {
                Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
                System.out.println(model.size());
                model.add(rpolitica, ODRL.PASSIGNER, rparty);
                System.out.println(rpolitica.getURI() + " - " + ODRL.PASSIGNER.getURI() + " - " + rparty.getURI());
                System.out.println(model.size());
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

        //MAKES SURE THAT THE IMPORTANT CLASSES ARE WELL INFERRED
        model = RDFUtils.inferClassFromRange(model, ODRL.PCONSTRAINT.getURI(), ODRL.RCONSTRAINT.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PPERMISSION.getURI(), ODRL.RPERMISSION.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PPROHIBITION.getURI(), ODRL.RPROHIBITION.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.POBLIGATION.getURI(), ODRL.RDUTY.getURI());
        model = RDFUtils.inferClassFromRange(model, ODRL.PDUTY.getURI(), ODRL.RDUTY.getURI());

        out = RDFUtils.getString(model);
//        System.out.println("INTERMEDIO :\n" + out);

        //INTRODUCES THE GENERAL PROPERTIES (DEFINED AT POLICY LEVEL) IN EACH RULE LEVEL.
        //asset, target, assigner, assignee
        model = interiorizar(model, ODRL.PTARGET.getURI());
        model = interiorizar(model, ODRL.PASSIGNER.getURI());
        model = interiorizar(model, ODRL.PASSIGNEE.getURI());
        model = interiorizar(model, ODRL.PACTION.getURI());

        model = heredar(model);

        out = RDFUtils.getString(model);
        System.out.println("PREPROCESADO:\n" + out);
        return out;
    }

    private static Model heredar(Model model) throws Exception {
        List<String> politicas = getPoliticas(model);
        Map<Integer, String> mapa = new HashMap();
        Map<String, Integer> rmapa = new HashMap();
        PolicyGraph g = new PolicyGraph(politicas.size());
        Integer contador=0;
        for (String politica : politicas) {
            mapa.put(contador, politica);
            rmapa.put(politica,contador);
            contador++;
        }
        System.out.println(Collections.singletonList(mapa)); // method 2

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
                if (!politicas.contains(padre))
                    throw new Exception("warning. inheritsFrom must be used pointing to an existing policy");
                g.addEdge(rmapa.get(padre),rmapa.get(politica));
//                System.out.println(politica +" <-- " + padre );
                System.out.println(rmapa.get(politica) +" <-- " + rmapa.get(padre) +"          "+ (politica +" <-- " + padre));
            }
        }
        
        //Aqui se ordena el grafo topológicamente
        int resultado[]=g.topologicalSort();
        
        List<String> orderedpolicies = new ArrayList();
        //Aquí ya está ordenado!
        for(Integer i=0;i<resultado.length;i++)
        {
            String politica = mapa.get(resultado[i]);
            orderedpolicies.add(politica);
            System.out.println(politica);
        }
        
        for (String politica : politicas) {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            NodeIterator it = model.listObjectsOfProperty(rpolitica, ODRL.PINHERITFROM);
            while (it.hasNext()) {
                RDFNode nodo = it.nextNode();
                Resource rpadre = nodo.asResource();
                List<Resource> rreglas = getReglasDirectas(model, rpolitica);

            }
        }
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

    private static List<Resource> getReglasDirectas(Model model, Resource rpolitica) {
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

}
