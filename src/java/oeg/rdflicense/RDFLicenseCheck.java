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
import org.apache.commons.collections.ListUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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
        String resulting ="";

        reason ="A computation has been made on the basis of the main permissions, prohibitions. The result has been computed automatically and no warranty exists on its reliability. Please check the legal text.";
        source="computed";
        
        List<String> per1 = RDFLicenseCheck.getPermissions(lic1);
        List<String> pro1 = RDFLicenseCheck.getProhibitions(lic1);
        List<String> dut1 = RDFLicenseCheck.getDuties(lic1);
        List<String> per2 = RDFLicenseCheck.getPermissions(lic2);
        List<String> pro2 = RDFLicenseCheck.getProhibitions(lic2);
        List<String> dut2 = RDFLicenseCheck.getDuties(lic2);
        

        /*System.out.print("Per1: ");
        for(String p : per1)
            System.out.print(p+" ");
        System.out.print("\nPro1: ");
        for(String p : pro1)
            System.out.print(p+" ");
        System.out.print("\nPer2: ");
        for(String p : per2)
            System.out.print(p+" ");
        System.out.print("\nPro2: ");
        for(String p : pro2)
            System.out.print(p+" ");
        System.out.print("\n");*/
        
        
        
        if (lic1.getURI().equals(lic2.getURI()))
        {
            compatible="compatible";
            reason = "The licenses are the same.";
        }

        List<String> per3 = ListUtils.intersection(per1, per2);
        List<String> pro3 = ListUtils.union(pro1, pro2);
        List<String> dut3 = ListUtils.union(dut1, dut2);
        
        List<String> em1=ListUtils.intersection(pro3, per3);
        if (!em1.isEmpty() )
        {
            compatible = "not compatible";
        }
        else
        {
            compatible ="compatible";
        }
        
        RDFLicense lic3 = RDFLicenseFactory.createLicense(per3, dut3, pro3);

        resulting = lic3.toTTL();
//        System.out.println(lic3.toTTL());
        
        
        String json ="";
        try {
            JSONObject obj = new JSONObject();
            obj.put("compatible", compatible);
            obj.put("reason", reason);
            obj.put("source", source);
            obj.put("resulting", resulting);
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
                    String s = n2.asResource().getURI();
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
                    String s = n2.asResource().getURI();
                    list.add(s);
                }
            }
        }             
        return list;
    }
    private static List<String> getDuties(RDFLicense lic) {
        List<String> list = new ArrayList();
//        NodeIterator ni = lic.model.listObjectsOfProperty( ModelFactory.createDefaultModel().createResource(lic.getURI()), Odrl.PDUTY);
        NodeIterator ni = lic.model.listObjectsOfProperty( Odrl.PDUTY);
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
                    String s = n2.asResource().getURI();
                    list.add(s);
                }
            }
        }             
        return list;
    }

    public static void main(String[] args) {
        Licensius.initLogger();
        String uri ="http://purl.org/NET/rdflicense/cc-by-nc3.0";
        String uri2= "http://purl.org/NET/rdflicense/cc-by-nd3.0";
        String uri3= "http://purl.org/NET/rdflicense/ukogl-nc2.0";
        String uri4 = "http://purl.org/NET/rdflicense/cc-by-nd3.0cl";
        
        RDFLicense lic1 = RDFLicenseDataset.getRDFLicense(uri);
        RDFLicense lic2 = RDFLicenseDataset.getRDFLicense(uri2);
        
        System.out.println(lic1.toTTL());
        System.out.println(lic2.toTTL());
        String res = RDFLicenseCheck.compose(lic1, lic2);
        System.out.println(res);
        Object obj=JSONValue.parse(res);
        JSONObject jobj=(JSONObject)obj;
        
        Object x = jobj.get("resulting");
        String sx = (String)x;
        System.out.println(x);
    }

    
    
    
}


