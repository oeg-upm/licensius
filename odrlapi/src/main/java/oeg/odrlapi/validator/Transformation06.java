package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import oeg.odrlapi.rdf.ODRLParser;
import oeg.odrlapi.rdf.ODRLRule;
import oeg.odrlapi.rdf.RDFSugar;
import oeg.odrlapi.rdf.RDFUtils;

import static oeg.odrlapi.validator.Preprocessing.getReglas;
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
 * De-composes complex rules.
 * @author vroddon
 */
public class Transformation06 implements Transformation {

    @Override
    public Model transform(Model model) throws Exception {
        Set<Resource> politicas = ODRLParser.getPolicies(model);
        for(Resource politica : politicas)
        {
            Set<Model> rules = ODRLParser.getRootRules(model, politica);
            for(Model rule : rules)
            {
                Resource ruleid = ODRLParser.getRuleId(rule, politica);
                Set<Resource> assets = RDFSugar.getObjects(model, ruleid, ODRL.PTARGET);
                if (assets.size() > 1)
                {
                    model = desdoblar(model, ruleid, ODRL.PTARGET, politica);
                }
            }
            rules = ODRLParser.getRootRules(model, politica);
            for(Model rule : rules)
            {
                Resource ruleid = ODRLParser.getRuleId(rule, politica);
                Set<Resource> assets = RDFSugar.getObjects(model, ruleid, ODRL.PASSIGNER);
                if (assets.size() > 1)
                {
                    model = desdoblar(model, ruleid, ODRL.PASSIGNER, politica);
                }
            }
            rules = ODRLParser.getRootRules(model, politica);
            for(Model rule : rules)
            {
                Resource ruleid = ODRLParser.getRuleId(rule, politica);
                Set<Resource> assets = RDFSugar.getObjects(model, ruleid, ODRL.PASSIGNEE);
                if (assets.size() > 1)
                {
                    model = desdoblar(model, ruleid, ODRL.PASSIGNEE, politica);
                }
            }
            
            rules = ODRLParser.getRootRules(model, politica);
            for(Model rule : rules)
            {
                Resource ruleid = ODRLParser.getRuleId(rule, politica);
                Set<Resource> assets = RDFSugar.getObjects(model, ruleid, ODRL.PACTION);
                if (assets.size() > 1)
                {
                    model = desdoblar(model, ruleid, ODRL.PACTION, politica);
                }
            }            
            
        }
        return model;
    }
    
    //no es perfecto, ojo, funciona solo al primer nivel
    /**
     * @param prop ODRL.PTARGET
     */
    public static Model desdoblar(Model model, Resource regla, Property prop, Resource politica)
    {
        Model racimo = RDFUtils.getRacimo(model, regla, new ArrayList<Resource>());
        Set<Resource> sassets = RDFSugar.getObjects(model, regla, prop);
        Property TIPO = getTipoRegla(model,regla);
        
        List<Resource> assets = new ArrayList();
        for(Resource r : sassets)
            assets.add(r);

        //borro todo el racimo
        Set<Statement> aborrar = RDFUtils.getSetOfStatements(model, new SimpleSelector(null, prop, (RDFNode)null));
        model = model.remove(racimo);  //VVV
        for(Statement borra : aborrar)
        {
            racimo.remove(borra);
//            model.remove(borra);
        }
        
        int tam = assets.size();
        List<Model> racimos = copiar(racimo, tam);
        for(int i =0;i<tam;i++)
        {
            Model m = racimos.get(i);
            Resource anoni = ResourceFactory.createResource();
            m.add(regla, prop, assets.get(i));
            m = replace(m,regla ,anoni);
            
            model.add(m);
            model.add(politica, TIPO , anoni);
//            System.out.println("RACIM:\n" + RDFUtils.getString(m));
        }
   //vvv     model = model.remove(racimo);
        model = model.remove(politica, TIPO, regla);
        
        return model;
    }
    public static Model replace(Model model, Resource rvieja, Resource rnueva)
    {
        Model model2 = ModelFactory.createDefaultModel();
        StmtIterator si = model.listStatements();
        while (si.hasNext())
        {
            Statement st = si.nextStatement();
            Resource s = st.getSubject();
            Property p = st.getPredicate();
            RDFNode o = st.getObject();
            if (s.equals(rvieja))
                s = rnueva;
            if (o.isResource() && o.asResource().equals(rvieja))
                o = rnueva;
            model2.add(s, p, o);
        }
        return model2;
    }
    
    public static Property getTipoRegla(Model model, Resource regla)
    {
        NodeIterator ni = model.listObjectsOfProperty(regla, RDF.type);
        while(ni.hasNext())
        {
            Resource r = ni.next().asResource();
            if (r.getURI().equals(ODRL.RPERMISSION.getURI()))
                return ODRL.PPERMISSION;
            if (r.getURI().equals(ODRL.RDUTY.getURI()))
                return ODRL.POBLIGATION;
            if (r.getURI().equals(ODRL.RPROHIBITION.getURI()))
                return ODRL.PPROHIBITION;
        }
        return null;
    }

    public static List<Model> copiar(Model model, int copias)
    {
        List<Model> modelos = new ArrayList();
        for(int i=0;i<copias;i++)
        {
            Model mi = ModelFactory.createDefaultModel();
            modelos.add(mi);
        }
        StmtIterator li = model.listStatements();
        while(li.hasNext())
        {
            Statement s = li.next();
            for(int i=0;i<copias;i++)
            {
                modelos.get(i).add(s);
            }
        }
        return modelos;
    }    
    

    
}
