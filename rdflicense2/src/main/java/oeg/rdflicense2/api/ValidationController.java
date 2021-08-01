package oeg.rdflicense2.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import oeg.rdflicense2.ValidationResponse;
import oeg.rdflicense2.XMLValidator;
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
        ValidationResponse vr = validateXML(xml);
        return new ResponseEntity<>( vr ,HttpStatus.OK);
    }

    public static ValidationResponse validateXML(String xml) {
        ValidationResponse vr = new ValidationResponse(true,"");
        try {
            System.out.println("Vamos a validar!");
            InputSource is = new InputSource(new StringReader(xml));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
            vr = new ValidationResponse(false,"XML is not well formed");
            return vr;
        }
        boolean v = XMLValidator.validateXMLSchema2("ELG-SHARE.xsd", xml);
        if (v==false)
        {
            vr = new ValidationResponse(false,"XML is well formed, but not conformant to ELG-SHARE.xsd");
            return vr;
            
        }
        
        return vr;
    }

}
