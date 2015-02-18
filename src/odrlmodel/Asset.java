package odrlmodel;

//JAVA
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import odrlmodel.managers.PolicyManagerOld;


/**
 * This class represents an ODRL 2.0 Asset
 * The Asset entity is aimed at identifying the content that is the subject of an ODRL policy, e.g. a media file or ebook. 
 * Furthermore, it can be used to represent other Asset entities that are needed to undertake the Policy expression, such as with the Duty entity. 
 * The Asset entity is referred to by the Permission and/or Prohibition entities, and also by the Duty entity.
 * The Asset entity contains the following attribute:
 * uid: the unique identification of the Asset (REQUIRED)
 * The identification of the Asset entity is a key foundation of the ODRL Policy language. However, there are some use cases where the ODRL Policy expression MAY be embedded inside the target Asset. In these cases, it MAY be more appropriate to provide, or infer, a link to the Asset entity (as the complete Asset uid may not be known at the time) through the local context. Use of such inference and context MUST be documented in the relevant ODRL community Profile.
 * Since ODRL _policies could deal with any kind of asset, the ODRL Core Model does not provide additional metadata to describe Asset entities of particular media types. It is recommended to use already existing metadata standards, such as Dublin Core Metadata Terms that are appropriate to the Asset type or purpose.
 * The Relation entity is used to associate the Asset entity with the relevant Permission, Prohibition, and Duty entities
 * @author Victor
 */
public class Asset extends MetadataObject {

    /**
     * An asseet may have attached a set of _policies
     */
    public List<Policy> policies = new ArrayList();

    /**
     * Creates a Linked Data resource, identifiable by its URI.
     * @param s URI of the asset
     */
    public Asset(String s) {
        uri=s;
    }

    /**
     * Creates a Linked Data resource, identifiable by its URI.
     * A random URI is given.
     */
    public Asset() {
        this(MetadataObject.DEFAULT_NAMESPACE+"asset/" + UUID.randomUUID().toString());        
    }

    /**
     * 
     */
    public String toString() {
        String label = getLabel("");
        if (label.isEmpty()) {
            return uri.toString();
        }
        return label;
    }

    /**
     * Returns the list of _policies ya completada
     * @return List of _policies
     */
    public List<Policy> getPolicies() {
        
        ///VVXXXVVV
        List<Policy> lpolicies = new ArrayList();
        for(Policy p : policies)
        {
            Policy policy2 = PolicyManagerOld.getPolicy(p.getURI());
            if (policy2!=null)
                lpolicies.add(policy2);
            else
                lpolicies.add(p);
                        
        }
        
        return lpolicies;
    }

    /**
     * Sets the given policy as the only policy
     * @param policy The policy
     */
    public void setPolicy(Policy policy) {
        policies.clear();
        policies.add(policy);
    }

    /**
     * Adds a policy to the list of policy
     * @param policy New policy to be added
     */
    public void addPolicy(Policy policy) {
        policies.add(policy);
    }
    
    /**
     * Removes the policy of the given URI
     * @pram uri of the policy to be removed
     */
    public void removePolicy(String uri)
    {
        for (int i=0;i<policies.size();i++)
        {
            Policy policy = policies.get(i);
            if (policy.getURI().equals(uri))
            {
                policies.remove(policy);
                return;
            }
        }
    }

    /**
     * Returns true if it has at least one Policy which isInOffer
     * @return true if in offer
     */
    public boolean isInOffer() {
        List<Policy> _policies = getPolicies();
        for(Policy policy : _policies)
            if (policy.isInOffer())
                return true;
        return false;
    }

    /**
     * Returns true if it has at least one Policy is Open
     * @return true if open
     */
    public boolean isOpen() {
        List<Policy> _policies = getPolicies();
        for(Policy policy : _policies)
            if (policy.isOpen())
                return true;
        return false;
    }

}
