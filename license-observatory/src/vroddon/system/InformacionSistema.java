package vroddon.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Clase que obtiene datos básicos del sistema donde se está ejecutando.
 */
public class InformacionSistema {

    public static void main(String args[]) {
        System.out.println(InformacionSistema.getSystemInfo());
    }

    public static boolean hasInternetConnection()
    {
    try{
        final URL url = new URL("http://www.google.com");
        final URLConnection conn = url.openConnection();
        return true;
        }catch(Exception e){
            return false;
        }
    }

    public static String getSystemInfo() {
        String s = "";

        s += "Nombre de la maquina: " + nombrePC() + "\n";
        s += "Procesador: " + procesador() + "\n";
        s += "Sistema operativo: " + SO() + "\n";
        s += "Version JDK: " + JDK() + "\n";
        s += "Directorio actual: " + dir() + "\n";
        s += "Memoria total JVM: " + Runtime.getRuntime().maxMemory() / (1024 * 1024 * 1024) + "Gb \n";
        s += "Internal IP: " + getInternalIP()+ "\n";
        s += "External IP: " + getIP()+ "\n";
        s += "Internet connection: " + hasInternetConnection() + "\n";
        s += "Windows Admin " + isAdmin() + "\n";
        return s;
    }

public static boolean isAdmin() {
    String groups[] = (new com.sun.security.auth.module.NTSystem()).getGroupIDs();
    for (String group : groups) {
        if (group.equals("S-1-5-32-544"))
            return true;
    }
    return false;
}

    public static String nombrePC() {
        return System.getenv("COMPUTERNAME");
    }

    public static String usuario() {
        return System.getProperty("user.name");
    }

    public static String procesador() {
        return System.getenv("PROCESSOR_IDENTIFIER");
    }

    public static String SO() {
        return System.getProperty("os.name");
    }

    public static String JDK() {
        return System.getProperty("java.version");
    }

    public static String dir() {
        return System.getProperty("user.dir");
    }

    public static String getInternalIP() {
        String res = "";
        try {
            InetAddress thisIp = InetAddress.getLocalHost();
            res = thisIp.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String getIP()
    {
        String ip = "";
        try{URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        ip = in.readLine(); //you get the IP as a String
        in.close();}catch(Exception e){}
        return ip;
    }

}
