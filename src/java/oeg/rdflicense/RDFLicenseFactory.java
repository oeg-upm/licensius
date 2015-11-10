package oeg.rdflicense;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.List;
import java.util.UUID;
import oeg.odrl.Odrl;
import oeg.odrl.RDFPrefixes;

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
        Statement st = ResourceFactory.createStatement(rlic, RDF.type, Odrl.RPOLICY);
        lic.model.add(st);

        
        //WE ADD THE PROHIBITIONS
        if (!pro.isEmpty())
        {
            Resource rp = ModelFactory.createDefaultModel().createResource(AnonId.create());
            st = ResourceFactory.createStatement(rp, RDF.type, Odrl.RPROHIBITION);
            for(String p : pro)
            {
                Resource px = lic.model.createResource(p);
                st = ResourceFactory.createStatement(rp, Odrl.PACTION, px);
                lic.model.add(st);
            }
            st = ResourceFactory.createStatement(rlic, Odrl.PPROHIBITION, rp);
            lic.model.add(st);
        }
        
        //WE ADD THE PERMISSIONS
        if (!per.isEmpty())
        {
            Resource rp = ModelFactory.createDefaultModel().createResource(AnonId.create());
            st = ResourceFactory.createStatement(rp, RDF.type, Odrl.RPERMISSION);
            for(String p : per)
            {
                Resource px = lic.model.createResource(p);
                st = ResourceFactory.createStatement(rp, Odrl.PACTION, px);
                lic.model.add(st);
            }
            st = ResourceFactory.createStatement(rlic, Odrl.PPERMISSION, rp);
            lic.model.add(st);
        }

        //WE ADD THE DUTIES
        if (!dut.isEmpty())
        {
            Resource rp = ModelFactory.createDefaultModel().createResource(AnonId.create());
            st = ResourceFactory.createStatement(rp, RDF.type, Odrl.RDUTY);
            for(String p : dut)
            {
                Resource px = lic.model.createResource(p);
                st = ResourceFactory.createStatement(rp, Odrl.PACTION, px);
                lic.model.add(st);
            }
            st = ResourceFactory.createStatement(rlic, Odrl.PDUTY, rp);
            lic.model.add(st);
        }
        
        lic.model=RDFPrefixes.addPrefixesIfNeeded(lic.model);
        return lic;
    }
    
}
