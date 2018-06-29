package ldc.model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ldc.LdcConfig;
import static ldc.model.ConditionalDataset.logger;

//
import oeg.utils.rdf.NQuad;
import oeg.utils.rdf.NQuadRawFile;

//LOG4J
import org.apache.log4j.Logger;

/**
 *
 * @author vroddon
 */
public class DatasetDump extends NQuadRawFile {

    private static final Logger logger = Logger.getLogger(NQuadRawFile.class);
    //link back to the container
    ConditionalDataset conditionalDataset = null;
    public int ntriples=0;
    char separador = 10; //0A, \n, separador unix.
    
    public DatasetDump(ConditionalDataset cd) {
        super(LdcConfig.getDataFolder().endsWith("/") ? LdcConfig.getDataFolder() : (LdcConfig.getDataFolder()+"/") + cd.name + "/data.nq");
        conditionalDataset = cd;
    }
    //Devuelve las entradas para el indice

    public String getDataFileName()
    {
        String sfolder = LdcConfig.getDataFolder();
        if (!sfolder.endsWith("/")) sfolder+="/";
        String filename2 = sfolder + conditionalDataset.name + "/data.nq";
        return filename2;
    }    
    
    public Set<String> getGrafos() {
        Set set = new HashSet<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            while ((line = br.readLine()) != null) {
                String g = NQuad.getGraph(line);
                set.add(g);
            }
        } catch (Exception e) {
        }
        return set;
    }

    
    /**
     * ASUME QUE EL ARCHIVO DE NQUADS ESTÁ ORDENADO POR SUJETO
     */
    public static boolean crearIndiceSujetos(String nquadsFilename, String indexFilename) {
        Map<String, List<Integer>> map = new HashMap();
        try {
            FileOutputStream fos = new FileOutputStream(indexFilename);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));            
            BufferedReader br = new BufferedReader(new FileReader(nquadsFilename));
            
            int i = -1;
            String line = null;
            String lasts = "";
            int ini = 0;
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                i++;
                String s = NQuad.getSubject(line);      //rapido
                if (s.equals(lasts)) {
                    continue;
                }
                if (i == 0) {
                    lasts=s;
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
            logger.warn("Error. " + e.getMessage());
            return false;
        }
        return true;
    }    
    

    //Devuelve las entradas para el indice
    // REQUIERE QUE EL DUMP ESTE ORDENADO ALFABETICAMENTE. OJO....
    public Map<String, List<Integer>> createIndiceSujetos() {
        
        Map<String, List<Integer>> map = new HashMap();
        try {
            ntriples=0;
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            String lasts = "";
            int ini = 0;
            while ((line = br.readLine()) != null) {    ///potencialmente, 100M de lineas
                i++;
                String s = NQuad.getSubject(line);      //rapido
                if (s.equals(lasts)) {
                    continue;
                }
                if (i == 0) {
                    lasts=s;
                    continue;
                }
                List<Integer> li = new ArrayList();
                li.add(ini);
                li.add(i - 1);
                map.put(lasts, li);
                lasts = s;
                ini = i;
            }
            ntriples=i+1;
        } catch (Exception e) {
        }
        return map;
    }

    /**
     * Obtiene los números de línea en los cuales aparece una entidad bien en 
     */
    List<Integer> getLineas(String clave) {
        List list = new ArrayList<Integer>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;
            int conta = -1;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty())
                    continue;
                conta++;
                logger.trace("Leída la línea: " + conta +  " Tamaño de lista: " + list.size());
                String s = NQuad.getSubject(line);
                if (s.equals(clave)) {
                    list.add(conta);
                    continue;
                }
                String slocal = oeg.utils.strings.StringUtils.getLocalName(s);
                if (slocal.equals(clave)) {
                    list.add(conta);
                    continue;
                }

                String p = NQuad.getProperty(line);
                if (p.equals(clave)) {
                    list.add(conta);
                    continue;
                }
                String o = NQuad.getObject(line);
                if (o.equals(clave)) {
                    list.add(conta);
                    continue;
                }
                String g = NQuad.getGraph(line);
                if (g.equals(clave)) {
                    list.add(conta);
                    continue;
                }
//                String lan = NQuad.getObjectLangTag(line);

            }
        } catch (Exception e) {
            logger.warn("Error procesando la línea " + e.getMessage());
        }
        return list;
    }

    String getTextBetweenLines(Integer g0, Integer g1) {
        String str = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            int i = -1;
            String line = null;

            while ((line = br.readLine()) != null) {
                i++;
                if (i > g1) {
                    return str;
                }
                if (i >= g0) {
                    str += line + "\n";
                }
            }
        } catch (Exception e) {
            logger.debug("mal");
        }
        return str;
    }
    List<String> getNQuadsBetweenLines(Integer g0, Integer g1) {
        List<String> str = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));                
            int i = -1;
            String line = null;

            while ((line = br.readLine()) != null) {
                i++;
                if (i > g1) {
                    return str;
                }
                if (i >= g0) {
                    str.add(line);
                }
            }
        } catch (Exception e) {
            logger.debug("mal");
        }
        return str;
    }

    /**
     * Hace un replaceAll
     */
    public boolean rebase(String datasuri) {
        try {
            if (filename==null || filename.isEmpty())
            {
                filename=getDataFileName();
            }
            BufferedReader br = new BufferedReader(new FileReader(filename));
            File dest=new File(filename+"tmp");
            if(!dest.exists()) {
                dest.createNewFile();
            }            
            FileOutputStream fos = new FileOutputStream(dest);
            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));           
            
            int i = -1;
            String line = null;
            
            String context = LdcConfig.get("context", "/ldc");
            String newbase = LdcConfig.getServer();
            if (!newbase.endsWith("/"))
                newbase+="/";
            newbase+=context+"/data/"+conditionalDataset.name+"/";
            newbase = newbase.replace("//", "/");
            newbase = newbase.replace("http:/", "http://");
            
            
            if (!datasuri.endsWith("/") && !datasuri.endsWith("#"))
                datasuri+="/";
            
            logger.info("Rebasing " + datasuri + " por " + newbase);
            
            int changes=0;
            while ((line = br.readLine()) != null) {
                String line2 = line.replace(datasuri, newbase);
                if(!line.equals(line2))
                    changes++;
                char separador = 10; //0A, \n, separador unix.
                out.write(line2+separador);
            }
            logger.info("Se han realizado " + changes +" changes rebaseando el dump");
            
            out.close();
            br.close();
            File fin = new File(filename);
            boolean b1=fin.delete();
            fin = new File(filename);
            boolean b2=dest.renameTo(fin);
            if (b1==false || b2==false)
                logger.error("Error making rebase due to file permissions");
            return true;
        } catch (Exception e) {
            logger.warn("Error mientras se hacia rebase " + e.getMessage());
            return false;
        }        
    }

    void resetGraphs(String newg) {
        try {
            if (filename==null || filename.isEmpty())
            {
                filename=getDataFileName();
            }
            BufferedReader br = new BufferedReader(new FileReader(filename));
            File dest=new File(filename+"tmp");
            if(!dest.exists()) {
                dest.createNewFile();
            }            
            FileOutputStream fos = new FileOutputStream(dest);
            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));           
            int i = -1;
            String line = null;
            
            while ((line = br.readLine()) != null) {
                if (line.isEmpty())
                    continue;
                if (!NQuad.isNQuad(line))
                {
                    logger.warn("No se considera como NQUAD la siguiente línea: " + line);
                    continue;
                }
                String oldgraph = NQuad.getGraph(line);
                String newq = NQuad.createNQuad(NQuad.getSubject(line), NQuad.getProperty(line), NQuad.getObject(line), newg);
                char separador = 10; //0A, \n, separador unix.
//                line+=separador;
                out.write(newq+separador);
            }
            out.close();
            br.close();
            File fin = new File(filename);
            fin.delete();
            fin = new File(filename);
            dest.renameTo(fin);
            return;
        } catch (Exception e) {
            logger.warn("Error mientras se hacia reset grafos " + e.getMessage());
            return;
        }             
    }

    boolean match(String line, String s, String p, String o)
    {
        String ns=NQuad.getSubject(line);
        if (!s.equals("*") && !s.equals(ns))
            return false;
        
        String np=NQuad.getProperty(line);
        if (!p.equals("*") && !p.equals(np))
            return false;
        
        String no=NQuad.getObject(line);
        if (!o.equals("*") && !o.equals(no))
            return false;
        
        return true;
    }
    
    void setGraph(String s, String p, String o, String g) {
        
        try {
            if (filename==null || filename.isEmpty())
            {
                filename=getDataFileName();
            }
            BufferedReader br = new BufferedReader(new FileReader(filename));
            File dest=new File(filename+"tmp");
            if(!dest.exists()) {
                dest.createNewFile();
            }            
            FileOutputStream fos = new FileOutputStream(dest);
            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));           
            int i = -1;
            String line = null;
            
            while ((line = br.readLine()) != null) {
                boolean ok = NQuad.isNQuad(line);
                if (!ok)
                    continue;
                if (match(line, s, p, o))
                {
                    logger.info("Hit: " + line);
                    String newq = NQuad.createNQuad(NQuad.getSubject(line), NQuad.getProperty(line), NQuad.getObject(line), g);
                    out.write(newq+separador);
                }
                else
                    out.write(line+separador);
                    
            }
            out.close();
            br.close();
            File fin = new File(filename);
            fin.delete();
            fin = new File(filename);
            dest.renameTo(fin);
            return;
        } catch (Exception e) {
            logger.warn("Error mientras se hacia set grafos " + e.getMessage());
            return;
        }                     
        
    }

    void addGraph(String s, String p, String o, String g) {
        
        try {
            if (filename==null || filename.isEmpty())
            {
                filename=getDataFileName();
            }
            BufferedReader br = new BufferedReader(new FileReader(filename));
            File dest=new File(filename+"tmp");
            if(!dest.exists()) {
                dest.createNewFile();
            }            
            FileOutputStream fos = new FileOutputStream(dest);
            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));           
            int i = -1;
            String line = null;
            
            while ((line = br.readLine()) != null) {
                boolean ok = NQuad.isNQuad(line);
                if (!ok)
                    continue;
                if (match(line, s, p, o))
                {
                    logger.info("Hit: " + line);
                    String newq = NQuad.createNQuad(NQuad.getSubject(line), NQuad.getProperty(line), NQuad.getObject(line), g);
                    out.write(newq+separador);
                }
                out.write(line+separador);
                    
            }
            out.close();
            br.close();
            File fin = new File(filename);
            fin.delete();
            fin = new File(filename);
            dest.renameTo(fin);
            return;
        } catch (Exception e) {
            logger.warn("Error mientras se hacia set grafos " + e.getMessage());
            return;
        }                    }
    
}
