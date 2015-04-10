package oeg.rdf.commons;

/**
 *
 * @author Victor
 */
public class NTriple {
    public static String getObject(String ntriple) {
        ntriple = ntriple.trim();
        int i = ntriple.lastIndexOf(" ");   
        if (i==-1)
            return "";
        return ntriple.substring(i+2,ntriple.length()-1);
    }
    
}
