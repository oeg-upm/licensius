/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package observatory;

import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.log4j.Logger;
import vroddon.commons.StringUtils;
import vroddon.sw.Dataset;
import vroddon.sw.Modelo;
import vroddon.sw.Vocab;

/**
 *
 * @author Victor
 */
public class ObservatoryCommands {

    /**
     * Indica si un modelo carga bien o no
     * 
     */
    static boolean isAlive(Vocab v) {
        String modelo = Modelo.loadXMLRDF("cachevocab",v.uri);
        boolean ok = false;
        ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();
        if (!modelo.isEmpty()) {
            try {
                ok = Modelo.parseFromString(modelo,false);
                view.flashStatus("Modelo " + v.uri + " cargado ");
            } catch (Exception e) {
                Logger.getLogger("licenser").info("Model could not be parsed");
                view.flashStatus("Modelo " + v.uri + " NO cargado ");
                ok = false;
            }
        }
        return ok;
    }

    public static void testAliveVocabs(List<Vocab> vocabs) {
        ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();
        for(Vocab v : vocabs)
        {
            boolean ok = ObservatoryCommands.isAlive(v);
            v.alive = ok;
//            view.updateStatus("Loaded " + v.uri, true);
        }
        
    }
    public static boolean loadVocab(Vocab v)
    {
        ObservatoryView view = (ObservatoryView) ObservatoryApp.getApplication().getMainView();
        boolean ok = Modelo.parser(v.uri);
        if (!ok) {
            view.flashStatus("Error parsing the model");
            return false;
        }
        return true;        
    }

    public static boolean loadVoid(Dataset d) {
        if (d==null || d.voiduri==null || d.voiduri.isEmpty())
            return false;
        boolean ok=false;
        String modelo = Modelo.loadXMLRDF("cachevoid", d.voiduri);
        if (!modelo.isEmpty()) {
            try {
                ok = Modelo.parseFromString(modelo,true);
                if (ok==false)
                {
                     modelo = Modelo.loadTurtle("cachevoid", d.voiduri, false);
                     ok = Modelo.parseFromString(modelo,true);
                }
            } catch (Exception e) {
                Logger.getLogger("licenser").info("Model could not be parsed");
                ok = false;
            }
        }
        if (ok && (d.uri==null || d.uri.isEmpty()) )
            d.uri = Modelo.getDatasetUri();
        return ok;
    }
    
    
}
