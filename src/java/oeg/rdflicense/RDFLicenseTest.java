package oeg.rdflicense;

import java.util.List;
import main.Licensius;

/**
 *
 * @author Victor
 */
public class RDFLicenseTest {
    public static void main(String[] args) {
        Licensius.initLogger();
        
        RDFLicenseDataset dataset = new RDFLicenseDataset();
        List<RDFLicense> licenses = dataset.listRDFLicenses();
        
        for(RDFLicense license : licenses)
        {
            String s = license.getSeeAlso();
//            String s2 = license.getLabel();
            String s2 = license.getSeeAlso();
            System.out.println(s+"\t"+s2);
        }
        
    }
    
}
