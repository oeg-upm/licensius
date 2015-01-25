package dyldo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vroddon.commons.FileUtils;
import vroddon.sw.NTriples;

/**
 * This package reads and processes data from DYLDO (http://swse.deri.org/dyldo/)
 * @author Victor
 */
public class DyldoTest {

    public static void main(String[] args) throws Exception {
//        String archivo = "local/dyldo/2015.01.11.data.nq";
//        String archivo = "local/dyldo/2012.12.30.data.nq";
//        String archivo = "e://data//dyldo//2014.01.05.data.nq";
//          String archivo = "e://data//dyldo//2014.12.05.linghub.nt";
//          String archivo = "e://data//dyldo//salida.txt";
        String archivo = "e://data//dyldo//2015.01.11.data.nq";
        parseHugeNQuads(archivo);

//        int lineas = FileUtils.countLineNumber(archivo);
//        int lineas = FileUtils.countLineByLine(archivo);
//        System.out.println("El archivo " + archivo + " tiene " + lineas + " lineas");
//        parseHugeNTriple(archivo);
    }

    /**
     * Parses a huge file 
     */
    public static void parseHugeNTriple(String filename) throws Exception {
        Map<String, Set<String>> mapa = new HashMap();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        int lines = 0;
        int langtags = 0;
        int en = 0;
        int it = 0;
        int fr = 0;
        int es = 0;
        int de = 0;
        int ru = 0;
        int pl = 0;
        int nl = 0;
        int zhtw = 0;
        int pt = 0;
        int sv = 0;
        int others = 0;
        int nulo = 0;

        int literals = 0;
        while ((line = br.readLine()) != null) {
            lines++;


            //PRESENTAMOS LA INFORMACIÓN CADA CIERTO TIEMPO
            if (lines % 1048576 == 0) {
                System.out.println(lines + "\t" + literals + "\t" + langtags + "\t" + en + "\t" + it + "\t" + fr + "\t" + es + "\t" + de + "\t" + ru + "\t" + pl + "\t" + nl + "\t" + zhtw + "\t" + pt + "\t" + sv + "\t" + others + "\t");
            }


            //OBTENEMOS OBJETO Y GRAFO
            line = line.trim();
            String object = NTriples.getObjectFromNTriple(line);


            //PROCESAMOS EL OBJETO Y SU LANGTAG
            if (object != null) {
                if (!NTriples.isLiteral(object)) {
                    continue;
                }
                literals++;
                String langtag = NTriples.getLangTag(object);
                if (langtag == null) {
                    continue;
                }

                langtags++;
                langtag = langtag.toLowerCase();
                if (langtag.equalsIgnoreCase("en") || langtag.startsWith("en-") || langtag.startsWith("en_")) {
                    en++;
                } else if (langtag.equals("it") || langtag.startsWith("it-") || langtag.startsWith("it_")) {
                    it++;
                } else if (langtag.equals("fr") || langtag.startsWith("fr-") || langtag.startsWith("fr_")) {
                    fr++;
                } else if (langtag.equals("es") || langtag.startsWith("es-") || langtag.startsWith("es_")) {
                    es++;
                } else if (langtag.equals("de") || langtag.startsWith("de-") || langtag.startsWith("de_")) {
                    de++;
                } else if (langtag.equals("ru") || langtag.startsWith("ru-") || langtag.startsWith("ru_")) {
                    ru++;
                } else if (langtag.equals("pl") || langtag.startsWith("pl-") || langtag.startsWith("pl_")) {
                    pl++;
                } else if (langtag.equals("nl") || langtag.startsWith("nl-") || langtag.startsWith("nl_")) {
                    nl++;
                } else if (langtag.equals("zw") || langtag.startsWith("zw-") || langtag.startsWith("zw_")) {
                    zhtw++;
                } else if (langtag.equals("pt") || langtag.startsWith("pt-") || langtag.startsWith("pt_")) {
                    pt++;
                } else if (langtag.equals("sv") || langtag.startsWith("sv-") || langtag.startsWith("sv_")) {
                    sv++;
                } else {
                    others++;
                }
                System.out.println(langtag);
            }


        }
        System.out.println(lines + "\t" + literals + "\t" + langtags + "\t" + en + "\t" + it + "\t" + fr + "\t" + es + "\t" + de + "\t" + ru + "\t" + pl + "\t" + nl + "\t" + zhtw + "\t" + pt + "\t" + sv + "\t" + others + "\t");

        br.close();
    }

    /**
     * Parses a huge file 
     */
    public static void parseHugeNQuads(String filename) throws Exception {
        Map<String, Set<String>> mapa = new HashMap();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        int lines = 0;
        int langtags = 0;
        int en = 0;
        int it = 0;
        int fr = 0;
        int es = 0;
        int de = 0;
        int ru = 0;
        int pl = 0;
        int nl = 0;
        int zh = 0;
        int pt = 0;
        int sv = 0;
        int others = 0;
        int nulo = 0;

        int literals = 0;
        while ((line = br.readLine()) != null) {
            lines++;


            //PRESENTAMOS LA INFORMACIÓN CADA CIERTO TIEMPO
            if (lines % 1048576 == 0) {
                int nolingual = 0;
                int monolingual = 0;
                int multilingual = 0;
                Iterator it2 = mapa.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry e = (Map.Entry) it2.next();
                    Set<String> todos = (Set<String>) e.getValue();
                    if (todos == null || todos.isEmpty()) {
                        nolingual++;
                    } else if (todos.size() == 1) {
                        monolingual++;
                    } else {
                        multilingual++;
                    }
                }
                System.out.println(lines + "\t" + literals + "\t" + langtags + "\t" + en + "\t" + it + "\t" + fr + "\t" + es + "\t" + de + "\t" + ru + "\t" + pl + "\t" + nl + "\t" + zh + "\t" + pt + "\t" + sv + "\t" + others + "\t" + nolingual + "\t" + monolingual + "\t" + multilingual + "\t");
            }


            //OBTENEMOS OBJETO Y GRAFO
            line = line.trim();
            String object = NTriples.getObjectFromNQuad(line);
            String grafo = NTriples.getGraphFromNQuad(line);

            //AÑADIMOS EL DATASET SI LO HUBIERA
            Set<String> idiomas = new HashSet();
            if (grafo != null) {
                grafo = NTriples.getPayLevelDomain(grafo);
                idiomas = mapa.get(grafo);
                if (idiomas == null) {
                    idiomas = new HashSet();
                    mapa.put(grafo, idiomas);
                }
            }

            //PROCESAMOS EL OBJETO Y SU LANGTAG
            if (object != null) {
                if (!NTriples.isLiteral(object)) {
                    continue;
                }
                literals++;
                String langtag = NTriples.getLangTag(object);
                if (langtag == null) {
                    continue;
                }
                langtags++;
                langtag = langtag.toLowerCase();
                if (langtag.equalsIgnoreCase("en") || langtag.startsWith("en-") || langtag.startsWith("en_")) {
                    en++;
                } else if (langtag.equals("it") || langtag.startsWith("it-") || langtag.startsWith("it_")) {
                    it++;
                } else if (langtag.equals("fr") || langtag.startsWith("fr-") || langtag.startsWith("fr_")) {
                    fr++;
                } else if (langtag.equals("es") || langtag.startsWith("es-") || langtag.startsWith("es_")) {
                    es++;
                } else if (langtag.equals("de") || langtag.startsWith("de-") || langtag.startsWith("de_")) {
                    de++;
                } else if (langtag.equals("ru") || langtag.startsWith("ru-") || langtag.startsWith("ru_")) {
                    ru++;
                } else if (langtag.equals("pl") || langtag.startsWith("pl-") || langtag.startsWith("pl_")) {
                    pl++;
                } else if (langtag.equals("nl") || langtag.startsWith("nl-") || langtag.startsWith("nl_")) {
                    nl++;
                } else if (langtag.equals("zw") || langtag.startsWith("zw-") || langtag.startsWith("zw_")) {
                    zh++;
                } else if (langtag.equals("pt") || langtag.startsWith("pt-") || langtag.startsWith("pt_")) {
                    pt++;
                } else if (langtag.equals("sv") || langtag.startsWith("sv-") || langtag.startsWith("sv_")) {
                    sv++;
                } else {
                    others++;
                }
                idiomas.add(langtag);
                if (grafo != null) {
                    mapa.put(grafo, idiomas);
                }
            }


        }

        int nolingual = 0;
        int monolingual = 0;
        int multilingual = 0;
        Iterator it2 = mapa.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry e = (Map.Entry) it2.next();
            Set<String> todos = (Set<String>) e.getValue();
            if (todos == null || todos.isEmpty()) {
                nolingual++;
            } else if (todos.size() == 1) {
                monolingual++;
            } else {
                multilingual++;
            }
        }
        System.out.println(lines + "\t" + literals + "\t" + langtags + "\t" + en + "\t" + it + "\t" + fr + "\t" + es + "\t" + de + "\t" + ru + "\t" + pl + "\t" + nl + "\t" + zh + "\t" + pt + "\t" + sv + "\t" + others + "\t" + nolingual + "\t" + monolingual + "\t" + multilingual + "\t");

        br.close();
    }
}
