package oeg.rdflicense2;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * https://www.journaldev.com/895/how-to-validate-xml-against-xsd-in-java
 */
public class XMLValidator {

    public static void main(String[] args) {

        /*      System.out.println("EmployeeRequest.xml validates against Employee.xsd? "+validateXMLSchema("Employee.xsd", "EmployeeRequest.xml"));
      System.out.println("EmployeeResponse.xml validates against Employee.xsd? "+validateXMLSchema("Employee.xsd", "EmployeeResponse.xml"));
      System.out.println("employee.xml validates against Employee.xsd? "+validateXMLSchema("Employee.xsd", "employee.xml"));
         */
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
"<ms:MetadataRecord xmlns:ms=\"http://w3id.org/meta-share/meta-share/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://w3id.org/meta-share/meta-share/ https://live.european-language-grid.eu/metadata-schema/ELG-SHARE.xsd\"><ms:metadataCreationDate>2020-02-27</ms:metadataCreationDate><ms:metadataLastDateUpdated>2020-10-21</ms:metadataLastDateUpdated><ms:metadataCurator><ms:actorType>Person</ms:actorType><ms:surname xml:lang=\"en\">admin</ms:surname><ms:givenName xml:lang=\"en\">elg</ms:givenName></ms:metadataCurator><ms:compliesWith>http://w3id.org/meta-share/meta-share/ELG-SHARE</ms:compliesWith><ms:metadataCreator><ms:actorType>Person</ms:actorType><ms:surname xml:lang=\"en\">admin</ms:surname><ms:givenName xml:lang=\"en\">elg</ms:givenName></ms:metadataCreator><ms:DescribedEntity><ms:LicenceTerms><ms:entityType>LicenceTerms</ms:entityType><ms:LicenceIdentifier ms:LicenceIdentifierScheme=\"http://w3id.org/meta-share/meta-share/SPDX\">Abstyles</ms:LicenceIdentifier><ms:licenceTermsName xml:lang=\"en\">Abstyles License</ms:licenceTermsName><ms:licenceTermsShortName xml:lang=\"en\">Abstyles</ms:licenceTermsShortName><ms:licenceTermsURL>https://fedoraproject.org/wiki/Licensing/Abstyles</ms:licenceTermsURL><ms:conditionOfUse>http://w3id.org/meta-share/meta-share/unspecified</ms:conditionOfUse></ms:LicenceTerms></ms:DescribedEntity></ms:MetadataRecord>";

        boolean v = validateXMLSchema2("ELG-SHARE.xsd", xml);
        System.out.println("EmployeeResponse.xml validates against ELG-share? " + v);

    }

    public static boolean validateXMLSchema2(String xsdPath, String xml) {

        try {
            SchemaFactory factory
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean validateXMLSchema(String xsdPath, String xmlPath) {

        try {
            SchemaFactory factory
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }
}
