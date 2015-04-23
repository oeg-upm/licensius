package oeg.rdf.commons;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import odrlmodel.rdf.RDFUtils;

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

    static RDFNode getObjectRDFNode(String nquad)
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


    static String getObject(String nquad) {
        nquad = nquad.trim();
        int i0 = nquad.indexOf("<");   
        int i1 = nquad.indexOf("<", i0+1);
        int i2 = nquad.indexOf(">", i1+1);
        int i3 = nquad.lastIndexOf("<");   
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

    static String getPredicate(String nquad) {
        nquad = nquad.trim();
        int i0 = nquad.indexOf("<");   
        int i1 = nquad.indexOf("<", i0+1);
        int i2 = nquad.indexOf(">", i1+1);
        return nquad.substring(i1+1,i2);
    }

    static Model getStatement(String nquad) {
        String s= getSubject(nquad);
        String p= getPredicate(nquad);
        String o= getObject(nquad);
        
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
            int index = o.lastIndexOf("\"");
            o = o.substring(1,index);
            Literal jo = model.createLiteral(o);
            js.addProperty(jp, jo);
        }
        return model;
    }
    
}
