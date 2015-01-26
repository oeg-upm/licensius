package vroddon.sw;

import java.io.File;

/**
 * Class with different functionalities to be excerpted on a RDFDump file
 * @author vroddon
 */
public class RDFDump {
    
    File file = null;
    
    /**
     * No lee el archivo
     */
    public RDFDump(File f)
    {
        file = f;
    }
    
    /**
     * Determina si el archivo es de NTriples o no, leyendo solo la primera linea
     */
    boolean seemNTriples()
    {
        return false;
    }

    /**
     * Determina si el archivo es de NTriples o no, leyendo solo la primera linea
     */
    boolean seemNQuads()
    {
        return false;
    }
    
}
