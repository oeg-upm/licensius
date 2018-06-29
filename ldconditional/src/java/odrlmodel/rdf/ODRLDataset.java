package odrlmodel.rdf;

//JENA
import odrlmodel.rdf.SemanticWeb;
import odrlmodel.rdf.Recurso;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

//JAVA
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vroddon
 */
public class ODRLDataset implements Serializable, Comparable {

    public String uri;
    public String title;
    public String voiduri;
    public String sparql;
    public String description;
    public List<String> uris = new ArrayList();
    public boolean alive = false;
    public String license_id;
    public String license;
    public String license_title;
    public List<Recurso> recursos = new ArrayList();

    public String toSummaryString() {
        String s = title + "\t" + license_id + "\t" + license + "\t" + license_title + "\t" + recursos.size();
        return s;
    }

    public String toString() {
        return title;
    }

    /**
     * Convierte un Dataset a una cadena RDF en Turtle
     */
    public static Model Dataset2DCAT(ODRLDataset dataset) {

        //EN PRIMER LUGAR SE VERIFICA QUE EL DATASET TENGA UNA URI (Y SEA VÁLIDA)
        if (dataset.uri==null)
            return null;
        try {
            URI uri = new URI (dataset.uri);
        } catch (URISyntaxException ex) {
            return null;
        }
        
        Model model = ModelFactory.createDefaultModel();

        //WE DECLARE THAT THIS IS A DATASET
        Resource rmydataset = model.createResource(dataset.uri);
        rmydataset.addProperty(RDF.type, SemanticWeb.DATASET);
        if (dataset.title != null) {
            rmydataset.addProperty(SemanticWeb.TITLE, dataset.title);
        }
        if (dataset.description != null) {
            rmydataset.addProperty(SemanticWeb.DESCRIPTION, dataset.description);
        }
        
        rmydataset.addProperty(SemanticWeb.ALIVE, dataset.alive?"true":"false");

        if (dataset.license != null) {
            if (dataset.license!=null)
                rmydataset.addProperty(SemanticWeb.LICENSE, dataset.license);
        }

        for (Recurso r : dataset.recursos) {
            Model m = Recurso.toDCATModel(r, dataset.uri);
            model.add(m);
            Resource res = Recurso.getResource(m, r);
            if (res != null) {
                rmydataset.addProperty(SemanticWeb.pDISTRIBUTION, res);
            }
        }


        return model;
    }

    /**
     * From model to datasets
     */
    public static List<ODRLDataset> toDatasets(Model model) {
        String dcat = SemanticWeb.toString(model);
        List<ODRLDataset> datasets = new ArrayList();

        StmtIterator iter = model.listStatements(null, RDF.type, SemanticWeb.DATASET);
        while (iter.hasNext()) {
            ODRLDataset dataset = new ODRLDataset();
            Statement stmt = iter.nextStatement();
            Resource res = stmt.getSubject();
            dataset.uri = res.getURI();

            StmtIterator iter2 = res.listProperties();
            while (iter2.hasNext()) {
                Statement stmt2 = iter2.nextStatement();
                RDFNode nodo = stmt2.getObject();
                Property prop = stmt2.getPredicate();
                if (prop.equals(SemanticWeb.TITLE)) {
                    dataset.title = nodo.toString();
                }
                if (prop.equals(SemanticWeb.LICENSE)) {
                    dataset.license = nodo.toString();
                }
                if (prop.equals(SemanticWeb.ALIVE)) {
                    dataset.alive = nodo.toString().equals("true");
                }
                if (prop.equals(SemanticWeb.DESCRIPTION)) {
                    dataset.description = nodo.toString();
                }
                if (prop.equals(SemanticWeb.pDISTRIBUTION)) {
                    Recurso r = new Recurso();
                    Statement st=null;
                    Resource distribution = nodo.asResource();
                    r.uri=distribution.toString();
                    
                    st = distribution.getProperty(SemanticWeb.FORMAT);
                    if(st!=null)
                        r.mimetype=st.getObject().toString();
                    
                    st = distribution.getProperty(SemanticWeb.TITLE);
                    if(st!=null)
                        r.title=st.getObject().toString();
                    
                    st = distribution.getProperty(SemanticWeb.SOURCE);
                    if(st!=null)
                        r.url=st.getObject().toString();

                    st = distribution.getProperty(SemanticWeb.IDENTIFIER);
                    if(st!=null)
                        r.ckanid=st.getObject().toString();
                    
                    dataset.recursos.add(r);
                }
                
            }



            datasets.add(dataset);
        }
        return datasets;
    }

    /**
     * Testing method
     */
    public static void main(String args[]) {
        ODRLDataset ds = new ODRLDataset();
        ds.uri = "prueba";
        ds.title = "Prueba";
        Recurso r = new Recurso();
        r.uri = "rprueba";
        r.mimetype = "rdf";
        ds.recursos.add(r);

        Model model = Dataset2DCAT(ds);



        String dcat = SemanticWeb.toString(model);
 //       System.out.println(dcat);
        List<ODRLDataset> datasets = ODRLDataset.toDatasets(model);
  //      System.out.println(datasets);
        
        System.out.println("-------------------");
      
    }

    public int compareTo(Object o) {
        ODRLDataset d = (ODRLDataset)o;
        return d.uri.compareTo(uri);
    }
}
