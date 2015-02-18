package odrlmodel.rdf;

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
import com.hp.hpl.jena.vocabulary.RDF;

//LOG4J
import org.apache.log4j.Logger;

//ODRL
import odrlmodel.Action;
import odrlmodel.Asset;
import odrlmodel.Constraint;
import odrlmodel.ConstraintIndustry;
import odrlmodel.ConstraintLocation;
import odrlmodel.ConstraintPay;
import odrlmodel.MetadataObject;
import odrlmodel.Policy;
import odrlmodel.Rule;


/**
 * Bridge between the ODRL Model in Java and in OWL
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class ODRLRDF {
    
    /**
     * Gets a policy object from a RDF resource
     * @param an RDF Resource
     * @return An ODRL policy
     */
    public static Policy getPolicyFromResource(Resource rpolicy) {
        Policy policy = new Policy(); 
        policy.uri = rpolicy.getURI();
        
        //CARGA LOS METADATOS COMUNES SI LOS HAY
        policy = (Policy)getResourceMetadata(policy, rpolicy);

        //CARGA EL CÓDIGOLEGAL
        policy.legalCode = RDFUtils.getFirstPropertyValue(rpolicy, ODRLModel.PLEGALCODE);
        
        
        // Observamos qué tipo de licencia es
        List<String> stypes = RDFUtils.getAllPropertyStrings(rpolicy, RDF.type);
        if (stypes.contains("http://creativecommons.org/ns#License"))
        {
            policy.setType(Policy.POLICY_CC);
    //        Rule ccRule = CreativeCommonsRDF.findCreativeCommons(rpolicy);
    //        if (ccRule!=null)
    //            policy.addRule(ccRule);
    //        return policy;
            
        }
        else if (stypes.contains("http://www.w3.org/ns/odrl/2/Set") || stypes.contains("http://www.w3.org/ns/odrl/2/Policy"))
            policy.setType(Policy.POLICY_SET);   
//        String stype =  RDFUtils.getFirstPropertyValue(rpolicy, RDF.type);
//        policy.setType(stype);

        
        //IF HANDLING AN ODRL PROHIBITION CASE
        List<Resource> rrulesProhibitionODRL = RDFUtils.getAllPropertyResources(rpolicy, ODRLModel.PPROHIBITION);
        for (Resource rrule : rrulesProhibitionODRL) {
            Rule rule = ODRLRDF.fromResourceToRule(rrule, Rule.RULE_PROHIBITION);
            if (rule!=null)
                policy.addRule(rule);
        }
        
        //IF HANDLING AN ODRL PERMISSION CASE
        List<Resource> rrulesPermissionsODRL = RDFUtils.getAllPropertyResources(rpolicy, ODRLModel.PPERMISSION);
        for (Resource rrule : rrulesPermissionsODRL) {
            Rule rule = ODRLRDF.fromResourceToRule(rrule, Rule.RULE_PERMISSION);
            if (rule!=null)
                policy.addRule(rule);
        }
        return policy;
    }
    
    
    
    /**
     * Creates an ODRL Rule from a Jena Resource
     * @param rrule Resource
     * @param tipo ?
     * @return an ODRL Rule or null.
     */
    private static Rule fromResourceToRule(Resource rrule, int tipo)
    {
        Rule rule = new Rule();
        rule.setKindOfRule(tipo);
            //está directamente expresado
            if (RDFUtils.isOfKind(rrule, ODRLModel.RACTION.getURI()))
            {
                Action a =new Action(rrule.getURI());
                a = ODRLModel.enrichAction(a);
                rule.addAction(a);
            }
            rule.setAssignee(RDFUtils.getFirstPropertyValue(rrule, ODRLModel.PASSIGNEE));
            rule.setAssigner(RDFUtils.getFirstPropertyValue(rrule, ODRLModel.PASSIGNER));
            
            String target1 = RDFUtils.getFirstPropertyValue(rrule, ODRLModel.PTARGET);
            rule.setTarget(target1);

            List<String> sactions1 = RDFUtils.getAllPropertyStrings(rrule, ODRLModel.PACTION);
            for (String saction : sactions1) {
                Action a = new Action(saction);
                a = ODRLModel.enrichAction(a);
                rule.addAction(a);
            }
            List<Resource> sconstraints = RDFUtils.getAllPropertyResources(rrule, ODRLModel.PCONSTRAINT);
            for (Resource sconstraint : sconstraints) {
                Constraint c = new Constraint(sconstraint.getURI());
                c=ODRLModel.enrichConstraint(c);
                String location=RDFUtils.getFirstPropertyValue(sconstraint, ODRLModel.PSPATIAL);
                if (!location.isEmpty())
                {
                    ConstraintLocation cl = new ConstraintLocation(c);
                    cl.location = location;
                    c = cl;
                }
                String industry=RDFUtils.getFirstPropertyValue(sconstraint, ODRLModel.PINDUSTRY);
                if (!industry.isEmpty())
                {
                    ConstraintIndustry cl = new ConstraintIndustry(c);
                    cl.industry = industry;
                    c = cl;
                }
                
                rule.addConstraint(c);
                
            }
            List<Resource> sduties = RDFUtils.getAllPropertyResources(rrule, ODRLModel.PDUTY);
            for (Resource rduty : sduties) {
                Constraint c = new Constraint(rduty.getURI());
                c=ODRLModel.enrichConstraint(c);

                String action = RDFUtils.getFirstPropertyValue(rduty, ODRLModel.PACTION);
//                c.label = LD.getFirstPropertyValue(rrule, RDFS.label);
                Action a = new Action(action);
                a = ODRLModel.enrichAction(a);
                c.setLabel(a.getLabel("en"));

                if (a.getURI().equals(""))
                {
                    
                }
                
                if (a.getURI().equals("http://www.w3.org/ns/odrl/2/pay")){
//                if (a.getLabel("en").equals("Pay")) {
                    c = new ConstraintPay(c);
                    Property ppunit=ModelFactory.createDefaultModel().createProperty("http://purl.org/goodrelations/UnitOfMeasurement");
                    String good = RDFUtils.getFirstPropertyValue(rduty, ppunit);
                    Property pperunit=ModelFactory.createDefaultModel().createProperty("http://purl.org/goodrelations/amountOfThisGood");
                    String perunit = RDFUtils.getFirstPropertyValue(rduty, pperunit);
           //         if (perunit!=null && !perunit.isEmpty())
           //             c.setPerUnit(true);
                    String target = RDFUtils.getFirstPropertyValue(rduty, ODRLModel.PTARGET);
                    int i = target.indexOf(" ");
                    if (i != -1) {
                        String s1 = target.substring(0, i);
                        String s2 = target.substring(i);
                        s2=s2.trim();
                        double d = 0.0;
                        try {
                            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                            Number number = format.parse(s1);
                            d = number.doubleValue();
                        } catch (Exception e) {
                        }
                        int iperunit = 1;
                        try{iperunit=Integer.parseInt(perunit);}catch(Exception e){}
                        ((ConstraintPay)c).setPayment(d, s2, good,iperunit );
                    }
                } else {
            //        c.action = action;
                }
                rule.addConstraint(c);
            }        
        
        return rule;
    }
    
    public static MetadataObject getResourceMetadata(MetadataObject me, Resource resource)
    {
        me.title = RDFUtils.getFirstPropertyValue(resource, RDFUtils.TITLE);
        me.comment = RDFUtils.getFirstPropertyValue(resource, RDFUtils.COMMENT);
        me.setLabel(RDFUtils.getFirstPropertyValue(resource, RDFUtils.LABEL));
        List<String> labels=RDFUtils.getAllPropertyStrings(resource, RDFUtils.LABEL);
        me.labels.clear();
        for(String label:labels)
            me.addLabel(label);
        me.seeAlso = RDFUtils.getFirstPropertyValue(resource, RDFUtils.SEEALSO);
        return me;
    }
    
    
    /**
     * Sets the resource metadata from the given MetadataObject
     * @param me MetadataObject
     * @param resource Input resource
     */
    public static Resource setResourceMetadata(MetadataObject me, Resource resource)
    {
        if (!me.comment.isEmpty()) {
            resource.addProperty(RDFUtils.COMMENT, me.comment);
        }
        if (!me.title.isEmpty()) {
            resource.addProperty(RDFUtils.TITLE, me.title);
        }   
        if (!me.getLabel("en").isEmpty()) {
            resource.addProperty(RDFUtils.LABEL, me.getLabel("en"));
        }   
        if (!me.seeAlso.isEmpty()) {
            resource.addProperty(RDFUtils.SEEALSO, me.seeAlso);
        }   
        return resource;
    }
    
    /**
     * Serializes the license in a RDF string
     * @param policy A given policy
     * @return The Turtle serialization of the policy
     */
    public static String getRDF(Policy policy) {
        Resource r = getResourceFromPolicy(policy);
        Model model = ModelFactory.createDefaultModel();
        addPrefixesToModel(model);
        model.add(r.getModel());
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, Lang.TTL);
        String s = sw.toString();
        return s;
    }
    

    /**
     * Serializes the policy into a RDF string
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
    
    public static Resource getResourceFromPolicy(Policy policy)
    {
        String s = "";
        Model model = ModelFactory.createDefaultModel();
        addPrefixesToModel(model);

        //CREATION OF THE POLICY
        Resource rpolicy = model.createResource(policy.uri.toString());
        rpolicy.addProperty(RDF.type, ODRLModel.RPOLICY);

        //HANDLING OF THE TYPE OF THE POLICY
        if (policy.getType()==Policy.POLICY_REQUEST) 
            rpolicy.addProperty(RDF.type, ODRLModel.REQUEST);
        else if (policy.getType()==Policy.POLICY_OFFER) 
            rpolicy.addProperty(RDF.type, ODRLModel.OFFER);
        else if (policy.getType()==Policy.POLICY_SET) 
            rpolicy.addProperty(RDF.type, ODRLModel.RSET);
        else  
            rpolicy.addProperty(RDF.type, ODRLModel.RSET);


        //COMMON METADATA
        rpolicy=setResourceMetadata(policy, rpolicy);

        //RULES
        for (Rule r : policy.rules) {
            r.uri="";   //las hacemos anónimas
            Resource rrule;
            if (r.uri==null || r.uri.isEmpty())
                rrule = model.createResource();
            else
                rrule = model.createResource(r.uri.toString());
            if (r.getKindOfRule()==Rule.RULE_PERMISSION) {
                rrule.addProperty(RDF.type, ODRLModel.RPERMISSION);
                rpolicy.addProperty(ODRLModel.PPERMISSION, rrule);
            }
            if (r.getKindOfRule()==Rule.RULE_PROHIBITION) {
                rrule.addProperty(RDF.type, ODRLModel.RPROHIBITION);
                rpolicy.addProperty(ODRLModel.PPROHIBITION, rrule);
            }
            if (!r.target.isEmpty()) 
                rrule.addProperty(ODRLModel.PTARGET, r.target);
            if (!r.getAssignee().isEmpty())
                rrule.addProperty(ODRLModel.PASSIGNEE, r.getAssignee());
           if (!r.getAssigner().isEmpty())
                rrule.addProperty(ODRLModel.PASSIGNER, r.getAssigner());

            for (Constraint req : r.constraints) {
                if (req.getClass().equals(ConstraintPay.class))
                {
                    ConstraintPay cp = ((ConstraintPay)req);
                    Resource rconstraint = model.createResource();
                    rconstraint.addProperty(RDFUtils.LABEL, "Pay");
                    rconstraint.addProperty(RDF.type, ODRLModel.RDUTY);
                    rconstraint.addProperty(ODRLModel.PACTION, ODRLModel.RPAY);
                    String scount = String.format("%.02f %s", cp.amount, cp.currency);
                    rconstraint.addProperty(ODRLModel.PTARGET, scount);
                    Property pgood = model.createProperty(cp.good);
                    rconstraint.addProperty(ODRLModel.PAMOUNTOFTHISGOOD, ""+cp.amountOfThisGood);
                    rconstraint.addProperty(ODRLModel.PUNITOFMEASUREMENT, pgood);
                    rrule.addProperty(ODRLModel.PDUTY, rconstraint);
                } else if (req.getClass().equals(ConstraintLocation.class))
                {
                    ConstraintLocation cp = ((ConstraintLocation)req);
                    Resource rconstraint = model.createResource();
                    rconstraint.addProperty(ODRLModel.PSPATIAL, cp.location);
                    rconstraint.addProperty(ODRLModel.POPERATOR, ODRLModel.LEQ);
                    rrule.addProperty(ODRLModel.PCONSTRAINT, rconstraint);
                }else if (req.getClass().equals(ConstraintIndustry.class))
                {
                    ConstraintIndustry cp = ((ConstraintIndustry)req);
                    Resource rconstraint = model.createResource();
                    rconstraint.addProperty(ODRLModel.PINDUSTRY, cp.industry);
                    rconstraint.addProperty(ODRLModel.POPERATOR, ODRLModel.LEQ);
                    rrule.addProperty(ODRLModel.PCONSTRAINT, rconstraint);
                }

                
                else {
                    if (req==null)
                    {
                        Logger.getLogger("ldr").warn("Warning when processing a constraint");
                        continue;
                    }
                    Resource rconstraint;
                    if (req.uri==null || req.uri.isEmpty())
                        rconstraint = model.createResource();
                    else 
                        rconstraint = model.createResource(req.uri.toString());
            //        rconstraint.addProperty(RDF.type, ODRL.RCONSTRAINT);
                    rrule.addProperty(ODRLModel.PCONSTRAINT, rconstraint);

                }
            }
            for (Action a : r.actions) {
                Resource raction = model.createResource(a.uri.toString());
        //        raction.addProperty(RDF.type, ODRL.RACTION);
                rrule.addProperty(ODRLModel.PACTION, raction);
            }
        }
        Model mx = ModelFactory.createDefaultModel();
        mx.add(rpolicy.getModel());
  //      RDFUtils.print(mx);
        
        return rpolicy;
     /*   StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, Lang.TTL);
        s = sw.toString();
        return s;*/
    }
    
    /**
     * Gets the RDF from the a resource
     */
    public static String getRDFFromResource(Resource resource)
    {
        Model model = ModelFactory.createDefaultModel();
        model.add(resource.getModel());
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, Lang.TTL);
        String s = sw.toString();
        return s;
    }
    

    /**
     * Gets an RDF Resource from a policy
     * @param policy ODRL policy
     * @return an RDF Resource
     */
    /*public static Resource getResourceFromPolicy(Policy policy)
    {
        String spolicy=getRDF(policy);
        try{
            InputStream streamPolicy = new ByteArrayInputStream(spolicy.getBytes("UTF-8"));
            Model modelPolicy = ModelFactory.createDefaultModel();
            RDFDataMgr.read(modelPolicy, streamPolicy, Lang.TTL);
            Resource rpolicy = modelPolicy.getResource(policy.getURI());
            return rpolicy;
        }catch(Exception e)
        {
            Logger.getLogger("ldr").warn("Error when creating resource from Policy ");
            e.printStackTrace();
            return null;
        }
    }*/
    
    /**
     * Takes a RDF Resource and creates a new Asset from it.
     * @param rasset An RDF Resource describing an ODRL asset
     * @return An ODRL asset
     */
    public static Asset getAssetFromResource(Resource rasset)
    {
        Asset asset = new Asset(rasset.getURI());

        //READ THE METADATA
        asset = (Asset)getResourceMetadata(asset, rasset);
       
        //READ THE POLICIES
        List<Resource> rpolicies = RDFUtils.getAllPropertyResources(rasset,RDFUtils.PLICENSE);
        for(Resource rpolicy : rpolicies)
        {
            Policy policy = getPolicyFromResource(rpolicy);
            asset.addPolicy(policy);
        }
        return asset;
    }
    
    /**
     * Takes an Asset and creates a new Resource from it.
     * @param An asset object
     * @return An RDF Resource
     */
    public static Resource getResourceFromAsset(Asset asset)
    {
        String s = "";
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/");//http://w3.org/ns/odrl/2/
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("mysite", MetadataObject.DEFAULT_NAMESPACE);
        model.setNsPrefix("cc", "http://creativecommons.org/ns#");
        model.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        model.setNsPrefix("wot","http://xmlns.com/wot/0.1/");

        //CREATION OF THE ASSET
        Resource rasset = model.createResource(asset.uri.toString());
        rasset.addProperty(RDF.type, RDFUtils.RDATASET);
        
        //CREATION OF THE POLICIES
        List<Policy> policies = asset.getPolicies();
        for(Policy policy : policies)
        {
            Resource rpolicy = ODRLRDF.getResourceFromPolicy(policy);//ESTA EL BUG AQUIIII?
            
            
     //       System.out.println("a");RDFUtils.print(model);            
            if (rpolicy==null)
                continue;
            rasset.addProperty(RDFUtils.PLICENSE, rpolicy);
            
       //     System.out.println("o");RDFUtils.print(model);            
            
       //     model.add(rpolicy.getModel());
            
      //      System.out.println("u");RDFUtils.print(model);            
        }

        //ADDITION OF THE METADATA
        rasset=ODRLRDF.setResourceMetadata(asset, rasset);
        
        return rasset;
    }

    /**
     * Adds the most common prefixes to the generated model
     * 
     */
    public static void addPrefixesToModel(Model model)
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
    
}
