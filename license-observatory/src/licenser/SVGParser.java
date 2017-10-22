/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package licenser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Victor
 */
public class SVGParser extends DefaultHandler {

    private XMLReader xr = null;
    Writer out = null;

    public static void main(String[] args) {
       // System.out.println("Ahora");

        LicenseFinder lf = new LicenseFinder();

    //    lf.ejecutar();


        SVGParser p = new SVGParser();
        p.f();
//            p.parseXmlFile();
    }

    private void emit(String s) throws SAXException {
        try {
            out.write(s);
            out.flush();
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }

    private void nl()
            throws SAXException {
        String lineEnd = System.getProperty("line.separator");
        try {
            out.write(lineEnd);
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }

    void f() {

        FileReader fr;
        try {
            out = new OutputStreamWriter(System.out, "UTF8");
            xr = XMLReaderFactory.createXMLReader();
            xr.setContentHandler(this);
            xr.setErrorHandler(this);
            fr = new FileReader("lod.svg");
            xr.parse(new InputSource(fr));
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(SVGParser.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public void startDocument()
            throws SAXException {
        emit("<?xml version='1.0' encoding='UTF-8'?>");
        nl();
    }

    public void endDocument()
            throws SAXException {
        try {
            nl();
            out.flush();
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes atts) {
        //       System.out.println("tElemento: " + name);
        if (name.equals("a")) {
        String id = "";
        String tipo = "";
        for (int i = 0; i < atts.getLength(); i++) {
            String atr = atts.getLocalName(i);
            String val = atts.getValue(i);
            //       System.out.println(name + " " + atr + " " + val);
            if (atr.equals("class") && (val.equals("dataset geographic"))) {
                //    System.out.println("IRRINCHI");
                tipo = val;
            }
            if (atr.equals("id")) {
                id = val.substring(8);
            }
        }
        if (!tipo.isEmpty()) {
            System.err.println(id + " " + tipo);
        }
        }
        try {
            String eName = name; // element name
            if ("".equals(eName)) {
                eName = qName; // not namespace-aware
            }
            emit("<" + eName);
            if (atts != null) {
                for (int i = 0; i < atts.getLength(); i++) {
                    String aName = atts.getLocalName(i); // Attr name
                    if ("".equals(aName)) {
                        aName = atts.getQName(i);
                    }
                    emit(" ");
                    emit(aName + "=\"" + atts.getValue(i) + "\"");
                }
            }
            emit(">");
        } catch (Exception e) {
            System.err.println("ay");
        }
    }

    public void endElement(String namespaceURI,
            String sName, // simple name
            String qName // qualified name
            )
            throws SAXException {
        String eName = sName; // element name
        if ("".equals(eName)) {
            eName = qName; // not namespace-aware
        }
        emit("</" + eName + ">");
    }

    private void parseXmlFile() {
        /*
        try {
        File file = new File("lod.svg");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();
        System.out.println("Root element " + doc.getDocumentElement().getNodeName());
        NodeList nodeLst = doc.getElementsByTagName("svg");
        System.out.println("Parseando");
        
        for (int s = 0; s < nodeLst.getLength(); s++) {
        
        Node fstNode = nodeLst.item(s);
        
        if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
        
        Element fstElmnt = (Element) fstNode;
        NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("g");
        Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
        NodeList fstNm = fstNmElmnt.getChildNodes();
        System.out.println("First Name : " + ((Node) fstNm.item(0)).getNodeValue());
        NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("lastname");
        Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
        NodeList lstNm = lstNmElmnt.getChildNodes();
        System.out.println("Last Name : " + ((Node) lstNm.item(0)).getNodeValue());
        }
        
        }
        } catch (Exception e) {
        e.printStackTrace();
        }*/
    }
}
