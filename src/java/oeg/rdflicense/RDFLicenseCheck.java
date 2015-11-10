package oeg.rdflicense;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.util.ArrayList;
import java.util.List;
import main.Licensius;
import oeg.odrl.Odrl;

/**
 *
 * @author vrodriguez
 */
public class RDFLicenseCheck {

    public RDFLicense license = null;
    
    public RDFLicenseCheck(RDFLicense lic)
    {
        license=lic;
    }
    
    /**
     * Verifies if there is a permission to read.
     */
    public boolean isOpen() {
        
        boolean open = false;

        List<String> permitidas = new ArrayList();
        List<String> prohibidas = new ArrayList();
        //PROCESANDO LO PERMITIDO.
        NodeIterator ni = license.model.listObjectsOfProperty( ModelFactory.createDefaultModel().createResource(license.getURI()), Odrl.PPERMISSION);
        while(ni.hasNext())
        {
            RDFNode n = ni.next();
            if(n.isResource())
            {
                Resource r = n.asResource();
                StmtIterator sit = r.listProperties(Odrl.PACTION);
                while(sit.hasNext())
                {
                    Statement st=sit.next();
                    RDFNode n2 = st.getObject();
                    String s = n2.asResource().getLocalName();
                    permitidas.add(s);
                }
            }
        }
        
        //PROCESANDO LO PROHIBIDO
        ni = license.model.listObjectsOfProperty( ModelFactory.createDefaultModel().createResource(license.getURI()), Odrl.PPROHIBITION);
        while(ni.hasNext())
        {
            RDFNode n = ni.next();
            if(n.isResource())
            {
               Resource r = n.asResource();
                StmtIterator sit = r.listProperties(Odrl.PACTION);
                while(sit.hasNext())
                {
                    Statement st=sit.next();
                    RDFNode n2 = st.getObject();
                    String s = n2.asResource().getLocalName();
                    prohibidas.add(s);
                }
            }
        }        
        for(String s : permitidas)
            System.out.println("OK " + s);
        for(String s : prohibidas)
            System.out.println("NOK " + s);
        if (permitidas.contains("Distribution") && permitidas.contains("DerivativeWorks") && !prohibidas.contains("CommercialUse"))
            open = true;
        return open;
    }    
    
    public static void main(String[] args) {
        Licensius.initLogger();
        String uri2 ="http://purl.org/NET/rdflicense/cc-by3.0";
        String uri4 = "http://purl.org/NET/rdflicense/cc-by-nd3.0cl";
        String uri= "http://purl.org/NET/rdflicense/ukogl-nc2.0";
        RDFLicense lic = RDFLicenseDataset.getRDFLicense(uri);
        
        System.out.println(lic.toTTL());
        
        RDFLicenseCheck checker = new RDFLicenseCheck(lic);
        System.out.println(checker.isOpen());
    }
    
    
}


