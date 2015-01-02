package vroddon.web.utils;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

//APACHE COMMONS IO
import org.apache.commons.io.IOUtils;

/**
 * Clase para dascargar archivos.
 */
public class Downloader {

    private static class ProgressListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // e.getSource() gives you the object of DownloadCountingOutputStream
            // because you set it in the overriden method, afterWrite().
            System.out.print("\rDownloaded bytes : " + ((DownloadCountingOutputStream) e.getSource()).getByteCount());
        }
    }

    /**
     * Descarga un archivo de la web
     */
    public static boolean Descargar(String url, String archivo)  {
        URL dl = null;
        File fl = null;
        String x = null;
        OutputStream os = null;
        InputStream is = null;
        ProgressListener progressListener = new ProgressListener();
        try {
            fl = new File(archivo);
            dl = new URL(url);
            os = new FileOutputStream(fl);
            is = dl.openStream();

            DownloadCountingOutputStream dcount = new DownloadCountingOutputStream(os);
            dcount.setListener(progressListener);

            // this line give you the total length of source stream as a String.
            // you may want to convert to integer and store this value to
            // calculate percentage of the progression.
            String total=dl.openConnection().getHeaderField("Content-Length");
            System.out.println("Total: " + total + " bytes");
            // begin transfer by writing to dcount, not os.
            IOUtils.copy(is, dcount);
            return true;
        } catch (Exception e) {
            System.err.println(e);
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (is != null) {
             //   is.close();
            }
        }
    }
}