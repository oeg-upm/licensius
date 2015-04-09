package ldconditional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This class represents the Linked Data datasets
 * @author Victor
 */
public class ConditionalDatasets {

    public static List<ConditionalDataset> datasets = new ArrayList();

    private static final Logger logger = Logger.getLogger(ConditionalDatasets.class);

    /**
     * Loads the datasets description
     */
    public static void loadDatasets() {
        datasets.clear();
        File folder = new File("datasets");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if(file.isDirectory())
            {
                logger.info("Dataset " + file.getName()+" is being been loaded");
                ConditionalDataset dataset = new ConditionalDataset(file.getName());
                dataset.addMetadata("http://purl.org/dc/terms/title",file.getName());
                datasets.add(dataset);
                logger.info("Dataset " + file.getName()+" has been loaded");
            }
        }
    }
    
}
