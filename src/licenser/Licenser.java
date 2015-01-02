
package licenser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Victor
 */
public class Licenser {
    
    /**
     * Obtiene una lista con los predicados más usados para info de derechos
     */
    public static List<String> getRightsPredicates()
    {
        List<String> predicados = new ArrayList();
        predicados.add("http://purl.org/dc/terms/rights");
        predicados.add("http://purl.org/dc/terms/license");
        predicados.add("http://creativecommons.org/ns#license");
        predicados.add("http://www.w3.org/1999/xhtml/vocab/‎license");
        predicados.add("http://purl.org/dc/elements/1.1/license");
        predicados.add("http://purl.org/dc/elements/1.1/rights");
        return predicados;
    }
}
