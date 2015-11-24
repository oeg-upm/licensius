package oeg.rdflicense.mining;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;
import oeg.rdflicense.ODRL;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;
import oeg.rdflicense.RDFUtils;

/**
 *
 * @author vrodriguez
 */
public class CCTextMiner {

    public static void main(String[] args) throws IOException {

        RDFLicenseDataset ds = new RDFLicenseDataset();
        List<RDFLicense> list = ds.getRDFLicenses();
        Collections.sort(list,RDFLicense.COMPARE_URI);
        for (RDFLicense lic : list) {
            if (lic == null) {
                continue;
            }
            String also = lic.getSeeAlso();
            if (also != null && !also.isEmpty()) {
                System.out.println("Legal Code: " + lic.getLegalCode().length());
                System.out.println("URI: " + also);
                if (!also.contains("ca/legalcode"))
                {
                    continue;
                }
                
                String also2 = also+".en";
                String also3 = also+".fr";
                
                String x2 = getText(also2);
                String x3 = getText(also3);
                System.out.println("Found HTML: " + x2.length() + " " + x3.length());
                char tmp = (char) new InputStreamReader(System.in).read ();
                System.out.println(tmp);
                if (tmp=='y')
                {
                    System.out.println(x2);
                    Model m = lic.getModel();
                    Literal l = m.createLiteral(x2, "en");
                    m.add(ModelFactory.createDefaultModel().createResource(lic.getURI()), ODRL.PLEGALCODE, l);
                    
                    Literal l3 = m.createLiteral(x3, "fr");
                    m.add(ModelFactory.createDefaultModel().createResource(lic.getURI()), ODRL.PLEGALCODE, l3);
                    
                    StringWriter sw = new StringWriter();
                    m = RDFUtils.cleanUnusedPrefixes(m);
                    if (m != null) {
                        m.write(sw, "TURTLE");
                    }
                    String ttl = sw.toString();
                    
                        FileWriter fw = new FileWriter("sal.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(ttl);
			bw.close();
                    
                }
            }
        }
    }
    
    public static void updateLicense()
    {
        
    }
    

    public static String getText(String uri) {
        try {
            URLConnection conn = new URL(uri).openConnection();
            EditorKit kit = new HTMLEditorKit();
            Document doc = kit.createDefaultDocument();
            doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
            Reader rd = new InputStreamReader(conn.getInputStream(), "UTF-8");
            kit.read(rd, doc, 0);
            String x = doc.getText(0, doc.getLength());
            return x;
        } catch (Exception e) {
            return "";
        }
    }

}
