package vroddon.languages;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Hace de mapa de idiomas ISO 639
 * Banderitas de idiomas: http://usrz.github.io/bootstrap-languages/
 * @author Victor Rodriguez Doncel
 */
public class LanguageManager {
    
    public static Map<String,String> mapa32 = new HashMap();
    public static Map<String,String> mapa3n = new HashMap();

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


    /**
     * @param lan2 2-letters language code of the language
     * sa by bg cz dk de gr us es ee fi fr ie in hr hu id is it il jp kr lt lv mk my mt nl no pl pt ro ru sk si al rs se th tr ua vn cn
     * @return HTML code to be inserted to have a flag.
     */
    public static String getHTMLFlag(String lan2)
    {
        return "<span class=\"lang-xs\" lang=\""+ lan2+"\"></span>";        
    }

    /**
     * @param lan2 2-letters language code of the language
     * @return HTML code to be inserted to have a flag.
     */
    public static String getHTMLHeader(String lan2)
    {
        return "<link rel=\"stylesheet\" href=\"/css/languages.min.css\">";        
    }
    

}
