package oeg.rdflicense2.transformers;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.Action;
import oeg.jodrlapi.odrlmodel.Constraint;
import oeg.jodrlapi.odrlmodel.Duty;
import oeg.jodrlapi.odrlmodel.Party;
import oeg.jodrlapi.odrlmodel.Permission;
import oeg.jodrlapi.odrlmodel.Policy;
import oeg.jodrlapi.odrlmodel.Prohibition;
import oeg.rdflicense2.TransformationResponse;
import org.apache.jena.riot.Lang;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * From Metashare XML to ODRL.
 * 
 * @seeAlso https://docs.google.com/spreadsheets/d/1od-ip9FZ17Y0B8kVh6-yN8lGQh9i4y8KTGeMZNJ--IE/edit#gid=0
 * @author vroddon
 */
public class TransformerXMLODRL {
    
        public static void main(String args[])
        {
            String xml ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
"<ms:MetadataRecord xmlns:ms=\"http://w3id.org/meta-share/meta-share/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://w3id.org/meta-share/meta-share/ https://live.european-language-grid.eu/metadata-schema/ELG-SHARE.xsd\"><ms:metadataCreationDate>2020-03-05</ms:metadataCreationDate><ms:metadataLastDateUpdated>2020-10-21</ms:metadataLastDateUpdated><ms:metadataCurator><ms:actorType>Person</ms:actorType><ms:surname xml:lang=\"en\">Mapelli</ms:surname><ms:givenName xml:lang=\"en\">Valérie</ms:givenName></ms:metadataCurator><ms:compliesWith>http://w3id.org/meta-share/meta-share/ELG-SHARE</ms:compliesWith><ms:metadataCreator><ms:actorType>Person</ms:actorType><ms:surname xml:lang=\"en\">Mapelli</ms:surname><ms:givenName xml:lang=\"en\">Valérie</ms:givenName></ms:metadataCreator><ms:DescribedEntity><ms:LicenceTerms><ms:entityType>LicenceTerms</ms:entityType><ms:LicenceIdentifier ms:LicenceIdentifierScheme=\"http://w3id.org/meta-share/meta-share/other\">ELRA-END-USER-ACADEMIC-MEMBER-NONCOMMERCIALUSE-1.0</ms:LicenceIdentifier><ms:licenceTermsName xml:lang=\"en\">ELRA-END-USER-ACADEMIC-MEMBER-NONCOMMERCIALUSE-1.0</ms:licenceTermsName><ms:licenceTermsURL>http://catalogue.elra.info/static/from_media/metashare/licences/ELRA_END_USER.pdf</ms:licenceTermsURL><ms:conditionOfUse>http://w3id.org/meta-share/meta-share/nonCommercialUse</ms:conditionOfUse><ms:conditionOfUse>http://w3id.org/meta-share/meta-share/attribution</ms:conditionOfUse><ms:licenceCategory>http://w3id.org/meta-share/meta-share/allowsAccessWithSignature</ms:licenceCategory><ms:licenceCategory>http://w3id.org/meta-share/meta-share/requiresUserAuthentication</ms:licenceCategory></ms:LicenceTerms></ms:DescribedEntity></ms:MetadataRecord>";
            System.out.println(transformXML2ODRL(xml).transformation);

            if (true)
                return;

            Policy policy = new Policy("_:policy");
            Permission permission = new Permission();

            Duty duty = new Duty();
            Action action = new Action("http://purl.org/odrl-lr/deposit");
            action.addRefinement(new Constraint("http://purl.org/odrl-lr/depositingParty",ODRLRDF.REQ.toString(), ODRLRDF.RASSIGNER.toString()));
            duty.setActions(Arrays.asList(new Action[]{action}));
            permission.addDuty(duty);

            permission.setActions(Arrays.asList(new Action[]{new Action("odrl:reproduce")}));

            policy.addRule(permission);
            System.out.println(policy.toJSONLD());
            System.out.println(ODRLRDF.getRDF(policy, Lang.TTL)+"\n================================\n");
        }
    
        public static TransformationResponse transformXML2ODRL(String xml) {
            System.out.println("Vamos a transformar a ODRL!");
        try {
            Policy policy = new Policy();
            Permission permission = new Permission();
            List<Action> actions = new ArrayList(); 
            actions.add(new Action("odrl:reproduce"));

            InputSource is = new InputSource(new StringReader(xml));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            
            //for each ms:LicenceTerms we create a http://w3id.org/meta-share/meta-share/LicenceTerms
            NodeList nl0 = doc.getElementsByTagName("ms:LicenceTerms");
            for(int i=0;i<nl0.getLength();i++)
            {
                Node n0 = nl0.item(i);
                NodeList nl1 = n0.getChildNodes();

                for(int j=0;j<nl1.getLength();j++)
                {
                    Node n1 = nl1.item(j);
                    String nodename = n1.getNodeName();
                    String nodetext = n1.getTextContent();
                    if (nodename.equals("ms:licenceTermsName"))
                    {
                        policy.setTitle(nodetext);
                    }
                    if (nodename.equals("ms:licenceTermsShortName"))
                    {
                        try{
                            URI uri = new URI("http://www.w3.org/ns/odrl/2/policy/"+nodetext);
                            policy.setURI(uri.toString());
                        }catch(Exception e0){
                            
                        }
                    }
                    if (nodename.equals("ms:licenceTermsURL"))
                    {
                        policy.setSource(nodetext);
                    }
                    if (nodename.equals("ms:conditionOfUse"))
                    {
                        if (nodetext.equals("ms:noDerivatives") || nodetext.equals("http://w3id.org/meta-share/meta-share/noDerivatives"))
                        {
                            Prohibition prohibition = new Prohibition();
                            prohibition.setActions(Arrays.asList(new Action[]{new Action("cc:DerivativeWorks")}));
                            policy.addRule(prohibition);
                        }
                        if (nodetext.equals("ms:nonCommercialUse") || nodetext.equals("http://w3id.org/meta-share/meta-share/nonCommercialUse"))
                        {
                            Prohibition prohibition = new Prohibition();
                            prohibition.setActions(Arrays.asList(new Action[]{new Action("cc:CommercialUse")}));
                            policy.addRule(prohibition);
                        }
                        if (nodetext.equals("ms:noRedistribution") || nodetext.equals("http://w3id.org/meta-share/meta-share/noRedistribution"))
                        {
                            Prohibition prohibition = new Prohibition();
                            prohibition.setActions(Arrays.asList(new Action[]{new Action("cc:Distribution")}));
                            policy.addRule(prohibition);
                        }
                        if (nodetext.equals("ms:attribution") || nodetext.equals("http://w3id.org/meta-share/meta-share/attribution"))
                        {
                            Duty duty = new Duty();
                            duty.setActions(Arrays.asList(new Action[]{new Action("cc:Attribution")}));
                            permission.addDuty(duty);
                        }
                        if (nodetext.equals("ms:shareAlike") || nodetext.equals("http://w3id.org/meta-share/meta-share/shareAlike"))
                        {
                            Duty duty = new Duty();
                            duty.setActions(Arrays.asList(new Action[]{new Action("cc:ShareAlike")}));
                            permission.addDuty(duty);
                        }
                        if (nodetext.equals("ms:redeposit") || nodetext.equals("http://w3id.org/meta-share/meta-share/redeposit"))
                        {
                            Duty duty = new Duty();
                            Action action2 = new Action("odrl-lr:deposit"); //http://purl.org/odrl-lr/deposit
                            action2.addRefinement(new Constraint("http://purl.org/odrl-lr/depositingParty",ODRLRDF.REQ.toString(), ODRLRDF.RASSIGNER.toString()));
                            duty.setActions(Arrays.asList(new Action[]{action2}));
                            permission.addDuty(duty);
                        }
                        if (nodetext.equals("ms:informLicensor") || nodetext.equals("http://w3id.org/meta-share/meta-share/informLicensor"))
                        {
                            Duty duty = new Duty();
                            Action action2 = new Action("odrl-lr:informLicensor");
                            action2.addRefinement(new Constraint("http://purl.org/odrl-lr/informedParty",ODRLRDF.REQ.toString(), ODRLRDF.RASSIGNER.toString()));
                            action2.addRefinement(new Constraint("http://purl.org/odrl-lr/objectOfReport",ODRLRDF.REQ.toString(), "odrl:Use"));
                            duty.setActions(Arrays.asList(new Action[]{action2}));
                            permission.addDuty(duty);
                        }
                        if (nodetext.equals("ms:spatial") || nodetext.equals("http://w3id.org/meta-share/meta-share/spatial"))
                        {
                            Constraint constraint = new Constraint("odrl:spatial",ODRLRDF.REQ.toString(), "only at assignee's site");
                            permission.addConstraint(constraint);
                        }
                        if (nodetext.equals("ms:academicUseOnly") || nodetext.equals("http://w3id.org/meta-share/meta-share/academicUseOnly"))
                        {
                            Constraint constraint = new Constraint("odrl:purpose",ODRLRDF.REQ.toString(), "ms:academicUse");
                            permission.addConstraint(constraint);
                        }
                        if (nodetext.equals("ms:evaluationUse") || nodetext.equals("http://w3id.org/meta-share/meta-share/evaluatonUse"))
                        {
                            Constraint constraint = new Constraint("odrl:purpose",ODRLRDF.REQ.toString(), "ms:evaluation");
                            permission.addConstraint(constraint);
                        }
                        if (nodetext.equals("ms:languageEngineeringResearchUse") || nodetext.equals("http://w3id.org/meta-share/meta-share/languageEngineeringResearchUse"))
                        {
                            Constraint constraint = new Constraint("odrl:purpose",ODRLRDF.REQ.toString(), "ms:languageEngineeringResearchUse");
                            permission.addConstraint(constraint);
                        }
                        if (nodetext.equals("ms:researchUse") || nodetext.equals("http://w3id.org/meta-share/meta-share/researchUse"))
                        {
                            Constraint constraint = new Constraint("odrl:purpose",ODRLRDF.REQ.toString(), "ms:research");
                            permission.addConstraint(constraint);
                        }
                        if (nodetext.equals("ms:trainingUse") || nodetext.equals("http://w3id.org/meta-share/meta-share/trainingUse"))
                        {
                            Constraint constraint = new Constraint("odrl:purpose",ODRLRDF.REQ.toString(), "ms:training");
                            permission.addConstraint(constraint);
                        }
                        if (nodetext.equals("ms:academicUser") || nodetext.equals("http://w3id.org/meta-share/meta-share/academicUser"))
                        {
                            Party assignee = new Party("");
                            assignee.addRefinement(new Constraint("odrl:userType",ODRLRDF.REQ.toString(), "ms:academic"));
                            policy.setAssigneeInAllRules(assignee);
                        }
                        if (nodetext.equals("ms:commercialUser") || nodetext.equals("http://w3id.org/meta-share/meta-share/commercialUse"))
                        {
                            Party assignee = new Party("");
                            assignee.addRefinement(new Constraint("odrl:userType",ODRLRDF.REQ.toString(), "ms:commercial"));
                            policy.setAssigneeInAllRules(assignee);
                        }
                        if (nodetext.equals("ms:memberOfAssociation") || nodetext.equals("http://w3id.org/meta-share/meta-share/memberOfAssociation"))
                        {
                            Party assignee = new Party("");
                            assignee.addRefinement(new Constraint("odrl:userType",ODRLRDF.RPARTOF.toString(), "association X"));
                            policy.setAssigneeInAllRules(assignee);
                        }
                        if (nodetext.equals("ms:requestPlan") || nodetext.equals("http://w3id.org/meta-share/meta-share/requestPlan"))
                        {
                            Duty duty = new Duty();
                            Action action2 = new Action("odrl-lr:report");
                            action2.addRefinement(new Constraint("http://purl.org/odrl-lr/informedParty",ODRLRDF.REQ.toString(), ODRLRDF.RASSIGNER.toString()));
                            action2.addRefinement(new Constraint("http://purl.org/odrl-lr/objectOfReport",ODRLRDF.REQ.toString(), "odrl-lr:ResearchPlan"));
                            duty.setActions(Arrays.asList(new Action[]{action2}));
                            permission.addDuty(duty);
                        }
                        
                    }
                    if (nodename.equals("ms:licenceCategory"))
                    {
                        if (nodetext.equals("ms:allowsDirectAccess") || nodetext.equals("http://w3id.org/meta-share/meta-share/allowsDirectAccess"))
                        {
                            actions.add(new Action("odrl:reproduce"));
                            actions.add(new Action("odrl-lr:execute"));
                        }
                        if (nodetext.equals("ms:allowsProcessing") || nodetext.equals("http://w3id.org/meta-share/meta-share/allowsProcessing"))
                        {
                            actions.add(new Action("odrl-lr:process"));
                        }
                        if (nodetext.equals("ms:allowsAccessWithSignature") || nodetext.equals("http://w3id.org/meta-share/meta-share/allowsAccessWithSignature"))
                        {
                            Duty duty = new Duty();
                            Action action2 = new Action("odrl-lr:signLicense");
                            duty.addAction(action2);
                            permission.addDuty(duty);
                        }
                        if (nodetext.equals("ms:requiresUserAuthentication") || nodetext.equals("http://w3id.org/meta-share/meta-share/requiresUserAuthentication"))
                        {
                            Duty duty = new Duty();
                            Action action2 = new Action("odrl-lr:signIn");
                            duty.addAction(action2);
                            permission.addDuty(duty);
                        }                  
                    }
                    
                    
                    
                    
                }
            }
            permission.setActions(actions);
            policy.addRule(permission);

            /*
            String ttl = "";
            String prefix = "@prefix odrl: <http://www.w3.org/ns/odrl/2/> .\n@prefix dct: <http://purl.org/dc/terms/> .";
            String line0="_:license a odrl:Policy ;";
            ttl+=line0+"\n";
            String basepermission = "odrl:permission [ odrl:action odrl:reproduce ] \n";
            String line1 = "";*/
            

            String ttl = ODRLRDF.getRDF(policy, Lang.TTL);
            TransformationResponse tr = new TransformationResponse(true, ttl);
            
            return tr;
        }catch(Exception e)
        {
            TransformationResponse tr = new TransformationResponse(false, e.getMessage());
            return tr;
        }
    }
}
