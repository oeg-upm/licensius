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
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import ldconditional.model.ConditionalDataset;
import ldconditional.model.ConditionalDatasets;
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
        String grafo = "<http://localhost/datasets/default> .";

        Main.standardInit();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));

//        ordenar("E:\\data\\ldconditional\\iate\\data.nq");
//          indexarSujetosStream("E:\\data\\ldconditional\\iate\\data.nq", "E:\\data\\ldconditional\\iate\\indexsujetos2.idx");
//        rebasear("E:\\data\\ldconditional\\iate\\data.nq", "E:\\data\\ldconditional\\iate\\data-salonica.nq", "http://tbx2rdf.lider-project.eu/data/iate", "http://salonica.dia.fi.upm.es/iate/resource");
//          unicodizar("E:\\data\\ldconditional\\iate\\data.nq", "E:\\data\\ldconditional\\iate\\datau.nq");
//          cortar("E:\\data\\ldconditional\\iate\\data.nq", "E:\\data\\ldconditional\\iate\\data2.nq", 10000000);
//        ultraFastAccess("E:\\data\\ldconditional\\iate\\data.nq", "E:\\data\\ldconditional\\iate\\indexsujetos2.idx", "oeg-iate:\"\"\"πράσινο\"\" κτήριο\"-el" );
        limpiarSaltoLinea();
        System.out.println(dateFormat.format(new Date()));

    }

    public static void limpiarSaltoLinea() {
        for (ConditionalDataset cd : ConditionalDatasets.datasets) {
            System.out.println("Limpiando " + cd.name);
            String f = cd.getDatasetDump().getFileName();
            replaceall(f, f, "", "");

        }
    }

    public static int replaceall(String sfile1, String sfile2, String cad1, String cad2) {
        boolean same = false;
        int count = 0;
        if (sfile1.equals(sfile2)) {
            same = true;
            sfile2 = sfile1 + "TMP";
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(sfile1));
            FileOutputStream fos = new FileOutputStream(new File(sfile2));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            String line;
            while ((line = br.readLine()) != null) {
                count++;
                if (count % 1000000 == 0) {
                    System.out.println("Lineas cargadas: " + count);
                }
                line = line.replace(cad1, cad2);
                char separador = 10; //0a
                bw.write(line + separador);
            }
            bw.close();
            fos.close();
            br.close();
            if (same) {
                File f1 = new File(sfile1);
                f1.delete();
                File f2 = new File(sfile2);
                f2.renameTo(new File(sfile1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public static void cortar(String sfile1, String sfile2, int lineas) {
        if (sfile1.equals(sfile2)) {
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(sfile1));
            FileOutputStream fos = new FileOutputStream(new File(sfile2));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                count++;
                if (count > lineas) {
                    break;
                }
                if (count % 1000000 == 0) {
                    System.out.println("Lineas cargadas: " + count);
                }
                bw.write(line + "\n");
            }
            bw.close();
            fos.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unicodizar(String sfile1, String sfile2) {
        if (sfile1.equals(sfile2)) {
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(sfile1));
            FileOutputStream fos = new FileOutputStream(new File(sfile2));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                count++;
                if (count % 1000000 == 0) {
                    System.out.println("Lineas cargadas: " + count);
                }
                try {
                    line = URLDecoder.decode(line, "UTF-8");
                } catch (Exception e) {
                }
                bw.write(line + "\n");
            }
            bw.close();
            fos.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String ultraFastAccess(String nquadsfile, String indexFile, String search) {
        String res = "";
        Map<String, Integer> mapaOffset = new HashMap<String, Integer>();
        try {
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(indexFile));
            System.out.println("Cargando diccionario");
            int i = 0;
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                int ind = line.lastIndexOf(" ");
                String s1 = line.substring(0, ind);
                String s2 = line.substring(ind + 1, line.length());
                if (s2.isEmpty()) {
                    break;
                }
                int indice = Integer.parseInt(s2);
                mapaOffset.put(s1, indice);
            }
            br.close();
            System.out.println("Diccionario cargado");
            Integer k = mapaOffset.get(search);

            String search2 = URLEncoder.encode(search, "UTF-8");
            search2 = search2.replace("oeg-iate%3A", "http://salonica.dia.fi.upm.es/iate/resource/");
            search2 = "<" + search2 + ">";

            System.out.println("Descodificado: " + search2);
            BufferedReader br2 = new BufferedReader(new FileReader(nquadsfile));
            br2.skip(k);
            while ((line = br2.readLine()) != null) {    ///potencialmente, 100M de lineas
                System.out.println(line);
                if (line.startsWith(search2)) {
                    res += line + "\n";
                } else {
                    break;
                }
            }

//            System.out.println("Encontrado aqui: " + k);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("--\nres\n--");

        return res;
    }

    /**
     * Indexa volcando el mapa en stream
     */
    public static void indexarSujetosStream(String nquadsFile, String indexFile) {
        int separator = 1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(nquadsFile));
            FileOutputStream fos = new FileOutputStream(new File(indexFile));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            int i = -1;
            String line = null;
            String lasts = "";
            int ini = 0;
            int contador = 0;
            int inicontador = 0;
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                contador += line.length() + separator;
                i++;
                if (i % 1000000 == 0) {
                    System.out.println("Millones de lineas cargadas: " + i / 1000000);
                }

                String s = NQuad.getSubject(line);      //rapido
                if (s.equals(lasts)) {
                    continue;
                }
                if (i == 0) {
                    lasts = s;
                    continue;
                }
//                List<Integer> li = new ArrayList();
//                li.add(ini);
//                li.add(i - 1);

//                String abreviado = lasts.replace("http://salonica.dia.fi.upm.es/geo/resource/", "local:");
                String abreviado = lasts.replace("http://salonica.dia.fi.upm.es/iate/resource/", "oeg-iate:");
                try {
                    abreviado = URLDecoder.decode(abreviado, "UTF-8");
                } catch (Exception e) {
                }
//                bw.write(abreviado+" "+ini+" "+(i-1)+"\n");
                bw.write(abreviado + " " + inicontador + "\n");
                lasts = s;
                ini = i;
                inicontador = contador - line.length() - separator;
            }
            bw.close();
            fos.close();
            br.close();

        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public static void ordenar(String filename) {
        ExternalSort.ordenar(filename);
    }

    /**
     * Indexa volcando el mapa a saco
     */
    public static void indexarSujetos(String nquadsFile, String indexFile) {
        Map<String, List<Integer>> map = new HashMap();
        try {
            BufferedReader br = new BufferedReader(new FileReader(nquadsFile));
            int i = -1;
            String line = null;
            String lasts = "";
            int ini = 0;
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                i++;
                if (i % 100000 == 0) {
                    System.out.println("Lineas cargadas: " + i);
                }

                String s = NQuad.getSubject(line);      //rapido
                if (s.equals(lasts)) {
                    continue;
                }
                if (i == 0) {
                    lasts = s;
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

    public static void nt2nq(String sfile1, String sfile2, String grafo) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(sfile1));
            FileOutputStream fos = new FileOutputStream(new File(sfile2));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            String line;
            int count = 0;

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            System.out.println(dateFormat.format(new Date()));

            while ((line = br.readLine()) != null) {
                count++;
                int lastin = line.lastIndexOf(".");
                if (lastin == -1) {
                    continue;
                }
                line = line.substring(0, lastin) + grafo;
                bw.write(line);
                bw.newLine();
                if (count % 100000 == 0) {
                    System.out.println("Lineas procesadas:" + count);
                }
            }
            bw.close();
            fos.close();
            br.close();

        } catch (Exception e) {
            System.err.println("Error. " + e.getMessage());
        }
    }

}
