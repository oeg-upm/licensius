package oeg.utils.strings;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

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
    
    public static String getImage64(final BufferedImage img) {
        String encodedImage = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            baos.flush();
         //   encodedImage = Base64.getEncoder().encodeToString(baos.toByteArray());
            encodedImage = DatatypeConverter.printBase64Binary(baos.toByteArray());
            baos.close();
            encodedImage = java.net.URLEncoder.encode(encodedImage, "ISO-8859-1");
        } catch (Exception e) {
         }
        return encodedImage;
    }            

    public static List<String> superset(String cadena) {
        String separador="/";
        List<String> ls = new ArrayList();
        int k=0;
        List<Integer> hits = new ArrayList();
        for(char c : cadena.toCharArray())
        {
            if (c=='/' || c=='#')
                hits.add(k);
            k++;
        }
        for(Integer hit : hits)
        {
            String s = cadena.substring(0,hit);
            
            if (s.startsWith("http://www.w3.org"))
                continue;
            
            ls.add(s);
        }
        ls.remove("http:");
        ls.remove("http:/");
        ls.remove("https:");
        ls.remove("https:/");
        return ls;
    }
 
    public static void main(final String[] args) throws IOException {
        String x ="http://www.w3.org/ns/odrl/2/action";
        List<String> ls = superset(x);
        for(String s : ls)
        {
            System.out.println(s);
        }

    }
    
    
    public static <K, V extends Comparable<? super V>> Map<K, V>  sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return -1*(o1.getValue()).compareTo( o2.getValue() );
            }
        } );
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }    

}
