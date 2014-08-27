package odrlmodel;

//JAVA
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

//LOG4J
import org.apache.log4j.Logger;

/**
 * Reads and writes parameters in a configfile
 * 
 * El archivo es <i>"ldr.config"</i> y debe estar en la misma carpeta que el jar
 * Parámetros (ejemplo):
 * @author Victor Rodriguez Doncel
 */
public class LDRConfig {

    //Propiedades
    static Properties prop = new Properties();


    /**
     * Inserta el valor de un parámetro para un elemento consumidor.
     * @param nomparam Nombre del parámetro
     * @param idElemCons Id del elemento consumidor
     * @param valor Valor
     */
    public static void insertParamValue(String nomparam, int idElemCons, int valor)
    {
        String clave="~"+nomparam + ":" + idElemCons;
        prop.setProperty(clave,Integer.toString(valor));
    }
    
    /**
    * Obtiene el valor de un parámetro para un elemento consumidor.
    * @param nomparam Nombre del parámetro
    * @param idElemCons Identificador del elemento consumidor
    * @return Un valor numérico
    */
    public static int getParamValue(String nomparam, int idElemCons)
    {
        String clave="~"+nomparam + ":" + idElemCons;
        String s = prop.getProperty(clave);
        if (s == null)
        {
            clave="~"+nomparam + ":" + "-1"; // valor por defecto
            s = prop.getProperty(clave);            
        }
        if (s != null){
            return Integer.parseInt(s);
        }
        return Integer.MIN_VALUE; //solo si no hubiera valor por defecto
    }
    


    /**
     * Obtiene el valor de una propiedad
     * @param p Propiedad
     */
    public static String get(String p) {
        return prop.getProperty(p, "null");
    }

    /**
     * Obtiene el valor de una propiedad, y si no lo tiene da un valor por defecto
     * @param p Propiedad
     * @param valor Valor por defecto
     * @return El valor leido o el de por defecto
     */
    public static String get(String p, String valor) {
        return prop.getProperty(p, valor);
    }

    /**
     * Estable el valor de una propiedad
     * @param p Propiedad
     * @param valor Valor 
     */
    public static void set(String p, String defvalue) {
        prop.setProperty(p, defvalue);
        Store();
    }

    
    public static String getAuthorizebydefault() {
        return prop.getProperty("authorizebydefault", "true");
    }
    
    public static void setAuthorizebydefault(String numBins) {
        prop.setProperty("authorizebydefault", numBins);
        Store();
    }
    
    public static String getKeystore() {
        return prop.getProperty("keystore", "myKeyStore.jks");
    }
    
    public static void setKeystore(String numBins) {
        prop.setProperty("keystore", numBins);
        Store();
    }
    
    public static String getLicensesfolder() {
        return prop.getProperty("licensesfolder", "../licenses");
    }
    
    public static void setLicensesfolder(String str) {
        prop.setProperty("licensesfolder", str);
        Store();
    }
    
    
    public static String getDatafolder() {
        return prop.getProperty("datafolder", "../data");
    }
    
    public static void setDatafolder(String numBins) {
        prop.setProperty("datafolder", numBins);
        Store();
    }
    
     public static String getPolicyassetstore() {
        return prop.getProperty("policyassetstore", "../data/assets.ttl");
    }
    
    public static void setPolicyassetstore(String numBins) {
        prop.setProperty("policyassetstore", numBins);
        Store();
    }

    public static void setNamespace(String str) {
        prop.setProperty("namespace", str);
        Store();
    }
   
    public static String getNamespace() {
        return prop.getProperty("namespace", "http://salonica.dia.fi.upm.es/ldr/");
    }

    public static void setServer(String str) {
        prop.setProperty("server", str);
        Store();
    }
   
    public static String getServer() {
        return prop.getProperty("server", "http://salonica.dia.fi.upm.es");
    }
    
    public static void setPort(String str) {
        prop.setProperty("port", str);
        Store();
    }
   
    public static String getPort() {
        return prop.getProperty("port", "80");
    }
    
    
    

    public static String getDataset() {
        return prop.getProperty("dataset", "../data/data.nq");
    }
    public void setDataset(String str) {
        prop.setProperty("dataset", str);
        Store();
    }
       
    /**
     * Carga los parametros de configuracion del archivo contenidos.config
     * @return True si todo fue bien
     */
    public static boolean Load() {
        InputStream is;
        try {
            is = new FileInputStream("../ldr.config");
            prop.load(is);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace(); //todavía no está el logger
            return false;
        }
    }

    /**
     * Almacena los parámetros de configuración en el archivo
     * Este archivo no es configurable
     * El orden de almacenamiento en archivo es alfabético
     */
    public static void Store() {
        OutputStream os;
        try {
            Properties tmp = new Properties() {
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }
            };
            tmp.putAll(prop);
            tmp.store(new FileWriter("../ldr.config"), null);
        } catch (Exception ex) {
            Logger.getLogger("ldr").error("Error opening config file" + ex.toString());
        }
    }
}
