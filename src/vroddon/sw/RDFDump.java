package vroddon.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.List;

import org.apache.log4j.Logger;

import vroddon.hilos.Reportador;
import vroddon.hilos.ReportadorNull;

/**
 * Class with different functionalities to be excerpted on a RDFDump file
 * @author vroddon
 */
public class RDFDump {

    File file = null;
    static final Logger logger = Logger.getLogger(RDFDump.class);
    Reportador reportador = new ReportadorNull();

    /**
     * The constructor assumes we are parsing a large file and the given file is not open
     * @param filename Name of the file
     */
    public RDFDump(String filename) {
        file = new File(filename);
    }

    public void setReportador(Reportador rep)
    {
        reportador= rep;
    }
    
    /**
     * Determines in a fast manner if the file is NTriples or not, by reading the first line.
     * @return True if it looks like an NTriples file
     */
    boolean seemNTriples() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            br.close();
            if (line == null) {
                return false;
            }
            List<String> spo = NTriple.getSPO(line);
            if (spo != null && spo.size() == 3) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Determina si el archivo es de NTriple o no, leyendo solo la primera linea
     * @todo IMPLEMENT 
     */
    boolean seemNQuads() {
        return false;
    }

    /**
     * Counts the number of lines in the file. This is the fastest possible method.
     * @return Number of lines
     */
    public int countTriples() {
        int lines = 0;
        try {
            if (!seemNTriples() && !seemNQuads()) {
                return 0;
            }
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
            lineNumberReader.skip(Long.MAX_VALUE);
            lines = lineNumberReader.getLineNumber();
            lineNumberReader.close();
            logger.info("File " + file.getName() + " contains " + lines + " lines (probably triples)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
        return lines;
    }

    /**
     * Exports the RDFDump to a text file separating S, P, O with tabs instead of blankspaces
     * @param fileNameOutput Name of the otuput file
     */
    public void exportToCSV(String fileNameOutput) {
        BufferedWriter bw = null;
        if (!seemNTriples()) {
            return;
        }
        try {
            File file = new File(fileNameOutput);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }
        reportador.flashStatus("Counting triples...");
        int total = countTriples();
        reportador.flashStatus("Parsing " + total + " lines");
        int lines = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                List<String> triple = NTriple.getSPO(line);
                String linea = triple.get(0) + "\t" + triple.get(1) + "\t" + triple.get(2) + "\t.\n";
                bw.write(linea);
                if (lines % (1024 * 64) == 0) {
                    reportador.status("Parsed triples (thousands): " + lines / 1000 + " (" + total / 1000 + ")", 100 * lines / total);
                }
                lines++;
            }
            br.close();
        } catch (Exception e) {
            logger.warn("Error parsing line "+lines);
        }
        try {
            bw.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Generates a NTriples file with the filtered triples from input
     * @param predicates List of predicates to search for
     * @param fileNameOutput
     */
    public void filterByPredicates(List<String> predicates, String fileNameOutput) {
        BufferedWriter bw = null;
        if (!seemNTriples()) {
            return;
        }
        try {
            File file = new File(fileNameOutput);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }
        reportador.flashStatus("Counting triples...");
        int total = countTriples();
        reportador.flashStatus("Parsing " + total + " lines");
        int lines = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                try {
                    List<String> spo = NTriple.getSPO(line);
                    for (String predicate : predicates) {
                        if (spo.get(1).equals(predicate)) {
                            bw.write(line + "\n");
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Error parsing line " + lines);
                }
                if (lines % (1024 * 64) == 0) {
                    reportador.status("Parsed triples (thousands): " + lines / 1000 + " (" + total / 1000 + ")", 100 * lines / total);
                }
                lines++;
            }
            br.close();
            bw.close();
        } catch (Exception e) {
        }
    }
    
    
}
