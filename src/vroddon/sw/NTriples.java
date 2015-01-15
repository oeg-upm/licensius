package vroddon.sw;

import org.apache.commons.exec.util.StringUtils;

/**
 *
 * @author Victor
 */
public class NTriples {

    
    /*

     "<http://data.open.ac.uk/openlearn/div_1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Document> <http://data.open.ac.uk/resource/openlearn/div_1> ."
    
     * 
     * */
    public static void main(String[] args) throws Exception {
//        String line ="<http://data.open.ac.uk/openlearn/div_1> <http://www.w3.org/2000/01/rdf-schema#label> \"Numbers: Getting to grips with division\"^^<http://www.w3.org/2001/XMLSchema#string> ";
        String line=" <http://zbw.eu/namespaces/zbw-extensions/> <http://purl.org/dc/elements/1.1/title> \"Extensions to SKOS and other standard vocabularies used by the German National Library of Economics (ZBW)\"@en <http://zbw.eu/namespaces/zbw-extensions/zbw-extensions.rdf> .";        
        String literal = getObjectFromNQuad(line);
        String graph = getGraphFromNQuad(line);
        System.out.println(graph);
        
        if (literal!=null)
        {
            String langtag=getLangTag(literal);
            if (langtag!=null)
                System.out.println(langtag);
        }
        
    }
    
    public static String getGraphFromNQuad(String line) {
        String res = null;
        int finsubject=line.indexOf(' ');
        if (finsubject==-1) 
            return null;
        int inipredicate=line.indexOf(' ', finsubject);   //a partir de inipredicate+1 empieza el predicado
        if (inipredicate==-1)
            return null;

        int iniobj=line.indexOf(' ', inipredicate+1);   //a partir de iniobj empieza el objeto
        if (iniobj==-1)
            return null;
        
        int i3=line.lastIndexOf(' ');   //a partir de i3 solo hay un espacio y un punto
        if (i3==-1)
            return null;

        int i2=line.lastIndexOf(' ', i3-1); //a partir de i2+1 está el grafo, incluyendo un espacio y un punto al final
        if (i2==-1)
            return null;

        String obj=line.substring(i2+1,i3);
        res=obj;
        return res;        
    }
    
    /**
     * Gets the literal in a NQuad, if any. 
     * The NQUuad must be of the form "<s> <p> <o> <g> ." where <o> may be a URI or a literal
     * It is critical the location spaces, which must be as in the pattern described above.
     * @param line A line with a NQuad
     * @return Null, if there is no literal in the line
     */
    public static String getObjectFromNQuad(String line) {
        String res = null;
        int finsubject=line.indexOf(' ');
        if (finsubject==-1) 
            return null;
        int inipredicate=line.indexOf(' ', finsubject);   //a partir de inipredicate+1 empieza el predicado
        if (inipredicate==-1)
            return null;

        int iniobj=line.indexOf(' ', inipredicate+1);   //a partir de iniobj empieza el objeto
        if (iniobj==-1)
            return null;
        
        int i3=line.lastIndexOf(' ');   //a partir de i3 solo hay un espacio y un punto
        if (i3==-1)
            return null;

        int i2=line.lastIndexOf(' ', i3-1); //a partir de i2+1 está el grafo, incluyendo un espacio y un punto al final
        if (i2==-1)
            return null;

        String obj=line.substring(iniobj+1,i2);
        res=obj;
        return res;
    }    
    
    
    /**
     * Gets the literal in a NQuad, if any. 
     * The NQUuad must be of the form "<s> <p> <o> <g> ." where <o> may be a URI or a literal
     * It is critical the location spaces, which must be as in the pattern described above.
     * @param line A line with a NQuad
     * @return Null, if there is no literal in the line
     */
    public static String getObjectFromNTriple(String line) {
        String res = null;
        int finsubject=line.indexOf(' ');
        if (finsubject==-1) 
            return null;
        int inipredicate=line.indexOf(' ', finsubject);   //a partir de inipredicate+1 empieza el predicado
        if (inipredicate==-1)
            return null;

        int iniobj=line.indexOf(' ', inipredicate+1);   //a partir de iniobj empieza el objeto
        if (iniobj==-1)
            return null;
        
        int i3=line.lastIndexOf(' ');   //a partir de i3 solo hay un espacio y un punto
        if (i3==-1)
            return null;

/*        int i2=line.lastIndexOf(' ', i3-1); //a partir de i2+1 está el grafo, incluyendo un espacio y un punto al final
        if (i2==-1)
            return null;*/

        String obj=line.substring(iniobj+1,i3);
        res=obj;
        return res;
    }
    
    /**
     * Devuelve el lang tag (o nulo)
     */
    public static String getLangTag(String literal)
    {
        int index = literal.lastIndexOf('@');
        if (index==-1)
            return null;
        int index2 = literal.lastIndexOf("\"");
        if (index2>index)
            return null;
        return literal.substring(index+1);
    }

    public static boolean isLiteral(String object) {
        return !(object.startsWith("<"));
    }

    public static String getPayLevelDomain(String grafo) {
        String pld="";
        if (grafo.isEmpty())
            return "";

        int i=grafo.indexOf('/',8);
        pld=grafo.substring(8,i);
        
        return pld;
    }

    
    
}
