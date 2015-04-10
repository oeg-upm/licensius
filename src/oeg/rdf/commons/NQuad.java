package oeg.rdf.commons;

/**
 * Class representing a NQuad. 
 * It is very fast.
 * @author Victor
 */
public class NQuad {

    /**
     * Gets the graph of a NQUAD
     */
    public static String getGraph(String nquad)
    {
        nquad = nquad.trim();
        int i = nquad.lastIndexOf(" <");   
        int j = nquad.lastIndexOf(">");
        if (i==-1)
            return "";
        return nquad.substring(i+2,j);
    }

    public static String getSubject(String nquad) {
        nquad = nquad.trim();
        int i = nquad.indexOf("<");   
        int j = nquad.indexOf(">");
        if (i==-1 || j==-1)
            return "";
        return nquad.substring(i+1,j);
    }

    static String getObject(String nquad) {
        nquad = nquad.trim();
        int i0 = nquad.indexOf("<");   
        int i1 = nquad.indexOf("<", i0+1);
        int i2 = nquad.indexOf(">", i1+1);
        int i3 = nquad.lastIndexOf("<");   
        String o = nquad.substring(i2+2,i3-1);
        if (o.startsWith("<"))
            return o.substring(1, o.length()-1);
        return o;
    }

    static String getPredicate(String nquad) {
        nquad = nquad.trim();
        int i0 = nquad.indexOf("<");   
        int i1 = nquad.indexOf("<", i0+1);
        int i2 = nquad.indexOf(">", i1+1);
        return nquad.substring(i1+1,i2);
    }
    
}
