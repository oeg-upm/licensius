package vroddon.hilos;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import java.util.concurrent.TimeoutException;

/**
 * Clase trabajadora de Swing que implementa un trabajador. Ejemplo de uso:
 * 
 * EjecutadorOperacionLenta task = new EjecutadorOperacionLenta(new OperacionLenta() {
 *  public Object metodolento() { wait(5100L);return null;}
 *  public void done() {};});
 * task.execute(); 
 * 
 * O bien con un timeout
          EjecutadorOperacionLenta task = new EjecutadorOperacionLenta(5, new OperacionLenta() {
            public Object metodolento() {wait(5100L);return null;}
            public void done() {};});
          try {task.runWithTimeout();} catch (Exception e) {e.printStackTrace();}
 * 
 * 2012 July 
 * @author Victor Rodriguez Doncel 
 */
public class EjecutadorOperacionLenta extends SwingWorker {

    OperacionLenta tarea = null;
    private Throwable throwable;
    private Object result;
    private long startTime;
    long timeOut = -1;
    
    public EjecutadorOperacionLenta(OperacionLenta l) {
        tarea = l;
    }

    /**
     * @param Operación lenta a ejecutar
     * @param _timeOut En segundos
     */
    public EjecutadorOperacionLenta(int _timeOut, OperacionLenta l ) {
        tarea = l;
        timeOut = _timeOut*1000;
    }

    /**
     * Método que invoca a la operación lenta
     */
    @Override
    public Object doInBackground() {
        setProgress(0);
        return tarea.metodolento();
    }

    /**
     * Método que se invoca cuando ya esta todo hecho
     */
    @Override
    public void done() {
        setProgress(100);
        tarea.done();
    }

    /**
     * Ejecuta la tarea teniendo un timeout.
     */
    public Object runWithTimeout() throws TimeoutException, Exception {
        Thread operationThread = new Thread("noname") {
            @Override
            public void run() {
                try {
                    result = doOperation();
                } catch (Exception ex) {
                    throwable = ex;
                } catch (Throwable uncaught) {
                    throwable = uncaught;
                }
                synchronized (EjecutadorOperacionLenta.this) {
                    EjecutadorOperacionLenta.this.notifyAll();
                }
            }
            @Override
            public synchronized void start() {
                super.start();
            }
        };
        operationThread.start();
        startTime = System.currentTimeMillis();
        synchronized (this) {
            while (operationThread.isAlive() && (timeOut == -1 || System.currentTimeMillis() < startTime + timeOut)) {
                try {
                    wait(1000L);
                } catch (InterruptedException ex) {
                }
            }
        }
        if (throwable != null) {
            if (throwable instanceof Exception) {
                throw (Exception) throwable;
            } else if (throwable instanceof Error) {
                throw (Error) throwable;
            }
        }
        if (result != null) {
            return result;
        }
        if (System.currentTimeMillis() > startTime + timeOut) {
            throw new TimeoutException("Operation '" + "noname" + "' timed out after " + timeOut + " ms");
        } else {
            //todo fue bien!
//            throw new Exception("No result, no exception, and no timeout!");
            return true;
        }
    }

    public Object doOperation() 
    {
        Object o = tarea.metodolento();
        tarea.done();
        return o;
    };

    public static void main(String[] args) throws Throwable {
        EjecutadorOperacionLenta task = new EjecutadorOperacionLenta(4900, new OperacionLenta() {
            public Object metodolento() {
                try {wait(5100L);} catch (InterruptedException ex) {Logger.getLogger(EjecutadorOperacionLenta.class.getName()).log(Level.SEVERE, null, ex);}
            return null;}
            public void done() {};});
          try {task.runWithTimeout();} catch (Exception e) {e.printStackTrace();}
    }
}
