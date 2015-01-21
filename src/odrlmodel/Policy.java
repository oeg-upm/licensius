package odrlmodel;

//JAVA
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//APACHE COMMONS
import org.apache.commons.io.FilenameUtils;

/**
 * Policy represents an ODRL policy, supporting a reduced set of the features defined in the ODRL2.0 specification
 * @seeAlso For more information, check http://www.w3.org/community/odrl/two/model/#section-21
 * An abstract common ancestor to Permissions, Prohibitions and Duties.
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class Policy extends MetadataObject {

    public String fileName = "";
    public String legalCode ="";
    
    //A policy is made of one or more rules
    public List<Rule> rules = new ArrayList();
    
    public static final int POLICY_SET = 0; //DEFAULT
    public static final int POLICY_REQUEST = 1;
    public static final int POLICY_OFFER = 2;
    public static final int POLICY_CC = 3;
    
    private int type = POLICY_SET;

    public String getLegalcode()
    {
        return legalCode;
    }
    
    /**
     * Policy constructor with a random URI in the default namespace
     * A policy is by default a Set policy
     */
    public Policy() {
        uri = MetadataObject.DEFAULT_NAMESPACE + "policy/" + UUID.randomUUID().toString();
    }

    /**
     * Gets the type of the Policy
     * @return one of: Policy.POLICY_SET, Policy.POLICY_REQUEST, Policy.POLICY_OFFER
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type of the policy
     * @param _type, one of: Policy.POLICY_SET, Policy.POLICY_REQUEST, Policy.POLICY_OFFER
     */
    public void setType(int _type) {
        type = _type;
    }

    /**
     * Sets the type from a string
     * @param stype, one of: "Set", "Offer", "Request"
     */
    public void setType(String stype) {
        if (stype.equals("Set")) {
            setType(Policy.POLICY_SET);
        }
        if (stype.equals("Offer")) {
            setType(Policy.POLICY_OFFER);
        }
        if (stype.equals("Request")) {
            setType(Policy.POLICY_REQUEST);
        }
    }

    /**
     * Gets a string describing the type of policy
     */
    public String getTypeName() {
        if (type == Policy.POLICY_OFFER) {
            return "Offer";
        }
        if (type == Policy.POLICY_SET) {
            return "Set";
        }
        if (type == Policy.POLICY_REQUEST) {
            return "Request";
        }
        return "Set";
    }

    /**
     * Devuelve el label, y si no, la URI
     */
    @Override
    public String toString() {
        String label = getLabel("en");
        if (!label.isEmpty()) {
            return label;
        }
        return FilenameUtils.getBaseName(uri);
    }

    /**
     * Adds a rule to the policy
     */
    public void addRule(Rule r) {
        rules.add(r);
    }

    /**
     * Gets the list of rules this policy has
     * @return List of rules
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Sets the list of rules
     * @param _rules List of rules
     */
    public void setRules(List<Rule> _rules) {
        rules = _rules;
    }

    /**
     * Makes a human readable version of the license in HTML
     * @return HTML String
     */
    public String toHumanHTML(String lan) {
        String s = "";
        s += "<html>\n";
        s += "<head><style type=\"text/css\">";
        s += "html, body, div, span, applet, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre, a, abbr, acronym, address, big, cite, code, del, dfn, em, img, ins, kbd, q, s, samp, small, strike, strong, sub, sup, tt, var, b, u, i, center, dl, dt, dd, ol, ul, li, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td, article, aside, canvas, details, embed, figure, figcaption, footer, header, hgroup, menu, nav, output, ruby, section, summary, time, mark, audio, video { margin: 0;padding: 0;border: 0;font-size: 100%;font: inherit;vertical-align: baseline; }article, aside, details, figcaption, figure, footer, header, hgroup, menu, nav, section {display: block; }footer{margin-top: 100px;}body {margin-top: 100px;line-height: 1; }";
        s += "ol, ul {list-style: none; }blockquote, q {quotes: none; }blockquote:before, blockquote:after,q:before, q:after {content: '';content: none; }table {border-collapse: collapse;border-spacing: 0; }";
        s += "body {background: #fff;font: 14px/21px \"HelveticaNeue\", \"Helvetica Neue\", Helvetica, Arial, sans-serif;color: #444;-webkit-font-smoothing: antialiased; -webkit-text-size-adjust: 100%;}";
        s += "h1, h2, h3, h4, h5, h6 {color: #181818;font-family: \"Georgia\", \"Times New Roman\", serif;font-weight: normal; }";
        s += "h1 a, h2 a, h3 a, h4 a, h5 a, h6 a { font-weight: inherit; }h1 { font-size: 46px; line-height: 50px; margin-bottom: 14px;}h2 { font-size: 35px; line-height: 40px; margin-bottom: 10px; }h3 { font-size: 28px; line-height: 34px; margin-bottom: 8px; }h4 { font-size: 21px; line-height: 30px; margin-bottom: 4px; }h5 { font-size: 17px; line-height: 24px; }h6 { font-size: 14px; line-height: 21px; }.subheader { color: #777; }";
        s += "p { margin: 0 0 20px 0; }p img { margin: 0; }p.lead { font-size: 21px; line-height: 27px; color: #777;  }em { font-style: italic; }strong { font-weight: bold; color: #333; }small { font-size: 80%; }\n";
        s += ".container { position: relative; width: 960px; margin: 0 auto; padding: 0; }.container .column,.container .columns { float: left; display: inline; margin-left: 10px; margin-right: 10px; }.row { margin-bottom: 20px; }";
        s += ".container .sixteen.columns { width: 940px; }.container .offset-by-two{ padding-left: 120px; }";

        s += "</style></head>\n<body>\n";
        s += "<div class=\"container\"><div class=\"sixteen columns\">";
        s += "<h1> License: " + title + "</h1>";
        List<Rule> rules = getRules();
        for (Rule r : rules) {
            s += "<h3>Rule (" + r.getKindOfRuleString() + ")</h3>";
            s += "<h4>Rights</h4>\n";
            List<Action> actions = r.getActions();
            for (Action action : actions) {
                s += action + ", ";
            }
            s += "<h4>Constraints</h4>\n";
            List<Constraint> constraints = r.getConstraints();
            for (Constraint constraint : constraints) {
                s += constraint + ",";
            }
            s += "<h4>Target</h4>\n";
            String target = r.target;
            if (!target.isEmpty()) {
                s += "To be acted on " + target + "</br>";
            }
            String assignee = r.getAssignee();
            if (!assignee.isEmpty()) {
                s += "In favour of " + assignee + "</br>";
            }
            String assigner = r.getAssigner();
            if (!assigner.isEmpty()) {
                s += "Issued by " + assigner + "</br>";
            }

        }
        s += "</div></div>";
        s += "</body></html>\n";
        return s;
    }

    /**
     * Returns true if there is at least one constraint that offers the good per rdf:Statement
     */
    public boolean isPerTriple() {
        if (rules.size() > 0) {
            Rule r = rules.get(0);
            if (r.getConstraints().size() > 0) {
                List<Constraint> lc = r.getConstraints();
                if (lc.size() > 0) {
                    Constraint c = lc.get(0);
                    if (c.getClass().equals(ConstraintPay.class))
                    {
                    if (((ConstraintPay)c).isPerTriple()) {
                        return true;
                    }
                    }
                }
            }
        }
        return false;
    }

    //Si hay al menos alguna abierta
    public boolean isOpen() {
        boolean open = false;
        for (Rule r : rules) {
            List<Action> actions = r.getActions();
            boolean hasPlay = false;
            for (Action action : actions) {
                if (action.hasPlay()) {
                    hasPlay = true;
                }
            }
            if (hasPlay == false) {
                continue;
            }
            List<Constraint> lc = r.getConstraints();
            for (Constraint constraint : lc) {
                if (constraint.isOpen()) {
                    return true;
                }
            }
        }
        return open;
    }

    public String getPriceString() {
        String price = "";
        for (Rule r : rules) {
            List<Action> actions = r.getActions();
            boolean hasPlay = false;
            for (Action action : actions) {
                if (action.hasPlay()) {
                    hasPlay = true;
                }
            }
            if (hasPlay == false) {
                continue;
            }
            List<Constraint> lc = r.getConstraints();
            for (Constraint constraint : lc) {
                if (!constraint.isOpen()) {
                    ConstraintPay cp = (ConstraintPay)constraint;
                    price = cp.getPriceString();
                }
            }
        }
        return price;
    }

    /**
     * Decides whether there is a play at least in a one of the rules
     */
    public boolean isInOffer() {
        for (Rule r : rules) {
            List<Action> actions = r.getActions();
            boolean hasPlay = false;
            for (Action action : actions) {
                if (action.hasPlay()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasFirstTarget(String resource) {
        for (Rule r : rules) {
            if (r.target.equals(resource)) {
                return true;
            }
        }
        return false;
    }

    public void setTargetInAllRules(String targetName) {
        for (Rule r : rules) {
            r.target = targetName;
        }
    }
    public void setAssigneeInAllRules(String targetName) {
        for (Rule r : rules) {
            r.assignee = targetName;
        }
    }

    public String getFirstTarget() {
        for (Rule r : rules) {
            return r.target;
        }
        return "";
    }

    public String getFirstCurrency() {
        for (Rule r : rules) {
            List<Constraint> lc = r.getConstraints();
            for (Constraint c : lc) {
                if (c.getClass().equals(ConstraintPay.class)) {
                    ConstraintPay cp = (ConstraintPay) c;
                    if (cp.amount!=0)
                        return cp.currency;
                }
            }
        }
        return "";
    }    
    public double getFirstPrice() {
        for (Rule r : rules) {
            List<Constraint> lc = r.getConstraints();
            for (Constraint c : lc) {
                if (c.getClass().equals(ConstraintPay.class)) {
                    ConstraintPay cp = (ConstraintPay) c;
                    if (cp.amount != 0) {
                        return cp.amount;
                    }
                }
            }
        }
        return 0.0;
    }
}
