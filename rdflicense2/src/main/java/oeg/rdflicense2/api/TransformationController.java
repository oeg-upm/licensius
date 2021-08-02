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
import oeg.rdflicense2.transformers.TransformerXMLODRL;
import oeg.rdflicense2.transformers.TransformerXMLRDF;
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
* 
* cd C:\Users\vroddon\Desktop\xml_records\
for /r %%i in (*) do curl -X POST -H "Content-Type: text/xml; charset=utf-8" -d @%%i https://rdflicense.linkeddata.es/xml2rdf > %%i.ttl
 Example of invocation: 
* curl -X POST -H "Content-Type: text/xml; charset=utf-8" -d @myxmlfile.xml https://rdflicense.linkeddata.es/xml2rdf 
* 
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
        TransformationResponse tr = TransformerXMLRDF.transformXML(xml);
        return new ResponseEntity<>(tr, HttpStatus.OK);
    }
    @CrossOrigin
    @ApiOperation(value = "Transforms the licensing information of a Metashare resource in XML to an ODRL policy")
    @RequestMapping(value = "/xml2odrl", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity transformToOdrl(   
    @RequestBody String xml) {
        TransformationResponse tr = TransformerXMLODRL.transformXML2ODRL(xml);
        return new ResponseEntity<>(tr, HttpStatus.OK);
    }
    


}
