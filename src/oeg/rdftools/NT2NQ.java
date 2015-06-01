package oeg.rdftools;

//APACHE CLI
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import ldconditional.Main;
import oeg.rdf.commons.NQuad;
import oeg.utils.ExternalSort;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * Class with the methods for the fast transformation of a .NT file into a
 * .NQUAD file. This operation is made in a stream process that does not require 
 * much memory.
 *
 * @author Victor
 */
public class NT2NQ {

    static final Logger logger = Logger.getLogger(NT2NQ.class);
    
    
    public static void main(String[] args) {
        String sfile1 = "E:\\data\\iate\\iate.nt";
        String sfile2 = "E:\\data\\iate\\iate.nq";
        String grafo = "<default> .";
        
        
        Main.initLogger();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));            
        
//        ordenar("E:\\data\\ldconditional\\iate\\data.nq");
        indexarSujetos("E:\\data\\ldconditional\\iate\\data.nq", "E:\\data\\ldconditional\\iate\\indexsujetos.idx");
 
        System.out.println(dateFormat.format(new Date()));            
        
    }

    public static void ordenar(String filename)
    {
        ExternalSort.ordenar(filename);
    }
    
    
    /**
     * Indexa volcando el mapa a saco
     */
    public static void indexarSujetos(String nquadsFile, String indexFile)
    {
        Map<String, List<Integer>> map = new HashMap();
        try {
            BufferedReader br = new BufferedReader(new FileReader(nquadsFile));
            int i = -1;
            String line = null;
            String lasts = "";
            int ini = 0;
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                i++;
                if (i%100000==0)
                    System.out.println("Lineas cargadas: " + i);
                
                String s = NQuad.getSubject(line);      //rapido
                if (s.equals(lasts)) {
                    continue;
                }
                if (i == 0) {
                    lasts=s;
                    continue;
                }
                List<Integer> li = new ArrayList();
                li.add(ini);
                li.add(i - 1);
                map.put(lasts, li);
                lasts = s;
                ini = i;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }        
        try {
            FileOutputStream fos = new FileOutputStream(indexFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }        
    }
    
    
    public static void nt2nq(String sfile1, String sfile2, String grafo)
    {

        try {
            BufferedReader br = new BufferedReader(new FileReader(sfile1));
            FileOutputStream fos = new FileOutputStream(new File(sfile2));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));            
            String line;
            int count=0;
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            System.out.println(dateFormat.format(new Date()));            

            while ((line = br.readLine()) != null) {
                count++;
                int lastin = line.lastIndexOf(".");
                if (lastin==-1)
                    continue;
                line = line.substring(0, lastin)+grafo;
                bw.write(line);
                bw.newLine();
                if (count%100000==0)
                    System.out.println("Lineas procesadas:" + count);
            }
            bw.close();
            fos.close();
            br.close();

        } catch (Exception e) {
            System.err.println("Error. " + e.getMessage());
        }
    }

}
