package oeg.rdflicense2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import oeg.rdflicense2.TransformationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
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

    public static TransformationResponse transformXML(String xml) {
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
                    System.out.println(n1.toString());
                    System.out.println(n1.getNodeName());
                    System.out.println(n1.getTextContent());
                    if (n1.getNodeName().equals("ms:licenceTermsName"))
                    {
                        rdf += "_:license <http://purl.org/dc/terms/title> \""+ n1.getTextContent() +  "\" .\n";
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
