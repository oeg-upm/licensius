package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import oeg.odrlapi.rdf.ODRLParser;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rdf.RDFUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * Preprocesses a policy.
 * @author vroddon
 */
public class Preprocessing {

    public static String preprocess(String rdf) throws Exception {
        String out = "";
        Model model = RDFSugar.getModel(rdf);
        if (model == null) {
            return rdf;
        }

        model = new TransformationN02a().transform(model);
        
        model = new TransformationN01().transform(model);

        model = new TransformationN02b().transform(model);

        model = new TransformationN06().transform(model);
        
        model = new TransformationN07().transform(model);
        
        model = new TransformationN03().transform(model);

        model = new TransformationN04().transform(model);
        
        System.out.println("1 "+RDFUtils.getString(model));
        model = new TransformationN05().transform(model);
        System.out.println("2 "+ RDFUtils.getString(model));

        out = RDFUtils.getString(model);
        
   //     System.out.println(out);
        return out;
    }


    //This is ok, because policies MUST Have an ID
    static Set<String> getPoliticas(Model model) {
        Set<String> lista = new HashSet();
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.RPOLICY));
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.RAGREEMENT));
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.ROFFER));
        lista.addAll(RDFUtils.getSubjectOfType(model, ODRL.RSET));
        return lista;
    }

    public static Set<Resource> getDirectRules(Model model, Resource rpolitica) {
        Set<Resource> rreglas = new HashSet();//quiero obetner toads las relgas
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


/*
    public static Model policy2Set(Model model) {
        Set<String> politicas = getPoliticas(model);
        for (String politica : politicas) {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            NodeIterator nx = model.listObjectsOfProperty(rpolitica, RDF.type);
            int count=0;
            while (nx.hasNext()) {
                RDFNode nodex = nx.next();
                if (!nodex.isResource()) {
                    continue;
                }
                Resource tipo = nodex.asResource();
                if (tipo.getURI().equals(ODRL.RSET.getURI()))
                    count++;
                if (tipo.getURI().equals(ODRL.ROFFER.getURI()))
                    count++;
                if (tipo.getURI().equals(ODRL.RAGREEMENT.getURI()))
                    count++;
            }
            if (count==0)
                model.add(rpolitica, RDF.type, ODRL.RSET);
        }

        return model;
    }*/



    

    
    
    
    public static Set<Resource> getReglas(Model model){
        Set<Resource> reglas = new HashSet();
        Set<String> politicas = getPoliticas(model);
        for(String politica : politicas)
        {
            Resource rpolitica = ModelFactory.createDefaultModel().createResource(politica);
            Set<Resource> rreglas = getDirectRules(model, rpolitica);
            reglas.addAll(rreglas);
        }
        return reglas;
    }
    

}
