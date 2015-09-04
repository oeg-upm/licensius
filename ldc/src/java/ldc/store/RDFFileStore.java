package ldc.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ldc.Ldc;
import ldc.LdcConfig;
import ldc.model.ConditionalDataset;
import org.apache.log4j.Logger;

/**
 *
 * @author vroddon
 */
public class RDFFileStore implements RDFStore {
    static final Logger logger = Logger.getLogger("ldc");

    @Override
    public List<String> getDatasets() {
        String sfolder = LdcConfig.get("datasetsfolder", "d:\\data\\ldc");
        List<String> datasets = new ArrayList();
        File folder = new File(sfolder);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                ConditionalDataset dataset = new ConditionalDataset(file.getName());
                if (dataset.name == null) {
                    logger.info("Dataset " + file.getName() + " not correct");
                    continue;
                }
                datasets.add(dataset.name);
                logger.info("Dataset " + file.getName() + " has been loaded");
            }
        }
        return datasets;
    }

    @Override
    public ConditionalDataset addDataset(String datas) {
        ConditionalDataset existente = Ldc.getDataset(datas);
        String sfolder = LdcConfig.get("datasetsfolder", "d:\\data\\ldc");
        if (!sfolder.endsWith("/")) {
            sfolder += "/";
        }
        sfolder += datas;
        boolean success = (new File(sfolder)).mkdirs();
        success = new File(sfolder).exists();
        if (success) {
            ConditionalDataset cd = new ConditionalDataset(datas);
            return cd;
        } else {
            return null;
        }        
    }

    @Override
    public String getEntity(String recurso) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String loadGraph(String toString) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> listResources(String ds, int init, int ilimit, String searchFrase) {
        //What is a resource? In principle everything. but this has to be configurable
        List<String> ls = new ArrayList();
        ls.add("http://asdf.asdf");
        return ls;
    }
    
}
