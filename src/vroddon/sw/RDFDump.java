package vroddon.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;
import vroddon.hilos.Reportador;
import vroddon.hilos.ReportadorNull;

/**
 * Class with different functionalities to be excerpted on a RDFDump file
 * @author vroddon
 */
public class RDFDump {

    File file = null;

    /**
     * No lee el archivo
     */
    public RDFDump(String s) {
        file = new File(s);
    }


    /**
     * Determina si el archivo es de NTriple o no, leyendo solo la primera linea
     */
    boolean seemNTriples() throws FileNotFoundException, IOException {
	BufferedReader br = new BufferedReader(new FileReader(file));
	String line = br.readLine();
	br.close();
        if (line==null)
            return false;

        List<String> spo=NTriple.getSPO(line);
        if (spo!=null && spo.size()==3)
            return true;
        return false;
    }

    /**
     * Determina si el archivo es de NTriple o no, leyendo solo la primera linea
     */
    boolean seemNQuads() {
        return false;
    }

    public int countTriples() throws FileNotFoundException, IOException {
        int lines = 0;
        
        if (!seemNTriples() && !seemNQuads())
            return 0;
        
        try {
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
            lineNumberReader.skip(Long.MAX_VALUE);
            lines = lineNumberReader.getLineNumber();
            lineNumberReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException Occured" + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException Occured" + e.getMessage());
        }
        return lines;

    }
}
