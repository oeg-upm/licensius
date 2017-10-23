package observatorio.portals.gob;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import org.apache.jena.riot.RDFDataMgr;
import org.jsoup.Jsoup;

/**
 * Manager de datos.gob.es. Realiza consultas y obtiene estadísticas.
 *
 * @author vroddon
 */
public class GobManager {

    public static void main(String[] args) {
        GobManager g = new GobManager();
        List<String> list = g.getCommonLicenses();
        int famosas=0;
        int i=0;
        for(String url : list)
        {
            boolean ok = false;
            System.out.print(i+"\t"+url+"\t");
            
            if (url.contains("opendefinition") || url.contains("creativecommons"))
            {
                famosas++;
                System.out.print("Famosa\n");
                continue;
            }
                
            try{
                String html = Jsoup.connect(url).get().html();
                if (html.contains("desnaturalizar el sentido"))
                {
                    ok=true;
                    System.out.print("Risp"); 
                }
                else
                {
                    System.out.print("Otro"); 
                }
               System.out.print("\n"); 
            }catch(Exception e){
                System.out.print("Error\n"); 
            }
            i++;
            if (i==100)
                return;
        }
    }
    
    public static void f0(){
        System.out.println("Análisis de licencias en datos.gob.es");

        Map<String,List<String>> mapDatasetDistributions = new HashMap();
        List<String> distributions = new ArrayList();
        Map<String, String> mapDistributionLicencia = new HashMap();
                
        Map<String, String> mapDistributionDataset = new HashMap();
        Map<String, String> mapDatasetLicencia = new HashMap();
                
        Model model = GobManager.getModelFromFile();
        ResIterator ri = model.listSubjectsWithProperty(RDF.type, ModelFactory.createDefaultModel().createResource("http://www.w3.org/ns/dcat#Dataset"));
        int count=0;
        List<String> datasets = new ArrayList();
        while(ri.hasNext())
        {
            Resource r = ri.next();
   //         System.out.println(count+"\t"+r);
            datasets.add(r.toString());
            count++;
        }
        for(String dataset : datasets)
        {
           List<String> ls = new ArrayList();
     //      System.out.print(dataset+"\t");
           NodeIterator ni = model.listObjectsOfProperty( ModelFactory.createDefaultModel().createResource(dataset),ModelFactory.createDefaultModel().createProperty("http://www.w3.org/ns/dcat#distribution"));
            while(ni.hasNext())
            {
                RDFNode node = ni.next();
                if (!node.isResource())
                    continue;
                ls.add(dataset);
                String xx = node.asResource().getURI();
     //           System.out.print(xx + "\t");
                distributions.add(xx);
                mapDistributionDataset.put(xx,dataset);
            }
  //          System.out.print("\n");
            mapDatasetDistributions.put(dataset, ls);
        }
        System.out.println("Datasets: " + mapDatasetDistributions.size());
        System.out.println("Distributions: " + distributions.size());
        for(String distribution : distributions)
        {
       //     System.out.print(distribution+"\t");
           NodeIterator ni = model.listObjectsOfProperty( ModelFactory.createDefaultModel().createResource(distribution),ModelFactory.createDefaultModel().createProperty("http://purl.org/dc/terms/license"));
           while(ni.hasNext())
           {
                RDFNode node = ni.next();
                if (!node.isResource())
                    continue;
                String lic = node.asResource().getURI();
        //    System.out.print(lic+"\t");
                mapDistributionLicencia.put(distribution,lic);
                
                String dataset = mapDistributionDataset.get(distribution);
                mapDatasetLicencia.put(dataset, lic);
//                break;
           }
      //     System.out.println();
        }        
        int conta=0;
        for(String dataset : datasets)
        {
            conta++;
            String lic = mapDatasetLicencia.get(dataset);
            System.out.println(conta+"\t"+dataset+"\t"+lic);
        }
    }
    
    public List<String> getCommonLicenses()
    {
        List<String> licencias = new ArrayList();
        try
        {
            InputStream in = this.getClass().getResourceAsStream("licenciasgob.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str = "";
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, "\t");
                String num = st.nextToken();
                String url = st.nextToken();      
                licencias.add(url);
            }
        }catch(Exception e)
        {
            
        }
        return licencias;
        
    }

    public static Model getModelFromFile() {
        try {
            String archivo = "D:\\svn\\licensius\\license-observatory\\data\\datosgobes.rdf";
            
            Model model2 = RDFDataMgr.loadModel(archivo) ;
            
   //         String rdf = new Scanner(new URL(archivo).openStream(), "UTF-8").useDelimiter("\\A").next();
   //         Model model = getModel(rdf);
            return model2;
        } catch (Exception e) {
            return null;
        }
    }

    public static Model getModel(String rdf) {
        int hash = rdf.hashCode();
        Model model = null;
        if (model == null) {
            model = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
            try {
                model.read(is, null, "TURTLE");
                return model;
            } catch (Exception e) {
                try {
                    is.close();
                    is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
                    model.read(is, null, "RDF/XML");
                    return model;
                } catch (Exception e2) {

                    try {
                        is.close();
                        is = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
                        model.read(is, null, "JSON-LD");
                        System.out.println("turtle " + getString(model));
                        return model;
                    } catch (Exception e3) {

                        return null;
                    }
                }
            }
        }
        return model;
    }

    public static String getString(Model model) {
        String syntax = "TURTLE"; // also try "N-TRIPLE" and "TURTLE"
        StringWriter out = new StringWriter();
        model.write(out, syntax);
        String result = out.toString();
        return result;
    }

}
