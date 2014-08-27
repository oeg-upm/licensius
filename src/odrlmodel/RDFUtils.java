package odrlmodel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.openjena.riot.Lang;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import odrlmodel.Action;
import odrlmodel.Constraint;


/**
 * Helper class with some useful methods to manipulate RDF
 * Not to be used by external users
 * @exclude
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class RDFUtils {


    String property="";
    String value="";
    
    public static Property TITLE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/title");
    public static Property COMMENT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#comment");
    public static Property RIGHTS = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/rights");
    public static Property RLICENSE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/license");
    public static Property LABEL = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    public static Property SEEALSO= ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");

    public static Resource RDATASET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/dcat#Dataset");
    public static Resource RLINKSET = ModelFactory.createDefaultModel().createResource("http://rdfs.org/ns/void#Linkset");
    
    public static OntModel coreModel;
    static {
        try {
            initModel(true);
        } catch (Exception e) {
            System.err.println("ODRL ontology could not be loaded");
        }
    }
    /**
     * Loads the models.
     * - The ODRL model
     * - The CCREL model
     * @param Whether the file is locally loaded or from the oficial URI
     */
    private static void initModel(boolean local) throws IOException
    {
            coreModel = ModelFactory.createOntologyModel();
            InputStream in1 =null, in2=null;
            if (local==false)
            {
                in1 = FileManager.get().open("http://w3.org/ns/odrl/2/ODRL20.rdf");
                in2 = FileManager.get().open("http://web.resource.org/cc/");            
            }
            else
            {
                in1 = FileManager.get().open("../schemas/ODRL20.rdf");
                in2 = FileManager.get().open("../schemas/ccrel.rdf");
            }
            coreModel.read(in1, "");
            coreModel.read(in2, "");
            in1.close();in2.close();
    }    
    /**
     * Gets the ODRL core actions
     */
    public static List<String> getCoreODRLActions() {
        List<String> actions = new ArrayList();
        OntClass action = coreModel.getOntClass("http://www.w3.org/ns/odrl/2/Action");
        ExtendedIterator it = action.listInstances();
        while (it.hasNext()) {
            Individual saction = (Individual) it.next();
            String comment = RDFUtils.getFirstPropertyValue(saction, RDFUtils.COMMENT);
            String label = RDFUtils.getFirstPropertyValue(saction, RDFUtils.LABEL);
            actions.add(label);
        }
        return actions;
    }

    public static String getIndividualLabel(String uri) {
        Individual ind = coreModel.getIndividual(uri);
        String label = RDFUtils.getFirstPropertyValue(ind, RDFUtils.LABEL);
        return label;
    }

    public static String getIndividualComment(String uri) {
        Individual ind = coreModel.getIndividual(uri);
        String label = RDFUtils.getFirstPropertyValue(ind, RDFUtils.COMMENT);
        return label;
    }

    /**
     * Creates an action with the metadata properties defined in the core model
     */
    public static Action enrichAction(Action action1) {
        Action action = new Action(action1);
        Resource r = coreModel.getResource(action.uri);
        if (r!=null)
        {
            List<String> labels = RDFUtils.getAllPropertyStrings(r, RDFUtils.LABEL);
            for(String label : labels)
                action.addLabel(label);
            action.title = RDFUtils.getFirstPropertyValue(r, RDFUtils.TITLE);
            action.comment=RDFUtils.getFirstPropertyValue(r, RDFUtils.COMMENT);
            action.seeAlso=RDFUtils.getFirstPropertyValue(r, RDFUtils.SEEALSO);
        }
        return action;
    }
    
    /**
     * Creates a constraints with the metadata properties defined in the core model
     */
    public static Constraint enrichConstraint(Constraint coriginal) {
        Constraint action = new Constraint(coriginal);
        Resource r = coreModel.getResource(action.uri);
        if (r!=null)
        {
            List<String> labels = RDFUtils.getAllPropertyStrings(r, RDFUtils.LABEL);
            for(String label : labels)
                action.addLabel(label);
            action.comment=RDFUtils.getFirstPropertyValue(r, RDFUtils.COMMENT);
            action.seeAlso=RDFUtils.getFirstPropertyValue(r, RDFUtils.SEEALSO);
        }
        return action;
    }
    @Override
    public String toString() {
        String s = "";
        StmtIterator eit = coreModel.listStatements();
        while (eit.hasNext()) {
            Statement oc = (Statement) eit.next();
            s += oc;
        }
        return s;
    }
    
    /**
     * Retrieves a reduced set of actions for demonstration purposes
     */
    public static List<Action> getBasicActions()
    {
        List<Action> actions = new ArrayList();
        Action action = new Action("http://www.w3.org/ns/odrl/2/reproduce");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/distribute");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/derive");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/copy");
        action = enrichAction(action);
        actions.add(action);
        
        return actions;
    }    
    
    /**
     * Retrieves a reduced set of actions for demonstration purposes
     */
    public static List<Action> getBasicDuties()
    {
        List<Action> actions = new ArrayList();
        Action action = new Action("http://www.w3.org/ns/odrl/2/pay");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://creativecommons.org/ns#ShareAlike");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/attribute");
        action = enrichAction(action);
        actions.add(action);
        
        return actions;
    }    
    
    /**
     * Retrieves a reduced set of constraints for demonstration purposes
     */
    public static List<Constraint> getBasicConstraints()
    {
        List<Constraint> actions = new ArrayList();
        Action a = new Action();
        Constraint c = new Constraint("dateTime");
        c = enrichConstraint(c);
        actions.add(c);
        
        return actions;
    }    
    
    
    public static void print(Model modelo)
    {
        RDFDataMgr.write(System.out, modelo, Lang.TURTLE) ;
    }
    
    
    public static boolean save(Model modelo, String filename)
    {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
//            RDFDataMgr.write(System.out, modelo, Lang.TURTLE) ;
            RDFDataMgr.write(out, modelo, Lang.TURTLE) ;
            out.close();
        } catch (IOException ex) {
            Logger.getLogger("licenser").warn("Error saving file. " + ex.getMessage());
            return false;
        }
        return true;
    }
    
    
    
    public static boolean save(String modelo, String filename)
    {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(modelo);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger("licenser").warn("Error saving file. " + ex.getMessage());
            return false;
        }
        return true;
    }
    
 

    static boolean isOfKind(Resource rrule, String uri) {
        if (rrule==null || uri==null || uri.isEmpty() || rrule.getURI()==null)
            return false;
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.add(coreModel);
        Individual res = ontModel.getIndividual(rrule.getURI());
        OntClass clase = ontModel.getOntClass(uri);
        if (res==null || clase==null)
            return false;
        return res.hasOntClass(clase);
        
    }
    
    /**
     * Returns a CreativeCommons rule from a Resource
     * @param rpolicy A Jena resource with a CreativeCommons resource
     * @return an ODRL Rule compatible 
     */
    public static Rule findCreativeCommons(Resource rpolicy)
    {
        List<String> permisosCC = RDFUtils.getAllPropertyStrings(rpolicy, ODRLRDF.PCCPERMISSION);
        List<String> constraintsCC = RDFUtils.getAllPropertyStrings(rpolicy, ODRLRDF.PCCREQUIRES);
        //IF HANDLING A CREATIVE COMMONS CASE
        if (!permisosCC.isEmpty()) {
            Rule rulecc = new Rule();
            rulecc.uri = "";
            for (String permisoCC : permisosCC) {
                Action actionCC = new Action(permisoCC);
                actionCC = enrichAction(actionCC);
                rulecc.actions.add(actionCC);
            }
            for (String sconstraint : constraintsCC) {
                Constraint constraint = new Constraint(sconstraint);
                constraint=enrichConstraint(constraint);
                rulecc.constraints.add(constraint);
            }
            return rulecc;
        }    
        return null;
    }    
        

    /**
     * Gets a list of resources of the given type
     * 
     * @param model Jena model
     * @param resource Given resource
     * @return List of resources
     */
    public static List<Resource> getOntResourcesOfType(Model model, Resource resource)
    {
        List<Resource> res=new ArrayList();
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.add(model);
        ontModel.add(coreModel);
        
       // RDFUtils.print(ontModel);
        
        Resource r2 = ontModel.createResource(resource.getURI());
        ExtendedIterator it=ontModel.listIndividuals(r2);
        while(it.hasNext())
        {
            Individual ind = (Individual)it.next();
            if (ind.isResource())
                res.add(ind);
        }
        return res;
    }   
            
    /**
     * Obtiene todos los recursos que son de un tipo dado
     */
    public static List<Resource> getResourcesOfType(Model model, Resource resource)
    {
        List<Resource> policies = new ArrayList();
        ResIterator it = model.listResourcesWithProperty(RDF.type, resource);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            String objeto = s.getObject().asResource().getURI();
            Resource sujeto = s.getSubject();
            policies.add(sujeto);
        }        
        return policies;
    }   
    
    /**
     * Get 
     */
    public static List<String> getObjectsOfType(Model model, Resource resource)
    {
        List<String> policies = new ArrayList();
        ResIterator it = model.listResourcesWithProperty(RDF.type, resource);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            String objeto = s.getObject().asResource().getURI();
            Resource sujeto = s.getSubject();
            policies.add(sujeto.getURI());
        }        
        return policies;
    }      
    
    public static List<String> getObjectsOfType(Model model, String uri)
    {
        List<String> policies = new ArrayList();

        ResIterator it = model.listSubjectsWithProperty(RDF.type);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            String objeto = s.getObject().asResource().getURI();
            if (s.getObject().isURIResource())
            {
                String sujeto = s.getSubject().getURI();
                String ouri=s.getObject().asResource().getURI();
                if(uri.equals(ouri))
                    policies.add(sujeto);
            }
        }        
        return policies;
    }       
    
    /**
     * Gets the first value of a property
     */
    public static String getFirstPropertyValue(Resource resource, Property property)
    {
        String value = "";
        StmtIterator it = resource.listProperties(property);
        if (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            value = nodo.toString();
            return value;
        }
        return "";
    }
    
    /**
     * Gets all the values of the given property. It assumes that all the values are strings.
     * @param resource
     * @param property
     */
    public static List<String> getAllPropertyStrings(Resource resource, Property property)
    {
        List<String> cadenas = new ArrayList();
        StmtIterator it = resource.listProperties(property);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            String rtitle = nodo.toString();
            cadenas.add(rtitle);
        }
        return cadenas;
    }
    
    
    public static List<Resource> getAllPropertyResources(Resource resource, Property property)
    {
        List<Resource> cadenas = new ArrayList();
        StmtIterator it = resource.listProperties(property);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            if (nodo.isResource())
            {
                Resource re = (Resource)nodo;
                cadenas.add(re);
            }
        }
        return cadenas;
    }    
    
}
