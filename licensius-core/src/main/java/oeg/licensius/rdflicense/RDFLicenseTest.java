package oeg.licensius.rdflicense;

import java.util.List;
import oeg.licensius.core.Licensius;

/**
 *
 * @author Victor
 */
public class RDFLicenseTest {
    public static void main(String[] args) {
        Licensius.init();
        test2();
        /*
        RDFLicenseDataset dataset = new RDFLicenseDataset();
        List<RDFLicense> licenses = dataset.listRDFLicenses();
        for(RDFLicense license : licenses)
        {
            String s = license.getSeeAlso();
            String s2 = license.getSeeAlso();
            System.out.println(s+"\t"+s2);
        }
        */
    }
    
//         java -jar licensius-core.jar -legalcode http://purl.org/NET/rdflicense/ukogl-nc2.0
    public static void test2()
    {
        String uri="http://purl.org/NET/rdflicense/ukogl-nc2.0";
 //       RDFLicenseDataset dataset = new RDFLicenseDataset();
        RDFLicense lic = new RDFLicense(uri);
        String legalcode = lic.getLegalCode("en");
        System.out.println(legalcode);
        
    }
    
    
    
}
