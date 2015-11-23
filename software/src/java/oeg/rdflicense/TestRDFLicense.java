package oeg.rdflicense;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
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
        
        testODRL();
        
        if(true)
            return;
        
        RDFLicenseDataset dataset = new RDFLicenseDataset();

        RDFLicense lic = dataset.getRDFLicense("http://purl.org/NET/rdflicense/cc-by-nc-nd3.0es");

        List<String> ls = RDFLicenseCheck.getPermissions(lic);
        for(String s : ls)
        {
            String f = ODRL.getFirstComment(s);
            f=f.replace("\n", "");
            f=f.replace("\t", "");
            for(int i=0;i<10;i++)
                f=f.replace("  ", " ");
        }
//        System.out.println(lic.toTTL());
        
//        String s= MainServlet.getTable();
//        System.out.println(s);
    }
    
    public static void testclean()
    {
        
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
    
 
    public static void testODRL()
    {
        //We load ODRL
        InputStream in = ODRL.class.getResourceAsStream("../../resources/owl/odrl21.rdf");
        //Model model = ModelFactory.createDefaultModel();
        OntModel model = ModelFactory.createOntologyModel();

        if (in != null) {
            try{
                
                Model parcial = ModelFactory.createDefaultModel();
                String raw = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = "";
                while ((str = br.readLine()) != null) {
                    raw += str + " \n";
                }
                InputStream is = new ByteArrayInputStream(raw.getBytes());
                parcial.read(is,null, "RDF/XML");
                model.add(parcial);
            }catch(Exception e)
            {
                System.err.println("Not loaded " + e.getLocalizedMessage());
            }
        }    
        StringWriter sw = new StringWriter();
        model.write(sw, "TTL");
        System.out.println(sw.toString());
        
        System.out.println("==");
        
        
    }
    
}
