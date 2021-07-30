package oeg.rdflicense2;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.Policy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Loads the collection of licenses in the repo
 *
 * @author vroddon
 */
public class TripleStore {

    private static FusekiServer server = null;
    private static Dataset ds = null;
    public static List<Policy> policies = new ArrayList();
    public static Map<String, LicenseEntry> policiesIndex = new HashMap();

    public static final String GITREPO = "https://github.com/w3c/odrl/";
    public static final String DATAFOLDER = "odrl/bp/license";
    public static final String INDEXFILE ="odrl/bp/license/index.json";
    
    
    public static void main(String args[]) {
//        startServer();
//        startup();
//        clonegit();
        //       readIndex();
        createIndex();

    }
    /**
     * Deletes the data in memory
     */
    public static void clear()
    {
        policies = new ArrayList();
        policiesIndex = new HashMap();
        ds = null;
        server = null;
    }

    /**
     * @deprecated
     */
    public static void createIndex() {
        String folder = DATAFOLDER;
        loadlicenses(folder);
        System.out.println("================================================");
        for (Policy p : policies) {
            String titulo = p.getLabel("en").isEmpty() ? p.getTitle() : p.getLabel("en");
            if (titulo.isEmpty() || titulo==null)
                continue;
            String id = getLastBitFromUrl(p.getURI()).replace(".ttl", "");
            //System.out.println(id + " " + titulo);
            LicenseEntry le = new LicenseEntry(id);
            le.title= titulo;
            le.mapping.add(p.getURI()+".ttl");
            le.source = p.getSource();
            le.licenseid = id;
            try{
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(le );
                System.out.println(json+",");
            }catch(Exception e){}
        }
    }

    public static String getLastBitFromUrl(final String url) {
        // return url.replaceFirst("[^?]*/(.*?)(?:\\?.*)","$1);" <-- incorrect
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    public static void loadlicensesIndex(String file) {
        policiesIndex.clear();
        try {
            String str = IOUtils.toString(new FileInputStream(new File(file)), "UTF-8");
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray) parser.parse(str);
            Iterator<JSONObject> iterator = arr.iterator();
            while (iterator.hasNext()) {
                JSONObject o = (JSONObject) iterator.next();
                String id = o.get("licenseid").toString();
                if (id == null || id.isEmpty()) {
                    continue;
                }
                LicenseEntry e = new LicenseEntry(id);
                e.title = o.get("title").toString();
                e.source = o.get("source").toString();
                JSONArray am = (JSONArray) o.get("mapping");
                for (int i = 0; i < am.size(); i++) {
                    e.mapping.add((String) am.get(i));
                }
                policiesIndex.put(id, e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startup() {

        //We clone the git into a temporal folder
        //  clonegit();
/*
        //We clone the git into a temporal folder
        pullgit();
*/
        //We load all the licenses
        
        loadlicenses("..\\..\\temporal\\data\\licenses");
         
        //We start the server
        startSPARQLServer();

        System.out.println("Loaded a grandtotal of licenses: " + policies.size());
    }

    public static void clonegit(String repo, String folder) {
        try {
            FileUtils.deleteDirectory(new File(folder));
            Git.cloneRepository().setURI(repo).setDirectory(new File(folder)).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clonegit() {
        /*String repo = "https://github.com/Pret-a-LLOD/pddm/";
        String folder = "..\\..\\temporal";*/
        try {
            FileUtils.deleteDirectory(new File(DATAFOLDER));
            Git.cloneRepository().setURI(GITREPO).setDirectory(new File(DATAFOLDER)).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pullgit() {
        try {
//            String repo = "https://github.com/Pret-a-LLOD/pddm/";
//            String folder = "..\\..\\temporal";
            Git git = Git.open(new File(DATAFOLDER));
            /* CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI(repo);
        cloneCommand.setDirectory(new File(folder));
        cloneCommand.call();*/

//        Git git = Git.open(new File(folder));
            /*
        StoredConfig config = git.getRepository().getConfig();
        config.setString("branch", "master", "merge", "refs/heads/master");
        config.setString("branch", "master", "remote", "origin");
        config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
     //   config.setString("remote", "origin", "url", repo);
        config.save();
             */
            PullResult result = git.pull().setRemote("origin").setRemoteBranchName("master").call();
            PullCommand pull = git.pull();
            pull.setRemote("origin");
            pull.setRemoteBranchName("master");
            pull.call();
            git.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadlicenses()
    {
        loadlicenses("./"+DATAFOLDER+"/data/licenses");
    }
    
    public static void loadlicenses(String folder) {
        File ffolder = new File(folder);
        ds = DatasetFactory.createTxnMem();

        Collection<File> coleccion = FileUtils.listFiles(new File(folder), new String[]{"ttl", "rdf"}, true);

//        for (final File fileEntry : ffolder.listFiles()) {
        for (File fileEntry : coleccion) {
            System.out.println("Processing: " + fileEntry.getName());
            loadlicense(fileEntry.getAbsolutePath());

        }
    }

    public static void loadlicense(String file) {
        try {
            Dataset dtmp = RDFDataMgr.loadDataset(file);
            String s = "http://purl.org/NET/rdflicense/" + new File(file).getName().replace(".ttl", "");
            Model model = dtmp.getDefaultModel();
            ds.addNamedModel(s, model);

            String str = IOUtils.toString(new FileInputStream(new File(file)), "UTF-8");

            List<Policy> _policies = ODRLRDF.loadFromURI(file);
            for (Policy p : _policies) {
                if (p != null) {
                    policies.add(p);
                }
                /* String id = p.getIdentifier();
                
                if (!id.isEmpty())
                {
                    LicenseEntry entry = policiesIndex.get(id);
                    if (entry==null)
                        entry = new LicenseEntry(id);
                    if (entry.title.isEmpty())
                        entry.title = p.getTitle();
                    if (entry.source.isEmpty())
                        entry.source = p.getSource();
                    entry.mapping.add(p.getURI());
                    policiesIndex.put(id, entry);
                }*/
            }

        } catch (Exception e) {
            System.out.println("NOT loaded: " + file);
        }

    }

        /**
         * Starts the SPARQL server
         */
    public static void startSPARQLServer() {
       // loadlicenses("..\\..\\temporal\\data\\licenses");

        /*      System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
        Logger.getLogger("org.eclipse").setLevel(Level.OFF);
        Logger.getRootLogger().setLevel(Level.OFF);
         */
        try {
            Dataset dataset = RDFDataMgr.loadDataset("http://rdflicense.appspot.com/rdflicense/ms-c-nored-ff.ttl");
//            Dataset dataset = RDFDataMgr.read(ds.getUnionModel(), "");

            Model model = dataset.getDefaultModel();
            String syntax = "TURTLE"; // also try "N-TRIPLE" and "TURTLE"
            StringWriter out = new StringWriter();
            model.write(out, syntax);
            String result = out.toString();
            // System.out.println(result);

            server = FusekiServer.create().port(3330).enableCors(true).add("/ds", dataset, true).build();
            server.start();

            try (QueryExecution qExec = QueryExecutionFactory.create("SELECT * {?s ?p ?o}", dataset)) {
                ResultSet rs = qExec.execSelect();
                ResultSetFormatter.outputAsTSV(rs);
                int tot = rs.getRowNumber();
                System.out.println("triples " + tot);
                ResultSetFormatter.out(rs);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e2) {
            e2.printStackTrace();
        }
        System.out.println("Grandtotal total " + policies.size());
    }

    public static void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
