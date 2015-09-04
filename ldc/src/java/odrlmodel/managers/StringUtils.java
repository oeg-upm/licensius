package odrlmodel.managers;

//JAVA
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//APACHE LOG4J
import org.apache.log4j.Logger;

/**
 *
 * @author Victor
 */
public class StringUtils {

    /**
     * Limpia una cadena dejándola como una secuencia de palabras separadas por espacios
     * Ejemplo
     * "Esto es un ejemplo.   De texto! de-mierda"
     * "Esto es un ejemplo de texto de mierda"
     * @param texto Texto 
     * @return palabras y espacios
     */
    public static String fromTextToWords(String texto) {
        String salida = texto;
        String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        salida = salida.replaceAll(regex, "");

//        salida = salida.replaceAll("\\W"," "); //works well in English
        salida = salida.replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑçÇ]", " "); //works well in English
        salida = salida.replaceAll(" +", " ");
        /*        int len = salida.length();
        int oldlen= salida.length();
        do{
        oldlen = len;
        salida = salida.replaceAll("  "," ");
        len = salida.length();
        }while(oldlen!=len);
         */
        salida = salida.trim();
        salida = " " + salida + " ";
        return salida;
    }

    public static int order(List<String> lista, String cadena) {
        int valor = 0;
        for (String s : lista) {
            if (s.equals(cadena)) {
                return valor;
            }
            valor++;
        }
        return -1;
    }

    public static boolean contains(List<String> lista, String cadena) {
        for (String s : lista) {
            if (s.equals(cadena)) {
                return true;
            }
        }
        return false;
    }

    public static boolean Contains(String cadena, String clave) {
        cadena = cadena.toLowerCase();
        clave = clave.toLowerCase();
        if (cadena.lastIndexOf(clave) != -1) {
            return true;
        }
        return false;
    }

    /**
     * This method reads a text file and loads it into a String
     * @param file File name
     * @return String with the text contents of the file
     */
    public static String readFileAsString(String file) {
        String s = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        } catch (Exception ex) {
            Logger.getLogger("licenser").warn(ex.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger("licenser").warn(ex.getMessage());
            }
        }
        return s;
    }

    public static int getNumberOfOccurrences(String str, String findStr) {

        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {

            lastIndex = str.indexOf(findStr, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }
        return count;
    }

    public static String readTestFile() {
        String cadena = "";
        try {
            InputStream is = StringUtils.class.getResourceAsStream("test.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = "";
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    cadena += str;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cadena;
    }
}
