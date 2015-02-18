package oeg.rdf.commons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ldconditional.ConditionalDataset;
import ldconditional.ConditionalDatasets;
import ldconditional.Main;
import org.apache.log4j.Logger;

/**
 * This class represents a NQuads raw file
 * @author Victor
 */
public class NQuadRawFile {
    
    private String filename;
    private static final Logger logger = Logger.getLogger(NQuadRawFile.class);    
    
    public NQuadRawFile(String _filename)
    {
        filename= _filename;
    }


    
    
    /**
     * Gets a list with the graphs in the raw file
     */
    public List<String> getGraphs() {
        List<String> lgrafos = new ArrayList();
        Set<String> grafos = new HashSet();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                String grafo = NQuad.getGraph(line);
                if (!grafo.isEmpty())
                    grafos.add(grafo);
            }
            }catch(Exception e)
            {
                logger.warn(e.getMessage());
            }
        for(String grafo : grafos)
            lgrafos.add(grafo);
        return lgrafos;
    }
    
    /**
    *        
    */
    public void rebase(String from, String to)
    {
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename+"_rebased"));
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.replace(from, to);
                bw.write(line+"\n");
            }
            bw.close();
            br.close();
        }catch(Exception e)
        {
            logger.warn(e.getMessage());
        }
    }    
    
    public static void main(String[] args) throws Exception {
        Main.initLogger();
        
        /*ConditionalDatasets.loadDatasets();
        for(ConditionalDataset d : ConditionalDatasets.datasets)
        {
            List<String> grafos = d.getGraphs();
            for(String grafo : grafos)
            {
                System.out.println(grafo);
            }
        }*/
        NQuadRawFile raw = new NQuadRawFile("datasets/geo/data.nq");
     //   raw.rebase("http://conditional.linkeddata.es/ldr/", "http://salonica.dia.fi.upm.es/geo/");
        
        
    }
    
    
}
