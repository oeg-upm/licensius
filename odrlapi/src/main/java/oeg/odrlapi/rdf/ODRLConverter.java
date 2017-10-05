package oeg.odrlapi.rdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.WriterDatasetRIOT;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.util.Context;

/**
 * This class converts between syntaxes.
 * https://github.com/apache/jena/blob/master/jena-arq/src-examples/arq/examples/riot/ExJsonLD.java
 *
 * @author vroddon
 */
public class ODRLConverter {

    public static void main(String args[]) {
        for(int i=0;i<75;i++)
        {
            String testfile = String.format("http://odrlapi.appspot.com/samples/sample%03d", i);
            convertToJson(testfile);
        }
    }

    public static void convertToJson(String testfile) {
        try {
            String folder = "D:\\svn\\licensius\\odrlapi\\src\\main\\webapp\\samples";

            String rdf = new Scanner(new URL(testfile).openStream(), "UTF-8").useDelimiter("\\A").next();
//            System.out.println(rdf);
            Model model = RDFSugar.getModel(rdf);
            model.setNsPrefix("odrlapi", "http://odrlapi.appspot.com/samples/");
            model.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/");
            DatasetGraph g = DatasetFactory.create(model).asDatasetGraph();
            JsonLDWriteContext ctx = new JsonLDWriteContext();
            String atContextAsJson = "{\"@context\": [\n"
                    + "        \"http://www.w3.org/ns/odrl.jsonld\",\n"
                    + "        { \"odrl\": \"http://www.w3.org/ns/odrl/2/\" }\n"
                    + "    ]}";
            ctx.setJsonLDContext(atContextAsJson);
            String tf2=FilenameUtils.removeExtension(testfile);
            String tf3 = FilenameUtils.getName(tf2);
            tf3 = folder+"\\" + tf3;
            String ofile = tf3+".json";
            write(g, RDFFormat.JSONLD_PRETTY, ctx, ofile);

            //    model.write( System.out, "JSON-LD" );
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private static void write(DatasetGraph g, RDFFormat f, Context ctx, String ofile) {

        try {
            File initialFile = new File(ofile);
            OutputStream os = new FileOutputStream(initialFile);
            //os can also be System.out
            write(os, g, f, ctx);
        } catch (Exception e) {
            System.out.println("Error with " + ofile);
        }
    }

    /**
     * Write RDF data as JSON-LD.
     *
     * To get more control about the output, we have to use a mechanism provided
     * by jena to pass information to the writing process (cf.
     * org.apache.jena.riot.WriterDatasetRIOT and the "Context" mechanism). For
     * that, we have to create a WriterDatasetRIOT (a one-time use object) and
     * we pass a "Context" object (not to be confused with the "@context" in
     * JSON-LD) as argument to its write method
     *
     * @param out
     * @param f RDFFormat of the output, eg. RDFFormat.JSONLD_COMPACT_PRETTY
     * @param g the data that we want to output as JSON-LD
     * @param ctx the object that allows to control the writing process (a set
     * of parameters)
     */
    public static void write(OutputStream out, DatasetGraph g, RDFFormat f, Context ctx) {
        // create a WriterDatasetRIOT with the RDFFormat
        WriterDatasetRIOT w = RDFDataMgr.createDatasetWriter(f);
        PrefixMap pm = RiotLib.prefixMap(g);
        String base = null;
        w.write(out, g, pm, base, ctx);
    }

    String sample = "{\n"
            + "\"@context\": [\n"
            + "        \"http://www.w3.org/ns/odrl.jsonld\",\n"
            + "        { \"odrl\": \"http://www.w3.org/ns/odrl/2/\" }\n"
            + "    ],\n"
            + "    \"@type\": \"odrl:Set\",\n"
            + "    \"@id\": \"http://example.com/policy:1010\",\n"
            + "    \"permission\": [{\n"
            + "        \"target\": \"http://example.com/asset:9898\",\n"
            + "        \"action\": \"odrl:read\"\n"
            + "    }],\n"
            + "    \"prohibition\": [{\n"
            + "        \"target\": \"http://example.com/asset:9898\",\n"
            + "        \"action\": \"odrl:reproduce\"\n"
            + "    }]\n"
            + "}";

}
