
package upm.w3cModelInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "@id",
    "@type",
    "http://www.w3.org/2000/01/rdf-schema#label",
    "http://www.w3.org/2002/07/owl#deprecated",
    "http://www.w3.org/2004/02/skos/core#definition"
})
public class Expression {

    @JsonProperty("@id")
    private String id;
    @JsonProperty("@type")
    private List<String> type = null;
    @JsonProperty("http://www.w3.org/2000/01/rdf-schema#label")
    private List<HttpWwwW3Org200001RdfSchemaLabel> httpWwwW3Org200001RdfSchemaLabel = null;
    @JsonProperty("http://www.w3.org/2002/07/owl#deprecated")
    private List<HttpWwwW3Org200207OwlDeprecated> httpWwwW3Org200207OwlDeprecated = null;
    @JsonProperty("http://www.w3.org/2004/02/skos/core#definition")
    private List<HttpWwwW3Org200402SkosCoreDefinition> httpWwwW3Org200402SkosCoreDefinition = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("@id")
    public String getId() {
        return id;
    }

    @JsonProperty("@id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("@type")
    public List<String> getType() {
        return type;
    }

    @JsonProperty("@type")
    public void setType(List<String> type) {
        this.type = type;
    }

    @JsonProperty("http://www.w3.org/2000/01/rdf-schema#label")
    public List<HttpWwwW3Org200001RdfSchemaLabel> getHttpWwwW3Org200001RdfSchemaLabel() {
        return httpWwwW3Org200001RdfSchemaLabel;
    }

    @JsonProperty("http://www.w3.org/2000/01/rdf-schema#label")
    public void setHttpWwwW3Org200001RdfSchemaLabel(List<HttpWwwW3Org200001RdfSchemaLabel> httpWwwW3Org200001RdfSchemaLabel) {
        this.httpWwwW3Org200001RdfSchemaLabel = httpWwwW3Org200001RdfSchemaLabel;
    }

    @JsonProperty("http://www.w3.org/2002/07/owl#deprecated")
    public List<HttpWwwW3Org200207OwlDeprecated> getHttpWwwW3Org200207OwlDeprecated() {
        return httpWwwW3Org200207OwlDeprecated;
    }

    @JsonProperty("http://www.w3.org/2002/07/owl#deprecated")
    public void setHttpWwwW3Org200207OwlDeprecated(List<HttpWwwW3Org200207OwlDeprecated> httpWwwW3Org200207OwlDeprecated) {
        this.httpWwwW3Org200207OwlDeprecated = httpWwwW3Org200207OwlDeprecated;
    }

    @JsonProperty("http://www.w3.org/2004/02/skos/core#definition")
    public List<HttpWwwW3Org200402SkosCoreDefinition> getHttpWwwW3Org200402SkosCoreDefinition() {
        return httpWwwW3Org200402SkosCoreDefinition;
    }

    @JsonProperty("http://www.w3.org/2004/02/skos/core#definition")
    public void setHttpWwwW3Org200402SkosCoreDefinition(List<HttpWwwW3Org200402SkosCoreDefinition> httpWwwW3Org200402SkosCoreDefinition) {
        this.httpWwwW3Org200402SkosCoreDefinition = httpWwwW3Org200402SkosCoreDefinition;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
