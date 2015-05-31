package ldconditional.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import ldconditional.LDRConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * This class represents the Linked Data datasets
 *
 * @author Victor
 */
public class ConditionalDatasets {

    public static List<ConditionalDataset> datasets = new ArrayList();
    private static final Logger logger = Logger.getLogger(ConditionalDatasets.class);
    static ConditionalDataset defaultDataset = null;

    /**
     * Loads the datasets description. The location is extracted from a
     * configurable parameter.
     */
    public static void loadDatasets() {
        datasets.clear();
        String sfolder = LDRConfig.get("datasetsfolder", "datasets");
        File folder = new File(sfolder);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                logger.info("Dataset " + file.getName() + " is being been loaded");
                try {
                    ConditionalDataset dataset = new ConditionalDataset(file.getName());
                    if (dataset.name == null) {
                        continue;
                    }
                    datasets.add(dataset);
                    logger.info("Dataset " + file.getName() + " has been loaded");
                } catch (Exception e) {
                    logger.info("Dataset " + file.getName() + " has NOT been loaded");
                }
            }
        }
    }

    public static ConditionalDataset setSelectedDataset(String se) {
        ConditionalDataset cd = getDataset(se);
        defaultDataset = cd;
        return cd;
    }

    /**
     * Returns the selected dataset
     */
    public static ConditionalDataset getSelectedDataset() {
        if (defaultDataset == null) {
            for (ConditionalDataset cd : datasets) {
                if (cd.name.equals("geo")) {
                    defaultDataset = cd;
                }
            }

        }
        return defaultDataset;
    }

    /**
     * Obtains one preloaded dataset from its name
     */
    public static ConditionalDataset getDataset(String dataset) {
        for (ConditionalDataset cd : datasets) {
            if (cd.name.equals(dataset)) {
                return cd;
            }
        }
        return null;
    }

    public static ConditionalDataset addDataset(String datas) {
        String sfolder = LDRConfig.get("datasetsfolder", "datasets");
        if (!sfolder.endsWith("/")) {
            sfolder += "/";
        }
        sfolder += datas;
        boolean success = (new File(sfolder)).mkdirs();
        if (success) {
            ConditionalDataset cd = new ConditionalDataset(datas);
            if (cd != null) {
                datasets.add(cd);
            }
            return cd;
        } else {
            return null;
        }
    }
    
    public static boolean deleteDataset(String datas)
    {
        String sfolder = LDRConfig.get("datasetsfolder", "datasets");
        if (!sfolder.endsWith("/")) {
            sfolder += "/";
        }
        sfolder += datas;
        try {        
            FileUtils.deleteDirectory(new File(sfolder));
            loadDatasets();
            return true;
        } catch (IOException ex) {
            logger.warn("WARN WARN WARN Could not be deleted");
            return false;
        }
    }
    
}
