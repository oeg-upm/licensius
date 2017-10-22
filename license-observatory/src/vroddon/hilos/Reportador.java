package vroddon.hilos;

/**
 * Clase abstracta para que una interfaz de usuario (clase xxxView) la implemente
 * Representa algo que tiene la capacidad de refrescarse. 
 * Será invocado por procesos * Los algoritmos esperarán recibir un reportador para para informando de su progreso
 * @author Victor
 */
public interface Reportador {

    /**
     * Notifica el estado del algoritmo junto con su progreso
     * @param text Texto que será mostrado
     * @param progreso Número entre 0 y 100 indicando el progreso del algoritmo
     */
    public void status(String text, int progreso);
    
    /**
     * Actualiza la pantalla y pone un mensaje de texto
     * @param s Cadena a mostrar
     * @param refresh Si ha de refrescarse la interfaz además
     */
    public void updateStatus(String s, boolean refresh);
    
    
    /**
     * Muestra un mensaje durante 5 segundos y luego pone el mensaje de ready
     */
    public void flashStatus(String text);
    /**
     * Muestra un mensaje importante en un popup o de manera que requiera atención del usuario
     * @param text Cadena a mostrar
     * @param tipo Puede ser "info", "warn" o "error"
     */
    public void promptMessage(String text, String tipo);
    
}
