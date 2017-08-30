package odrlmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * The Permission entity indicates the Actions that the assignee is permitted to 
 * perform on the associated Asset. In other words, what the assigner (supplier) 
 * has granted to the assignee (consumer).
 * 
 * An ODRL policy expression MAY contain at least one Permission. 
 * It is important to verify the semantics of the Policy type attribute as this 
 * MAY indicate additional constraints on the Policy expression structure.
 *
 * If several Permission entities are referred to by a Policy, then all of them are valid.
 * 
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class Permission extends Rule {
    
    //A permission may have duties
    private List<Duty> duties = new ArrayList();

    /**
     * Permission constructor
     * Permission are by default anonymous
     */
    public Permission()
    {
        super("");
        setKindOfRule(Rule.RULE_PERMISSION);
    }
    
   /**
     * Creates a permission identified by the given URI. 
     * @param _uri URI of the prohibition
     */
    public Permission(String _uri) {
        super(_uri);
        setKindOfRule(Rule.RULE_PERMISSION);
    }      
    
    /**
     * Sets a single duty associated to this permission
     * @param duty Duty to be retrieved
     */
    public void setDuty(Duty duty)
    {
        duties.clear();
        duties.add(duty);
    }
    
    /**
     * Sets the duties associated to this permission
     * @param _duties List of duties to be retrieved
     */
    public void setDuties(List<Duty> _duties)
    {
        duties = _duties;
    }
    
    /**
     * Sets the duties associated to this permission
     * @return List of duties
     */
    public List<Duty> getDuties()
    {
        return duties;
    }
    
            
            
}
