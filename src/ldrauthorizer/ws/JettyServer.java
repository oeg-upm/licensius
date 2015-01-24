package ldrauthorizer.ws;

import java.io.IOException;
import ldconditional.LDRConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Wrapper class of a jetty server
 */
public class JettyServer {

    public Server server;

    public JettyServer() {
        this(8585);
    }

    public JettyServer(Integer runningPort) {
        server = new Server(runningPort);
    }

    public void setHandler(AbstractHandler handler) {
        server.setHandler(handler);
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
        server.join();
    }

    public boolean isStarted() {
        return server.isStarted();
    }

    public boolean isStopped() {
        return server.isStopped();
    }

    public static void main(String[] args) throws IOException {
        try {
            int PORT = Integer.parseInt(LDRConfig.getPort());
            JettyServer server = new JettyServer(PORT);
            server.setHandler(new CLDHandlerDefault());
            server.start();
            System.out.println("server started");
            System.in.read();
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
}
