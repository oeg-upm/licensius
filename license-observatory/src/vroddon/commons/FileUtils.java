package vroddon.commons;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;

/**
 * Class with different useful functions
 * @author Victor
 */
public class FileUtils {

    //prevengo que me hagan instancias y me deriven.
    private FileUtils() {
    }

    /**
     * Cuenta las líneas en un archivo. -1 si hay un error
     */
    public static int count(String filename) {
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(filename));
            try {
                byte[] c = new byte[1024*1024];
                int count = 0;
                int readChars = 0;
                while ((readChars = is.read(c)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (c[i] == '\n') {
                            ++count;
                        }
                    }
                }
                if (count%1024==0)
                    System.out.println(count/1000 + " miles");
                return count;
            } finally {
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public static int countLineByLine(String filename) throws Exception
    {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int lines = 0;
        while (reader.readLine() != null){
        lines++;
                if (lines%1048576==0)
                    System.out.println(lines/1000 + " miles");
        
        }
        reader.close();        
        return lines;
    }            
            
    

    /**
     * Like before  but faster
     */
    public static int countLineNumber(String filename) {
        int lines = 0;
        try {
            File file = new File(filename);
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
            lineNumberReader.skip(Long.MAX_VALUE);
            lines = lineNumberReader.getLineNumber();
            lineNumberReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException Occured"
                    + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException Occured" + e.getMessage());
        }
        return lines;

    }
}
