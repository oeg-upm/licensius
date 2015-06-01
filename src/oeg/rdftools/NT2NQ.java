package oeg.rdftools;

//APACHE CLI
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
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

    public static void main(String[] args) {

        String sfile1 = "E:\\data\\iate\\iate.nt";
        String sfile2 = "E:\\data\\iate\\iate.nq";
        String grafo = "<default> .";

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
           System.out.println(dateFormat.format(new Date()));            

        } catch (Exception e) {
            System.err.println("Error. " + e.getMessage());
        }
    }

}
