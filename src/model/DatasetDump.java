package model;

import com.hp.hpl.jena.rdf.model.Statement;
import java.util.List;
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
