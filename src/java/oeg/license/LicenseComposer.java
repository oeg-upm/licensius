package oeg.license;

/**
 *
 * @author Victor
 */
public class LicenseComposer {

    /**
     * This method 
     */
    public static String compose(String lic1, String lic2) {
        String s = "";

        s+="The result of integrating in resource licensed as " + lic1 +" and another one license as " + lic2 + " is :<strong>";
        
        String tmp = "Unknown";

        if (lic1.equals("CC0")) {
            tmp = lic2;
        } else if (lic2.equals("CC0")) {
            tmp = lic1;
        } else {

            if (lic1.equals("CC-BY") && lic2.equals("CC-BY")) {
                tmp = "CC-BY";
            }
            if (lic1.equals("CC-BY") && lic2.equals("CC-BY-SA")) {
                tmp = "CC-BY-SA";
            }
            if (lic1.equals("CC-BY") && lic2.equals("CC-BY-NC")) {
                tmp = "CC-BY-NC";
            }
            if (lic1.equals("CC-BY") && lic2.equals("CC-BY-ND")) {
                tmp = "CC-BY-ND";
            }
            if (lic1.equals("CC-BY") && lic2.equals("CC-BY-NC-ND")) {
                tmp = "CC-BY-NC-ND";
            }
            if (lic1.equals("CC-BY") && lic2.equals("CC-BY-NC-SA")) {
                tmp = "CC-BY-NC-SA";
            }

            if (lic1.equals("CC-BY-SA") && lic2.equals("CC-BY")) {
                tmp = "CC-BY-SA";
            }
            if (lic1.equals("CC-BY-SA") && lic2.equals("CC-BY-SA")) {
                tmp = "CC-BY-SA";
            }
            if (lic1.equals("CC-BY-SA") && lic2.equals("CC-BY-NC")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-SA") && lic2.equals("CC-BY-ND")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-SA") && lic2.equals("CC-BY-NC-ND")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-SA") && lic2.equals("CC-BY-NC-SA")) {
                tmp = "Not compatible";
            }

            if (lic1.equals("CC-BY-NC") && lic2.equals("CC-BY")) {
                tmp = "CC-BY-NC";
            }
            if (lic1.equals("CC-BY-NC") && lic2.equals("CC-BY-SA")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-NC") && lic2.equals("CC-BY-NC")) {
                tmp = "CC-BY-NC";
            }
            if (lic1.equals("CC-BY-NC") && lic2.equals("CC-BY-ND")) {
                tmp = "CC-BY-NC-ND";
            }
            if (lic1.equals("CC-BY-NC") && lic2.equals("CC-BY-NC-ND")) {
                tmp = "CC-BY-NC-ND";
            }
            if (lic1.equals("CC-BY-NC") && lic2.equals("CC-BY-NC-SA")) {
                tmp = "CC-BY-NC-SA";
            }

            if (lic1.equals("CC-BY-ND") && lic2.equals("CC-BY")) {
                tmp = "CC-BY-ND";
            }
            if (lic1.equals("CC-BY-ND") && lic2.equals("CC-BY-SA")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-ND") && lic2.equals("CC-BY-NC")) {
                tmp = "CC-BY-NC-ND";
            }
            if (lic1.equals("CC-BY-ND") && lic2.equals("CC-BY-ND")) {
                tmp = "CC-BY-ND";
            }
            if (lic1.equals("CC-BY-ND") && lic2.equals("CC-BY-NC-ND")) {
                tmp = "CC-BY-NC-ND";
            }
            if (lic1.equals("CC-BY-ND") && lic2.equals("CC-BY-NC-SA")) {
                tmp = "Not compatible";
            }

            if (lic1.equals("CC-BY-NC-ND") && lic2.equals("CC-BY")) {
                tmp = "CC-BY-NC-ND";
            }
            if (lic1.equals("CC-BY-NC-ND") && lic2.equals("CC-BY-SA")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-NC-ND") && lic2.equals("CC-BY-NC")) {
                tmp = "CC-BY-NC-ND";
            }
            if (lic1.equals("CC-BY-NC-ND") && lic2.equals("CC-BY-ND")) {
                tmp = "CC-BY-NC-ND";
            }
            if (lic1.equals("CC-BY-NC-ND") && lic2.equals("CC-BY-NC-ND")) {
                tmp = "CC-BY-NC-ND";
            }
            if (lic1.equals("CC-BY-NC-ND") && lic2.equals("CC-BY-NC-SA")) {
                tmp = "Not compatible";
            }

            if (lic1.equals("CC-BY-NC-SA") && lic2.equals("CC-BY")) {
                tmp = "CC-BY-NC-SA";
            }
            if (lic1.equals("CC-BY-NC-SA") && lic2.equals("CC-BY-SA")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-NC-SA") && lic2.equals("CC-BY-NC")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-NC-SA") && lic2.equals("CC-BY-ND")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-NC-SA") && lic2.equals("CC-BY-NC-ND")) {
                tmp = "Not compatible";
            }
            if (lic1.equals("CC-BY-NC-SA") && lic2.equals("CC-BY-NC-SA")) {
                tmp = "CC-BY-NC-SA";
            }
        }
        s += tmp + "</strong><br/>";


        s += "If the sources are from different authors, you will have to give attribution to both of them<br/>";

        return s;
    }
}
