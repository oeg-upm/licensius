package ldc;

//JAVA
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//APACHE COMMONS IO
import org.apache.commons.io.FileUtils;

//LOG4J
import org.apache.log4j.Logger;

//LDC
import ldc.model.ConditionalDataset;
import ldc.store.RDFStore;
import ldc.store.RDFStoreFactory;


/**
 * Contains the singletons and main variables in the application
 * @author vroddon
 */
public class Ldc {

    public static List<ConditionalDataset> datasets = new ArrayList();
    public static RDFStore store = Ldc.store = RDFStoreFactory.getStore("filesystem");
    static final Logger logger = Logger.getLogger("ldc");

    public static ConditionalDataset getDataset(String id) {
        if (datasets.isEmpty())
            loadDatasets();
        for(ConditionalDataset cd: datasets)
        {
            if (cd.name.equals(id) || cd.getURI().equals(id))
                return cd;
        }
        return null;
    }
    public static ConditionalDataset addDataset(String id) {
        ConditionalDataset cd = store.addDataset(id);
        if (cd!=null)
            datasets.add(cd);
        return cd;

    }
    public static void loadDatasets()
    {
        List<String> ls = Ldc.store.getDatasets();
        Ldc.datasets.clear();
        for(String s : ls)
        {
            ConditionalDataset cd = new ConditionalDataset(s);
            if (cd.verify()==false)
                continue;
            Ldc.datasets.add(cd);
            logger.info("Dataset '" + s + "' has been loaded " + cd.getURI());
        }
    }
    public static boolean deleteDataset(String datas)
    {
        String sfolder = LdcConfig.get("datasetsfolder", "D:\\data\\ldc");
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

    static ConditionalDataset selected = null;
    public static ConditionalDataset getSelectedDataset() {

        
        if (selected==null)
        {
            if (datasets.isEmpty())
            {
                Ldc.loadDatasets();
                if (datasets.isEmpty())
                {
                    return null;
                }
                selected=datasets.get(0);
            }
        }
        return selected;
    }

    public static void setSelectedDataset(String name) {
        ConditionalDataset cd = Ldc.getDataset(name);
        selected=cd;
    }

}
