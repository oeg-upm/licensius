package ldc;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/**
 *
 * @author vroddon
 */
public class LdcConfig {

    //Propiedades
    static Properties prop = new Properties();
    static boolean loaded = false;

    /**
     * Obtiene el valor de una propiedad, y si no lo tiene da un valor por
     * defecto
     *
     * @param p Propiedad
     * @param valor Valor por defecto
     * @return El valor leido o el de por defecto
     */
    public static String get(String p, String valor) {
        if (!loaded) {
            LoadEmbedded();
        }
        return prop.getProperty(p, valor);
    }

    /**
     * Estable el valor de una propiedad
     *
     * @param p Propiedad
     * @param valor Valor
     */
    public static void set(String p, String defvalue) {
        prop.setProperty(p, defvalue);
    }

    /**
     * Carga los parametros de configuracion del archivo contenidos.config
     *
     * @return True si todo fue bien
     */
    public static boolean LoadEmbedded() {
        try {
            InputStream is_local = LdcConfig.class.getResourceAsStream("ldc.properties");
            prop.load(is_local);
            return true;
        } catch (Exception ex2) {
            System.out.println("NO CONFIG FILE AT ALL at " + System.getProperty("user.dir") + ex2.getMessage());
            return false;
        }
    }

    public static String getDataFolder() {
        String folder = LdcConfig.get("datasetsfolder", "D:\\data\\ldc");
        return folder;
    }

}
