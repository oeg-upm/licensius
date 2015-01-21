package odrlmodel.rdf;

import odrlmodel.rdf.RDFUtils;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
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
 * This class represents the ODRL2.0 Core Model as defined in the specification
 * @see http://www.w3.org/community/odrl/two/model/
 * Documentation in 
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class ODRLModel {

    public static OntModel coreModel;

    
    
    static {
        try {
            initModel(true);
        } catch (Exception e) {
            System.err.println("ODRL ontology could not be loaded");
        }
    }
    
    
    public static Resource RPOLICY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Policy");
    public static Resource RLICENSE = ModelFactory.createDefaultModel().createResource("http://purl.org/dc/terms/LicenseDocument");
    public static Resource RCCLICENSE = ModelFactory.createDefaultModel().createResource("http://creativecommons.org/ns#License");
    public static Resource RSET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Set");
    public static Resource OFFER = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Offer");
    public static Resource REQUEST = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Request");
    public static Resource RPERMISSION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Permission");
    public static Resource RPROHIBITION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Prohibition");
    public static Property PPROHIBITION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/prohibition");
    public static Resource RDUTY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Duty");
    public static Resource RCONSTRAINT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Constraint");
    public static Resource RACTION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Action");
    public static Property PPERMISSION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/permission");
    public static Property PTARGET = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/target");
    public static Property PASSIGNER = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assigner");
    public static Property PASSIGNEE = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assignee");
    public static Property PACTION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/action");
    public static Property PDUTY = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/duty");
    public static Property PCONSTRAINT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/constraint");
    public static Property PCOUNT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/count");
    public static Property POPERATOR = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/operator");
    public static Property PCCPERMISSION = ModelFactory.createDefaultModel().createProperty("http://creativecommons.org/ns#permits");
    public static Property PCCPERMISSION2 = ModelFactory.createDefaultModel().createProperty("http://web.resource.org/cc/permits");
    public static Property PCCREQUIRES = ModelFactory.createDefaultModel().createProperty("http://creativecommons.org/ns#requires");
    public static Resource RPAY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/pay");
    public static Property RDCLICENSEDOC = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/LicenseDocument");
    public static Property PAMOUNTOFTHISGOOD = ModelFactory.createDefaultModel().createProperty("http://purl.org/goodrelations/amountOfThisGood");
    public static Property PUNITOFMEASUREMENT = ModelFactory.createDefaultModel().createProperty("http://purl.org/goodrelations/UnitOfMeasurement");
    
    public static Property PDCLICENSE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/license");
    public static Property PDCRIGHTS = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/rights");
    public static Property PWASGENERATEDBY = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/prov#wasGeneratedBy");
    public static Property PWASASSOCIATEDWITH = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/prov#wasAssociatedWith");
    public static Property PENDEDATTIME = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/prov#endedAtTime");
    public static Property PLEGALCODE = ModelFactory.createDefaultModel().createProperty("http://creativecommons.org/ns#legalcode");
    
    
    public static Property PINDUSTRY = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/industry");
    public static Property PSPATIAL = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/spatial");
 //   public static Property POPERATOR = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/operator");
    public static Literal LEQ = ModelFactory.createDefaultModel().createLiteral("http://www.w3.org/ns/odrl/2/eq");
                


    
    public static void main(String args[]) {
        List<String> ls = ODRLModel.getCoreODRLActions();
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
        Resource r = ODRLModel.coreModel.getResource(action.uri);
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
        Resource r = ODRLModel.coreModel.getResource(action.uri);
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
        action = ODRLModel.enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/distribute");
        action = ODRLModel.enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/derive");
        action = ODRLModel.enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/copy");
        action = ODRLModel.enrichAction(action);
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
        action = ODRLModel.enrichAction(action);
        actions.add(action);
        action = new Action("http://creativecommons.org/ns#ShareAlike");
        action = ODRLModel.enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/attribute");
        action = ODRLModel.enrichAction(action);
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
        c = ODRLModel.enrichConstraint(c);
        actions.add(c);
        
        return actions;
    }
    
}
