package oeg.utils.rdf;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import oeg.utils.rdf.RDFUtils;

import org.apache.log4j.Logger;

/**
 * Class with static methods for handling NQuads efficiently
 * @author Victor
 */
public class NQuad {

    static final Logger logger = Logger.getLogger(NQuad.class);

    /**
     * Crea un NQuad a partir de s, p, o, g.
     * Depending on the object, it is treated as a resource or not: resources start all by "http".
     * Also, entities in the form _:x are handled as resources
     */
    public static String createNQuad(String s, String p, String o, String g)
    {
        String nquad="";
        
        if (s.startsWith("http"))
            nquad+="<" + s + "> ";
        else
            nquad+="" + s + " ";
        nquad+="<" + p + "> ";
        if (o.startsWith("http"))
            nquad+="<" + o + "> ";
        else if (o.startsWith("_:"))
            nquad+=  o + " ";
        else
            nquad+="\"" + o + "\" ";
        nquad+="<"+g+"> .";
        return nquad;
    }
    
    /**
     * Determines whether a line is a NQUAD or not.
     */
    public static boolean isNQuad(String line)
    {
        line = line.trim();
        try{
            String s = NQuad.getSubject(line);
            String p = NQuad.getProperty(line);
            String o = NQuad.getObject(line);
            String g = NQuad.getGraph(line);
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }
    
    /**
     * Determines if, by looking at the first line, the file contains nquads. 
     * It does not exclude further errrors.
     */
    public static boolean isNQuadFile(String filename)
    {
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            if (br==null)
                return false;
            String nquad = br.readLine();
            return isNQuad(nquad);
        }catch(Exception e)
        {
            logger.warn("Verifying if a file was a NQUAD file... " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the graph in a NQuad
     * @param nquad String with a nquad. For example: 
     * <http://salonica.dia.fi.upm.es/geo/resource/ComunidadAut%C3%B3noma/Castilla-La%20Mancha> <http://salonica.dia.fi.upm.es/geo/ontology/formadoPor> <http://salonica.dia.fi.upm.es/geo/resource/Provincia/Albacete> <http://salonica.dia.fi.upm.es/geo/dataset/default> .
     * @return Gets the graph of a NQUAD. In the example before, it would return "http://salonica.dia.fi.upm.es/geo/dataset/default".
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
    public static void main(String[] args) {
            String total = "<http://localhost:8080/ldc/data/rdflicense/GNU-LGPL3.0> <http://creativecommons.org/ns#legalcode> <http://www.gnu.org/licenses/lgpl-3.0.html> <http://localhost:8080/ldc/data/rdflicense/default> .\n" +
        "        <http://localhost:8080/ldc/data/rdflicense/GNU-LGPL3.0> <http://purl.org/dc/terms/hasVersion> \"3.0\" <http://localhost:8080/ldc/data/rdflicense/default> .\n" +
        "        _:B4cc0d24d20a5725a30f797b590950da4 <http://www.w3.org/ns/odrl/2/action> <http://www.w3.org/ns/odrl/2/commercialize> <http://localhost/datasets/default> .\n";
            
        try {
            InputStream is = new ByteArrayInputStream( total.getBytes( "UTF-8" ) );
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line="";
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                String s = NQuad.getSubject(line);
                String p = NQuad.getProperty(line);
                String o = NQuad.getObject(line);
                String g = NQuad.getGraph(line);
                System.out.println(s+" "+p+" "+o+" "+g);
            }
            
        } catch (Exception ex) {
        }

    }
    /**
     * Gets the subject in a NQuad
     * @param nquad String with a nquad. For example: 
     * <http://salonica.dia.fi.upm.es/geo/resource/ComunidadAut%C3%B3noma/Castilla-La%20Mancha> <http://salonica.dia.fi.upm.es/geo/ontology/formadoPor> <http://salonica.dia.fi.upm.es/geo/resource/Provincia/Albacete> <http://salonica.dia.fi.upm.es/geo/dataset/default> .
     * @return The subject: in the example before, http://salonica.dia.fi.upm.es/geo/resource/ComunidadAut%C3%B3noma/Castilla-La%20Mancha
     */
    public static String getSubject(String nquad) {
        nquad = nquad.trim();
        int ind = nquad.indexOf(" ");
        if (ind == -1)
            return "";
        if (nquad.startsWith("<"))
            return nquad.substring(1,ind-1);
        else
            return nquad.substring(0,ind);
        
/*        
        int i = nquad.indexOf("<");   
        int j = nquad.indexOf(">");
        if (i==-1 || j==-1)
            return "";
        return nquad.substring(i+1,j);
*/        
    }

    public static RDFNode getObjectRDFNode(String nquad)
    {
        Model m = ModelFactory.createDefaultModel();
        nquad = nquad.trim();
        int i0 = nquad.indexOf("<");   
        int i1 = nquad.indexOf("<", i0+1);
        int i2 = nquad.indexOf(">", i1+1);
        int i3 = nquad.lastIndexOf("<");   
        String o = nquad.substring(i2+2,i3-1);
        if (o.startsWith("<"))
        {
            Resource r = m.createResource(o.substring(1, o.length()-1));
            return r;
        }
        int index=o.indexOf("^^");
        if (index!=-1)
            o = o.substring(0, index);
        index = o.lastIndexOf("\"");
        if (index!=-1)
            o = o.substring(1, index);
        Literal l = m.createLiteral(o);
        return l;
        
    }


    /**
     * Gets the object in a NQuad
     * @param nquad String with a nquad. For example: 
     * <http://salonica.dia.fi.upm.es/geo/resource/ComunidadAut%C3%B3noma/Castilla-La%20Mancha> <http://salonica.dia.fi.upm.es/geo/ontology/formadoPor> <http://salonica.dia.fi.upm.es/geo/resource/Provincia/Albacete> <http://salonica.dia.fi.upm.es/geo/dataset/default> .
     * @return Gets the objectof a NQUAD. 
     */
    public static String getObject(String nquad) {
        nquad = nquad.trim();
        int i0 = nquad.indexOf(" ");            //inicio del sujeto
        int i1 = nquad.indexOf("<", i0);      //inicio de la propiedad
        int i2 = nquad.indexOf(">", i1);      //fin de la propiedad
        int i3 = nquad.lastIndexOf("<");        //inicio del grafo
        String o = nquad.substring(i2+2,i3-1);
        if (o.startsWith("<"))
            return o.substring(1, o.length()-1);
        int index=o.indexOf("^^");
        if (index!=-1)
            o = o.substring(0, index);
        index = o.lastIndexOf("\"");
        if (index!=-1)
            o = o.substring(1, index);
        return o;
    }

    public static String getProperty(String nquad) {
        nquad = nquad.trim();
        int i0 = nquad.indexOf(" ");   
        int i1 = nquad.indexOf("<", i0);
        int i2 = nquad.indexOf(">", i1);
        return nquad.substring(i1+1,i2);
    }
 
    public static Model getStatement(String nquad) {
        String s= getSubject(nquad);
        String p= getProperty(nquad);
        String o= getObject(nquad);
        String lan = NQuad.getObjectLangTag(nquad);
        
        Model model = ModelFactory.createDefaultModel();
        Resource js = model.createResource(s);
        Property jp = model.createProperty(p);
        if (RDFUtils.isURI(o))
        {
            Resource jo = model.createResource(o);
            js.addProperty(jp, jo);
        }
        else
        {
            Literal jo = null;
            if (lan.isEmpty())
                jo=model.createLiteral(o);
            else
                jo = model.createLiteral(o, lan);
            js.addProperty(jp, jo);
        }
        return model;
    }

    public static String getObjectLangTag(String line) {
        String lan="";
        try{
            int i0= line.lastIndexOf("\"");
            int i1= line.lastIndexOf("<");
            if (i0!=-1 && i1!=-1)
            {
                String lan2=line.substring(i0+1,i1-1);
                int i2 = lan2.indexOf("@");
                if (i2!=-1)
                {
                    lan = lan2.substring(1, lan2.length());
                }
            }
        }catch(Exception e)
        {
          return "";
        }
        return lan;
    }
    
}
