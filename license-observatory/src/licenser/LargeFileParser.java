package licenser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;

/**
 *
 * @author Victor
 */
public class LargeFileParser {

    /**
     * Método para testear la API. 
     */
    public static void main(String[] args) {
        LargeFileParser.searchPredicatesInLargeFile("data.nq","<http://purl.org/dc/terms/license>");
    }

    public static void searchPredicatesInLargeFile(String filename, String predicate) {
        int conta = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            int hit=0;
            while ((line = br.readLine()) != null) {
//                Pattern pt = Pattern.compile("(\"[^\"]*\")");
                
                final String[] t = line.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
//                final String[] t = line.split("(?<=\") *(?=\")");
                if (t.length >= 4) {
                    String s = t[0];
                    String p = t[1];
                    String o = t[2];
                    String c = t[3];
                    if (p.equals(predicate))
                    {
                        System.out.println(s+"\t"+p+"\t"+o +"\t"+c);
                        hit++;
                    }
                }
                for (final String x : t) {
//            System.out.println(x);
                }
                conta++;
                if (conta % 1000000 == 0) {
                    System.err.println(conta+"\t"+hit);
                }
                // process the line.
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
