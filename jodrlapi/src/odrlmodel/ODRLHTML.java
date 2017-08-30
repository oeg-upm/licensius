package odrlmodel;

import java.util.List;

/**
 * Helper class to produce a possible HTML output from ODRL
 * @author Victor
 */
public class ODRLHTML {

    /**
     * Makes a human readable version of the license in HTML.
     * @param policy ODRL2.0 Policy
     * @param lan Language
     * @return HTML String
     */
    public static String toHumanHTML(Policy policy, String lan) {
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
        s += "<h1> License: " + policy.getTitle() + "</h1>";
        List<Rule> rules = policy.getRules();
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
            Party assignee = r.getAssignee();
            if (assignee!=null) {
                s += "In favour of " + assignee + "</br>";
            }
            Party passigner = r.getAssigner();
            if (passigner!=null) {
                s += "Issued by " + passigner.toString() + "</br>";
            }

        }
        s += "</div></div>";
        s += "</body></html>\n";
        return s;
    }

    
}
