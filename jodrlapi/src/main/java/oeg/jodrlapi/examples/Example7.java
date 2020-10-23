package oeg.jodrlapi.examples;

import oeg.jodrlapi.helpers.ValidatorClient;

/**
 * A local ODRL policy is validated here with an external service.
 * Client for service at http://odrlapi.appspot.com/validator.
 * @author vroddon
 */
public class Example7 {

    public static void main(String[] args) {
        String policy0 = "@prefix cc:    <http://creativecommons.org/ns#> . @prefix void:  <http://rdfs.org/ns/void#> . @prefix dct:   <http://purl.org/dc/terms/> . @prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . @prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> . @prefix ldr:   <http://purl.oclc.org/NET/ldr/ns#> . @prefix gr:    <http://purl.org/goodrelations/> . @prefix odrl:  <http://www.w3.org/ns/odrl/2/> . @prefix dcat:  <http://www.w3.org/ns/dcat#> . @prefix prov:  <http://www.w3.org/ns/prov#> . <http://example.com/policy0099> a odrl:Policy , odrl:Set ; rdfs:label \"policy0099\" ; odrl:permission [ a odrl:Permission ; odrl:action odrl:reproduce ; odrl:target \"http://example.com/asset9898\" ] ; odrl:prohibition [ a odrl:Prohibition ; odrl:action odrl:distribute ; odrl:target \"http://example.com/asset9898\" ] .";
        String result = ValidatorClient.validate(policy0);
        System.out.println(result);
    }


}
