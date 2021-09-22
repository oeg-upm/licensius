package oeg.rdflicense2.transformers;

import java.io.StringReader;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import oeg.jodrlapi.helpers.RDFUtils;
import oeg.rdflicense2.TransformationResponse;
import oeg.rdflicense2.api.TransformationController;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author vroddon
 */
public class TransformerXMLRDF {
    /**
     * Creates a triple in ntriples format. 
     * If the object starts with http, it will be stroed as a resource <uri> <uri> <uri>
     */
    private static String getTriple(String s, String p, String o, String lang)
    {
        Model model = ModelFactory.createDefaultModel();        
        String nt ="";
        Resource rs = ModelFactory.createDefaultModel().createResource(s);
        Property rp = ModelFactory.createDefaultModel().createProperty(p);
        RDFNode ro = null; //ModelFactory.createDefaultModel().createProperty(p);
        if (o.startsWith("http"))
        {   
            model.add(rs, rp, ModelFactory.createDefaultModel().createResource(o));
        }
        else
        {   if (lang.isEmpty())
                model.add(rs, rp, ModelFactory.createDefaultModel().createLiteral(o));
            else
                model.add(rs, rp, ModelFactory.createDefaultModel().createLiteral(o, lang));
        }
        nt = RDFUtils.toRDF(model, "TURTLE");
        return nt;
    }
    
    private static String getXMLAttributeValue(Node n, String atext)
    {
            NamedNodeMap attributes = n.getAttributes();
            for(int k=0;k<attributes.getLength();k++)
            {
                String atributo = attributes.item(k).getNodeName();
                if (atributo.equals(atext))
                {
                    return attributes.item(k).getNodeValue();
                }
            }        
            return "";
    }
    private static String getTriple2(String s, String p, String o, String lang)
    {
        String rdf="";
        if (!lang.isEmpty())
            lang="@"+lang;                            
        
        if (s.startsWith("http"))
            s = "<"+s+">";
        
        if (o.startsWith("http"))
            rdf += s +" <" + p+"> <"+ o +  "> .\n";
        else
        {
            o = Normalizer.normalize(o, Normalizer.Form.NFKC);
            rdf += s + " <" + p+"> \""+ o +  "\""+lang+" .\n";
        }
        return rdf;
    }

    public static TransformationResponse transformXML(String xml) {
        
        Map<String, String> pairs = new HashMap();
        pairs.put("ms:conditionOfUse", "http://w3id.org/meta-share/meta-share/conditionOfUse");
        pairs.put("ms:licenceCategory", "http://w3id.org/meta-share/meta-share/licenceCategory");
        pairs.put("ms:licenceTermsURL", "http://w3id.org/meta-share/meta-share/licenceTermsURL");
        pairs.put("ms:licenceTermsName", "http://w3id.org/meta-share/meta-share/licenceTermsName");
        pairs.put("ms:licenceTermsShortName", "http://w3id.org/meta-share/meta-share/licenceTermsShortName");
        pairs.put("ms:licenceTermsAlternativeName", "http://w3id.org/meta-share/meta-share/licenceTermsAlternativeName");
   
        
        
//???   ms:LicenceIdentifier --> get the attribute LicenceIdentifierScheme and check it is http://w3id.org/meta-share/meta-share/SPDX
//
                
        try {
            String rdf = "";
            System.out.println("Vamos a transformar!");
            InputSource is = new InputSource(new StringReader(xml));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            
            //for each ms:LicenceTerms we create a http://w3id.org/meta-share/meta-share/LicenceTerms
            NodeList nl0 = doc.getElementsByTagName("ms:LicenceTerms");
            int cuantos = nl0.getLength();
            for(int i=0;i<nl0.getLength();i++)
            {
                Node n0 = nl0.item(i);
                NodeList nl1 = n0.getChildNodes();

                rdf += "_:license <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://w3id.org/meta-share/meta-share/LicenceTerms> .\n";

                for(int j=0;j<nl1.getLength();j++)
                {
                    Node n1 = nl1.item(j);
/*                    System.out.println(n1.toString());
                    System.out.println(n1.getNodeName());
                    System.out.println(n1.getTextContent());*/
                    String nodename = n1.getNodeName();
                    String nodetext = n1.getTextContent();
                    for(String clave : pairs.keySet())
                    {
                        if (clave.equals(nodename))
                        {
                            String lang = getXMLAttributeValue(n1, "xml:lang");
//                            String triple0 = getTriple("_:license", pairs.get(clave), nodetext, lang);  //this is working too...
                            String triple1 = getTriple2("_:license", pairs.get(clave), nodetext, lang);
                            rdf+=triple1;
                            
                            if (nodename.equals("ms:licenceTermsName"))
                            {
                                triple1 = getTriple2("_:license", "http://www.w3.org/2000/01/rdf-schema#label", nodetext, lang);
                                rdf+=triple1;
                            }
                        }
                    }
                    if (nodename.equals("ms:LicenceIdentifier"))
                    {
                        String propiedad = "http://w3id.org/meta-share/meta-share/LicenceIdentifier";
                        String identifier = getXMLAttributeValue(n1, "ms:LicenceIdentifierScheme");
                        if (identifier.equals("http://w3id.org/meta-share/meta-share/SPDX"))
                        {
//                            rdf += "_:license <" + propiedad +"> <"+ nodetext +  "> .\n";
                            String triple1 = getTriple2("_:license", propiedad, nodetext, "");                            
                            rdf+=triple1;                            
                            triple1 = getTriple2("_:license", "http://www.w3.org/2000/01/rdf-schema#seeAlso", "https://spdx.org/licenses/"+nodetext, "");
                            rdf+=triple1;
                            
                        }
                    }
                            
                }
            }
            rdf = rdf.replace("@@", "@");
         //   rdf = rdf.replace("\n", "\\n");       //this was annoying to many, apparently
            TransformationResponse tr = new TransformationResponse(true, rdf);
            if (nl0.getLength()==0)
                tr = new TransformationResponse(false, "No ms:LicenceTerms was found");
            return tr;
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            e.printStackTrace();
            TransformationResponse tr = new TransformationResponse(false, e.getMessage());
            return tr;
        }
    }    
    
}
