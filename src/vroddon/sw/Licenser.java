package vroddon.sw;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Victor Rodriguez Doncel
 */
public class Licenser {
    
    /**
     * Retrieves a list with the most common predicates
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
