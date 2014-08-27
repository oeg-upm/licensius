package odrlmodel;

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
 * @author Victor Rodríguez Doncel
 */
public class Permission extends Rule {
    
    public Permission()
    {
        setKindOfRule(Rule.RULE_PERMISSION);
    }
    
}
