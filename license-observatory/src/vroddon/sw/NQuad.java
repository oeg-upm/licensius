package vroddon.sw;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Victor
 */
public class NQuad {

    /**
     * Might not be the fastest, as it uses Regular Expressions
     * It does not verify that URIs are correct URIs etc.
     */
    public static List<String> getSPOG(final String nquad) {
        String ntriplen = nquad.trim();
        List<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
        Matcher regexMatcher = regex.matcher(ntriplen);
        int count=0;
        while (regexMatcher.find()) {
            String parte=regexMatcher.group();
//            if (count<2)
//                parte = parte.substring(1, parte.length()-1);
            if (!parte.equals("."))
                matchList.add(parte);
            count++;
        }
        
        return matchList;
    }    
    
}
