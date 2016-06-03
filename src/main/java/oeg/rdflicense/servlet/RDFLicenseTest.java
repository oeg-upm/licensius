package oeg.rdflicense.servlet;

import java.util.List;
import oeg.rdflicense.RDFLicense;
import oeg.rdflicense.RDFLicenseDataset;

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
