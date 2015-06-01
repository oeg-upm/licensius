package ldconditional.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ldconditional.LDRConfig;
import ldconditional.Main;
import oeg.utils.ExternalSort;
import org.apache.log4j.Logger;

/**
 * https://github.com/rgl/elasticsearch-setup/releases
 * @author vroddon
 */
public class DatasetIndexOld {
    private static final Logger logger = Logger.getLogger(DatasetIndexOld.class);
    private ConditionalDataset cd = null;

    Map<String, List<Integer>> mapGrafos, mapSujetos;

    public DatasetIndexOld(ConditionalDataset _cd) {
        cd = _cd;
    }

    // "Sujeto1" --> 1, 10
    // "Sugjet2" --> 11, 14
    public Map<String, List<Integer>> createIndexSubjects() {
        logger.info("Creating graph index of dataset: " + cd.name);
        DatasetDump dump = cd.getDatasetDump();
        Map<String, List<Integer>> map  = dump.createIndiceSujetos();
        return map;
    }

    // No usando elasticsearch
    // "Grafo1" --> 12, 122,241,2142,11231
    public Map<String, List<Integer>> createIndexGrafos() {
        logger.info("Creating graph index of dataset: " + cd.name);
        Map<String, List<Integer>> map = new HashMap();
        DatasetDump dump = cd.getDatasetDump();
        Set<String> ls = dump.getGrafos();
        List<String> claves = new ArrayList();
        logger.info(ls.size()+" keys have been found");
        claves.addAll(ls);
        Collections.sort(claves);
        int i=0;
        for (String clave : claves) {
            i++;
            List<Integer> li = dump.getLineas(clave);
    //        System.out.println(clave+" "+li);
            map.put(clave, li);
        }
        return map;
    }
public Map<String, List<Integer>> readIndexGrafos()
    {
//        String filename = "datasets/" + cd.name + "/indexgrafos.idx";

            String sfolder = LDRConfig.get("datasetsfolder", "datasets");
            if (!sfolder.endsWith("/")) sfolder+="/";
            String filename = sfolder + cd.name + "/indexgrafos.idx";
//            String filename="datasets/" + ConditionalDatasets.getSelectedDataset().name + "/void.ttl";
            File f = new File(filename);
        
        
        
        HashMap<String, List<Integer>> map = null;
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
      }catch(Exception ioe)
      {
         ioe.printStackTrace();
         return map;
      }
        return map;
    }
    public Map<String, List<Integer>> readIndexSujetos()
    {
        String sfolder = LDRConfig.get("datasetsfolder", "datasets");
        if (!sfolder.endsWith("/")) sfolder+="/";
        String filename = sfolder + cd.name + "/indexsujetos.idx";
        HashMap<String, List<Integer>> map = null;
        if (!(new File(filename)).exists())
            return map;
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
      }catch(Exception ioe)
      {
         ioe.printStackTrace();
         return map;
      }
        return map;
    }

    public void writeIndexGrafos(Map<String, List<Integer>> map) {
            String sfolder = LDRConfig.get("datasetsfolder", "datasets");
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
    public void writeIndexSujetos(String filename, Map<String, List<Integer>> map) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (Exception e) {
        }
    }

    public List<Integer> getIndices(Map<String, List<Integer>> map, String term) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String clave = (String) e.getKey();
            if (clave.equals(term)) {
                List<Integer> li = (List<Integer>) e.getValue();
                return li;
            }
        }
        return new ArrayList();

    }

    public List<String> matchSujetos(String term)
    {
        List<String> ls = new ArrayList();
        List<String> sujetos = getIndexedSujetos();
        for(String sujeto : sujetos)
        {
            if (sujeto.contains(term))
                ls.add(sujeto);
        }
        return ls;

    }

    public List<String> getIndexedSujetos()
    {
        List<String> li = new ArrayList();
        if (mapSujetos==null)
            mapSujetos = readIndexSujetos();
        if (mapSujetos==null)
            return li;
            
        Iterator it = mapSujetos.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String clave = (String) e.getKey();
            li.add(clave);
        }
        return li;
    }

    public List<String> getNQuadsForSujeto(String sujeto)
    {
        List<String> out = new ArrayList();
        List<Integer> li = mapSujetos.get(sujeto);
        if (li==null || li.isEmpty())
            return out;
        List<String> ls =cd.getDatasetDump().getNQuadsBetweenLines(li.get(0), li.get(1));
        return ls;
    }
    public String buscarSujeto(String term)
    {
        List<Integer> li = mapSujetos.get(term);
        if (li==null || li.isEmpty())
            return "";
        String str="";

        str = cd.getDatasetDump().getTextBetweenLines(li.get(0), li.get(1));
        logger.info("Buscando de la " + li.get(0) + " " + li.get(1));
        System.out.println(str);
        return str;
    }

    public void indexar()
    {
//        ExternalSort.ordenar(LDRConfig.get("datasetsfolder", "datasets").endsWith("/") ? LDRConfig.get("datasetsfolder", "datasets") : (LDRConfig.get("datasetsfolder", "datasets")+"/")+cd.name+"/data.nq");
        mapGrafos = createIndexGrafos();
        writeIndexGrafos(mapGrafos);
        String sfolder = LDRConfig.get("datasetsfolder", "datasets");
        if (!sfolder.endsWith("/")) sfolder+="/";
        String filename = sfolder + cd.name + "/data.nq";
        ExternalSort.ordenar(filename);
        mapSujetos = createIndexSubjects();
        
        
        String sfolder2 = LDRConfig.get("datasetsfolder", "datasets");
        if (!sfolder2.endsWith("/")) sfolder2+="/";
        String filename2 = sfolder2 + cd.name + "/indexsujetos.idx";
        writeIndexSujetos(filename2, mapSujetos);
    }
    
    public void leerIndice()
    {
        mapGrafos = readIndexGrafos();
        mapSujetos = readIndexSujetos();
    }

    public int getIndexedTriplesPerGrafo(String grafo)
    {
        List<Integer> li =  mapGrafos.get(grafo);
        if (li==null || li.isEmpty())
            return 0;
        else
            return li.size();
    }
    public int getIndexedTriplesPerSubject(String su)
    {
        List<Integer> li =  mapSujetos.get(su);
        if (li==null || li.isEmpty())
            return 0;
        else
            return li.get(1)-li.get(0)+1;
    }

    List<String> getIndexedGrafos() {
        List<String> li = new ArrayList();
        Iterator it = mapGrafos.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String clave = (String) e.getKey();
            li.add(clave);
        }
        return li;
    }

}
