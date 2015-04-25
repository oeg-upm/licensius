package ldrauthorizer.ws;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ldrauthorizerold.GoogleAuthHelper;
import model.ConditionalDataset;
import odrlmodel.Policy;
import odrlmodel.managers.PolicyManagerOld;
import odrlmodel.managers.Signature;
import odrlmodel.rdf.ODRLRDF;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 * This class represents the policies a user has paid for.
 * 
 * @author vroddon
 */
public class Portfolio {

    //List of policies in the portfolio of a user
    public List<Policy> policies = new ArrayList();

    //Mapa de "state" a portfolio
    public static Map<String, Portfolio> portfolios = new HashMap();


    public List<Policy> getPolicies()
    {
        return policies;
    }

    public List<String> getGrafos(ConditionalDataset cd)
    {
        List<String> ls = new ArrayList();
        List<Policy> lp = getPolicies();
        for(Policy p: lp)
        {
            String target = p.getFirstTarget();
            ls.add(target);
        }
        return ls;
    }

    /**
     * Stores the portfolios for a given user
     */
    public static void StorePortfolio(String mail)
    {
        if (mail==null)
            return;
        try{
            Portfolio p = Portfolio.getPortfolio(mail);
            
            mail=mail.replaceAll("mailto:", "");
            String filename = "./portfolios/"+mail;
            Path path = Paths.get(filename);
            Charset ENCODING = StandardCharsets.UTF_8;
            BufferedWriter writer = Files.newBufferedWriter(path, ENCODING);
            writer.write(p.toTurtle());
            writer.close();
        }catch(Exception e){e.printStackTrace();}
    }
    
    /**
     * Reads a portfolio from a given user
     */
    public static Portfolio ReadPortfolio(String mail)
    {
        mail=mail.replaceAll("mailto:", "");
        String filename = "../data/portfolios/"+mail;
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, filename, Lang.TTL);
        List<Resource> ls = PolicyManagerOld.findPolicies(model);
        Portfolio portfolio = new Portfolio();
        for(Resource r :ls)
        {
            Policy policy=ODRLRDF.getPolicyFromResource(r);
            portfolio.policies.add(policy);
        }
        return portfolio;
    }
    
    
    /**
     * Gets the portfolio for a given session id number
     */
    public static Portfolio getPortfolio(String state)
    {
        if (state==null)
            state="noname@tmp.com";
        Portfolio p=portfolios.get(state);
        if (p==null)
        {
            p = new Portfolio();
            portfolios.put(state,p);
        }
        return p;
    }
    
    /**
     * Sets the portfolio for a given session id number
     */
    public static void setPortfolio(String state, Portfolio p) {
        if (state==null)
            state="guest";
        portfolios.put(state,p);
        StorePortfolio(state);
   }
    
    /**
     * Expresses the portfolio as Turtle
     */
    public String toTurtle()
    {
        Model model = ModelFactory.createDefaultModel();
        ODRLRDF.addPrefixesToModel(model);
        for(Policy policy : policies)
        {
            Resource r = ODRLRDF.getResourceFromPolicy(policy);
            model.add(r.getModel());
        }
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, model, Lang.TTL);
        String s = sw.toString();
        return s;
    }
    
    /**
     * Expresses the portfolio as Signed RDF/XML
     */
    public String toSignedRDF()
    {
        Model model = ModelFactory.createDefaultModel();
        ODRLRDF.addPrefixesToModel(model);
        for(Policy policy : policies)
        {
            Resource r = ODRLRDF.getResourceFromPolicy(policy);
            model.add(r.getModel());
        }
        StringWriter sw = new StringWriter();
        StringWriter sw2 = new StringWriter();
        RDFDataMgr.write(sw, model, Lang.RDFXML);String s = sw.toString();
        RDFDataMgr.write(sw2, model, Lang.TTL);String s2 = sw2.toString();
        String firmado="";
        try {
            firmado = Signature.firmar(s, -1);
        } catch (Exception ex) {
            Logger.getLogger(Portfolio.class.getName()).log(Level.SEVERE, null, ex);
        }
        return firmado;
    }
    
    
}
