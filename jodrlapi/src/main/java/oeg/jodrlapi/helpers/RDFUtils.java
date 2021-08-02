package oeg.jodrlapi.helpers;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import oeg.jodrlapi.JODRLApiSettings;
import oeg.jodrlapi.odrlmodel.Action;
import oeg.jodrlapi.odrlmodel.Constraint;
import oeg.jodrlapi.odrlmodel.Rule;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class with some useful methods to manipulate RDF
 * Not to be used by external users.
 * @exclude
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
public class RDFUtils {
    
//    private static final Logger logger = Logger.getLogger(RDFUtils.class.getName());

    String property = "";
    String value = "";
    public static Property TITLE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/title");
    public static Property COMMENT = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#comment");
    public static Property DEFINITION = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2004/02/skos/core#definition");
    public static Property NOTE = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2004/02/skos/core#note");
    public static Property RIGHTS = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/rights");
    public static Property RLICENSE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/license");
    public static Property LABEL = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    public static Property SEEALSO = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
    public static Property SOURCE = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/source");
    public static Resource RDATASET = ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/dcat#Dataset");
    public static Resource RLINKSET = ModelFactory.createDefaultModel().createResource("http://rdfs.org/ns/void#Linkset");
    public static Property RIDENTIFIER = ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/identifier");
    public static Property CLOSEMATCH = ModelFactory.createDefaultModel().createProperty("http://www.w3.org/2004/02/skos/core#closeMatch");
    public static OntModel coreModel;

    static {
        try {
            initModel(true);
        } catch (Exception e) {
    //        logger.warn("ODRLapi could not loaded odrl ontology, " + e.getMessage());
            System.err.println("ODRLApi error");
        }
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RDFUtils.class);

    /**
     * Loads the models.
     * - The ODRL model
     * - The CCREL model
     * @param Whether the file is locally loaded or from the oficial URI
     */
    private static void initModel(boolean local) throws IOException {
        coreModel = ModelFactory.createOntologyModel();
        InputStream in1 = null, in2 = null;
        if (local == false) {
            in1 = FileManager.get().open(JODRLApiSettings.ODRL_ONTOLOGY);
            in2 = FileManager.get().open(JODRLApiSettings.CC_ONTOLOGY);
        } else {
            in1 = RDFUtils.class.getClassLoader().getResourceAsStream("odrl22.ttl"); //To find them in this package it should be odrlmodel/odrl21.ttl etc.
            in2 = RDFUtils.class.getClassLoader().getResourceAsStream("ccrel.rdf");
        }
        if (in1 != null && in2 != null) {
            try {
                coreModel.read(in1, null, "RDF/XML");
            } catch (Exception e) {
                in1.close();
                in1 = RDFUtils.class.getClassLoader().getResourceAsStream("odrl22.ttl"); //To find them in this package it should be odrlmodel/odrl21.ttl etc.
                coreModel.read(in1, null, "TTL");
            }
            try {
                coreModel.read(in2,null, "RDF/XML");
            } catch (Exception e2) {
                in2.close();
                in2 = RDFUtils.class.getClassLoader().getResourceAsStream("ccrel.rdf");
                coreModel.read(in2, null,"TTL");
            }
        }
        if (coreModel == null) {
            System.err.println("Error loading internal ontologies");
        }
        in1.close();
        in2.close();
    }

    public static String getIndividualLabel(String uri) {
        Individual ind = coreModel.getIndividual(uri);
        String label = RDFUtils.getFirstPropertyValue(ind, RDFUtils.LABEL);
        return label;
    }

    public static String getIndividualComment(String uri) {
        Individual ind = coreModel.getIndividual(uri);
        String label = RDFUtils.getFirstPropertyValue(ind, RDFUtils.COMMENT);
        return label;
    }

    /**
     * Creates an action with the metadata properties defined in the core model
     */
    public static Action enrichAction(Action action1) {
        Action action = new Action(action1);
        Resource r = coreModel.getResource(action.uri);
        if (r != null) {
            List<String> labels = RDFUtils.getAllPropertyStrings(r, RDFUtils.LABEL);
            for (String label : labels) {
                action.addLabel(label);
            }
            action.title = RDFUtils.getFirstPropertyValue(r, RDFUtils.TITLE);
            action.comment = RDFUtils.getFirstPropertyValue(r, RDFUtils.COMMENT);
            action.seeAlso = RDFUtils.getFirstPropertyValue(r, RDFUtils.SEEALSO);
        }
        return action;
    }

    /**
     * Creates a constraint with the metadata properties defined in the core model
     */
    public static Constraint enrichConstraint(Constraint coriginal) {
        Constraint action = new Constraint(coriginal);
        Resource r = coreModel.getResource(action.uri);
        if (r != null) {
            List<String> labels = RDFUtils.getAllPropertyStrings(r, RDFUtils.LABEL);
            for (String label : labels) {
                action.addLabel(label);
            }
            action.comment = RDFUtils.getFirstPropertyValue(r, RDFUtils.COMMENT);
            action.seeAlso = RDFUtils.getFirstPropertyValue(r, RDFUtils.SEEALSO);
        }
        return action;
    }

    @Override
    public String toString() {
        String s = "";
        StmtIterator eit = coreModel.listStatements();
        while (eit.hasNext()) {
            Statement oc = (Statement) eit.next();
            s += oc;
        }
        return s;
    }

    /**
     * Retrieves a reduced set of actions for demonstration purposes
     */
    public static List<Action> getBasicActions() {
        List<Action> actions = new ArrayList();
        Action action = new Action("http://www.w3.org/ns/odrl/2/reproduce");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/distribute");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/derive");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/copy");
        action = enrichAction(action);
        actions.add(action);

        return actions;
    }

    /**
     * Retrieves a reduced set of actions for demonstration purposes
     */
    public static List<Action> getBasicDuties() {
        List<Action> actions = new ArrayList();
        Action action = new Action("http://www.w3.org/ns/odrl/2/pay");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://creativecommons.org/ns#ShareAlike");
        action = enrichAction(action);
        actions.add(action);
        action = new Action("http://www.w3.org/ns/odrl/2/attribute");
        action = enrichAction(action);
        actions.add(action);

        return actions;
    }

    /**
     * Retrieves a reduced set of constraint for demonstration purposes
     */
    public static List<Constraint> getBasicConstraints() {
        List<Constraint> actions = new ArrayList();
        Action a = new Action();
        Constraint c = new Constraint("dateTime");
        c = enrichConstraint(c);
        actions.add(c);

        return actions;
    }

    public static void print(Model modelo) {
        RDFDataMgr.write(System.out, modelo, Lang.TURTLE);
    }

    public static boolean save(Model modelo, String filename) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
//            RDFDataMgr.write(System.out, modelo, Lang.TURTLE) ;
            RDFDataMgr.write(out, modelo, Lang.TURTLE);
            out.close();
        } catch (IOException ex) {
//            Logger.getLogger("licenser").warn("Error saving file. " + ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean save(String modelo, String filename) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(modelo);
            out.close();
        } catch (IOException ex) {
     //       Logger.getLogger("licenser").warn("Error saving file. " + ex.getMessage());
            return false;
        }
        return true;
    }

    static boolean isOfKind(Resource rrule, String uri) {
        if (rrule == null || uri == null || uri.isEmpty() || rrule.getURI() == null) {
            return false;
        }
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.add(coreModel);
        Individual res = ontModel.getIndividual(rrule.getURI());
        OntClass clase = ontModel.getOntClass(uri);
        if (res == null || clase == null) {
            return false;
        }
        return res.hasOntClass(clase);

    }

    /**
     * Returns a CreativeCommons rule from a Resource
     * @param rpolicy A Jena resource with a CreativeCommons resource
     * @return an ODRL Rule compatible 
     */
    public static Rule findCreativeCommons(Resource rpolicy) {
        List<String> permisosCC = RDFUtils.getAllPropertyStrings(rpolicy, ODRLRDF.PCCPERMISSION);
        List<String> constraintsCC = RDFUtils.getAllPropertyStrings(rpolicy, ODRLRDF.PCCREQUIRES);
        //IF HANDLING A CREATIVE COMMONS CASE
        if (!permisosCC.isEmpty()) {
            Rule rulecc = new Rule();
            rulecc.uri = "";
            for (String permisoCC : permisosCC) {
                Action actionCC = new Action(permisoCC);
                actionCC = enrichAction(actionCC);
                rulecc.actions.add(actionCC);
            }
            for (String sconstraint : constraintsCC) {
                Constraint constrainti = new Constraint(sconstraint);
                constrainti = enrichConstraint(constrainti);
                rulecc.constraint.add(constrainti);
            }
            return rulecc;
        }
        return null;
    }

    /**
     * Gets a list of resources of the given type
     * 
     * @param model Jena model
     * @param resource Given resource
     * @return List of resources
     */
    public static List<Resource> getOntResourcesOfType(Model model, Resource resource) {
        List<Resource> res = new ArrayList();
        OntModel ontModel = ModelFactory.createOntologyModel();
        
        ontModel.add(coreModel);
        ontModel.add(model);
        

        // RDFUtils.print(ontModel);

        Resource r2 = ontModel.createResource(resource.getURI());
        ExtendedIterator it = ontModel.listIndividuals(r2);
        while (it.hasNext()) {
            Individual ind = (Individual) it.next();
            
            if (ind.isResource()) {
                res.add(ind);
            }
        }
        return res;
    }

    /**
     * Obtiene todos los recursos que son de un tipo dado
     */
    public static List<Resource> getResourcesOfType(Model model, Resource resource) {
        List<Resource> policies = new ArrayList();
        ResIterator it = model.listResourcesWithProperty(RDF.type, resource);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            String objeto = s.getObject().asResource().getURI();
            Resource sujeto = s.getSubject();
            policies.add(sujeto);
        }
        return policies;
    }

    /**
     * Get 
     */
    public static List<String> getObjectsOfType(Model model, Resource resource) {
        List<String> policies = new ArrayList();
        ResIterator it = model.listResourcesWithProperty(RDF.type, resource);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            String objeto = s.getObject().asResource().getURI();
            Resource sujeto = s.getSubject();
            policies.add(sujeto.getURI());
        }
        return policies;
    }

    public static List<String> getObjectsOfType(Model model, String uri) {
        List<String> policies = new ArrayList();

        ResIterator it = model.listSubjectsWithProperty(RDF.type);
        while (it.hasNext()) {
            Resource res = it.next();
            Statement s = res.getProperty(RDF.type);
            String objeto = s.getObject().asResource().getURI();
            if (s.getObject().isURIResource()) {
                String sujeto = s.getSubject().getURI();
                String ouri = s.getObject().asResource().getURI();
                if (uri.equals(ouri)) {
                    policies.add(sujeto);
                }
            }
        }
        return policies;
    }
    
    public static List<String> getAllProperties(Resource resource) {
        List<String> list = new ArrayList();
        StmtIterator it = resource.listProperties();
        while(it.hasNext())
        {
            Statement stmt2 = it.nextStatement();
            Resource res = stmt2.getPredicate();
            RDFNode node = stmt2.getObject();
            list.add(res.toString());
        }
        return list;
    }

    /**
     * Gets the first value of a property
     */
    public static String getFirstPropertyValue(Resource resource, Property property) {
        String value = "";
        StmtIterator it = resource.listProperties(property);
        if (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            value = nodo.toString();
            return value;
        }
        return "";
    }

    /**
     * Gets all the values of the given property. It assumes that all the values are strings.
     * @param resource
     * @param property
     */
    public static List<String> getAllPropertyStrings(Resource resource, Property property) {
        List<String> cadenas = new ArrayList();
        StmtIterator it = resource.listProperties(property);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            String rtitle = nodo.toString();
            cadenas.add(rtitle);
        }
        return cadenas;
    }

    public static List<Resource> getAllPropertyResources(Resource resource, Property property) {
        List<Resource> cadenas = new ArrayList();
        StmtIterator it = resource.listProperties(property);
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            if (nodo.isResource()) {
                Resource re = (Resource) nodo;
                cadenas.add(re);
            }
        }
        return cadenas;
    }

    /**
     * Downloads a file making its best:
     * - following redirects
     * - implementing the best content negotiation
     * curl -I -L -H "Accept: application/rdf+xml" http://datos.bne.es/resource/XX947766
     */
    public static String browseSemanticWeb(String url) {
        String document = "";
        String acceptHeaderValue = "application/rdf+xml,application/turtle"; //;q=0.9,application/x-turtle;q=0.9,text/n3;q=0.8,text/turtle;q=0.8,text/rdf+n3;q=0.7,application/xml;q=0.5,text/xml;q=0.5,text/plain;q=0.4,*/*;q=0.2
        boolean redirect = false;
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestProperty("Accept", acceptHeaderValue);
            conn.setReadTimeout(5000);
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    redirect = true;
                }
            }
            if (redirect) {
                String newUrl = conn.getHeaderField("Location");
                String cookies = conn.getHeaderField("Set-Cookie");
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer html = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                html.append(inputLine);
            }
            in.close();
            document = html.toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return document;
    }

    /**
     * Parses the ontology from a text
     * @param txt Text with an ontology
     */
    public static Model parseFromText(String txt) {
        Model model = ModelFactory.createDefaultModel();
        InputStream is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8)); //StandardCharsets.UTF_8 IMPORTANT
        try {
            model.read(is, null, "RDF/XML");
            return model;
        } catch (Exception e) {
            try {
                is.close();
                is = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8)); //StandardCharsets.UTF_8
                model.read(is, null, "TURTLE");
                return model;
            } catch (Exception e2) {
                LOGGER.error("Unable to create model, result is null");
                return null;
            }
        }
    }
    public static String getLastPartOfUri(String suri) {
        try {
            URI uri = new URI(suri);
            String path = uri.getPath();
            String idStr = path.substring(path.lastIndexOf('/') + 1);
            return idStr;
        } catch (Exception e) {
            return "";
        }
    }
    public static String toRDF(Model model, String syntax) {
        try {
            StringWriter out = new StringWriter();
            model.write(out, syntax);            
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }    
}
