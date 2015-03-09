package oeg.rdflicense;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Victor
 */
public class ODRL {

    private static Resource RPOLICY = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Policy");
    private static Resource RLICENSE = ModelFactory.createDefaultModel().createResource("http://purl.org/dc/terms/LicenseDocument");
    private static Resource RCCLICENSE = ModelFactory.createDefaultModel().createResource("http://creativecommons.org/ns#License");
    private static Resource RSET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Set");
    private static Resource ROFFER = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Offer");
    private static Resource RREQUEST = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Request");
    private static Resource RAGREEMENT = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Agreement");
    private static Resource RTICKET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/odrl/2/Ticket");
    
    
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
