package odrlmodel;

//JAVA
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

//JENA
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Interface to serialize ODRL2.0 Expressions from / to RDF.
 * 
 * @author Victor
 */
public class ODRLRDF {
 
    /**
     * Serializes the policy into a RDF string
     * 
     * @param policy A given policy
     * @return The serialization language for the policy. For example, Lang.TTL
     */
    public static String getRDF(Policy policy, org.apache.jena.riot.Lang lang) {
        Resource r = getResourceFromPolicy(policy);
        Model model = ModelFactory.createDefaultModel();
        addPrefixesToModel(model);
        model.add(r.getModel());
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model,lang);
        String s = sw.toString();
        return s;
    }   
    
    /**
     * Gets the JENA resource from a policy
     * @param policy Policy
     */
    private static Resource getResourceFromPolicy(Policy policy)
    {
        String s = "";
        Model model = ModelFactory.createDefaultModel();
        addPrefixesToModel(model);

        //CREATION OF THE POLICY
        Resource rpolicy = model.createResource(policy.uri.toString());
        rpolicy.addProperty(RDF.type, ODRLRDF.RPOLICY);

        //HANDLING OF THE TYPE OF THE POLICY
        if (policy.getType()==Policy.POLICY_REQUEST) 
            rpolicy.addProperty(RDF.type, ODRLRDF.REQUEST);
        else if (policy.getType()==Policy.POLICY_OFFER) 
            rpolicy.addProperty(RDF.type, ODRLRDF.OFFER);
        else if (policy.getType()==Policy.POLICY_AGREEMENT) 
            rpolicy.addProperty(RDF.type, ODRLRDF.RAGREEMENT);
        else if (policy.getType()==Policy.POLICY_TICKET) 
            rpolicy.addProperty(RDF.type, ODRLRDF.RTICKET);
        else  
            rpolicy.addProperty(RDF.type, ODRLRDF.RSET);


        //COMMON METADATA
        rpolicy=setResourceMetadata(policy, rpolicy);

        //RULES
        for (Rule r : policy.rules) {
            Resource rrule2 = getResourceFromRule(r);
            if (r.getClass().equals(Permission.class))
                rpolicy.addProperty(ODRLRDF.PPERMISSION, rrule2);
            if (r.getClass().equals(Duty.class))
                rpolicy.addProperty(ODRLRDF.PDUTY, rrule2);
            if (r.getClass().equals(Prohibition.class))
                rpolicy.addProperty(ODRLRDF.PPROHIBITION, rrule2);
            model.add(rrule2.getModel());
        }

        Model mx = ModelFactory.createDefaultModel();
        mx.add(rpolicy.getModel());
        return rpolicy;
    }

/******************* PRIVATE METHODS ******************************************/    
    
    /**
     * Gets a Jena Resource from an action
     * @param duty Duty in the ODRL2.0 Model
     * @return A Jena action
     */
    private static Resource getResourceFromAction(Action action)
    {
        Model model = ModelFactory.createDefaultModel();
        Resource raction = model.createResource(action.uri.toString());
        raction.addProperty(RDF.type, ODRLRDF.RACTION);
        return raction;
    }
    
    /**
     * Gets a Jena Resource from a party
     * @param duty Duty in the ODRL2.0 Model
     * @return A Jena action
     */
    private static Resource getResourceFromParty(Party party)
    {
        Model model = ModelFactory.createDefaultModel();
        Resource raction = model.createResource(party.uri.toString());
        raction.addProperty(RDF.type, ODRLRDF.RPARTY);
        return raction;
    }
    
    /**
     * Gets a Jena Resource from a a constraint
     * @param constraint Constraint in the ODRL2.0 Model
     * @return A Jena constraint
     */
    private static Resource getResourceFromConstraint(Constraint constraint)
    {
        Model model = ModelFactory.createDefaultModel();
        Resource rconstraint = constraint.isAnon() ? model.createResource() : model.createResource(constraint.uri.toString());
        rconstraint.addProperty(RDF.type, ODRLRDF.RCONSTRAINT);
        
        Property prho = model.createProperty(constraint.rightOperand);
        rconstraint.addProperty(prho, constraint.value);
        rconstraint.addProperty(ODRLRDF.POPERATOR,  model.createResource(constraint.operator));
        
        return rconstraint;
    }
    
    
    /**
     * Gets a Jena Resource from a rule
     * @param permission Permission in the ODRL2.0 Model
     * @return A Jena permission
     */
    private static Resource getResourceFromRule(Rule rule)
    {
        Model model = ModelFactory.createDefaultModel();
        Resource rrule = rule.isAnon() ? model.createResource() : model.createResource(rule.uri.toString());
        
        if (rule.getKindOfRule()==Rule.RULE_PERMISSION) 
            rrule.addProperty(RDF.type, ODRLRDF.RPERMISSION);
        if (rule.getKindOfRule()==Rule.RULE_DUTY) 
            rrule.addProperty(RDF.type, ODRLRDF.RDUTY);
        if (rule.getKindOfRule()==Rule.RULE_PROHIBITION) 
            rrule.addProperty(RDF.type, ODRLRDF.RPROHIBITION);
            
        //TARGET, ASSIGNER, ASSIGNEE
        if (!rule.target.isEmpty()) 
            rrule.addProperty(ODRLRDF.PTARGET, rule.target);
        if (rule.getAssigner()!=null)
        {
            Resource rparty =getResourceFromParty(rule.getAssigner());
            rrule.addProperty(ODRLRDF.PASSIGNER, rparty);
            model.add(rparty.getModel());
        }
        if (rule.getAssignee()!=null)
        {
            Resource rparty =getResourceFromParty(rule.getAssignee());
            rrule.addProperty(ODRLRDF.PASSIGNEE, rparty);
            model.add(rparty.getModel());
        }
        
        //ACTIONS
        for (Action a : rule.actions) {
            Resource raction = getResourceFromAction(a);
            rrule.addProperty(ODRLRDF.PACTION, raction);
        }        
        
        //CONSTRAINTS
        for(Constraint c : rule.constraints)
        {
            Resource rconstraint = getResourceFromConstraint(c);
            rrule.addProperty(ODRLRDF.PCONSTRAINT, rconstraint);
            model.add(rconstraint.getModel());
        }
        
        
        //DUTIES (ONLY FOR PERMISSIONS)
        if (rule.getClass().equals(Permission.class))
        {
            Permission permiso = (Permission)rule;
            List<Duty> duties=permiso.getDuties();
            for(Duty duty : duties)
            {
                Resource rduty = ODRLRDF.getResourceFromRule(duty);
                model.add(rduty.getModel());
                rrule.addProperty(ODRLRDF.PDUTY, rduty);
            }
        }
        
        return rrule;
    }
    
    
    /**
     * Sets the resource metadata from the given MetadataObject
     * @param me MetadataObject
     * @param resource Input resource
     */
    private static Resource setResourceMetadata(MetadataObject me, Resource resource)
    {
        if (!me.comment.isEmpty()) {
            resource.addProperty(ODRLRDF.COMMENT, me.comment);
        }
        if (!me.title.isEmpty()) {
            resource.addProperty(ODRLRDF.TITLE, me.title);
        }   
        if (!me.getLabel("en").isEmpty()) {
            resource.addProperty(ODRLRDF.LABEL, me.getLabel("en"));
        }   
        if (!me.seeAlso.isEmpty()) {
            resource.addProperty(ODRLRDF.SEEALSO, me.seeAlso);
        }   
        return resource;
    }    
    
    /**
     * Adds the most common prefixes to the generated model
     */
    private static void addPrefixesToModel(Model model)
    {
        model.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/"); //http://w3.org/ns/odrl/2/
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("cc", "http://creativecommons.org/ns#");
        model.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        model.setNsPrefix("void", "http://rdfs.org/ns/void#");
        model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        model.setNsPrefix("gr", "http://purl.org/goodrelations/");
        model.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
    }    
    
    private static Resource RPOLICY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Policy");
    private static Resource RLICENSE = ModelFactory.createDefaultModel().createResource("http://purl.org/dc/terms/LicenseDocument");
    private static Resource RCCLICENSE = ModelFactory.createDefaultModel().createResource("http://creativecommons.org/ns#License");
    private static Resource RSET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Set");
    private static Resource OFFER = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Offer");
    private static Resource REQUEST = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Request");
    private static Resource RAGREEMENT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Agreement");
    private static Resource RTICKET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Ticket");
    private static Resource RPERMISSION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Permission");
    private static Resource RPROHIBITION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Prohibition");
    private static Property PPROHIBITION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/prohibition");
    private static Resource RDUTY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Duty");
    private static Resource RCONSTRAINT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Constraint");
    private static Resource RACTION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Action");
    private static Resource RPARTY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Party");
    private static Property PPERMISSION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/permission");
    private static Property PTARGET = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/target");
    private static Property PASSIGNER = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assigner");
    private static Property PASSIGNEE = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assignee");
    private static Property PACTION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/action");
    private static Property PDUTY = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/duty");
    private static Property PCONSTRAINT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/constraint");
    private static Property PCOUNT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/count");
    private static Property POPERATOR = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/operator");
    private static Property PCCPERMISSION = ModelFactory.createDefaultModel().createProperty("http://creativecommons.org/ns#permits");
    private static Property PCCPERMISSION2 = ModelFactory.createDefaultModel().createProperty("http://web.resource.org/cc/permits");
    private static Property PCCREQUIRES = ModelFactory.createDefaultModel().createProperty("http://creativecommons.org/ns#requires");
    private static Resource RPAY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/pay");
    private static Property RDCLICENSEDOC = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/LicenseDocument");
    private static Property PAMOUNTOFTHISGOOD = ModelFactory.createDefaultModel().createProperty("http://purl.org/goodrelations/amountOfThisGood");
    private static Property PUNITOFMEASUREMENT = ModelFactory.createDefaultModel().createProperty("http://purl.org/goodrelations/UnitOfMeasurement");
    private static Property PDCLICENSE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/license");
    private static Property PDCRIGHTS = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/rights");
    private static Property PWASGENERATEDBY = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/prov#wasGeneratedBy");
    private static Property PWASASSOCIATEDWITH = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/prov#wasAssociatedWith");
    private static Property PENDEDATTIME = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/prov#endedAtTime");
    private static Property PINDUSTRY = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/industry");
    private static Property PSPATIAL = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/spatial");
    private static Literal LEQ = ModelFactory.createDefaultModel().createLiteral("http://www.w3.org/ns/odrl/2/eq");    
    private static Property TITLE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/title");
    private static Property COMMENT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#comment");
    private static Property RIGHTS = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/rights");
    private static Property LABEL = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    private static Property SEEALSO= ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
    private static Resource RDATASET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/dcat#Dataset");
    private static Resource RLINKSET = ModelFactory.createDefaultModel().createResource("http://rdfs.org/ns/void#Linkset");    
}
