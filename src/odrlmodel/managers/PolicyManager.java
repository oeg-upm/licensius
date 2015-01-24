package odrlmodel.managers;

//JENA
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

//JAVA
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

//APACHE COMMONS
import ldconditional.LDRConfig;
import odrlmodel.rdf.ODRLModel;
import odrlmodel.rdf.ODRLRDF;
import odrlmodel.Policy;
import odrlmodel.rdf.RDFUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

/**
 * This class manages the files in a pre-defined folder, granting access to 
 * default policies and providing serialization methods
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class PolicyManager {

    private static List<Policy> policies = new ArrayList();
    
    /**
     * Reads policies found in the given folder.
     * Each policy is supposed to be in a TTL file placed in the folder
     * @return A list of ODRL policies
     */
    public static List<Policy> readPolicies(String folder) {
        List<Policy> pols = PolicyManager.getLicensesInFolder(folder);
        return pols;
    }

    /**
     * Establishes the list of managed policies
     * @parma _policies Policies to be set
     */
    public static void setPolicies(List<Policy> _policies) {
        policies = _policies;
    }

    /**
     * Gets the policy from its complete uri
     * @param uri URI of the policy
     */
    public static Policy getPolicy(String uri)
    {
        for(Policy policy : getPolicies())
        {
            if (policy.getURI().equals(uri))
                return policy;
        }
        return null;
    }

    /**
     * Gets the list of managed policies. Reads the file if needed.
     */
    public static List<Policy> getPolicies() {
        if (policies.isEmpty())
        {
            String folder=LDRConfig.getLicensesfolder();
            PolicyManager.setPolicies(PolicyManager.readPolicies(folder));            
        }
        return policies;
    }

    /**
     * Deletes a policy from the default folder
     * @param policy Policy to be deleted
     * @return true on success
     */
    public static boolean deletePolicy(Policy policy) {
        String fileName = policy.fileName;
        File f = new File(fileName);
        if (!f.exists()) {
            Logger.getLogger("ldr").warn("Delete: no such file or directory: " + fileName);
            return false;
        }
        if (!f.canWrite()) {
            Logger.getLogger("ldr").warn("Delete: write protected: " + fileName);
            return false;
        }
        if (f.isDirectory()) {
            String[] files = f.list();
            if (files.length > 0) {
                Logger.getLogger("ldr").warn("Delete: directory not empty: " + fileName);
                return false;
            }
        }
        boolean success = f.delete();
        if (!success) {
            Logger.getLogger("ldr").warn("Delete: deletion failed: " + fileName);
            return false;
        }
        return true;
    }

    /**
     * Loads all the policies in the default folder
     * @return Licenses in the folder
     */
    public static List<Policy> getLicensesInFolder(String sfolder) {
        List<Policy> politicasx = new ArrayList();
        File folder = new File(sfolder);
        if (!folder.exists())
            return politicasx;
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
//                Logger.getLogger("ldr").info("Parsing " + listOfFiles[i].getAbsolutePath());
                try {
                    File f = listOfFiles[i];
                    Logger.getLogger("ldr").info("Parsed licence in " + f.getAbsolutePath() + " successfully");
                    List<Policy> lp = PolicyManager.load(f.getAbsolutePath());
                    politicasx.addAll(lp);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.getLogger("ldr").warn("The file " + listOfFiles[i].getName() + " could not be understood " + e.getMessage());
                    Logger.getLogger("ldr").info("The file " + listOfFiles[i].getName() + " could not be understood " + e.getMessage());
                }
            } else if (listOfFiles[i].isDirectory()) {
            }
        }
        return politicasx;
    }

    /**
     * Loads the policies found in a file.
     * @return A set of policies
     */
    public static List<Policy> load(String path) {
        List<Policy> politicas=new ArrayList();
        try{
            Model model = RDFDataMgr.loadModel(path);
            List<Resource> ls = PolicyManager.findPolicies(model);
            for (Resource rpolicy : ls) {
                Policy policy = ODRLRDF.getPolicyFromResource(rpolicy);
                policy.fileName = path;
                politicas.add(policy);
            }
        }catch(Exception e)
        {
            Logger.getLogger("ldr").warn("No se pudo cargar la licencia " + path);
            e.printStackTrace();
        }
        return politicas;
    }
    
    /**
     * Loads a policy that is expected to be found in a file.
     * @todo IT IS NOT LOADING THE FULL RESTRICTIONS!
     * @return A policy
     */
    public static Policy load(String path, String spolicy) {
        List<Policy> politicas=new ArrayList();
        Policy politica = null;
        try{
            Model model = RDFDataMgr.loadModel(path);
            List<Resource> ls = PolicyManager.findPolicies(model);
            for (Resource rpolicy : ls) {
                Policy policy = ODRLRDF.getPolicyFromResource(rpolicy);
                policy.fileName = path;
                politicas.add(policy);
                if (policy.getURI().equals(spolicy))
                    politica=policy;
            }
        }catch(Exception e)
        {
            Logger.getLogger("ldr").warn("No se pudo cargar la licencia " + path);
            e.printStackTrace();
        }
        return politica;
    }    
    
    /**
     * Stores a policy in a file
     * @return true on success
     */
    public static boolean store(Policy policy, String path)
    {
        try {
            PrintWriter out = new PrintWriter(path);
            Resource rpolicy = ODRLRDF.getResourceFromPolicy(policy);
            out.println(ODRLRDF.getRDF(policy));
            out.close();
            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    /**
     * Finds the policies in the model. It determines URIs with a type of a known policy term.
     * Policies are accepted to be odrl:policy, cc:License or dc:LicenseDocument
     * @param model Model
     * @return List of resources
     */
    public static List<Resource> findPolicies(Model model) {
        List<Resource> total = new ArrayList();
        List<Resource> ls = RDFUtils.getOntResourcesOfType(model,ODRLModel.RPOLICY);
        total.addAll(ls);
        List<Resource> ls2 = RDFUtils.getResourcesOfType(model,ODRLModel.RCCLICENSE);
        total.addAll(ls2);
        List<Resource> ls3 = RDFUtils.getResourcesOfType(model, ODRLModel.RDCLICENSEDOC);
        total.addAll(ls3);
        return total;
    }
    
    
    
}
