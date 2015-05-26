package oeg.utils;

import java.net.URI;

/**
 *
 * @author vroddon
 */
public class StringUtils {

    public static String getLocalName(String suri)
    {
        int i1 = suri.lastIndexOf("#");
        int i2 = suri.lastIndexOf("/");
        int i = Math.max(i1, i2);
        if (i==-1)
            return "";
        String label = suri.substring(i + 1, suri.length());
        return label;
    }

}
