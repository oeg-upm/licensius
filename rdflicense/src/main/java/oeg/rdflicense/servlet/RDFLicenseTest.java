package oeg.rdflicense.servlet;

import java.util.List;
import oeg.licensius.rdflicense.RDFLicense;
import oeg.licensius.rdflicense.RDFLicenseDataset;

/**
 * Class to test that everything is alrgiht.
 * @author vrodriguez
 */
public class RDFLicenseTest {

    public static void main(String[] args) {
        RDFLicenseDataset dataset = new RDFLicenseDataset();
        List<RDFLicense> licenses = dataset.getRDFLicenses();
        for(RDFLicense license : licenses)
        {
            System.out.println(license.getLabel() + "     " + license.getURI());
        }
        System.out.println(licenses.size());
        
        
//        loadLocal();
    }


}
