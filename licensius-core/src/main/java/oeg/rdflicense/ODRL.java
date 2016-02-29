package oeg.rdflicense;


import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * This class includes some static shortcuts to ODRL elements as Jena resources and properties
 * See the spec at https://www.w3.org/ns/odrl/2/ODRL21
 * @author Victor
 */
public class ODRL {
    public static Property PPERMISSION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/permission");
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
    
    public static Resource ROFFER = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Offer");
    public static Resource RREQUEST = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Request");
    public static Resource RAGREEMENT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Agreement");
    public static Resource RTICKET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Ticket");
    
    
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
    
    
}
