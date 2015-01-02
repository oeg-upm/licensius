/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vroddon.sw;

import java.util.concurrent.TimeoutException;

public abstract class TimeoutOperation {

long timeOut = -1;
String name = "Timeout Operation"; 

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public long getTimeOut() {
    return timeOut;
}

public void setTimeOut(long timeOut) {
    this.timeOut = timeOut;
}

public TimeoutOperation (String name, long timeout) {
    this.timeOut = timeout;
}

private Throwable throwable;
private Object result;
private long startTime;

public Object run () throws TimeoutException, Exception {
    Thread operationThread = new Thread (getName()) {
        public void run () {
            try {
                result = doOperation();
            } catch (Exception ex) {
                throwable = ex;
            } catch (Throwable uncaught) {
                throwable = uncaught;
            }
            synchronized (TimeoutOperation.this) {
                TimeoutOperation.this.notifyAll();
            }   
        }
        public synchronized void start() {
            super.start();
        }
    };
    operationThread.start();
    startTime = System.currentTimeMillis();
    synchronized (this) {
        while (operationThread.isAlive() && (getTimeOut() == -1 || System.currentTimeMillis() < startTime + getTimeOut())) {
            try {
                wait (1000L);
            } catch (InterruptedException ex) {}
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
    if (System.currentTimeMillis() > startTime + getTimeOut()) {
        throw new TimeoutException("Operation '"+getName()+"' timed out after "+getTimeOut()+" ms");
    } else {
        throw new Exception ("No result, no exception, and no timeout!");
    }
}

public abstract Object doOperation () throws Exception;

public static void main (String [] args) throws Throwable {
    Object o = new TimeoutOperation("Test timeout", 4900) {
        public Object doOperation() throws Exception {
            try {
                Thread.sleep (5000L);
            } catch (InterruptedException ex) {}
            return "OK";
        }
    }.run();
    System.out.println(o);
}   

}