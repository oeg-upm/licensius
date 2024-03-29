package oeg.jodrlapi.helpers;

//JAVA
import java.io.StringWriter;
import java.util.List;

//JENA
//import org.apache.jena.riot.RDFDataMgr;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import oeg.jodrlapi.JODRLApiSettings;
import oeg.jodrlapi.odrlmodel.Action;
import oeg.jodrlapi.odrlmodel.Constraint;
import oeg.jodrlapi.odrlmodel.Duty;
import oeg.jodrlapi.odrlmodel.Party;
import oeg.jodrlapi.odrlmodel.Permission;
import oeg.jodrlapi.odrlmodel.Policy;
import oeg.jodrlapi.odrlmodel.Prohibition;
import oeg.jodrlapi.odrlmodel.ResourceModel;
import oeg.jodrlapi.odrlmodel.Rule;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface to serialize ODRL2 Expressions from / to RDF.
 *
 * @author Victor
 */
public class ODRLRDF {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODRLRDF.class);

    /**
     * Serializes the policy into a RDF string
     *
     * @param policy A given policy
     * @param lang
     * @return The serialization language for the policy. For example, Lang.TTL
     */
    public static String getRDF(Policy policy, org.apache.jena.riot.Lang lang) {
        Resource r = getResourceFromPolicy(policy);
        Model model = ModelFactory.createDefaultModel();
        model.add(r.getModel());
        addPrefixesToModelIfNeeded(model);
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, lang);
        String s = sw.toString();
        return s;
    }
    
    public static List<Policy> loadFromRDF(String rdf)
    {
        List<Policy> politicas = new ArrayList();
        Model model = RDFUtils.parseFromText(rdf);
        List<Resource> ls = ODRLRDF.findPolicies(model);
        for (Resource rpolicy : ls) {
            Policy policy = ODRLRDF.getPolicyFromResource(rpolicy);
            policy.fileName = "unk";
            politicas.add(policy);
        }
        return politicas;
    }
    /**
     * Loads the ODRL2.2 policies found in a file or uri.
     * @param uri URI or local file location
     * @return A set of policies
     */
    public static List<Policy> loadFromURI(String path)
    {
        List<Policy> politicas = new ArrayList();
        try {
            Model model = null;
            if (path.startsWith("http")) {
                LOGGER.debug("HTTP Model");
                String rdf = RDFUtils.browseSemanticWeb(path);

                model = RDFUtils.parseFromText(rdf);

            }
            else{
                LOGGER.debug("File Model");
                String rdf = FileUtils.readFileToString(new File(path));
                model = RDFUtils.parseFromText(rdf);
            } 
            List<Resource> ls = ODRLRDF.findPolicies(model);
            for (Resource rpolicy : ls) {
                Policy policy = ODRLRDF.getPolicyFromResource(rpolicy);
                policy.fileName = path;
                politicas.add(policy);
            }
        } catch (Exception e) {
            LOGGER.error("error " + e.getMessage());
            e.printStackTrace();
        }
        return politicas;
    }
    

    /**
     * Loads the ODRL2.2 policies found in a file.
     * @deprecated 
     * @param path File location
     * @return A set of policies
     */
    public static List<Policy> load(String path) {
        List<Policy> politicas = new ArrayList();
        try {
            Model model = null;
            if (path.startsWith("http")) {
                LOGGER.info("HTTP Model");
                String rdf = RDFUtils.browseSemanticWeb(path);

                model = RDFUtils.parseFromText(rdf);

            }
            if ((path.startsWith("/")) || (path.startsWith("C:"))) {
                LOGGER.info("File Model");
                String rdf = FileUtils.readFileToString(new File(path));

                model = RDFUtils.parseFromText(rdf);

            } else {
                LOGGER.info("String Model");
                String rdf = path;

                model = RDFUtils.parseFromText(rdf);
//                model = RDFDataMgr.loadModel(path);
            }

            List<Resource> ls = ODRLRDF.findPolicies(model);

            for (Resource rpolicy : ls) {
                Policy policy = ODRLRDF.getPolicyFromResource(rpolicy);
                policy.fileName = path;
                politicas.add(policy);
            }
        } catch (Exception e) {
            LOGGER.error("error " + e.getMessage());
            e.printStackTrace();
        }
        return politicas;
    }

    /**
     * Gets a policy from an a text document (RDF) containing one policy.
     *
     * @return One single policy.
     */
    public static Policy getPolicy(String rdf) {
        Model model = RDFUtils.parseFromText(rdf);
        List<Resource> ls = ODRLRDF.findPolicies(model);
        if (!ls.isEmpty()) {
            Policy policy = ODRLRDF.getPolicyFromResource(ls.get(0));
            return policy;
        }
        return null;
    }

    /**
     * ***************** NON-PUBLIC METHODS *****************************************
     */
    /**
     * ***************** NON-PUBLIC METHODS *****************************************
     */
    /**
     * ***************** NON-PUBLIC METHODS *****************************************
     */
    /**
     * ***************** NON-PUBLIC METHODS *****************************************
     */
    /**
     * ***************** NON-PUBLIC METHODS *****************************************
     */
    /**
     * ***************** NON-PUBLIC METHODS *****************************************
     */
    /**
     * Finds the policies in the model. It determines URIs with a type of a
     * known policy term. Policies are accepted to be odrl:policy, cc:License or
     * dc:LicenseDocument
     *
     * @param model Model
     * @return List of resources
     */
    protected static List<Resource> findPolicies(Model model) {
        List<Resource> total = new ArrayList();
        List<Resource> ls = RDFUtils.getOntResourcesOfType(model, ODRLRDF.RPOLICY);
        total.addAll(ls);
        List<Resource> ls2 = RDFUtils.getResourcesOfType(model, ODRLRDF.RCCLICENSE);
        total.addAll(ls2);
        List<Resource> ls3 = RDFUtils.getResourcesOfType(model, ODRLRDF.RDCLICENSEDOC);
        total.addAll(ls3);
        return total;
    }

    /**
     * Gets a ODRL2.0 policy object from a RDF resource
     *
     * @param an RDF Resource
     * @return An ODRL policy
     */
    protected static Policy getPolicyFromResource(Resource rpolicy) {
        Policy policy = new Policy();
        policy.uri = rpolicy.getURI();

        //CARGA LOS METADATOS COMUNES SI LOS HAY
        policy = (Policy) getResourceMetadata(policy, rpolicy);

        // Observamos qué tipo de licencia es
        List<String> stypes = RDFUtils.getAllPropertyStrings(rpolicy, RDF.type);

        if (stypes.contains("http://www.w3.org/ns/odrl/2/Set") || stypes.contains("http://www.w3.org/ns/odrl/2/Policy")) {
            policy.setTypeofpolicy(Policy.POLICY_SET);
        } else if (stypes.contains("http://www.w3.org/ns/odrl/2/Offer")) {
            policy.setTypeofpolicy(Policy.POLICY_OFFER);
        } else if (stypes.contains("http://www.w3.org/ns/odrl/2/Ticket")) {
            policy.setTypeofpolicy(Policy.POLICY_TICKET);
        } else if (stypes.contains("http://www.w3.org/ns/odrl/2/Request")) {
            policy.setTypeofpolicy(Policy.POLICY_REQUEST);
        } else if (stypes.contains("http://www.w3.org/ns/odrl/2/Agreement")) {
            policy.setTypeofpolicy(Policy.POLICY_AGREEMENT);
        } else if (stypes.contains("http://creativecommons.org/ns#License")) {
            policy.setTypeofpolicy(Policy.POLICY_CC);
            Rule ccRule = RDFUtils.findCreativeCommons(rpolicy);
            if (ccRule != null) {
                policy.addRule(ccRule);
            }
            return policy;
        }

        //IF HANDLING AN ODRL PROHIBITION CASE
        List<Resource> rrulesProhibitionODRL = RDFUtils.getAllPropertyResources(rpolicy, ODRLRDF.PPROHIBITION);
        for (Resource rrule : rrulesProhibitionODRL) {
            Rule rule = ODRLRDF.fromResourceToRule(rrule, Rule.RULE_PROHIBITION);
            if (rule != null) {
                policy.addRule(rule);
            }
        }

        //IF HANDLING AN ODRL PERMISSION CASE
        List<Resource> rrulesPermissionsODRL = RDFUtils.getAllPropertyResources(rpolicy, ODRLRDF.PPERMISSION);
        for (Resource rrule : rrulesPermissionsODRL) {
            Rule rule = ODRLRDF.fromResourceToRule(rrule, Rule.RULE_PERMISSION);
            if (rule != null) {
                policy.addRule(rule);
            }
        }
        return policy;
    }

    /**
     * Gets the JENA resource from a policy.
     *
     * @param policy Policy
     */
    private static Resource getResourceFromPolicy(Policy policy) {
        Model model = ModelFactory.createDefaultModel();
        addPrefixesToModel(model);

        //CREATION OF THE POLICY
        Resource rpolicy = model.createResource(policy.uri.toString());
        rpolicy.addProperty(RDF.type, ODRLRDF.RPOLICY);

        //HANDLING OF THE TYPE OF THE POLICY
        if (policy.getTypeofpolicy() == Policy.POLICY_REQUEST) {
            rpolicy.addProperty(RDF.type, ODRLRDF.REQUEST);
        } else if (policy.getTypeofpolicy() == Policy.POLICY_OFFER) {
            rpolicy.addProperty(RDF.type, ODRLRDF.OFFER);
        } else if (policy.getTypeofpolicy() == Policy.POLICY_AGREEMENT) {
            rpolicy.addProperty(RDF.type, ODRLRDF.RAGREEMENT);
        } else if (policy.getTypeofpolicy() == Policy.POLICY_TICKET) {
            rpolicy.addProperty(RDF.type, ODRLRDF.RTICKET);
        } else {
            rpolicy.addProperty(RDF.type, ODRLRDF.RSET);
        }

        //COMMON METADATA
        rpolicy = setResourceMetadata(policy, rpolicy);

        //RULES
        for (Rule r : policy.rules) {
            ResourceModel rm = r.getResourceModel();
            Resource rrule2 = rm.resource;
//            Resource rrule2 = getResourceFromRule(r);
            if (r.getClass().equals(Permission.class)) {
                rpolicy.addProperty(ODRLRDF.PPERMISSION, rrule2);
            }
            if (r.getClass().equals(Duty.class)) {
                rpolicy.addProperty(ODRLRDF.PDUTY, rrule2);
            }
            if (r.getClass().equals(Prohibition.class)) {
                rpolicy.addProperty(ODRLRDF.PPROHIBITION, rrule2);
            }

            model.add(rpolicy.getModel());
            model.add(rrule2.getModel());
        }

        Model mx = ModelFactory.createDefaultModel();
        mx.add(rpolicy.getModel());
        return rpolicy;
    }

    /**
     * Creates an ODRL Rule from a Jena Resource
     *
     * @param rrule Resource
     * @param tipo ?
     * @return an ODRL Rule or null.
     */
    private static Rule fromResourceToRule(Resource rrule, int tipo) {
        String uri = rrule.isAnon() ? "" : rrule.getURI();
        Rule rule = new Rule(uri);
        if (tipo == Rule.RULE_PERMISSION) {
            rule = new Permission(uri);
        }
        if (tipo == Rule.RULE_PROHIBITION) {
            rule = new Prohibition(uri);
        }
        if (tipo == Rule.RULE_DUTY) {
            rule = new Duty(uri);
        }

        rule.setKindOfRule(tipo);
        //está directamente expresado
        if (RDFUtils.isOfKind(rrule, ODRLRDF.RACTION.getURI())) {
            Action a = new Action(rrule.getURI());
            a = RDFUtils.enrichAction(a);
            rule.addAction(a);
        }
        String sassignee = RDFUtils.getFirstPropertyValue(rrule, ODRLRDF.PASSIGNEE);
        if (!sassignee.isEmpty()) {
            rule.setAssignee(new Party(sassignee));
        }

        String sassigner = RDFUtils.getFirstPropertyValue(rrule, ODRLRDF.PASSIGNER);
        if (!sassigner.isEmpty()) {
            rule.setAssignee(new Party(sassigner));
        }

        String target1 = RDFUtils.getFirstPropertyValue(rrule, ODRLRDF.PTARGET);
        if (!target1.isEmpty()) {
            rule.setTarget(target1);
        }

        List<String> sactions1 = RDFUtils.getAllPropertyStrings(rrule, ODRLRDF.PACTION);
        for (String saction : sactions1) {
            Action a = new Action(saction);
            a = RDFUtils.enrichAction(a);
            rule.addAction(a);
        }
        List<Resource> sconstraints = RDFUtils.getAllPropertyResources(rrule, ODRLRDF.PCONSTRAINT);
        for (Resource sconstraint : sconstraints) {
            Constraint c = new Constraint(sconstraint.getURI());
            c = RDFUtils.enrichConstraint(c);

            String valor = RDFUtils.getFirstPropertyValue(sconstraint, ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/operator"));
            List<String> ls = RDFUtils.getAllProperties(sconstraint);
            for (String s : ls) {
                if (s.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                    continue;
                }
                if (s.contains("operator"))
                {
                    c.operator = RDFUtils.getFirstPropertyValue(sconstraint, ModelFactory.createDefaultModel().createProperty(s));
                    continue;
                }
                c.rightOperand = s; //getLastPartOfUri(s);
                c.leftOperand = RDFUtils.getFirstPropertyValue(sconstraint, ModelFactory.createDefaultModel().createProperty(s));
                    
            }
            rule.addConstraint(c);

        }
        List<Resource> sduties = RDFUtils.getAllPropertyResources(rrule, ODRLRDF.PDUTY);
        for (Resource rduty : sduties) {
            Constraint c = new Constraint(rduty.getURI());
            c = RDFUtils.enrichConstraint(c);

            String action = RDFUtils.getFirstPropertyValue(rduty, ODRLRDF.PACTION);
//                c.label = LD.getFirstPropertyValue(rrule, RDFS.label);
            Action a = new Action(action);
            a = RDFUtils.enrichAction(a);
            c.setLabel(a.getLabel("en"));

            if (a.getURI().equals("")) {

            }
        }
        return rule;
    }


    /**
     * Returns a MetadataObject from a Jena resource
     *
     * @param me MetadataObject to be enriched
     * @param resource Resource with the data
     */
    protected static MetadataObject getResourceMetadata(MetadataObject me, Resource resource) {
        me.identifier = RDFUtils.getFirstPropertyValue(resource, RDFUtils.RIDENTIFIER);
        me.title = RDFUtils.getFirstPropertyValue(resource, RDFUtils.TITLE);
        me.comment = RDFUtils.getFirstPropertyValue(resource, RDFUtils.COMMENT);
        me.setLabel(RDFUtils.getFirstPropertyValue(resource, RDFUtils.LABEL));
        List<String> cms = RDFUtils.getAllPropertyStrings(resource, RDFUtils.CLOSEMATCH);
        me.closeMatch.clear();
        for (String cm : cms) {
            me.closeMatch.add(cm);
        }
        List<String> labels = RDFUtils.getAllPropertyStrings(resource, RDFUtils.LABEL);
        me.labels.clear();
        for (String label : labels) {
            me.addLabel(label);
        }
        me.seeAlso = RDFUtils.getFirstPropertyValue(resource, RDFUtils.SEEALSO);
        me.source = RDFUtils.getFirstPropertyValue(resource, RDFUtils.SOURCE);
        return me;
    }


    /**
     * Gets a Jena Resource from a party
     *
     * @param duty Duty in the ODRL2.0 Model
     * @return A Jena action
     */
    /*public static Resource getResourceFromParty(Party party) {
        Model model = ModelFactory.createDefaultModel();
        Resource raction = model.createResource(party.uri.toString());
        raction.addProperty(RDF.type, ODRLRDF.RPARTY);
        return raction;
    }*/

    /**
     * Gets a Jena Resource from a a constraint
     *
     * @param constraint Constraint in the ODRL2.0 Model
     * @return A Jena constraint
     */
    public static Resource getResourceFromConstraint(Constraint constraint) {
        Model model = ModelFactory.createDefaultModel();
        Resource rconstraint = constraint.isAnon() ? model.createResource() : model.createResource(constraint.uri.toString());
        rconstraint.addProperty(RDF.type, ODRLRDF.RCONSTRAINT);

        Property prho = model.createProperty(constraint.leftOperand);
        rconstraint.addProperty(prho, constraint.rightOperand);
        rconstraint.addProperty(ODRLRDF.POPERATOR, model.createResource(constraint.operator));

        return rconstraint;
    }

    /**
     * Gets a Jena Resource from a rule
     *
     * @param permission Permission in the ODRL2.0 Model
     * @return A Jena permission
     * @deprecated
     */
/*    public static Resource getResourceFromRuleX(Rule rule) {
        Model model = ModelFactory.createDefaultModel();
        Resource rrule = rule.isAnon() ? model.createResource(new AnonId()) : model.createResource(rule.uri.toString());

        if (rule.getKindOfRule() == Rule.RULE_PERMISSION) {
            rrule.addProperty(RDF.type, ODRLRDF.RPERMISSION);
        }
        if (rule.getKindOfRule() == Rule.RULE_DUTY) {
            rrule.addProperty(RDF.type, ODRLRDF.RDUTY);
        }
        if (rule.getKindOfRule() == Rule.RULE_PROHIBITION) {
            rrule.addProperty(RDF.type, ODRLRDF.RPROHIBITION);
        }

        //TARGET, ASSIGNER, ASSIGNEE
        if (!rule.target.isEmpty()) {
            rrule.addProperty(ODRLRDF.PTARGET, rule.target);
        }
        if (rule.getAssigner() != null) {
            Resource rparty = getResourceFromParty(rule.getAssigner());
            rrule.addProperty(ODRLRDF.PASSIGNER, rparty);
            model.add(rparty.getModel());
        }
        if (rule.getAssignee() != null) {
            Resource rparty = getResourceFromParty(rule.getAssignee());
            rrule.addProperty(ODRLRDF.PASSIGNEE, rparty);
            model.add(rparty.getModel());
        }

        //ACTIONS
        for (Action a : rule.actions) {
            ResourceModel rm = a.getResourceModel();
            rrule.addProperty(ODRLRDF.PACTION, rm.resource);
            model.add(rm.model);
        }

        //CONSTRAINTS
        for (Constraint c : rule.constraint) {
            Resource rconstraint = getResourceFromConstraint(c);
            rrule.addProperty(ODRLRDF.PCONSTRAINT, rconstraint);
            model.add(rconstraint.getModel());
        }

        //DUTIES (ONLY FOR PERMISSIONS)
        if (rule.getClass().equals(Permission.class)) {
            Permission permiso = (Permission) rule;
            List<Duty> duties = permiso.getDuties();
            for (Duty duty : duties) {
                
                ResourceModel rm = duty.getResourceModel();
                Resource rduty = rm.resource;

//                Resource rduty = ODRLRDF.getResourceFromRule(duty);
                model.add(rduty.getModel());
                rrule.addProperty(ODRLRDF.PDUTY, rduty);
            }
        }
        return rrule;
    }
*/
    /**
     * Sets the resource metadata from the given MetadataObject
     *
     * @param me MetadataObject
     * @param resource Input resource
     */
    private static Resource setResourceMetadata(MetadataObject me, Resource resource) {
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
        if (!me.source.isEmpty()) {
            resource.addProperty(ODRLRDF.SOURCE, me.source);
        }
        return resource;
    }

    private static Map<String, String> mapeos = new HashMap();
    static {
        mapeos.put("odrl", "http://www.w3.org/ns/odrl/2/"); //http://w3.org/ns/odrl/2/
        mapeos.put("dct", "http://purl.org/dc/terms/");
        mapeos.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        mapeos.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        mapeos.put("cc", "http://creativecommons.org/ns#");
        mapeos.put("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        mapeos.put("void", "http://rdfs.org/ns/void#");
        mapeos.put("dcat", "http://www.w3.org/ns/dcat#");
        mapeos.put("gr", "http://purl.org/goodrelations/");
        mapeos.put("prov", "http://www.w3.org/ns/prov#");
        mapeos.put("odrl-lr", "http://purl.org/odrl-lr/");
        
    }

    private static void addPrefixesToModelIfNeeded(Model model) {
        Collection<String> values = mapeos.values();
        
        StmtIterator sts = model.listStatements();
        while(sts.hasNext())
        {
            Statement st = sts.nextStatement();
            String s = st.getSubject().getURI();
            String p = st.getPredicate().getURI();
            String o = "";
            if (st.getObject().isResource())
                o = st.getObject().asResource().getURI();

            for(String clave : mapeos.keySet())
            {
                String v = mapeos.get(clave);
                if ((s!=null && s.contains(v)) || p.contains(v) || (o!=null && o.contains(v)))
                {
                    model.setNsPrefix(clave, v);
                }
            }
            
        }
    }
    
    /**
     * Adds the most common prefixes to the generated model
     *
     * @param model Jena Model
     */
    private static void addPrefixesToModel(Model model) {
        for(String clave : mapeos.keySet())
        {
            model.setNsPrefix(clave, mapeos.get(clave));
        }
/*        model.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/"); //http://w3.org/ns/odrl/2/
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("cc", "http://creativecommons.org/ns#");
        model.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        model.setNsPrefix("void", "http://rdfs.org/ns/void#");
        model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        model.setNsPrefix("gr", "http://purl.org/goodrelations/");
        model.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
        model.setNsPrefix("odrl-lr", "http://purl.org/odrl-lr/");*/
    }

    public static String unroll(String _uri)
    {
        for(String clave : mapeos.keySet())
        {
            if (_uri.startsWith(clave+":"))
            {
                _uri = _uri.replace(clave+":", mapeos.get(clave));
            }
        }        
        return _uri;
    }
    
    public static String randomURI()
    {
        return JODRLApiSettings.ODRL_NS + "" + UUID.randomUUID().toString().substring(0,8);
    }
    

    private static Resource RPOLICY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Policy");
    private static Resource RLICENSE = ModelFactory.createDefaultModel().createResource("http://purl.org/dc/terms/LicenseDocument");
    private static Resource RCCLICENSE = ModelFactory.createDefaultModel().createResource("http://creativecommons.org/ns#License");
    private static Resource RSET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Set");
    private static Resource OFFER = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Offer");
    private static Resource REQUEST = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Request");
    private static Resource RAGREEMENT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Agreement");
    private static Resource RTICKET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Ticket");
    public static Resource RPERMISSION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Permission");
    public static Resource RPROHIBITION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Prohibition");
    private static Property PPROHIBITION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/prohibition");
    public static Resource RDUTY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Duty");
    private static Resource RCONSTRAINT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Constraint");
    public static Resource RACTION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Action");
    public static Resource RPARTY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Party");
    private static Property PPERMISSION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/permission");
    public static Property PTARGET = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/target");
    public static Property PASSIGNER = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assigner");
    public static Resource RASSIGNER = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Assigner");
    public static Property PASSIGNEE = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assignee");
    public static Property PACTION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/action");
    public static Property PDUTY = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/duty");
    public static Property PCONSTRAINT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/constraint");
    private static Property PCOUNT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/count");
    public static Property POPERATOR = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/operator");
    public static Property PRIGHTOPERAND = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/rightOperand");
    public static Property PLEFTOPERAND = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/leftOperand");
    protected static Property PCCPERMISSION = ModelFactory.createDefaultModel().createProperty("http://creativecommons.org/ns#permits");
    protected static Property PCCPERMISSION2 = ModelFactory.createDefaultModel().createProperty("http://web.resource.org/cc/permits");
    protected static Property PCCREQUIRES = ModelFactory.createDefaultModel().createProperty("http://creativecommons.org/ns#requires");
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
    private static Property SEEALSO = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
    private static Property SOURCE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/source");
    public static Property PREFINEMENT  = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/refinement");
    private static Resource RDATASET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/dcat#Dataset");
    private static Resource RLINKSET = ModelFactory.createDefaultModel().createResource("http://rdfs.org/ns/void#Linkset");
    public static Resource REQ = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/eq");
    public static Resource RPARTOF = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/isPartOf");
    public static Property PVALUE  = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");

}
