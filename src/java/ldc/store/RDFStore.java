package ldc.store;

import java.util.List;
import ldc.model.ConditionalDataset;

/**
 *
 * @author vroddon
 */
public interface RDFStore {
    
    public List<String> getDatasets();

    public ConditionalDataset addDataset(String id);

    public String getEntity(String recurso);

    public String loadGraph(String toString);

    public List<String> listResources(String ds, int init, int ilimit, String searchFrase);
}
