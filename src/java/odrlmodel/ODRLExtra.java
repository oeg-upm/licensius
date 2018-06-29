package odrlmodel;

//JAVA
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//APACHE COMMONS
import org.apache.commons.io.FilenameUtils;


/**
 * This class contains non-tested, demo functionality
 * @author Victor
 */
public class ODRLExtra {
 
    /**
     * @deprecated
     */
    public static boolean isOpen(Policy p) {
        boolean open = false;
        for (Rule r : p.rules) {
            List<Action> actions = r.getActions();
            boolean hasPlay = false;
            for (Action action : actions) {
                if (hasPlay(action)) {
                    hasPlay = true;
                }
            }
            if (hasPlay == false) {
                continue;
            }
            List<Constraint> lc = r.getConstraints();
            for (Constraint constraint : lc) {
                return true;
            }
        }
        return open;
    }
    
    /**
     * Returns true if it has at least one Policy is Open
     * @return true if open
     */
    public static boolean isOpen(Asset asset) {
        List<Policy> _policies = asset.getPolicies();
        for(Policy policy : _policies)
            if (ODRLExtra.isOpen(policy))
                return true;
        return false;
    }
    
    /**
     * @deprecated
     * Returns true if 
     * @return true if the action gives access to the resource.
     */
    public static boolean hasPlay(Action action) {
        boolean containsPlay = false;
        if (action.uri.equals("http://creativecommons.org/ns#Reproduction")) 
            containsPlay = true;
        if (action.uri.equals("http://www.w3.org/ns/odrl/2/reproduce")) 
            containsPlay = true;
	if (action.uri.equals("http://www.w3.org/ns/odrl/2/display"))
            containsPlay=true;
	if (action.uri.equals("http://www.w3.org/ns/odrl/2/play"))
            containsPlay=true;
	if (action.uri.equals("http://www.w3.org/ns/odrl/2/present"))
            containsPlay=true;
        return containsPlay;
    }

    
    /**
     * @deprecated
     */
    public static String getPriceString(Policy policy) {
        String price = "";
        for (Rule r : policy.rules) {
            List<Action> actions = r.getActions();
            boolean hasPlay = false;
            for (Action action : actions) {
                if (hasPlay(action)) {
                    hasPlay = true;
                }
            }
            if (hasPlay == false) {
                continue;
            }
            List<Constraint> lc = r.getConstraints();
            for (Constraint constraint : lc) {
            }
        }
        return price;
    }    
    
    
    /**
     * @deprecated
     * Decides whether there is a play at least in a one of the rules
     */
    public static boolean isInOffer(Policy policy) {
        for (Rule r : policy.rules) {
            List<Action> actions = r.getActions();
            boolean hasPlay = false;
            for (Action action : actions) {
                if (hasPlay(action)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if it has at least one Policy which isInOffer
     * @return true if in offer
     */
    public static boolean isInOffer(Asset asset) {
        List<Policy> _policies = asset.getPolicies();
        for(Policy policy : _policies)
            if (isInOffer(policy))
                return true;
        return false;
    }

    
    /**
     * Returns
     * @deprecated
     * @return  true if there is at least one constraint that offers the good per rdf:Statement
     */
    protected static boolean isPerTriple(Policy p) {
        if (p.rules.size() > 0) {
            Rule r = p.rules.get(0);
            if (r.getConstraints().size() > 0) {
                List<Constraint> lc = r.getConstraints();
                if (lc.size() > 0) {
                    Constraint c = lc.get(0);
                }
            }
        }
        return false;
    }
    
}
