package oeg.jodrlapi.examples;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.odrlmodel.*;
import oeg.jodrlapi.helpers.ODRLHTML;
import org.apache.commons.io.FileUtils;

/**
 * This class shows how to write HTML from a policy.
 * @author Victor
 */
public class Example5 {
    
    /**
     * @param args No arguments are needed
     */
    public static void main(String[] args) throws Exception {
        String spolicy = "@prefix cc:    <http://creativecommons.org/ns#> . @prefix void:  <http://rdfs.org/ns/void#> . @prefix dct:   <http://purl.org/dc/terms/> . @prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . @prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> . @prefix ldr:   <http://purl.oclc.org/NET/ldr/ns#> . @prefix gr:    <http://purl.org/goodrelations/> . @prefix odrl:  <http://www.w3.org/ns/odrl/2/> . @prefix dcat:  <http://www.w3.org/ns/dcat#> . @prefix prov:  <http://www.w3.org/ns/prov#> . <http://example.com/policy0099> a odrl:Policy , odrl:Set ; rdfs:label \"policy0099\" ; odrl:permission [ a odrl:Permission ; odrl:action odrl:reproduce ; odrl:target \"http://example.com/asset9898\" ] ; odrl:prohibition [ a odrl:Prohibition ; odrl:action odrl:distribute ; odrl:target \"http://example.com/asset9898\" ] .";
        Policy policy = ODRLRDF.getPolicy(spolicy);
        String html = ODRLHTML.toHTML(policy);
        FileUtils.writeStringToFile(new File("policy.html"), html, "UTF-8"); 
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("policy.html"));
        }        
    }
}
