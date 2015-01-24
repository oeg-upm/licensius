package odrlmodel.managers;

import odrlmodel.rdf.ODRLRDF;
import odrlmodel.managers.PolicyManager;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMaker;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import java.util.List;
import odrlmodel.Action;
import ldconditional.LDRConfig;
import odrlmodel.Policy;
import odrlmodel.Rule;
import odrlmodel.rdf.ODRLModel;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openjena.riot.Lang;


/**
 * Informative class to demonstrate the use of the Simple ODRL API.
 * If you don't know where to start, start here!
 * ---
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class TestODRLModel {

    
    
    public static void main(String args[]) {
        Logger.getRootLogger().setLevel(Level.OFF);
        
        TestODRLModel test = new TestODRLModel();

        System.out.println("Test ================ Load Metashare Policy ");
        test.loadMetashare();
        /*
        System.out.println("Test ================ Load ODRL complex Policy ");
        test.loadComplex();
        
/*        System.out.println("Test ================ Load ODRL-CC Policy ");
        test.loadCC();
        System.out.println("Test ================ Load ODRL Policy with Payment");
        test.loadODRLPolicyWithPayment();
        System.out.println("Test ================ Load ODRL Policy with Payment per triple");
        test.loadODRLPolicyWithPaymentPerTriple();
 */       
        
/*
        System.out.println("TestA ================ Authorization");
        test.authorize();

        System.out.println("Test0 ================ Creates a simple dataset");
        test.createSimpleDataset();
        
        System.out.println("Test1 ================ Creation of a simple policy");
        test.createSimplePolicy();

        System.out.println("Test2 ================ Serialization of policies");
        test.readWrite();
        
        System.out.println("Test3 ================ Reading CreativeCommons licenses as an ODRL policy");
        test.loadCreativeCommonsLicense();
        
        System.out.println("Test4 ================ Creation of a complexer policy");
        test.createComplexPolicy();
 */
    }

    private Policy createSimplePolicy()
    {
        Policy policy = new Policy();
        Rule rule = new Rule();
        rule.target = "Lucy in the sky with diamonds";
        Action action = new Action("http://www.w3.org/ns/odrl/2/play");
        action = ODRLModel.enrichAction(action);
        rule.addAction(action);
        rule.setAssignee("John");
        policy.rules.add(rule);
        String rdf=ODRLRDF.getRDF(policy);
        System.out.println(rdf);
        return policy;
    }   
    
    private void loadCreativeCommonsLicense()
    {
        List<Policy> policies = PolicyManager.load("./licenses/cc-by.ttl");
        for(Policy policy : policies)
        {
            String rdf=ODRLRDF.getRDF(policy);
            System.out.println(rdf);
        }
    }
    

    private void readWrite()
    {
        //Write
        System.out.println("Writing");
        PolicyManager.store(createSimplePolicy(), "test.ttl");
        System.out.println("Reading");
        List<Policy> policies = PolicyManager.load("test.ttl");
        for(Policy policy2 : policies)
            System.out.println(ODRLRDF.getRDF(policy2));
    }

    private Policy createComplexPolicy()
    {
        Policy policy = new Policy();
        Rule rule = new Rule();
        rule.target = "Lucy in the sky with diamonds";
        Action action = new Action("http://www.w3.org/ns/odrl/2/play");
        action = ODRLModel.enrichAction(action);
        rule.addAction(action);
        rule.setAssignee("Victor");
        policy.rules.add(rule);
        String rdf=ODRLRDF.getRDF(policy);
        System.out.println(rdf);
        return policy;
    }

    private void createSimpleDataset() {
        
       Model modelA = ModelFactory.createDefaultModel();        
       Resource ra1=modelA.createResource("apple");
       Property pa1=modelA.createProperty("definition");
       Literal la1=modelA.createLiteral("Delicious fruit");
       Statement st=modelA.createStatement(ra1, pa1, la1);
       modelA.add(st);
       
       Model modelB = ModelFactory.createDefaultModel();        
       Resource rb1=modelB.createResource("apple");
       Property pb1=modelB.createProperty("definition");
       Literal lb1=modelB.createLiteral("Fruta deliciosa");
       Statement st1=modelB.createStatement(rb1, pb1, lb1);
       modelB.add(st);
        
       DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {
            public Graph create() {
                return new GraphMem();
            }
       };
       Dataset ds = DatasetImpl.wrap(new DatasetGraphMaker(maker));
       ds.addNamedModel("English", modelA);
       ds.addNamedModel("Spanish", modelB);
        
       RDFDataMgr.write(System.out, ds, Lang.NQUADS) ;  
        
    }
    
    private void authorize()
    {
        Policy policy = new Policy();
        Rule permission = new Rule();
        permission.target = "English";
        
        Action action = new Action("http://www.w3.org/ns/odrl/2/play");
        action = ODRLModel.enrichAction(action);
        permission.addAction(action);
        
        policy.addRule(permission);

        Rule prohibition = new Rule();
        prohibition.target = "Spanish";

        prohibition.addAction(action);
        prohibition.setKindOfRule(Rule.RULE_PROHIBITION);
        policy.addRule(prohibition);
        
        
        Policy request = new Policy();
        request.setType(Policy.POLICY_REQUEST);
 //       request.addAction(new Action("http://www.w3.org/ns/odrl/2/play"));
        permission.target = "English";
        
        System.out.println(ODRLRDF.getRDF(policy));
//        System.out.println(ODRLRDF.getRDF(request));

        
//       RDFDataMgr.write(System.out, ds, Lang.NQUADS) ;          
        
    }

    /**
     * Loads a CreativeCommons license. 
     */
    private void loadCC() {
        LDRConfig.Load();
        String folder=LDRConfig.getLicensesfolder();
        List<Policy> policies = PolicyManager.load(folder+"/cc-by.ttl");
        for(Policy policy : policies)
        {
            System.out.println(ODRLRDF.getRDF(policy));
            System.out.println("Open: " + policy.isOpen());
            System.out.println("Per triple: " + policy.isPerTriple());
            System.out.println("Offer: " + policy.isInOffer());
        }
    }
    
    private void loadMetashare(){
        LDRConfig.Load();
        String folder=LDRConfig.getLicensesfolder();
        List<Policy> policies = PolicyManager.load(folder+"/C-NoReD.ttl");
        for(Policy policy : policies)
        {
            System.out.println(ODRLRDF.getRDF(policy));
            System.out.println("Open: " + policy.isOpen());
            System.out.println("Per triple: " + policy.isPerTriple());
            System.out.println("Offer: " + policy.isInOffer());
        }
    }    
    
    private void loadComplex(){
        LDRConfig.Load();
        String folder=LDRConfig.getLicensesfolder();
        List<Policy> policies = PolicyManager.load(folder+"/research.ttl");
        for(Policy policy : policies)
        {
            System.out.println(ODRLRDF.getRDF(policy));
            System.out.println("Open: " + policy.isOpen());
            System.out.println("Per triple: " + policy.isPerTriple());
            System.out.println("Offer: " + policy.isInOffer());
        }
    }
    
    
    private void loadODRLPolicyWithPayment(){
        LDRConfig.Load();
        String folder=LDRConfig.getLicensesfolder();
        List<Policy> policies = PolicyManager.load(folder+"/pago15euros.ttl");
        for(Policy policy : policies)
        {
            System.out.println(ODRLRDF.getRDF(policy));
            System.out.println("Open: " + policy.isOpen());
            System.out.println("Per triple: " + policy.isPerTriple());
            System.out.println("Offer: " + policy.isInOffer());
        }
    }
    
    private void loadODRLPolicyWithPaymentPerTriple(){
        LDRConfig.Load();
        String folder=LDRConfig.getLicensesfolder();
        List<Policy> policies = PolicyManager.load(folder+"/onecent.ttl");
        for(Policy policy : policies)
        {
            System.out.println(ODRLRDF.getRDF(policy));
            System.out.println("Open: " + policy.isOpen());
            System.out.println("Per triple: " + policy.isPerTriple());
            System.out.println("Offer: " + policy.isInOffer());
        }
    }


}
