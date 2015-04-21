package languages;

import java.util.HashMap;
import java.util.Map;

/**
 * Transforms chains for multiple languages
 * @author Victor
 */
public class Multilingual {

    static final Map<Integer, String> mapaes = new HashMap();
    static final Map<Integer, String> mapaen = new HashMap();
     
    static{
        mapaes.put(1,"No existe dicho recurso");
        mapaen.put(1,"Resource not found");
        
        mapaes.put(2, "Número de triples");
        mapaen.put(2, "Number of triples");

        mapaes.put(3, "Licencia");
        mapaen.put(3, "Dataset license");

        mapaes.put(4, "Cambiar a licencia");
        mapaen.put(4, "Change to license");

        mapaes.put(5, "Cambiar licencia");
        mapaen.put(5, "Change license");

        mapaes.put(6, "Descripción");
        mapaen.put(6, "Description");
        
        mapaes.put(7,"Información de pago");
        mapaen.put(7,"Limited access");
        
        mapaes.put(8, "Por triple RDF");
        mapaen.put(8, "Per RDF triple");       
        
        mapaes.put(9, "Política");
        mapaen.put(9, "Policy");       

        mapaes.put(10, "Propiedad");
        mapaen.put(10, "Property");       
        
        mapaes.put(11, "Valor");
        mapaen.put(11, "Value");
        
        
        mapaes.put(12, "Añadir");
        mapaen.put(12, "Add");
        
        mapaes.put(13, "Quitar");
        mapaen.put(13, "Remove");        

        mapaes.put(14, "Ver");
        mapaen.put(14, "View");        
        
        
        mapaes.put(19, "Oferta");
        mapaen.put(19, "Offer");            
        
    }
    
    public static String get(int idcadena, String lan)
    {
        String res= "";
        
        if (lan.equals("es"))
            res=mapaes.get(idcadena);
        if (lan.equals("en"))
            res=mapaen.get(idcadena);
    
        if (res==null) 
            res="";
        return res;
    }
    
    
    
    
}
