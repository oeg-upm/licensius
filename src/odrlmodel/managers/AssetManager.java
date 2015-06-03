package odrlmodel.managers;

//JENA
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMem;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraphMaker;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.vocabulary.RDF;

//JAVA
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import odrlmodel.Asset;
import ldconditional.LDRConfig;
import odrlmodel.MetadataObject;
import odrlmodel.rdf.ODRLModel;
import odrlmodel.rdf.ODRLRDF;
import odrlmodel.Policy;

//APACHE COMMONS
import odrlmodel.Policy;
import oeg.rdf.commons.RDFUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

/**
 * The AssetManager manages a set of licensed assets.
 * These assets can be either a generic resource (described by an URI), a reified statement (rdf:Statement), a dataset (dcat:Dataset) or a mapping (void:Linkset).
 * The governed assets are stored in a RDF file, as this is merely intended for demo purposes.
 * 
 * @author Victor
 */
public class AssetManager {

    private static final String FILENAME = LDRConfig.getPolicyassetstore();//"../data/assets.ttl";
    private static List<Asset> assets = new ArrayList();
    private static Model model = null;

    static {
        init();
    }

    /**
     * Inits the assets model
     */
    private static void init() {
        String store = LDRConfig.getPolicyassetstore();
        model = ModelFactory.createDefaultModel();
        model.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/"); //http://w3.org/ns/odrl/2/
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("cc", "http://creativecommons.org/ns#");
        model.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        model.setNsPrefix("void", "http://rdfs.org/ns/void#");
        model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        model.setNsPrefix("mysite", MetadataObject.DEFAULT_NAMESPACE);
    }

    /**
     * Gets an Asset given its URI
     * @param asseturi URI of the asset
     */
    public static Asset getAsset(String asseturi) {
        if (assets.isEmpty()) {
            assets = AssetManager.readAssets();
        }

        for (Asset asset : assets) {
            if (asset.uri.equals(asseturi)) {
                return asset;
            }
            try {
                URI uri = new URI(asset.uri);
                String[] segments = uri.getPath().split("/");
                if (segments.length > 0) {
                    String id = segments[segments.length - 1];
                    if (id.equals(asseturi)) {
                        return asset;
                    }
                }
            } catch (Exception ex) {
            }
        }
        return null;
    }

    /**
     * Sets the Jena Model with the information from the assets.
     */
    private static void updateModelFromAssets() {
        init(); //Reseteamos el modelo
        int conta = 0;
        for (Asset asset : assets) {
            Resource rasset = ODRLRDF.getResourceFromAsset(asset);
            model.add(rasset.getModel());
            //    System.out.println("--xx-- " + ++conta);
            //     RDFUtils.print(model);
        }


    }

    private static void updateAsset(Asset asset) {
        int index = findAssetIndex(asset);
        if (index == -1) {
            return;
        }
        assets.remove(index);
        assets.add(asset);
    }

    private static int findAssetIndex(Asset asset) {
        int found = -1;
        for (int i = 0; i < assets.size(); i++) {
            Asset a = assets.get(i);
            if (asset.getURI().equals(a.getURI())) {
                return i;
            }
        }
        return found;
    }

    /**
     * Sets the given assets
     * @param _assets
     */
    public static void setAssets(List<Asset> _assets) {
        assets = _assets;
    }

    /**
     * Retrieves the managed assets
     * @return A list of assets
     */
    public static List<Asset> getAssets() {
        return assets;
    }

    /**
     * Reads the assets in the predefined store (assets.ttl)
     * @return List of assets
     */
    public static List<Asset> readAssets() {
        init();
        List<Asset> assets = new ArrayList();
        String path = FILENAME;
        File f = new File(path);
        if (!f.exists()) {
            return assets;
        }
        RDFDataMgr.read(model, path);

        String sInfo = "";
        List<Resource> resources = RDFUtils.getResourcesOfType(model, model.getResource(RDFUtils.RDATASET.getURI()));
        for (Resource resource : resources) {
            Asset asset = ODRLRDF.getAssetFromResource(resource);
            assets.add(asset);
            sInfo+=asset+"\n";
        }
        Logger.getLogger("ldr").info(assets.size() + " assets have been succesfully parsed");
        return assets;
    }

    public static Asset findAsset(String resource) {
        if (assets.isEmpty()) {
            assets = AssetManager.readAssets();
        }
        for (Asset a : assets) {
            Resource ra = ODRLRDF.getResourceFromAsset(a);
            if (ra.getURI().equals(resource) || resource.equals(ra.getLocalName())) {
                return a;
            }
        }
        return null;
    }

    public static Policy findPolicyByTitle(String resource) {
        List<Policy> policies = PolicyManagerOld.getPolicies();
        for (Policy p : policies) {
            if (p.getTitle().equals(resource)) {
                return p;
            }
        }
        return null;
    }

    public static Policy findPolicyByLabel(String resource) {
        List<Policy> policies = PolicyManagerOld.getPolicies();
        for (Policy p : policies) {
            if (p.getLabel("en").equals(resource)) {
                return p;
            }
        }
        return null;
    }

    public static void writeAssets() {
        AssetManager.updateModelFromAssets();
        writeAssets(FILENAME);
    }

    /**
     * Writes the assets in the default file
     */
    public static void writeAssets(String path) {
        //      LDREditorView view = (LDREditorView) LDREditorApp.getApplication().getMainView();

        try {
            StringWriter sw = new StringWriter();
            RDFDataMgr.write(sw, model, Lang.TTL);
            String s = sw.toString();
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.write(s);
            writer.close();
        } catch (Exception e) {
            //          view.flashStatus("Error saving file");
        }
    }

    /**
     * Creates and adds a new asset to the list.
     * @param URI of the new asset
     * @param tipo Kind of the new asset (Dataset, Mapping, etc.)
     */
    public static Asset addNewAsset(String uri, String tipo) {
        Asset asset = new Asset(uri);
//        asset.setKind(tipo);
        assets.add(asset);
        return asset;
    }

    /**
     * Deletes an asset from the managed list
     * @param asset Asset
     */
    public static void deleteAsset(Asset asset) {
        for (Asset a : assets) {
            if (a.uri.equals(asset.uri)) {
                assets.remove(a);
                Resource rasset = ODRLRDF.getResourceFromAsset(asset);
                final Resource todelete = rasset;
                StmtIterator it = model.listStatements(new Selector() {

                    public boolean test(Statement stmnt) {
                        if (stmnt.getSubject().equals(todelete)) {
                            return true;
                        }
                        if (stmnt.getObject().equals(todelete)) {
                            return true;
                        }
                        return false;
                    }

                    public boolean isSimple() {
                        return false;
                    }

                    public Resource getSubject() {
                        return null;
                    }

                    public Property getPredicate() {
                        return null;
                    }

                    public RDFNode getObject() {
                        return null;
                    }
                });
                List<Statement> lists = new ArrayList();
                while (it.hasNext()) {
                    Statement st = it.nextStatement();
                    lists.add(st);
                }
                model.remove(lists);
                return;
            }
        }
    }

    /**
     * 
     */
    public static void setPolicyByLabel(String sasset, String spolicy) {
        try {
            Asset asset = AssetManager.findAsset(sasset);
            Policy policy = AssetManager.findPolicyByLabel(spolicy);
            if (asset != null && policy != null) {
                AssetManager.setPolicy(asset, policy);
            }
        } catch (Exception e) {
            Logger.getLogger("ldr").warn("Error " + e.getMessage());
        }
    }

    public static void addPolicyByTitle(String sasset, String spolicy) {
        try {
            Asset asset = AssetManager.findAsset(sasset);
            Policy policy = AssetManager.findPolicyByTitle(spolicy);
            if (asset != null && policy != null) {
                AssetManager.addPolicy(asset, policy);
            }
        } catch (Exception e) {
            Logger.getLogger("ldr").warn("Error " + e.getMessage());
        }
    }

    public static void addPolicyByLabel(String sasset, String spolicy) {
        try {
            Asset asset = AssetManager.findAsset(sasset);
            Policy policy = AssetManager.findPolicyByLabel(spolicy);
            if (asset != null && policy != null) {
                AssetManager.addPolicy(asset, policy);
            }
        } catch (Exception e) {
            Logger.getLogger("ldr").warn("Error " + e.getMessage());
        }
    }

    /**
     * Removes the policy from an asset, being both defined by their name
     */
    public static void removePolicyByLabel(String sasset, String spolicy) {
        try {
            Asset asset = AssetManager.findAsset(sasset);
            Policy policy = AssetManager.findPolicyByLabel(spolicy);
            if (asset != null && policy != null) {
                AssetManager.removePolicy(asset, policy);
            }
        } catch (Exception e) {
            Logger.getLogger("ldr").warn("Error " + e.getMessage());
        }
    }

    /**
     * Sets a policy to an asset, deleting any previous assignment
     */
    public static void setPolicy(Asset asset, Policy policy) {

        try {

            /*Resource rpolicy = model.getResource(policy.getURI());
            if (rpolicy==null)  //si no está en el archivo se añade
            {
            String spolicy = ODRLRDF.getRDF(policy);
            InputStream streamPolicy = new ByteArrayInputStream(spolicy.getBytes("UTF-8"));
            Model modelPolicy = ModelFactory.createDefaultModel();
            RDFDataMgr.read(modelPolicy, streamPolicy, Lang.TTL);
            model.add(modelPolicy);
            rpolicy = model.getResource(policy.getURI());
            }
            asset.getResource().removeAll(RDFUtils.PLICENSE);
            asset.getResource().addProperty(RDFUtils.PLICENSE, rpolicy);
             */
            AssetManager.setAssets(AssetManager.readAssets());
            //        RDFUtils.print(AssetManager.model);
            //        System.out.println("=============================");
            asset.setPolicy(policy);
            AssetManager.updateAsset(asset);
            AssetManager.updateModelFromAssets();
            //       RDFUtils.print(AssetManager.model);
            AssetManager.writeAssets();
            AssetManager.setAssets(AssetManager.readAssets());
        } catch (Exception e) {
            Logger.getLogger("ldr").warn("This policy could not be applied to that policy" + e.getMessage());
        }
    }

    /**
     * Adds a policy for an asset. 
     * @param policy Policy
     * @param asset Asset
     */
    public static void addPolicy(Asset asset, Policy policy) {

        //We make the change in the model
        asset.addPolicy(policy);
        AssetManager.updateAsset(asset);

        //And then, automatically, save the file.
        try {
            AssetManager.writeAssets();
            /*            
            Resource rpolicy=ODRLRDF.getResourceFromPolicy(policy);
            String spolicy = ODRLRDF.getRDF(policy);
            InputStream streamPolicy = new ByteArrayInputStream(spolicy.getBytes("UTF-8"));
            Model modelPolicy = ModelFactory.createDefaultModel();
            RDFDataMgr.read(modelPolicy, streamPolicy, Lang.TTL);
            model.add(modelPolicy);
            Resource rpolicy = model.getResource(policy.getURI());
            Resource rasset=ODRLRDF.getResourceFromAsset(asset);
            rasset.addProperty(RDFUtils.PLICENSE, rpolicy);
            AssetManager.writeAssets();
            AssetManager.setAssets(AssetManager.readAssets());*/
//            LD.print(model);
        } catch (Exception e) {
            Logger.getLogger("ldr").warn("This policy could not be applied to that policy" + e.getMessage());
        }
    }

    /**
     * Removes the association of a policy to an asset
     * @param policy Policy
     * @param asset Asset
     */
    public static void removePolicy(Asset asset, Policy policy) {
        Resource rpolicy = model.getResource(policy.getURI());
        Resource rasset = model.getResource(asset.getURI().toString());
        model.remove(rasset, RDFUtils.PLICENSE, rpolicy);
        asset.removePolicy(policy.getURI());
        AssetManager.writeAssets();
//        AssetManager.setAssets(AssetManager.readAssets());
    }

    public static String writeAssetPolicy(Asset asset, Policy policy, String fileName) {
        String s = "";
        Model model2 = ModelFactory.createDefaultModel();
        model2.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/");//http://w3.org/ns/odrl/2/
        model2.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model2.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model2.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model2.setNsPrefix("mysite", MetadataObject.DEFAULT_NAMESPACE);
        model2.setNsPrefix("cc", "http://creativecommons.org/ns#");
        model2.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        Resource rasset3 = ODRLRDF.getResourceFromAsset(asset);
        Resource rasset = model2.createResource(rasset3);
        if (rasset == null) {
            return "";
        }
        try {
            String spolicy = ODRLRDF.getRDF(policy);
            InputStream streamPolicy = new ByteArrayInputStream(spolicy.getBytes("UTF-8"));
            Model modelPolicy = ModelFactory.createDefaultModel();
            Resource rpolicy = model2.getResource(policy.getURI());
//            asset.getResource().addProperty(RDFUtils.PLICENSE, rpolicy);
            RDFDataMgr.read(modelPolicy, streamPolicy, Lang.TTL);
            model2.add(modelPolicy);

            Resource rasset2 = model2.getResource(asset.getURI().toString());
            rasset2.addProperty(RDFUtils.PLICENSE, rpolicy);

            FileOutputStream fos = new FileOutputStream(fileName);
            RDFDataMgr.write(fos, model2, Lang.RDFXML);
            fos.close();
        } catch (Exception e) {
            Logger.getLogger("ldr").warn("This policy could not be applied to that policy");
        }
        return s;
    }

    public static Model getModel() {
        return model;
    }

    public static String getGrafoComment(String selectedGrafo) {

        Asset asset = findAsset(selectedGrafo);
        if (asset == null) {
            return "Grafo desconocido";
        }
        String comentario = asset.getComment();
        return comentario;
    }

    public static Dataset getFullDatasetNew() {
        Dataset dataset = RDFDataMgr.loadDataset(LDRConfig.getDataset());
        if (true) {
            return dataset;
        }

        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {

            public Graph create() {
                return new GraphMem();
            }
        };
        Dataset dataxet = DatasetImpl.wrap(new DatasetGraphMaker(maker));

        RDFDataMgr.read(dataxet, LDRConfig.getDataset());
        return dataxet;
    }

    public static Dataset getFullDataset() {
        Model fullmodel = ModelFactory.createDefaultModel();

        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {

            public Graph create() {
                return new GraphMem();
            }
        };
        Dataset dataxet = DatasetImpl.wrap(new DatasetGraphMaker(maker));
        RDFDataMgr.read(dataxet, LDRConfig.getDataset()); //"../data/data.nq"
        Iterator<String> it = dataxet.listNames();
        while (it.hasNext()) {
            String s = it.next();
            System.out.println(s);
        }
        return dataxet;
    }

    /**
     * Retrieves the full model
     */
    public static Model getFullModel() {
        Model fullmodel = ModelFactory.createDefaultModel();
        List<String> grafos = AssetManager.getGrafosURIs();
        for (String grafo : grafos) {
            List<Statement> ls = AssetManager.getTriples(grafo);
            fullmodel.add(ls);
        }

//        RDFDataMgr.read(fullmodel, LDRConfig.getDataset()); //esto solo leeria los que no estén en ningún namedgraph
        return fullmodel;
    }

    /**
     * Obtiene el número de triples (como cadena) en el namedgraph dado
     * @return Puede devolver la cadena "desconocido"
     */
    public static String getGrafoNumTriples(String selectedGrafo) {
        Asset asset = findAsset(selectedGrafo);
        if (asset == null) {
            return "desconocido";
        }
        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {

            public Graph create() {
                return new GraphMem();
            }
        };
        Dataset dataxet = DatasetImpl.wrap(new DatasetGraphMaker(maker));
        RDFDataMgr.read(dataxet, LDRConfig.getDataset()); //"../data/data.nq"
        Model model = dataxet.getNamedModel(selectedGrafo);
        if (model == null) {
            return "desconocido";
        }
        int conta = 0;
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement stmt = it.next();
            conta++;
        }

        String comentario = "" + conta;
        model.close();
        return comentario;

    }

    /**
     * Obtiene los títulos de las políticas para un asset dada su URI
     */
    public static List<String> getPolicyTitlesForAssetURI(String selectedGrafo) {
        List<String> ls = new ArrayList();
        Asset asset = findAsset(selectedGrafo);
        if (asset == null) {
            return ls;
        }
        List<Policy> policies = asset.getPolicies();
        for (Policy p : policies) {
            Policy p2 = PolicyManagerOld.getPolicy(p.getURI());
            if (p2 == null) {
                Logger.getLogger("ldr").info(p.getURI() + " es una uri desconocida");
                ls.add("Unknown");
            } else {
                ls.add(p2.getTitle());
            }
        }
        return ls;
    }

    public static List<String> getPolicyLabelsForAssetURI(String selectedGrafo) {
        List<String> ls = new ArrayList();
        Asset asset = findAsset(selectedGrafo);
        if (asset == null) {
            return ls;
        }
        List<Policy> policies = asset.getPolicies();
        for (Policy p : policies) {
            Policy p2 = PolicyManagerOld.getPolicy(p.getURI());
            if (p2 == null) {
                Logger.getLogger("ldr").info(p.getURI() + " es una uri desconocida");
                ls.add("Unknown");
            } else {
                ls.add(p2.getLabel("en"));
            }
        }
        return ls;
    }

    /**
     * Obtiene la licencia del grafo dado
     */
    public static String getPolicyTitleForAssetURI(String selectedGrafo) {
        Asset asset = findAsset(selectedGrafo);
        if (asset == null) {
            return "desconocida";
        }
        List<Policy> policies = asset.getPolicies();
        if (policies.isEmpty()) {
            return "Sin licenciar";
        }
        Policy p = policies.get(0);
        p = PolicyManagerOld.getPolicy(p.getURI());
        if (p == null) {
            return "unknown";
        }
        return (p.title);
    }

    /**
     * Obtiene la licencia del grafo dado
     */
    public static String getPolicyLabelForAssetURI(String selectedGrafo) {
        Asset asset = findAsset(selectedGrafo);
        if (asset == null) {
            return "desconocida";
        }
        List<Policy> policies = asset.getPolicies();
        if (policies.isEmpty()) {
            return "Sin licenciar";
        }
        Policy p = policies.get(0);
        p = PolicyManagerOld.getPolicy(p.getURI());
        if (p == null) {
            return "unknown";
        }
        return (p.getLabel("en"));
    }

    /**
     * Restaura la configuración inicial.
     */
    public static void restoreDefaultPolicyAssignment() {
        String licencia="";
        List<String> grafos = AssetManager.getGrafosURIs();
        for(String grafo : grafos)
        {
//            System.out.println(grafo);
            List<String> politicas = AssetManager.getPolicyLabelsForAssetURI(grafo);
            for(String politica : politicas)
                AssetManager.removePolicyByLabel(grafo, politica);
        }
        for(String grafo : grafos)
        {
            String nombre=grafo.substring(grafo.lastIndexOf("/")+1);
            String str = LDRConfig.get(nombre);
            if (str != null && !str.equals("null"))
            {
                Logger.getLogger("ldr").info("Trying to assign the default policy to " + grafo +" " + str);
                str=str.trim();
                AssetManager.setPolicyByLabel(grafo, str);
            }
        }
    }

    /**
     * Returns a string with the model
     */
    public String toString() {
        try {
            String ttl = IOUtils.toString(new FileInputStream(AssetManager.FILENAME));
            return ttl;
        } catch (IOException ex) {
            return "";
        }
    } 

    /**
     * Obtiene los triples de un grafo dado
     */
    public static List<Statement> getTriples(String grafo) {
        List<Statement> ls = new ArrayList();

        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {

            public Graph create() {
                return new GraphMem();
            }
        };
        Dataset dataxet = DatasetImpl.wrap(new DatasetGraphMaker(maker));
        RDFDataMgr.read(dataxet, LDRConfig.getDataset());
        Model model = dataxet.getNamedModel(grafo);
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement st = it.nextStatement();
            ls.add(st);
        }

        return ls;
    }

    /**
     * Obtiene del Archivo de datos todos los grafos (URI)
     * @return Lista con todos los namedgraphs que existen
     */
    public static List<String> getGrafosURIs() {
        List<String> grafos = new ArrayList();
        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {

            public Graph create() {
                return new GraphMem();
            }
        };
        Dataset dataxet = DatasetImpl.wrap(new DatasetGraphMaker(maker));
        RDFDataMgr.read(dataxet, LDRConfig.getDataset()); //"../data/data.nq"
        Iterator its = dataxet.listNames();
        while (its.hasNext()) {
            String grafo = (String) its.next();
            grafos.add(grafo);
        }
        return grafos;
    }

    /**
     * Obtiene del Archivo de datos todos los labels de todos los grafos (String)
     */
    public static List<String> getGrafosLabels() {
        List<String> grafos = new ArrayList();
        DatasetGraphFactory.GraphMaker maker = new DatasetGraphFactory.GraphMaker() {

            public Graph create() {
                return new GraphMem();
            }
        };
        Dataset dataxet = DatasetImpl.wrap(new DatasetGraphMaker(maker));
        RDFDataMgr.read(dataxet, LDRConfig.getDataset()); //"../data/data.nq"
        Iterator its = dataxet.listNames();
        while (its.hasNext()) {
            String grafo = (String) its.next();
            Asset asset = AssetManager.getAsset(grafo);
            if (asset != null) {
                grafos.add(asset.getLabel("en"));
            }
        }
        return grafos;
    }

    public static List<Policy> getPolicies() {
        init();
        List<Policy> policies = new ArrayList();
        String path = FILENAME;
        File f = new File(path);
        if (!f.exists()) {
            return policies;
        }
        RDFDataMgr.read(model, path);
        List<Resource> ls1 = RDFUtils.getResourcesOfType(model, model.createResource(ODRLModel.RPOLICY.getURI()));
        for (Resource s : ls1) {
            //     System.out.println(s.getURI());
            Policy policy = ODRLRDF.getPolicyFromResource(s);
            policies.add(policy);
        }

        return policies;
    }
}
