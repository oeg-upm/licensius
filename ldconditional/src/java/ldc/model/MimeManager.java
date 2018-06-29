package ldconditional.ldserver;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Gestor de tipos MIME
 * @author vroddon
 */
public class MimeManager {
    public static Map<String,String> mapa = new HashMap();
    /**
     * The constructor reads the name together with the iso2 and iso3 representation
     */
    public MimeManager() {
        try {
            InputStream in = this.getClass().getResourceAsStream("mimetypes.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str = "";
            mapa.clear();
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, "\t");
                String col1=st.nextToken();
                String col2=st.nextToken();
                mapa.put(col1, col2);
            }
        } catch (Exception e) {
        }
    }    
    
    
}
