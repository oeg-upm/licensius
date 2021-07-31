package oeg.rdflicense2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author vroddon
 */
@Controller
@Api(tags = "Validation", value = "Validation")
@ApiOperation(value = "Validates a catalogue entry")
public class ValidationController {

    @CrossOrigin
    @ApiOperation(value = "Validates an input schema")
    @RequestMapping(value = "/validate",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity validate(@RequestBody String xml) {
        boolean b = validateXML(xml);
        return new ResponseEntity<>( b+"" ,HttpStatus.OK);
        
        
    }

    public static boolean validateXML(String xml) {
        try {
            System.out.println("Vamos a validar!");
            InputSource is = new InputSource(new StringReader(xml));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
