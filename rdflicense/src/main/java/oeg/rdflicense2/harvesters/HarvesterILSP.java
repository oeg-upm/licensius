package oeg.rdflicense2.harvesters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Class created ad-hoc to transform CLARIN licenses (described in a
 * spreadsheet) into independent ODRL files.
 *
 * @author vroddon
 */
public class HarvesterILSP {

    private static final String patron = "<http://purl.org/NET/rdflicense/ilsp/$SPDX$>\n"
            + " a       odrl:Policy ;\n"
            + " rdfs:label \"$NAME$\" ;\n"
            + " dct:source <$URL$> ;\n"
            + " dct:language <http://www.lexvo.org/page/iso639-3/eng> ;\n"
            + " dct:identifier \"$SPDX$\" ;\n";

    //process csv line by line
    public static void main(String args[]) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
        LocalDate localDate = LocalDate.now();
        String fecha = dtf.format(localDate);

        String path = "D:\\Dropbox\\Viajes\\2021.07.Atenas\\Trabajo\\clarin3.tsv";
        File file = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int linecount = 0;
            while ((line = br.readLine()) != null) {
                String spolicy = patron;
                if (++linecount == 1) {
                    continue;
                }
                // process the line.
                String[] parts = line.split(",");
                String name = parts[1];
                String url = parts[2];
                String spdx = parts[3];
                String categorias = parts[4];
                categorias = categorias.replace("'", " ");
                categorias = categorias.replace("[", "");
                categorias = categorias.replace("]", "");
                categorias = categorias.replace("  ", " ");

                String condiciones = parts[5];
                condiciones = condiciones.replace("'", " ");
                condiciones = condiciones.replace("[", "");
                condiciones = condiciones.replace("]", "");
                condiciones = condiciones.replace("  ", " ");

                String urls[] = url.split(" ");

                if (spdx.isEmpty()) {
                    continue;
                }
                System.out.println(spdx);

                List<String> duties = new ArrayList();
                String catcon[] = ArrayUtils.addAll(categorias.split(" "), condiciones.split(" "));
                for (String c : catcon) {
                    if (c.contains("requiresUserAuthentication")) {
                        duties.add("odrl-lr:authenticated");
                    }
                    if (c.contains("allowsAccessWithSignature")) {
                        duties.add("odrl-lr:certificated");
                    }
                    if (c.contains("attribution")) {
                        duties.add("cc:attribution");
                    }
                    if (c.contains("shareAlike")) {
                        duties.add("cc:shareAlike");
                    }
                }

                if (!duties.isEmpty()) {
                    spolicy += " odrl:duty [ odrl:action ";
                    for (String duty : duties) {
                        spolicy += duty + ",";
                    }
                    if (duties.size() > 1) {
                        spolicy = removeLastChar(spolicy);
                    }
                    spolicy += "] ;\n";
                }

                String outputfile = "D:\\Dropbox\\Viajes\\2021.07.Atenas\\Trabajo\\" + spdx + ".ttl";
                File fileo = new File(outputfile);
                spolicy = spolicy.replace("$NAME$", name);
                spolicy = spolicy.replace("$SPDX$", spdx);
                spolicy = spolicy.replace("$URL$", urls[0]);
                spolicy += " dct:date \"imported on " + fecha + "\" .";
                FileUtils.writeStringToFile(fileo, spolicy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }
}
