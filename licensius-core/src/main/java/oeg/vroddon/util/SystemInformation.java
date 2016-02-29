/*
Copyright (c) 2015 OEG

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package oeg.vroddon.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;


/**
 * This class gathers some system information
 */
public class SystemInformation {

    private static final long MEGABYTE = 1024L * 1024L;

    public static void main(String args[]) {
        System.out.println("Nombre del PC: " + nombrePC());
        System.out.println("Nombre usuario: " + usuario());
        System.out.println("Procesador: " + procesador());
        System.out.println("Sistema operativo: " + SO());
        System.out.println("Version JDK: " + JDK());
        System.out.println("Directorio actual: " + dir());
    }


    /**
     * Obtiene información del sistema
     */
    public static String getSystemInfo() {
        StringBuilder s = new StringBuilder();
        s.append("Nombre de la maquina: ").append(nombrePC()).append("\n");
        s.append("Procesador: ").append(procesador()).append("\n");
        s.append("Available processors (cores): " + Runtime.getRuntime().availableProcessors()+"\n");
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memory = runtime.totalMemory();
        s.append("Memoria disponible para JVM: ").append(bytesToMegabytes(memory)).append("MB\n");
        s.append("Sistema operativo: ").append(SO()).append("\n");
        s.append("Version JDK: ").append(JDK()).append("\n");
        s.append("Directorio actual: ").append(dir()).append("\n");

        return s.toString();
    }

    public static String nombrePC() {
        String nombre = System.getenv("COMPUTERNAME");
        nombre = null;
        if (nombre == null) {
            try {
                nombre = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                nombre = "unknown";
            }
        }
        return nombre;
    }

    private static String usuario() {
        return System.getProperty("user.name");
    }

    private static String procesador() {
        return System.getenv("PROCESSOR_IDENTIFIER");
    }

    private static String SO() {
        return System.getProperty("os.name");
    }

    private static String JDK() {
        return System.getProperty("java.version");
    }

    private static String dir() {
        return System.getProperty("user.dir");
    }

    /**
     * Nos indica si tenemos o no conexión a internet
     */
    public static boolean isInternetReachable() {
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");
            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
     /**
     * Get date a class was compiled by looking at the corresponding class file in the jar.
     * @author Zig http://mindprod.com/jgloss/compiletimestamp.html
     */
    public static Date getCompileTimeStamp(Class<?> cls) {
        ClassLoader loader = cls.getClassLoader();
        String filename = cls.getName().replace('.', '/') + ".class";
        // get the corresponding class file as a Resource.
        URL resource = (loader != null)
                ? loader.getResource(filename)
                : ClassLoader.getSystemResource(filename);
        try {
            URLConnection connection = resource.openConnection();
            long time = connection.getLastModified();
            return (time != 0L) ? new Date(time) : null;
        } catch (Exception e) {
            return null;
        }
    }         

    private static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
}
