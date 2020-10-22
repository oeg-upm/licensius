package oeg.jodrlapi.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import oeg.jodrlapi.helpers.ODRLRDF;
import oeg.jodrlapi.helpers.ValidatorClient;
import oeg.jodrlapi.odrlmodel.Policy;
import org.apache.jena.riot.Lang;

/**
 * A local ODRL policy is validated here with an external service
 *
 * @author vroddon
 */
public class Example7 {

    public static void main(String[] args) {
        String policy0 = "@prefix cc:    <http://creativecommons.org/ns#> . @prefix void:  <http://rdfs.org/ns/void#> . @prefix dct:   <http://purl.org/dc/terms/> . @prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . @prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> . @prefix ldr:   <http://purl.oclc.org/NET/ldr/ns#> . @prefix gr:    <http://purl.org/goodrelations/> . @prefix odrl:  <http://www.w3.org/ns/odrl/2/> . @prefix dcat:  <http://www.w3.org/ns/dcat#> . @prefix prov:  <http://www.w3.org/ns/prov#> . <http://example.com/policy0099> a odrl:Policy , odrl:Set ; rdfs:label \"policy0099\" ; odrl:permission [ a odrl:Permission ; odrl:action odrl:reproduce ; odrl:target \"http://example.com/asset9898\" ] ; odrl:prohibition [ a odrl:Prohibition ; odrl:action odrl:distribute ; odrl:target \"http://example.com/asset9898\" ] .";
        /*           List<Policy> policies = ODRLRDF.load("http://rdflicense.appspot.com/rdflicense/cc-by4.0");
           Policy policy = policies.get(0);
           String rdf=ODRLRDF.getRDF(policy,Lang.TTL);*/
        ValidatorClient.validate(policy0);
    }


}
