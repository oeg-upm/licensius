package observatorio.portals.ckan;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import observatorio.utils.WebLicenseAnalyzer;
import org.apache.commons.io.FileUtils;
import observatorio.svg.Lodcloud;
import vroddon.sw.Dataset;
import vroddon.sw.RecursoDescrito;
import vroddon.web.utils.Downloader;

/**
 * Class to test the CKAN-access methods
 * @author vroddon
 */
public class CKANtest {

    public static Map<String, String> mapa = new HashMap();


    
    
    public static void main(String[] args) {
        
        List<String> rdfdatasets = CKANExplorer.getRDFDatasets();
    }
    
public static String getLastBitFromUrl(final String url){
    // return url.replaceFirst("[^?]*/(.*?)(?:\\?.*)","$1);" <-- incorrect
    return url.replaceFirst(".*/([^/?]+).*", "$1");
}    

    public static void descargaMasiva() {
        List<String> sds = CKANDatasets.getPreloadedDatasets("/ckan/llod2014cloud.txt");//lod2014cloud.txt
        boolean ok=false;
        ExecutorService executor = Executors.newFixedThreadPool(25);        
        for (String sd : sds) {
            sd = sd.replace("http://datahub.io/dataset/", "");
            /*Dataset ds = CKANExplorer.getDatasetFromCKAN(sd);
            for(RecursoDescrito rd : ds.recursos)
            {
                rd.
            }*/
            
            List<String> files = CKANExplorer.getRDFFilesFromCKAN(sd);
            for (String url : files) {
                System.out.println("Downloading " + url);
                String filename=getLastBitFromUrl(url);
                if (filename.equals("senses.rdf.zip"))
                    ok=true;
                if (!ok)continue;

//                File f = new File("./data/"+filename);
                try{
                    Downloader d = new Downloader();
                    d.setUrl(url.toString());
                    d.setArchivo("./data/"+filename);
                    executor.execute(d);
                    
                boolean sok = Downloader.Descargar(url.toString(), "./data/"+filename);
                if (sok)
                    System.out.println("Downloaded " + filename);
                }catch(Exception e)
                {
                    System.out.println("Failed to download " + filename);
                }

             //   WebLicenseAnalyzer.downloadRDFfromHTML(file);
             //   System.out.println(file);
            }
        }
    }

    /**
     * Loads the datsets from a folder and checks its license_id, filling in the field "tipo". 
     */
    public static List<Dataset> fillWithKindOfLicense() {
        List<Dataset> lds = CKANDatasets.getLocalDatasets("local/ckan");
        List<Dataset> ret = new ArrayList();
        for (Dataset d : lds) {
            String tipo = "";
            if (d.license_id == null || d.license_id.equals("null") || d.license_id.equals("notspecified") || d.license_id.equals("")) {
                tipo = "notspecified";
            } else if (d.license_id.equals("other-pd") || d.license_id.equals("cc-zero") || d.license_id.equals("odc-pddl")) {
                tipo = "publicdomain";
            } else if (d.license_id.equals("cc-by") || d.license_id.equals("odc-by") || d.license_id.equals("other-at") || d.license_id.equals("apache")) {
                tipo = "attribution";
            } else if (d.license_id.equals("cc-by-sa") || d.license_id.equals("gfdl") || d.license_id.equals("odc-odbl") || d.license_id.equals("uk-ogl")) {
                tipo = "sharealike";
            } else if (d.license_id.equals("cc-nc") || d.license_id.equals("gpl-2.0") || d.license_id.equals("gpl-3.0") || d.license_id.equals("other-nc") || d.license_id.equals("ukcrown-withrights")) {
                tipo = "restrictions";
            } else if (d.license_id.equals("other-closed")) {
                tipo = "closed";
            } else if (d.license_id.equals("other-open") || d.license_id.equals("other")) {
                tipo = "other";
            }
            d.tipo = tipo;

            ret.add(d);
            if (d.license_id == null) {
                d.license_id = "";
            }
            //      System.out.println(d.uri +"\t"+d.license_id+"\t"+tipo);
        }
        return ret;


    }

    /**
     * Loads a list of 569 preloaded datasets with the lod as of 2014, downloading the json in the given folder
     * @param fileName File with a list of entries, one per line, with entries like "http://datahub.io/dataset/uniprot". Fille: llod201
     * @param outputFolder Por ejemplo, "local/ckan/"
     */
    public static void downloadJSONsFromCKAN(String fileName, String outputfolder) {
        List<String> sds = CKANDatasets.getPreloadedDatasets(fileName);//lod2014cloud.txt

        for (String sd : sds) {
            sd = sd.replace("http://datahub.io/dataset/", "");
            Dataset ds = CKANExplorer.getDatasetFromCKAN(sd);
            if (ds == null) {
                System.out.print(sd);
                continue;
            }
            String s = ds.uri;
            s += "\t" + ds.license_id + "\t" + ds.license_title + "\t" + ds.license;
            System.out.println(s);
            String ruta = outputfolder + ds.title;
            try {
                FileUtils.writeStringToFile(new File(ruta), ds.ckanjson);
            } catch (Exception e) {
                System.err.println("err");
                e.printStackTrace();
            }

        }

    }

    public static void f1() {
        List<Dataset> datasets = CKANExplorer.getDatasetsFromCKAN(1999, true);
        for (Dataset d : datasets) {
            String s = d.uri;
            if (!d.hasTag("LinkedDataCrawl2014")) {
                continue;
            }
            s += "\t" + d.license_id + "\t" + d.license_title;
            System.out.println(s);



        }
        System.out.println(datasets.size());

    }
}
