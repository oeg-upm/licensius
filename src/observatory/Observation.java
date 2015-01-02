package observatory;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import licenser.EndPointExplorer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import vroddon.sw.Dataset;
import vroddon.sw.SemanticWeb;
import vroddon.sw.Vocab;

/**
 * Una observación es una lista de vocabularios y datasets con sus atributos.
 * 
 * @author vroddon
 */
public class Observation implements Serializable {


    
    public List<Vocab> vocabs = new ArrayList();
    public List<Dataset> datasets = new ArrayList();
    
    
    /**
     * Almacena las observaciones en un archivo
     */
    public static boolean saveToFile(String filename)
    {
        ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();
        try{
            FileOutputStream fout = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(Observatory.observation);    
            oos.close();
            Logger.getLogger("licenser").info("Observations saved to file " + filename);
            view.flashStatus("Observations stored to file " + filename);
            
        }catch(Exception e)
        {
            Logger.getLogger("licenser").warn("Observations could not be stored");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveToFileAsRDF(String filename)
    {
        ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();
        try{
            FileOutputStream fout = new FileOutputStream(filename);
            
            String rdf = Observatory.toRDF();
            fout.write(rdf.getBytes("UTF-8"));
            fout.close();
            Logger.getLogger("licenser").info("Observations saved to file " + filename);
            view.flashStatus("Observations stored to file " + filename);
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
        
    }

    /**
     * Carga las observaciones de un archivo
     */
    static boolean loadFromFileRDF(String filename) {
        ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();
        try{
            String s = FileUtils.readFileToString(new File(filename));
            Model m = SemanticWeb.fromString(s);
            List<Dataset> lds=Dataset.toDatasets(m);
            Collections.sort(lds);
            Observatory.observation.datasets = lds;
            Logger.getLogger("licenser").info("Observations loaded from file " + filename);
            view.flashStatus("Observations loaded from file " + filename);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public static boolean loadFromFile(String filename)
    {
        ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();
        try{
            FileInputStream fin = new FileInputStream(filename);
            ObjectInputStream oos = new ObjectInputStream(fin);
            Observatory.observation = (Observation)oos.readObject();     
            oos.close();
            Logger.getLogger("licenser").info("Observations loaded from file " + filename);
            view.flashStatus("Observations loaded from file " + filename);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }    
    
    public static boolean loadListFromFile(String filename)
    {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            
//            InputStream is = EndPointExplorer.class.getResourceAsStream(filename);
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(strLine, "\t");
                if (st.hasMoreTokens()) {
                    String vocab = st.nextToken();
                    String uri = st.nextToken();
                    Vocab v = new Vocab(vocab,uri);
                    Observatory.observation.vocabs.add(v);
                }
            }
 //           is.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
        Logger.getLogger("licenser").info("Precargados " + Observatory.observation.vocabs.size() + " vocabularios");
        return true;
    }

    void updateDataset(Dataset ds) {

        List<Dataset> lds=new ArrayList();
        for(Dataset d : datasets)
        {
            if (!d.uri.equals(ds.uri))
                lds.add(d);
            else
                lds.add(ds);
        }
        datasets=lds;
    }
    
}
