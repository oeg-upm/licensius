package oeg.rdflicense;

import java.util.List;

/**
 * Not JUnit tests, but exemplary tests
 * @author Victor
 */
public class TestRDFLicense {

    /**
     * This method (and observing the logs) is enough to test if the dataset is correct
     */
    public static void main(String[] args) {
//        RDFLicenseDataset dataset = new RDFLicenseDataset();

//        RDFLicense lic = dataset.getRDFLicense("http://purl.org/NET/rdflicense/cc-by-nc-nd3.0es");
//        System.out.println(lic.getLabel());
        
        String s= MainServlet.getTable();
        System.out.println(s);
        

    }
    
    public static void test1()
    {
        RDFLicenseDataset dataset = new RDFLicenseDataset();
        System.out.println("Valido: " + dataset.isValid());
        List<RDFLicense> licenses = dataset.getRDFLicenses();
        for (RDFLicense license : licenses) {
            System.out.println(license.getSeeAlso()+"\t"+license.getLabel());
        }
    }
    

    
    public static void testhtml()
    {
        RDFLicenseDataset dataset = new RDFLicenseDataset();
        List<RDFLicense> licenses = dataset.getRDFLicenses();
        for (RDFLicense license : licenses) {
            String s = "<tr>";
            s+= "<td>"+license.getLabel()+"</td>";
            s+= "<td>"+license.getURI()+"</td>";
            s+= "<td>"+license.getVersion()+"</td>";
            s+= "<td><a href=\""+  license.getURI() +"\"><img src=\"img/rdflicense32.png\"/></a>"; 
            s+= "<a href=\""+  license.getURI() +".ttl\"><img src=\"img/rdf32.png\"/></a></td>"; 
            s+="</tr>\n";
//            System.out.println(s);
        }
    }
    
    
}
