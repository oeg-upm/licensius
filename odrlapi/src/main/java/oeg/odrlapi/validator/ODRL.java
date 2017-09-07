package oeg.odrlapi.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public static Resource RLOGICALCONSTRAINT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/LogicalConstraint");
    public static Resource RASSETCOLLECTION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/AssetCollection");
    
    public static Resource RACTION = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Action");
    public static Property PTARGET = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/target");
    public static Property PASSIGNER = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assigner");
    public static Property PASSIGNEE = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assignee");
    public static Property PACTION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/action");
    public static Property PDUTY = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/duty");
    public static Property POBLIGATION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/obligation");
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
    public static Property PASSIGNEROF = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assignerOf");
    public static Property PASSIGNEEOF = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/assigneeOf");
    public static Property PHASPOLICY = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/hasPolicy");
    public static Property PREFINEMENT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/refinement");
    public static Property PLEFTOPERAND = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/leftOperand");
    public static Property PRIGHTOPERAND = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/rightOperand");
    
    public static Property PINHERITFROM = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/inheritFrom");
    public static Property PPROFILE = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/profile");
    public static Property PCONFLICT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/conflict");
    public static Property PSOURCE = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/odrl/2/source");
    
            
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
    
    /**
     * Returns the ODRL actions. Does NOT load the ontology in order to speed
     * up.
     */
    public static Set<String> getActions() {
        Set<String> set = new HashSet();
        set.add("http://www.w3.org/ns/odrl/2/transfer");
        set.add("http://www.w3.org/ns/odrl/2/use");
        
        set.add("http://www.w3.org/ns/odrl/2/acceptTracking");
        set.add("http://www.w3.org/ns/odrl/2/aggregate");
        set.add("http://www.w3.org/ns/odrl/2/annotate");
        set.add("http://www.w3.org/ns/odrl/2/anonymize");
        set.add("http://www.w3.org/ns/odrl/2/archive");
        set.add("http://www.w3.org/ns/odrl/2/attribute");
        set.add("http://creativecommons.org/ns#Attribution");
        set.add("http://creativecommons.org/ns#CommericalUse");
        set.add("http://www.w3.org/ns/odrl/2/compensate");
        set.add("http://www.w3.org/ns/odrl/2/concurrentUse");
        set.add("http://www.w3.org/ns/odrl/2/delete");
        set.add("http://www.w3.org/ns/odrl/2/derive");
        set.add("http://creativecommons.org/ns#DerivativeWorks");
        set.add("http://www.w3.org/ns/odrl/2/digitize");
        set.add("http://www.w3.org/ns/odrl/2/display");
        set.add("http://www.w3.org/ns/odrl/2/distribute");
        set.add("http://creativecommons.org/ns#Distribution");
        set.add("http://www.w3.org/ns/odrl/2/ensureExclusivity");
        set.add("http://www.w3.org/ns/odrl/2/execute");
        set.add("http://www.w3.org/ns/odrl/2/extract");
        set.add("http://www.w3.org/ns/odrl/2/give");
        set.add("http://www.w3.org/ns/odrl/2/grantUse");
        set.add("http://www.w3.org/ns/odrl/2/include");
        set.add("http://www.w3.org/ns/odrl/2/index");
        set.add("http://www.w3.org/ns/odrl/2/inform");
        set.add("http://www.w3.org/ns/odrl/2/install");
        set.add("http://www.w3.org/ns/odrl/2/modify");
        set.add("http://www.w3.org/ns/odrl/2/move");
        set.add("http://www.w3.org/ns/odrl/2/nextPolicy");
        set.add("http://creativecommons.org/ns#Notice");
        set.add("http://www.w3.org/ns/odrl/2/obtainConsent");
        set.add("http://www.w3.org/ns/odrl/2/play");
        set.add("http://www.w3.org/ns/odrl/2/present");
        set.add("http://www.w3.org/ns/odrl/2/print");
        set.add("http://www.w3.org/ns/odrl/2/read");
        set.add("http://www.w3.org/ns/odrl/2/reproduce");
        set.add("http://creativecommons.org/ns#Reproduction");
        set.add("http://www.w3.org/ns/odrl/2/reviewPolicy");
        set.add("http://www.w3.org/ns/odrl/2/sell");
        set.add("http://creativecommons.org/ns#ShareAlike");
        set.add("http://creativecommons.org/ns#Sharing");
        set.add("http://creativecommons.org/ns#SourceCode");
        set.add("http://www.w3.org/ns/odrl/2/stream");
        set.add("http://www.w3.org/ns/odrl/2/synchronize");
        set.add("http://www.w3.org/ns/odrl/2/textToSpeech");
        set.add("http://www.w3.org/ns/odrl/2/transform");
        set.add("http://www.w3.org/ns/odrl/2/translate");
        set.add("http://www.w3.org/ns/odrl/2/uninstall");
        set.add("http://www.w3.org/ns/odrl/2/watermark");
        return set;
    }

    public static Set<String> getOperators() {
        Set<String> set = new HashSet();
        set.add("http://www.w3.org/ns/odrl/2/eq");
        set.add("http://www.w3.org/ns/odrl/2/gt");
        set.add("http://www.w3.org/ns/odrl/2/gteq");
        set.add("http://www.w3.org/ns/odrl/2/lt");
        set.add("http://www.w3.org/ns/odrl/2/lteq");
        set.add("http://www.w3.org/ns/odrl/2/neq");
        set.add("http://www.w3.org/ns/odrl/2/isA");
        set.add("http://www.w3.org/ns/odrl/2/hasPart");
        set.add("http://www.w3.org/ns/odrl/2/isPartOf");
        set.add("http://www.w3.org/ns/odrl/2/isAllOf");
        set.add("http://www.w3.org/ns/odrl/2/isAnyOf");
        set.add("http://www.w3.org/ns/odrl/2/isNoneOf");
        return set;
    }

    public static Set<String> getLeftOperands() {
        Set<String> set = new HashSet();
        set.add("http://www.w3.org/ns/odrl/2/absolutePosition");
        set.add("http://www.w3.org/ns/odrl/2/absoluteSpatialPosition");
        set.add("http://www.w3.org/ns/odrl/2/absoluteTemporalPosition");
        set.add("http://www.w3.org/ns/odrl/2/absoluteSize");
        set.add("http://www.w3.org/ns/odrl/2/count");
        set.add("http://www.w3.org/ns/odrl/2/dateTime");
        set.add("http://www.w3.org/ns/odrl/2/delayPeriod");
        set.add("http://www.w3.org/ns/odrl/2/deliveryChannel");
        set.add("http://www.w3.org/ns/odrl/2/elapsedTime");
        set.add("http://www.w3.org/ns/odrl/2/event");
        set.add("http://www.w3.org/ns/odrl/2/fileFormat");
        set.add("http://www.w3.org/ns/odrl/2/industry");
        set.add("http://www.w3.org/ns/odrl/2/language");
        set.add("http://www.w3.org/ns/odrl/2/media");
        set.add("http://www.w3.org/ns/odrl/2/meteredTime");
        set.add("http://www.w3.org/ns/odrl/2/payAmount");
        set.add("http://www.w3.org/ns/odrl/2/percentage");
        set.add("http://www.w3.org/ns/odrl/2/product");
        set.add("http://www.w3.org/ns/odrl/2/purpose");
        set.add("http://www.w3.org/ns/odrl/2/recipient");
        set.add("http://www.w3.org/ns/odrl/2/relativePosition");
        set.add("http://www.w3.org/ns/odrl/2/relativeSpatialPosition");
        set.add("http://www.w3.org/ns/odrl/2/relativeTemporalPosition");
        set.add("http://www.w3.org/ns/odrl/2/relativeSize");
        set.add("http://www.w3.org/ns/odrl/2/resolution");
        set.add("http://www.w3.org/ns/odrl/2/spatial");
        set.add("http://www.w3.org/ns/odrl/2/spatialCoordinates");
        set.add("http://www.w3.org/ns/odrl/2/systemDevice");
        set.add("http://www.w3.org/ns/odrl/2/timeInterval");
        set.add("http://www.w3.org/ns/odrl/2/unitOfCount");
        set.add("http://www.w3.org/ns/odrl/2/version");
        set.add("http://www.w3.org/ns/odrl/2/virtualLocation");

        return set;
    }
    
}
