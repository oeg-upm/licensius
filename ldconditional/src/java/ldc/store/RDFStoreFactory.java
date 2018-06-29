package ldc.store;

import java.util.List;

/**
 *
 * @author vroddon
 */
public class RDFStoreFactory  {

    public static RDFStore getStore(String tipo)
    {
        return new RDFFileStore();
    }

    
}
