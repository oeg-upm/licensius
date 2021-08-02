package oeg.rdflicense2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.StringReader;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import oeg.jodrlapi.helpers.RDFUtils;
import oeg.rdflicense2.TransformationResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Makes transformations to RDF
 * @author vroddon
 * 
 * @ApiParam(name="xml", value="Metashare XMl to be transformed",  example="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
"<ms:MetadataRecord xmlns:ms=\"http://w3id.org/meta-share/meta-share/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://w3id.org/meta-share/meta-share/ https://live.european-language-grid.eu/metadata-schema/ELG-SHARE.xsd\"><ms:metadataCreationDate>2020-02-27</ms:metadataCreationDate><ms:metadataLastDateUpdated>2020-10-21</ms:metadataLastDateUpdated><ms:metadataCurator><ms:actorType>Person</ms:actorType><ms:surname xml:lang=\"en\">admin</ms:surname><ms:givenName xml:lang=\"en\">elg</ms:givenName></ms:metadataCurator><ms:compliesWith>http://w3id.org/meta-share/meta-share/ELG-SHARE</ms:compliesWith><ms:metadataCreator><ms:actorType>Person</ms:actorType><ms:surname xml:lang=\"en\">admin</ms:surname><ms:givenName xml:lang=\"en\">elg</ms:givenName></ms:metadataCreator><ms:DescribedEntity><ms:LicenceTerms><ms:entityType>LicenceTerms</ms:entityType><ms:LicenceIdentifier ms:LicenceIdentifierScheme=\"http://w3id.org/meta-share/meta-share/SPDX\">Abstyles</ms:LicenceIdentifier><ms:licenceTermsName xml:lang=\"en\">Abstyles License</ms:licenceTermsName><ms:licenceTermsShortName xml:lang=\"en\">Abstyles</ms:licenceTermsShortName><ms:licenceTermsURL>https://fedoraproject.org/wiki/Licensing/Abstyles</ms:licenceTermsURL><ms:conditionOfUse>http://w3id.org/meta-share/meta-share/unspecified</ms:conditionOfUse></ms:LicenceTerms></ms:DescribedEntity></ms:MetadataRecord>")
, produces = "application/n-triples" * 
 */
@Controller
@Api(tags = "Transformation", value = "Transformation")
@ApiOperation(value = "Transforms a catalogue entry")
public class TransformationController {

    @CrossOrigin
    @ApiOperation(value = "Transforms the licensing information of a Metashare resource in XML to its flat RDF version")
    @RequestMapping(value = "/xml2rdf", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity transform(   
    @RequestBody String xml) {
        TransformationResponse tr = transformXML(xml);
        return new ResponseEntity<>(tr, HttpStatus.OK);
    }
    
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
    
    private static String getAttributeValue(Node n, String atext)
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
        if (!lang.isEmpty())
            lang="@"+lang;
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

                rdf += "_:license a <http://w3id.org/meta-share/meta-share/LicenceTerms> .\n";

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
                            String lang = getAttributeValue(n1, "xml:lang");
                            String triple0 = TransformationController.getTriple("_:license", pairs.get(clave), nodetext, lang);
                            String triple1 = getTriple2("_:license", pairs.get(clave), nodetext, lang);
                            rdf+=triple1;
                        }
                    }
                    if (nodename.equals("ms:LicenceIdentifier"))
                    {
                        String propiedad = "http://w3id.org/meta-share/meta-share/LicenceIdentifier";
                        String identifier = getAttributeValue(n1, "ms:LicenceIdentifierScheme");
                        if (identifier.equals("http://w3id.org/meta-share/meta-share/SPDX"))
                            rdf += "_:license <" + propiedad +"> <"+ nodetext +  "> .\n";
                    }
                            
                }
            }
            TransformationResponse tr = new TransformationResponse(true, rdf);
            return tr;
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            e.printStackTrace();
            TransformationResponse tr = new TransformationResponse(false, e.getMessage());
            return tr;
        }
    }
}
