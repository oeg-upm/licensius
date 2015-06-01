
package tutorial;

import static ldconditional.Main.standardInit;
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;

/**
 *
 * @author Victor
 */
public class Test1 {
    public static void main(String[] args) throws Exception {
        standardInit();
        
        ConditionalDataset cd = ConditionalDatasets.getDataset("iate");
        
        cd.getDatasetIndex().indexarSujetosStream(cd.getDatasetDump().getDataFileName(), cd.getDatasetIndex().getIndexFileName());
        
        
    }
    
}
