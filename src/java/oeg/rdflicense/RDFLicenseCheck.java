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
import org.json.simple.JSONObject;

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
    
    
    /**
     * This is to determine the compatibility between any two licenses
     */
    public static String compose(RDFLicense lic1, RDFLicense lic2) {
        
        
        String compatible = "";
        String reason = "";
        String source = "";

        List<String> per1 = RDFLicenseCheck.getPermissions(lic1);
        List<String> pro1 = RDFLicenseCheck.getProhibitions(lic1);
        List<String> dut1 = RDFLicenseCheck.getDuties(lic1);
        

        
        reason ="A computation has been made on the basis of the main permissions, prohibitions. The result has been computed automatically and no warranty exists on its reliability. Please check the legal text.";
        source="computed";
        
        
        
        String json ="";
        try {
            JSONObject obj = new JSONObject();
            obj.put("compatible", compatible);
            obj.put("reason", reason);
            obj.put("source", source);
            json = obj.toString();
        } catch (Exception e) {
            json = "error";
        }
        return json;
    }
    
    
    private static List<String> getPermissions(RDFLicense lic) {
        List<String> list = new ArrayList();
        NodeIterator ni = lic.model.listObjectsOfProperty( ModelFactory.createDefaultModel().createResource(lic.getURI()), Odrl.PPERMISSION);
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
                    list.add(s);
                }
            }
        }        
        return list;
    }
    private static List<String> getProhibitions(RDFLicense lic) {
        List<String> list = new ArrayList();
        NodeIterator ni = lic.model.listObjectsOfProperty( ModelFactory.createDefaultModel().createResource(lic.getURI()), Odrl.PPROHIBITION);
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
                    list.add(s);
                }
            }
        }             
        return list;
    }
    private static List<String> getDuties(RDFLicense lic) {
        List<String> list = new ArrayList();
        NodeIterator ni = lic.model.listObjectsOfProperty( ModelFactory.createDefaultModel().createResource(lic.getURI()), Odrl.PDUTY);
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
                    list.add(s);
                }
            }
        }             
        return list;
    }

    public static void main(String[] args) {
        Licensius.initLogger();
        String uri2 ="http://purl.org/NET/rdflicense/cc-by3.0";
        String uri4 = "http://purl.org/NET/rdflicense/cc-by-nd3.0cl";
        String uri= "http://purl.org/NET/rdflicense/ukogl-nc2.0";
        
        RDFLicense lic1 = RDFLicenseDataset.getRDFLicense(uri);
        RDFLicense lic2 = RDFLicenseDataset.getRDFLicense(uri2);
        
        System.out.println(lic1.toTTL());
        System.out.println(lic2.toTTL());
        System.out.println(RDFLicenseCheck.compose(lic1, lic2));
    }
    
}


