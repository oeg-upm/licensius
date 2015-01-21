package languages;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * v?
 * @author Victor Rodriguez Doncel
 */
public class LanguageManager {
    
    public Map<String,String> mapa32 = new HashMap();
    public Map<String,String> mapa3n = new HashMap();

    /**
     * The constructor reads the name together with the iso2 and iso3 representation
     */
    public LanguageManager() {
        try {
            InputStream in = this.getClass().getResourceAsStream("languages.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str = "";
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, "\t");
                String name=st.nextToken();
                String iso3=st.nextToken();
                String iso2=st.nextToken();
                mapa32.put(iso3, iso2);
                mapa3n.put(iso3, name);
            }
        } catch (Exception e) {
        }
    }
}
