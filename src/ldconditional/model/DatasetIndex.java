package ldconditional.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
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
//            FileReader fr=new FileReader(nquadsFile);
            FileInputStream fis = new FileInputStream(nquadsFile);
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            FileOutputStream fos = new FileOutputStream(new File(indexFile));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            String line = null;
            String lasts = "";
            long i = -1;
            long contador = 0;
            long inicontador = 0;
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                contador += line.length() + separator;
                long ltest = fis.getChannel().position();
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

    
    
     private static byte[] readFromFile(String filePath, long position, int size) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        file.seek(position);
        byte[] bytes = new byte[size];
        file.read(bytes);
        file.close();
        return bytes;
    }
 
    public List<String> getNQuadsForSujetoFAST(String search) {
        List<String> res = new ArrayList();
        if (mapas.isEmpty()) {
            cargarIndice();
        }
        int iesimo = Collections.binarySearch(mapas, search);
        if (iesimo<0)
            return res;
        Long k = mapai.get(iesimo);
        
        if (k==null)
            return res;
        try {
            String search2 = "<" + search + ">";
            
            byte chunk[]=readFromFile(cd.getDatasetDump().getDataFileName(), k, 1024*1024);
            String schunk = new String(chunk, "UTF-8");
            StringReader sr= new StringReader(schunk);
            BufferedReader br2= new BufferedReader(sr); 
            
//            FileReader fr = new FileReader(cd.getDatasetDump().getDataFileName());
//            BufferedReader br2 = new BufferedReader(fr);
//            br2.skip(k);        //SLOW. THIS OPERATION IS SLOW BECAUSE THE WHOLE FILE IS PARSED BEFORE REACHING THE END
            
            
            String line = "";
            while ((line = br2.readLine()) != null) {    
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
    
    
    public List<String> getNQuadsForSujeto(String search) {
        List<String> res = new ArrayList();
        if (mapas.isEmpty()) {
            cargarIndice();
        }
        int iesimo = Collections.binarySearch(mapas, search);
        if (iesimo<0)
            return res;
        Long k = mapai.get(iesimo);
        
        if (k==null)
            return res;
        try {
            String search2 = "<" + search + ">";

            FileInputStream fis = new FileInputStream(cd.getDatasetDump().getDataFileName());
            InputStreamReader isr=new InputStreamReader(fis);
            BufferedReader br2 = new BufferedReader(isr);
            
            
//            FileReader fr = new FileReader(cd.getDatasetDump().getDataFileName());
//            BufferedReader br2 = new BufferedReader(fr);
            br2.skip(k);        //SLOW. THIS OPERATION IS SLOW BECAUSE THE WHOLE FILE IS PARSED BEFORE REACHING THE END
            
            String line = "";
            while ((line = br2.readLine()) != null) {    
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
