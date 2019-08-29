package server.workshop;

import java.io.IOException;
import java.nio.channels.Selector;

public class Step10_SingleThreadedServerWorkshop extends Step9_ServerWorkshop {

    public Step10_SingleThreadedServerWorkshop(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        // run event loop over selector, hint: SingleThreadedEventLoopWorkshop.runOver
    }

    public static void main(String[] args) throws IOException {
        new Step10_SingleThreadedServerWorkshop(81).start();
    }
}