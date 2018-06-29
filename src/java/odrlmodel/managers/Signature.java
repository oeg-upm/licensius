package odrlmodel.managers;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.security.*;
//import java.security.cert.X509Certificate;
import java.util.*;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import ldc.LdcConfig;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dom.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.*;
import javax.xml.crypto.test.KeySelectors.KeyValueKeySelector;
import javax.xml.crypto.test.dsig.X509KeySelector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;

import org.w3c.dom.*;

/**
 * Sign XML file.
 * Mostly from http://www.xinotes.net/notes/note/751/
 * http://www.adictosaltrabajo.com/tutoriales/tutoriales.php?pagina=xmlSignature
 */
public class Signature {

    private static final String KEY_STORE_TYPE = "JKS";
    private static final String KEY_STORE_NAME = "myKeyStore.jks";
    private static final String KEY_STORE_PASS = "abc12345";
    private static final String PRIVATE_KEY_PASS = "abc1234";
    private static final String KEY_ALIAS = "victor";
    private static final String PATH = ".";
    private static final String ID = "acct";

    private static enum SignatureType {
        SIGN_BY_ID,
        SIGN_BY_PATH,
        SIGN_WHOLE_DOCUMENT
    };

    public static void main(String args[]) {
        try {
            boolean ok = false;
            Signature.firmar("afirmar.xml", "afirmar2.xml", -1);
            ok = verificarFirma("firmado2.xml");
            System.out.println();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Signas an XML string
     * @param str XML fragment as a String
     */
    public static String firmar(String str, int tipo) throws Exception {

        SignatureType sigType = SignatureType.SIGN_WHOLE_DOCUMENT;
        if (tipo == 0) //(by id)
            sigType = SignatureType.SIGN_BY_ID;
         else if (tipo == 1) 
            sigType = SignatureType.SIGN_BY_PATH;



        // Instantiate the document to be signed
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        
        InputStream is = new ByteArrayInputStream(str.getBytes());
        Document doc = dbFactory.newDocumentBuilder().parse(is);
        
       
                
        // prepare signature factory
        String providerName = System.getProperty(
                "jsr105Provider",
                "org.jcp.xml.dsig.internal.dom.XMLDSigRI");

        final XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance(
                "DOM",
                (Provider) Class.forName(providerName).newInstance());

        Node nodeToSign = null;
        Node sigParent = null;
        String referenceURI = null;
        XPathExpression expr = null;
        NodeList nodes;
        List transforms = null;

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        switch (sigType) {
            case SIGN_BY_ID:
                expr = xpath.compile(
                        String.format("//*[@id='%s']", ID));
                nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                if (nodes.getLength() == 0) {
                    System.out.println("Can't find node with id: " + ID);
                    return "";
                }

                nodeToSign = nodes.item(0);
                sigParent = nodeToSign.getParentNode();
                referenceURI = "#" + ID;
                /* 
                 * This is not needed since the signature is alongside the signed element, not enclosed in it.
                transforms = Collections.singletonList(
                sigFactory.newTransform(
                Transform.ENVELOPED, 
                (TransformParameterSpec) null
                )
                );
                 */
                break;
            case SIGN_BY_PATH:
                // Find the node to be signed by PATH
                expr = xpath.compile(PATH);
                nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                if (nodes.getLength() < 1) {
                    System.out.println("Invalid document, can't find node by PATH: " + PATH);
                    return "";
                }

                nodeToSign = nodes.item(0);
                sigParent = nodeToSign.getParentNode();
                referenceURI = ""; // Empty string means whole document
                transforms = new ArrayList<Transform>() {

                    {
                        add(sigFactory.newTransform(
                                Transform.XPATH,
                                new XPathFilterParameterSpec(PATH)));
                        add(sigFactory.newTransform(
                                Transform.ENVELOPED,
                                (TransformParameterSpec) null));
                    }
                };

                break;
            default:
                sigParent = doc.getDocumentElement();
                referenceURI = ""; // Empty string means whole document
                transforms = Collections.singletonList(
                        sigFactory.newTransform(
                        Transform.ENVELOPED,
                        (TransformParameterSpec) null));
                break;
        }

        // Retrieve signing key
        String keystore = LdcConfig.get("keystore", "myKeyStore.jks");

        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(keystore),KEY_STORE_PASS.toCharArray());

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(
                KEY_ALIAS,
                PRIVATE_KEY_PASS.toCharArray());

        java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) keyStore.getCertificate(KEY_ALIAS);
        PublicKey publicKey = cert.getPublicKey();

        // Create a Reference to the enveloped document
        Reference ref = sigFactory.newReference(
                referenceURI,
                sigFactory.newDigestMethod(DigestMethod.SHA1, null),
                transforms,
                null,
                null);

        // Create the SignedInfo
        SignedInfo signedInfo = sigFactory.newSignedInfo(
                sigFactory.newCanonicalizationMethod(
                CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                (C14NMethodParameterSpec) null),
                sigFactory.newSignatureMethod(
                SignatureMethod.RSA_SHA1,
                null),
                Collections.singletonList(ref));

        // Create a KeyValue containing the RSA PublicKey 
        KeyInfoFactory keyInfoFactory = sigFactory.getKeyInfoFactory();
        KeyValue keyValue = keyInfoFactory.newKeyValue(publicKey);

        // Create a KeyInfo and add the KeyValue to it
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(keyValue));

        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element
        DOMSignContext dsc = new DOMSignContext(
                privateKey,
                sigParent);

        // Create the XMLSignature (but don't sign it yet)
        XMLSignature signature = sigFactory.newXMLSignature(signedInfo, keyInfo);

        // Marshal, generate (and sign) the enveloped signature
        signature.sign(dsc);

        // output the resulting document
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        StringWriter sw = new StringWriter();
//        OutputStream os = new FileOutputStream(outputFile);
        trans.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
        
    }    
    public static void firmar(String inputFile, String outputFile, int tipo) throws Exception {

        SignatureType sigType = SignatureType.SIGN_WHOLE_DOCUMENT;
        if (tipo == 0) //(by id)
        {
            sigType = SignatureType.SIGN_BY_ID;
        } else if (tipo == 1) {
            sigType = SignatureType.SIGN_BY_PATH;
        }



        // Instantiate the document to be signed
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        Document doc = dbFactory.newDocumentBuilder().parse(new FileInputStream(inputFile));

        // prepare signature factory
        String providerName = System.getProperty(
                "jsr105Provider",
                "org.jcp.xml.dsig.internal.dom.XMLDSigRI");

        final XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance(
                "DOM",
                (Provider) Class.forName(providerName).newInstance());

        Node nodeToSign = null;
        Node sigParent = null;
        String referenceURI = null;
        XPathExpression expr = null;
        NodeList nodes;
        List transforms = null;

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        switch (sigType) {
            case SIGN_BY_ID:
                expr = xpath.compile(
                        String.format("//*[@id='%s']", ID));
                nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                if (nodes.getLength() == 0) {
                    System.out.println("Can't find node with id: " + ID);
                    return;
                }

                nodeToSign = nodes.item(0);
                sigParent = nodeToSign.getParentNode();
                referenceURI = "#" + ID;
                /* 
                 * This is not needed since the signature is alongside the signed element, not enclosed in it.
                transforms = Collections.singletonList(
                sigFactory.newTransform(
                Transform.ENVELOPED, 
                (TransformParameterSpec) null
                )
                );
                 */
                break;
            case SIGN_BY_PATH:
                // Find the node to be signed by PATH
                expr = xpath.compile(PATH);
                nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                if (nodes.getLength() < 1) {
                    System.out.println("Invalid document, can't find node by PATH: " + PATH);
                    return;
                }

                nodeToSign = nodes.item(0);
                sigParent = nodeToSign.getParentNode();
                referenceURI = ""; // Empty string means whole document
                transforms = new ArrayList<Transform>() {

                    {
                        add(sigFactory.newTransform(
                                Transform.XPATH,
                                new XPathFilterParameterSpec(PATH)));
                        add(sigFactory.newTransform(
                                Transform.ENVELOPED,
                                (TransformParameterSpec) null));
                    }
                };

                break;
            default:
                sigParent = doc.getDocumentElement();
                referenceURI = ""; // Empty string means whole document
                transforms = Collections.singletonList(
                        sigFactory.newTransform(
                        Transform.ENVELOPED,
                        (TransformParameterSpec) null));
                break;
        }

        // Retrieve signing key
        String keystore = LdcConfig.get("keystore", "myKeyStore.jks");
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(
                new FileInputStream(keystore),
                KEY_STORE_PASS.toCharArray());

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(
                KEY_ALIAS,
                PRIVATE_KEY_PASS.toCharArray());

        java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) keyStore.getCertificate(KEY_ALIAS);
        PublicKey publicKey = cert.getPublicKey();

        // Create a Reference to the enveloped document
        Reference ref = sigFactory.newReference(
                referenceURI,
                sigFactory.newDigestMethod(DigestMethod.SHA1, null),
                transforms,
                null,
                null);

        // Create the SignedInfo
        SignedInfo signedInfo = sigFactory.newSignedInfo(
                sigFactory.newCanonicalizationMethod(
                CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                (C14NMethodParameterSpec) null),
                sigFactory.newSignatureMethod(
                SignatureMethod.RSA_SHA1,
                null),
                Collections.singletonList(ref));

        // Create a KeyValue containing the RSA PublicKey 
        KeyInfoFactory keyInfoFactory = sigFactory.getKeyInfoFactory();
        KeyValue keyValue = keyInfoFactory.newKeyValue(publicKey);

        // Create a KeyInfo and add the KeyValue to it
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(keyValue));

        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element
        DOMSignContext dsc = new DOMSignContext(
                privateKey,
                sigParent);

        // Create the XMLSignature (but don't sign it yet)
        XMLSignature signature = sigFactory.newXMLSignature(signedInfo, keyInfo);

        // Marshal, generate (and sign) the enveloped signature
        signature.sign(dsc);

        // output the resulting document
        OutputStream os = new FileOutputStream(outputFile);
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(os));
    }

    public static boolean test(String fileName) throws Exception {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(fileName));
        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        }

        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_NAME), KEY_STORE_PASS.toCharArray());
        // Create a DOMValidateContext and specify a KeySelector
        // and document context.
        DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(keyStore), nl.item(0));

        // Unmarshal the XMLSignature.
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);

        // Validate the XMLSignature.
        boolean coreValidity = signature.validate(valContext);
        return coreValidity;
    }

    public static boolean verificarFirma(String fileName) throws Exception {
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(fileName));
        NodeList nl =doc.getElementsByTagNameNS(XMLSignature.XMLNS,"Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        }

        String providerName = System.getProperty(
                "jsr105Provider",
                "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
        XMLSignatureFactory fac =
                XMLSignatureFactory.getInstance("DOM",
                (Provider) Class.forName(providerName).newInstance());
        DOMValidateContext valContext = new DOMValidateContext(new KeyValueKeySelector(), nl.item(0));

        XMLSignature signature =
                fac.unmarshalXMLSignature(valContext);
        boolean coreValidity = signature.validate(valContext);

        if (coreValidity == false) {
            System.err.println("Signature failed");
            return false;
        } else {
            System.out.println("Signature passed");
            return true;
        }
    }
    
    
}