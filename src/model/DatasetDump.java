package model;

import oeg.rdf.commons.NQuadRawFile;

/**
 *
 * @author vroddon
 */
public class DatasetDump extends NQuadRawFile {

    public DatasetDump(String name)
    {
        super("datasets/"+name+"/data.nq");
    }


}
