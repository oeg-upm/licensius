package ckan;

import java.util.List;
import vroddon.sw.Dataset;

/**
 * Class to test the CKAN-access methods
 * @author vroddon
 */
public class CKANtest {

    public static void main(String[] args) {
        
        List<Dataset> datasets=CKANExplorer.getDatasetsFromCKAN(1999, true);
        for(Dataset d : datasets)
        {
            String s = d.uri;
            if (!d.hasTag("LinkedDataCrawl2014"))
                continue;
             s+="\t"+d.license+"\t"+d.license_id+"\t"+d.license_title;
            System.out.println(s);
        }
        System.out.println(datasets.size());
        
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
}
