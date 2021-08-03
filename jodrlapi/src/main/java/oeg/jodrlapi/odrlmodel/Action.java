package oeg.jodrlapi.odrlmodel;


//JAVA
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import oeg.jodrlapi.helpers.MetadataObject;
import java.util.UUID;
import oeg.jodrlapi.JODRLApiSettings;
import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.helpers.RDFUtils;
import static oeg.jodrlapi.helpers.RDFUtils.coreModel;

//APACHE COMMONS
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * This class represents an ODRL Action.
 * 
 * The Action entity (when related to a Permission entity) indicates the operations (e.g. play, copy, etc.) that the assignee (i.e. the consumer) 
 * is permitted to perform on the related Asset linked to by Permission. 
 * 
 * When related to a Prohibition, the Action entity indicates the operations that the assignee (again the consumer) is prohibited to perform on the Asset linked to by Prohibition. 
 * Analogously, when related to a Duty, it indicates the operation to be performed.
 * Action contains the following attribute:
 * name: indicates the Action entity term (REQUIRED)
 * As its value, the name attribute MAY take one of a set of Action names which are formally defined in profiles. 
 * 
 * The ODRL Common Vocabulary defines a standard set of potential terms that MAY be used. For example:
 * <ul>
 * <li>	http://www.w3.org/ns/odrl/2/write</li>
 * <li>	http://www.w3.org/ns/odrl/2/read</li>
 * <li>	http://www.w3.org/ns/odrl/2/distribute</li>
 * </ul>
 * 
 * Communities will develop new (or extend existing) profiles to capture additional and refined semantics.
 * @author Victor Rodriguez Doncel at OEG-UPM 2014
 */
@JsonSerialize(using = ActionSerializer.class)
public class Action extends MetadataObject {

    private List<Constraint> refinements = new ArrayList();
    
    /**
     * Action constructor with a random URI in the default namespace. 
     * By default, an action will be like this: http://salonica.dia.fi.upm.es/ldr/action/2e7de960-7001-4c07-bde5-c5ad1f35133d
     */
    public Action() {
        uri = JODRLApiSettings.ODRL_NS + "action/" + UUID.randomUUID().toString().substring(0,8);
    }

    /**
     * Cloner constructor
     */
    public Action(Action a) {
        super(a);
    }

    /**
     * Creates an action identified by the given URI. 
     * This Action might be one of those defined by ODRL, in which case label, 
     * comment, and seeAlso are loaded from the ODRL ontology
     * @param _uri URI of the action
     */
    public Action(String _uri) {
        super(_uri);
        _uri = ODRLRDF.unroll(_uri);
        setURI(_uri);
        
        if (!_uri.contains("http"))
            setURI("http://www.w3.org/ns/odrl/2/" + _uri);

    }
    
    public void addRefinement(Constraint c)
    {
        getRefinements().add(c);
    }
    public void clearRefinements()
    {
        getRefinements().clear();
    }
    
    
    /**
     * Gets all the official actions. 
     * This method requires internet connection.
     * @return List of URIs with actions
     */
    public static List<Action> getCoreODRLActions() {
        List<Action> actions = new ArrayList();
        OntClass action = coreModel.getOntClass("http://www.w3.org/ns/odrl/2/Action");
        ExtendedIterator it = action.listInstances();
        while (it.hasNext()) {
            Action a = new Action();
            Individual saction = (Individual) it.next();
            String comment = RDFUtils.getFirstPropertyValue(saction, RDFUtils.COMMENT);
            String label = RDFUtils.getFirstPropertyValue(saction, RDFUtils.LABEL);
            String definition = RDFUtils.getFirstPropertyValue(saction, RDFUtils.DEFINITION);
            String note = RDFUtils.getFirstPropertyValue(saction, RDFUtils.NOTE);
            label = label.replace("@en", "");
            
            a.uri = saction.getURI();
            a.title = label;
            a.comment = comment;
            a.note = note;
            a.definition = definition;
            actions.add(a);
        }
        return actions;
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
    
    /**
     * Gets a Jena ResourceModel from an action
     * @return A Jena action
     */
    public ResourceModel getResourceModel() {
        Model model = ModelFactory.createDefaultModel();

        //In principle anonymous
  //      Resource raction = model.createResource(JODRLApiSettings.ODRL_NS + "action/" + UUID.randomUUID().toString().substring(0,8));
        Resource raction = model.createResource(new AnonId());
        if (!this.getRefinements().isEmpty())
        {
            raction.addProperty(RDF.type, ODRLRDF.RACTION);            
            model.add(raction, ODRLRDF.PVALUE, model.createResource(this.uri.toString()));
        }
        else    //otherwise we use the URI
        {
            raction = model.createResource(this.uri.toString());
        }
        for(Constraint ref : this.getRefinements())
        {
            ResourceModel rm = ref.getResourceModel();
            model.add(rm.model);
            model.add(raction, ODRLRDF.PREFINEMENT, rm.resource);
        }
        ResourceModel resourcemodel = new ResourceModel(raction, model);        
        return resourcemodel;
    }

}
//Class intended for a cleanear JSON-LD serialization.
class ActionSerializer extends JsonSerializer<Action> 
{
    @Override
    public void serialize(Action a, JsonGenerator jgen, SerializerProvider sp) throws IOException {
        jgen.writeString(a.uri);
    }
}