package server.workshop;

import java.io.IOException;
import java.nio.channels.Selector;

public class SingleThreadedServerWorkshop extends ServerWorkshop {

    public SingleThreadedServerWorkshop(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        // run event loop over selector, hint: SingleThreadedEventLoopWorkshop.runOver
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedServerWorkshop(81).start();
    }
}