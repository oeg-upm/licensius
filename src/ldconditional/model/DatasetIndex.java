package ldconditional.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ldconditional.LDRConfig;
import oeg.rdf.commons.NQuad;
import oeg.utils.ExternalSort;
import org.apache.log4j.Logger;

/**
 * https://github.com/rgl/elasticsearch-setup/releases
 *
 * @author Victor
 */
public class DatasetIndex {

    private static final Logger logger = Logger.getLogger(DatasetIndex.class);
    private ConditionalDataset cd = null;
    
//    Map<String, Integer> mapaOffset = new HashMap<String, Integer>();
    List<String> mapas = new ArrayList();
    List<Long> mapai = new ArrayList();

    public DatasetIndex(ConditionalDataset _cd) {
        cd = _cd;
    }

    public String getIndexFileName() {
        String sfolder = LDRConfig.get("datasetsfolder", "datasets");
        if (!sfolder.endsWith("/")) {
            sfolder += "/";
        }
        String filename = sfolder + cd.name + "/indexsujetos.idx";
        return filename;
    }

    public void indexar() {
        ExternalSort.ordenar(cd.getDatasetDump().getDataFileName());
        //SE CALCULA EL MAPA
        indexarSujetosStream(cd.getDatasetDump().getDataFileName(), getIndexFileName());
    }

    public List<String> matchSujetos(String term) {
        List<String> ls = new ArrayList();
        for (String sujeto : mapas) {
            if (sujeto.contains(term)) {
                ls.add(sujeto);
            }
        }
        return ls;

    }

    private void cargarIndice() {
        String res = "";
        logger.info("Cargando indice de " + cd.name);
        try {
            String line = "";
            mapas.clear();
            mapai.clear();
            BufferedReader br = new BufferedReader(new FileReader(getIndexFileName()));
            long i = 0;
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                if (i%1000000==0)
                    logger.info("Millones: "+ i/1000000);
                i++;
                int ind = line.lastIndexOf(" ");
                String s1 = line.substring(0, ind);
                String s2 = line.substring(ind + 1, line.length());
                if (s2.isEmpty()) {
                    break;
                }
                long indice = Long.parseLong(s2);
//                String s = s1.replace("oeg-iate:", "http://salonica.dia.fi.upm.es/iate/resource/");
//                s1 = s1.replace("oeg-geo:","http://salonica.dia.fi.upm.es/geo/resource/" );
                mapas.add(s1);
                mapai.add(indice);
            }
            br.close();
            logger.info("Cargado diccionario de " + cd.name);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    /**
     * ***********************************************************************
     */
    /**
     * Indexa volcando el mapa en stream
     */
    public static void indexarSujetosStream(String nquadsFile, String indexFile) {
        int separator = 1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(nquadsFile));
            FileOutputStream fos = new FileOutputStream(new File(indexFile));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            String line = null;
            String lasts = "";
            long i = -1;
            long contador = 0;
            long inicontador = 0;
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
//                String abreviado = lasts.replace("http://salonica.dia.fi.upm.es/geo/resource/", "oeg-geo:");
//                abreviado = lasts.replace("http://salonica.dia.fi.upm.es/iate/resource/", "oeg-iate:");

/*                try {
                    abreviado = URLDecoder.decode(abreviado, "UTF-8");
                } catch (Exception e) {
                }*/
                bw.write(lasts + " " + inicontador + "\n");
                lasts = s;
                inicontador = contador - line.length() - separator;
            }
            bw.close();
            fos.close();
            br.close();

        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public List<String> getNQuadsForSujeto(String search) {
        List<String> res = new ArrayList();
        if (mapas.isEmpty()) {
            cargarIndice();
        }
//        String searchi = search.replace("http://salonica.dia.fi.upm.es/iate/resource/", "oeg-iate:");
//        searchi=searchi.replace("http://salonica.dia.fi.upm.es/geo/resource/", "oeg-geo:");

        int iesimo = Collections.binarySearch(mapas, search);
        if (iesimo<0)
            return res;
        Long k = mapai.get(iesimo);
        
        if (k==null)
            return res;
        try {
//            String search2 = search;
//            search = search2.replace("oeg-iate:", "http://salonica.dia.fi.upm.es/iate/resource/");
            String search2 = "<" + search + ">";
            
            FileReader fr = new FileReader(cd.getDatasetDump().getDataFileName());
            BufferedReader br2 = new BufferedReader(fr);
            br2.skip(k);        //SLOW. THIS OPERATION IS SLOW BECAUSE THE WHOLE FILE IS PARSED BEFORE REACHING THE END
            
            
            String line = "";
            while ((line = br2.readLine()) != null) {    ///potencialmente, 100M de lineas
                if (line.startsWith(search2)) {
                    res.add(line);
                } else {
                    break;
                }
            }

        } catch (Exception e) {
            logger.info("error " + e.getMessage());
        }
        return res;

    }

    public List<String> getIndexedSujetos() {
        if (mapas.isEmpty())
            cargarIndice();
        return mapas;
    }
    public int getIndexedSujetosSize() {
        if (mapas.isEmpty())
            cargarIndice();
        return mapas.size();
    }

}
