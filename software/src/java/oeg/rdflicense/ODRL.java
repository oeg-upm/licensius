package oeg.rdflicense;

//JENA
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

//JAVA
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains several the fast access to some ODRL classes and properties
 * @author Victor
 */
public class ODRL {

public static final Resource RPOLICY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Policy");
    public static final Resource RLICENSE = ModelFactory.createDefaultModel().createResource("http://purl.org/dc/terms/LicenseDocument");
    public static final Resource RCCLICENSE = ModelFactory.createDefaultModel().createResource("http://creativecommons.org/ns#License");
    public static final Resource RSET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Set");
    public static final Resource ROFFER = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Offer");
    public static final Resource RREQUEST = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Request");
    public static final Resource RAGREEMENT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Agreement");
    public static final Resource RTICKET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Ticket");
    
    public static Property PPERMISSION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/permission");
    public static Resource OFFER = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Offer");
    public static Resource REQUEST = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Request");
    public static Resource RPERMISSION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Permission");
    public static Resource RPROHIBITION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Prohibition");
    public static Property PPROHIBITION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/prohibition");
    public static Resource RDUTY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Duty");
    public static Resource RCONSTRAINT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Constraint");
    public static Resource RACTION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Action");
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
    public static Literal LEQ = ModelFactory.createDefaultModel().createLiteral("http://www.w3.org/ns/odrl/2/eq");    
    
    
    /**
     * Gets a list of common classes used to denote a license or policy
     */
    public static List<Resource> getCommonPolicyClasses()
    {
        List<Resource> list = new ArrayList();
        list.add(RPOLICY);
        list.add(RLICENSE);
        list.add(RCCLICENSE);
        list.add(RSET);
        list.add(ROFFER);
        list.add(RREQUEST);
        list.add(RAGREEMENT);
        list.add(RTICKET);
        return list;
    }
    
    /**
     * Gets the first comment found for an element
     */
    public static String getFirstComment(String element)
    {
        System.out.println(element);
//        Model model = getStandardModels();
        Model model = ModelFactory.createDefaultModel();
        Resource rright = model.createResource(element);

        
        NodeIterator nit = model.listObjectsOfProperty(rright, RDFS.comment);
        while(nit.hasNext())
        {
            RDFNode nodo = nit.next();
            if (nodo.isLiteral())
                return nodo.asLiteral().getLexicalForm();
        }
        return "";
    }
    
    /**
     * Loads the standard models in a Jena Model
     * It may take relatively long (too long to be called before serving a web)
     */
    public static Model getStandardModels()
    {
        Model model = ModelFactory.createDefaultModel();
        
        //We load CREATIVE COMMONS
        InputStream in = ODRL.class.getResourceAsStream("../../resources/owl/creativecommons.rdf");
        if (in != null) {
            try{
                Model parcial = ModelFactory.createDefaultModel();
                String raw = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String str = "";
                while ((str = br.readLine()) != null) {
                    raw += str + " \n";
                }
                InputStream is = new ByteArrayInputStream(raw.getBytes());
                parcial.read(is,null);
                model.add(parcial);
            }catch(Exception e)
            {
                System.err.println("Not loaded " + e.getLocalizedMessage());
            }
        }

        //We load ODRL
        in = ODRL.class.getResourceAsStream("../../resources/owl/odrl21.rdf");
        if (in != null) {
            try{
                Model parcial = ModelFactory.createDefaultModel();
                String raw = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String str = "";
                while ((str = br.readLine()) != null) {
                    raw += str + " \n";
                }
                InputStream is = new ByteArrayInputStream(raw.getBytes());
                parcial.read(is,null, "RDF/XML");
                model.add(parcial);
            }catch(Exception e)
            {
                System.err.println("Not loaded " + e.getLocalizedMessage());
            }
        }
        
        //We load LDR
        in = ODRL.class.getResourceAsStream("../../resources/owl/ldr.ttl");
        if (in != null) {
            try{
                Model parcial = ModelFactory.createDefaultModel();
                String raw = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String str = "";
                while ((str = br.readLine()) != null) {
                    raw += str + " \n";
                }
                InputStream is = new ByteArrayInputStream(raw.getBytes());
                parcial.read(is,null, "TTL");
                model.add(parcial);
            }catch(Exception e)
            {
                System.err.println("Not loaded " + e.getLocalizedMessage());
            }
        }

        
        return model;
    }
    
    
}
