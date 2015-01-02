package vroddon.commons;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Victor
 */
public class FileUtils {

    //prevengo que me hagan instancias y me deriven.
    private FileUtils(){}
    
    /**
     * Cuenta las líneas en un archivo. -1 si hay un error
     */
    public static int count(String filename) {
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(filename));
            try {
                byte[] c = new byte[1024];
                int count = 0;
                int readChars = 0;
                while ((readChars = is.read(c)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (c[i] == '\n') {
                            ++count;
                        }
                    }
                }
                return count;
            } finally {
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
