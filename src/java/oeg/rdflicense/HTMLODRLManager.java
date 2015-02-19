package oeg.rdflicense;

//GAGAWA https://code.google.com/p/gagawa/
import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.H1;
import com.hp.gagawa.java.elements.Head;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Title;
import java.util.List;

//ODRLMODEL
import odrlmodel.Action;
import odrlmodel.Constraint;
import odrlmodel.Policy;
import odrlmodel.Rule;

/**
 * @author Victor
 */
public class HTMLODRLManager {

    
    /**
     * Example of use of this class
     */
    public static void main(String[] args) {

        RDFLicenseDataset dataset = new RDFLicenseDataset();
        String rdflicenseuri = "http://purl.org/NET/rdflicense/cc-by3.0gr";
        RDFLicense license = dataset.getRDFLicense(rdflicenseuri);
        Policy policy = license.getPolicy();
        if (policy != null) {
            String html = HTMLODRLManager.htmlPolicy(policy, "en");
            System.out.println(html);
        }

    }

    /**
     * Makes a human readable version of the license in HTML
     * @return HTML String
     */
    public static String htmlPolicy(Policy p, String lan) {
        String s = "";
        Html html = new Html();
        Head head = new Head();
        Title title = new Title();
        title.appendChild(new Text("Policy"));
        head.appendChild(title);

        s += "<style type=\"text/css\">";
        s += "html, body, div, span, applet, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre, a, abbr, acronym, address, big, cite, code, del, dfn, em, img, ins, kbd, q, s, samp, small, strike, strong, sub, sup, tt, var, b, u, i, center, dl, dt, dd, ol, ul, li, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td, article, aside, canvas, details, embed, figure, figcaption, footer, header, hgroup, menu, nav, output, ruby, section, summary, time, mark, audio, video { margin: 0;padding: 0;border: 0;font-size: 100%;font: inherit;vertical-align: baseline; }article, aside, details, figcaption, figure, footer, header, hgroup, menu, nav, section {display: block; }footer{margin-top: 100px;}";
        s += "body {margin-top: 100px;line-height: 1; }";
        s += "ol, ul {list-style: none; }blockquote, q {quotes: none; }blockquote:before, blockquote:after,q:before, q:after {content: '';content: none; }table {border-collapse: collapse;border-spacing: 0; }";
        s += "body {background: #eee;font: 16px/21px \"HelveticaNeue\", \"Helvetica Neue\", Helvetica, Arial, sans-serif;color: #444;-webkit-font-smoothing: antialiased; -webkit-text-size-adjust: 100%;}";
        s += "h1, h2, h3, h4, h5, h6 {color: #181818;font-family: \"Georgia\", \"Times New Roman\", serif;font-weight: normal; }";
        s += "h1 a, h2 a, h3 a, h4 a, h5 a, h6 a { font-weight: inherit; }h1 { font-size: 46px; line-height: 50px; margin-bottom: 14px;}h2 { font-size: 35px; line-height: 40px; margin-bottom: 10px; }h3 { font-size: 28px; line-height: 34px; margin-bottom: 8px; }h4 { font-size: 21px; line-height: 30px; margin-bottom: 4px; }h5 { font-size: 17px; line-height: 24px; }h6 { font-size: 14px; line-height: 21px; }.subheader { color: #777; }";
        s += "p { margin: 0 0 20px 0; }p img { margin: 0; }p.lead { font-size: 21px; line-height: 27px; color: #777;  }em { font-style: italic; }strong { font-weight: bold; color: #333; }small { font-size: 80%; }\n";
        s += ".container { position: relative; width: 960px; margin: 0 auto; padding: 0; }.container .column,.container .columns { float: left; display: inline; margin-left: 10px; margin-right: 10px; }.row { margin-bottom: 20px; }";
        s += ".container .sixteen.columns { width: 940px; }.container .offset-by-two{ padding-left: 120px; }";
        s += "</style>\n";
        head.appendText(s);
        html.appendChild(head);
        Body body = new Body();
        s = "<div class=\"container\"><div class=\"sixteen columns\">";
        s += "<div style=\"border:2px solid; border-radius:25px; margin:10px; padding: 8px 8px 8px 8px;background:#AAAAAA;\">";


        s += "<h1> License: " + p.getLabel("en") + "</h1>";

        s += "<h4>Summary</h4>\n";
        s += p.getComment() + "\n";


        List<Rule> rules = p.getRules();
        for (Rule r : rules) {
            s += "<h3>Rule (" + ")</h3>";
            s += "<h4>Rights</h4>\n";
            List<Action> actions = r.getActions();
            int k = 0;
            for (Action action : actions) {
                s += action + ", ";
                k++;
                if (k != actions.size()) {
                    s += ",";
                }
            }
            s += "<h4>Constraints</h4>\n";
            List<Constraint> constraints = r.getConstraints();

            if (constraints.size() > 1) {
                s = s + "";
            }


            for (Constraint constraint : constraints) {
                s += constraint + "<br/>";
            }
            String target = r.target;
            if (!target.isEmpty()) {
                s += "<h4>Target</h4>\n";
                String targetaux = target;
                if (target.contains("xxxxx")) {
                    targetaux = targetaux.replace("xxxxx", " - ");
                }

                s += "To be acted on " + targetaux + "</br>";
            }
            if (r.getAssignee()!=null)
            {
                s += "<h4>Assignee</h4>\n";
                s += "In favour of " + r.getAssignee().getTitle() + "</br>";
            }
            if (r.getAssigner()!=null) {
                s += "<h4>Assigner</h4>\n";
                s += "Issued by " + r.getAssigner().getTitle() + "</br>";
            }
        }
        s += "<hr/><img src=\"/ldr/img/rdf24.png\"/><small>View <a type=\"text/turtle\" href=\"" + p.getURI() + ".ttl\">license in RDF</a></small>";
        s += "</div>";
        s += "<br/><!--TEMPLATEHERE1-->";

        s += "</div></div>\n";
        body.appendText(s);
        html.appendChild(body);
        s = html.write();
        return s;
    }
}
