package oeg.jodrlapi.odrlmodel;


//JAVA
import java.util.ArrayList;
import java.util.List;
import oeg.jodrlapi.helpers.MetadataObject;
import java.util.UUID;
import oeg.jodrlapi.JODRLApiSettings;
import oeg.jodrlapi.helpers.ODRLRDF;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

//APACHE COMMONS
//import org.apache.commons.io.FilenameUtils;

/**
 * This class represents an ODRL2.2 Party
 * The Party entity is aimed at identifying a person, group of people, or organisation. 
 * The Party MUST identify a (legal) entity that can participate in policy transactions
 * @author Victor
 */
public class Party extends MetadataObject {

    private List<PartyCollection> partOf  = new ArrayList();
    
    private List<Constraint> refinements = new ArrayList();
    
    /**
     * Party constructor with a random URI in the default namespace. 
     * By default, an party will be like this: http://salonica.dia.fi.upm.es/ldr/party/2e7de960-7001-4c07-bde5-c5ad1f35133d
     */
    public Party() {
        uri = JODRLApiSettings.ODRL_NS + "party/" + UUID.randomUUID().toString();
    }

    /**
     * Cloner constructor
     */
    public Party(Party a) {
        super(a);
    }

    /**
     * Creates an party identified by the given URI. 
     * This Party might be one of those defined by ODRL, in which case label, 
     * comment, and seeAlso are loaded from the ODRL ontology
     * @param _uri URI of the party
     */
    public Party(String _uri) {
        super(_uri);
    }    

    /**
     * @return the partOf
     */
    public List<PartyCollection> getPartOf() {
        return partOf;
    }

    /**
     * @param partOf the partOf to set
     */
    public void setPartOf(List<PartyCollection> partOf) {
        this.partOf = partOf;
    }

    /**
     * @return the refinements
     */
    public List<Constraint> getRefinements() {
        return refinements;
    }

    /**
     * @param refinements the refinements to set
     */
    public void setRefinements(List<Constraint> refinements) {
        this.refinements = refinements;
    }
    
    public void addRefinement(Constraint c)
    {
        refinements.add(c);
    }
    
    public ResourceModel getResourceModel()
    {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(new AnonId());
        if (!uri.isEmpty())
        {
            resource = model.createResource(uri.toString());
        }
        else
        {
            resource.addProperty(RDF.type, ODRLRDF.RPARTY);
        }
        
        for (Constraint c : refinements) {
            Resource rconstraint = ODRLRDF.getResourceFromConstraint(c);
            resource.addProperty(ODRLRDF.PCONSTRAINT, rconstraint);
            model.add(rconstraint.getModel());
        }
        ResourceModel rm = new ResourceModel(resource, model);
        return rm;
    }
}
