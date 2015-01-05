package ckan;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import svg.Lodcloud;
import vroddon.sw.Dataset;

/**
 * Class to test the CKAN-access methods
 * @author vroddon
 */
public class CKANtest {

    public static Map<String, String> mapa = new HashMap();

    public static void main(String[] args) {
        
        f4();
    /*    List<Dataset> lds = f3();

        for (Dataset d : lds) {
            mapa.put(d.uri, d.tipo);
        }

        Lodcloud.parse();*/

        /*
        //        Dataset ds=CKANExplorer.getDatasetFromCKAN("rdflicense");
        //        System.out.println(ds.toSummaryString());
        List<String> sdatasets = CKANExplorer.getAllDatasets();
        //       List<String> sdatasets = CKANExplorer.getDatasetsFromAGroup("LinkedDataCrawl2014");
        int conta=0;
        for (String sdataset : sdatasets) {
        Dataset ds=CKANExplorer.getDatasetFromCKAN(sdataset);
        if (!ds.hasTag("LinkedDataCrawl2014"))
        continue;
        
        
        
        System.out.println(++conta+" "+ sdataset);
        }*/


    }
  public static void f4() {
      List<String> datasets = CKANExplorer.getDatasetNamesFromTag("lod");
  }
    public static List<Dataset> f3() {
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

    public static void f2() {
        List<String> sds = CKANDatasets.getPreloadedDatasets("/ckan/lod2014cloud.txt");//lod2014cloud.txt

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
            String ruta = "local/ckan/" + ds.title;
            try {
                FileUtils.writeStringToFile(new File(ruta), ds.ckanjson);
            } catch (Exception e) {
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
