package oeg.rdflicense2.transformers;

import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.Action;
import oeg.jodrlapi.odrlmodel.Constraint;
import oeg.jodrlapi.odrlmodel.Duty;
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
      //      String xml ="<?xml version=\"1.0\" encoding=\"utf-8\"?><ms:MetadataRecord xmlns:ms=\"http://w3id.org/meta-share/meta-share/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://w3id.org/meta-share/meta-share/ https://live.european-language-grid.eu/metadata-schema/ELG-SHARE.xsd\"><ms:metadataCreationDate>2020-02-27</ms:metadataCreationDate><ms:metadataLastDateUpdated>2020-10-21</ms:metadataLastDateUpdated><ms:metadataCurator><ms:actorType>Person</ms:actorType><ms:surname xml:lang=\"en\">admin</ms:surname><ms:givenName xml:lang=\"en\">elg</ms:givenName></ms:metadataCurator><ms:compliesWith>http://w3id.org/meta-share/meta-share/ELG-SHARE</ms:compliesWith><ms:metadataCreator><ms:actorType>Person</ms:actorType><ms:surname xml:lang=\"en\">admin</ms:surname><ms:givenName xml:lang=\"en\">elg</ms:givenName></ms:metadataCreator><ms:DescribedEntity><ms:LicenceTerms><ms:entityType>LicenceTerms</ms:entityType><ms:LicenceIdentifier ms:LicenceIdentifierScheme=\"http://w3id.org/meta-share/meta-share/SPDX\">Abstyles</ms:LicenceIdentifier><ms:licenceTermsName xml:lang=\"en\">Abstyles License</ms:licenceTermsName><ms:licenceTermsShortName xml:lang=\"en\">Abstyles</ms:licenceTermsShortName><ms:licenceTermsURL>https://fedoraproject.org/wiki/Licensing/Abstyles</ms:licenceTermsURL><ms:conditionOfUse>http://w3id.org/meta-share/meta-share/unspecified</ms:conditionOfUse></ms:LicenceTerms></ms:DescribedEntity></ms:MetadataRecord>";
      //      System.out.println(transformXML2ODRL(xml).transformation);
        }
    
        public static TransformationResponse transformXML2ODRL(String xml) {
            System.out.println("Vamos a transformar!");
        try {
            Policy policy = new Policy();
            Permission permission = new Permission();
            permission.setActions(Arrays.asList(new Action[]{new Action("odrl:reproduce")}));

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
                            Action action = new Action("odrl-lr:deposit"); //http://purl.org/odrl-lr/deposit
                            action.addRefinement(new Constraint("http://purl.org/odrl-lr/depositingParty",ODRLRDF.REQ.toString(), ODRLRDF.RASSIGNER.toString()));
                            duty.setActions(Arrays.asList(new Action[]{action}));
                            permission.addDuty(duty);
                        }
                        if (nodetext.equals("ms:informLicensor") || nodetext.equals("http://w3id.org/meta-share/meta-share/informLicensor"))
                        {
                            Duty duty = new Duty();
                            Action action = new Action("odrl-lr:informLicensor");
                            action.addRefinement(new Constraint("http://purl.org/odrl-lr/informedParty",ODRLRDF.REQ.toString(), ODRLRDF.RASSIGNER.toString()));
                            action.addRefinement(new Constraint("http://purl.org/odrl-lr/objectOfReport",ODRLRDF.REQ.toString(), "odrl:Use"));
                            duty.setActions(Arrays.asList(new Action[]{action}));
                            permission.addDuty(duty);
                        }
                        
                    }
                    
                    
                }
            }
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
