package ldconditional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the Linked Data datasets
 * @author Victor
 */
public class ConditionalDatasets {

    public static List<ConditionalDataset> datasets = new ArrayList();

    /**
     * Loads the datasets description
     */
    public static void loadDatasets() {
        datasets.clear();
        File folder = new File("datasets");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
            if(file.isDirectory())
            {
                ConditionalDataset dataset = new ConditionalDataset(file.getName());
                dataset.addMetadata("http://purl.org/dc/terms/title",file.getName());
                datasets.add(dataset);
            }
        }
    }
    
}
