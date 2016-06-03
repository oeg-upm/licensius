package oeg.rdflicense.servlet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author vrodriguez
 */
public class RDFLicenseTest {

    public static void main(String[] args) {
        RDFLicenseDataset dataset = new RDFLicenseDataset();
        List<RDFLicense> licenses = dataset.getRDFLicenses();
        for(RDFLicense license : licenses)
        {
            System.out.println(license.getLabel());
        }
        System.out.println(licenses.size());
        
        
//        loadLocal();
    }


}
