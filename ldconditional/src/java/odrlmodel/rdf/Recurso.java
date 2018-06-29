package odrlmodel.rdf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Representa un Recurso de la Web Semantica, con alguna descripcion
 * @author vroddon
 */
public class Recurso {

    public String uri;
    public String url;
    public String title;
    public String mimetype;
    public String description;
    public String ckanid;

    public static Model toDCATModel(Recurso recurso, String datasetname) {
        Model model = ModelFactory.createDefaultModel();
        String ruri=datasetname+"/resource/"+recurso.ckanid;
        Resource rmydataset = model.createResource(ruri);
        rmydataset.addProperty(RDF.type, SemanticWeb.DISTRIBUTION);
        if (recurso.title != null) {
            rmydataset.addProperty(SemanticWeb.TITLE, recurso.title);
        }
        if (recurso.mimetype != null) {
            rmydataset.addProperty(SemanticWeb.FORMAT, recurso.mimetype);
        }
        if (recurso.description != null) {
            rmydataset.addProperty(SemanticWeb.DESCRIPTION, recurso.description);
        }
        if (recurso.ckanid != null) {
            rmydataset.addProperty(SemanticWeb.IDENTIFIER, recurso.ckanid);
        }
        if (recurso.url != null) {
            rmydataset.addProperty(SemanticWeb.SOURCE, recurso.url);
        }
        return model;
    }

    public static Resource getResource(Model m, Recurso r) {
        ResIterator iter = m.listResourcesWithProperty(RDF.type, SemanticWeb.DISTRIBUTION);
        while (iter.hasNext()) {
            Resource res = iter.nextResource();
            return res;
        }
        return null;
    }

    /**
     * Testing method
     */
    public static void main(String args[]) {
        Recurso r = new Recurso();
        r.uri = "myuri";
        r.title = "titulo";
        r.mimetype = "mimetype";

        Model m = Recurso.toDCATModel(r,"nada");
        String s = SemanticWeb.toString(m);
        Model m2 = SemanticWeb.fromString(s);


        String s2 = SemanticWeb.toString(m);
   //     System.out.println(s);
   //     System.out.println(s2);
    }
}
