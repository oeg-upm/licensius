package oeg.jodrlapi;

/**
 * Despliegue como maven: 
 * 
 * Edito el archivo settings.xml
 * 
 * mvn deploy -Dregistry=https://maven.pkg.github.com/oeg-upm -Dtoken=TOKENQUESEA
 * @author vroddon  
 */
public class JODRLApiSettings {
    public static String ODRL_NS = "http://www.w3.org/ns/odrl/2/";
    public static String EXAMPLE_NS = "http://example.com/";
    public static String ODRL_ONTOLOGY = "https://www.w3.org/ns/odrl/2/ODRL22.ttl"; //http://w3.org/ns/odrl/2/ODRL20.rdf
    public static String CC_ONTOLOGY = "https://web.resource.org/cc/schema.rdf"; //http://web.resource.org/cc/

    public static void main(String arg[])
    {
    }
}
