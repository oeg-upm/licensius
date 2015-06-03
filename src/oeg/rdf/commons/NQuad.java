package oeg.rdf.commons;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.log4j.Logger;

/**
 * Class with static methods for handling NQuads efficiently
 * @author Victor
 */
public class NQuad {

    static final Logger logger = Logger.getLogger(NQuad.class);

    
    /**
     * Determines if, by looking at the first line, the file contains nquads. 
     * It does not exclude further errrors.
     */
    public static boolean isNQuad(String filename)
    {
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            if (br==null)
                return false;
            String nquad = br.readLine();
            if (nquad==null || nquad.isEmpty())
                return false;
            nquad = nquad.trim();
            String s = getSubject(nquad);
            if (s.isEmpty())
                return false;
            String o = getObject(nquad);
            if (o.isEmpty())
                return false;
        }catch(Exception e)
        {
            logger.warn(e.getMessage());
            return false;
        }
        return true;
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

    /**
     * Gets the subject in a NQuad
     * @param nquad String with a nquad. For example: 
     * <http://salonica.dia.fi.upm.es/geo/resource/ComunidadAut%C3%B3noma/Castilla-La%20Mancha> <http://salonica.dia.fi.upm.es/geo/ontology/formadoPor> <http://salonica.dia.fi.upm.es/geo/resource/Provincia/Albacete> <http://salonica.dia.fi.upm.es/geo/dataset/default> .
     * @return The subject: in the example before, http://salonica.dia.fi.upm.es/geo/resource/ComunidadAut%C3%B3noma/Castilla-La%20Mancha
     */
    public static String getSubject(String nquad) {
        nquad = nquad.trim();
        int i = nquad.indexOf("<");   
        int j = nquad.indexOf(">");
        if (i==-1 || j==-1)
            return "";
        return nquad.substring(i+1,j);
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
        int i0 = nquad.indexOf("<");            //inicio del sujeto
        int i1 = nquad.indexOf("<", i0+1);      //inicio de la propiedad
        int i2 = nquad.indexOf(">", i1+1);      //fin de la propiedad
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

    public static String getPredicate(String nquad) {
        nquad = nquad.trim();
        int i0 = nquad.indexOf("<");   
        int i1 = nquad.indexOf("<", i0+1);
        int i2 = nquad.indexOf(">", i1+1);
        return nquad.substring(i1+1,i2);
    }

    public static Model getStatement(String nquad) {
        String s= getSubject(nquad);
        String p= getPredicate(nquad);
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
