package ldc.auth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents any interesting event to be logged
 * @author vroddon
 */
public class Evento {
    
    //Event queue
    public static List<Evento> cola = new ArrayList();
    
    //Accumulated profit
    public static double ingresos = 0.0;
    
    
    //Description of an event
    String descripcion ="";

    //timestamp of the event
    Date timestamp = new Date();
    
    
    /**
     * Adds a new event to the queue of to-be-logged events.
     * The timestamp of the event is automatically assigned by calling this function
     * @param desc Description of the event to be recorded
     * @param ingreso Whether there is economical value in the event
     */
    public static void addEvento(String desc, double ingreso)
    {
        Evento e = new Evento();
        e.timestamp = new Date();
        e.descripcion=desc;
        ingresos+=ingreso;
        cola.add(e);
    }

    /**
     * Gets the list of (last) events as a mere HTML description
     * @return an HTML text
     */
    public static String getEventosHTML()
    {
        String html="";
        
        html += String.format("<strong>Income so far %.2f EUR </strong><br/><hr/>\n", ingresos);
        
        int tam=cola.size();
        int conta =0;
        for(int i=tam-1; i>=0 ;i--)
        {
            Evento e=cola.get(i);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String hora = sdf.format(e.timestamp);
            html+= hora + " -- " + e.descripcion + "<br/>\n";
            conta++;
            if (conta==10)
                break;
        }
        return html;
    }
    
    
}
