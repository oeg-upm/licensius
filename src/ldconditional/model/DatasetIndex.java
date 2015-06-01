package ldconditional.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.Integer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ldconditional.LDRConfig;
import ldconditional.Main;
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
    
    Map<String, Integer> mapaOffset = new HashMap<String, Integer>();
    List<String> sujetosindexados = new ArrayList();

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
        List<String> sujetos = getIndexedSujetos();
        for (String sujeto : sujetos) {
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
            mapaOffset.clear();
            BufferedReader br = new BufferedReader(new FileReader(getIndexFileName()));
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
            logger.info("Cargado diccionario de " + cd.name);
            
            //SE CALCULA LA LISTA
            sujetosindexados.clear();
            Iterator it = mapaOffset.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String s = (String) e.getKey();
                s = s.replace("oeg-iate:", "http://salonica.dia.fi.upm.es/iate/resource/");
                sujetosindexados.add(s);
            }
            
            
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
    private static void indexarSujetosStream(String nquadsFile, String indexFile) {
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
                String abreviado = lasts.replace("http://salonica.dia.fi.upm.es/geo/resource/", "oeg-geo:");
                abreviado = lasts.replace("http://salonica.dia.fi.upm.es/iate/resource/", "oeg-iate:");

                try {
                    abreviado = URLDecoder.decode(abreviado, "UTF-8");
                } catch (Exception e) {
                }
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

    public List<String> getNQuadsForSujeto(String search) {
        List<String> res = new ArrayList();
        if (mapaOffset.isEmpty()) {
            cargarIndice();
        }
        String searchi = search.replace("http://salonica.dia.fi.upm.es/iate/resource/", "oeg-iate:");
        
        System.out.println("Searching for " + search); 
        
        
        Integer k = mapaOffset.get(searchi);
        if (k==null)
            return res;
        try {
            String search2 = URLEncoder.encode(searchi, "UTF-8");
            search2 = search2.replace("oeg-iate%3A", "http://salonica.dia.fi.upm.es/iate/resource/");
            search2 = "<" + search2 + ">";
            BufferedReader br2 = new BufferedReader(new FileReader(cd.getDatasetDump().getDataFileName()));
            br2.skip(k);
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
        if (sujetosindexados.isEmpty())
            cargarIndice();
        return sujetosindexados;
    }

}
