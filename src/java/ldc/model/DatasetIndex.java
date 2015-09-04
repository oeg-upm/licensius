package ldc.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ldc.LdcConfig;
import oeg.utils.rdf.NT2NQ;
import oeg.utils.strings.ExternalSort;
import org.apache.log4j.Logger;

/**
 * Indice de sujetos mantenido manualmente.
 * https://github.com/rgl/elasticsearch-setup/releases
 * Gestiona un archivo con dos parejas: sujeto + linea_donde_se_encuentra
 * @author Victor
 */
public class DatasetIndex {

    private static final Logger logger = Logger.getLogger(DatasetIndex.class);
    private ConditionalDataset cd = null;
    List<Long> mapai = new ArrayList();
    public Map<String, List<Integer>> indexGrafos = new HashMap();

    private List<String> mapas = new ArrayList();
    

    /**
     * ConditionalDataset del cual procede
     */
    public DatasetIndex(ConditionalDataset _cd) {
        cd = _cd;
    }

    /**
     * Nombre de archivo del índice. Por ejemplo indexsujetos.idx
     */
    public String getIndexFileName() {
        String sfolder = LdcConfig.get("datasetsfolder", "D:\\data\\ldc");
        if (!sfolder.endsWith("/")) {
            sfolder += "/";
        }
        String filename = sfolder + cd.name + "/indexsujetos.idx";
        return filename;
    }

    /**
     * Ordena el archivo de dump.
     * Genera el índice en un archivo.
     * Actualiza el número de triples.
     */
    public void indexar() {
        ExternalSort.ordenar(cd.getDatasetDump().getDataFileName());
        cd.getDatasetDump().ntriples = NT2NQ.replaceInStreamFile(cd.getDatasetDump().getFileName(), cd.getDatasetDump().getFileName(), "", "");
        cd.getDatasetVoid().setProperty("http://rdfs.org/ns/void#triples", ""+cd.getDatasetDump().ntriples);
        //SE CALCULA EL MAPA
        indexarSujetosStream(cd.getDatasetDump().getDataFileName(), getIndexFileName());
    }

    /**
     * Dadoun término, indica si casa o no. Es un "contains" así que mucho mejor.
     */
    public List<String> matchSujetos(String term) {
        List<String> ls = new ArrayList();
        for (String sujeto : mapas) {
            if (sujeto.contains(term)) {
                ls.add(sujeto);
            }
        }
        return ls;

    }

    /**
     * Carga el índice de sujetos
     * 
     */
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
                if (i % 1000000 == 0) {
                    logger.info("Millones: " + i / 1000000);
                }
                i++;
                int ind = line.lastIndexOf(" ");
                String s1 = line.substring(0, ind);
                String s2 = line.substring(ind + 1, line.length());
                if (s2.isEmpty()) {
                    break;
                }
                long indice = Long.parseLong(s2);
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
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            FileOutputStream fos = new FileOutputStream(new File(indexFile));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            String line = null;
            String lasts = "";
            long i = -1;
            long contador = 0;
            long inicontador = 0;
            char separador = 10; //0a
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                byte[] b = line.getBytes(Charset.forName("UTF-8"));
                long btest = b.length + 1;
                contador += btest; //line.length()
                i++;
                if (i % 1000000 == 0) {
                    System.out.println("Millones de lineas cargadas: " + i / 1000000);
                }
                
                //ZONA QUE HA CAMBIADO RECIENTEMENTE
//                String s = NQuad.getSubject(line);      //rapido
                line = line.trim();
                int ix = line.indexOf(" ");
                if (ix==-1)
                    continue;
                String s = line.substring(0,ix);
                //FIN DE ZONA QUE HA CAMBIADO RECIENTEMENTE
                
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

    /**
     * Obtiene los NQUADS para un sujeto dado
     */
    public List<String> getNQuadsForSujeto(String search) {
        List<String> res = new ArrayList();
        if (mapas.isEmpty()) {
            cargarIndice();
        }
        if (search.startsWith("http"))
            search = "<"+search+">";
        
        int iesimo = Collections.binarySearch(mapas, search);
        if (iesimo < 0) {
            logger.warn("No se han encontrado triples para el sujeto " + search);
            return res;
        }
        Long k = mapai.get(iesimo);

        if (k == null) {
            return res;
        }
        logger.info("Index hit from line of dump: " + k);
        try {
            String search2 = search;
            if (search.startsWith(("http")))
                search2 = "<" + search + ">";

            byte chunk[] = readFromFile(cd.getDatasetDump().getDataFileName(), k, 1024 * 256); //maximo 256kb
            String schunk = new String(chunk, "UTF-8");
            StringReader sr = new StringReader(schunk);
            BufferedReader br2 = new BufferedReader(sr);

//            FileReader fr = new FileReader(cd.getDatasetDump().getDataFileName());
//            BufferedReader br2 = new BufferedReader(fr);
//            br2.skip(k);        //SLOW. THIS OPERATION IS SLOW BECAUSE THE WHOLE FILE IS PARSED BEFORE REACHING THE END
            String line = "";
            while ((line = br2.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith(search2)) {
                    res.add(line);
                } else {
                    break;
                }
            }
            br2.close();

        } catch (Exception e) {
            logger.info("error " + e.getMessage());
        }
        return res;
    }

    public List<String> getIndexedSujetos() {
        if (mapas.isEmpty()) {
            cargarIndice();
        }
        List<String> mapas2 = new ArrayList();
        for(String s : mapas)
        {
            if (s.startsWith("<"))
                mapas2.add(s.substring(1,s.length()-1));
        }
        return mapas2;
    }
    
    /**
     * Obtiene una lista de cuantos sujetos a partir de ini.
     */
    public List<String> getSujetos(int ini, int cuantos)
    {
        List<String> ss = new ArrayList();
        if (mapas.isEmpty()) {
            cargarIndice();
        }
        if (ini >= mapas.size())
            return ss;
        int fin = ini+cuantos;
        fin = Math.min(fin, mapas.size());
        for(int i=ini;i<fin;i++)
        {
            String s = mapas.get(i);
            s = s.replace("<", "");
            s = s.replace(">", "");
            ss.add(s);
        }
        return ss;
    }

    public List<String> getSujetos(int ini, int cuantos, String search, boolean onlylocal)
    {
        int fin = ini+cuantos;
        logger.info("Inicio de getSujetos " + ini +" " + cuantos+" " + search+" " + onlylocal+" ");
        List<String> ss = new ArrayList();
        if (mapas.isEmpty()) {
            cargarIndice();
        }
        List<String> mapasf = new ArrayList();
        for(String s : mapas)
        {
            boolean conly=false;
            if (onlylocal==false)
                conly=true;
            if (onlylocal==true && s.contains(cd.getURI()))
                conly=true;
            if (conly==false)
                continue;
            if (search.isEmpty() || s.contains(search))
                mapasf.add(s);
            if (mapasf.size()> (fin))
                break;
        }
        if (ini >= mapasf.size())
            return ss;
        fin = Math.min(fin, mapasf.size());
        for(int i=ini;i<fin;i++)
        {
            String s = mapasf.get(i);
            s = s.replace("<", "");
            s = s.replace(">", "");
            ss.add(s);
        }
        logger.info("Fin de getSujetos");
        return ss;
    }    
    /**
     * Crea el índice de los grafos
     */
    public Map<String, List<Integer>> createIndexGrafos() {
        logger.info("Creating graph index of dataset: " + cd.name);
        DatasetDump dump = cd.getDatasetDump();
        Set<String> ls = dump.getGrafos();
        List<String> claves = new ArrayList();
        logger.info(ls.size() + " keys have been found");
        claves.addAll(ls);
        Collections.sort(claves);
        int i = 0;
        indexGrafos.clear();
        for (String clave : claves) {
            if (clave.isEmpty())
                continue;
            i++;
            List<Integer> li = dump.getLineas(clave);
            indexGrafos.put(clave, li);
        }
        return indexGrafos;
    }

    public Map<String, List<Integer>> readIndexGrafos() {
        if (!indexGrafos.isEmpty())
            return indexGrafos;
        
        String sfolder = LdcConfig.get("datasetsfolder", "D:\\data\\ldc");
        if (!sfolder.endsWith("/")) {
            sfolder += "/";
        }
        String filename = sfolder + cd.name + "/indexgrafos.idx";
        File f = new File(filename);
        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            indexGrafos = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception ioe) {
            ioe.printStackTrace();
            return indexGrafos;
        }
        return indexGrafos;
    }
    public void writeIndexGrafos(Map<String, List<Integer>> map) {
            String sfolder = LdcConfig.get("datasetsfolder", "D:\\data\\ldc");
            if (!sfolder.endsWith("/")) sfolder+="/";
            String filename = sfolder + cd.name + "/indexgrafos.idx";
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (Exception e) {
        }
    }    
    
    /**
     * Devuelve los grafos que están indexados
     * @return Lista de URIs
     */
    public List<String> getIndexedGrafos() {
        if (indexGrafos.isEmpty())
            indexGrafos = readIndexGrafos();
        List<String> li = new ArrayList();
        Iterator it = indexGrafos.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String clave = (String) e.getKey();
            li.add(clave);
        }
        return li;
    }    
    public int getIndexedTriplesPerGrafo(String grafo)
    {
        if (indexGrafos.isEmpty())
            indexGrafos = readIndexGrafos();
        List<Integer> li =  indexGrafos.get(grafo);
        if (li==null || li.isEmpty())
            return 0;
        else
            return li.size();
    }    
    
}
