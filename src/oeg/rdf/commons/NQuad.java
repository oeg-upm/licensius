package oeg.rdf.commons;

/**
 *
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
    
}
