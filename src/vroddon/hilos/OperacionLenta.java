package vroddon.hilos;

/**
 * Clase para ser sobrecargada e implementar tareas costosas.
 * 
 * 2012 July 
 * @author Victor Rodriguez Doncel
 */
public interface OperacionLenta {

    /**
     * Method implementing the lenghty operation
     */
    public Object metodolento();

    /**
     * Method invoked upon completion of the task
     */
    public void done();
}
