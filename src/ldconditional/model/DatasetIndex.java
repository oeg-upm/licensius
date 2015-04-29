package ldconditional.model;

import java.io.File;
import java.util.Set;
import ldconditional.Main;

/**
 * https://github.com/rgl/elasticsearch-setup/releases
 * @author vroddon
 */
public class DatasetIndex {
    private ConditionalDataset cd = null;

    public DatasetIndex(ConditionalDataset _cd) {
        cd = _cd;
    }

    public boolean createIndex()
    {
        String filename = "datasets/" + cd.name + "/index.idx";

        DatasetDump dump = cd.getDatasetDump();
        Set<String> ls = dump.getEntradas();
        for(String s : ls)
            System.out.println(s);

        File f = new File(filename);
        if (f.exists()) {
        }
        return true;
    }

    public static void main(String[] args)
    {
        Main.standardInit();
        ConditionalDataset cd = ConditionalDatasets.getDataset("geo");
        DatasetIndex di = cd.getDatasetIndex();
        di.createIndex();
    }


}
