package oeg.rdflicense2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import oeg.rdflicense2.TransformationResponse;
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

/**
 * Makes transformations to RDF
 * @author vroddon
 */
@Controller
@Api(tags = "Transformation", value = "Transformation")
@ApiOperation(value = "Transforms a catalogue entry")
public class TransformationController {

    @CrossOrigin
    @ApiOperation(value = "Transforms the licensing information of a Metashare resource in XML to its flat RDF version")
    @RequestMapping(value = "/xml2rdf", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity transform(@RequestBody String xml) {
        TransformationResponse tr = transformXML(xml);
        return new ResponseEntity<>(tr, HttpStatus.OK);
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
                            if (!lang.isEmpty())
                                lang="@"+lang;
                            
//                           Resource r = ModelFactory.createDefaultModel().createResource("_:licence");
//                           Resource r = ModelFactorycreateDefaultModel().createResource("_:licence");
                            if (nodetext.startsWith("http"))
                                rdf += "_:license <" + pairs.get(clave)+"> <"+ nodetext +  "> .\n";
                            else
                                rdf += "_:license <" + pairs.get(clave)+"> \""+ nodetext +  "\""+lang+" .\n";
                        }
                    }
                    if (nodename.equals("ms:LicenceIdentifier"))
                    {
                        String propiedad = "http://w3id.org/meta-share/meta-share/LicenceIdentifier";
                        String identifier = getAttributeValue(n1, "ms:LicenceIdentifierScheme");
                        rdf += "_:license <" + propiedad +"> <"+ identifier +  "> .\n";
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
