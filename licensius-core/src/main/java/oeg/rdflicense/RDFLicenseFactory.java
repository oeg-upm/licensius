package oeg.rdflicense;

import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import java.util.List;
import java.util.UUID;
import oeg.vroddon.util.RDFPrefixes;

/**
 *
 * @author vrodriguez
 */
public class RDFLicenseFactory {

    
    public static RDFLicense createLicense(List<String> per, List<String> dut, List<String> pro)
    {
        RDFLicense lic = new RDFLicense();
        lic.model = ModelFactory.createDefaultModel();
        lic.uri = "http://syntheticlicense/"+UUID.randomUUID().toString();
        
        //THIS IS A LICENSE
        Resource rlic = ModelFactory.createDefaultModel().createResource(lic.uri);
        Statement st = ResourceFactory.createStatement(rlic, RDF.type, ODRL.RPOLICY);
        lic.model.add(st);

        
        //WE ADD THE PROHIBITIONS
        if (!pro.isEmpty())
        {
            Resource rp = ModelFactory.createDefaultModel().createResource(AnonId.create());
            st = ResourceFactory.createStatement(rp, RDF.type, ODRL.RPROHIBITION);
            lic.model.add(st);
            for(String p : pro)
            {
                Resource px = lic.model.createResource(p);
                st = ResourceFactory.createStatement(rp, ODRL.PACTION, px);
                lic.model.add(st);
            }
            st = ResourceFactory.createStatement(rlic, ODRL.PPROHIBITION, rp);
            lic.model.add(st);
        }
        
        //WE ADD THE PERMISSIONS
        if (!per.isEmpty())
        {
            Resource rp = ModelFactory.createDefaultModel().createResource(AnonId.create());
            st = ResourceFactory.createStatement(rp, RDF.type, ODRL.RPERMISSION);
            lic.model.add(st);
            for(String p : per)
            {
                Resource px = lic.model.createResource(p);
                st = ResourceFactory.createStatement(rp, ODRL.PACTION, px);
                lic.model.add(st);
            }
            st = ResourceFactory.createStatement(rlic, ODRL.PPERMISSION, rp);
            lic.model.add(st);
            
            
            //WE ADD THE DUTIES
           if (!dut.isEmpty())
           {
               Resource rpx = ModelFactory.createDefaultModel().createResource(AnonId.create());
               st = ResourceFactory.createStatement(rpx, RDF.type, ODRL.RDUTY);
               lic.model.add(st);
               for(String p : dut)
               {
                   Resource px = lic.model.createResource(p);
                   st = ResourceFactory.createStatement(rpx, ODRL.PACTION, px);
                   lic.model.add(st);
               }
               st = ResourceFactory.createStatement(rp, ODRL.PDUTY, rpx);
               lic.model.add(st);
           }           
        }
        lic.model=RDFPrefixes.addPrefixesIfNeeded(lic.model);
        return lic;
    }
    
}
