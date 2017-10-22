package observatory;

import vroddon.hilos.Reportador;

/**
 *
 * @author vroddon
 */
class ReportadorNull implements Reportador {

    public ReportadorNull() {
    }

    public void status(String text, int progreso) {
    }

    public void updateStatus(String s, boolean refresh) {
    }

    public void flashStatus(String text) {
    }

    public void promptMessage(String text, String tipo) {
    }
    
}
