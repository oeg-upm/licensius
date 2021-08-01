package oeg.rdflicense2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
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
        String str = transformXML(xml);
        return new ResponseEntity<>(str, HttpStatus.OK);
    }

    public static String transformXML(String xml) {
        try {
            System.out.println("Vamos a transformar!");
            InputSource is = new InputSource(new StringReader(xml));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
